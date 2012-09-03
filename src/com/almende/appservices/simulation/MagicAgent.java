package com.almende.appservices.simulation;

import java.util.List;

import com.almende.appservices.model.Task;
import com.chap.memo.memoNodes.MemoNode;

public class MagicAgent {

	//Periodic update agent's plan according to:
	static MemoNode baseNode = MemoNode.getRootNode().getChildByStringValue("Memo-appservices demo");
	static float oldLat=0,oldLon=0;
	
	private boolean close(MemoNode one, MemoNode theOther){
		Float lat1=Float.parseFloat(one.getPropertyValue("lat")); 
		Float lon1=Float.parseFloat(one.getPropertyValue("lon")); 
		Float lat2=Float.parseFloat(theOther.getPropertyValue("lat")); 
		Float lon2=Float.parseFloat(theOther.getPropertyValue("lon")); 
		if (Math.abs(lat2-lat1) < 0.001 && Math.abs(lon2-lon1) < 0.001) return true;
		return false;
	}
	private boolean moving(MemoNode node){
		Float lat=Float.parseFloat(node.getPropertyValue("lat")); 
		Float lon=Float.parseFloat(node.getPropertyValue("lon")); 
		if (oldLat == 0 || oldLon==0){
			oldLat=lat;
			oldLon=lon;
		}
		if (Math.abs(oldLat-lat)+Math.abs(oldLon-lon) > 0.001) return true;
		return false;
	}
	public void perform(){
		List<MemoNode> scenarioTasks = baseNode.getChildByStringValue("scenarioTask").getChildren();
		
		if (scenarioTasks.size() <=0) return;
		Task task = new Task(scenarioTasks.get(0));
		if (task.getState().equals("pending")) return;
		
		MemoNode scenarioBase = baseNode.getChildByStringValue("scenarioResources");
		
		MemoNode A = null,B = null,E = null;
		for (MemoNode agent: scenarioBase.getChildren()){
			if (agent.getPropertyValue("login").equals("2255")) A = agent;
			if (agent.getPropertyValue("login").equals("3366")) B = agent;
			if (agent.getPropertyValue("login").equals("6699")) E = agent;
		}
	    if (close(A,E)){
	    	A.setPropertyValue("plan", "Drive towards incident location when E has entered the truck");
	    	E.setPropertyValue("plan", "Please get into the FireTruck");
	    	if (moving(A) && close(A,E)){
		    	A.setPropertyValue("plan", "Drive towards incident location.");
		    	E.setPropertyValue("plan", "Stay in truck while traveling");	    		
	    	}
	    }
		if (!close(A,B)){
	    	A.setPropertyValue("plan", "Please get into the FireTruck");
	    	B.setPropertyValue("plan", "Proceed to the FireTruck at the base.");
	    	E.setPropertyValue("plan", "Wait for the FireTruck at your present location.");
		}
		if (close(A,B)){
	    	A.setPropertyValue("plan", "Drive towards E when B has entered the truck.");
	    	B.setPropertyValue("plan", "Please get into the FireTruck");
	    	if (moving(A) && close(A,B)){
		    	A.setPropertyValue("plan", "Drive towards E.");
		    	E.setPropertyValue("plan", "Stay in truck while traveling");	    			    		
	    	}
		}
	}
}
