package com.yueme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


/**
 * @author heshaokang	
 * 2015-3-7 下午5:02:22
 */
public class SystemSettings extends Activity implements OnClickListener{
	private ImageView system_back; //顶部返回键
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_setting);
		system_back = (ImageView) findViewById(R.id.system_back);
		system_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
	}
	
}
