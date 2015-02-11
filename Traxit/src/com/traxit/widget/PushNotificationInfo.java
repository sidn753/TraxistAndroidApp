package com.traxit.widget;

import com.traxit.common.Config;

public class PushNotificationInfo {
	private String type;
	private String sendFrom;
    private String message;
    private long timeInterval;
    private String fullName;
    private double latitude;
    private double longitude;
    
    public PushNotificationInfo(){
    	
    }
    public void pushNotificationMessage(String sendFrom, String name, String message,  long time){
    	this.type = Config.kPushMessageType;
    	this.sendFrom = sendFrom;
    	this.fullName = name;
    	this.message = message;
    	this.timeInterval = time;
    }
    
    public void pushNotificationInvite(String sendFrom, String name){
    	this.type = Config.kPushInviteType;
    	this.sendFrom = sendFrom;
    	this.fullName = name;
    }
    
    public void pushNotificationDecline(String sendFrom, String name){
    	this.type = Config.kPushDeclineType;
    	this.sendFrom = sendFrom;
    	this.fullName = name;
    }
    
    public void pushNotificationAccept(String sendFrom, String name, double lat, double lon){
    	this.type = Config.kPushAcceptType;
    	this.sendFrom = sendFrom;
    	this.fullName = name;
    	this.latitude = lat;
    	this.longitude = lon;
    }
    public void pushNotificationLocaiton(String sendFrom, double lat, double lon){
    	this.type = Config.kPushLocationType;
    	this.sendFrom = sendFrom;
    	this.latitude = lat;
    	this.longitude = lon;
    }
    
    // GET
    public String getType(){
    	return this.type;
    }
    public String getSender(){
    	return this.sendFrom;
    }
    public String getName(){
    	return this.fullName;
    }
    public String getMessage(){
    	return this.message;
    }
    public long getTime(){
    	return this.timeInterval;
    }
    public double getLatitude(){
    	return this.latitude;
    }
    public double getLongitude(){
    	return this.longitude;
    }
}
