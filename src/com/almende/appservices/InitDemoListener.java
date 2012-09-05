package com.almende.appservices;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.chap.memo.memoNodes.MemoNode;

public class InitDemoListener implements ServletContextListener  {

	public InitDemoListener(){
	}

	@Override
	public void contextDestroyed(ServletContextEvent context) {
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		InitDemoListener.initDemoModel();
	}

	public static MemoNode addNode(String value, Map<String,String> properties){
		MemoNode res = new MemoNode(value);
		for (Entry<String,String> ent: properties.entrySet()){
			res.setPropertyValue(ent.getKey(),ent.getValue());
		}
		return res;
	}
	public static MemoNode addTask(MemoNode tasks, Map<String,String> properties){
		MemoNode task = tasks.addChild(addNode("task",properties));
		MemoNode resources = task.addChild(new MemoNode("resources"));
		MemoNode humans = resources.addChild(new MemoNode("human"));
		
		resources.addChild(new MemoNode("car"));
		humans.setChild(new MemoNode("accepted"))
			  .setChild(new MemoNode("offered"))
			  .setChild(new MemoNode("rejected"));
		return task;
	}
	
	public static void initDemoModel(){
		//generate example memo db
		MemoNode rootNode = MemoNode.getRootNode();
		if (rootNode.getChildByStringValue("Memo-appservices demo") == null){
			System.out.println("Generating test/demo data");
			MemoNode baseNode = rootNode.addChild(new MemoNode("Memo-appservices demo"));
			MemoNode tasks = baseNode.addChild(new MemoNode("tasks"));
			MemoNode agents = baseNode.addChild(new MemoNode("agents"));
			MemoNode scenarioBase = baseNode.addChild(new MemoNode("scenarioResources"));
			
			Map<String,String> properties = new HashMap<String,String>();
			properties.put("description", "Go to designated location");
			properties.put("lat", "47.075765");
			properties.put("lon", "9.392449");
			properties.put("duration", "360");
			properties.put("eta", "1345586369");
			MemoNode task = addTask(tasks,properties);
			
			properties.clear();
			properties.put("name", "PoliceOfficer#324353");
			properties.put("login", "1");
			properties.put("password", "1");
			properties.put("lat", "47.091895");
			properties.put("lon", "9.344835");
			properties.put("resType", "PoliceOfficer");
			properties.put("state", "Busy");
			properties.put("taskDescription", task.getPropertyValue("description"));
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setChild(new MemoNode("tasks").setChild(task))
					.setParent(task.getChildByStringValue("resources").getChildByStringValue("human").getChildByStringValue("offered"))
			;
			
			properties.clear();
			properties.put("name", "PoliceOfficer#145123");
			properties.put("login", "2");
			properties.put("password", "2");
			properties.put("lat", "47.091895");
			properties.put("lon", "9.344835");
			properties.put("resType", "PoliceOfficer");
			properties.put("state", "Busy");
			properties.put("taskDescription", task.getPropertyValue("description"));
			properties.put("seen", new Long(new Date().getTime()).toString());
			MemoNode doubleAgent = addNode("agent",properties)
					.setParent(agents)
					.setChild(new MemoNode("tasks").setChild(task))
					.setParent(task.getChildByStringValue("resources").getChildByStringValue("human").getChildByStringValue("offered"))
			;
					
			
			properties.clear();
			properties.put("name", "PoliceCar#3502");
			properties.put("lat", "47.089207");
			properties.put("lon", "9.34595");
			properties.put("resType", "PoliceVehicle");
			properties.put("state", "Free");
			properties.put("taskDescription", "");
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setChild(new MemoNode("tasks").setChild(task))
					.setParent(task.getChildByStringValue("resources").getChildByStringValue("car"))
			;

			properties.clear();
			properties.put("name", "Firefighter#324333");
			properties.put("login", "3");
			properties.put("password", "3");
			properties.put("lat", "47.097351");
			properties.put("lon", "9.356282");
			properties.put("resType", "FireFighter");
			properties.put("state", "Busy");
			properties.put("taskDescription", task.getPropertyValue("description"));
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setChild(new MemoNode("tasks").setChild(task))
					.setParent(task.getChildByStringValue("resources").getChildByStringValue("human").getChildByStringValue("offered"))
			;
						
			properties.clear();
			properties.put("description", "Re-crew base");
			properties.put("lat", "47.091983");
			properties.put("lon", "9.342385");
			properties.put("duration", "50");
			properties.put("eta", "1345588000");
			task = addTask(tasks,properties);
			
			properties.clear();
			properties.put("name", "PoliceOfficer#43432");
			properties.put("login", "4");
			properties.put("password", "4");
			properties.put("lat", "47.093895");
			properties.put("lon", "9.354835");
			properties.put("resType", "PoliceOfficer");
			properties.put("state", "Busy");
			properties.put("taskDescription", task.getPropertyValue("description"));
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setChild(new MemoNode("tasks").setChild(task))
					.setParent(task.getChildByStringValue("resources").getChildByStringValue("human").getChildByStringValue("offered"))
			;

			properties.clear();
			properties.put("name", "PoliceCar#56722");
			properties.put("lat", "47.093895");
			properties.put("lon", "9.354835");
			properties.put("resType", "PoliceVehicle");
			properties.put("state", "Free");
			properties.put("taskDescription", "");
			properties.put("seen", new Long(new Date().getTime()).toString());			
			addNode("agent",properties)
					.setParent(agents)
					.setChild(new MemoNode("tasks").setChild(task))
					.setParent(task.getChildByStringValue("resources").getChildByStringValue("car"))
			;
			
			doubleAgent
				.setChild(doubleAgent.getChildByStringValue("tasks").setChild(task))
				.setParent(task.getChildByStringValue("resources").getChildByStringValue("human").getChildByStringValue("offered"));
			
			properties.clear();
			properties.put("description", "Respond to incident");
			properties.put("lat", "47.075765");
			properties.put("lon", "9.392449");
			properties.put("duration", "150");
			properties.put("eta", "1345586000");
			task = addTask(tasks,properties);

			properties.clear();
			properties.put("name", "Medic#412345");
			properties.put("login", "5");
			properties.put("password", "5");
			properties.put("lat", "47.089207");
			properties.put("lon", "9.34595");
			properties.put("resType", "Paramedic");
			properties.put("state", "Busy");
			properties.put("taskDescription", task.getPropertyValue("description"));
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setChild(new MemoNode("tasks").setChild(task))
					.setParent(task.getChildByStringValue("resources").getChildByStringValue("human").getChildByStringValue("offered"))
			;
			
			properties.clear();
			properties.put("name", "Ambulance#3502");
			properties.put("lat", "47.089207");
			properties.put("lon", "9.34595");
			properties.put("resType", "AmbulanceVehicle");
			properties.put("state", "Free");
			properties.put("taskDescription", "");
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setChild(new MemoNode("tasks").setChild(task))
					.setParent(task.getChildByStringValue("resources").getChildByStringValue("car"))
			;

			properties.clear();
			properties.put("name", "Firefighter#A");
			properties.put("login", "2255");
			properties.put("password", "5522");
			properties.put("lat", "47.091019");
			properties.put("lon", "9.347096");
			properties.put("resType", "FireFighter");
			properties.put("state", "Free");
			properties.put("taskDescription", "");
			properties.put("plan", "");
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setParent(scenarioBase)
			;
			properties.clear();
			properties.put("name", "Firefighter#B");
			properties.put("login", "3366");
			properties.put("password", "6633");
			properties.put("lat", "47.09139");
			properties.put("lon", "9.344395");
			properties.put("resType", "FireFighter");
			properties.put("state", "Free");
			properties.put("taskDescription", "");
			properties.put("plan", "");
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setParent(scenarioBase)
			;
			properties.clear();
			properties.put("name", "Firefighter#C");
			properties.put("login", "4477");
			properties.put("password", "7744");
			properties.put("lat", "47.090142");
			properties.put("lon", "9.343003");
			properties.put("resType", "FireFighter");
			properties.put("state", "Free");
			properties.put("taskDescription", "");
			properties.put("plan", "");
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setParent(scenarioBase)
			;
			properties.clear();
			properties.put("name", "Firefighter#D");
			properties.put("login", "5588");
			properties.put("password", "8855");
			properties.put("lat", "47.089602");
			properties.put("lon", "9.348415");
			properties.put("resType", "FireFighter");
			properties.put("state", "Free");
			properties.put("taskDescription", "");
			properties.put("plan", "");
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setParent(scenarioBase)
			;
			properties.clear();
			properties.put("name", "Firefighter#E");
			properties.put("login", "6699");
			properties.put("password", "9966");
			properties.put("lat", "47.097202");
			properties.put("lon", "9.355306");
			properties.put("resType", "FireFighter");
			properties.put("state", "Free");
			properties.put("taskDescription", "");
			properties.put("plan", "");
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setParent(scenarioBase)
			;
			properties.clear();
			properties.put("name", "FireTruck#F");
			properties.put("lat", "47.091019");
			properties.put("lon", "9.347096");
			properties.put("resType", "FireVehicle");
			properties.put("state", "Free");
			properties.put("taskDescription", "");
			properties.put("seen", new Long(new Date().getTime()).toString());
			addNode("agent",properties)
					.setParent(agents)
					.setParent(scenarioBase)
			;
			
			MemoNode.flushDB();
		}
	}
}
