package com.android.intercom.adapter;

import java.util.List;

import com.android.intercom.object.GroupObject;
import com.android.intercom.IntercomApplication;
import com.android.intercom.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {

	private final String TAG = "GroupAdapte";
	private Context mContext;
	private List<GroupObject> groupList;
	private List<GroupObject> activeGroupList;
	private IntercomApplication intercomApp;
		
	public GroupAdapter(Context context, List<GroupObject> groupList,List<GroupObject> activeGroupList) {
		// TODO Auto-generated constructor stub
		mContext = context;
		this.groupList = groupList;
		this.activeGroupList = activeGroupList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.groupList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return groupList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v;
		if( null == convertView){
			v = LayoutInflater.from(mContext).inflate(R.layout.group_item, null);
		}else{
			v = convertView;
		}
		
		TextView groupIdTxtView = (TextView)v.findViewById(R.id.groupIdTxtView);
		TextView groupNameTxtView = (TextView)v.findViewById(R.id.groupNameTxtView);
		groupIdTxtView.setText(""+groupList.get(position).getGroupId());
		groupNameTxtView.setText(""+groupList.get(position).getGroupName());
		
		v.setBackgroundColor(R.color.white);
		
		/*for(int index=0;index< activeGroupList.size();index++){
			if( activeGroupList.get(index).getGroupId() == groupList.get(position).getGroupId()){
				v.setBackgroundColor(R.color.red);
				break;
			}
		}*/
		
		//mark the joined group.
		GroupObject activeGroup = intercomApp.getActiveGroupObj();
		
		if (null != activeGroup) {
			if (activeGroup.getGroupId() == groupList.get(
					position).getGroupId()) {
				groupIdTxtView.setTextColor(mContext.getResources().getColor(R.color.blue));
				groupNameTxtView.setTextColor(mContext.getResources().getColor(R.color.blue));
			}else{
				groupIdTxtView.setTextColor(mContext.getResources().getColor(R.color.black));
				groupNameTxtView.setTextColor(mContext.getResources().getColor(R.color.black));				
			}
		}
		
		return v;
	}

	public List<GroupObject> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<GroupObject> groupList) {
		this.groupList = groupList;
	}

	public List<GroupObject> getActiveGroupList() {
		return activeGroupList;
	}

	public void setActiveGroupList(List<GroupObject> activeGroupList) {
		this.activeGroupList = activeGroupList;
	}

	public IntercomApplication getIntercomApp() {
		return intercomApp;
	}

	public void setIntercomApp(IntercomApplication intercomApp) {
		this.intercomApp = intercomApp;
	}
}
