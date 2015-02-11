package com.traxit.main;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.traxit.adapter.FriendManageListAdapter;
import com.traxit.common.AppSetting;
import com.traxit.library.UserFunctions;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.ResultCode;

public class RecentMessageFragment extends Fragment {
	
	private ListView mListView;
	private List<PersonInfo> mItems;
	private FriendManageListAdapter mAdapter;
	private Context mContext;
	private Activity mActivity;
	private ProgressDialog mProgressDialog;
	private Button mTitleBarRightButton;
	
	
	private EditText mSearchBox;
	private ImageView mSearchIcon;
	private ImageView mSearchCancelIcon;
	
	public RecentMessageFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.friend_manage_fragment, container, false);
        mActivity = this.getActivity();
        mContext = this.getActivity().getApplicationContext();
        mTitleBarRightButton = (Button)mActivity.findViewById(R.id.titlebarRightImageButton);
        mTitleBarRightButton.setVisibility(View.GONE);
        mListView = (ListView)rootView.findViewById(R.id.friendList);
        mItems = AppSetting.getFriends();
        //mAdapter = new FriendManageListAdapter(mContext, mItems, this);
        //mListView.setAdapter(mAdapter);
        
		mSearchIcon = (ImageView) rootView.findViewById(R.id.search_icon);
		mSearchCancelIcon = (ImageView)rootView.findViewById(R.id.search_cancel);
		mSearchBox = (EditText)rootView.findViewById(R.id.search_text);
		initializeSearchBox();
		
        return rootView;
    }
	
	public void deleteFriend(final PersonInfo person){
    	AlertDialog.Builder customField = new AlertDialog.Builder(mActivity);
    	customField.setTitle("Disconnect");
    	customField.setMessage("Are you sure to remove '"+ person.getName() + "' from contacts?");
        customField.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   removeFriendFromContacts(person);
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
	
	private void removeFriendFromContacts(final PersonInfo person){
		final String email = AppSetting.getUserEmail(mActivity);
		final String friend = person.getName();
		mProgressDialog = ProgressDialog.show(mActivity, "",
				"Remove friend from contats...", true);
		AsyncTask<Void, Void, ResultCode> mTask = new AsyncTask<Void, Void, ResultCode>() {

			@Override
			protected ResultCode doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.deleteFriend(email, friend);
					
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
					AppSetting.removeFriend(person.getEmail());
					mItems.remove(person);
					mAdapter.notifyDataSetChanged();
				}else{
					Toast.makeText(mContext, " fail to remove friend from contacts.", Toast.LENGTH_LONG).show();
				}
				super.onPostExecute(result);
			}

		};
		
		// execute AsyncTask		
		mTask.execute(null, null, null);
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
                	if(mItems.size()>0){
                		mAdapter.getFilter().filter(cs);
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
				mSearchBox.setPadding(100, 0, 0, 0);
			}
		});
	}
}
