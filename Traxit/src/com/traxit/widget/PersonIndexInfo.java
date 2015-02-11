package com.traxit.widget;

public class PersonIndexInfo {
	private int groupIndex;
	private int childIndex;
	private boolean isUngroup;
	
	public PersonIndexInfo(int groupIndex, int childIndex, boolean isUngroup){
		this.groupIndex = groupIndex;
		this.childIndex = childIndex;
		this.isUngroup = isUngroup;
	}
	
	public int groupIndex(){
		return this.groupIndex;
	}
	public int childIndex(){
		return this.childIndex;
	}
	public boolean isUngroup(){
		return this.isUngroup;
	}
}
