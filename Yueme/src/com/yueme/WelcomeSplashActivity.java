package com.yueme;

import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class WelcomeSplashActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welome_splash);
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				SharedPreferences sp = getSharedPreferences("yueme", 0);
				String userID = sp.getString("userID", null);
				if(userID==null) {
					startActivity(new Intent(WelcomeSplashActivity.this, LoginRegisterActivity.class));
				} else{
					GlobalValues.USER_ID = userID;
					startActivity(new Intent(WelcomeSplashActivity.this	, MainActivity.class));
				}
				finish();
			}
		}, 2500L);
	}
	
	
}
