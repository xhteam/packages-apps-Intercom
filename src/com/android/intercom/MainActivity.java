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

public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG = "MainActivity";
	private TextView statusTxtView;
	private Button clearBtn;
	private Button checkPttBussiness;
	private Phone intercom;
	private StatusChangedReceiver statusChangedReceiver;
	private IntercomApplication intercomApp;
	private String stautsText = "";

	private boolean isLongPressKey = false;
	private boolean lockLongPressKey = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		initView();
	}

	private void init() {
		intercomApp = (IntercomApplication) this.getApplication();
		intercom = intercomApp.getIntercom();
		Log.v(TAG, "get defaultPhone.");
		statusChangedReceiver = new StatusChangedReceiver();
		registerReceiver(statusChangedReceiver, new IntentFilter(
				Constants.STATUS_CHANGED_ACTION));
	}

	private void initView() {
		statusTxtView = (TextView) findViewById(R.id.statusTxtView);
		clearBtn = (Button)findViewById(R.id.clearBtn);
		checkPttBussiness = (Button)findViewById(R.id.checkPttBussiness);
		clearBtn.setOnClickListener(this);
		checkPttBussiness.setOnClickListener(this);
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
		case R.id.clearBtn:
			statusTxtView.setText("");
			break;
		case R.id.checkPttBussiness:
			intercomApp.requestPttQueryBizState();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "IN MainActivity, onKeyDown,KEYCODE: " + keyCode);
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
		Log.d(TAG, "IN MainActivity, onKeyUp,KEYCODE: " + keyCode);
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

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.audioGroupCall:
			intercomApp.setAiServiceType(AI_SERVICE_TYPE.AUDIO_GROUP_CALL);
			break;
		case R.id.audioBroadCast:
			intercomApp.setAiServiceType(AI_SERVICE_TYPE.AUDIO_GROUP_BROADCAST);
			break;
		case R.id.audioPointCall:
			setupPointCallOptionView();
			// intercomApp.setAiServiceType(AI_SERVICE_TYPE.AUDIO_POINT_CALL);
			break;
		case R.id.groupListView:
			Intent intent = new Intent("GroupActivity");
			startActivity(intent);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void setupPointCallOptionView() {
		AlertDialog.Builder alertDialogBuilding = new AlertDialog.Builder(this);
		final View v = LayoutInflater.from(this).inflate(
				R.layout.call_number_view, null);
		final EditText phoneEditText = (EditText) v
				.findViewById(R.id.phoneNumEditText);
		Button okButton = (Button) v.findViewById(R.id.okBtn);
		Button cancelButton = (Button) v.findViewById(R.id.cancelBtn);

		alertDialogBuilding.setView(v);
		final AlertDialog alertDialog = alertDialogBuilding.show();
		alertDialog.setCanceledOnTouchOutside(false);

		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intercomApp.setAiServiceType(AI_SERVICE_TYPE.AUDIO_POINT_CALL);
				intercomApp.setBussinessCallNum(phoneEditText.getText()
						.toString());
				alertDialog.dismiss();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
	}

	class StatusChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Constants.STATUS_CHANGED_ACTION)) {
				stautsText = "\n\n"
						+ intent.getExtras().getString(
								Constants.EXTRA_UE_STATUS);
				String oldTextStr = statusTxtView.getText().toString();
				statusTxtView.setText(oldTextStr + stautsText);
			}
		}
	}
}
