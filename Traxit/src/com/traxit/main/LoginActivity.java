package com.traxit.main;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.traxit.common.AppSetting;
import com.traxit.common.CommonFunctions;
import com.traxit.common.Config;
import com.traxit.library.UserFunctions;
import com.traxit.widget.ResultCode;

@SuppressLint({ "CutPasteId", "CommitPrefEdits" })
public class LoginActivity extends BaseActivity {
	
	EditText mUserEmailEditText;
	EditText mUserPasswordEditText;
	ProgressDialog mProgressDialog;
	Controller mController;
	AsyncTask<Void, Void, ResultCode> mLoginTask ;
	ScrollView mContainer;
	private String mGCMRegId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.getActionBar().hide();
		setContentView(R.layout.activity_login);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		mUserEmailEditText = (EditText)findViewById(R.id.userEmailEditText);

		mUserEmailEditText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mContainer.scrollTo(0, mContainer.getBottom());
				return false;
			}
		});
		mUserPasswordEditText = (EditText)findViewById(R.id.userPasswordEditText);
		mUserEmailEditText.setText(AppSetting.getUserEmail(this));
		mUserPasswordEditText.setText(AppSetting.getUserPassword(this));
		mContainer = (ScrollView)findViewById(R.id.container);
		
		Button btnSignup = (Button) findViewById(R.id.signUpButton);
		btnSignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
			}
		});
		
		Button btnLogin = (Button)findViewById(R.id.loginButton);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				checkLogin();
			}
		});
		
		
		//-------------Google  Cloud Message----------
	      //Get Global Controller Class object (see application tag in AndroidManifest.xml)
		mController = (Controller) getApplicationContext();
	      		
	      		
  		// Check if Internet present
  		if (!mController.isConnectingToInternet()) {
    			// Internet Connection is not present
  			mController.showAlertDialog(LoginActivity.this,
  					"Internet Connection Error",
  					"Please connect to Internet connection", false);
  			// stop executing code by return
  			return;
  		}
  		checkGCMRegId();
  		addKeyboardListener();
	}
	
	private void addKeyboardListener(){
		mContainer.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
	            if ((mContainer.getRootView().getHeight() - mContainer.getHeight()) >  mContainer.getRootView().getHeight()/3) {
	            	mContainer.scrollTo(0, mContainer.getBottom());
				 }
		     }
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.overLayout) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(AppSetting.getLoginStatus(this)){
			Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(mainIntent);
		}
	}
	private void checkLogin() {

		String userEmail = mUserEmailEditText.getText().toString();
		String userPassword = mUserPasswordEditText.getText().toString();
		
		if (mGCMRegId.equals("")) {
			mGCMRegId = getGCMRegId();
			if (mGCMRegId.equals("")) {
	  			Toast.makeText(getApplicationContext(), "Can't get your device token id, please retry!", Toast.LENGTH_LONG).show();
	  			return;
			}
		}
		
		if(userEmail.length() == 0 || userPassword.length() == 0){
		   showWarningDialog("Invalid user id or password!");
		} else{
           login(userEmail, userPassword, mGCMRegId);
		}
		
	}
	
	private void login(final String userEmail, final String userPassword, final String token){
		mProgressDialog = ProgressDialog.show(LoginActivity.this, "",
				"Login...", true);
		 mLoginTask = new AsyncTask<Void, Void, ResultCode>() {

			@Override
			protected ResultCode doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.loginUser(userEmail, userPassword, token);
					
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
				mProgressDialog.dismiss();
				int res = result.getReturnCode();
				if(res == 0) {
					loginCompleted();
				}else{
					showWarningDialog(result.getReturnMessage());
					Toast.makeText(getApplicationContext(), "The User Id and Email are not match!", Toast.LENGTH_LONG).show();
				}
				mLoginTask = null;
			}

		};
		
		// execute AsyncTask
		mLoginTask.execute(null, null, null);
	}
	
	private void loginCompleted(){

		String userEmail = mUserEmailEditText.getText().toString();
		String userPassword = mUserPasswordEditText.getText().toString();
		AppSetting.saveUserLoginInfo(this, userEmail, userPassword);

		//Intent mainIntent = new Intent(getApplicationContext(), MapActivity.class);
		Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
		//dashboard.putExtra("name", userId);
		//Global.userName = userId;
		startActivity(mainIntent);
		
		
	}


    // Create a broadcast receiver to get message and show on screen 
 	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
 		
 		@Override
 		public void onReceive(Context context, Intent intent) {
 			
 			String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);

 			mController.acquireWakeLock(getApplicationContext());
 			Toast.makeText(getApplicationContext(), "Got Message: " + newMessage, Toast.LENGTH_LONG).show();
 			mController.releaseWakeLock();
 		}
 	};
 	
 	private void checkGCMRegId(){
 		
 		mGCMRegId = AppSetting.getDeviceToken(this);
 		
 		if(mGCMRegId == null || mGCMRegId.equals("")){
 	  		// Make sure the device has the proper dependencies.
 	  		GCMRegistrar.checkDevice(this);

 	  		// Make sure the manifest permissions was properly set 
 	  		GCMRegistrar.checkManifest(this);
 	  		
 	  		// Register custom Broadcast receiver to show messages on activity
 	  		registerReceiver(mHandleMessageReceiver, new IntentFilter(
 	  				Config.DISPLAY_MESSAGE_ACTION));
 	  		
 	  		// Get GCM registration id
 	  		mGCMRegId = GCMRegistrar.getRegistrationId(this);

 	  		// Check if regid already presents
 	  		if (mGCMRegId.equals("")) {
 	   			GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);
 	  			if(!CommonFunctions.isUsableGCM(this)) return;
 	  		}else{
 	  			AppSetting.saveDeviceToken(this, mGCMRegId);
 	  		}
 		}

 	}
 	
 	private String getGCMRegId(){
  		// Make sure the device has the proper dependencies.
  		GCMRegistrar.checkDevice(this);

  		// Make sure the manifest permissions was properly set 
  		GCMRegistrar.checkManifest(this);
  		
  		// Register custom Broadcast receiver to show messages on activity
  		registerReceiver(mHandleMessageReceiver, new IntentFilter(
  				Config.DISPLAY_MESSAGE_ACTION));
  		
  		// Get GCM registration id
  		String regId = GCMRegistrar.getRegistrationId(this);
  		return regId;
 	}
 
}
