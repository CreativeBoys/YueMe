package com.yueme;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.yueme.fragment.HomeFragment;
import com.yueme.public_class.DemandItem;
import com.yueme.util.ToastUtil;

public class PubRequireActivity extends Activity {
	EditText requirementClassEt;
	TextView effictiveTimeTv;
	EditText numAccountEt;
	EditText requirementContentEt;
	TextView publish;
	String bundleString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish_requirement);
		bundleString = getIntent().getExtras().getString("CLASSIFY");
		Log.d("hello", "bundlestring" + bundleString);
		initView();
	}

	private void initView() {
		requirementClassEt = (EditText) findViewById(R.id.requirementClass);
		effictiveTimeTv = (TextView) findViewById(R.id.effictiveTime);
		numAccountEt = (EditText) findViewById(R.id.numAccount);
		requirementContentEt = (EditText) findViewById(R.id.requirementContent);
		publish = (TextView) findViewById(R.id.publish);
		requirementClassEt.setText(bundleString);
		setListener();
	}

	private void setListener(){
		publish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("hello", "publish");
				int icon_id = R.drawable.user_head;
				String demandClassify = requirementClassEt.getText().toString().trim();
				String demandContent = requirementContentEt.getText().toString().trim();
				String effictiveTime = effictiveTimeTv.getText().toString().trim();
				String numAccount = numAccountEt.getText().toString().trim();
				
				if(demandClassify==null || demandContent==null || effictiveTime==null || numAccount==null || demandClassify.equals("") || demandContent.equals("") || effictiveTime.equals("") || numAccount.equals("")) {
					ToastUtil.showToast("请先完善信息！", PubRequireActivity.this);
				} else{
					Date date = new    Date(System.currentTimeMillis());
					SimpleDateFormat simpleFormat = new SimpleDateFormat("MM-dd HH:mm");
					String pubTime = simpleFormat.format(date);
					HomeFragment.demandListItems.add(new DemandItem(icon_id, "name",demandClassify,  pubTime, demandContent, effictiveTime));
					setResult(HomeFragment.PUBLISH_COMPLETED);
					finish();
				}
			}
		});
	}
	
	

	public void back(View v) {
		finish();
	}
}
