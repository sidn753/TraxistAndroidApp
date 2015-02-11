package com.traxit.widget;

public class NavDrawerItem {
	
	private String title;
	private int icon;
	// boolean to set visiblity of the counter
	private boolean isSwitchVisible = false;
	
	public NavDrawerItem(){}

	public NavDrawerItem(String title, int icon){
		this.title = title;
		this.icon = icon;
	}
	
	public NavDrawerItem(String title, int icon, boolean isSwitchVisible){
		this.title = title;
		this.icon = icon;
		this.isSwitchVisible = isSwitchVisible;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public int getIcon(){
		return this.icon;
	}
	
	public boolean getSwitchVisibility(){
		return this.isSwitchVisible;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setIcon(int icon){
		this.icon = icon;
	}

	
	public void setSwitchVisibility(boolean isSwitchVisible){
		this.isSwitchVisible = isSwitchVisible;
	}
}
