package com.almende.appservices.proxy;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.almende.appservices.model.Agent;
import com.almende.appservices.model.Resource;
import com.almende.appservices.model.Task;
import com.chap.memo.memoNodes.MemoNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Path("/agent/{agentId}/tasks")
public class TaskProxy {
	protected final static Logger log = Logger.getLogger(TaskProxy.class.getName());
	static final ObjectMapper om = new ObjectMapper();

	@GET
	@Produces("application/json")
	public Response getTasks(@PathParam("agentId") String agentId){
		try {
			MemoNode tasks = new Agent(agentId).getNode().getChildByStringValue("tasks");
			if (tasks != null){
				ArrayNode result = om.createArrayNode();
				for (MemoNode task: tasks.getChildren()){
					result.add(om.valueToTree(new Task(task)));
				}
				return Response.ok(result.toString()).build();
			}
			return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.serverError().build();
	}
	
	public Response setTaskState(String agentId,String taskId, String state){
		try {
			Task task = new Task(taskId);
			if (task != null){
				ArrayList<Resource> resources = task.getResources();
				for (Resource res : resources){
					if (res.getId().equals(agentId)){
						res.setState(state);
					return getTasks(agentId);
//						return getTask(agentId,taskId);
					}
				}
			}
			return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (Exception e){
			e.printStackTrace();
		}
		return Response.serverError().build();		
	}
	
	@Path("/{taskid}")
	@GET
	@Produces("application/json")
	public Response getTask(@PathParam("agentId") String agentId,@PathParam("taskid") String taskId){
		try {
			Task task = new Task(taskId);
			if (task != null){
				ArrayNode result =om.valueToTree(task);
				return Response.ok(result.toString()).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.serverError().build();
	}
	
	@Path("/{taskid}/accept")
	@POST
	public Response acceptTask(@PathParam("agentId") String agentId, @PathParam("taskid") String taskId){
		return setTaskState(agentId,taskId,"accepted");
	}
	@Path("/{taskid}/reject")
	@POST
	public Response rejectTask(@PathParam("agentId") String agentId, @PathParam("taskid") String taskId){
		return setTaskState(agentId,taskId,"rejected");
	}
	@Path("/{taskid}/withdraw")
	@POST
	public Response withdrawfromtask(@PathParam("agentId") String agentId, @PathParam("taskid") String taskId){
		return setTaskState(agentId,taskId,"rejected");
	}

}
