package com.traxit.widget;

import java.util.List;

public class TraxitDataManager {
	private List<GroupInfo> groups;
	private List<PersonInfo> unGroups;
	private List<PersonInfo> requests;
	
	public TraxitDataManager(List<GroupInfo> groups, List<PersonInfo> unGroups, List<PersonInfo> requests){
		this.groups = groups;
		this.unGroups = unGroups;
		this.requests = requests;
	}
	
	public List<GroupInfo> getGroups(){
		return this.groups;
	}
	public List<PersonInfo> getUnGroup(){
		return this.unGroups;
	}
	public List<PersonInfo> getRequests(){
		return this.requests;
	}
}
