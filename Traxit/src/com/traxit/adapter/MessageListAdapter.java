package com.traxit.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.traxit.main.R;
import com.traxit.widget.MessageInfo;

public class MessageListAdapter extends BaseAdapter{
	private Context mContext;
	private List<MessageInfo>mItems;
	private Bitmap mFriendPhoto;
	private Bitmap mMyPhoto;
	public MessageListAdapter(Context context, List<MessageInfo> items, Bitmap myPhoto, Bitmap friendPhoto) {
		this.mContext = context;
		this.mItems = items;
		this.mMyPhoto = myPhoto;
		this.mFriendPhoto = friendPhoto;
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
		View row = convertView;
		MessageInfo message = mItems.get(position);
		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		if(message.getIsMine()){

				
			row = inflater.inflate(R.layout.my_message_item, parent, false);

	        ImageView imgIcon = (ImageView) row.findViewById(R.id.friendPhotoView);
	        TextView lblTime = (TextView) row.findViewById(R.id.messageTimeLabel);
	        TextView lblMessage = (TextView) row.findViewById(R.id.messageLabel);
	        imgIcon.setImageBitmap(mMyPhoto);
	        lblTime.setText(message.getTime());
	        lblMessage.setText(message.getMessage());
		}else{


			row = inflater.inflate(R.layout.friend_message_item, parent, false);

	        ImageView imgIcon = (ImageView) row.findViewById(R.id.friendPhotoView);
	        TextView lblTime = (TextView) row.findViewById(R.id.messageFriendTimeLabel);
	        TextView lblMessage = (TextView) row.findViewById(R.id.friendMessageLabel);
	        if(mFriendPhoto !=null){
	        	imgIcon.setImageBitmap(mFriendPhoto);
	        }
	        lblTime.setText(message.getTime());
	        lblMessage.setText(message.getMessage());
		}

		return row;
	}
	
	@Override
	public boolean areAllItemsEnabled(){
		return false;
	}
	@Override
	public boolean isEnabled(int position){
		return false;
	}

}
