package com.almende.appservices.simulation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.almende.appservices.model.Task;
import com.chap.memo.memoNodes.MemoNode;

@Path("magic")
public class MagicAgent {

	//Periodic update agent's plan according to:
	static float oldLat=0,oldLon=0;
	
	private String location(MemoNode node){
		return node.getPropertyValue("lat")+" "+node.getPropertyValue("lon");
	}
	private boolean close(MemoNode one, MemoNode theOther){
		Float lat1=Float.parseFloat(one.getPropertyValue("lat")); 
		Float lon1=Float.parseFloat(one.getPropertyValue("lon")); 
		Float lat2=Float.parseFloat(theOther.getPropertyValue("lat")); 
		Float lon2=Float.parseFloat(theOther.getPropertyValue("lon")); 
		if (Math.abs(lat2-lat1) < 0.0003 && Math.abs(lon2-lon1) < 0.0003) return true;
		return false;
	}
	private boolean moving(MemoNode node){
		Float lat=Float.parseFloat(node.getPropertyValue("lat")); 
		Float lon=Float.parseFloat(node.getPropertyValue("lon")); 
		if (oldLat == 0 || oldLon==0){
			oldLat=lat;
			oldLon=lon;
		}
		if (Math.abs(oldLat-lat)+Math.abs(oldLon-lon) > 0.0005) return true;
		return false;
	}
	@GET
	public Response perform(){
		MemoNode baseNode = MemoNode.getRootNode().getChildByStringValue("Memo-appservices demo");
		if (baseNode.getChildByStringValue("scenarioTask") == null) return Response.ok("No task given yet").build();
		List<MemoNode> scenarioTasks = baseNode.getChildByStringValue("scenarioTask").getChildren();
		
		if (scenarioTasks.size() <=0) return Response.ok("No task given yet").build();
		Task task = new Task(scenarioTasks.get(0));
		if (task.getState().equals("pending")) return Response.ok("Waiting for the firefighters to accept the task").build();
		
		MemoNode scenarioBase = baseNode.getChildByStringValue("scenarioResources");
		
		MemoNode A = null,B = null,E = null;
		for (MemoNode agent: scenarioBase.getChildren()){
			if (agent.getPropertyValue("login").equals("2255")) A = agent;
			if (agent.getPropertyValue("login").equals("3366")) B = agent;
			if (agent.getPropertyValue("login").equals("6699")) E = agent;
		}
	    if (close(A,E)){
	    	A.setPropertyValue("plan", "Drive towards incident location when E has entered the truck");
	    	A.setPropertyValue("toLocation", location(task.getNode()));
	    	E.setPropertyValue("plan", "Please get into the FireTruck");
	    	E.setPropertyValue("toLocation", location(A));
	    	if (moving(A) && close(A,E)){
		    	A.setPropertyValue("plan", "Drive towards incident location.");
		    	A.setPropertyValue("toLocation", location(task.getNode()));
		    	E.setPropertyValue("plan", "Stay in truck while traveling");
		    	A.setPropertyValue("toLocation", location(task.getNode()));
		    	B.setPropertyValue("toLocation", location(task.getNode()));
		    	return Response.ok("Drive together to incident").build();
	    	}
	    	return Response.ok("Waiting for E to get into truck").build();
	    }
		if (!close(A,B)){
	    	A.setPropertyValue("plan", "Please get into the FireTruck");
	    	A.setPropertyValue("toLocation",location(A));
	    	B.setPropertyValue("plan", "Proceed to the FireTruck at the base.");
	    	B.setPropertyValue("toLocation",location(A));
	    	E.setPropertyValue("plan", "Wait for the FireTruck at your present location.");
	    	E.setPropertyValue("toLocation",location(E));
	    	return Response.ok("Waiting for B to reach A").build();
		}
		if (close(A,B)){
	    	A.setPropertyValue("plan", "Drive towards E when B has entered the truck.");
	    	A.setPropertyValue("toLocation",location(E));
	    	B.setPropertyValue("plan", "Please get into the FireTruck");
	    	B.setPropertyValue("toLocation",location(A));
	    	if (moving(A) && close(A,B)){
		    	A.setPropertyValue("plan", "Drive towards E.");
		    	A.setPropertyValue("toLocation",location(E));
		    	B.setPropertyValue("plan", "Stay in truck while traveling");
		    	B.setPropertyValue("toLocation",location(E));
		    	return Response.ok("Waiting for A & B  to reach E").build();
	    	}
	    	return Response.ok("Waiting for B to get into truck").build();
		}
		return Response.ok("Nothing?").build();
	}
}
