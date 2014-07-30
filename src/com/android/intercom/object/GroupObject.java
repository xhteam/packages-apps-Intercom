package com.android.intercom.object;

public class GroupObject {

	private int groupId;
	private int gpriority;
	private int gstate;
	private String groupName;
	
	private boolean isJoinedSuccess;
	
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

	public boolean isJoinedSuccess() {
		return isJoinedSuccess;
	}

	public void setJoinedSuccess(boolean isJoinedSuccess) {
		this.isJoinedSuccess = isJoinedSuccess;
	}

}
