package com.traxit.main;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.traxit.common.CommonFunctions;
import com.traxit.common.Config;
import com.traxit.library.UserFunctions;
import com.traxit.widget.ResultCode;


public class RegisterActivity extends BaseActivity {
	ProgressDialog mProgressDialog;
	static ImageView mFlagButton;
	static TextView mCallingCodeLabel;
	
	EditText mFirstNameEditText;
	EditText mLastNameEditText;
	EditText mEmailEditText;
	EditText mPhoneNumberEditText;
	EditText mPasswordEditText;
	AsyncTask<Void, Void, ResultCode> mSignupTask ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		mContext = this;
		this.getActionBar().setDisplayUseLogoEnabled(false);
		this.getActionBar().setDisplayShowHomeEnabled(false);
		
		mFirstNameEditText = (EditText)findViewById(R.id.firstNameEditText);
		mLastNameEditText =  (EditText)findViewById(R.id.lastNameEditText);
		mEmailEditText = (EditText) findViewById(R.id.emailEditText);
		mPhoneNumberEditText = (EditText)findViewById(R.id.phoneNumberEditText);
		mPasswordEditText = (EditText)findViewById(R.id.passwordEditText);
		
		
		Button btnSignup = (Button) findViewById(R.id.btnRegister);
		btnSignup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				singup();
			}
		});
	
		mFlagButton = (ImageView)findViewById(R.id.flagButton);
		mFlagButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectCallingCode();
			}
		});
		
		mCallingCodeLabel = (TextView)findViewById(R.id.callingCodeLabel);
		mCallingCodeLabel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectCallingCode();
			}
		});
		
		
	}
	
	private String getGCMDeviceToken(){

		if (!GCMRegistrar.isRegisteredOnServer(this)) {
			GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);
		}
		
		
		
  		// Make sure the device has the proper dependencies.
  		GCMRegistrar.checkDevice(this);

  		// Make sure the manifest permissions was properly set 
  		GCMRegistrar.checkManifest(this);
 
  		// Get GCM registration id
  		final String regId = GCMRegistrar.getRegistrationId(this);

  		// Check if regid already presents
  		if (regId.equals("")) {
    			// Register with GCM			
  			GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);
  			if(!CommonFunctions.isUsableGCM(this)) return "";
  			return "";			
  		} else {
  			return regId;
  		}
	}
	
	private void selectCallingCode(){
		Intent countryCodeIntent = new Intent(getApplicationContext(), CountryCodeActivity.class);
		startActivity(countryCodeIntent);
	}
	
	static void setCallingCode(String code, String dialCode){
		//String flagName = code + ".png";
		//int resID = mContext.getResources().getIdentifier(flagName, "drawable", mContext.getPackageName());
		//mFlagButton.setImageResource(resID);
		mFlagButton.setImageBitmap(getBitmapFromAsset(code));
		mCallingCodeLabel.setText(dialCode);
	}
	private static Bitmap getBitmapFromAsset(String code){
		AssetManager assetManager = mContext.getAssets();
		InputStream is = null;
		try{
			String flagName = "Flags/" +code + ".png";
			is = assetManager.open(flagName);
		}catch (IOException ex){
			ex.printStackTrace();
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		return bitmap;
	}
	
	private void singup(){
		
	    final String phoneNumber = mPhoneNumberEditText.getText().toString();
	    if(phoneNumber.length() == 0) {
	    	showWarningDialog("Please input correct phone number.");
	    	return;
	    }

	    //====Register User ============
	    final String firstName = mFirstNameEditText.getText().toString();
	    if(firstName.length() == 0) {
	    	showWarningDialog("Please input your first name.");
	    	return;
	    }
	    final String lastName =  mLastNameEditText.getText().toString();
	    if(lastName.length() == 0) {
	    	showWarningDialog("Please input your last name.");
	    	return;
	    }
	    
	    final String email = mEmailEditText.getText().toString();
	    
	    if(email.length() == 0) {
	    	showWarningDialog("Please input your email.");
	    	return;
	    }
	    
	    final String password = mPasswordEditText.getText().toString();
	   
	    if(email.length() == 0) {
	    	showWarningDialog("Please input password.");
	    	return;
	    }

	    final String countryCode = (String) mCallingCodeLabel.getText();
	   
	    final String token = getGCMDeviceToken();
	    if(token.equals("")) {
	    	showWarningDialog("Can't get your device Id, please retry!.");
	    	return;
	    }
	    
		mProgressDialog = ProgressDialog.show(mContext, "",	"Sign up...", true);
		mSignupTask = new AsyncTask<Void, Void, ResultCode>() {

			@Override
			protected ResultCode doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.signUp(email, password, firstName, lastName, countryCode, phoneNumber, token);
					
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
					Toast.makeText(getApplicationContext(), "Sucess to register user!", Toast.LENGTH_LONG).show();
					finish();
				}else{
					showWarningDialog(result.getReturnMessage());
					//Toast.makeText(getApplicationContext(), "The User Id and Email are not match!", Toast.LENGTH_LONG).show();
				}
				mSignupTask = null;
			}

		};
		
		// execute AsyncTask
		mSignupTask.execute(null, null, null);
                     
	}
	

}
