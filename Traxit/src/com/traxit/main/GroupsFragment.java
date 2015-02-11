package com.traxit.main;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.traxit.adapter.GroupListAdapter;
import com.traxit.widget.SegmentedRadioGroup;

public class GroupsFragment extends Fragment implements OnCheckedChangeListener {
	
	ListView mListView;
	GroupListAdapter mAdapter;
	Context mContext;
	Activity mActivity;
	ProgressDialog mProgressDialog;
	private SegmentedRadioGroup mTitleBarSegmentButton;
	
	public GroupsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);
        mActivity = this.getActivity();
        mContext = this.getActivity().getApplicationContext();
        
        mTitleBarSegmentButton = (SegmentedRadioGroup) mActivity.findViewById(R.id.titlebarSegmentButton);
        mTitleBarSegmentButton.setVisibility(View.VISIBLE);
        
        RadioButton firstButton = (RadioButton) mActivity.findViewById(R.id.segGroup);
        firstButton.setText(this.getResources().getString(R.string.groups));
        RadioButton secondButton = (RadioButton) mActivity.findViewById(R.id.segFriend);
        secondButton.setText(this.getResources().getString(R.string.friends));

        mTitleBarSegmentButton.setOnCheckedChangeListener(this);
        mTitleBarSegmentButton.check(R.id.segGroup);
        openGroupManagerPage();
        return rootView;
    }
	
	@Override
	public void onResume(){
		super.onResume();
	}
	private void openGroupManagerPage(){
		Fragment fragment = new GroupManageFragment();
		FragmentManager fragmentManager =getActivity().getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.group_page_container, fragment).commit();
	}
	
	private void openFriendManagePage(){
		Fragment fragment = new FriendManageFragment();
		FragmentManager fragmentManager =getActivity().getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.group_page_container, fragment).commit();
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checked) {	
		if(checked == R.id.segGroup){
			openGroupManagerPage();
		}else{
			openFriendManagePage();
		}
		
	}

	
}
