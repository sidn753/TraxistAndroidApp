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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.traxit.widget.SegmentedRadioGroup;

public class MessageFragment extends Fragment implements OnCheckedChangeListener {
	Context mContext;
	Activity mActivity;
	ProgressDialog mProgressDialog;
	private SegmentedRadioGroup mTitleBarSegmentButton;
	
	public MessageFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        mActivity = this.getActivity();
        mContext = this.getActivity().getApplicationContext();
        
        mTitleBarSegmentButton = (SegmentedRadioGroup) mActivity.findViewById(R.id.titlebarSegmentButton);
        mTitleBarSegmentButton.setVisibility(View.VISIBLE);
        
        RadioButton firstButton = (RadioButton) mActivity.findViewById(R.id.segGroup);
        firstButton.setText("   List   ");
        RadioButton secondButton = (RadioButton) mActivity.findViewById(R.id.segFriend);
        secondButton.setText(" Message ");
        

        mTitleBarSegmentButton.setOnCheckedChangeListener(this);
        mTitleBarSegmentButton.check(R.id.segGroup);
        openMessageManagerPage();
        return rootView;
    }

	@Override
	public void onCheckedChanged(RadioGroup group, int checked) {	
		if(checked == R.id.segGroup){
			openMessageManagerPage();
		}else{
			openRecentMessagePage();
		}
		
	}
	
	private void openMessageManagerPage(){
		Fragment fragment = new MessageManageFragment();
		FragmentManager fragmentManager =getActivity().getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.message_page_container, fragment).commit();
	}
	
	private void openRecentMessagePage(){
		Fragment fragment = new RecentMessageFragment();
		FragmentManager fragmentManager =getActivity().getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.message_page_container, fragment).commit();
	}
}
