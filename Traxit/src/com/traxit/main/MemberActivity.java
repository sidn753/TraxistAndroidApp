package com.traxit.main;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.traxit.adapter.MemberListAdapter;
import com.traxit.common.AppSetting;
import com.traxit.common.Config;
import com.traxit.library.SwipeDismissListViewTouchListener;
import com.traxit.library.UserFunctions;
import com.traxit.widget.GroupInfo;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.ResultCode;
import com.traxit.widget.TraxitDataManager;


public class MemberActivity extends Activity {

	Context mContext;
	
	ListView mListView;
	MemberListAdapter mAdapter;
	List<PersonInfo> mItems;
	double mGroupId;
	String mGroupName;
	static List<String>mAddedFriends;
	static List<String> mRemovedFriends;
	ProgressDialog mProgressDialog;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member);

		mContext = this;
		mListView = (ListView)findViewById(R.id.memberList);
		
		mRemovedFriends = new ArrayList<String>();
		mAddedFriends = new ArrayList<String>();
		mGroupId = -1;
		
  		Intent i = getIntent();
  		mGroupName = i.getStringExtra(Config.GroupName);
  		mItems = getMembers(mGroupName);
  		if(mItems == null || mItems.size() == 0){
  			mItems = new ArrayList<PersonInfo>();
  		}
  		mAdapter = new MemberListAdapter(mContext, mItems);
		mListView.setAdapter(mAdapter);
		
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                		mListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

							@Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
								
                                for (int position : reverseSortedPositions) {
                                	PersonInfo person = (PersonInfo)mAdapter.getItem(position);
                                	checkRemovedItem(person.getEmail());
                                	AppSetting.traxitDataManager.getUnGroup().add(person);
                                	mItems.remove(person);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        mListView.setOnTouchListener(touchListener);
        
		Button btnDone = (Button)findViewById(R.id.titlebarLeftButton);
		btnDone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				updateGroup();
			}
		});
		
		Button btnAdd = (Button) findViewById(R.id.titlebarRightButton);
		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(mContext,	UnGroupActivity.class);
				i.putExtra(Config.GroupName, mGroupName);
				startActivity(i);
			}
		});
	}
	@Override
	public void onResume(){
		super.onResume();
		if(mItems != null){
			mAdapter.notifyDataSetChanged();
		}
	}
	private List<PersonInfo>  getMembers(String groupName){
		TraxitDataManager data = AppSetting.traxitDataManager;
		if(data == null) return null;
		
		for(int i = 0 ; i < data.getGroups().size() ; i++){
			if(data.getGroups().get(i).getGroupName().equals(groupName)){
				mGroupId = data.getGroups().get(i).getGroupId();
				return data.getGroups().get(i).getFriends();
			}
		}
		return null;
	}
	
	public static void addFriends(List<String> friends){
		
		
		if(mRemovedFriends != null) {
			for(int j = 0 ; j < friends.size(); j ++){
				boolean isExist = false;
				String newFriend = friends.get(j);
				for(int i = 0 ; i <  mRemovedFriends.size(); i++){
					if(mRemovedFriends.get(i).equals(newFriend)){
						mRemovedFriends.remove(i);
						isExist = true;
						break;
					}
				}
				if(!isExist){
					mAddedFriends.add(newFriend);
				}
			}
		}else{
			mAddedFriends = friends;
		}
	}
	
	private void checkRemovedItem(String email){
	
		boolean isExist = false;
		if(mAddedFriends != null) {
			for(int i = 0 ; i <  mAddedFriends.size(); i++){
				if(mAddedFriends.get(i).equals(email)){
					mAddedFriends.remove(i);
					isExist = true;
					break;
				}
			}
		}
		if(!isExist){
			mRemovedFriends.add(email);
		}
	}
	
	private void updateGroup(){
		if(mAddedFriends.size() == 0 && mRemovedFriends.size() == 0) {
			finish();
			return;
		}
		if(mGroupId == -1) {
			finish();
			return;
		}
		final String email = AppSetting.getUserEmail(this);
		mProgressDialog = ProgressDialog.show(this, "",
				"updating group ...", true);
		AsyncTask<Void, Void, ResultCode> mTask = new AsyncTask<Void, Void, ResultCode>() {

			@Override
			protected ResultCode doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.updateGroup(email, mGroupId, mRemovedFriends, mAddedFriends);
					
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
				if(res != 0) {
					recoverTraxitData();
					Toast.makeText(mContext, "Update fail.", Toast.LENGTH_LONG).show();
				}
				finish();
				super.onPostExecute(result);
			}

		};
		
		// execute AsyncTask		
		mTask.execute(null, null, null);
	}
	
	private void recoverTraxitData(){
		TraxitDataManager data = AppSetting.traxitDataManager;
		if(data == null) return;
		GroupInfo group = null;
		for(int i = 0 ; i < data.getGroups().size() ; i++){
			if(data.getGroups().get(i).getGroupName().equals(mGroupName)){
				group = data.getGroups().get(i);
				break;
			}
		}
		if(group == null) return;
		if(mRemovedFriends.size() > 0){
			List<PersonInfo> removedPersons = new ArrayList<PersonInfo>();
			for(int i = 0 ; i < data.getUnGroup().size() ; i++){
				PersonInfo person = data.getUnGroup().get(i);
				for(int j = 0 ; j < mRemovedFriends.size() ;  j++){
					if(person.getEmail().equals(mRemovedFriends.get(j))){
						removedPersons.add(person);
					}
				}
			}
			group.getFriends().addAll(removedPersons);
			data.getUnGroup().removeAll(removedPersons);
		}
		
		if(mAddedFriends.size() > 0){
			List<PersonInfo> addedPersons = new ArrayList<PersonInfo>();
			for(int i = 0 ; i < group.getFriends().size() ; i++){
				PersonInfo person = group.getFriends().get(i);
				for(int j = 0 ; j < mAddedFriends.size() ;  j++){
					if(person.getEmail().equals(mRemovedFriends.get(j))){
						addedPersons.add(person);
					}
				}
			}
			group.getFriends().removeAll(addedPersons);
			data.getUnGroup().addAll(addedPersons);
		}
	}
}
