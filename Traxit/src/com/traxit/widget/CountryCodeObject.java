package com.traxit.widget;

public class CountryCodeObject {
	private String mCountryCode;
	private String mCountryName;
	private String mCallingCode;
	
	public CountryCodeObject(String code, String name, String callingCode){
		this.setCountryCode(code);
		this.setCountryName(name);
		this.setCallingCode(callingCode);
	}
	
	public void setCountryCode(String code){
		this.mCountryCode = code;
	}
	public String getCountryCode(){
		return this.mCountryCode;
	}
	
	public void setCountryName(String name){
		this.mCountryName = name;
	}
	public String getCountryName(){
		return this.mCountryName;
	}
	public void setCallingCode(String code){
		this.mCallingCode = code;
	}
	public String getCallingCode(){
		return this.mCallingCode;
	}
}
