package com.android.intercom;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

import com.android.intercom.R;
import com.android.intercom.adapter.FunctionAdapter;
import com.android.intercom.object.GroupObject;
import com.android.intercom.utils.Constants;
import com.android.intercom.utils.Constants.AI_SERVICE_TYPE;

public class MainActivity extends TabActivity implements OnClickListener,
		OnItemClickListener, OnTabChangeListener {
	private static final String TAG = "johnny";
	private Button exitButton;
	private TabHost tabHost;
	private EditText statusTxtView;
	private Button clearButton;
	
	private Phone intercom;
	
	private Handler mHandler;
	private ListView functionListView;
	private FunctionAdapter adapter;
	private StatusChangedReceiver statusChangedReceiver;
	
	private String[] classeActions = null;
	private String[] classeNames = null;
	private IntercomApplication intercomApp;
	private String stautsText = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initHandler();
		init();
		initView();
	}

	private void init() {
		intercomApp = (IntercomApplication) this.getApplication();
		intercom = intercomApp.getIntercom();
		Log.v(TAG, "get defaultPhone.");
		statusChangedReceiver = new StatusChangedReceiver();
		registerReceiver(statusChangedReceiver, new IntentFilter(Constants.STATUS_CHANGED_ACTION));
	}

	private void initHandler() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				Object obj = msg.obj;
				
				Log.v("johnny",
						"====================get message from ril.obj: "
								+ obj.toString()+"what: "+msg.what);
			}
		};
	}

	private void initView() {
		tabHost=getTabHost();
		tabHost.setOnTabChangedListener(this);
		tabHost.addTab(tabHost.newTabSpec("tabfirst").setIndicator(getString(R.string.statusTitleTxt))
                .setContent(R.id.tab1));
		tabHost.addTab(tabHost.newTabSpec("tabsecond").setIndicator(getString(R.string.operateTxt))
                .setContent(R.id.tab2));
		setTab(tabHost); 
		tabHost.setCurrentTab(0);

		statusTxtView = (EditText)findViewById(R.id.statusTxtView);
		clearButton = (Button)findViewById(R.id.clearBtn);
		exitButton = (Button) findViewById(R.id.exitBtn);
		clearButton.setOnClickListener(this);
		exitButton.setOnClickListener(this);
		functionListView = (ListView) findViewById(R.id.functionsListView);
		classeNames = this.getResources().getStringArray(R.array.app_names);
		classeActions = this.getResources().getStringArray(R.array.app_actions);
		adapter = new FunctionAdapter(this, classeNames);
		functionListView.setAdapter(adapter);
		functionListView.setOnItemClickListener(this);
	}

	private void setTab(TabHost tabhost) {
	    for(int i=0;i<tabhost.getTabWidget().getChildCount();i++)
	    {
	    	((TextView)((LinearLayout)tabhost.getTabWidget().getChildAt(i)).getChildAt(1)).setTextSize(18);
	    	((TextView)((LinearLayout)tabhost.getTabWidget().getChildAt(i)).getChildAt(1)).setTextColor(Color.parseColor("#FF000000"));
	    	tabhost.getTabWidget().getChildAt(i).getLayoutParams().height = 50;
	    	tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#87CEEB"));
	    }
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(statusChangedReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.exitBtn:
			intercom.getIntercomGroupList("hello",
					mHandler.obtainMessage(Constants.RIL_REQUEST_PTT_QUERY_AVAILABLE_GROUPS));
			//finish();
			break;
		case R.id.clearBtn:
			statusTxtView.setText("");
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		// TODO Auto-generated method stub
		Log.d(TAG, "position=" + position);
		if (classeActions != null && classeActions.length >= position
				&& classeNames != null && classeNames.length >= position) {
			if (classeNames.length != classeActions.length)
				Log.d(TAG, "classeActions len != classeNames len");
			try {
				startActivity(new Intent(classeActions[position]));
			} catch (ActivityNotFoundException anf) {

			} catch (Exception e) {
				Log.d(TAG, "error : " + e.getMessage());
			}
		} else {
			Log.d(TAG, "classeActions or classeNames no collect");
			return;
		}

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

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.audioGroupCall:
			intercomApp.setAiServiceType(AI_SERVICE_TYPE.AUDIO_GROUP_CALL);
			break;
		case R.id.audioBroadCast:
			intercomApp.setAiServiceType(AI_SERVICE_TYPE.AUDIO_GROUP_BROADCAST);
			break;
		case R.id.audioPointCall:
			setupPointCallOptionView();
			//intercomApp.setAiServiceType(AI_SERVICE_TYPE.AUDIO_POINT_CALL);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void setupPointCallOptionView() {
		AlertDialog.Builder alertDialogBuilding = new AlertDialog.Builder(this);
		final View v = LayoutInflater.from(this).inflate(
				R.layout.call_number_view, null);
		final EditText phoneEditText = (EditText) v.findViewById(R.id.phoneNumEditText);
		Button okButton = (Button)v.findViewById(R.id.okBtn);
		Button cancelButton = (Button) v.findViewById(R.id.cancelBtn);
		
		alertDialogBuilding.setView(v);
		final AlertDialog alertDialog = alertDialogBuilding.show();
		alertDialog.setCanceledOnTouchOutside(false);
		
		okButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intercomApp.setAiServiceType(AI_SERVICE_TYPE.AUDIO_POINT_CALL);
				intercomApp.setBussinessCallNum(phoneEditText.getText().toString());
				alertDialog.dismiss();
			}			
		});
		cancelButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				alertDialog.dismiss();
			}
		});
				
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		
	}

	class StatusChangedReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if( intent.getAction().equals(Constants.STATUS_CHANGED_ACTION)){
				stautsText = "\n\n"+intent.getExtras().getString(Constants.EXTRA_UE_STATUS); 
				String oldTextStr = statusTxtView.getText().toString();
				statusTxtView.setText(oldTextStr+stautsText);
			}
		}
		
	}
}
