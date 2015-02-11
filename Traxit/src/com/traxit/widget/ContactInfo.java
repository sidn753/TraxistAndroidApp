package com.traxit.widget;

import java.util.List;


public class ContactInfo {
	private String mFullName;
	private List<String> mEmails;
	private String mMobileNumber;
	private String mPhotoUri;
	
	public ContactInfo(List<String> emails, String name, String number, String photoUri){
		this.mFullName = name;
		this.mEmails = emails;
		this.mMobileNumber = number;
		this.mPhotoUri = photoUri;
	}
	
	public void setEmail(List<String> emails){
		this.mEmails = emails;
	}
	public void setName(String name){
		this.mFullName = name;
	}
	public void setPhoneNumber(String number){
		this.mMobileNumber = number;
	}
	public void setPhotoUri(String uri){
		this.mPhotoUri = uri;
	}
	public List<String> getEmail(){
		return this.mEmails;
	}
	public String getName(){
		return this.mFullName;
	}
	public String getPhoneNumer(){
		return this.mMobileNumber;
	}
	public String getPhotoUri(){
		return this.mPhotoUri;
	}
}
