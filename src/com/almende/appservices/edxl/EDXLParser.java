package com.almende.appservices.edxl;

import java.io.StringReader;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Path("/edxl")
public class EDXLParser {

	private String getStringByPath(Element from, String[] path){
		Element elem= getElementByPath(from,path);
		if (elem != null) return elem.getTextContent();
		return "";
	}
	
	private Element getElementByPath(Element from, String[] path){
		try {
			Element elem = from;
			for (String tag : path){
				elem = (Element) elem.getElementsByTagName(tag).item(0); 
			}
			return elem;
		} catch (Exception e){}
		return null;
	}
	
	private Response parseRequestResource(Element rootElement){
		System.out.println("Received RequestResource doc!");
		//Currently only supporting ResourceInformation -> Resource,AssignmentInformation,ScheduleInformation("RequestArrival","targetArea") tags.
		System.out.println(getStringByPath(rootElement,new String[]{"MessageDescription"}));
		System.out.println(getStringByPath(rootElement,new String[]{"OriginatingMessageID"}));
		NodeList req_resources = rootElement.getElementsByTagName("ResourceInformation");
		for (int i=0; i<req_resources.getLength();i++){
			Element res = (Element)req_resources.item(i);
			String resourceType = getStringByPath(res,new String[]{"Resource","TypeStructure","rm:Value"});
			String resourceID = getStringByPath(res,new String[]{"Resource","ResourceID"});
			String amountString = getStringByPath(res,new String[]{"AssignmentInformation","Quantity","rm:MeasuredQuantity","rm:Amount"});
			String location = "";
			String dateTime = "";
			NodeList schedules = res.getElementsByTagName("ScheduleInformation");
			for (int j=0; j<schedules.getLength(); j++){
				Element schedule = (Element)schedules.item(j);
				if ("RequestedArrival".equals(getStringByPath(schedule,new String[]{"ScheduleType"}))){
					location = getStringByPath(schedule,new String[]{
							"Location","rm:TargetArea","gml:Point","gml:pos"
					});
					dateTime = getStringByPath(schedule,new String[]{"DateTime"});
				};
			}
			System.out.println("--->"+resourceID+"|"+resourceType+":"+amountString + " at "+location + "@"+dateTime);
		}
		return Response.ok().build();
	}
	private Response parseReleaseResource(Element rootElement){
		System.out.println("Received ReleaseResource doc!");
		//Currently only supporting ResourceInformation -> Resource,AssignmentInformation,ScheduleInformation("RequestArrival","targetArea") tags.
		System.out.println(getStringByPath(rootElement,new String[]{"MessageDescription"}));
		System.out.println(getStringByPath(rootElement,new String[]{"OriginatingMessageID"}));
		NodeList req_resources = rootElement.getElementsByTagName("ResourceInformation");
		for (int i=0; i<req_resources.getLength();i++){
			Element res = (Element)req_resources.item(i);
			String resourceType = getStringByPath(res,new String[]{"Resource","TypeStructure","rm:Value"});
			String resourceID = getStringByPath(res,new String[]{"Resource","ResourceID"});
			String amountString = getStringByPath(res,new String[]{"AssignmentInformation","Quantity","rm:MeasuredQuantity","rm:Amount"});
			String location = "";
			String dateTime = "";
			NodeList schedules = res.getElementsByTagName("ScheduleInformation");
			for (int j=0; j<schedules.getLength(); j++){
				Element schedule = (Element)schedules.item(j);
				if ("RequestedArrival".equals(getStringByPath(schedule,new String[]{"ScheduleType"}))){
					location = getStringByPath(schedule,new String[]{
							"Location","rm:TargetArea","gml:Point","gml:pos"
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
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
		    Document document = builder.parse(new InputSource(new StringReader(xml)));
		    Element rootElement = document.getDocumentElement();
		    String msgType = rootElement.getNodeName();
		    if ("RequestResource".equals(msgType)) return parseRequestResource(rootElement);
		    if ("ReleaseResource".equals(msgType)) return parseReleaseResource(rootElement);
		    return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return Response.serverError().build();
	}

}