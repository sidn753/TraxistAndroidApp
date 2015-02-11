package com.traxit.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.traxit.main.R;
import com.traxit.widget.CountryCodeObject;

public class CountryCodeListAdapter extends ArrayAdapter<CountryCodeObject> implements Filterable{

	private Context mContext;
	private List<CountryCodeObject> mRecordList;
	private List<CountryCodeObject> mOriginalValues;
	private int mLayout;
	public CountryCodeListAdapter(Context context, List<CountryCodeObject> itemList	) {
		super(context, R.layout.country_code_list_item, itemList);
		mLayout = R.layout.country_code_list_item;
		this.mContext = context;
		this.mRecordList = itemList;
	}
	public int getCount() {
		return mRecordList.size();
	}

	public CountryCodeObject getItem(int position) {
		return mRecordList.get(position);
	}

	public long getItemId(int position) {
		return mRecordList.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		RecordHolder holder = null;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(mLayout, null);
			
			holder = new RecordHolder();		
			holder.flag = (ImageView) v.findViewById(R.id.flagImageView);
			holder.countryName = (TextView) v.findViewById(R.id.countryNameLabel);
			holder.callingCode = (TextView)v.findViewById(R.id.countryCodeLabel);
			v.setTag(holder);
		}else{
			holder = (RecordHolder) v.getTag();
		}
		
		CountryCodeObject item = mRecordList.get(position);
		String code = item.getCountryCode();
		//String flagName = code + ".png";
		//int resID = mContext.getResources().getIdentifier(flagName, "drawable", mContext.getPackageName());
		//holder.flag.setImageResource(resID);
		holder.flag.setImageBitmap(getBitmapFromAsset(code));
		holder.countryName.setText(item.getCountryName());
		holder.callingCode.setText(item.getCallingCode());
		return v;
		
	}

	
	@SuppressLint("DefaultLocale")
	@Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

            	mRecordList = (List<CountryCodeObject>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @SuppressLint("DefaultLocale")
			@Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<CountryCodeObject> FilteredArrList = new ArrayList<CountryCodeObject>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<CountryCodeObject>(mRecordList); // saves the original data in mOriginalValues
                }

                /********
                 * 
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)  
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return  
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                    	CountryCodeObject record = mOriginalValues.get(i);
                    	String name = record.getCountryName();
                        if (name.toLowerCase().startsWith(constraint.toString()) ) {
                            FilteredArrList.add(record);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
	
	public static class RecordHolder {
		ImageView flag;
		TextView countryName;
		TextView callingCode;
	}
	
	private Bitmap getBitmapFromAsset(String code){
		AssetManager assetManager = mContext.getAssets();
		InputStream is = null;
		try{
			String flagName = "Flags/" +code + ".png";
			is = assetManager.open(flagName);
		}catch (IOException ex){
			ex.printStackTrace();
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		return bitmap;
	}

}
