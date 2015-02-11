/**
 * Author: wozheguo
 * date:2/3/2014
 * */
package com.traxit.library;

import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.util.Log;

import com.traxit.common.Config;

public class UserFunctions {
	
	private JSONParser jsonParser;

	private static String traxitURL;

	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
		traxitURL = Config.TraxitServerURL;
	}
	
	/**
	 * function make Login Request
	 * @param email
	 * @param password
	 * */
	public JSONObject loginUser(String email, String password, String token){
		// Building Parameters
		//NSDictionary * parameters = @{@"tag":@"login", @"email":email, @"password":password, @"token":token};
		Log.e("Login", "email:"+email);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "login"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", getMD5EncryptedString(password)));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("device", String.valueOf(1)));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	/**
	 * function make signup Request
	 * @param email
	 * @param password
	 * */
	public JSONObject signUp(String email, String password, String firstName, String lastName, String countryCode, String phoneNumber,  String token){
		// Building Parameters
		/*	    NSDictionary * parameters = @{@"tag":@"register",
		        @"email":email,
		        @"password":password,
		        @"firstName":firstName,
		        @"lastName":lastName,
		        @"countryCode":userCountryCallingCode,
		        @"phoneNumber":phoneNumber,
		        @"device":@"0",
		        @"token":token};*/
		Log.e("SignUp", "email:"+email);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "register"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", getMD5EncryptedString(password)));
		params.add(new BasicNameValuePair("firstName", firstName));
		params.add(new BasicNameValuePair("lastName", lastName));
		params.add(new BasicNameValuePair("countryCode", countryCode));
		params.add(new BasicNameValuePair("phoneNumber", phoneNumber));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("device", String.valueOf(1)));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	
	/**
	 * Download traxit data
	 * @param email
	 * */
	public JSONObject getInitTraxitData(String email){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "getInitContactsData"));
		params.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	
	/**
	 * Download Message History
	 * @param email
	 * @param friendEmail
	 * @param chat page number
	 * */
	public JSONObject getMessageHistory(String email, String friendEmail, int recodeCount){
		// @"friend":friendEmail, @"pageNumber":
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "getMessageHistory"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("friend", friendEmail));
		params.add(new BasicNameValuePair("recodeCount", String.valueOf(recodeCount)));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	/**
	 * Download Message History
	 * @param email
	 * @param friendEmail
	 * @param chat page number
	 * */
	public JSONObject readMessage(String email, String friendEmail){
		// @"friend":friendEmail, @"pageNumber":
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "readMessage"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("friend", friendEmail));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	
	/**
	 * send message to friend
	 * @param email
	 * @param friendEmail
	 * @param message
	 * */
	public JSONObject sendMessageToServer(String email, String friendEmail, String message){
		//   NSDictionary * parameters = @{@"tag":@"message", @"email":email, @"friend":friendEmail, @"message":message}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "message"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("friend", friendEmail));
		params.add(new BasicNameValuePair("message", message));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	
	
	
	/**
	 * send invite request to friend
	 * @param email
	 * @param friendEmail
	 * @param message
	 * */
	public JSONObject sendInviteRequestToServer(String email, List<String> friends, List<String> numbers){
		//   NSDictionary * parameters = @{@"tag":@"invite", @"email":email, @"friends":friends, @"mobiles":mobiles};
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "invite"));
		params.add(new BasicNameValuePair("email", email));
		for(int i = 0 ; i <  friends.size(); i++){
			params.add(new BasicNameValuePair("friends[]", friends.get(i)));	
		}
		for(int i = 0 ; i <  numbers.size(); i++){
			params.add(new BasicNameValuePair("mobiles[]", numbers.get(i)));	
		}
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	/**
	 * update Location
	 * @param email
	 * @param latitude
	 * @param logitude
	 * */
	public JSONObject updateLocation(String email, double latitude, double longitude){
		//  NSDictionary * parameters = @{@"tag":@"updateLocation", @"email":email, @"longitude":longitude,@"latitude":latitude};
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "updateLocation"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
		params.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	
	
	/**
	 * Accept request
	 * @param email
	 * @param friendEmail
	 * */
	public JSONObject acceptInviteRequestFrom(String email, String friendEmail){
		//  NSDictionary * parameters = @{@"tag":@"inviteAccept", @"email":email, @"friend":friendEmail};
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "inviteAccept"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("friend",friendEmail));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	
	
	/**
	 * Decline request
	 * @param email
	 * @param friendEmail
	 * */
	public JSONObject declineInviteRequestFrom(String email, String friendEmail){
		//   NSDictionary * parameters = @{@"tag":@"inviteDecline", @"email":email, @"friend":friendEmail};
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "inviteDecline"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("friend",friendEmail));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	
	/**
	 * Change group name
	 * @param email
	 * @param name
	 * @param newName
	 * */
	public JSONObject changeGroupName(String email, String originalName, String newGroupName){
		//NSDictionary * parameters = @{@"tag":@"changeGroupName", @"email":email, @"name":originalName, @"newName":newGroupName};
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "changeGroupName"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("name",originalName));
		params.add(new BasicNameValuePair("newName",newGroupName));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	
	/**
	 * Add new group
	 * @param email
	 * @param newName
	 * */
	public JSONObject addNewGroupItem(String email, String newGroupName){
		//NSDictionary * parameters = @{@"tag":@"addNewGroup", @"email":email, @"name":newGroupName};
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "addNewGroup"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("name",newGroupName));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	
	
	/**
	 * Add new group
	 * @param email
	 * @param newName
	 * */
	public JSONObject deleteGroup(String email, String groupName){
		//NSDictionary * parameters = @{@"tag":@"removeGroup", @"email":email, @"name":groupName};
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "removeGroup"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("name",groupName));
		JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
		return json;
	}
	

		/**
		 * update group
		 * @param email
		 * @param groupId
		 * @param removed Items
		 * @param added Items
		 * */
		public JSONObject updateGroup(String email, double groupId,  List<String> removedItems, List<String> addedItems){
			//   NSDictionary * parameters = @{@"tag":@"updateMember", @"email":email, @"groupId":groupId, @"remove":removedItems,@"add":addedItems};
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "updateMember"));
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("groupId", String.valueOf(groupId)));
			for(int i = 0 ; i <  removedItems.size(); i++){
				params.add(new BasicNameValuePair("remove[]", removedItems.get(i)));	
			}
			for(int i = 0 ; i <  addedItems.size(); i++){
				params.add(new BasicNameValuePair("add[]", addedItems.get(i)));	
			}
			JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
			return json;
		}

		/**
		 * load profile
		 * @param email
		 * @param groupId
		 * @param removed Items
		 * @param added Items
		 * */
		public JSONObject loadProfile(String email){
			//  NSDictionary * parameters = @{@"tag":@"getProfile", @"email":email};
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "getProfile"));
			params.add(new BasicNameValuePair("email", email));
			JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
			return json;
		}
		
		/**
		 * update profile
		 * @param email
		 * @param groupId
		 * @param removed Items
		 * @param added Items
		 * */
		public JSONObject updateProfile(String email, String firstName, String lastName, String countryCode, String phoneNumber){
			/* 
			 *   NSDictionary * parameters = @{@"tag":@"updateProfile",
	        @"email":email,
	        @"firstName":firstName,
	        @"lastName":lastName,
	        @"countryCode":userCountryCallingCode,
	        @"phoneNumber":phoneNumber};*/
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "updateProfile"));
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("firstName", firstName));
			params.add(new BasicNameValuePair("lastName", lastName));
			params.add(new BasicNameValuePair("countryCode", countryCode));
			params.add(new BasicNameValuePair("phoneNumber", phoneNumber));
			JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
			return json;
		}
		/**
		 * Delete friend
		 * @param email
		 * @param friend
		 * */
		public JSONObject deleteFriend(String email, String friend){
			//NSDictionary * parameters = @{@"tag":@"removeGroup", @"email":email, @"name":groupName};
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "removeFriend"));
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("friend",friend));
			JSONObject json = jsonParser.getJSONFromUrl(traxitURL, params);
			return json;
		}
	/**
	 * 
	 * Check connection to server
	 * */
	public boolean isConnectedToServer(String url, int timeout) {
	    try{
	        URL myUrl = new URL(url);
	        URLConnection connection = myUrl.openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.connect();
	        return true;
	    } catch (Exception e) {
	        // Handle your exceptions
	        return false;
	    }
	}

	private String getMD5EncryptedString(String encTarget){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
	}
	
}
