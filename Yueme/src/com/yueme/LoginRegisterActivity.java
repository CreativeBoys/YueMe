package com.yueme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginRegisterActivity extends Activity{
	Button loginButton, registerButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_register);
		
		initView();
	}
	
	private void initView(){
		loginButton = (Button)findViewById(R.id.loginBtn);
		registerButton = (Button)findViewById(R.id.registerBtn);
		
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginRegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginRegisterActivity.this, RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
}
