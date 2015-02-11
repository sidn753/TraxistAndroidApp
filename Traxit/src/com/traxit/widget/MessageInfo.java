package com.traxit.widget;

public class MessageInfo {
	private boolean  isMine;
	private String messageTime;
	private String message;
	
	public MessageInfo(){
		
	}
	public MessageInfo(boolean isMine, String message, String time){
		this.isMine = isMine;
		this.message = message;
		this.messageTime = time;
	}
	
	public boolean getIsMine(){
		return this.isMine;
	}
	public String getMessage(){
		return this.message;
	}
	public String getTime(){
		return this.messageTime;
	}
	
	public void setIsMine(boolean mine){
		this.isMine = mine;
	}
	public void setMessage(String message){
		this.message = message;
	}
	public void setTime(String time){
		this.messageTime = time;
	}
}
