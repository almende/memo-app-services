package com.almende.appservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.digest.DigestUtils;

import com.chap.memo.memoNodes.MemoNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class PersAgent {
	protected final static Logger log = Logger.getLogger(PersAgent.class.getName());
	private MemoNode myNode= null;
	static final ObjectMapper om = new ObjectMapper();

	public static PersAgent getPersAgentById(String uuid){
		PersAgent result = new PersAgent();
		HashMap<String,String> arguments = new HashMap<String,String>();
		arguments.put("id", uuid);
		ArrayList<MemoNode> res = MemoNode.getRootNode().search(PersAgent.getPreamble(), PersAgent.getPattern(),-1,arguments);
		if (res.size()<=0){
			return null;
		}
		result.myNode = res.get(0);
		if (result.myNode != null){
			return result;			
		} else {
			return null;
		}
	}
	
	public static MemoNode getPattern(){
		MemoNode rootNode = MemoNode.getRootNode();
		MemoNode pattern;
		ArrayList<MemoNode> result = rootNode.getChildrenByStringValue("PersAgentPattern",1);
		if (result == null || result.size() == 0){
			pattern    = new MemoNode("PersAgentPattern");
			rootNode.addChild(pattern);
			
			MemoNode patternQ = new MemoNode("equal;PersAgent");
			MemoNode patternChild = new MemoNode("equal;id");
			patternQ.addChild(patternChild);
			MemoNode patternChild2 = new MemoNode("equal;arg(id)");
			patternChild.addChild(patternChild2);
			
			pattern.addChild(patternQ);
		} else {
			pattern = result.get(0);
		}
		return pattern;
	}
	public static MemoNode getPreamble(){
		MemoNode rootNode = MemoNode.getRootNode();
		MemoNode pattern;
		ArrayList<MemoNode> result = rootNode.getChildrenByStringValue("PersAgentPreAmble",1);
		if (result == null || result.size() == 0){
			pattern    = new MemoNode("PersAgentPreAmble");
			MemoNode patternQ = new MemoNode("equal;root");
			MemoNode patternChild = new MemoNode("equal;agents");
			MemoNode patternChild2 = new MemoNode("any");
			patternQ.addChild(patternChild);			
			patternChild.addChild(patternChild2);			
			pattern.addChild(patternQ);
			rootNode.addChild(pattern);
		} else {
			pattern = result.get(0);
		}
		return pattern;
	}
	
	/*
	 * --------------------------------------------------------------------------
	 * Proxy methods:
	 * --------------------------------------------------------------------------
	 */
	@Path("/resources")
	@GET
	@Produces("application/json")
	public Response getResources(@QueryParam("tags") String tags ){
		if (myNode == null) return Response.noContent().build();
		MemoNode resNode = myNode.getChildByStringValue("resources");
		if (resNode == null) return Response.ok().build();
		HashMap<String,String> result = new HashMap<String,String>();
		if ( tags == null || tags.equals( "" ) ) {
			ArrayList<MemoNode> resources = resNode.getChildren();
			for (MemoNode res : resources){
				ArrayList<MemoNode> values = res.getChildren();
				if (values == null || values.size() == 0) continue;
				MemoNode value = values.get(0);
				result.put(res.getStringValue(), value.getStringValue());
			}
		} else {
			try {
				result = om.readValue(tags,new TypeReference<Map<String,String>>(){});
				for (Map.Entry<String, String> e : result.entrySet()) {
					String value=resNode.getPropertyValue(e.getKey());
					if (value != null && !value.equals("")){
						e.setValue(value);
					}
				}
			} catch (Exception e){
				log.warning("Couldn't parse tags!");
			}
		}
		try {
			return Response.ok(om.writeValueAsString(result)).build();
		} catch (Exception e) {
			log.warning("Couldn't get resources!"+e.getMessage());
		}
		return Response.serverError().build();
	}
	
	@GET
	@Path("/register")
	public Response register(@Context UriInfo info
			, @QueryParam("uuid") String uuid, @QueryParam("pass") String pass
			, @QueryParam("name") String name, @QueryParam("phone") String phone 
			, @QueryParam("module") String module, @QueryParam("plainPass") String plainPass) {
		
		if (uuid ==null || uuid.equals("")) return Response.status( Response.Status.BAD_REQUEST ).build();
		if ((pass == null || pass.equals(""))&&(plainPass == null || plainPass.equals(""))) return Response.status( Response.Status.BAD_REQUEST ).build();
		if (PersAgent.getPersAgentById(uuid) != null) return Response.status(Response.Status.CONFLICT ).build();
		
		MemoNode rootNode = MemoNode.getRootNode();
		ArrayList<MemoNode> agents = rootNode.getChildrenByStringValue("agents", 1);
		if (agents.size() == 0) {
			MemoNode agentsNode = new MemoNode("agents");
			rootNode.addChild(agentsNode);
			agents.add(agentsNode);
		}
		MemoNode node = new MemoNode("PersAgent");
		node.setPropertyValue("id", uuid);
		agents.get(0).addChild(node);
		
		MemoNode res = new MemoNode("resources"); 
		node.addChild(res);
		if (pass != null && !pass.equals("")) res.setPropertyValue("pass", pass);
		if (name != null && !name.equals("")) res.setPropertyValue("name", name);
		if (phone != null && !phone.equals("")) res.setPropertyValue("phone", phone);
		if (plainPass != null && !plainPass.equals("")) res.setPropertyValue("pass", DigestUtils.md5Hex(plainPass));
		
		return Response.ok().build();
	}
	
	
}
