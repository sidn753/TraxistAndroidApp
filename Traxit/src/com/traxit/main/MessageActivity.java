package com.traxit.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.traxit.adapter.MessageListAdapter;
import com.traxit.common.AppSetting;
import com.traxit.common.Config;
import com.traxit.library.NotificationService;
import com.traxit.library.TextBitmap;
import com.traxit.library.UserFunctions;
import com.traxit.widget.MessageInfo;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.PushNotificationInfo;
import com.traxit.widget.ResultCode;

public class MessageActivity extends Activity{
	static final int MENU_MANUAL_REFRESH = 0;
	static final int MENU_DISABLE_SCROLL = 1;
	static final int MENU_SET_MODE = 2;
	static final int MENU_DEMO = 3;

	private LinkedList<MessageInfo> mListItems;
	private PullToRefreshListView mMessageListView;
	private ListView mActualMessageListView;
	public MessageListAdapter mAdapter;
	
	private PersonInfo mPersonInfo;
	ProgressDialog mProgressDialog;
	Context mContext;
	
	private int mCalledNumber;
	
	private EditText mMessageEditText;
	
	//final String testMessage = "Lionel Palairet (1870–1933) was an English amateurcricketer who played forSomerset and Oxford University. A graceful right-handed batsman, he was selected to play Test cricket for England twice in 1902; an unwillingness to tour during the English winter limited his Test appearances. For Somerset, he frequently opened the batting with Herbie Hewett. In 1892, they shared a partnership of 346 for the first wicket, an opening stand that set a record for the County Championship and remains Somerset's highest first-wicket partnership. In that season, Palairet was named as one of the Five Batsmen of the Year by Wisden. Over the following decade, he was one of the leading amateur batsmen in England. He passed 1,000 first-class runs in a season on seven occasions, and struck two double centuries. After 1904, he appeared infrequently for Somerset, though he played a full season in 1907 when he was chosen to captain the county. He retired from first-class cricket in 1909, having scored over 15,000 runs. Contemporaries judged Palairet to have one of the most attractive batting styles of the period, and his obituary in The Times described him as the most beautiful batsman of all time. ";
	
	private final Observer receivedNotification = new Observer()
	 {
			@Override
			public void update(Observable observable, Object data) {
				PushNotificationInfo pushInfo = (PushNotificationInfo)data;
				displayNewMessage(pushInfo);
			}
	 };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getActionBar().setDisplayUseLogoEnabled(false);
		this.getActionBar().setDisplayShowHomeEnabled(false);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_message);
		mContext = this;
		
		NotificationService.getInstance().addObserver(Config.Notification_Message, receivedNotification);
	

  		
  		Button btnSendMessage = (Button)findViewById(R.id.btnMessageSend);
  		btnSendMessage.setOnClickListener(new View.OnClickListener() {
				@Override
			public void onClick(View arg0) {
				sendMessageToFriend();
			}
		});
  		
  		
   		mMessageListView = (PullToRefreshListView)findViewById(R.id.messageTable);
		// Set a listener to be invoked when the list should be refreshed.
   		mMessageListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				syncMessageHistory();
			}
		});
   		
   		mMessageEditText =(EditText)findViewById(R.id.txtMessage);
   		mMessageListView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);
			}
		});
   		
   		mActualMessageListView = mMessageListView.getRefreshableView();

		// Need to use the Actual ListView when registering for Context Menu
		registerForContextMenu(mActualMessageListView);

		addKeyboardListener();

		
	}
	
	private void initParameter(){
		mListItems = new LinkedList<MessageInfo>();
  		Intent i = getIntent();
  		String friendEmail = i.getStringExtra(Config.FriendEmail);
	
  		if(friendEmail == null || friendEmail.equals("") ){
  			return;
  		}else{
  			mPersonInfo = AppSetting.getPersonFromEmail(friendEmail);
  			if(mPersonInfo == null){
  	  			String friendName = i.getStringExtra(Config.FriendName);
  	  			if(friendName == null || friendName.equals("") ){
  	  				mPersonInfo = new PersonInfo(mContext, friendEmail, "");
  	  			}else{
  	  				mPersonInfo = new PersonInfo(mContext, friendEmail, friendName);
  	  			}
  	  				
  			}

  		}
  		mPersonInfo.setMessageCount(0);
  		AppSetting.currentChatFriendEmail = mPersonInfo.getEmail();
		Bitmap friendPhoto = null;
        if(mPersonInfo.getPhoto() != null){
        	friendPhoto = mPersonInfo.getPhoto();
        }else if(mPersonInfo.getName() !=null && !mPersonInfo.getName().equals("")){
        	 TextBitmap textBitmap = new TextBitmap();
        	 friendPhoto = textBitmap.createBitmapWithText(mPersonInfo.getName()); 
        }else{
	       	 TextBitmap textBitmap = new TextBitmap();
	       	 friendPhoto = textBitmap.createBitmapWithText(mPersonInfo.getEmail()); 
        }
        
        Bitmap myPhoto = AppSetting.getUserProfilePicture(this);
        if(myPhoto == null){
       	 	TextBitmap textBitmap = new TextBitmap();
       	 	myPhoto = textBitmap.createBitmapWithText(AppSetting.getUserName(this));  
        }
        
        mAdapter = new MessageListAdapter(mContext, mListItems, myPhoto, friendPhoto);
		/**
		 * Add Sound Event Listener
		 */
		/*SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		mMessageListView.setOnPullEventListener(soundListener);
		*/

		// You can also just use setListAdapter(mAdapter) or
		// mPullRefreshListView.setAdapter(mAdapter)
        mActualMessageListView.setAdapter(mAdapter);
        mCalledNumber = 0;
        getMessageHistory();
	}
	private void addKeyboardListener(){
		final View activityRootView = findViewById(R.id.container);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
	            if (mCalledNumber <2 && (activityRootView.getRootView().getHeight() - activityRootView.getHeight()) >  activityRootView.getRootView().getHeight()/3) {
	            	Log.e("Keyboard", "opened");
            		mActualMessageListView.setSelection(mListItems.size()-1);
            		mCalledNumber++;
				 }else{
					 mCalledNumber = 0;
				 }
		     }
		});
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    // Checks whether a hardware keyboard is available
	    if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
	        Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
	    } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
	        Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
	    }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			notifyToReadMessage();
			super.onBackPressed();
			break;
			
		}
		return true;
	}
	@Override
	public void onResume(){
		super.onResume();
		initParameter();
	}
	
	@Override
	public void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		setIntent(intent);
	}
	private void displayNewMessage(PushNotificationInfo pushInfo){
		String type = pushInfo.getType();
		if(!type.equals(Config.kPushMessageType)) return;
		String sendFrom = pushInfo.getSender();
		if(!sendFrom.equals(mPersonInfo.getEmail())) return;
		String message = pushInfo.getMessage();
		long time = pushInfo.getTime();
        MessageInfo  newMessage = new MessageInfo(false, message, getTimeFromMillis(time));
        mListItems.add(newMessage);
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 mAdapter.notifyDataSetChanged();
		    	 mActualMessageListView.setSelection(mListItems.size()-1);

		    }
		});
		notifyToReadMessage();
	
	}
	

	

	
private void getMessageHistory()
	{
	    //------initialize userEmail-------a
		final String email = AppSetting.getUserEmail(this);;
		final String friendEmail = mPersonInfo.getEmail();
		mProgressDialog = ProgressDialog.show(this, "",
				"Loading Data...", true);

		 AsyncTask<Void, Void, JSONObject> mInitTraxiTask = new AsyncTask<Void, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.getMessageHistory(email, friendEmail, 0);
					
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
						JSONArray json = result.getJSONArray("msg");
						loadMessageData(json, true);
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
private void syncMessageHistory()
{
    //------initialize userEmail-------a
	final String email = AppSetting.getUserEmail(this);;
	final String friendEmail = mPersonInfo.getEmail();
	 AsyncTask<Void, Void, JSONObject> mInitTraxiTask = new AsyncTask<Void, Void, JSONObject>() {

		@Override
		protected JSONObject doInBackground(Void... params) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.getMessageHistory(email, friendEmail, mListItems.size());
				
			if(json!=null){
				return json; 
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			int resultCode;
			try {
				resultCode = result.getInt("result");
		        
				if(resultCode == 0) {
					JSONArray json = result.getJSONArray("msg");
					loadMessageData(json, false );
				}else{
					loadMessageData(null, false);;
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
	private void loadMessageData(JSONArray json, boolean isRecent) throws JSONException{
        /*   
         $item['isMine'] ;
         $item['message'];
         $item['sentTime'];
         $item['status'];*/
		if(json !=null){
            for (int i = 0; i < json.length(); i++) {
            	JSONObject data = json.getJSONObject(i);
            	boolean isMine = data.getInt("isMine") == 1? true: false;
            	String message = data.getString("message");
            	String messageTime = getTimeFromMillis( data.getLong("sentTime"));
            	MessageInfo  newMessage = new MessageInfo(isMine,  message, messageTime);
            	//newData.add(newMessage);
            	mListItems.add(i, newMessage);
            }
			mAdapter.notifyDataSetChanged();
			if(isRecent){
				mActualMessageListView.setSelection(mListItems.size()-1);
			}

		}
		mMessageListView.onRefreshComplete();

	 }
	private void notifyToReadMessage()
	{
	    //------initialize userEmail-------a
		final String email = AppSetting.getUserEmail(this);;
		final String friendEmail = mPersonInfo.getEmail();

		AsyncTask<Void, Void, ResultCode> mReadTask = new AsyncTask<Void, Void, ResultCode>() {

			@Override
			protected ResultCode doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.readMessage(email, friendEmail);
					
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
				super.onPostExecute(result);
			}

		};
		
		// execute AsyncTask
		mReadTask.execute(null, null, null);

	}
	private String getCurrentTime(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormate = new SimpleDateFormat("MMM dd, yyyy HH:mm");
		return dateFormate.format(c.getTime());
	}
	
	private String getTimeFromMillis(long milliseconds){
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliseconds*1000);
		return formatter.format(calendar.getTime());
	}
	
	private void sendMessageToFriend() {
	    boolean  needReload = false;
	    String message = mMessageEditText.getText().toString();
	    if (!message.equals("") && message.length() > 0) {
	        needReload = true;
	        MessageInfo  newMessage = new MessageInfo(true, message, getCurrentTime());
	        
	        mListItems.add(newMessage);
	        sendMessage(message);
	    }
	    mMessageEditText.setText("");
		//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);
	    
	    if (needReload) {
	    	mAdapter.notifyDataSetChanged();
	    	mActualMessageListView.setSelection(mListItems.size()-1);
	        //[_messageListTable scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:messageList.count - 1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];
	    }
	}
	private void sendMessage(String message){
		int length = message.length();
		int subMessageLength = 500;
		
		int part = length / subMessageLength;
		for(int i = 0; i < part+1; i++){
			int start = i*subMessageLength;
			int end = start + subMessageLength;
			if(i == part ) end = length;
			String subMessage = message.substring(start, end);
			sendMessageToServer(subMessage);
		}

	}
	private void sendMessageToServer(final String message)
	{
	    //------initialize userEmail-------a
		final String email = AppSetting.getUserEmail(this);;
		final String friendEmail = mPersonInfo.getEmail();
		 AsyncTask<Void, Void, ResultCode> sendMessageTask = new AsyncTask<Void, Void, ResultCode>() {

					@Override
					protected ResultCode doInBackground(Void... params) {
						UserFunctions userFunction = new UserFunctions();
						JSONObject json = userFunction.sendMessageToServer(email, friendEmail, message);
							
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
						}else{
							Toast.makeText(getApplicationContext(), result.getReturnMessage(), Toast.LENGTH_LONG).show();
						}
						super.onPostExecute(result);
					}

				};
				// execute AsyncTask
				sendMessageTask.execute(null, null, null);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppSetting.currentChatFriendEmail = null;
		NotificationService.getInstance().removeObserver(Config.Notification_Message, receivedNotification);
	}
	
	
}
