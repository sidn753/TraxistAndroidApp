package com.traxit.widget;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.traxit.common.AppSetting;
import com.traxit.common.Config;
import com.traxit.library.NotificationService;

public class PersonInfo {
	private Context mContext;
	private double mGroupId;
	private String mFullName;
	private String mEmail;
	private String mMobileNumber;
	private Bitmap mPhoto;
	private String mPhotoURL;
	private String mPhotoUri;
	private String mActiveTime;
	private LatLng mPosition;
	private String mAddress;
	private String mDistance;
	private int mMessageCount;
	
	public PersonInfo(Context context, String email, String name){
		this.mContext = context;
		this.mFullName = name;
		this.mEmail = email;
	}
	
	public void setEmail(String email){
		this.mEmail = email;
	}
	public void setName(String name){
		this.mFullName = name;
	}
	public void setGroupId(int id){
		this.mGroupId = id;
	}
	public void setPhoneNumber(String number){
		this.mMobileNumber = number;
	}
	public void setProfilePhoto(Bitmap image){
		this.mPhoto = image;
	}
	public void setPhotoURL(String url){
		this.mPhotoURL = url;
	}
	public void setPhotoUri(String uri){
		this.mPhotoUri = uri;
	}
	public void setActiveTime(String time){
		this.mActiveTime = time;
	}
	public void setPosition(LatLng position){
		this.mPosition = position;
		LatLng currentPosition = AppSetting.currentUserLocation;
		getUserAddress(position);
		if(currentPosition == null) {
			mDistance = "";
			return;
		}
		Location locationA = new Location("A");
		locationA.setLatitude( currentPosition.latitude);
		locationA.setLongitude(currentPosition.longitude);
		Location locationB = new Location("B");
		locationB.setLatitude(position.latitude);
		locationB.setLongitude(position.longitude);
		double distance = locationA.distanceTo(locationB);
	    if (distance > 1000) {
	    	mDistance  = String.format("%.2fKm", distance/1000);
	    }else{
	        mDistance  = String.format("%.0fm", distance);
	    }
	    
	    getUserAddress(position);
	}
	public void setAddress(String address){
		this.mAddress = address;
	}
	public void setDistance(String distance){
		this.mDistance = distance;
	}
	public void setMessageCount(int count){
		this.mMessageCount = count;
	}
	
	
	
	public String getEmail(){
		return this.mEmail;
	}
	public String getName(){
		return this.mFullName;
	}
	public double getGroupId(){
		return this.mGroupId;
	}
	public String getPhoneNumer(){
		return this.mMobileNumber;
	}
	public String getPhotoURL(){
		return this.mPhotoURL;
	}
	public String getPhotoUri(){
		return this.mPhotoUri;
	}
	public Bitmap getPhoto(){
		return this.mPhoto;
	}
	public String getActiveTime(){
		return this.mActiveTime;
	}
	public LatLng getPosition(){
		return this.mPosition;
	}
	public String getAddress(){
		return this.mAddress;
	}
	public String getDistance(){
		return this.mDistance;
	}
	public int getMessageCount(){
		return this.mMessageCount;
	}
	
	private void getUserAddress(final LatLng location)
	{
		//indicator.setVisibility(View.VISIBLE);
	    AsyncTask<Void, Void, String> mUpdateTask = new AsyncTask<Void, Void, String>() {

				@Override
				protected String doInBackground(Void... params) {

					return getStreetName(location);
				}

				@Override
				protected void onPostExecute(String address) {

					if(address !=null && !address.equals("")){
						mAddress = address;
						NotificationService.getInstance().postNotification(Config.Notification_Update_Address, PersonInfo.this);
					}
					super.onPostExecute(address);
				}

			};
			
			// execute AsyncTask
			mUpdateTask.execute(null, null, null); 

	}
	private String getStreetName( LatLng location ){
		String street ="";
		if(Geocoder.isPresent()){
	        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
	        List<Address> addresses = null;
			try {
		         addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				street = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
			}
		}
		return street;
	}
}
