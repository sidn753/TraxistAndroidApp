package com.traxit.widget;

public class ResultCode {
	int mReturnCode;
	String mReturnMessage;
	
	public ResultCode(){
		this.setReturnCode("100");
		this.setReturnMessage("Can't access on Traxit server!");
	}
	public ResultCode(String code, String msg){
		this.setReturnCode(code);
		this.setReturnMessage(msg);
	}
	public void setReturnCode(String code){
		this.mReturnCode = Integer.parseInt(code);
	}
	public void setReturnMessage(String msg){
		this.mReturnMessage = msg;
	}
	public int getReturnCode(){
		return this.mReturnCode;
	}
	public String getReturnMessage(){
		return this.mReturnMessage;
	}
	
}
