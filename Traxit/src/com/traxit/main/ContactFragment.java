package com.traxit.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.traxit.common.AppSetting;
import com.traxit.library.TextBitmap;
import com.traxit.library.UserFunctions;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.ResultCode;




public class ContactFragment extends Fragment implements LocationListener, OnMarkerClickListener {
	
	private GoogleMap mMap;
	private int mapType = GoogleMap.MAP_TYPE_NORMAL;
	Context mContext;
	Activity mActivity;
	LinearLayout mMainView;
	FrameLayout mMapView;
	LinearLayout mContactView;
	LocationManager mLocationManager;
	ScreenStatus mScreenStatus;
	Button mShowContactViewBtn;
	CameraUpdate mCameraUpdate;
	
	Marker mUserMarker;
	Location mCurrentLocation;
	String mUserAddress;
	List<Marker> mFriendMarkers;
	
	FriendListFragment mFriendListFragment ;
	
	public enum ScreenStatus {expand, collapse}
	
	public ContactFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        mActivity = this.getActivity();
        mContext = mActivity.getApplicationContext();
        addContactListView(rootView);  //    Add Bottom View
        
        Button mTitleBarRightButton = (Button)mActivity.findViewById(R.id.titlebarRightButton);
        mTitleBarRightButton.setVisibility(View.INVISIBLE);
        
        setUpMapIfNeeded();
        return rootView;
    }
	
	
	private void setUpMapIfNeeded() {
	    mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    if (mMap == null) {
	        return;
	    }
	   mMap.setMapType(mapType);
	   mMap.setMyLocationEnabled(true);
	   mMap.getUiSettings().setCompassEnabled(true);
	   mMap.getUiSettings().setZoomControlsEnabled(true);
	   mMap.getUiSettings().setAllGesturesEnabled(true);
	   mMap.setOnMarkerClickListener(this);
	   getCurrentLocation();
	   mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
    	   
           // Use default InfoWindow frame
           @Override
           public View getInfoWindow(Marker arg0) {
               return null;
           }

           // Defines the contents of the InfoWindow
           @Override
           public View getInfoContents(Marker marker) {

               // Getting view from the layout file info_window_layout
               View v = mActivity.getLayoutInflater().inflate(R.layout.info_window, null);

               // Getting the position from the marker
               LatLng latLng = marker.getPosition();
               
               
               // Getting reference to the TextView to set latitude
               TextView lblName = (TextView) v.findViewById(R.id.lblInfoLayoutUserName);

               // Getting reference to the TextView to set longitude
               TextView lblAddress = (TextView) v.findViewById(R.id.lblInfoLayoutAddress);
               //ProgressBar indicator = (ProgressBar)v.findViewById(R.id.progressBar1);
               
               String email = marker.getTitle();
               PersonInfo person = AppSetting.getPersonFromEmail(email);
               
               if(person == null){
            	   lblName.setText(email);
            	   if(email.equals("Me")){
            		   if(mUserAddress == null){
            			   lblAddress.setText("Loading address...");
            			   displayUserAddress(latLng, marker);
            		   }else{
            			   lblAddress.setText(mUserAddress);  
            		   }
            	   }
               }else{
            	   lblName.setText(person.getName());
            	   if(person.getAddress() !=null && !person.getAddress().equals("")){
            		   lblAddress.setText(person.getAddress());
            	   }else{
            		   lblAddress.setText("Loading address...");
            		   //displayAddress(latLng,lblAddress, indicator);
            		   displayAddress(person, marker);
            	   }
               }
               //String name = getNameFromEmail(marker.getTitle());
               // Setting the latitude
    
              // displayAddress(latLng,lblAddress, indicator);

               // Returning the view containing InfoWindow contents
               return v;

           }
       });
	}
	private void displayUserAddress(final LatLng location, final Marker marker)
	{
		//indicator.setVisibility(View.VISIBLE);
	    AsyncTask<Void, Void, String> mUpdateTask = new AsyncTask<Void, Void, String>() {

				@Override
				protected String doInBackground(Void... params) {

					return getStreetName(location);
				}

				@Override
				protected void onPostExecute(String address) {
					/*indicator.setVisibility(View.GONE);
					if(address !=null && !address.equals("")){
						textView.setText(address);
					}else{
						textView.setText("fail to get address");
					}*/
					mUserAddress = address;
					marker.showInfoWindow();
					
					super.onPostExecute(address);
				}

			};
			
			// execute AsyncTask
			mUpdateTask.execute(null, null, null); 

	}
	private void displayAddress(final PersonInfo person, final Marker marker)
	{
		final LatLng location = person.getPosition();
		//indicator.setVisibility(View.VISIBLE);
	    AsyncTask<Void, Void, String> mUpdateTask = new AsyncTask<Void, Void, String>() {

				@Override
				protected String doInBackground(Void... params) {

					return getStreetName(location);
				}

				@Override
				protected void onPostExecute(String address) {
					/*indicator.setVisibility(View.GONE);
					if(address !=null && !address.equals("")){
						textView.setText(address);
					}else{
						textView.setText("fail to get address");
					}*/
					person.setAddress(address);
					marker.showInfoWindow();
					
					super.onPostExecute(address);
				}

			};
			
			// execute AsyncTask
			mUpdateTask.execute(null, null, null); 

	}
	public void moveCamera(LatLng center){
		mCameraUpdate = CameraUpdateFactory.newLatLngZoom(center, 15);
 	   	mMap.animateCamera(mCameraUpdate); 
	}
	public void showMe(){
		
		if(mCurrentLocation == null) return;
		LatLng currentPostion = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
		
		View marker = ( (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
		ImageView photoView = (ImageView) marker.findViewById(R.id.friendPhotoView);
		
		if(AppSetting.getUserProfilePicture(mActivity) != null){
			photoView.setImageBitmap(AppSetting.getUserProfilePicture(mActivity));
		}else{
			TextBitmap textBitmap = new TextBitmap();
			Bitmap bitmap = textBitmap.createBitmapWithText(AppSetting.getUserName(mActivity));
			if(bitmap != null){
				photoView.setImageBitmap(bitmap);
			}
			
		}
		
		BitmapDescriptor icon=BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mActivity, marker));
		MarkerOptions option = new MarkerOptions().position(currentPostion)
				.title("Me")
	    	    .icon(icon);
		mUserMarker = mMap.addMarker(option);

		
		/*this.getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 mUserMarker.setTitle(getStreetName(mCurrentLocation));
		    }
		});*/
	}
	
	public void setUpMarker(int groupIndex) {
		removeMarkers();
		List<PersonInfo> friends ;
		if(groupIndex == 0){
			friends = AppSetting.traxitDataManager.getUnGroup();
		}else{
			 friends = AppSetting.traxitDataManager.getGroups().get(groupIndex-1).getFriends();

		}
		mFriendMarkers = new ArrayList<Marker>();
		  for(int i=0;i<friends.size();i++){
			  PersonInfo item = friends.get(i);
				LatLng currentPostion =item.getPosition();
				
				View marker = ( (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
				ImageView photoView = (ImageView) marker.findViewById(R.id.friendPhotoView);
				
				if(item.getPhoto() != null){
					photoView.setImageBitmap(item.getPhoto());
				}else{
					TextBitmap textBitmap = new TextBitmap();
					Bitmap bitmap = textBitmap.createBitmapWithText(item.getName());
					if(bitmap != null){
						photoView.setImageBitmap(bitmap);
					}
					
				}
				
				BitmapDescriptor icon=BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mActivity, marker));
				MarkerOptions option = new MarkerOptions().position(currentPostion)
			    	    .title(item.getEmail())
			    	    .icon(icon);
				
				Marker friendMarker = mMap.addMarker(option);
			    mFriendMarkers.add(friendMarker);
			}


	}
	
	public void reloadFriendListView(){
		mFriendListFragment.reloadFriendListView();
	}
	
	public void refreshFriendListView(){
		mFriendListFragment.refreshFriendListView();
	}
	
	public void removeGroupMarker(int groupIndex) {
	}
	//--------------------------------------------------------------
	private void removeMarkers(){
		if(mFriendMarkers == null) return;
		for(int i = 0; i < mFriendMarkers.size() ; i++){
			mFriendMarkers.get(i).remove();
		}
	}
	
	public void updateFriendPosition(final String email, final LatLng position){
		if(this.getActivity() == null) return;
		this.getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		 		for(int i =0 ; i <  mFriendMarkers.size(); i++){
					Marker marker = mFriendMarkers.get(i);
					if(marker.getTitle().equals(email)){
						marker.setPosition(position);
						break;
					}
				}
		    }
		});
		

	}
	
	public void updateFriendListTable(){
		mFriendListFragment.refreshFriendListView();
	}
	@Override
	public void onDestroyView(){
			Fragment f = (Fragment) getFragmentManager().findFragmentById(R.id.map);
			if(f!=null && f.isResumed()){
				getFragmentManager().beginTransaction().remove(f).commit();
			}
		super.onDestroyView();
	}
	
/*	
 * @Override
	public void onPause(){
		super.onPause();
		if(mMap != null){
			mMap = null;
		}
	}
*/
	//------Add Contacts View------------
	private void addContactListView(View v){
		
        mMainView = (LinearLayout)v.findViewById(R.id.mainView);
        mMapView = (FrameLayout)v.findViewById(R.id.mapView);
        mContactView = (LinearLayout)v.findViewById(R.id.contactView);
        
        mShowContactViewBtn = (Button)v.findViewById(R.id.mapBtn);
        mShowContactViewBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(mScreenStatus == ScreenStatus.collapse){
					setDefaultViewSize();
				}else{
					setMapFullScreen();
				}
				
			}
		});
        //mShowContactViewBtn.setVisibility(View.GONE);
       // setDefaultViewSize();
        
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragTransaction =  fragmentManager.beginTransaction();
		mFriendListFragment = new FriendListFragment();
		fragTransaction.add(mContactView.getId(), mFriendListFragment, "Contacts");
		fragTransaction.commit();
		mScreenStatus = ScreenStatus.expand;
	}
	
	private void setDefaultViewSize(){

	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	    		LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mMapView.getLayoutParams();
	    		lp.weight = 0.5f;
	    		mMapView.setLayoutParams(lp);
	            mMapView.requestLayout();
	            mScreenStatus = ScreenStatus.expand;
	            //mShowContactViewBtn.setVisibility(View.GONE);
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration(1000);
	    mMapView.startAnimation(a);
		
	}
	
	private void setMapFullScreen(){
		
	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	    		LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mMapView.getLayoutParams();
	    		lp.weight = 1.0f;
	    		mMapView.setLayoutParams(lp);
	            mMapView.requestLayout();
	            mScreenStatus = ScreenStatus.collapse;
	            mShowContactViewBtn.setVisibility(View.VISIBLE);
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration(1000);
	    mMapView.startAnimation(a);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		
		mCurrentLocation  = location;
		LatLng currentPostion = new LatLng(location.getLatitude(), location.getLongitude());
		sendUpdatedLocationInfoToServer(currentPostion);
		AppSetting.currentUserLocation = currentPostion;
	    
		if(mUserMarker!=null){
	    	mUserMarker.setPosition(currentPostion);
	    }
	    mUserAddress = null;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	
	/*=======================================================================
	 * 
	 *       MapView
	 *======================================================================*/
	 	private void getCurrentLocation(){
	 		
	    // Getting LocationManager object from System Service LOCATION_SERVICE
		mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
	    boolean enabledGPS = mLocationManager
	            .isProviderEnabled(LocationManager.GPS_PROVIDER);
	    boolean enabledWiFi = mLocationManager
	            .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	    // Check if enabled and if not send user to the GPS settings
	    if (!enabledGPS && !enabledWiFi) {
	        Toast.makeText(mContext, "GPS signal not found",
	                Toast.LENGTH_LONG).show();
	        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	        startActivity(intent);
	    }
	    // Creating a criteria object to retrieve provider
	    Criteria criteria = new Criteria();

	    // Getting the name of the best provider
	    String provider = mLocationManager.getBestProvider(criteria, true);

	    // Getting Current Location From GPS
	    Location oLc = mLocationManager.getLastKnownLocation(provider);

	    if (oLc != null) {
	        onLocationChanged(oLc);
	        moveCamera(new LatLng(oLc.getLatitude(), oLc.getLongitude()));
	    }
	    mLocationManager.requestLocationUpdates(provider, 10000, 1 , (LocationListener) this);

	}//--- END Method

	 	
		// Convert a view to bitmap
		private Bitmap createDrawableFromView(Activity activity, View view) {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
			view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
			view.buildDrawingCache();
			Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
	 
			Canvas canvas = new Canvas(bitmap);
			view.draw(canvas);
	 
			return bitmap;
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
		private void sendUpdatedLocationInfoToServer(LatLng currentPosition)
		{
			final String email = AppSetting.getUserEmail(mActivity);
		    final double longitude = currentPosition.longitude;
		    final double latitude = currentPosition.latitude;
		    AsyncTask<Void, Void, ResultCode> mUpdateTask = new AsyncTask<Void, Void, ResultCode>() {

					@Override
					protected ResultCode doInBackground(Void... params) {
						UserFunctions userFunction = new UserFunctions();
						JSONObject json = userFunction.updateLocation(email, latitude, longitude);
							
						// check for login response
						try {
							if(json!=null){
								ResultCode result = new ResultCode(json.getString("result"), json.getString("msg"));
								return result; 
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return new ResultCode();
					}

					@Override
					protected void onPostExecute(ResultCode result) {
						int res = result.getReturnCode();
						if(res != 0) {
							//Toast.makeText(getApplicationContext(), "The User Id and Email are not match!", Toast.LENGTH_LONG).show();
							Log.i("Location update", result.getReturnMessage());
						}
						super.onPostExecute(result);
					}

				};
				
				// execute AsyncTask
				mUpdateTask.execute(null, null, null); 

		}
		


}
