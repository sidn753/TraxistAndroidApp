package com.traxit.main;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.traxit.adapter.UnGroupListAdapter;
import com.traxit.adapter.UnGroupListAdapter.UngroupListHolder;
import com.traxit.adapter.UnGroupListAdapter.UngroupListItem;
import com.traxit.common.AppSetting;
import com.traxit.common.Config;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.TraxitDataManager;


public class UnGroupActivity extends Activity {

	Context mContext;
	
	ListView mListView;
	UnGroupListAdapter mAdapter;
	List<PersonInfo> mItems;
	Button mAddButton;
	String mGroupName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ungroup);

		mContext = this;
		mListView = (ListView)findViewById(R.id.memberList);

  		Intent i = getIntent();
  		mGroupName = i.getStringExtra(Config.GroupName);
  		
  		mItems = AppSetting.traxitDataManager.getUnGroup();
  		if(mItems == null || mItems.size() == 0){
  			mItems = new ArrayList<PersonInfo>();
  		}
  		mAdapter = new UnGroupListAdapter(mContext, mItems);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				UngroupListHolder holder = (UngroupListHolder) view.getTag();
				UngroupListItem item = (UngroupListItem) mAdapter.getItem(position);
				if(item.isSelected){
					item.isSelected = false;
					holder.imgCheck.setVisibility(View.INVISIBLE);
				}else{
					item.isSelected = true;
					holder.imgCheck.setVisibility(View.VISIBLE);
				}
				showAddButton();
			}
			
		});
		
		Button btnDone = (Button)findViewById(R.id.titlebarLeftButton);
		btnDone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		mAddButton = (Button) findViewById(R.id.titlebarRightButton);
		mAddButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addSeletedFriendsToGroup();
			}
		});
	}

	private void showAddButton(){
		if(isExistSelectedContacts()){
			mAddButton.setVisibility(View.VISIBLE);
		}else{
			mAddButton.setVisibility(View.INVISIBLE);
		}
		
	}
	
	private boolean isExistSelectedContacts(){
		boolean result = false;
		for(int i = 0; i < mItems.size(); i++ ){
			UngroupListItem item = (UngroupListItem) mAdapter.getItem(i);
			if(item.isSelected){
				result = true;
				break;
			}
			
		}
		return result;
	}
	
	private void addSeletedFriendsToGroup(){
		TraxitDataManager data = AppSetting.traxitDataManager;
		if(data == null){
			finish();
			return;
		}
		List<PersonInfo> addedItems = new ArrayList<PersonInfo>();
		List<String> friends = new ArrayList<String>();
		for(int i = 0; i < mItems.size(); i++ ){
			UngroupListItem item = (UngroupListItem) mAdapter.getItem(i);
			if(item.isSelected){
				addedItems.add(item.personInfo);
				friends.add(item.personInfo.getEmail());
			}
			
		}
		
		if(addedItems.size() > 0){
			for(int i = 0 ; i < data.getGroups().size() ; i++){
				if(data.getGroups().get(i).getGroupName().equals(mGroupName)){
					data.getGroups().get(i).getFriends().addAll(addedItems);
				}
			}
			data.getUnGroup().removeAll(addedItems);
		}
		
		MemberActivity.addFriends(friends);
		finish();
		
		
	}
}
