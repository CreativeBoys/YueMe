package com.yueme;

import com.yueme.values.ConstantValues;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeSplashActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welome_splash);
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SharedPreferences ini = getSharedPreferences("data", 0);
				if(ini.getBoolean(ConstantValues.IS_LOGINED, false)==false) {
					startActivity(new Intent(WelcomeSplashActivity.this, LoginRegisterActivity.class));
				} else{
					startActivity(new Intent(WelcomeSplashActivity.this	, MainActivity.class));
				}
				
				finish();
			}
			
		}, 2500L);
	}
	
	
}
