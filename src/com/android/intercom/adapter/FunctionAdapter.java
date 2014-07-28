package com.android.intercom.adapter;


import com.android.intercom.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FunctionAdapter extends BaseAdapter {

	private final String TAG = "functionAdapter";
	private String[] functionTextList;
	private Context mContext;
	
	public FunctionAdapter(Context context, String[] functionTextList) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.functionTextList = functionTextList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if( null == this.functionTextList ){
			return 0;
		}else{
			return this.functionTextList.length;
		}
		
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View v;
		if( null == contentView){
			v = LayoutInflater.from(mContext).inflate(R.layout.button_item, null);
		}else{
			v = contentView;
		}
		
		((TextView)v).setText(this.functionTextList[position]);
		v.setTag(functionTextList[position]);
		
		return v;
	}

	public String[] getFunctionTextList() {
		return functionTextList;
	}

	public void setFunctionTextList(String[] functionTextList) {
		this.functionTextList = functionTextList;
	}

}
