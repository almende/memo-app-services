package com.almende.appservices.proxy;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.almende.appservices.InitDemoListener;
import com.almende.appservices.model.Agent;
import com.chap.memo.memoNodes.MemoNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/agent")
//See: TaskProxy.java for /agent/{uuid}/tasks
public class AgentProxy {
	protected final static Logger log = Logger.getLogger(AgentProxy.class.getName());
	static final ObjectMapper om = new ObjectMapper();

	static MemoNode baseNode =null;
	public AgentProxy(){
		InitDemoListener.initDemoModel();
		baseNode = MemoNode.getRootNode().getChildByStringValue("Memo-appservices demo").getChildByStringValue("agents");
	}
	@GET
	@Produces("application/json")
	public Response getAgents(){
		try {
			ArrayList<MemoNode> agents = baseNode.getChildren();
			ArrayList<String> result =  new ArrayList<String>(agents.size());
			for (MemoNode agent : agents){
				result.add(agent.getId().toString());
			}
			return Response.ok(om.writeValueAsString(result)).build();
		} catch (Exception e){
			log.warning("Exception in handling getAgent:"+e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@POST
	@Consumes("text/plain,application/json")
	@Produces("text/plain")
	public Response createAgent(String json){
		Agent agent=Agent.create();
		try {
			agent = om.readerForUpdating(agent).readValue(json);
		
		//Until I have group support:
		baseNode.addChild(agent.getNode());
		//Done workaround
		
		return Response.ok(agent.getUuid()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.serverError().build();
		
	}
	
	@Path("/{uuid}")
	@GET
	@Produces("application/json")
	public Response getAgent(@PathParam("uuid") String uuid){
		try {
			Agent agent = new Agent(uuid);
			return Response.ok(om.writeValueAsString(agent)).build();
		} catch (Exception e){
			log.warning("Exception in handling getAgent:"+e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@Path("/{uuid}")
	@PUT
	@Consumes("text/plain,application/json")
	@Produces("text/plain")
	public Response createOrUpdateAgent(String json, @PathParam("uuid") String uuid){
		Agent agent = new Agent(uuid);
		try {
			agent = om.readerForUpdating(agent).readValue(json);

			//Until I have group support:
			baseNode.addChild(agent.getNode());
			//Done workaround
		
			return Response.ok(agent.getUuid()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.serverError().build();
	}	
	
	@Path("/{uuid}")	
	@POST
	@Consumes("text/plain,application/json")
	@Produces("text/plain")
	public Response createAgentWithId(String json){
		Agent agent = Agent.create();
		try {
			agent = om.readerForUpdating(agent).readValue(json);

			//Until I have group support:
			baseNode.addChild(agent.getNode());
			//Done workaround
		
			return Response.ok(agent.getUuid()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.serverError().build();
	}
}
