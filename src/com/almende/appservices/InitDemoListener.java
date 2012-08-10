package com.almende.appservices;

import java.util.Date;

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

	public static void initDemoModel(){
		//generate example memo db
		MemoNode rootNode = MemoNode.getRootNode();
		if (rootNode.getChildByStringValue("Memo-appservices demo") == null){
			System.out.println("Generating test/demo data");
			MemoNode baseNode = rootNode.addChild(new MemoNode("Memo-appservices demo"));
			MemoNode tasks = baseNode.addChild(new MemoNode("tasks"));
			
			MemoNode task = tasks.addChild(new MemoNode("task"));
			task.setPropertyValue("description", "Go to designated location");
			task.setPropertyValue("locationLat", "47.075765");
			task.setPropertyValue("locationLon", "9.392449");
			task.setPropertyValue("duration", "360");
			task.setPropertyValue("eta", "1345586369");
			MemoNode resources = task.addChild(new MemoNode("resources"));
			MemoNode humans = resources.addChild(new MemoNode("human"));
			MemoNode cars = resources.addChild(new MemoNode("car"));
			
			@SuppressWarnings("unused")
			MemoNode accepted = humans.addChild(new MemoNode("accepted"));
			MemoNode offered = humans.addChild(new MemoNode("offered"));
			@SuppressWarnings("unused")
			MemoNode rejected = humans.addChild(new MemoNode("rejected"));
			
			baseNode.addChild(new MemoNode("agent"))
					.setPropertyValue("name", "PoliceOfficer#324353")
					.setPropertyValue("lat", "47.091895")
					.setPropertyValue("lon", "9.344835")
					.setPropertyValue("resType", "PoliceOfficer")
					.setPropertyValue("state", "Busy")
					.setPropertyValue("taskDescription", "Go to designated location")
					.setPropertyValue("seen", new Long(new Date().getTime()).toString()
			).addParent(offered);
			
			baseNode.addChild(new MemoNode("agent"))
					.setPropertyValue("name", "Ambulance#3502")
					.setPropertyValue("lat", "47.089207")
					.setPropertyValue("lon", "9.34595")
					.setPropertyValue("resType", "Ambulance")
					.setPropertyValue("state", "Free")
					.setPropertyValue("taskDescription", "")
					.setPropertyValue("seen", new Long(new Date().getTime()).toString()
			).addParent(cars);
			
			baseNode.addChild(new MemoNode("agent"))
					.setPropertyValue("name", "Firefighter#324333")
					.setPropertyValue("lat", "47.097351")
					.setPropertyValue("lon", "9.356282")
					.setPropertyValue("resType", "FireFighter")
					.setPropertyValue("state", "Busy")
					.setPropertyValue("taskDescription", "Go to designated location")
					.setPropertyValue("seen", new Long(new Date().getTime()).toString()
			).addParent(offered);
			MemoNode.flushDB();
		}
	}
}
