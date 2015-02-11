package com.traxit.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.traxit.common.Config;
import com.traxit.common.Config.FriendCellType;
import com.traxit.library.TextBitmap;
import com.traxit.main.FriendListFragment;
import com.traxit.main.MessageActivity;
import com.traxit.main.R;
import com.traxit.widget.FriendListItem;
import com.traxit.widget.GroupInfo;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.TraxitDataManager;


public class FriendListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<FriendListItem> mListItem;
	private FriendListFragment mParent;


	public FriendListAdapter(Context context, TraxitDataManager dataManager, FriendListFragment parent) {
		this._context = context;
		this.mParent = parent;
		
		 List<GroupInfo> listGroups  = dataManager.getGroups();
		 List<PersonInfo> listUnGroups = dataManager.getUnGroup();
		 List<PersonInfo> listRequests = dataManager.getRequests();
		 mListItem = new ArrayList<FriendListItem>();
		
		//--------Add Group List--------------
		FriendListItem groupSection = new FriendListItem("Group");
		mListItem.add(groupSection);
		for(int i = 0 ; i < listGroups.size(); i++){
			FriendListItem item = new FriendListItem(listGroups.get(i));
			mListItem.add(item);
		}
		
		//-------Add UnGroup List--------
		if(listUnGroups.size() > 0){
			FriendListItem unGroupSection = new FriendListItem(" ");
			mListItem.add(unGroupSection);
			for(int i = 0; i < listUnGroups.size(); i++){
				FriendListItem  item = new FriendListItem(listUnGroups.get(i));
				mListItem.add(item);
			}	
		}


		//-------Add Request List-----------------
		if(listRequests.size() > 0){ 
			FriendListItem requestSection = new FriendListItem("Traxit Requests");
			mListItem.add(requestSection);
			for(int i = 0; i < listRequests.size(); i++){
				FriendListItem  item = new FriendListItem(listRequests.get(i), FriendCellType.requestCell);
				mListItem.add(item);
			}
		}

	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this.mListItem.get(groupPosition).getGroupInfo().getFriends().get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView( int groupPosition,  int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.friend_list_item, null);
		}
		final PersonInfo personItem = (PersonInfo) getChild(groupPosition, childPosition);

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.friendPhotoView);
        TextView lblTitle = (TextView) convertView.findViewById(R.id.lblCellTitle);
        TextView lblSubTitle = (TextView) convertView.findViewById(R.id.lblSubTitle);
        final TextView lblMessageCount = (TextView) convertView.findViewById(R.id.lblMessageCount);
        ImageView imgMessage = (ImageView) convertView.findViewById(R.id.imgMessage);
        imgMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Activity activity = (Activity)_context;
				//((MainActivity)activity).setDestroyMapFramgment(false);
				personItem.setMessageCount(0);
				lblMessageCount.setVisibility(View.INVISIBLE);
				Intent i = new Intent(_context,
						MessageActivity.class);
				i.putExtra(Config.FriendEmail, personItem.getEmail());
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				_context.startActivity(i);
				
			}
		});
        
        imgMessage.setTag(personItem);
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
        
        if (personItem.getMessageCount() == 0) {
        	lblMessageCount.setVisibility(View.INVISIBLE);
        }else{
        	lblMessageCount.setVisibility(View.VISIBLE);
        	lblMessageCount.setText(String.valueOf(personItem.getMessageCount()));
        }
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		//if(mListItem.size() <= groupPosition) return 0;
		
		FriendListItem item = this.mListItem.get(groupPosition);
		if(item.getCellType() == FriendCellType.groupCell){
			return item.getGroupInfo().getFriends().size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.mListItem.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.mListItem.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		FriendListItem item = (FriendListItem) getGroup(groupPosition);
		if(item.getCellType() == FriendCellType.sectionHeader){

			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.section_header, null);

			TextView lblCellTitle = (TextView) convertView
					.findViewById(R.id.lblCellTitle);
			lblCellTitle.setText(item.getSectionTitle());
			convertView.setOnClickListener(null);
			
		}else if(item.getCellType() == FriendCellType.groupCell){
			//if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_group, null);
			//}
			TextView lblListHeader = (TextView) convertView
					.findViewById(R.id.lblListHeader);
			TextView lblGroupCount = (TextView) convertView.findViewById(R.id.lblGroupCount);
			ImageView expandedIndicator = (ImageView)convertView.findViewById(R.id.expandableListViewIcon);
						
			GroupInfo groupItem = item.getGroupInfo();
			if(groupItem.getFriends().size() > 0){
				expandedIndicator.setVisibility(View.VISIBLE);
				if(item.isExpanded){
					expandedIndicator.setImageResource(R.drawable.expandable_image_up);
				}else{
					expandedIndicator.setImageResource(R.drawable.expandable_image);
				}
			}else{
				expandedIndicator.setVisibility(View.INVISIBLE);
			}
			
			lblListHeader.setText(groupItem.getGroupName());
			int count  = groupItem.getFriends().size();
			lblGroupCount.setText(String.valueOf(count));
			
		}else if(item.getCellType() == FriendCellType.ungroupCell){
			//if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.friend_list_item, null);
			//}

			final PersonInfo personItem = item.getPersonInfo();
	        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.friendPhotoView);
	        TextView lblTitle = (TextView) convertView.findViewById(R.id.lblCellTitle);
	        TextView lblSubTitle = (TextView) convertView.findViewById(R.id.lblSubTitle);
	        final TextView lblMessageCount = (TextView) convertView.findViewById(R.id.lblMessageCount);

	        ImageView imgMessage = (ImageView) convertView.findViewById(R.id.imgMessage);
	        imgMessage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//Activity activity = (Activity)_context;
					//((MainActivity)activity).setDestroyMapFramgment(false);
					personItem.setMessageCount(0);
					lblMessageCount.setVisibility(View.INVISIBLE);
					Intent i = new Intent(_context,
							MessageActivity.class);
					i.putExtra(Config.FriendEmail, personItem.getEmail());
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					_context.startActivity(i);
					
				}
			});
	        
	        
	         
	        if(personItem.getPhoto() != null){
	            imgIcon.setImageBitmap(personItem.getPhoto()); 	
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

        
	        if (personItem.getMessageCount() == 0) {
	        	lblMessageCount.setVisibility(View.INVISIBLE);
	        }else{
	        	lblMessageCount.setVisibility(View.VISIBLE);
	        	lblMessageCount.setText(String.valueOf(personItem.getMessageCount()));
	        }
		}else{

			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.reqeust_list_item, null);
			

			ImageView imgIcon = (ImageView) convertView.findViewById(R.id.friendPhotoView);
			TextView lblTitle = (TextView) convertView.findViewById(R.id.lblCellTitle);
			Button acceptButton = (Button) convertView.findViewById(R.id.btnAccept);
			Button declineButton = (Button) convertView.findViewById(R.id.btnDecline);
			
			final PersonInfo personInfo = item.getPersonInfo();
			acceptButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mParent.acceptInviteRequestFrom(personInfo.getEmail());
				}
			});
			declineButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mParent.acceptInviteRequestFrom(personInfo.getEmail());
				}
			});
			
	        if(personInfo.getPhoto() != null){
	        	imgIcon.setImageBitmap(personInfo.getPhoto()); 	
	        }else if(personInfo.getPhotoUri() != null && !personInfo.getPhotoUri().equals("") ){
	        	imgIcon.setImageURI(Uri.parse(personInfo.getPhotoUri()));
	        }else{
	        	 TextBitmap textBitmap = new TextBitmap();
	        	 Bitmap bitMap = textBitmap.createBitmapWithText(personInfo.getName());
	        	 imgIcon.setImageBitmap(bitMap);
	        }
	        
	        String name = personInfo.getName();
	        if(name== null || name.equals("")){
	        	lblTitle.setText(personInfo.getEmail());	
	        }else{
	        	lblTitle.setText(name);	
	        }
	        

		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public static class RequestHolder {
		PersonInfo atomObj;
		ImageView imgIcon;
        TextView lblTitle;
        public Button acceptButton;
        public Button declineButton;
        public boolean isOpen;
        
	}
}
