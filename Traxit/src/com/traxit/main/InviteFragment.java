package com.traxit.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.traxit.adapter.InviteListAdapter;
import com.traxit.adapter.InviteListAdapter.InviteListHolder;
import com.traxit.adapter.InviteListAdapter.InviteListItem;
import com.traxit.common.AppSetting;
import com.traxit.common.CommonFunctions;
import com.traxit.library.UserFunctions;
import com.traxit.widget.ResultCode;

public class InviteFragment  extends Fragment {

	private Activity mActivity;
	
	private InviteListAdapter mAdapter;
	private ListView mListView;
	public InviteFragment(){}
	private Button mTitleBarRightButton;
	private EditText mSearchBox;
	ImageView mSearchIcon;
	ImageView mSearchCancelIcon;
	private String myPhoneNumber;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_invite, container, false);
        mActivity = this.getActivity();
        
		mSearchIcon = (ImageView) rootView.findViewById(R.id.search_icon);
		mSearchCancelIcon = (ImageView)rootView.findViewById(R.id.search_cancel);
		mSearchBox = (EditText)rootView.findViewById(R.id.search_text);
		
		mTitleBarRightButton = (Button)mActivity.findViewById(R.id.titlebarRightButton);
		mTitleBarRightButton.setText("Send");
		mTitleBarRightButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				sendInvite();
			}
		});
		
        mListView = (ListView) rootView.findViewById(R.id.contactsListView);
        mAdapter = new InviteListAdapter(mActivity.getApplicationContext(), AppSetting.contacts);
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				InviteListHolder holder = (InviteListHolder) view.getTag();
				InviteListItem item = (InviteListItem) mAdapter.getItem(position);
				if(item.isSelected){
					item.isSelected = false;
					holder.imgCheck.setVisibility(View.INVISIBLE);
				}else{
					item.isSelected = true;
					holder.imgCheck.setVisibility(View.VISIBLE);
				}
				showSendButton();
			}
			
		});
		mListView.setAdapter(mAdapter);
		
		Button btnManualInvite = (Button)rootView.findViewById(R.id.btnManualInvite);
		btnManualInvite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				sendManuallInvite();
			}
		});
		initializeSearchBox();
		
		// add PhoneStateListener
		PhoneCallListener phoneListener = new PhoneCallListener();
		TelephonyManager telephonyManager = (TelephonyManager) mActivity
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener,
				PhoneStateListener.LISTEN_CALL_STATE);
		String callCode = telephonyManager.getNetworkCountryIso();
		myPhoneNumber = telephonyManager.getLine1Number();
		
        return rootView;
    }
	
	private boolean isExistSelectedContacts(){
		boolean result = false;
		for(int i = 0; i < AppSetting.contacts.size(); i++ ){
			InviteListItem item = (InviteListItem) mAdapter.getItem(i);
			if(item.isSelected){
				result = true;
				break;
			}
			
		}
		return result;
	}
	
	private void showSendButton(){
		if(isExistSelectedContacts()){
			mTitleBarRightButton.setVisibility(View.VISIBLE);
		}else{
			mTitleBarRightButton.setVisibility(View.INVISIBLE);
		}
		
	}
	
	private void sendInvite() {
	    
		
		List<HashMap<String, String>> friends = new ArrayList<HashMap<String, String>>();
	    //-----add  selected friends----------
	    for (int i = 0; i <  AppSetting.contacts.size(); i++) {
	    	InviteListItem item = (InviteListItem) mAdapter.getItem(i);
	        if (item.isSelected) {
	        	String email = "";
	        	if(item.contact.getEmail().size() > 0){
	        		email = item.contact.getEmail().get(0);
	        	}
        	
	            String  phoneNumber = item.contact.getPhoneNumer() !=null?item.contact.getPhoneNumer():"";
	            HashMap<String, String> info = new HashMap<String, String>();
	            info.put("email", email);
	            info.put("phoneNumber", phoneNumber);
	            friends.add(info);
	        }
	    }
	    
	    if(friends.size() == 0){
	    	showWarningDialog("There is no the selected friend!");
	        return;
	    }
	    
	    sendInviteRequstToServer(friends);
	    sendInviteToFriends(friends);
	}
	private void sendInviteToFriends(List<HashMap<String, String>> friends){
	    String message = "Hi, friend,  please use Traxit app: htt://traxit.org";
	    
	    List<String> invitePhoneNumbers = new ArrayList<String>();
	    List<String> inviteEmails =  new ArrayList<String>();
	    
	    for (int i = 0 ; i < friends.size(); i++) {
	    	HashMap<String, String> data = friends.get(i);
	        String  number = data.get("phoneNumber");
	        if (number !=null && !number.equals("")) {
	            invitePhoneNumbers.add(number);
	        }else{
	            String email = data.get("email");
	            if (email !=null && !email.equals("")) {
	                inviteEmails.add(email);
	            }
	        }
	    }
	    
	    if(invitePhoneNumbers.size() > 0){
	    	inviteViaSMS( invitePhoneNumbers,  message);
	    }else if(inviteEmails.size() > 0){
	    	inviteViaEmail(inviteEmails, message);
	    }else{
	    	Toast.makeText(mActivity, "There are no email or phone number.", Toast.LENGTH_SHORT).show();
	    }
	    
	}
	private void inviteViaEmail(List<String> emails, String message)
	{
		String targets[] = new String[emails.size()];
		for(int i = 0; i < emails.size(); i++){
			targets[i] = emails.get(i);
		}
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , targets);
		i.putExtra(Intent.EXTRA_SUBJECT, "TRAXIT");
		i.putExtra(Intent.EXTRA_TEXT   ,  message);
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(mActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	private void inviteViaSMS(List<String> phones, String message)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < phones.size(); i++) {
		    sb.append(phones.get(i));
		    if (i != phones.size() - 1) {
		        sb.append(", ");
		    }
		}
		String target = sb.toString();
		
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setType("vnd.android-dir/mms-sms");
		i.putExtra("address" , target);
		i.putExtra("sms_body", message);
		try {
		    startActivity(Intent.createChooser(i, "Send SMS..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(mActivity, "SMS failed, please try again later!", Toast.LENGTH_SHORT).show();
		}
	}

	private void sendInviteRequstToServer(List<HashMap<String, String>> friends){
	    final List<String> emails = new ArrayList<String>();
	    final List<String> mobiles =  new ArrayList<String>();
	    
	    for (int i = 0 ; i < friends.size(); i++) {
	    	HashMap<String, String> data = friends.get(i);
	        String  number = data.get("phoneNumber") != null  ? data.get("phoneNumber"):"";
	        String  email = data.get("email") != null  ? data.get("email"):"";
	        emails.add(email);
	        mobiles.add(number);
	    }

	    final String email = AppSetting.getUserEmail(mActivity);
	    
	    AsyncTask<Void, Void, ResultCode> inviteTask = new AsyncTask<Void, Void, ResultCode>() {

			@Override
			protected ResultCode doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.sendInviteRequestToServer(email, emails, mobiles);
					
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
				if(res == 0) {

				}else{
					showWarningDialog(result.getReturnMessage());
					Toast.makeText(mActivity.getApplicationContext(), "Fail to send invite request to server!", Toast.LENGTH_LONG).show();
				}
				super.onPostExecute(result);
			}

		};
		
		// execute AsyncTask
		inviteTask.execute(null, null, null);

	}
	private void showWarningDialog(String msg){
		CommonFunctions.showWarningDialog(mActivity.getApplicationContext(), msg);
	}
	private void sendManuallInvite() {
    	AlertDialog.Builder customField = new AlertDialog.Builder(mActivity);
    	customField.setTitle("Invite");
        LayoutInflater customInflater = mActivity.getLayoutInflater();
        final View customView=customInflater.inflate(R.layout.manual_invite, null);
        customField.setView(customView)
               .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   String name =((EditText) customView.findViewById(R.id.manualInviteName)).getText().toString();
                	   sendManualRequest(name); 
                   }

               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       //LoginDialogFragment.this.getDialog().cancel();
                   }
               });         
        AlertDialog alert = customField.create();
        alert.show();
	}
	private void sendManualRequest(String target){
		String message = "Hi, friend,  please use Traxit app: htt://traxit.org";
		List<String> friends = new ArrayList<String>();
		friends.add(target);
		List<HashMap<String, String>> targets = new ArrayList<HashMap<String, String>>();
		if(target.equals(AppSetting.getUserEmail(mActivity))){
			Toast.makeText(mActivity.getApplicationContext(), "You can't invite yourself.", Toast.LENGTH_LONG).show();
			return;
		}else if(target.equals(AppSetting.getUserPhoneNumber(mActivity))){
			Toast.makeText(mActivity.getApplicationContext(), "You can't invite yourself.", Toast.LENGTH_LONG).show();
			return;
		}
		if(isEmailValid(target)){
			inviteViaEmail(friends, message);

            HashMap<String, String> info = new HashMap<String, String>();
            info.put("email", target);
            info.put("phoneNumber", "");
            targets.add(info);
            sendInviteRequstToServer(targets);
            
		}else if(isValidPhoneNumber(target)){
			inviteViaSMS(friends, message);
            HashMap<String, String> info = new HashMap<String, String>();
            info.put("email", "");
            info.put("phoneNumber", target);
            targets.add(info);
            sendInviteRequstToServer(targets);
		}else{
			Toast.makeText(mActivity.getApplicationContext(), "Please type correct email or phonenumber!", Toast.LENGTH_LONG).show();
		}
		
	}
	private  boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}
	private  boolean isValidPhoneNumber(CharSequence target) {
	    if (target == null || TextUtils.isEmpty(target)) {
	        return false;
	    } else {
	        return android.util.Patterns.PHONE.matcher(target).matches();
	    }
	}

	private String getOnlyNumericPhoneNumber(String phoneNumber){
		return phoneNumber.replaceAll("[^0-9]", "");
	}
	private void initializeSearchBox() {
		mSearchBox.setHint("friend name");
		mSearchBox.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            	if(cs.length()>0){
            		if(mSearchIcon.getVisibility() == View.VISIBLE){
            			mSearchIcon.setVisibility(View.INVISIBLE);
            			mSearchCancelIcon.setVisibility(View.VISIBLE);
            			mSearchBox.setPadding(10, 0, 0, 0);
            		}

            	}else{
            		if(mSearchIcon.getVisibility() == View.INVISIBLE){
            			mSearchIcon.setVisibility(View.VISIBLE);
            			mSearchCancelIcon.setVisibility(View.INVISIBLE);
            			mSearchBox.setPadding(70, 0, 0, 0);
            		}
            	}  
            }
             
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
                 
            }
             
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
        });
		
		//-----------click canel icon----------------
		mSearchCancelIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSearchBox.setText(null);
				mSearchIcon.setVisibility(View.VISIBLE);
				mSearchCancelIcon.setVisibility(View.INVISIBLE);
				mSearchBox.setPadding(70, 0, 0, 0);
			}
		});
	}
	private class PhoneCallListener extends PhoneStateListener {

		private boolean isPhoneCalling = false;

		String LOG_TAG = "LOGGING 123";

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			if (TelephonyManager.CALL_STATE_RINGING == state) {
				// phone ringing
				Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
			}

			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				// active
				Log.i(LOG_TAG, "OFFHOOK");

				isPhoneCalling = true;
			}

			if (TelephonyManager.CALL_STATE_IDLE == state) {
				// run when class initial and phone call ended, need detect flag
				// from CALL_STATE_OFFHOOK
				Log.i(LOG_TAG, "IDLE");

				if (isPhoneCalling) {

					Log.i(LOG_TAG, "restart app");

					// restart app
					Intent i = mActivity.getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(
									mActivity.getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);

					isPhoneCalling = false;
				}

			}
		}
	}
}
