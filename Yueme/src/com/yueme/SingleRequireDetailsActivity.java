package com.yueme;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yueme.public_class.DemandItem;

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
		
		DemandItem demandItem = (DemandItem) getIntent().getParcelableExtra("DEMAND_INFO");
		Log.d("hello", "demandItem: "+demandItem.toString());
		img.setImageResource(demandItem.icon_id);
		time.setText(demandItem.time);
		userName.setText(demandItem.userName);
		demandContent.setText(demandItem.demandContent);
		restTime.setText(demandItem.restTime);
		
		
	}
	public void back(View v) {
		finish();
	}
}
