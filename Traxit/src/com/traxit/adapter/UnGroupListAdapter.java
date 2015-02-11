package com.traxit.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.traxit.library.TextBitmap;
import com.traxit.main.R;
import com.traxit.widget.PersonInfo;

public class UnGroupListAdapter extends BaseAdapter{
	
	private Context context;
	private List<UngroupListItem> mItems;
	
	public UnGroupListAdapter(Context context, List<PersonInfo> items){
		this.context = context;

		this.mItems = new ArrayList<UngroupListItem>();
		for(int i = 0; i<items.size(); i++){
			UngroupListItem element = new UngroupListItem();
			element.personInfo = items.get(i);
			element.isSelected = false;
			this.mItems.add(element);
		}
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UngroupListHolder holder = null;
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.friend_add_list, null);
        }
        holder = new UngroupListHolder();
        holder.atomObj = mItems.get(position);
        holder.imgIcon = (ImageView) convertView.findViewById(R.id.friendPhotoView);
        holder.lblTitle = (TextView) convertView.findViewById(R.id.lblCellTitle);
        holder.imgCheck = (ImageView) convertView.findViewById(R.id.imgCheck);
        convertView.setTag(holder);
         
		setupItem(holder);
        return convertView;
	}
	
	private void setupItem(UngroupListHolder holder){
        
        PersonInfo person = holder.atomObj.personInfo;
        if(person.getPhoto() != null){
        	holder.imgIcon.setImageBitmap(person.getPhoto()); 	
        }else if (person.getPhotoUri() != null && !person.getPhotoUri().equals("")) {
        	holder.imgIcon.setImageURI(Uri.parse(person.getPhotoUri()));
        }else if(person.getName() !=null && !person.getName().equals("")){
       	 	TextBitmap textBitmap = new TextBitmap();
       	 	holder.imgIcon.setImageBitmap(textBitmap.createBitmapWithText(person.getName())); 
        }
 
        holder.lblTitle.setText(person.getName());
        
        if(holder.atomObj.isSelected){
        	holder.imgCheck.setVisibility(View.VISIBLE);
        }else{
        	holder.imgCheck.setVisibility(View.INVISIBLE);
        }
	}
	
	public class UngroupListItem{
		public PersonInfo personInfo;
		public boolean isSelected;
	}
	public static class UngroupListHolder {
		UngroupListItem atomObj;
        ImageView imgIcon;
        TextView lblTitle;
        public ImageView imgCheck;
	}
}
