package com.almende.appservices.model;

import java.util.Date;
import java.util.Hashtable;

import com.chap.memo.memoNodes.MemoNode;
import com.eaio.uuid.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Agent {
	MemoNode myNode=null;
	Hashtable<String,String> resources = new Hashtable<String,String>();
	
	public Agent(){
	}
	public Agent initNew(){
		this.myNode=new MemoNode("agent");
		return this;
	}
	public static Agent create(){
		return new Agent().initNew();
	}
	@JsonIgnore
	public MemoNode getNode(){
		return this.myNode;
	}
	public Agent(String uuid){
		this.myNode=new MemoNode(new UUID(uuid));
	}
	public String getUuid(){
		return myNode.getId().toString();
	}
	public String getName(){
		return myNode.getPropertyValue("name");
	}
	public String getLat(){
		return myNode.getPropertyValue("lat");
	}
	public String getLon(){
		return myNode.getPropertyValue("lon");
	}
	public String getType(){
		return myNode.getPropertyValue("resType");
	}
	
	public void setUuid(String uuid){
		this.myNode = new MemoNode(uuid);
	}
	public void setName(String propValue){
		myNode.setPropertyValue("name", propValue);
	}
	public void setLat(String propValue){
		myNode.setPropertyValue("lat", propValue);
		myNode.setPropertyValue("seen", new Long(new Date().getTime()).toString());
	}
	public void setLon(String propValue){
		myNode.setPropertyValue("lon", propValue);
		myNode.setPropertyValue("seen", new Long(new Date().getTime()).toString());
	}
	public void setType(String resType){
		myNode.setPropertyValue("resType", resType);
	}

}
