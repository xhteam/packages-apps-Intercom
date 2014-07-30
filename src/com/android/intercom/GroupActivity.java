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
	private Button closeScanListBtn;
	private Button openScanListBtn;
	private GroupAdapter groupAdapter;
	private Phone intercom;
	private IntercomApplication intercomApp;
	private GroupListReceiver groupListReceiver;
	private boolean isLongPressKey = false;
	private boolean lockLongPressKey = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.group_list_layout);
		initData();
		initView();
	}

	private void initData() {
		intercomApp = (IntercomApplication)this.getApplication();
		intercom = intercomApp.getIntercom();
		groupListReceiver = new GroupListReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.GROUP_LIST_CHANGED_ACTION);
		intentFilter.addAction(Constants.JOINED_GROUP_CHANGED_ACTION);
		registerReceiver(groupListReceiver, intentFilter);
	}

	private void initView() {
		groupListView = (ListView) findViewById(R.id.groupListView);
		closeScanListBtn = (Button)findViewById(R.id.closeScanListBtn);
		openScanListBtn = (Button)findViewById(R.id.openScanListBtn);
		
		closeScanListBtn.setOnClickListener(this);
		openScanListBtn.setOnClickListener(this);
		
		groupAdapter = new GroupAdapter(this, intercomApp.getGroupObjList(),intercomApp.getActiveGroupObjList());
		groupListView.setAdapter(groupAdapter);
		groupAdapter.setIntercomApp(intercomApp);
		groupListView.setOnItemClickListener(this);

		intercomApp.startQueryGroups();	
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
		case R.id.openScanListBtn:
			intercomApp.openGroupListScan();
			break;
		case R.id.closeScanListBtn:
			intercomApp.closeGroupListScan();
			break;
		}
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
				intercomApp.requestJoinInGroup(groupObj);
				alertDialog.dismiss();
			}
		});
		autoJoinGroupBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int[] groupInfo = new int[]{groupObj.getGroupId(), 5,1};
				intercom.joinInGroup(groupInfo, intercomApp.getmHandler().obtainMessage(Constants.RIL_REQUEST_PTT_GROUP_SETUP));
				alertDialog.dismiss();
			}
		});
		exitGroupBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int[] groupInfo = new int[]{0,groupObj.getGroupId()};
				intercom.exitGroup(groupInfo,intercomApp.getmHandler().obtainMessage(Constants.RIL_REQUEST_PTT_GROUP_RELEASE));
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
		//intercomApp.onKeyDown(keyCode, event);
		Log.d(TAG, "IN GroupActivity, onKeyDown,KEYCODE: " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_F12) {
			if (event.getRepeatCount() == 0) {// 识别长按短按的代码
				event.startTracking();
				isLongPressKey = false;
			} else {
				if (isLongPressKey != true) {
					Log.d(TAG, "Long Click.");
					isLongPressKey = true;
					// intercomApp.onKeyDown(keyCode, event);
				}
			}
			Log.d(TAG, "event.getRepeatCount(): " + event.getRepeatCount());
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//intercomApp.onKeyUp(keyCode, event);
		Log.d(TAG, "IN GroupActivity, onKeyUp,KEYCODE: " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_F12) {
			lockLongPressKey = false;
			intercomApp.keyUp(keyCode, event);
			return true;
		}
		
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		Log.v(TAG, "key long pressed keyCode = " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_F12) {
			lockLongPressKey = true;
			intercomApp.keyDown(keyCode, event);
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}
	
	class GroupListReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.d(TAG, "action: "+action);
			if( action.equals(Constants.GROUP_LIST_CHANGED_ACTION)){
				groupAdapter.setGroupList(intercomApp.getGroupObjList());
				groupAdapter.setActiveGroupList(intercomApp.getActiveGroupObjList());
				groupAdapter.notifyDataSetChanged();
				if( null == intercomApp.getJoinedGroupObj() && 0 != intercomApp.getGroupObjList().size()){
					int[] groupInfo = new int[]{intercomApp.getGroupObjList().get(0).getGroupId(), 5,0};
					intercom.joinInGroup(
							groupInfo,
							intercomApp.getmHandler().obtainMessage(
									Constants.RIL_REQUEST_PTT_GROUP_SETUP));
				}				
			}else if(action.equals(Constants.JOINED_GROUP_CHANGED_ACTION)){
				groupAdapter.notifyDataSetChanged();
			}
		}		
	}
}
