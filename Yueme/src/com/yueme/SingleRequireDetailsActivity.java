package com.yueme;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.yueme.domain.Info;

/*点击主页当需求后显示详情的acivity*/

public class SingleRequireDetailsActivity extends SwipeBackActivity{
	
	protected void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
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
	
	/**
	 * 点击相约后推送消息
	 * @param view
	 */
	
	public void onSendTxtMsg(View view) {
		EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
		
		msg.setReceipt("123456789");
		TextMessageBody body = new TextMessageBody("ddfadf");
		msg.addBody(body);
		msg.setAttribute("extStringAttr", "String Test Value");
		
		try {
			EMChatManager.getInstance().sendMessage(msg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void back(View v) {
		finish();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
}
