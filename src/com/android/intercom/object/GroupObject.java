package com.android.intercom.object;

public class GroupObject {

	private int groupId;
	int gpriority;
	int gstate;
	private String groupName;
	
	public GroupObject(int groupId, String groupName) {
		// TODO Auto-generated constructor stub
		this.groupId = groupId;
		this.groupName = groupName;
	}
	
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getGpriority() {
		return gpriority;
	}

	public void setGpriority(int gpriority) {
		this.gpriority = gpriority;
	}

	public int getGstate() {
		return gstate;
	}

	public void setGstate(int gstate) {
		this.gstate = gstate;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
