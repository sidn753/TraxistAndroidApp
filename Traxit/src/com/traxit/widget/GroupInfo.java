package com.traxit.widget;

import java.util.List;

public class GroupInfo {
    private double groupId;
   // private int    countOfFriends;
    private String groupName;
    private List<PersonInfo> friends;
    private boolean isExpande;
	
	public GroupInfo(double id, int count, String name, List<PersonInfo> listFriends){
		this.groupId = id;
		//this.countOfFriends = count;
		this.groupName = name;
		this.friends = listFriends;
		this.isExpande = false;
	}
	public void setGroupId(double id){
		this.groupId = id;
	}
	public void setGroupName(String name){
		this.groupName = name;
	}
	public void setCountOfFriends(int count){
		//this.countOfFriends = count;
	}
	public void setFriends(List<PersonInfo> listFriends){
		this.friends = listFriends;
	}
	public void setExpandable(boolean is){
		this.isExpande = is;
	}
	public double getGroupId(){
		return this.groupId;
	}
	/*public int getCountOfFriends(){
		return this.countOfFriends;
	}*/
	public String getGroupName(){
		return this.groupName;
	}
	public List<PersonInfo> getFriends(){
		return this.friends;
	}
	
	public void addFriend(PersonInfo friend){
		this.friends.add(friend);
	}
	
	public boolean isExpanable(){
		return this.isExpande;
	}
	
	
}
