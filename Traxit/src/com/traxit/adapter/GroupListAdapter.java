package com.traxit.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.traxit.common.Config.FriendCellType;
import com.traxit.library.TextBitmap;
import com.traxit.main.GroupManageFragment;
import com.traxit.main.R;
import com.traxit.widget.FriendListItem;
import com.traxit.widget.GroupInfo;
import com.traxit.widget.PersonInfo;
import com.traxit.widget.TraxitDataManager;


public class GroupListAdapter extends BaseAdapter {

	private Context _context;
	private List<FriendListItem> mListItem;
	private GroupManageFragment mParent;


	public GroupListAdapter(Context context, TraxitDataManager dataManager, GroupManageFragment parent) {
		this._context = context;
		this.mParent = parent;
		
		 List<GroupInfo> listGroups  = dataManager.getGroups();
		 List<PersonInfo> listUnGroups = dataManager.getUnGroup();
		 mListItem = new ArrayList<FriendListItem>();
		
		//--------Add Group List--------------
		FriendListItem groupSection = new FriendListItem("Groups");
		mListItem.add(groupSection);
		for(int i = 0 ; i < listGroups.size(); i++){
			FriendListItem item = new FriendListItem(listGroups.get(i));
			mListItem.add(item);
		}
		
		//-------Add UnGroup List--------
		if(listUnGroups.size() > 0){
			FriendListItem unGroupSection = new FriendListItem("Not in Group");
			mListItem.add(unGroupSection);
			for(int i = 0; i < listUnGroups.size(); i++){
				FriendListItem  item = new FriendListItem(listUnGroups.get(i));
				mListItem.add(item);
			}	
		}

	}

	@Override
	public int getCount() {
		return mListItem.size();
	}

	@Override
	public Object getItem(int position) {
		return mListItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		FriendListItem item = (FriendListItem) getItem(position);
		if(item.getCellType() == FriendCellType.sectionHeader){

			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.section_header, null);

			TextView lblCellTitle = (TextView) convertView
					.findViewById(R.id.lblCellTitle);
			lblCellTitle.setText(item.getSectionTitle());
			convertView.setOnClickListener(null);
			
		}else if(item.getCellType() == FriendCellType.groupCell){
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.group_list_item, null);
			
			GroupHolder holder = new GroupHolder();
			holder.atomObj = item.getGroupInfo();
			holder.lblGroupName = (TextView) convertView
					.findViewById(R.id.lblListHeader);
			holder.lblCounts = (TextView) convertView.findViewById(R.id.lblGroupCount);
			holder.contentView = (RelativeLayout) convertView.findViewById(R.id.groupItemContentView);
			holder.btnShowMember = (Button)convertView.findViewById(R.id.btnShowMember);
			holder.btnChangeGroupName = (Button)convertView.findViewById(R.id.btnChangeGroupName);
			holder.btnDeleteGroup = (Button)convertView.findViewById(R.id.btnDeleteGroup);
			holder.lblTail = (TextView) convertView.findViewById(R.id.lblContentTail);
			setupItem(holder);
			
		}else if(item.getCellType() == FriendCellType.ungroupCell){
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.ungroup_list_item, null);

			final PersonInfo personItem = item.getPersonInfo();
	        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.friendPhotoView);
	        TextView lblTitle = (TextView) convertView.findViewById(R.id.lblCellTitle);

	        if(personItem.getPhoto() != null){
	            imgIcon.setImageBitmap(personItem.getPhoto()); 	
	        }else{
	        	 TextBitmap textBitmap = new TextBitmap();
	        	 imgIcon.setImageBitmap(textBitmap.createBitmapWithText(personItem.getName()));
	        }        
	        lblTitle.setText(personItem.getName());
	        
		}
		return convertView;
	}
	
	public static class GroupHolder {
		GroupInfo atomObj;
		TextView lblGroupName;
		TextView lblCounts;
		TextView lblTail;
		boolean isOpened;
		RelativeLayout contentView;
		Button btnShowMember;
		Button btnChangeGroupName;
		Button btnDeleteGroup;
		
	}
	
	private void setupItem(final GroupHolder holder){
		holder.lblGroupName.setText(holder.atomObj.getGroupName());
		holder.lblCounts.setText(String.valueOf(holder.atomObj.getFriends().size()));
		holder.lblTail.setText(String.valueOf(holder.atomObj.getFriends().size()));
		
		holder.contentView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				/*
				 * TranslateAnimation anim;
				if(holder.isOpened){
					anim = new TranslateAnimation(100f, 0f, 0f,  0f);
					holder.isOpened = false;
				}else{
					anim = new TranslateAnimation(-100f, 0f, 0f,  0f);
					holder.isOpened = true;
				}
				anim.setDuration(1000);
				holder.contentView.setAnimation(anim);
				*/
				holder.contentView.setVisibility(View.GONE);
			}
		});
		holder.lblTail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				holder.contentView.setVisibility(View.VISIBLE);
			}
		});
		holder.btnShowMember.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mParent.openMemberPage(holder.atomObj.getGroupName());
				holder.contentView.setVisibility(View.VISIBLE);
			}
		});
		holder.btnChangeGroupName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mParent.changeGroupName(holder.atomObj);
				holder.contentView.setVisibility(View.VISIBLE);
			}
		});
		holder.btnDeleteGroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mParent.showDeleteGroupDialog(holder.atomObj);
				holder.contentView.setVisibility(View.VISIBLE);
			}
		});
	}
}
