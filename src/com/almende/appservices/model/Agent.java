package com.almende.appservices.model;

import java.util.Hashtable;

import com.chap.memo.memoNodes.MemoNode;
import com.eaio.uuid.UUID;

public class Agent {
	MemoNode myNode=null;
	Hashtable<String,String> resources = new Hashtable<String,String>();
	
	public Agent(){
		this.myNode = new MemoNode("agent");
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
	public void setUuid(String uuid){
		this.myNode = new MemoNode(uuid);
	}
	public void setName(String propValue){
		myNode.setPropertyValue("name", propValue);
	}
	public void setLat(String propValue){
		myNode.setPropertyValue("lat", propValue);
	}
	public void setLon(String propValue){
		myNode.setPropertyValue("lon", propValue);
	}

}
