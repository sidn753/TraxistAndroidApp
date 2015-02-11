package com.traxit.main;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.traxit.adapter.CountryCodeListAdapter;
import com.traxit.library.CountryCode;
import com.traxit.widget.CountryCodeObject;


public class CountryCodeActivity extends Activity {

	Context mContext;
	ImageView mSearchIcon;
	ImageView mSearchCancelIcon;
	EditText mSearchBox;
	
	ListView mListView;
	CountryCodeListAdapter mAdapter;
	List<CountryCodeObject> mCountryList;
	boolean isProfilePage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_code);

		mContext = this;
		this.getActionBar().setDisplayUseLogoEnabled(false);
		this.getActionBar().setDisplayShowHomeEnabled(false);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
  		Intent i = getIntent();
  		isProfilePage = i.getBooleanExtra("profile", false);
  		
		mListView = (ListView)findViewById(R.id.countryCodeList);
		
		CountryCode countryCode = new CountryCode(mContext);
		mCountryList = countryCode.getCountryCodeList();
		
		mAdapter = new CountryCodeListAdapter(mContext, mCountryList);
		mListView.setAdapter(mAdapter);
		
		
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				CountryCodeObject selectedCountry = mAdapter.getItem(position);
				String code = selectedCountry.getCountryCode();
				String dialCode = selectedCountry.getCallingCode();
				if(isProfilePage){
					MainActivity.setCallingCode(code, dialCode);
					
					Log.d("country code : ", String.valueOf(mAdapter.getItem(position).getCountryCode()));
					Log.d("contry callingcode : ", String.valueOf(mAdapter.getItem(position).getCallingCode()));
					
					
				}else{
					RegisterActivity.setCallingCode(code, dialCode);	
				}
				finish();
			}
			
		});
		
		initializeSearchBox();
	
	}
	private void initializeSearchBox() {

		mSearchIcon = (ImageView) findViewById(R.id.search_icon);
		mSearchCancelIcon = (ImageView)findViewById(R.id.search_cancel);

		mSearchBox = (EditText)findViewById(R.id.search_text);
		mSearchBox.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            	if(cs.length()>0){
            		if(mSearchIcon.getVisibility() == View.VISIBLE){
            			mSearchIcon.setVisibility(View.INVISIBLE);
            			mSearchCancelIcon.setVisibility(View.VISIBLE);
            			mSearchBox.setPadding(10, 0, 0, 0);
            		}
                	if(mCountryList.size()>0){
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
				mSearchBox.setPadding(70, 0, 0, 0);
			}
		});
	}
}
