package com.almende.appservices.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.chap.memo.memoNodes.MemoNode;
import com.eaio.uuid.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Task implements Serializable {
	private static final long serialVersionUID = 712370311457400115L;

	MemoNode myNode;
	
	public Task(){}
	public Task(MemoNode task) {
		myNode = task;
	}
	public Task(String uuid) {
		myNode = new MemoNode(new UUID(uuid));
	}
	@JsonIgnore
	public MemoNode getNode(){
		return myNode;
	}
	public String getId(){
		return myNode.getId().toString();
	}
	public String getDescription(){
		return myNode.getPropertyValue("description");
	}
	public String getLat(){
		return myNode.getPropertyValue("lat");
	}
	public String getLon(){
		return myNode.getPropertyValue("lon");
	}
	public int getDuration(){
		return Integer.parseInt(myNode.getPropertyValue("duration"));
	}
	public long getEta(){
		return Long.parseLong(myNode.getPropertyValue("eta"));
	}
	public ArrayList<Resource> getResources(){
		ArrayList<Resource> result = new ArrayList<Resource>();
		MemoNode resources = myNode.getChildByStringValue("resources");
		if (resources == null){
			System.out.println("Task without resources?");
			return new ArrayList<Resource>(0);
		}
		for (MemoNode resType : myNode.getChildByStringValue("resources").getChildren()){
			if (resType.getStringValue().equals("car")){
				for (MemoNode resource : resType.getChildren()){
					result.add(new Resource(resource,myNode));		
				}
			} else {
				for (MemoNode list: resType.getChildren()){
					for (MemoNode resource : list.getChildren()){
						result.add(new Resource(resource,myNode));
					}
				}
			}
		}
		return result;
	}
	public String getState(){
		int count = myNode.getChildByStringValue("resources")
				          .getChildByStringValue("human")
				          .getChildByStringValue("offered")
				          .getChildren().size();
		return count>0?"pending":"operational";
	}
}
