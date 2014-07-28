package com.android.intercom.callback;

import android.view.KeyEvent;

public interface OnKeyEvenHappened {
	public void onKeyDown(int keyCode, KeyEvent keyEvent);
	public void onKeyUp(int keyCode, KeyEvent keyEvent);
}
