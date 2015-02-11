package com.traxit.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.traxit.common.AppSetting;
import com.traxit.common.Config;
import com.traxit.library.UserFunctions;
import com.traxit.widget.ResultCode;

public class ProfileFragment extends Fragment {
	static Context mContext;
	Activity mActivity;
	ProgressDialog mProgressDialog;
	Button mTitleBarRightButton;
	Button mTitleBarLeftButton;
	Button mTitleCancelButton;
	
	EditText mFirstNameEditText;
	EditText mLastNameEditText;
	EditText mPhoneNumberEditText;
	static TextView mCallingCodeLabel;
	static ImageView mFlagButton;
	ImageView mUserProfileImageView;
	
	boolean mEditable;
	String eidtButtonTitle = "Edit";
	String saveButtonTitle = "Save";
	String cancelButtonTitle = "Cancel";
	
	private final int PICK_FROM_CAMERA = 1001;
	private final int PICK_FROM_FILE = 1002;
	private String mPhotoOptionChosen;
	
	//----------Original profile data--------
	private String mOriginalFirstName;
	private String mOriginalLastName;
	private String mOriginalCallingCode;
	private String mOriginalPhoneNumber;
	private TextView mTitleBarTitleLaybel;
	
	public ProfileFragment(){
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mActivity = this.getActivity();
        mContext = this.getActivity().getApplicationContext();
        
        /**
         *  Title changed when you clicked country code activity.
         */
        
        mTitleBarTitleLaybel = (TextView)mActivity.findViewById(R.id.titlebarTitleLabel);
        String[] titleArray = mActivity.getResources().getStringArray(R.array.nav_drawer_items);
        mTitleBarTitleLaybel.setText(titleArray[2]);
        
    
      
    	mTitleBarRightButton = (Button)mActivity.findViewById(R.id.titlebarRightButton);
		mTitleBarRightButton.setText(eidtButtonTitle);
		mTitleBarRightButton.setVisibility(View.VISIBLE);
		
		mTitleBarRightButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				editProfile();
			}
		});
		
		mTitleCancelButton = (Button)mActivity.findViewById(R.id.titlebarLeftCancelButton);
		mTitleCancelButton.setText("Cancel");
		mTitleCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				changeEditProfilePage(false);
				setOriginalValue();
			}
		});
		
		mTitleBarLeftButton = (Button)mActivity.findViewById(R.id.titlebarLeftButton);
		
		mEditable = false;
		mFirstNameEditText = (EditText)rootView.findViewById(R.id.firstNameEditText);
		mLastNameEditText =  (EditText)rootView.findViewById(R.id.lastNameEditText);
		mPhoneNumberEditText = (EditText)rootView.findViewById(R.id.phoneNumberEditText);
		mUserProfileImageView = (ImageView)rootView.findViewById(R.id.userProfileImageView);
		mUserProfileImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
						
				selectImageFromCameraOrGallery();
			}
		});
		
		mFlagButton = (ImageView)rootView.findViewById(R.id.flagButton);
		
		mFlagButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/**
				 * changed by jiang.
				 */
				if(!mEditable)return;
				
				selectCallingCode();
			}
		});
		mCallingCodeLabel = (TextView)rootView.findViewById(R.id.callingCodeLabel);
		
		if(AppSetting.mProfileSaveableFlag){
			mEditable = AppSetting.mProfileSaveableFlag;
			
			Log.d("AppSetting.mProfileSaveableFlag", String.valueOf(AppSetting.m_countrycode));
			Log.d("AppSetting dial code", String.valueOf(AppSetting.m_dialcode));
			
			mFlagButton.setImageBitmap(getBitmapFromAsset(AppSetting.m_countrycode));
			mCallingCodeLabel.setText(AppSetting.m_dialcode);
			setProfileEditable(mEditable);
			
			editProfile();
			
	    }else{
	    	setProfileEditable(mEditable);
			loadProfileData();	
	    }
		
//		TextView menuUserEmailLabel = (TextView) rootView.findViewById(R.id.userEmailLabel);
//		menuUserEmailLabel.setText(AppSetting.getUserEmail(mActivity));	
		
        return rootView;
    }
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{ 
	    super.onActivityResult(requestCode, resultCode, data);
	    System.out.println(" ****** R code"+requestCode+" res code"+resultCode+"data"+data);
	    
	    Bitmap bm = null;
	    System.gc();
	    
	    switch (requestCode) {
		case PICK_FROM_CAMERA:
			if(resultCode == Activity.RESULT_OK)
		    {
				File f = new File(Environment.getExternalStorageDirectory().toString());
				for(File temp : f.listFiles()){
					if(temp.getName().equals("temp.jpg")){
						f = temp;
						break;
					}
				}
                
				//bm = BitmapFactory.decodeFile(f.getAbsolutePath());
				//mUserProfileImageView.setImageBitmap(bm);
				try{
					bm = decodeFile(f, Config.UPLOAD_POST_IMAGE_WIDTH, Config.UPLOAD_POST_IMAGE_HEIGHT);
					if(bm != null){
						mUserProfileImageView.setImageBitmap(bm);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
		    }
			break;
		case PICK_FROM_FILE:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = mActivity.getContentResolver().query(
						selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String photoPath = cursor.getString(columnIndex);
				
				File imgFile = new  File(photoPath);
				bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				mUserProfileImageView.setImageBitmap(bm);
				//bm = mImageLoader.decodeFile(new File(photoPath), Config.UPLOAD_POST_IMAGE_WIDTH, Config.UPLOAD_POST_IMAGE_HEIGHT);
				if(bm != null){
					//uploadImage(Utils.rotateBitmap(bm, photoPath),60);
				}
				cursor.close();				
			}
			break;
			
	 	default:
			break;
	    }	    
	}
	private void editProfile(){
		
		if(mTitleBarRightButton.getText().toString().endsWith(eidtButtonTitle)){
			changeEditProfilePage(true);
		}else{
			updateProfile();
			changeEditProfilePage(false);
		}
	}
	

	private void selectCallingCode(){
		Intent countryCodeIntent = new Intent(mContext, CountryCodeActivity.class);
		countryCodeIntent.putExtra("profile" , true);
		startActivity(countryCodeIntent);
	}
	private void loadProfileData(){
		if(!loadProfileFromLocal()){
			loadProfileFromServer();
		}
	}
	private boolean loadProfileFromLocal(){
		String lastName = AppSetting.getLastName(mActivity);
		String firstName = AppSetting.getFirstName(mActivity);
		String phoneNumber = AppSetting.getUserPhoneNumber(mActivity);
		String callingCode = AppSetting.getCountryCode(mActivity);
		
		if(lastName.equals("")) return false;
		if(firstName.equals("")) return false;
		if(phoneNumber.equals("")) return false;
		if(callingCode.equals("")) return false;
		mOriginalFirstName = lastName;
		mOriginalLastName = firstName;
		mOriginalCallingCode = callingCode;
		mOriginalPhoneNumber = phoneNumber;
		
		mFirstNameEditText.setText(lastName);
		mLastNameEditText.setText(firstName);
		mPhoneNumberEditText.setText(phoneNumber);
		mCallingCodeLabel.setText(callingCode);
		
		return true;
	}
	private void loadProfileFromServer(){
	    //------initialize userEmail-------a
		final String email = AppSetting.getUserEmail(mActivity);
		mProgressDialog = ProgressDialog.show(mActivity, "", "Loading profile...", true);

		 AsyncTask<Void, Void, JSONObject> mInitTraxiTask = new AsyncTask<Void, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.loadProfile(email);
				Log.d("Load Profile Data :", String.valueOf(json));
				if(json!=null){
					return json; 
				}
				return null;
			}

			@Override
			protected void onPostExecute(JSONObject result) {
				mProgressDialog.dismiss();
				int resultCode;
				try {
					resultCode = result.getInt("result");
			        
					if(resultCode == 0) {
						JSONObject json = result.getJSONObject("msg");
						diplayProfileData(json);
					}else{
						String errMsg = result.getString("msg");
						Log.e("message error", errMsg);
						//CommonFunctions.showWarningDialog(mContext, errMsg);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//mInitTraxiTask = null;
				super.onPostExecute(result);
			}

		};
		
		// execute AsyncTask
		mInitTraxiTask.execute(null, null, null);	
	}
	
	private void diplayProfileData(JSONObject json) throws JSONException{
        /*   
         $item['isMine'] ;
         $item['message'];
         $item['sentTime'];
         $item['status'];*/
		
		if(json !=null){
			String lastName = json.getString("lastName");
			String firstName = json.getString("firstName");
			String phoneNumber = json.getString("phoneNumber");
			String callingCode = json.getString("countryCode");
			
			mOriginalFirstName = lastName;
			mOriginalLastName = firstName;
			mOriginalCallingCode = callingCode;
			mOriginalPhoneNumber = phoneNumber;
			
			mFirstNameEditText.setText(firstName);
			mLastNameEditText.setText(lastName);
			mPhoneNumberEditText.setText(phoneNumber);
			mCallingCodeLabel.setText(callingCode);
		}

	 }
	
	private void updateProfile(){
		/* 
		 *   NSDictionary * parameters = @{@"tag":@"updateProfile",
        @"email":email,
        @"firstName":firstName,
        @"lastName":lastName,
        @"countryCode":userCountryCallingCode,
        @"phoneNumber":phoneNumber};*/
		
		if(!isChanged()) return;
		
		mProgressDialog = ProgressDialog.show(mActivity, "",
				"Login...", true);
		final String email = AppSetting.getUserEmail(mActivity);
		final String firstName = mFirstNameEditText.getText().toString();
		final String lastName = mLastNameEditText.getText().toString();
		final String countryCode = (String) mCallingCodeLabel.getText();
		final String phoneNumber = mPhoneNumberEditText.getText().toString();
		
		AsyncTask<Void, Void, ResultCode> mTask = new AsyncTask<Void, Void, ResultCode>() {

			@Override
			protected ResultCode doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.updateProfile(email, firstName, lastName, countryCode, phoneNumber);
					
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
				if(res == 0){ 
					mOriginalFirstName = lastName;
					mOriginalLastName = firstName;
					mOriginalCallingCode = countryCode;
					mOriginalPhoneNumber = phoneNumber;
				}else{
					setOriginalValue();
					Toast.makeText(mContext, "Fail to update profile", Toast.LENGTH_LONG).show();
				}
				super.onPostExecute(result);
			}

		};
		
		// execute AsyncTask
		mTask.execute(null, null, null);
	}
	
	private void changeEditProfilePage(boolean editable){
		mEditable = editable;
		if(editable){
			mTitleBarRightButton.setText(saveButtonTitle);
			mTitleBarLeftButton.setVisibility(View.GONE);
			mTitleCancelButton.setVisibility(View.VISIBLE);
			
			AppSetting.mProfileSaveableFlag = true;
		}else{
			mTitleBarRightButton.setText(eidtButtonTitle);
			mTitleBarLeftButton.setVisibility(View.VISIBLE);
			mTitleCancelButton.setVisibility(View.GONE);
			
			AppSetting.mProfileSaveableFlag = false;
		}
		setProfileEditable(editable);
	}
	private void setProfileEditable(boolean editable){
		mFirstNameEditText.setEnabled(editable);
		mLastNameEditText.setEnabled(editable);
		mPhoneNumberEditText.setEnabled(editable);
	}
	
	private boolean isChanged(){
		String firstName = mFirstNameEditText.getText().toString();
		if(!firstName.equals(mOriginalFirstName)) return true;
		String lastName = mLastNameEditText.getText().toString();
		if(!lastName.equals(mOriginalLastName)) return true;
		String countryCode = (String) mCallingCodeLabel.getText();
		if(!countryCode.equals(mOriginalCallingCode)) return true;
		String phoneNumber = mPhoneNumberEditText.getText().toString();
		if(!phoneNumber.equals(mOriginalPhoneNumber)) return true;
		return false;
	}
	
	private void setOriginalValue(){
		mFirstNameEditText.setText(mOriginalFirstName);
		mLastNameEditText.setText(mOriginalLastName);
		mPhoneNumberEditText.setText(mOriginalPhoneNumber);
		mCallingCodeLabel.setText(mOriginalCallingCode);
	}
	private void selectImageFromCameraOrGallery(){
		if(!mEditable) return;
		AlertDialog.Builder buildSingle = new AlertDialog.Builder(mActivity);
		buildSingle.setTitle(getResources().getString(R.string.select_photo_sheet_title));
		final ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(mActivity,
				android.R.layout.select_dialog_item);
		aAdapter.add(getResources().getString(R.string.take_picture));
		aAdapter.add(getResources().getString(R.string.gallery));

		buildSingle.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

		buildSingle.setAdapter(aAdapter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mPhotoOptionChosen = aAdapter.getItem(which)
								.toString();
						System.gc();
						if (mPhotoOptionChosen.equals(getResources().getString(R.string.take_picture))) {
							
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");							
				            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				            ProfileFragment.this.startActivityForResult(intent, PICK_FROM_CAMERA);
				            
						} else if (mPhotoOptionChosen.equals(getResources().getString(R.string.gallery))) {
							Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							intent.setType("image/*");
//							intent.setAction(Intent.ACTION_GET_CONTENT);
							ProfileFragment.this.startActivityForResult(Intent.createChooser(
									intent, "Select File"),
									PICK_FROM_FILE);// one can be replced
													// with any action code
						}
					}
				});
		buildSingle.show();
	}
	
    public void setCallingCode(String code, String dialCode){
    	Log.d("AppSetting.mProfileSaveableFlag ------------", String.valueOf(code));
		Log.d("AppSetting dial code ------------", String.valueOf(dialCode));
    	
    	AppSetting.m_countrycode = code;
    	AppSetting.m_dialcode = dialCode;
	}
    
	private Bitmap getBitmapFromAsset(String code){
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
    public Bitmap decodeFile(File f, int reqWidth, int reqHeight) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();
 
            // Find the correct scale value. It should be the power of 2.
//            final int REQUIRED_SIZE = 400;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            
            if(reqWidth > 0 && reqHeight >0){
	            while (true) {
	                if (width_tmp / 2 < reqWidth
	                        || height_tmp / 2 < reqHeight)
	                    break;
	                width_tmp /= 2;
	                height_tmp /= 2;
	                scale *= 2;
	            }
            }
            
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inPurgeable = true;
            o2.inInputShareable = true;
            o2.inPreferredConfig = Bitmap.Config.RGB_565;
            o2.inJustDecodeBounds = false;
            
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
