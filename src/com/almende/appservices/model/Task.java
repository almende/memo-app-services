package com.almende.appservices.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.chap.memo.memoNodes.MemoNode;

public class Task implements Serializable {
	private static final long serialVersionUID = 712370311457400115L;

	MemoNode myNode;
	
	public Task(){}
	public Task(MemoNode task) {
		myNode = task;
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
		for (MemoNode resType : myNode.getChildByStringValue("resources").getChildren()){
			if (resType.getStringValue().equals("car")){
				for (MemoNode resource : resType.getChildren()){
					result.add(new Resource(resource));		
				}
			} else {
				for (MemoNode list: resType.getChildren()){
					for (MemoNode resource : list.getChildren()){
						result.add(new Resource(resource));
					}
				}
			}
			
		}
		return result;
	}
}
