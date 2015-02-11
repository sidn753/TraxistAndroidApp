package com.traxit.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.traxit.library.TextBitmap;
import com.traxit.main.R;
import com.traxit.widget.PersonInfo;

public class MemberListAdapter extends BaseAdapter {

	List<PersonInfo> mItems;
	Context mContext;
	public MemberListAdapter(Context context, List<PersonInfo> items) {
		mItems = items;
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater infalInflater = (LayoutInflater) this.mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.ungroup_list_item, null);

		final PersonInfo personItem = (PersonInfo)getItem(position);
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.friendPhotoView);
        TextView lblTitle = (TextView) convertView.findViewById(R.id.lblCellTitle);

        if(personItem.getPhoto() != null){
            imgIcon.setImageBitmap(personItem.getPhoto()); 	
        }else{
        	 TextBitmap textBitmap = new TextBitmap();
        	 imgIcon.setImageBitmap(textBitmap.createBitmapWithText(personItem.getName()));
        }        
        lblTitle.setText(personItem.getName());
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mItems.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
