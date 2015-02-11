package com.traxit.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.traxit.widget.ContactInfo;
import com.traxit.widget.GroupInfo;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.TraxitDataManager;


public class AppSetting {
	public static TraxitDataManager traxitDataManager;
	public static List<ContactInfo> contacts;
	public static LatLng currentUserLocation;
	public static String currentChatFriendEmail;
	
	//--------------------------Profile setting CheckFlag----------------------//
    
    public static Boolean mProfileEditableFlag = false;
    public static Boolean mProfileSaveableFlag = false;
    public static String m_countrycode = "";
	public static String m_dialcode = "";
	
	/*------------------
	 * 		 UserInfo   
	 -------------------------*/
	public static void saveUserLoginInfo(Activity activity,String email, String password){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		SharedPreferences.Editor editor = sharedPred.edit();
		editor.putString(Config.UserEmail, email);
		editor.putString(Config.UserPassword, password);
		editor.putBoolean(Config.LoginStatus, true);
		editor.commit();
	}
	public static void saveUserName(Activity activity, String name){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		SharedPreferences.Editor editor = sharedPred.edit();
		editor.putString(Config.UserName, name);
		editor.commit();
	}
	
	public static void saveFirstName(Activity activity, String name){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		SharedPreferences.Editor editor = sharedPred.edit();
		editor.putString(Config.FirstName, name);
		editor.commit();
	}
	public static void saveLastName(Activity activity, String name){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		SharedPreferences.Editor editor = sharedPred.edit();
		editor.putString(Config.LastName, name);
		editor.commit();
	}
	
	public static void saveCountryCode(Activity activity, String code){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		SharedPreferences.Editor editor = sharedPred.edit();
		editor.putString(Config.CountryCode, code);
		editor.commit();
	}
	
	public static void saveUserPhoneNumer(Activity activity, String number){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		SharedPreferences.Editor editor = sharedPred.edit();
		editor.putString(Config.PhoneNumber, number);
		editor.commit();
	}
	public static void saveDeviceToken(Activity activity, String token){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		SharedPreferences.Editor editor = sharedPred.edit();
		editor.putString(Config.DeviceToken, token);
		editor.commit();
	}
	public static void setLogoutStatus(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		SharedPreferences.Editor editor = sharedPred.edit();
		editor.putBoolean(Config.LoginStatus, false);
		editor.commit();
	}
	
	public static void saveUserProfilePicture(Activity activity, Bitmap image){
	    File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
	            + "/Android/data/"
	            + activity.getPackageName()
	            + "/Files"); 

	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            return;
	        }
	    } 
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());

	    String mImageName="MI_"+ timeStamp +".jpg";
	    String filePath = mediaStorageDir.getPath() + File.separator + mImageName;
	    File pictureFile = new File(filePath);  
	    
	    String TAG = "Image save";
	    try {
	        FileOutputStream fos = new FileOutputStream(pictureFile);
	        image.compress(Bitmap.CompressFormat.PNG, 90, fos);
	        fos.close();
			SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
			SharedPreferences.Editor editor = sharedPred.edit();
			editor.putString(Config.UserPhotoName, filePath);
			editor.commit();
	    } catch (FileNotFoundException e) {
	        Log.d(TAG, "File not found: " + e.getMessage());
	    } catch (IOException e) {
	        Log.d(TAG, "Error accessing file: " + e.getMessage());
	    } 
	}


	public static String getUserEmail(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		String email = sharedPred.getString(Config.UserEmail, "");
		return email;
	}
	public static String getUserPassword(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		String password = sharedPred.getString(Config.UserPassword, "");
		return password;
	}
	
	public static String getUserName(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		String email = sharedPred.getString(Config.UserName, "");
		return email;
	}
	
	public static String getFirstName(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		String email = sharedPred.getString(Config.FirstName, "");
		return email;
	}
	
	public static String getLastName(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		String email = sharedPred.getString(Config.LastName, "");
		return email;
	}
	
	public static String getCountryCode(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		String email = sharedPred.getString(Config.CountryCode, "");
		return email;
	}
	
	public static String getUserPhoneNumber(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		String phoneNumber = sharedPred.getString(Config.PhoneNumber, "");
		return phoneNumber;
	}
	public static String getDeviceToken(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		String email = sharedPred.getString(Config.DeviceToken, "");
		return email;
	}
	public static boolean getLoginStatus(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		boolean loginStatus = sharedPred.getBoolean(Config.LoginStatus, false);
		return loginStatus;
	}
	
	public static Bitmap getUserProfilePicture(Activity activity){
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(activity);
		String filePath = sharedPred.getString(Config.UserPhotoName, "");
		if(filePath == null || filePath.equals("")) return null;
		File imgFile = new  File(filePath);
		if(imgFile.exists()){
		    return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		}else{
			return null;
		}
	}
	public static boolean setTraxitData(TraxitDataManager data){
		traxitDataManager = data;
		return initializeFriendData();
	}
	public static boolean setContacts(List<ContactInfo> contact){
		contacts = contact;
		return initializeFriendData();
	}
	
	private static boolean initializeFriendData(){
		if(contacts == null || traxitDataManager == null) return false;
		TraxitDataManager data = AppSetting.traxitDataManager;
	    for (int i = 0; i < data.getGroups().size(); i++) {
	    	GroupInfo groupInfo = data.getGroups().get(i);
	    	for(int j = 0 ; j <groupInfo.getFriends().size(); j++){
	    		PersonInfo person = groupInfo.getFriends().get(j);
	    		updatePersonInfo(person);
	    	}
	    }

    	for(int i = 0 ; i< data.getUnGroup().size(); i++){
    		PersonInfo person = data.getUnGroup().get(i);
    		updatePersonInfo(person);
    	}
    	
	    return true;
	}
	public static boolean  updatePersonInfo(PersonInfo person){
		boolean result = false;
		if(contacts == null) return false;
		for(int i = 0; i < contacts.size(); i++){
			ContactInfo contact = contacts.get(i);
			if(isSameEmail(contact.getEmail(), person.getEmail())){
				String name = contact.getName();
				if(!name.equals("") && name != null){
					person.setName(name);
				}
				person.setPhotoUri(contact.getPhotoUri());
				person.setPhoneNumber(contact.getPhoneNumer());
				result = true;
				break;
			}
		}
		return result;
	}
	
	private static boolean isSameEmail(List<String>emails, String email){
		boolean result = false;
		for(int i = 0; i <  emails.size(); i++){
			String e = emails.get(i);
			if(e.equals(email)){
				result = true;
				break;
			}
			
		}
		return result;
	}
	
/*	public static PersonIndexInfo getPersonInfo(String email){
		if(traxitDataManager == null) return null;
	    for (int i = 0; i < traxitDataManager.getGroups().size(); i++) {
	    	GroupInfo groupInfo = traxitDataManager.getGroups().get(i);
	    	for(int j = 0 ; j <groupInfo.getFriends().size(); j++){
	    		PersonInfo person = groupInfo.getFriends().get(j);
	    		if(person.getEmail().equals(email)){
	    			return new PersonIndexInfo(i,j,false);
	    		}
	    	}
	    }

    	for(int i = 0 ; i< traxitDataManager.getUnGroup().size(); i++){
    		PersonInfo person = traxitDataManager.getUnGroup().get(i);
    		if(person.getEmail().equals(email)){
    			return new PersonIndexInfo(i,0, true);
    		}
    	}
    	
    	return null;
	}
	*/
	public static PersonInfo getPersonFromEmail(String email){
		if(traxitDataManager == null) return null;
	    for (int i = 0; i < traxitDataManager.getGroups().size(); i++) {
	    	GroupInfo groupInfo = traxitDataManager.getGroups().get(i);
	    	for(int j = 0 ; j <groupInfo.getFriends().size(); j++){
	    		PersonInfo person = groupInfo.getFriends().get(j);
	    		if(person.getEmail().equals(email)){
	    			return person;
	    		}
	    	}
	    }

    	for(int i = 0 ; i< traxitDataManager.getUnGroup().size(); i++){
    		PersonInfo person = traxitDataManager.getUnGroup().get(i);
    		if(person.getEmail().equals(email)){
    			return person;
    		}
    	}
    	
    	return null;
	}
	
	public static List<PersonInfo>getFriends(){
		List<PersonInfo> friends = new ArrayList<PersonInfo>();
		if(traxitDataManager == null) return friends;
		
	    for (int i = 0; i < traxitDataManager.getGroups().size(); i++) {
	    	List<PersonInfo> members = traxitDataManager.getGroups().get(i).getFriends();
	    	if(members != null)friends.addAll(members);
	    }
	    friends.addAll(traxitDataManager.getUnGroup());
		return friends;
	}
	
	public static boolean removeFriend(String email){
		if(traxitDataManager == null) return false;
	    for (int i = 0; i < traxitDataManager.getGroups().size(); i++) {
	    	GroupInfo groupInfo = traxitDataManager.getGroups().get(i);
	    	for(int j = 0 ; j <groupInfo.getFriends().size(); j++){
	    		PersonInfo person = groupInfo.getFriends().get(j);
	    		if(person.getEmail().equals(email)){
	    			groupInfo.getFriends().remove(j);
	    			return true;
	    		}
	    	}
	    }

    	for(int i = 0 ; i< traxitDataManager.getUnGroup().size(); i++){
    		PersonInfo person = traxitDataManager.getUnGroup().get(i);
    		if(person.getEmail().equals(email)){
    			traxitDataManager.getUnGroup().remove(i);
    			return true;
    		}
    	}
    	
    	return false;
		
	}
}
