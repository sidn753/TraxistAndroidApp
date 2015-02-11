package com.traxit.library;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.traxit.widget.CountryCodeObject;

public class CountryCode {
	Context mContext;
	String mJSON;
	public CountryCode(Context context){
		this.mContext = context;
		mJSON = null;
		loadJSONFromFile();
	}
	
	private void loadJSONFromFile(){
		try{
			InputStream is = mContext.getAssets().open("countries.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			mJSON = new String(buffer, "UTF-8");
			
			
//			Log.d("Country code JSON Data :" , String.valueOf(mJSON));
			
		} catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public List<CountryCodeObject> getCountryCodeList(){
		List<CountryCodeObject> result = new ArrayList<CountryCodeObject>();
		try {
			JSONObject jsonObject = new JSONObject(mJSON);
			JSONArray jsonArray = jsonObject.getJSONArray("codeList");
			for(int i = 0 ; i < jsonArray.length(); i++){
				JSONObject item = jsonArray.getJSONObject(i);
				//[{"name":"Afghanistan","dial_code":"+93","code":"AF"}
				String code = item.getString("code");
				String name = item.getString("name");
				String dialCode = item.getString("dial_code");
				
				CountryCodeObject oneCountry = new CountryCodeObject(code, name, dialCode);
				result.add(oneCountry);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return result;
	}
}
