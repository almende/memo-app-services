package com.almende.appservices.model;

import java.io.Serializable;
import java.util.HashMap;

import com.chap.memo.memoNodes.MemoNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Resource implements Serializable {
	private static final long serialVersionUID = 2149798277058504607L;
	static final ObjectMapper om = new ObjectMapper();
	private final static HashMap<String,String> typeMap = new HashMap<String,String>(6); 
	
	MemoNode myNode;
	public Resource(){
		typeMap.put("PoliceOfficer","human");
		typeMap.put("FireFighter","human");
		typeMap.put("Medic","human");
		typeMap.put("PoliceCar","car");
		typeMap.put("FireTruck","car");
		typeMap.put("Ambulance","car");
	};
	public Resource(MemoNode resource){
		this();
		myNode = resource;
	}
	
	public String getType(){
		return typeMap.get(myNode.getPropertyValue("resType")); 
	}
	public String getId(){
		return myNode.getId().toString();
	}
	public ObjectNode getDetails(){
		ObjectNode result = om.createObjectNode();
		if (getType().equals("car")){
			result.put("carType", myNode.getPropertyValue("resType"));			
		} else {
			result.put("role", myNode.getPropertyValue("resType"));						
			result.put("name", myNode.getPropertyValue("name"));
			result.put("status", myNode.getPropertyValue("state"));
		}
		return result;
	}
}
