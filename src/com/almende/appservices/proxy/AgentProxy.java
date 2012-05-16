package com.almende.appservices.proxy;

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

import com.almende.appservices.model.Agent;
import com.chap.memo.memoNodes.MemoNode;

import flexjson.JSONSerializer;
import flexjson.JSONDeserializer;

@Path("/agent")
public class AgentProxy {
	protected final static Logger log = Logger.getLogger(AgentProxy.class.getName());
	
	@POST
	@Consumes("text/plain,application/json")
	@Produces("text/plain")
	public Response createAgent(String json){
		Agent agent = new JSONDeserializer<Agent>().deserializeInto(json, Agent.create());
		
		//Until I have group support:
		MemoNode baseNode = MemoNode.getRootNode().getChildByStringValue("geoRSS demo");
		baseNode.addChild(agent.getNode());
		//Done workaround
		
		return Response.ok(agent.getUuid()).build();
	}
	
	@Path("/{uuid}")
	@GET
	@Produces("application/json")
	public Response getAgent(@PathParam("uuid") String uuid){
		try {
			Agent agent = new Agent(uuid);
			return Response.ok(new JSONSerializer().exclude("*.class").serialize(agent)).build();
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
		agent = new JSONDeserializer<Agent>().deserializeInto(json, agent);

		//Until I have group support:
		MemoNode baseNode = MemoNode.getRootNode().getChildByStringValue("geoRSS demo");
		baseNode.addChild(agent.getNode());
		//Done workaround
		
		return Response.ok(agent.getUuid()).build();
	}		
}
