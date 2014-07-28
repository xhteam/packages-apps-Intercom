package com.android.intercom;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.intercom.R;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.intercom.adapter.GroupAdapter;
import com.android.intercom.object.GroupObject;
import com.android.intercom.utils.Constants;

public class GroupActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private final String TAG = "GroupActivity";
	
	private ListView groupListView;
	private Button exitButton;
	private Button closeScanListBtn;
	private Button openScanListBtn;
	private GroupAdapter groupAdapter;
	private Phone intercom;
	private Handler mHandler;
	private IntercomApplication intercomApp;
	private GroupListReceiver groupListReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.group_list_layout);
		initHandler();
		initData();
		initView();
	}

	private void initData() {
		intercomApp = (IntercomApplication)this.getApplication();
		intercom = intercomApp.getIntercom();
		groupListReceiver = new GroupListReceiver();
		registerReceiver(groupListReceiver, new IntentFilter(Constants.GROUP_LIST_CHANGED_ACTION));
	}

	private void initView() {
		groupListView = (ListView) findViewById(R.id.groupListView);
		exitButton = (Button) findViewById(R.id.exitBtn);
		closeScanListBtn = (Button)findViewById(R.id.closeScanListBtn);
		openScanListBtn = (Button)findViewById(R.id.openScanListBtn);
		
		exitButton.setOnClickListener(this);
		closeScanListBtn.setOnClickListener(this);
		openScanListBtn.setOnClickListener(this);
		List<GroupObject> groupObjList = new ArrayList<GroupObject>();
		for (int index = 0; index < 5; index++) {
			GroupObject obj = new GroupObject(index, "" + (index + 1));
			groupObjList.add(obj);
		}
		groupAdapter = new GroupAdapter(this, groupObjList,intercomApp.getActiveGroupObjList());
		groupListView.setAdapter(groupAdapter);

		groupListView.setOnItemClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(groupListReceiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.exitBtn:
			finish();
			break;
		case R.id.openScanListBtn:
			String groupIdList = "";
			List<GroupObject > groupList = intercomApp.getGroupObjList();
			for( int index=0;index<groupList.size()-1;index++){
				groupIdList += groupList.get(index).getGroupId()+" ";
			}
			groupIdList += ""+groupList.get(groupList.size()-1).getGroupId();
			//TODO::
			//intercom.currentGroupScanListUpdate();
			break;
		case R.id.closeScanListBtn:
			break;
		}
	}

	private void initHandler() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				Object obj = msg.obj;
				Log.v("johnny",
						"====================get message from ril.obj: "
								+ obj.toString());
				String statusStr = "";
				int[] dataInfo = new int[10];
				Intent i ;
				switch(msg.what){
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
					default:
						statusStr = "UE join group: "+ dataInfo[1]+"successfully.";
						break;
					}
					break;
				case Constants.RIL_REQUEST_PTT_GROUP_RELEASE:
					dataInfo = (int[]) obj;
					switch(dataInfo[0]){
					case Constants.RIL_REQUEST_PTT_GROUP_RELEASE_RESULT_TYPE.UE_CLOSE_GROUP_SUCCESS:
						statusStr = "close group successfully " + "groupId: "+ dataInfo[1]+ " ";
						break;
					case Constants.RIL_REQUEST_PTT_GROUP_RELEASE_RESULT_TYPE.UE_HAS_NO_RIGHT_CLOSED_GROUP:
						statusStr = "close group fail for UE_CLOSE_GROUP_SUCCESS";
						break;
					default:
						statusStr = "close group fail for other error";
						break;
					}
				}

				i = new Intent(Constants.STATUS_CHANGED_ACTION);
				i.putExtra(Constants.EXTRA_UE_STATUS, statusStr);
				sendBroadcast(i);
			}
		};
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		setupGroupOperateView((GroupObject)groupAdapter.getItem(position));
	}

	private void setupGroupOperateView(final GroupObject groupObj) {
		AlertDialog.Builder alertDialogBuilding = new AlertDialog.Builder(this);
		final View v = LayoutInflater.from(this).inflate(
				R.layout.operate_group, null);
		Button joinGroupBtn = (Button) v.findViewById(R.id.joinGroupBtn);
		Button autoJoinGroupBtn = (Button)v.findViewById(R.id.autoJoinGroupBtn);
		Button exitGroupBtn = (Button) v.findViewById(R.id.exitGroupBtn);
		Button cancelBtn = (Button) v.findViewById(R.id.cancelBtn);
		Button setAsCurrentGroupBtn = (Button)v.findViewById(R.id.setAsCurrentGroupBtn);
		
		alertDialogBuilding.setView(v);
		final AlertDialog alertDialog = alertDialogBuilding.show();
		alertDialog.setCanceledOnTouchOutside(false);

		joinGroupBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int[] groupInfo = new int[]{groupObj.getGroupId(), 5,0};
				intercom.joinInGroup(groupInfo, mHandler.obtainMessage(Constants.RIL_REQUEST_PTT_GROUP_SETUP));
				alertDialog.dismiss();
			}
		});
		autoJoinGroupBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int[] groupInfo = new int[]{groupObj.getGroupId(), 5,1};
				intercom.joinInGroup(groupInfo, mHandler.obtainMessage(Constants.RIL_REQUEST_PTT_GROUP_SETUP));
				alertDialog.dismiss();
			}
		});
		exitGroupBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int[] groupInfo = new int[]{groupObj.getGroupId(), 0,0};
				intercom.exitGroup(groupInfo,mHandler.obtainMessage(Constants.RIL_REQUEST_PTT_GROUP_RELEASE));
				alertDialog.dismiss();
			}
		});
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
		
		setAsCurrentGroupBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intercomApp.setBussinessGroup(groupObj);
				alertDialog.dismiss();
			}
			
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		intercomApp.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		intercomApp.onKeyUp(keyCode, event);
		return super.onKeyUp(keyCode, event);
	}
	
	class GroupListReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if( intent.getAction().equals(Constants.GROUP_LIST_CHANGED_ACTION)){
				Log.d(TAG, "GROUP_LIST_CHANGED_ACTION");
				groupAdapter.setGroupList(intercomApp.getGroupObjList());
				groupAdapter.setActiveGroupList(intercomApp.getActiveGroupObjList());
			}
		}		
	}
}
