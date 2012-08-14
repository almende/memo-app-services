package com.almende.appservices.proxy;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.almende.appservices.model.Agent;
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
		MemoNode tasks = new Agent(agentId).getNode().getChildByStringValue("tasks");
		try {
			ArrayNode result = om.createArrayNode();
			for (MemoNode task: tasks.getChildren()){
				result.add(om.valueToTree(new Task(task)));
			}
			return Response.ok(result.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.serverError().build();
	}
	
}
