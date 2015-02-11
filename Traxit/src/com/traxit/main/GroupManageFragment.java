package com.traxit.main;

import java.util.ArrayList;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.traxit.adapter.GroupListAdapter;
import com.traxit.common.AppSetting;
import com.traxit.common.Config;
import com.traxit.library.UserFunctions;
import com.traxit.widget.GroupInfo;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.ResultCode;
import com.traxit.widget.TraxitDataManager;

public class GroupManageFragment extends Fragment {
	
	ListView mListView;
	GroupListAdapter mAdapter;
	Context mContext;
	Activity mActivity;
	ProgressDialog mProgressDialog;
	private Button mTitleBarRightButton;
	
	public GroupManageFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.group_manage_fragment, container, false);
        mActivity = this.getActivity();
        mContext = this.getActivity().getApplicationContext();
        mListView = (ListView)rootView.findViewById(R.id.groupListView);
        
        /* if(AppSetting.traxitDataManager != null){
    		mAdapter = new GroupListAdapter(mContext, AppSetting.traxitDataManager, this);
    		mListView.setAdapter(mAdapter);
        }*/
        
		mTitleBarRightButton = (Button)mActivity.findViewById(R.id.titlebarRightImageButton);
		mTitleBarRightButton.setVisibility(View.VISIBLE);
		mTitleBarRightButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showAddNewGroupDialog();
			}
		});
        return rootView;
    }
	@Override
	public void onResume(){
		super.onResume();
		if(AppSetting.traxitDataManager != null ){
    		mAdapter = new GroupListAdapter(mContext, AppSetting.traxitDataManager, this);
    		mListView.setAdapter(mAdapter);
		}
	}
	public void changeGroupName(final GroupInfo groupInfo){
    	AlertDialog.Builder customField = new AlertDialog.Builder(mActivity);
    	customField.setTitle("Change group name");
        LayoutInflater customInflater = mActivity.getLayoutInflater();
        final View customView=customInflater.inflate(R.layout.manual_invite, null);
        TextView lblTitle = (TextView)customView.findViewById(R.id.record_field_name);
        lblTitle.setText(this.getResources().getString(R.string.change_group_dialog_title));
        customField.setView(customView)
               .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   String name =((EditText) customView.findViewById(R.id.manualInviteName)).getText().toString();
                	   changeGroupName(groupInfo, name);
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
	private void changeGroupName(final GroupInfo groupInfo, final String newGroupName){
		//NSDictionary * parameters = @{@"tag":@"changeGroupName", @"email":email, @"name":originalName, @"newName":newGroupName};
		final String originalName = groupInfo.getGroupName();
		final String email = AppSetting.getUserEmail(mActivity);
		mProgressDialog = ProgressDialog.show(mActivity, "",
				"updating group name...", true);
		AsyncTask<Void, Void, ResultCode> mTask = new AsyncTask<Void, Void, ResultCode>() {

			@Override
			protected ResultCode doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.changeGroupName(email, originalName, newGroupName);
					
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
					groupInfo.setGroupName(newGroupName);
					mAdapter.notifyDataSetChanged();
				}else{
					Toast.makeText(mContext, "Update fail.", Toast.LENGTH_LONG).show();
				}
				super.onPostExecute(result);
			}

		};
		
		// execute AsyncTask
		mTask.execute(null, null, null);
	}
	
	private void showAddNewGroupDialog(){
    	AlertDialog.Builder customField = new AlertDialog.Builder(mActivity);
    	customField.setTitle("Add a new group");
        LayoutInflater customInflater = mActivity.getLayoutInflater();
        final View customView=customInflater.inflate(R.layout.manual_invite, null);
        TextView lblTitle = (TextView)customView.findViewById(R.id.record_field_name);
        lblTitle.setText(this.getResources().getString(R.string.add_group_dialog_title));
        customField.setView(customView)
               .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   String name =((EditText) customView.findViewById(R.id.manualInviteName)).getText().toString();
                	   addNewGroup(name);
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
	
	private void addNewGroup(final String newGroupName){
		
		//NSDictionary * parameters = @{@"tag":@"addNewGroup", @"email":email, @"name":newGroupName};
		final String email = AppSetting.getUserEmail(mActivity);
		mProgressDialog = ProgressDialog.show(mActivity, "",
				"Add new group...", true);
		AsyncTask<Void, Void, JSONObject> mTask = new AsyncTask<Void, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.addNewGroupItem(email, newGroupName);

				if(json!=null){
					return json; 
				}
				return null;
			}

			@Override
			protected void onPostExecute(JSONObject result) {
				mProgressDialog.dismiss();
				if(result != null){
					int resultCode;
					try {
						resultCode = result.getInt("result");
				        
						if(resultCode == 0) {
							double groupId = result.getDouble("groupId");
							GroupInfo newGroup = new GroupInfo(groupId, 0, newGroupName, new ArrayList<PersonInfo>());
							AppSetting.traxitDataManager.getGroups().add(newGroup);
				    		mAdapter = new GroupListAdapter(mContext, AppSetting.traxitDataManager, GroupManageFragment.this);
				    		mListView.setAdapter(mAdapter);
						}else{
							Toast.makeText(mContext, "Fail to add new group.", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				super.onPostExecute(result);
			}

		};
		
		// execute AsyncTask
		mTask.execute(null, null, null);
	}
	
	
	//  Delete group item
	public void showDeleteGroupDialog(final GroupInfo groupInfo){
    	AlertDialog.Builder customField = new AlertDialog.Builder(mActivity);
    	customField.setTitle("Delete group");
    	customField.setMessage("Are you sure to delete '"+ groupInfo.getGroupName() + "' group item?");
        customField.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   deleteGroup(groupInfo);
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
	private void deleteGroup(final GroupInfo groupInfo){
		//NSDictionary * parameters = @{@"tag":@"removeGroup", @"email":email, @"name":groupName};
		final String email = AppSetting.getUserEmail(mActivity);
		final String groupName = groupInfo.getGroupName();
		mProgressDialog = ProgressDialog.show(mActivity, "",
				"updating group name...", true);
		AsyncTask<Void, Void, ResultCode> mTask = new AsyncTask<Void, Void, ResultCode>() {

			@Override
			protected ResultCode doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.deleteGroup(email, groupName);
					
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
	
					TraxitDataManager data = AppSetting.traxitDataManager;
					data.getUnGroup().addAll(groupInfo.getFriends());
					for(int i = 0 ; i < data.getGroups().size() ; i++ ){
						if(data.getGroups().get(i).getGroupName().equals(groupInfo.getGroupName())){
							data.getGroups().remove(i);
							break;
						}
					}
		    		mAdapter = new GroupListAdapter(mContext, AppSetting.traxitDataManager, GroupManageFragment.this);
		    		mListView.setAdapter(mAdapter);
				}else{
					Toast.makeText(mContext, "Update fail.", Toast.LENGTH_LONG).show();
				}
				super.onPostExecute(result);
			}

		};
		
		// execute AsyncTask		
		mTask.execute(null, null, null);
	}
	
	public void openMemberPage(String groupName){
		Intent i = new Intent(mContext,	MemberActivity.class);
		i.putExtra(Config.GroupName, groupName);
		startActivity(i);
	}
}
