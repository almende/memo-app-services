package com.almende.appservices.model;

import java.io.Serializable;
import java.util.HashMap;

import com.chap.memo.memoNodes.MemoNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Resource implements Serializable {
	private static final long serialVersionUID = 2149798277058504607L;
	static final ObjectMapper om = new ObjectMapper();
	private final static HashMap<String,String> typeMap = new HashMap<String,String>(6); 
	
	MemoNode myNode;
	MemoNode task;
	public Resource(){
		typeMap.put("PoliceOfficer","human");
		typeMap.put("FireFighter","human");
		typeMap.put("Medic","human");
		typeMap.put("PoliceCar","car");
		typeMap.put("FireTruck","car");
		typeMap.put("Ambulance","car");
	};
	public Resource(MemoNode resource, MemoNode task){
		this();
		myNode = resource;
		this.task = task;
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
			result.put("state", getState());
		}
		return result;
	}
	@JsonIgnore
	public String getState(){
		MemoNode resList = task.getChildByStringValue("resources").getChildByStringValue("human");
		if (myNode.isChildOf(resList.getChildByStringValue("offered")))return "offered";
		if (myNode.isChildOf(resList.getChildByStringValue("accepted")))return "accepted";
		if (myNode.isChildOf(resList.getChildByStringValue("rejected")))return "rejected";
		return "unknown";
	}
	@JsonIgnore
	public void setState(String newState){
		MemoNode resList = task.getChildByStringValue("resources").getChildByStringValue("human");
		String currentState = getState();
		resList.getChildByStringValue(currentState).delChild(myNode);
		resList.getChildByStringValue(newState).addChild(myNode);
	}
}
