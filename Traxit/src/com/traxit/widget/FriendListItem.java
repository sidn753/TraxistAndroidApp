package com.traxit.widget;

import com.traxit.common.Config.FriendCellType;

public class FriendListItem {
	private FriendCellType mCellType;
	private String sectionHeaderTile;
	private GroupInfo groupInfo;
	private PersonInfo personInfo;
	public  boolean isExpanded;
	
	public FriendListItem( String sectiontitle){
		this.mCellType = FriendCellType.sectionHeader;
		this.sectionHeaderTile = sectiontitle;
	}
	public FriendListItem(GroupInfo group){
		isExpanded = false;
		this.mCellType = FriendCellType.groupCell;
		this.groupInfo = group;
	}
	public FriendListItem(PersonInfo person){
		this.mCellType = FriendCellType.ungroupCell;
		this.personInfo = person;
	}
	public FriendListItem(PersonInfo person, FriendCellType type){
		this.mCellType = type;
		this.personInfo = person;
	}
	
	public FriendCellType getCellType(){
		return this.mCellType;
	}
	public GroupInfo getGroupInfo(){
		return this.groupInfo;
	}
	public PersonInfo getPersonInfo(){
		return this.personInfo;
	}
	public String getSectionTitle(){
		return this.sectionHeaderTile;
	}

}
