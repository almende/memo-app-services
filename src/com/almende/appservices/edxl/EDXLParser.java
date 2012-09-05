package com.almende.appservices.edxl;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.almende.appservices.InitDemoListener;
import com.chap.memo.memoNodes.MemoNode;

@Path("/edxl")
public class EDXLParser {
	private static SAXBuilder builder = new SAXBuilder();
	
	private String getStringByPath(Element from, String[] path){
		Element elem= getElementByPath(from,path);
		if (elem != null) return elem.getText();
		return "";
	}
	
	@SuppressWarnings("rawtypes")
	private Element getElementByPath(Element from, String[] path){
		try {
			Element elem = from;
			for (String tag : path){
				List children = elem.getChildren();
				for (int i=0; i<children.size(); i++){
					if (((Element)children.get(i)).getName().equalsIgnoreCase(tag)){
						elem = (Element)children.get(i);
						break;
					}
				}
				if (elem == null){
					System.out.println("need to return null!");
					return null;
				}
			}
			return elem;
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	private Response parseRequestResource(Element rootElement){
		System.out.println("Received RequestResource doc!");
		//Currently only supporting ResourceInformation -> Resource,AssignmentInformation,ScheduleInformation("RequestArrival","targetArea") tags.
		System.out.println(getStringByPath(rootElement,new String[]{"OriginatingMessageID"}));
		List list = rootElement.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Element res = (Element) list.get(i);
			if (!res.getName().equalsIgnoreCase("ResourceInformation")) continue;
			String resourceType = getStringByPath(res,new String[]{"Resource","TypeStructure","Value"});
			String resourceID = getStringByPath(res,new String[]{"Resource","ResourceID"});
			String amountString = getStringByPath(res,new String[]{"AssignmentInformation","Quantity","MeasuredQuantity","Amount"});
			String taskDescription = getStringByPath(res,new String[]{"AssignmentInformation","AnticipatedFunction"});
			String location = "";
			String dateTime = "";
			List schedules = res.getChildren();
			for (int j=0; j<schedules.size(); j++){
				Element schedule = (Element)schedules.get(j);
				if (!schedule.getName().equalsIgnoreCase("ScheduleInformation")) continue;
				
				if ("RequestedArrival".equalsIgnoreCase(getStringByPath(schedule,new String[]{"ScheduleType"}))){
					location = getStringByPath(schedule,new String[]{
							"Location","TargetArea","Point","pos"
					});
					dateTime = getStringByPath(schedule,new String[]{"DateTime"});
				};
			}
			System.out.println(taskDescription+"--->"+resourceID+"|"+resourceType+":"+amountString + " at "+location + "@"+dateTime);
			if (resourceType.equalsIgnoreCase("FireFighter") && !amountString.equals("") && !location.equals("")){
				MemoNode baseNode = MemoNode.getRootNode().getChildByStringValue("Memo-appservices demo");
				MemoNode tasks = baseNode.getChildByStringValue("tasks");
				
				Map<String,String> properties = new HashMap<String,String>();
				properties.put("description", taskDescription);
				//Parse:  146.03 -17.53 
				String[] geoLoc = location.trim().split(" ");
				properties.put("lat", geoLoc[0].trim());
				properties.put("lon", geoLoc[1].trim());
				properties.put("duration", "50");
				properties.put("eta", "1345588000");
				properties.put("amount", amountString);
				
				MemoNode task = InitDemoListener.addTask(tasks, properties);
				
				MemoNode scenarioTasks = baseNode.getChildByStringValue("scenarioTask");
				if (scenarioTasks == null){
					scenarioTasks = baseNode.addChild(new MemoNode("scenarioTask"));
				}
				for (MemoNode oldtask : scenarioTasks.getChildren()){
					List<MemoNode> parents = oldtask.getParents();
					for (MemoNode parent: parents){
						oldtask.delParent(parent);
					}
				}
				scenarioTasks.addChild(task);
				
				//Add 5 firefighters to task!
				MemoNode scenarioBase = baseNode.getChildByStringValue("scenarioResources");
				
				
				for (MemoNode agent: scenarioBase.getChildren()){
					MemoNode tasksNode = agent.getChildByStringValue("tasks");
					if (tasksNode == null) tasksNode = agent.addChild(new MemoNode("tasks"));
					tasksNode.setChild(task);
					if (agent.getPropertyValue("resType").endsWith("Vehicle")){
						agent.setParent(task.getChildByStringValue("resources").getChildByStringValue("car"));
					} else {
						agent.setParent(task.getChildByStringValue("resources").getChildByStringValue("human").getChildByStringValue("offered"));
					}
					agent.setPropertyValue("taskDescription",taskDescription);
				}
			}
		}
		return Response.ok().build();
	}
	@SuppressWarnings("rawtypes")
	private Response parseReleaseResource(Element rootElement){
		System.out.println("Received ReleaseResource doc!");
		//Currently only supporting ResourceInformation -> Resource,AssignmentInformation,ScheduleInformation("RequestArrival","targetArea") tags.
		System.out.println(getStringByPath(rootElement,new String[]{"MessageDescription"}));
		System.out.println(getStringByPath(rootElement,new String[]{"OriginatingMessageID"}));
		List req_resources = rootElement.getChildren();
		for (int i=0; i<req_resources.size();i++){
			Element res = (Element)req_resources.get(i);
			if (!res.getName().equalsIgnoreCase("ResourceInformation")) continue;
			String resourceType = getStringByPath(res,new String[]{"Resource","TypeStructure","Value"});
			String resourceID = getStringByPath(res,new String[]{"Resource","ResourceID"});
			String amountString = getStringByPath(res,new String[]{"AssignmentInformation","Quantity","MeasuredQuantity","Amount"});
			String location = "";
			String dateTime = "";
			List schedules = res.getChildren();
			for (int j=0; j<schedules.size(); j++){
				Element schedule = (Element)schedules.get(j);
				if (!schedule.getName().equalsIgnoreCase("ScheduleInformation")) continue;
				if ("RequestedArrival".equalsIgnoreCase(getStringByPath(schedule,new String[]{"ScheduleType"}))){
					location = getStringByPath(schedule,new String[]{
							"Location","TargetArea","Point","pos"
					});
					dateTime = getStringByPath(schedule,new String[]{"DateTime"});
				};
			}
			System.out.println("--->"+resourceID+"|"+resourceType+":"+amountString + " at "+location + "@"+dateTime);
		}
		return Response.ok().build();
	}
	@POST
	public Response parseXML(String xml) {
		try {
		    Document document = builder.build(new InputSource(new StringReader(xml)));
		    Element rootElement = document.getRootElement();
		    String msgType = rootElement.getName();
		    if ("EDXLDistribution".equalsIgnoreCase(msgType)){
		    	rootElement = (Element) getElementByPath(rootElement,new String[]{
		    		"contentObject",
		    		"xmlContent",
		    		"embeddedXMLContent"
		    	}).getChildren().get(0);
		    	msgType = rootElement.getName();
		    }
		    if ("RequestResource".equalsIgnoreCase(msgType)) return parseRequestResource(rootElement);
		    if ("ReleaseResource".equalsIgnoreCase(msgType)) return parseReleaseResource(rootElement);
		    return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return Response.serverError().build();
	}

}
