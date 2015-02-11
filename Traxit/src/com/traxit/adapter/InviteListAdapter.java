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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.traxit.library.TextBitmap;
import com.traxit.main.R;
import com.traxit.widget.ContactInfo;

public class InviteListAdapter extends BaseAdapter  implements Filterable{
	
	private Context context;
	private List<InviteListItem> mItems;
	
	public InviteListAdapter(Context context, List<ContactInfo> items){
		this.context = context;

		this.mItems = new ArrayList<InviteListItem>();
		for(int i = 0; i<items.size(); i++){
			InviteListItem element = new InviteListItem();
			element.contact = items.get(i);
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
		InviteListHolder holder = null;
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.invite_list_item, null);
        }
        holder = new InviteListHolder();
        holder.atomObj = mItems.get(position);
        holder.imgIcon = (ImageView) convertView.findViewById(R.id.friendPhotoView);
        holder.lblTitle = (TextView) convertView.findViewById(R.id.lblCellTitle);
        holder.lblSubTitle = (TextView) convertView.findViewById(R.id.lblSubTitle);
        holder.imgCheck = (ImageView) convertView.findViewById(R.id.imgCheck);
        convertView.setTag(holder);
         
		setupItem(holder);
        return convertView;
	}
	
	private void setupItem(InviteListHolder holder){
        
        ContactInfo contactInfo = holder.atomObj.contact;
        if (contactInfo.getPhotoUri() != null && !contactInfo.getPhotoUri().equals("")) {
        	holder.imgIcon.setImageURI(Uri.parse(contactInfo.getPhotoUri()));
        }else if(contactInfo.getName() !=null && !contactInfo.getName().equals("")){
       	 	TextBitmap textBitmap = new TextBitmap();
       	 	holder.imgIcon.setImageBitmap(textBitmap.createBitmapWithText(contactInfo.getName())); 
        }
        
        int type = 0;
        if(contactInfo.getName()!=null && !contactInfo.getName().equals("")){
        	holder.lblTitle.setText(contactInfo.getName());
        }else if(contactInfo.getEmail().size()>0){
        	type = 1;
        	holder.lblTitle.setText(contactInfo.getEmail().get(0));
        }else{
        	type = 2;
        	holder.lblTitle.setText(contactInfo.getPhoneNumer());
        }
        
        if(type == 0){
        	if(contactInfo.getEmail().size()>0){
        		holder.lblSubTitle.setText(contactInfo.getEmail().get(0));
            }else{
            	holder.lblSubTitle.setText(contactInfo.getPhoneNumer());
            }
        }else if(type == 1){
        	holder.lblSubTitle.setText(contactInfo.getPhoneNumer());
        }
        
        if(holder.atomObj.isSelected){
        	holder.imgCheck.setVisibility(View.VISIBLE);
        }else{
        	holder.imgCheck.setVisibility(View.INVISIBLE);
        }
	}
	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class InviteListItem{
		public ContactInfo contact;
		public boolean isSelected;
	}
	public static class InviteListHolder {
		InviteListItem atomObj;
		
        ImageView imgIcon;
        TextView lblTitle;
        TextView lblSubTitle;
        public ImageView imgCheck;
	}
}
