package com.yueme;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yueme.domain.Info;

public class SingleRequireDetailsActivity extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_require_activity);
		initView();
	}
	
	private void initView(){
		ImageView img = (ImageView) findViewById(R.id.userHeadIcon);
		TextView time = (TextView) findViewById(R.id.time);
		TextView userName = (TextView) findViewById(R.id.userName);
		TextView demandContent = (TextView)findViewById(R.id.demandContent);
		TextView restTime = (TextView) findViewById(R.id.restTime);
		Info info = (Info) getIntent().getSerializableExtra("info");
		String createTime="";
		long create_day = info.getCreate_day();
		if (DateUtils.isToday(create_day)) {
			createTime = "今天";
		} else {
			createTime = (String) DateFormat.format("yyyy-MM-dd",
					create_day);
		}
		time.setText(createTime);
		long deadline = info.getDeadline();
		restTime.setText("还有" + (deadline - new Date().getTime()) / (3600*1000)
				+ "小时");
		userName.setText(info.getNickname());
		demandContent.setText(info.getContent());
		
		
	}
	public void back(View v) {
		finish();
	}
}
