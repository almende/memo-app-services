package com.almende.appservices.simulation;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.almende.appservices.model.Task;
import com.chap.memo.memoNodes.MemoNode;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

@Path("magic")
public class MagicAgent {

	static Queue queue = QueueFactory.getDefaultQueue();
	
	
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
		if (Math.abs(oldLat-lat)+Math.abs(oldLon-lon) > 0.0005) return true;
		return false;
	}
	@POST
	public Response performPost(@QueryParam("cron") String cron){
		return perform(cron);
	}
	@GET
	public Response perform(@QueryParam("cron") String cron){
		if (cron != null && cron.equals("true")){
			//schedule two calls more:
			queue.add(withUrl("/magic").countdownMillis(10000).param("cron", "false"));
			queue.add(withUrl("/magic").countdownMillis(20000).param("cron", "false"));
			queue.add(withUrl("/magic").countdownMillis(30000).param("cron", "false"));
			queue.add(withUrl("/magic").countdownMillis(40000).param("cron", "false"));
			queue.add(withUrl("/magic").countdownMillis(50000).param("cron", "false"));
		}
		
		MemoNode baseNode = MemoNode.getRootNode().getChildByStringValue("Memo-appservices demo");
		if (baseNode.getChildByStringValue("scenarioTask") == null){
			oldLat=0;
			oldLon=0;
			return Response.ok("No task given yet").build();
		}
		List<MemoNode> scenarioTasks = baseNode.getChildByStringValue("scenarioTask").getChildren();
		
		if (scenarioTasks.size() <=0){
			oldLat=0;
			oldLon=0;
			return Response.ok("No task given yet").build();
		}
		Task task = new Task(scenarioTasks.get(0));
		if (task.getState().equals("pending")) return Response.ok("Waiting for the firefighters to accept the task").build();
		
		MemoNode scenarioBase = baseNode.getChildByStringValue("scenarioResources");
		
		MemoNode A = null,B = null,E = null;
		for (MemoNode agent: scenarioBase.getChildren()){
			if (agent.getPropertyValue("login").equals("2255")) A = agent;
			if (agent.getPropertyValue("login").equals("3366")) B = agent;
			if (agent.getPropertyValue("login").equals("6699")) E = agent;
		}
		if (close(A,task.getNode())){
			A.setPropertyValue("plan", "Report to incident commander");
			A.setPropertyValue("toLocation", location(task.getNode()));
			B.setPropertyValue("plan", "Report to incident commander");
			B.setPropertyValue("toLocation", location(task.getNode()));
			E.setPropertyValue("plan", "Report to incident commander");
			E.setPropertyValue("toLocation", location(task.getNode()));
	    	return Response.ok("Arrived at incident").build();
		} 
		if (close(A,E) || E.getPropertyValue("toLocation").equals(location(task.getNode()))){
	    	A.setPropertyValue("toLocation", location(task.getNode()));
	    	B.setPropertyValue("toLocation", location(task.getNode()));
	    	E.setPropertyValue("toLocation", location(task.getNode()));
	    	if (moving(A)){
		    	A.setPropertyValue("plan", "Drive towards incident location.");
		    	E.setPropertyValue("plan", "Stay in truck while traveling");
		    	B.setPropertyValue("plan", "Stay in truck while traveling");
		    	return Response.ok("Drive together to incident").build();
			} else {
				A.setPropertyValue("plan", "Drive towards incident location when E has entered the truck");
				E.setPropertyValue("plan", "Please get into the FireTruck");
		    	B.setPropertyValue("plan", "Stay in truck while traveling");
		    	return Response.ok("Waiting for E to enter the truck").build();
			}
	    }
		if (!close(A,B) && !moving(A)){
	    	A.setPropertyValue("plan", "Please get into the FireTruck");
	    	A.setPropertyValue("toLocation",location(A));
	    	B.setPropertyValue("plan", "Proceed to the FireTruck at the base.");
	    	B.setPropertyValue("toLocation",location(A));
	    	E.setPropertyValue("plan", "Wait for the FireTruck at your present location.");
	    	E.setPropertyValue("toLocation",location(E));
	    	return Response.ok("Waiting for B to reach A").build();
		}
    	A.setPropertyValue("plan", "Drive towards E when B has entered the truck.");
    	A.setPropertyValue("toLocation",location(E));
    	B.setPropertyValue("plan", "Please get into the FireTruck");
    	B.setPropertyValue("toLocation",location(E));
    	E.setPropertyValue("plan", "Wait for the FireTruck at your present location.");
    	E.setPropertyValue("toLocation",location(E));
    	if (moving(A) && moving(B)){
	    	A.setPropertyValue("plan", "Drive towards E.");
	    	A.setPropertyValue("toLocation",location(E));
	    	B.setPropertyValue("plan", "Stay in truck while traveling");
	    	B.setPropertyValue("toLocation",location(E));
	    	E.setPropertyValue("plan", "Wait for the FireTruck at your present location.");
	    	E.setPropertyValue("toLocation",location(E));
	    	return Response.ok("Waiting for A & B  to reach E").build();
    	}
    	return Response.ok("Waiting for B to get into truck").build();
	}
}
