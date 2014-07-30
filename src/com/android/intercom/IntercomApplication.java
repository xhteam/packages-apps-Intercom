package com.android.intercom;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.intercom.manager.AudioControl;
import com.android.intercom.object.GroupObject;
import com.android.intercom.utils.Constants;
import com.android.intercom.utils.Constants.AI_SERVICE_TYPE;
import com.android.intercom.utils.Constants.CALL_PRIORITY_TYPE;
import com.android.intercom.utils.Constants.GROUP_OWNER_IND_TYPE;
import com.android.intercom.utils.Constants.RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE;
import com.android.intercom.utils.Constants.RIL_UNSOL_PTT_CALL_CONNECT_STATUS_TYPE;
import com.android.intercom.utils.Constants.RIL_UNSOL_PTT_CALL_INDICATOR_RESULT_TYPE;
import com.android.intercom.utils.Constants.RIL_UNSOL_PTT_NOTIFICATION_DIAL_STATUS_TYPE;
import com.android.intercom.utils.Constants.RIL_UNSOL_PTT_OUTGOING_CALL_PROGRESS_STATUS_TYPE;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.PttGroup;
import com.android.internal.telephony.GroupInfo;
import com.android.internal.telephony.PttInfo;
import android.os.AsyncResult;


public class IntercomApplication extends Application {
	private final String TAG = "IntercomApplication";
	private Phone intercom;
	private GroupObject bussinessGroup;
	private Handler mHandler;
	private List<GroupObject> groupObjList;
	private List<GroupObject> activeGroupObjList;
	private AudioControl audioControl;
	private boolean isGroupCall = true;
	private boolean isVideoCall = false;
	private AI_SERVICE_TYPE aiServiceType;
	private String bussinessCallNum;
	private GroupObject joinedGroupObj;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initHandler();
		init();
	}

	private void init(){
		audioControl = AudioControl.getAudioControlInstance(IntercomApplication.this);		
		intercom = PhoneFactory.getDefaultPhone();
		aiServiceType = AI_SERVICE_TYPE.AUDIO_GROUP_CALL;
		//intercom.getIntercomGroupList("hello",
		//		mHandler.obtainMessage(Constants.RIL_REQUEST_PTT_QUERY_AVAILABLE_GROUPS));		
		
		intercom.registerForCallIndicator(mHandler, Constants.RIL_UNSOL_PTT_CALL_INDICATOR,null);
		intercom.registerForJoinOrExistGroup(mHandler, Constants.RIL_UNSOL_PTT_GROUP_RELEASE,null);
		intercom.registerForUEStatusChanged(mHandler, Constants.RIL_UNSOL_PTT_NOTIFICATION_DIAL,null);
		intercom.registerForGroupListUpdate(mHandler, Constants.RIL_UNSOL_PTT_AVAILABLE_GROUP_CHANGED,
				null);
		intercom.registerForNotificationCall(mHandler, Constants.RIL_UNSOL_PTT_NOTIFICATION_CALL,
				null);
		intercom.registerForCallConnect(mHandler, Constants.RIL_UNSOL_PTT_CALL_CONNECT,
				null);
		intercom.registerForCallHangup(mHandler, Constants.RIL_UNSOL_PTT_CALL_HANGUP,
				null);
		intercom.registerForOutGoingCallProgress(mHandler, Constants.RIL_UNSOL_PTT_OUTGOING_CALL_PROGRESS,
				null);
		intercom.registerForBlockedIndicator(mHandler, Constants.RIL_UNSOL_PTT_BLOCKED_INDICATOR,
				null);
		
		intercom.registerForCurrentGroupActiveList(mHandler, Constants.RIL_UNSOL_PTT_CURRENT_GROUP_ACTIVE_LIST,
				null);
		intercom.registerForDeviceInfo(mHandler, Constants.RIL_UNSOL_PTT_DEVICE_INFO,
				null);
		intercom.registerForGroupOwner(mHandler, Constants.RIL_UNSOL_PTT_GROUP_OWNER,
				null);
		intercom.registerForTrunckingMode(mHandler, Constants.RIL_UNSOL_PTT_TRUNKING_MODE,
				null);
		intercom.registerForNotificationJoinGroup(mHandler, Constants.RIL_UNSOL_PTT_NOTIFICATION_JOIN_GROUP,
				null);
		intercom.registerForBizInfo(mHandler, Constants.RIL_UNSOL_PTT_BIZ_INFO,
				null);
		intercom.registerForNetworkVersion(mHandler, Constants.RIL_UNSOL_PTT_NETWORK_VERSION,
				null);
		

		groupObjList = new ArrayList<GroupObject>();
		activeGroupObjList = new ArrayList<GroupObject>();
		
		startQueryGroups();
	}

	public Phone getIntercom() {
		return intercom;
	}

	public void keyDown(int keyCode, KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "when onKeyDown, keyCode: "+keyCode);
		if( keyCode != KeyEvent.KEYCODE_F12){
			return ;
		}
		int[] dataInfo ;
		int callType = aiServiceType.ordinal();
		if (AI_SERVICE_TYPE.AUDIO_GROUP_CALL == aiServiceType ||
				AI_SERVICE_TYPE.AUDIO_GROUP_BROADCAST == aiServiceType) {
			if (null != joinedGroupObj && true == joinedGroupObj.isJoinedSuccess() ) {
				dataInfo = new int[] { 0, callType, CALL_PRIORITY_TYPE.PRIORITY_15.ordinal(),
						joinedGroupObj.getGroupId() };
				intercom.callDial(dataInfo, mHandler
						.obtainMessage(Constants.RIL_REQUEST_PTT_CALL_DIAL));
			} else {
				Toast.makeText(this, "current group object is null or not join to the group.",
						Toast.LENGTH_LONG).show();
			}
		}else{
			if( null != bussinessCallNum && !bussinessCallNum.isEmpty()){
					dataInfo = new int[] { 0, callType, CALL_PRIORITY_TYPE.PRIORITY_15.ordinal(),
							Integer.parseInt(bussinessCallNum) };
				intercom.callDial(dataInfo, mHandler
						.obtainMessage(Constants.RIL_REQUEST_PTT_CALL_DIAL));
			}else{
				Toast.makeText(this, "current point call number is null",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void keyUp(int keyCode, KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "when onKeyUp, keyCode: "+keyCode);
		if( keyCode != KeyEvent.KEYCODE_F12){
			return ;
		}
		if( null != joinedGroupObj){
			//Group call dial
			int[] dataInfo = new int[]{0, joinedGroupObj.getGroupId()};
			intercom.hangupDial(dataInfo,mHandler.obtainMessage(Constants.RIL_REQUEST_PTT_CALL_HANGUP));
		}else{
			Toast.makeText(this, "current group object is null", Toast.LENGTH_LONG).show();
		}
	}

	private void initHandler() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				AsyncResult asyncResult = (AsyncResult)msg.obj;
				Object obj = asyncResult.result;
				Intent i ;
				String statusStr = "";
				Log.d(TAG, "PTT_TYPE: "+ msg.what);
				switch(msg.what){
				case Constants.RIL_UNSOL_PTT_CALL_INDICATOR:
					int[] dataInfo = (int[]) obj;
					if( Constants.GRANT_SUCCESS == dataInfo[2]){
						//Toast.makeText(IntercomApplication.this, "call dial success.", Toast.LENGTH_LONG).show();
						String owner = "IS_GROUP_OWNER";
						if( GROUP_OWNER_IND_TYPE.NOT_GROUP_OWNER.ordinal() == dataInfo[4]){
							owner = "NOT_GROUP_OWNER";
						}
						statusStr = "apply call successfully " + "groupId: "+ dataInfo[1]+ " "+owner;
						//audioControl.setMicrophoneMute(false);
						//audioControl.setSpeakerphoneOn(false);
					}else{
						//GroupCall is 0, means do not do related verified.
						if (0 != dataInfo[1]) {
							switch (dataInfo[3]) {
							case RIL_UNSOL_PTT_CALL_INDICATOR_RESULT_TYPE.NO_NETWOR:
								statusStr = "UE NO_NETWOR";
								break;
							case RIL_UNSOL_PTT_CALL_INDICATOR_RESULT_TYPE.NOT_SIGN_GROUP:
								statusStr = "UE NOT_SIGN_GROUP";
								break;
							case RIL_UNSOL_PTT_CALL_INDICATOR_RESULT_TYPE.UE_REMOTE_CLOSED_FOR_TEMP:
								statusStr = "UE_REMOTE_CLOSED_FOR_TEMP";
								break;
							case RIL_UNSOL_PTT_CALL_INDICATOR_RESULT_TYPE.UE_REMOTE_CLOSED_FOREVER:
								statusStr = "UE_REMOTE_CLOSED_FOREVER";
								break;
							}
						}else{
							statusStr = "gid is 0 "+" happended other error.errCode: "+ dataInfo[3];
						}
						Toast.makeText(IntercomApplication.this, "call dial fail.", Toast.LENGTH_LONG).show();
						statusStr = "apply call fail " + "groupId: "+ dataInfo[1]+ " error.errCode: "+dataInfo[3];
					}

					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);
					break;
				case Constants.RIL_UNSOL_PTT_AVAILABLE_GROUP_CHANGED:
				case Constants.RIL_REQUEST_PTT_QUERY_AVAILABLE_GROUPS:
					groupObjList.clear();
					PttGroup pttGroup = (PttGroup) obj;
					int allGroupNumber = pttGroup.groups_number + pttGroup.dyn_groups_number;
					List<GroupInfo> groupInfoList = pttGroup.ginfoList;
					Log.d(TAG,"allGroupNumber: "+allGroupNumber );
					for (int index = 0; index < allGroupNumber; index++) {
						GroupObject groupObj = new GroupObject(groupInfoList.get(index).gid,
								groupInfoList.get(index).gname);
						groupObj.setGstate(groupInfoList.get(index).gstate);
						groupObj.setGpriority(groupInfoList.get(index).gpriority);
						groupObjList.add(groupObj);
						Log.d(TAG, "GroupId: " + groupInfoList.get(index).gid
								+ "GroupName: "
								+ groupInfoList.get(index).gname);
					}
					i = new Intent(Constants.GROUP_LIST_CHANGED_ACTION);
					sendBroadcast(i);
					
					//start join group.
					if( 0 != groupObjList.size()){
						requestJoinInGroup(groupObjList.get(0));
					}
					break;
				case Constants.RIL_UNSOL_PTT_NOTIFICATION_CALL:
					//Incomming call.
					//open speaker.
					//audioControl.setSpeakerphoneOn(true);
					Log.d(TAG, "RIL_UNSOL_PTT_NOTIFICATION_CALL");
					audioControl.setInCommunication();
					int[] notificationCallInfo = (int[]) obj;
					String canGrab = notificationCallInfo[3] == 1 ? "can grab call right"
							: "can not grab call right. ";
					String isGroup = notificationCallInfo[7] == 0? " is group call":" is not group call. ";
					statusStr =canGrab + isGroup+ "call Id: "+notificationCallInfo[4] +" incoming call.";
					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);
					break;
				case Constants.RIL_UNSOL_PTT_CALL_CONNECT:
					int[] connectInfo = (int[]) obj;
					switch(connectInfo[1]){
					case RIL_UNSOL_PTT_CALL_CONNECT_STATUS_TYPE.CONNECT_FAIL:
						statusStr = "CONNECT_FAIL.";
						break;
					case RIL_UNSOL_PTT_CALL_CONNECT_STATUS_TYPE.CONNECT_SUCCESS:
						statusStr = "CONNECT_SUCCESS.";
						break;
					}
					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);
					break;
				case Constants.RIL_UNSOL_PTT_CALL_HANGUP:
					int[] callHangupInfo = (int[]) obj;
					//TODO::explain the hangup cause.
					statusStr = "CALL_HANGUP.";
					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);
					break;
				case Constants.RIL_UNSOL_PTT_OUTGOING_CALL_PROGRESS:
					int[] callProgressInfo = (int[]) obj;
					String temp = "";
					switch(callProgressInfo[1]){
					case RIL_UNSOL_PTT_OUTGOING_CALL_PROGRESS_STATUS_TYPE.CALLED_PROGRESSING:
						temp = "CALLED_PROGRESSING";
						break;
					case RIL_UNSOL_PTT_OUTGOING_CALL_PROGRESS_STATUS_TYPE.CALLED_QUEUED:
						temp = "CALLED_QUEUED";
						break;
					case RIL_UNSOL_PTT_OUTGOING_CALL_PROGRESS_STATUS_TYPE.CALLED_PARTY_PAGED:
						temp = "CALLED_PARTY_PAGED";
						break;
					case RIL_UNSOL_PTT_OUTGOING_CALL_PROGRESS_STATUS_TYPE.CALL_CONTINUE:
						temp = "CALL_CONTINUE";
						break;
					case RIL_UNSOL_PTT_OUTGOING_CALL_PROGRESS_STATUS_TYPE.HANG_TIME_EXPIRED:
						temp = "HANG_TIME_EXPIRED";					
						break;
					}
					statusStr = "Current point call Statuss is: "+temp;
					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);
					break;
				case Constants.RIL_UNSOL_PTT_BLOCKED_INDICATOR:
					int[] blockedInfo = (int[])obj;
					String bolckedStr = "";
					switch(blockedInfo[0]){
					case RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE.IMSI_OPEN_IMEI_OPERN:
						bolckedStr = "IMSI_OPEN_IMEI_OPERN";
						break;
					case RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE.IMSI_TEMP_CLOSE_TEMP_IMEI_OPEN:
						bolckedStr = "IMSI_TEMP_CLOSE_TEMP_IMEI_OPEN";
						break;
					case RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE.IMSI_CLOSE_FOREVER_IMEI_OPEN:
						bolckedStr = "IMSI_CLOSE_FOREVER_IMEI_OPEN";
						break;
					case RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE.IMSI_OPEN_IMEI_CLOSE_TEMP:
						bolckedStr = "IMSI_OPEN_IMEI_CLOSE_TEMP";
						break;
					case RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE.IMSI_OPEN_IMEI_CLOSE_FOREVER:
						bolckedStr = "IMSI_OPEN_IMEI_CLOSE_FOREVER";
						break;
					case RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE.IMSI_CLOSE_TEMP_IMEI_CLOSE_FOREVER:
						bolckedStr = "IMSI_CLOSE_TEMP_IMEI_CLOSE_FOREVER";
						break;
					case RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE.IMSI_CLOSE_FOREVER_IMEI_CLOSE_TEMP:
						bolckedStr = "IMSI_CLOSE_FOREVER_IMEI_CLOSE_TEMP";
						break;
					case RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE.IMSI_CLOSE_TEMP_IMEI_CLOSE_TEMP:
						bolckedStr = "IMSI_CLOSE_TEMP_IMEI_CLOSE_TEMP";
						break;
					case RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE.IMSI_CLOSE_FOREVER_IMEI_CLOSE_FOREVER:
						bolckedStr = "IMSI_CLOSE_FOREVER_IMEI_CLOSE_FOREVER";
						break;
					case RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE.IMSI_IMEI_EXCEPTION:
						bolckedStr = "IMSI_IMEI_EXCEPTION";
						break;
					}
					statusStr = "blocked status: " + bolckedStr;
					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);
					break;
				case Constants.RIL_REQUEST_PTT_CURRENT_GROUP_SCANLIST_UPDATE:
					statusStr = "operate grouplist scan setting successfully.";
					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);
					break;
				case Constants.RIL_REQUEST_PTT_QUERY_BIZ_STATE:
					PttInfo pttInfo = (PttInfo)obj;
					Log.d(TAG, "pttInfo: "+ pttInfo);
					statusStr = "PTT Business Status: "+ pttInfo.toString();
					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);
					break;
				case Constants.RIL_UNSOL_PTT_NOTIFICATION_DIAL:
					if( true){
						String[] tempObj = (String[])obj;
						int strlen = tempObj.length;
						for( int index=0;index<strlen;index++){
							if( 0 == index){
								statusStr = "rant Status: "+tempObj[0];
							}else if( 1 == index){
								statusStr += " speakerNum: "+tempObj[1];
							}else if( 2== index){
								statusStr +=" speaker: "+tempObj[2];
							}
						}
						Log.v(TAG,statusStr);						
						i = new Intent(Constants.STATUS_CHANGED_ACTION);
						i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
						sendBroadcast(i);
					}
					if (false) {
						String[] notificationDial = (String[]) obj;
						if (Constants.RIL_UNSOL_PTT_NOTIFICATION_DIAL_STATUS_TYPE.CALL_RIGHT_IN_LINE
								.equals(notificationDial[0])) {
							statusStr = "speaker number: "
									+ notificationDial[1] + " speaker name: "
									+ notificationDial[2];
						} else {
							statusStr = "call right is idel.";
						}
						i = new Intent(Constants.STATUS_CHANGED_ACTION);
						i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
						sendBroadcast(i);
					}
					break;
				case Constants.RIL_UNSOL_PTT_DEVICE_INFO:
					int[] deviceInfo = (int[])obj;
					statusStr = deviceInfo[0] == 1? "PPT VOICE SUPPORT":"PPT VOICE NOT SUPPROT";
					statusStr += "\n"+ (deviceInfo[1] == 1? "PPT SMS SUPPORT":"PPT SMS NOT SUPPORT");
					statusStr += "\n"+ (deviceInfo[2] == 1? "PPT PV SUPPORT":"PPT PV NOT SUPPORT");
					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);
					break;
				case Constants.RIL_UNSOL_PTT_GROUP_OWNER:
					int[] ownerInfo = (int[])obj;
					if( 1 == ownerInfo[0]){
						statusStr = " Current User is groupOwner, GroupId: "+ownerInfo[1];
					}else{
						statusStr = " Current User is not groupOwner, GroupId: "+ownerInfo[1];
					}
					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);						
					break;
				case Constants.RIL_UNSOL_PTT_GROUP_RELEASE:
					String[] releaseGroupInfo = (String[])obj;
					statusStr = "group release , groupId: "+releaseGroupInfo[1];
					i = new Intent(Constants.STATUS_CHANGED_ACTION);
					i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
					sendBroadcast(i);
					break;
				case Constants.RIL_UNSOL_PTT_CURRENT_GROUP_ACTIVE_LIST:
					activeGroupObjList.clear();
					Log.d(TAG, "===========" + obj.toString());
					if (!(obj instanceof int[])) {
						int s = (Integer) obj;
						Log.d(TAG, "-----Current group number: " + s);
					} else {
						int[] activeGroupInfo = (int[]) obj;
						Log.d(TAG, "-----Current group number: "
								+ activeGroupInfo[0]);

						if (0 != activeGroupInfo[0]) {
							for (int index = 0; index < activeGroupInfo[0]; index++) {
								GroupObject groupObj = new GroupObject(
										activeGroupInfo[index + 1], "name");
								activeGroupObjList.add(groupObj);
							}
							i = new Intent(Constants.GROUP_LIST_CHANGED_ACTION);
							sendBroadcast(i);
						}
					}
					break;

					//handle request join group.
					case Constants.RIL_REQUEST_PTT_GROUP_SETUP:
						dataInfo = (int[])obj;
						switch(dataInfo[0]){
						case Constants.RIL_REQUEST_PTT_GROUP_SETUP_RESULT_TYPE.NO_NETWORK:
							statusStr = "ue has NO_NETWORK";
							break;
						case Constants.RIL_REQUEST_PTT_GROUP_SETUP_RESULT_TYPE.NOT_SIGN_GROUP:
							statusStr = "ue NOT_SIGN_GROUP";
							break;
						case Constants.RIL_REQUEST_PTT_GROUP_SETUP_RESULT_TYPE.UE_NOT_SUPORT:
							statusStr = "UE_NOT_SUPORT";
							break;
						case Constants.RIL_REQUEST_PTT_GROUP_SETUP_RESULT_TYPE.UE_REMOTE_CLOSED_FOR_TEMP:
							statusStr = "UE_REMOTE_CLOSED_FOR_TEMP";
							break;
						case Constants.RIL_REQUEST_PTT_GROUP_SETUP_RESULT_TYPE.UE_REMOTE_CLOSED_FOREVER:
							statusStr = "UE_REMOTE_CLOSED_FOREVER";
							break;
					case Constants.RIL_REQUEST_PTT_GROUP_SETUP_RESULT_TYPE.UE_JOIN_GROUP_SUCCESS:
						statusStr = "UE_JOIN_GROUP_SUCCESS";
						// mark the joined group.
						if( null != joinedGroupObj){
							setJoinedGroupObjFlag();
							Log.d(TAG, "joined in group,groupId: "+ joinedGroupObj.getGroupId());
							i = new Intent(Constants.JOINED_GROUP_CHANGED_ACTION);
							sendBroadcast(i);
						}
						break;
						default:
							statusStr = "UE join group fail with error: "+ dataInfo[0];
							break;
						}
						i = new Intent(Constants.STATUS_CHANGED_ACTION);
						i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
						sendBroadcast(i);
						break;
					case Constants.RIL_REQUEST_PTT_GROUP_RELEASE:
						dataInfo = (int[]) obj;
						switch(dataInfo[0]){
						case Constants.RIL_REQUEST_PTT_GROUP_RELEASE_RESULT_TYPE.UE_CLOSE_GROUP_SUCCESS:
							statusStr = "close group successfully " + "groupId: "+ dataInfo[1]+ " ";
							if( joinedGroupObj.getGroupId() == dataInfo[1]){
								joinedGroupObj.setJoinedSuccess(false);
								joinedGroupObj = null;
							}
							i = new Intent(Constants.JOINED_GROUP_CHANGED_ACTION);
							sendBroadcast(i);
							break;
						case Constants.RIL_REQUEST_PTT_GROUP_RELEASE_RESULT_TYPE.UE_HAS_NO_RIGHT_CLOSED_GROUP:
							statusStr = "close group fail for UE_CLOSE_GROUP_SUCCESS";
							break;
						default:
							statusStr = "close group fail for other error";
							break;
						}
						i = new Intent(Constants.STATUS_CHANGED_ACTION);
						i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
						sendBroadcast(i);
						break;					
				}
				
			}
		};
	}

	public List<GroupObject> getGroupObjList() {
		return groupObjList;
	}

	public void setGroupObjList(List<GroupObject> groupObjList) {
		this.groupObjList = groupObjList;
	}

	public List<GroupObject> getActiveGroupObjList() {
		return activeGroupObjList;
	}

	public void setActiveGroupObjList(List<GroupObject> activeGroupObjList) {
		this.activeGroupObjList = activeGroupObjList;
	}

	public GroupObject getBussinessGroup() {
		return bussinessGroup;
	}

	public void setBussinessGroup(GroupObject bussinessGroup) {
		this.bussinessGroup = bussinessGroup;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub		
		intercom.unregisterForCallIndicator(mHandler);
		intercom.unregisterForJoinOrExistGroup(mHandler);
		intercom.unregisterForUEStatusChanged(mHandler);
		intercom.unregisterForGroupListUpdate(mHandler);
		intercom.unregisterForNotificationCall(mHandler);
		intercom.unregisterForCallConnect(mHandler);
		intercom.unregisterForCallHangup(mHandler);
		intercom.unregisterForOutGoingCallProgress(mHandler);
		intercom.unregisterForBlockedIndicator(mHandler);
		
		intercom.unregisterForCurrentGroupActiveList(mHandler);
		intercom.unregisterForDeviceInfo(mHandler);
		intercom.unregisterForGroupOwner(mHandler);
		intercom.unregisterForTrunckingMode(mHandler);
		intercom.unregisterForNotificationJoinGroup(mHandler);
		intercom.unregisterForBizInfo(mHandler);
		intercom.unregisterForNetworkVersion(mHandler);
		super.onTerminate();
	}

	public String getBussinessCallNum() {
		return bussinessCallNum;
	}

	public void setBussinessCallNum(String bussinessCallNum) {
		this.bussinessCallNum = bussinessCallNum;
	}

	public AI_SERVICE_TYPE getAiServiceType() {
		return aiServiceType;
	}

	public void setAiServiceType(AI_SERVICE_TYPE aiServiceType) {
		this.aiServiceType = aiServiceType;
	}

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	
	public GroupObject getJoinedGroupObj() {
		return joinedGroupObj;
	}

	public void setJoinedGroupObjFlag() {
		this.joinedGroupObj.setJoinedSuccess(true);
	}
	
	public void releaseGroup(int joinedGroupObjId){
		for( int index=0;index<groupObjList.size();index++){
			if( groupObjList.get(index).getGroupId() == joinedGroupObjId){
				this.joinedGroupObj = null;
				break;
			}
		}
	}
	
	public void startQueryGroups(){
		intercom.getIntercomGroupList("hello",
				mHandler.obtainMessage(Constants.RIL_REQUEST_PTT_QUERY_AVAILABLE_GROUPS));	
	}
	
	public void closeGroupListScan(){
		int[] args = new int[]{0};
		intercom.currentGroupScanListUpdate(args,
				mHandler.obtainMessage(Constants.RIL_REQUEST_PTT_CURRENT_GROUP_SCANLIST_UPDATE));
	}
	
	public void openGroupListScan(){
		int argLen = 1 + groupObjList.size();
		int[] args = new int[argLen];
		args[0] = 1;
		for(int index=0;index<groupObjList.size();index++){
			args[index+1] = groupObjList.get(index).getGroupId();
		}

		intercom.currentGroupScanListUpdate(args,
				mHandler.obtainMessage(Constants.RIL_REQUEST_PTT_CURRENT_GROUP_SCANLIST_UPDATE));
	}
	
	public void requestPttQueryBizState(){
		intercom.queryBizState(mHandler.obtainMessage(Constants.RIL_REQUEST_PTT_QUERY_BIZ_STATE));
	}
	
	public void requestJoinInGroup(GroupObject groupObj){
			if( null != joinedGroupObj && groupObj.getGroupId() == joinedGroupObj.getGroupId()
					&& true ==joinedGroupObj.isJoinedSuccess()){
				return;
			}
			int[] groupInfo = new int[]{groupObj.getGroupId(), 5,0};
			intercom.joinInGroup(
					groupInfo,
					mHandler.obtainMessage(
							Constants.RIL_REQUEST_PTT_GROUP_SETUP));
			joinedGroupObj = groupObj;
			joinedGroupObj.setJoinedSuccess(false);
	}
}
