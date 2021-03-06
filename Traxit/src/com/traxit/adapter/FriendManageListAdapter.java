package com.traxit.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.traxit.library.TextBitmap;
import com.traxit.main.FriendManageFragment;
import com.traxit.main.R;
import com.traxit.widget.PersonInfo;

public class FriendManageListAdapter extends ArrayAdapter<PersonInfo> implements Filterable{

	private Context mContext;
	private List<PersonInfo> mRecordList;
	private List<PersonInfo> mOriginalValues;
	private int mLayout;
	private FriendManageFragment mParent;
	
	public FriendManageListAdapter(Context context, List<PersonInfo> itemList, FriendManageFragment parent) {
		super(context, R.layout.friend_manage_list_item, itemList);
		mLayout = R.layout.friend_manage_list_item;
		this.mContext = context;
		this.mRecordList = itemList;
		this.mParent = parent;
	}
	public int getCount() {
		return mRecordList.size();
	}

	public PersonInfo getItem(int position) {
		return mRecordList.get(position);
	}

	public long getItemId(int position) {
		return mRecordList.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(mLayout, null);

		
		final PersonInfo personItem = mRecordList.get(position);

		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.friendPhotoView);
        TextView lblTitle = (TextView) convertView.findViewById(R.id.lblCellTitle);
        TextView lblSubTitle = (TextView) convertView.findViewById(R.id.lblSubTitle);
        final Button btnDelete = (Button) convertView.findViewById(R.id.btnDeleteGroup);
        btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mParent.deleteFriend(personItem);
			}
		});
        
        RelativeLayout contentView = (RelativeLayout) convertView.findViewById(R.id.contentView);
        contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(btnDelete.getVisibility() == View.GONE){
					btnDelete.setVisibility(View.VISIBLE);
				}else{
					btnDelete.setVisibility(View.GONE);
				}
			}
		});
        if(personItem.getPhoto() != null){
            imgIcon.setImageBitmap(personItem.getPhoto()); 	
        }else if(personItem.getPhotoUri() != null && !personItem.getPhotoUri().equals("") ){
        	imgIcon.setImageURI(Uri.parse(personItem.getPhotoUri()));
        }else{
        	 TextBitmap textBitmap = new TextBitmap();
        	 imgIcon.setImageBitmap(textBitmap.createBitmapWithText(personItem.getName())); 
        }
 
        lblTitle.setText(personItem.getName());
        String address = personItem.getAddress();
        String distance = personItem.getDistance();
        String subTitle = "";
        if(distance !=null && !distance.isEmpty()){
        	subTitle = distance;
        }
        if (address !=null && !address.isEmpty()) {
        	subTitle += " " +  address;
        }
        lblSubTitle.setText(subTitle);
        
		return convertView;
		
	}

	
	@SuppressLint("DefaultLocale")
	@Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

            	mRecordList = (List<PersonInfo>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @SuppressLint("DefaultLocale")
			@Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<PersonInfo> FilteredArrList = new ArrayList<PersonInfo>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<PersonInfo>(mRecordList); // saves the original data in mOriginalValues
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
                    	PersonInfo record = mOriginalValues.get(i);
                    	String name = record.getName();
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


}
