package com.traxit.main;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.traxit.adapter.FriendListAdapter;
import com.traxit.common.AppSetting;
import com.traxit.common.Config.FriendCellType;
import com.traxit.library.UserFunctions;
import com.traxit.widget.FriendListItem;
import com.traxit.widget.GroupInfo;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.TraxitDataManager;

public class FriendListFragment extends Fragment{
	ExpandableListView mFriendListView;
	FriendListAdapter mAdapter;
	AsyncTask<Void, Void, JSONObject> mInitTraxiTask;
	ProgressDialog mProgressDialog;
	Activity mActivity;
	Context mContext;
	boolean mStartDownLoaing;
	int mSelectedGroupIndex;
	boolean isUnGroup;
	
	public FriendListFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);
        
        mStartDownLoaing = false;
        mActivity = this.getActivity();
        mContext = mActivity.getApplicationContext();
        mFriendListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
        addClickListenerToListView();

        return rootView;
    }
	
	@Override
	public void onResume(){
		super.onResume();
		if(AppSetting.traxitDataManager == null && !mStartDownLoaing){
			initFriendListTable();
		}else if(AppSetting.traxitDataManager != null){
			loadTable(AppSetting.traxitDataManager);
		}
        
		
	}
	public void goToMessagePageOnClickHandler(View v) {
		//PersonInfo itemToRemove = (PersonInfo)v.getTag();
		Intent i = new Intent(mContext,
				MessageActivity.class);
		startActivity(i);
		
	}
	

	private void addClickListenerToListView(){
		// Listview Group click listener
		mFriendListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				FriendListItem groupItem = (FriendListItem) mAdapter.getGroup(groupPosition);
				if(groupItem.getCellType() == FriendCellType.ungroupCell){
					PersonInfo personInfo = groupItem.getPersonInfo();
					((MainActivity)mActivity).showFriend(personInfo.getPosition());
					
					if(isUnGroup){
						((MainActivity)mActivity).showGroup(0);
					}else{
						isUnGroup = true;	
					}
					
				}
				return false;
			}
		});

		// Listview Group expanded listener
		mFriendListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				FriendListItem groupItem = (FriendListItem) mAdapter.getGroup(groupPosition);
				if(groupItem.getCellType() == FriendCellType.groupCell){
					groupItem.isExpanded = true;
					if(mSelectedGroupIndex == groupPosition){
						((MainActivity)mActivity).showGroup(groupPosition);
					}else{
						mSelectedGroupIndex = groupPosition;
					}
					
				}
			}
		});

		// Listview Group collasped listener
		mFriendListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				FriendListItem groupItem = (FriendListItem) mAdapter.getGroup(groupPosition);
				if(groupItem.getCellType() == FriendCellType.groupCell){
					groupItem.isExpanded = false;
				}
			}
		});

		// Listview on child click listener
		mFriendListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				//v.setBackgroundColor(getResources().getColor(R.color.list_item_selected));
				PersonInfo personInfo = (PersonInfo) mAdapter.getChild(groupPosition, childPosition);
				((MainActivity)mActivity).showFriend(personInfo.getPosition());
				
				isUnGroup = false;
				if(mSelectedGroupIndex == groupPosition){
					((MainActivity)mActivity).showGroup(groupPosition);
				}else{
					mSelectedGroupIndex = groupPosition;
				}
				return false;
			}
		});
	}

	private void loadTable(TraxitDataManager dataManager){
		mAdapter = new FriendListAdapter(mContext,dataManager, this);
		mFriendListView.setAdapter(mAdapter);
		((MainActivity)mActivity).showMe();
		int groupIndex = 0;
		boolean isExist = false;
		for(int i = 0; i <  dataManager.getGroups().size(); i++){
			if(dataManager.getGroups().get(i).getFriends().size() > 0){
				isExist = true;
				groupIndex = i + 1;
				break;
			}
		}
		isUnGroup = false;
		mSelectedGroupIndex = -1;
		if(isExist){
			mFriendListView.expandGroup(groupIndex);
			mSelectedGroupIndex = groupIndex;
			isUnGroup = false;
			((MainActivity)mActivity).showGroup(groupIndex);
		}else{
			if(dataManager.getUnGroup().size() > 0){
				((MainActivity)mActivity).showGroup(0);
				isUnGroup = true;
			}
		}
		
		
		
	}
	private void initFriendListTable(){
		mProgressDialog = ProgressDialog.show(getActivity(), "",
				"Loading Data...", true);
		mStartDownLoaing = true;
	   final String email = AppSetting.getUserEmail(mActivity);
		mInitTraxiTask = new AsyncTask<Void, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.getInitTraxitData(email);
					
				if(json!=null){
					return json; 
				}
				return null;
			}

			@Override
			protected void onPostExecute(JSONObject result) {
				mProgressDialog.dismiss();
				mStartDownLoaing = false;
				if(result != null){
					int resultCode;
					try {
						resultCode = result.getInt("result");
				        
						if(resultCode == 0) {
							JSONObject json = result.getJSONObject("msg");
							initTraxitData(json);
						}else{
							String errMsg = result.getString("msg");
							showWarningDialog(errMsg);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					Toast.makeText(mContext, "Can't access on Traxit server!", Toast.LENGTH_LONG).show();
				}
				mInitTraxiTask = null;
			}

		};
		
		// execute AsyncTask
		mInitTraxiTask.execute(null, null, null);
	}
	
	private void initTraxitData(JSONObject data) throws JSONException{
		String fullName = data.getString("fullName");
		String phoneNumber = data.getString("phoneNumber");
		JSONArray groups = data.getJSONArray("groupList");
		JSONArray friends = data.getJSONArray("friends");
		JSONArray requests = data.getJSONArray("inviteRequest");
		
	    List<GroupInfo> groupList = new ArrayList<GroupInfo>();
	    
	    //-------------get group list-----------
	    for (int i = 0; i < groups.length(); i++) {
	    	JSONObject item = groups.getJSONObject(i);
	        double groupId = item.getDouble("id");
	        String groupName = item.getString("name");
	        int count = item.getInt("count");
	        JSONArray group = friends.getJSONArray(i+1);
	        List<PersonInfo> persons = getDataWithLocationContact(group);
	        GroupInfo  groupInfo = new GroupInfo(groupId, count, groupName, persons);
	        groupList.add(groupInfo);
	    }
	    List<PersonInfo> unGroupList = getDataWithLocationContact(friends.getJSONArray(0));
	    List<PersonInfo> requestList = getDataWithContactList(requests);
	    
	    AppSetting.saveUserName(mActivity, fullName);
	    AppSetting.saveUserPhoneNumer(mActivity, phoneNumber);
	    
	    TraxitDataManager dataManager = new TraxitDataManager(groupList, unGroupList, requestList);
	    AppSetting.setTraxitData(dataManager);
	    loadTable(dataManager);
	}

	private List<PersonInfo> getDataWithLocationContact(JSONArray data) throws JSONException
	{
	
	    List<PersonInfo> newData = new ArrayList<PersonInfo>();
	  
	    if (data.equals(null) || data.length() == 0)  return newData;
	    for (int i = 0; i < data.length(); i++) {
	        JSONObject locationInfo = data.getJSONObject(i);
	        String email = locationInfo.getString("email");
	        String name = locationInfo.getString("fullName");
	        double latitude = locationInfo.getDouble("latitude");
	        double longitude = locationInfo.getDouble("longitude");
	        int messageCount = locationInfo.getInt("newMessageCount");
	        
	        
	        if (email.equals("")) continue;
	        PersonInfo personInfo = new PersonInfo(mContext, email, name);
	        personInfo.setPosition(new LatLng(latitude, longitude));
	        personInfo.setMessageCount(messageCount);
	        newData.add(personInfo);
	    }
	    return newData;
	}
	private List<PersonInfo> getDataWithContactList(JSONArray data) throws JSONException
	{
	
	    List<PersonInfo> newData = new ArrayList<PersonInfo>();
	  
	    if (data.equals(null) || data.length() == 0)  return newData;
	    for (int i = 0; i < data.length(); i++) {
	        JSONObject locationInfo = data.getJSONObject(i);
	        String email = locationInfo.getString("email");
	        String name = locationInfo.getString("fullName");
 
	        if (email.equals("")) continue;
	        PersonInfo personInfo = new PersonInfo(mContext, email, name);
	        newData.add(personInfo);
	    }
	    return newData;
	}
	
		
	private void showWarningDialog(String message){
		showConfirmDialog("Warning", message);
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
	

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	public void acceptInviteRequestFrom(final String friendEmail){
	   final String email = AppSetting.getUserEmail(mActivity);
	   mInitTraxiTask = new AsyncTask<Void, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.acceptInviteRequestFrom(email, friendEmail);
					
				if(json!=null){
					return json; 
				}
				return null;
			}

			@Override
			protected void onPostExecute(JSONObject result) {
				if(result != null){
					int resultCode;
					try {
						resultCode = result.getInt("result");
				        
						if(resultCode == 0) {
							JSONObject json = result.getJSONObject("msg");
							addNewFriend(friendEmail,json);
						}else{
							String errMsg = result.getString("msg");
							Toast.makeText(mActivity.getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mInitTraxiTask = null;
			}

		};
		
		// execute AsyncTask
		mInitTraxiTask.execute(null, null, null);
	}
	
	public void declineInviteRequestFrom(final String friendEmail){
		   final String email = AppSetting.getUserEmail(mActivity);
		   mInitTraxiTask = new AsyncTask<Void, Void, JSONObject>() {

				@Override
				protected JSONObject doInBackground(Void... params) {
					UserFunctions userFunction = new UserFunctions();
					JSONObject json = userFunction.declineInviteRequestFrom(email, friendEmail);
						
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
							removeFromInviteRequest(friendEmail);
						}else{
							String errMsg = result.getString("msg");
							Toast.makeText(mActivity.getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					mInitTraxiTask = null;
				}

			};
			
			// execute AsyncTask
			mInitTraxiTask.execute(null, null, null);
	   /* NSLog(@"clicked decline button");
	    NSString *email = [TXAppManager sharedInstance].userEmail;
	    if (friendEmail  == nil) {
	        return;
	    }
	    NSDictionary * parameters = @{@"tag":@"inviteDecline", @"email":email, @"friend":friendEmail};
	    [[TXAppManager sharedInstance] sendRequestToServer:parameters callBackBlock:^(NSDictionary *data, NSError *error) {
	        
	        if ([[data objectForKey:@"result"] intValue] == 0) {
	            [self removeFromInviteRequest:friendEmail];
	        }
	        else {
	            NSString * errMsg = [data objectForKey:@"msg"];
	            [self showWarningDialog:errMsg];
	        }
	    }];*/
	}
	private void addNewFriend(String email, JSONObject data) throws JSONException
	{
        double latitude = data.getDouble("latitude");
        double longitude = data.getDouble("longitude");
        String fullName = data.getString("fullName");
        
        PersonInfo personInfo = new PersonInfo(mContext , email, fullName);
        personInfo.setPosition(new LatLng(latitude, longitude));
	    
        List<PersonInfo> requests = AppSetting.traxitDataManager.getRequests();
	    for (int i = 0; i < requests.size(); i++) {
	        PersonInfo  request = requests.get(i);
	        if(request.getEmail().equals(email)){
	        	requests.remove(i);
	            break;
	        }
	    }
	    AppSetting.traxitDataManager.getUnGroup().add(personInfo);
		mAdapter = new FriendListAdapter(mContext,AppSetting.traxitDataManager, this);
		mFriendListView.setAdapter(mAdapter);
	    
	}
	private void removeFromInviteRequest(String email)
	{
	    
        List<PersonInfo> requests = AppSetting.traxitDataManager.getRequests();
	    for (int i = 0; i < requests.size(); i++) {
	        PersonInfo  request = requests.get(i);
	        if(request.getEmail().equals(email)){
	        	requests.remove(i);
	            break;
	        }
	    }
	    reloadFriendListView();
	    
	}
	public void reloadFriendListView(){
		if(AppSetting.traxitDataManager == null) return;
		mAdapter = new FriendListAdapter(mContext, AppSetting.traxitDataManager, this);
		this.getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
				mFriendListView.setAdapter(mAdapter);
		    }
		});
	}
	
	public void refreshFriendListView(){
		if(AppSetting.traxitDataManager == null) return;
		if(mAdapter != null){
			this.getActivity().runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 mAdapter.notifyDataSetChanged();
			    }
			});
		}
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser){
		super.setUserVisibleHint(isVisibleToUser);
		
	}
}
