package com.almende.appservices.proxy;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.almende.appservices.model.Agent;

import flexjson.JSONSerializer;
import flexjson.JSONDeserializer;

@Path("/agent")
public class AgentProxy {
	protected final static Logger log = Logger.getLogger(AgentProxy.class.getName());
	
	@POST
	@Consumes("text/plain,application/json")
	public Response createAgent(String json){
		new JSONDeserializer<Agent>().use(null, Agent.class).deserialize(json);
		return Response.ok().build();
	}
	
	@Path("/{uuid}")
	@GET
	public Response getAgent(@PathParam("uuid") String uuid){
		try {
			Agent agent = new Agent(uuid);
			
			return Response.ok(new JSONSerializer().exclude("*.class").serialize(agent)).build();
		} catch (Exception e){
			log.warning("Exception in handling getAgent:"+e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@PUT
	@Consumes("text/plain,application/json")
	public Response createOrUpdateAgent(String json, @PathParam("uuid") String uuid){
		return Response.ok().build();		
	}
}
