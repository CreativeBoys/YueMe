package com.yueme;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class PubRequireActivity extends Activity{
	EditText requirementClass;
	EditText effictiveTime;
	EditText numAccount;
	String bundleString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish_requirement);
		bundleString = getIntent().getExtras().getString("CLASSIFY");
		Log.d("hello", "bundlestring"+bundleString);
		initView();
	}
	
	private void initView() {
		requirementClass = (EditText)findViewById(R.id.requirementClass);
		effictiveTime = (EditText)findViewById(R.id.effictiveTime);
		numAccount = (EditText)findViewById(R.id.numAccount);
		
		requirementClass.setText(bundleString);
	}
	
	public void back(View v) {
		finish();
	}
}
