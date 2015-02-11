package com.traxit.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.traxit.adapter.NavDrawerListAdapter;
import com.traxit.common.AppSetting;
import com.traxit.common.Config;
import com.traxit.library.NotificationService;
import com.traxit.widget.ContactInfo;
import com.traxit.widget.GroupInfo;
import com.traxit.widget.NavDrawerItem;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.PushNotificationInfo;
import com.traxit.widget.SegmentedRadioGroup;
import com.traxit.widget.TraxitDataManager;

public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private LinearLayout mMenuLayout;
	private ListView mDrawerList;
	
	//-------Custom ActionBar------
	private Button mTitleBarLeftButton;
	private Button mTitleBarRightImageButton;
	private Button mTitlBarRightButton;
	private TextView mTitleBarTitleLabel;
	private SegmentedRadioGroup mTitleBarSegmentButton;
	
	//private ActionBarDrawerToggle mDrawerToggle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	private int mCurrentPage;
	
	private ContactFragment mMapFragment;
	private static ProfileFragment mProfileFragment;
	private Context mContext;
	
	private final Observer receivedNotification = new Observer()
	 {
			@Override
			public void update(Observable observable, Object data) {
				// TODO Auto-generated method stub
				PushNotificationInfo pushInfo = (PushNotificationInfo)data;
				PushNotificationProcess(pushInfo);
			}
	 };
	 
	private final Observer updateAddressNotification = new Observer()
	 {
			@Override
			public void update(Observable observable, Object data) {
				// TODO Auto-generated method stub
				PersonInfo person = (PersonInfo)data;
				updateUserAddressNotification(person);
			}
	 };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.getActionBar().setDisplayUseLogoEnabled(false);
		//this.getActionBar().setDisplayShowHomeEnabled(false);
		setContentView(R.layout.activity_main);
		
		mContext = this;
		// init Custom title bar
		mTitleBarLeftButton = (Button)findViewById(R.id.titlebarLeftButton);
		mTitleBarTitleLabel = (TextView)findViewById(R.id.titlebarTitleLabel);
		mTitleBarRightImageButton = (Button)findViewById(R.id.titlebarRightImageButton);
		mTitlBarRightButton = (Button)findViewById(R.id.titlebarRightButton);
		mTitleBarSegmentButton = (SegmentedRadioGroup) findViewById(R.id.titlebarSegmentButton);
		
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mMenuLayout = (LinearLayout) findViewById(R.id.menuLayout);
		TextView menuUserEmailLabel = (TextView) findViewById(R.id.userEmailLabel);
		menuUserEmailLabel.setText(AppSetting.getUserEmail(this));
		
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Contacts
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Groups
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Profile
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Message
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Invite Friends
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// Save Password
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true));
		// Visibility
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1), true));
		// Logout
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setHomeButtonEnabled(true);

		/*mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};*/
		//mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		mCurrentPage = -1;
		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		setMenuIconClickListener();
		
        NotificationService.getInstance().addObserver(Config.Notification_Message, receivedNotification);
        NotificationService.getInstance().addObserver(Config.Notification_Update_Address, updateAddressNotification);
		
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			AppSetting.mProfileSaveableFlag = false;
			displayView(position);
		}
	}
	@Override
	public void onResume(){
		super.onResume();
		if(AppSetting.contacts == null){
			loadContacts();
		}
	}
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.overLayout:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
/*	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mMenuLayout);
		menu.findItem(R.id.overLayout).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		if(mCurrentPage == position){
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mMenuLayout);
			
		}else{
			
			mTitleBarRightImageButton.setVisibility(View.GONE);
			mTitleBarSegmentButton.setVisibility(View.GONE);
			mTitlBarRightButton.setVisibility(View.GONE);
			
			// update the main content by replacing fragments
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new ContactFragment();
				mMapFragment = (ContactFragment) fragment;
				break;
			case 1:
				fragment = new GroupsFragment();
				break;
			case 2:
				fragment = new ProfileFragment();
				mProfileFragment = (ProfileFragment) fragment;
				break;
			case 3:
				fragment = new MessageFragment();
				break;
			case 4:
				fragment = new InviteFragment();
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				logout();
				return;

			default:
				break;
			}
			
			if (fragment != null) {
				mCurrentPage = position;
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
	
				// update selected item and title, then close the drawer
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles[position]);
				mDrawerLayout.closeDrawer(mMenuLayout);
			} else {
				// error in creating fragment
				Log.e("MainActivity", "Error in creating fragment");
			}
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitleBarTitleLabel.setText(title);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		//mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		//mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onBackPressed(){
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		NotificationService.getInstance().removeObserver(Config.Notification_Message, receivedNotification);
		NotificationService.getInstance().removeObserver(Config.Notification_Update_Address, updateAddressNotification);
	}
	//--------Public Funcions----------------------
	public void showFriend(LatLng location){
		mMapFragment.moveCamera(location);
	}
	public void showMe(){
		
		if(mMapFragment == null) return;
		mMapFragment.showMe();
	}
	public void showGroup(int groupIndex){
		if(mMapFragment == null) return;
		mMapFragment.setUpMarker(groupIndex);
	}
	public void updateFriendPosition(String email, LatLng position){
		if(mMapFragment == null) return;
		mMapFragment.updateFriendPosition(email, position);
	}
	public void updateFriendListTable(){
		if(mMapFragment != null){
			mMapFragment.updateFriendListTable();	
		}
	}
	public static void setCallingCode(String code, String dialCode){
		if(mProfileFragment != null){
			mProfileFragment.setCallingCode(code, dialCode);
//			mProfileFragment = null;
		}
		
		
	}
	private void loadContacts(){
		AsyncTask<Void, Void, String> loadContactsTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				//getContactsDetails();
				readContacts();
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
			}
			
		};
		
		// execute AsyncTask
		loadContactsTask.execute(null, null, null);
	}
 /*   private void getContactsDetails() {
   	 	List<ContactInfo> contacts = new ArrayList<ContactInfo>();
        ContentResolver cr = getContentResolver();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);
        while (phones.moveToNext()) {
        	String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
            String Name = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if(Name == null) Name = "";
            String Number = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(Number == null) Number = "";
 
            String image_uri = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
 
            
            if (image_uri == null) {
            	image_uri = "";
                // image.setImageURI(Uri.parse(image_uri));
            }
            
            // get email and type
       	 
           List<String> emails = new ArrayList<String>();
           Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[]{id}, null);
            while (emailCur.moveToNext()) {
                // This would allow you get several email addresses
                    // if the email addresses were stored in an array
                String email = emailCur.getString(
                              emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                emails.add(email);
               // String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

               //   System.out.println("Email " + email + " Email Type : " + emailType);
            }
            emailCur.close();
            
            if(emails.size() > 0 || (Number!= null && !Number.equals(""))){
            	ContactInfo contact = new ContactInfo(emails, Name, Number, image_uri);
            	contacts.add(contact);
            }
 
        }
        boolean isUpdate =  AppSetting.setContacts(contacts);
        if(isUpdate){
        	updateFriendListTable();
        }
        
    }
    */
    private void readContacts() {
    	  List<ContactInfo> contacts = new ArrayList<ContactInfo>();
    	  ContentResolver cr = getContentResolver();
    	  Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
    	    null, null, null);

    	  if (cur.getCount() > 0) {
    	   while (cur.moveToNext()) {
	    	  String phone = null;
	    	 // String emailContact = null;
	    	 // String emailType = null;
	    	  String image_uri = "";
	    	  //Bitmap bitmap = null;
	    	    String id = cur.getString(cur
	    	      .getColumnIndex(ContactsContract.Contacts._ID));
	    	    String name = cur
	    	      .getString(cur
	    	        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	
	    	    image_uri = cur
	    	      .getString(cur
	    	        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
	    	    if (Integer
	    	      .parseInt(cur.getString(cur
	    	        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
			    	     Cursor pCur = cr.query(
			    	       ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
			    	       null,
			    	       ContactsContract.CommonDataKinds.Phone.CONTACT_ID
			    	         + " = ?", new String[] { id }, null);
			    	     while (pCur.moveToNext()) {
			    	      phone = pCur
			    	        .getString(pCur
			    	          .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			    	     }
			    	     pCur.close();
			
			    	     Cursor emailCur = cr.query(
			    	       ContactsContract.CommonDataKinds.Email.CONTENT_URI,
			    	       null,
			    	       ContactsContract.CommonDataKinds.Email.CONTACT_ID
			    	         + " = ?", new String[] { id }, null);
			    	     List<String> emails = new ArrayList<String>();
			    	     while (emailCur.moveToNext()) {
				    	      String emailContact = emailCur
				    	        .getString(emailCur
				    	          .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				    	     /* emailType = emailCur
				    	        .getString(emailCur
				    	          .getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));*/
				    	      emails.add(emailContact);
			
			    	     }
			
			    	     emailCur.close();
			            if(emails.size() > 0 || (phone!= null && !phone.equals(""))){
			            	ContactInfo contact = new ContactInfo(emails, name, phone, image_uri);
			            	contacts.add(contact);
			            }
	    	    }
	    	    
	    	   /* if (image_uri != null) {
	    	     System.out.println(Uri.parse(image_uri));
	    	     try {
	    	      bitmap = MediaStore.Images.Media
	    	        .getBitmap(this.getContentResolver(),
	    	          Uri.parse(image_uri));
	
	    	     } catch (FileNotFoundException e) {
	    	      // TODO Auto-generated catch block
	    	      e.printStackTrace();
	    	     } catch (IOException e) {
	    	      // TODO Auto-generated catch block
	    	      e.printStackTrace();
	    	     }*/
		    	


    	    }

	    	    
    	   }
          boolean isUpdate =  AppSetting.setContacts(contacts);
          if(isUpdate){
          	updateFriendListTable();
          }

  }
  

    private void logout(){
       // [[NSUserDefaults standardUserDefaults] setBool:NO forKey:LoginStatus];
       // [[NSUserDefaults standardUserDefaults] synchronize];
       // TXAppDelegate * appDelegate = (TXAppDelegate*)[[UIApplication sharedApplication] delegate];
       // [appDelegate presentLoginViewControllerWithAnimation:YES];
    	AppSetting.setLogoutStatus(this);
		AppSetting.traxitDataManager = null;
		finish();
    }
    

    //---------------Custom title bar ------------------------
    private void setMenuIconClickListener(){
    	mTitleBarLeftButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				boolean drawerOpen = mDrawerLayout.isDrawerOpen(mMenuLayout);
				if(drawerOpen){
					mDrawerLayout.closeDrawer(mMenuLayout);
				}else{
					switch(mCurrentPage){
					case 4: // Invite Page
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						EditText mSearchBox = (EditText)findViewById(R.id.search_text);
						imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
						break;
					
					}
					mDrawerLayout.openDrawer(mMenuLayout);
				}
				
			}
		});
    }
   
   //---------   Push Notification---------
	private void PushNotificationProcess(PushNotificationInfo pushInfo){
		if(AppSetting.traxitDataManager == null) return;
		
		String type = pushInfo.getType();
		if(type.equals(Config.kPushMessageType)){
			notifyNewMessageFrom(pushInfo.getSender());
		}else if(type.equals(Config.kPushInviteType)){
			receiveInviteRequestFrom(pushInfo.getSender());
		}else if(type.equals(Config.kPushAcceptType)){
			PersonInfo person = new PersonInfo(mContext, pushInfo.getSender(), pushInfo.getName());
			person.setPosition(new LatLng(pushInfo.getLatitude(),pushInfo.getLongitude()));
			notifyInviteAcceptFrom(person);
		}else if(type.equals(Config.kPushDeclineType)){
			String msg = "Your invite request is declined by " + pushInfo.getSender();
			showConfirmDialog("Decline", msg);
		}else if(type.equals(Config.kPushLocationType)){
			updateFriendLocation(pushInfo);
		}
	}
	
	private void updateUserAddressNotification(PersonInfo person){
		if(mMapFragment == null) return;
		mMapFragment.refreshFriendListView();
	}
	
	private void  notifyNewMessageFrom(String friendEmail){
		if(AppSetting.currentChatFriendEmail != null && friendEmail.equals(AppSetting.currentChatFriendEmail)) return;
		boolean isExist = false;
		TraxitDataManager data = AppSetting.traxitDataManager;
	    for (int i = 0; i < data.getGroups().size(); i++) {
	    	GroupInfo groupInfo = data.getGroups().get(i);
	    	for(int j = 0 ; j <groupInfo.getFriends().size(); j++){
	    		PersonInfo person = groupInfo.getFriends().get(j);
	    		if(person.getEmail().equals(friendEmail)){
	    			int count = person.getMessageCount();
	    			person.setMessageCount(count+1);
	    			isExist = true;
	    			break;
	    		}
	    	}
	    	if(isExist) break;
	    }
	    
	    if(!isExist){
	    	for(int i = 0 ; i< data.getUnGroup().size(); i++){
	    		PersonInfo person = data.getUnGroup().get(i);
	    		if(person.getEmail().equals(friendEmail)){
	    			int count = person.getMessageCount();
	    			person.setMessageCount(count+1);
	    			isExist = true;
	    			break;
	    		}
	    	}
	    }
	    
	    if(isExist){
	    	if(mMapFragment == null) return;
			mMapFragment.refreshFriendListView();
	    }
 

	}
	
	private void receiveInviteRequestFrom(String sender){
		PersonInfo personInfo = new PersonInfo(mContext, sender, "");
		AppSetting.updatePersonInfo(personInfo);
		AppSetting.traxitDataManager.getRequests().add(personInfo);
		
		if(mMapFragment == null) return;
		mMapFragment.reloadFriendListView();
	}
	
	private void updateFriendLocation(PushNotificationInfo pushInfo){
		final String email = pushInfo.getSender();
		PersonInfo person = AppSetting.getPersonFromEmail(email);
		if(person == null) return;
		final LatLng position = new LatLng(pushInfo.getLatitude(), pushInfo.getLongitude());
		person.setPosition(position);
		
		if(mMapFragment == null) return;
		mMapFragment.updateFriendPosition(email, position);

	}
	
	private void notifyInviteAcceptFrom(PersonInfo personInfo){
		AppSetting.updatePersonInfo(personInfo);
		AppSetting.traxitDataManager.getUnGroup().add(personInfo);
		
		if(mMapFragment == null) return;
		mMapFragment.reloadFriendListView();
	}
	
	@SuppressWarnings("deprecation")
	private void showConfirmDialog(String title, String message)
	 {
			AlertDialog alertDialog = new AlertDialog.Builder(
					mContext).create();
			alertDialog.setTitle(title);
			alertDialog.setMessage(message);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.show();
	 }
}
