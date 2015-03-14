package com.yueme;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class FindPasswordActiviity extends SwipeBackActivity{
	ImageView backBtn;
	EditText phoneNumEt, verifiEt, newPasswordEt;
	Button verifyBtn, confirmBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);
		init();
	}
	
	private void init() {
		backBtn = (ImageView)findViewById(R.id.backBtn);
		phoneNumEt = (EditText)findViewById(R.id.phoneNumEt);
		verifiEt = (EditText)findViewById(R.id.verifiEt);
		newPasswordEt= (EditText)findViewById(R.id.newPasswordEt);
		verifyBtn = (Button)findViewById(R.id.verifiBtn);
		confirmBtn = (Button)findViewById(R.id.confirmBtn);
		
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
		
		confirmBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FindPasswordActiviity.this, LoginActivity.class);
				startActivity(intent);
			}
		});
		
		
		
	}
}
