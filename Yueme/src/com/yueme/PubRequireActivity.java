package com.yueme;

import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.yueme.domain.MyDatePicker;
import com.yueme.domain.ProtocalResponse;
import com.yueme.util.EncodeUtil;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;
/*发布需求的activity*/
public class PubRequireActivity extends Activity {
	EditText requirementClassEt;
	
	EditText numAccountEt;
	EditText requirementContentEt;
	TextView effictiveTimeTv;
	TextView publish;
	String bundleString;
	PopupWindow popupWindow;
	View parentView;
	MyDatePicker myDatePicker;
	TimePicker timePicker;
	Button popEnsureBtn;
	Date effictiveDate;
	private String groupID;
	private String demandClassify;
	private String demandContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentView = LayoutInflater.from(PubRequireActivity.this).inflate(R.layout.activity_publish_requirement, null);
		setContentView(parentView);
		bundleString = getIntent().getExtras().getString("CLASSIFY");
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
				demandClassify = requirementClassEt.getText().toString().trim();
				demandContent = requirementContentEt.getText().toString().trim();
				String numAccount = numAccountEt.getText().toString().trim();
				
				if(effictiveDate==null || demandClassify==null || demandContent==null ||  numAccount==null || demandClassify.equals("") || demandContent.equals("")  || numAccount.equals("")) {
					ToastUtil.showToast("请先完善信息！", PubRequireActivity.this);
				} else{
					new CreateGroupAsyncTast().execute();
				}
			}
		});
		
		effictiveTimeTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopupWindow();
			}
		});
	}
	
	private class PubRequireAsyncTask extends AsyncTask<Void, Void, ProtocalResponse> {

		@Override
		protected ProtocalResponse doInBackground(
				Void... params) {
			try {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM, ConstantValues.ADD_INFO+"");
				map.put("title", EncodeUtil.chinese2URLEncode("测试"));
				map.put("content", EncodeUtil.chinese2URLEncode(demandContent));
				map.put("category", EncodeUtil.chinese2URLEncode(demandClassify));
				map.put("userID", GlobalValues.USER_ID);
				map.put("deadline", ""+effictiveDate.getTime());
				map.put("group_id", groupID);
				HttpGet get = new HttpGet(NetUtil.getUrlString(map));
				HttpClient clinet = new DefaultHttpClient();
				HttpResponse response = clinet.execute(get);
				if(response.getStatusLine().getStatusCode()==200) {
					String json = StreamUtil.getString(response.getEntity().getContent());
					Gson gson = new Gson();
					return gson.fromJson(json, ProtocalResponse.class);
				} else {
					ToastUtil.showToast("网络连接错误，请稍后再试", PubRequireActivity.this);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(ProtocalResponse result) {
			if(result!=null&&result.getResponseCode()==0) {
				PubRequireActivity.this.finish();
				
				
			} else {
				ToastUtil.showToast(result.getResponse(), PubRequireActivity.this);
			}
		};
	}
	
	private class CreateGroupAsyncTast extends AsyncTask<Void, Void, EMGroup> {

		@Override
		protected EMGroup doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			try {
				String groupName = ""+effictiveDate.getTime();
				return EMGroupManager.getInstance().createPublicGroup(groupName, groupName, null, false);
				
			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(EMGroup result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result!=null) {
				groupID =result.getGroupId();
				new PubRequireAsyncTask().execute();
				
			}
		}
		
	}
	
	private void showPopupWindow() {
		if (popupWindow != null && !popupWindow.isShowing()) {

			popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
			//设置半透明
			WindowManager.LayoutParams params = getWindow().getAttributes();
			params.alpha = 0.7f;
			getWindow().setAttributes(params);
		} else if (popupWindow == null) {
			View popView = LayoutInflater.from(PubRequireActivity.this)
					.inflate(R.layout.choose_time_popwindow, null);
			popupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
			popupWindow.setOutsideTouchable(true);
			
			timePicker = (TimePicker)popView.findViewById(R.id.timePicker);
			timePicker.setIs24HourView(true);
			
			myDatePicker = (MyDatePicker) popView.findViewById(R.id.myDateNumberPicker1);
			
			
			Button cancelBtn = (Button)popView.findViewById(R.id.cancelBtn);
			cancelBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					closePopWindow();
				}
			});
			
			popEnsureBtn =  (Button) popView.findViewById(R.id.ensureBtn);
			popEnsureBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					effictiveDate = new Date(); 
					int month = myDatePicker.getMonth();
					int day = myDatePicker.getDay();
					int hour = timePicker.getCurrentHour();
					int minute = timePicker.getCurrentMinute();
					
					effictiveDate.setMonth(month-1);
					effictiveDate.setDate(day);
					effictiveDate.setHours(hour);
					effictiveDate.setMinutes(minute);
					
					effictiveTimeTv.setText(month+"月"+day+"日  "+hour+":"+minute);
					closePopWindow();
					
				}
			});
			
			WindowManager.LayoutParams params = getWindow().getAttributes();
			params.alpha = 0.7f;
			getWindow().setAttributes(params);

		}

	}
	
	private void closePopWindow() {
		popupWindow.dismiss();
		WindowManager.LayoutParams params=getWindow().getAttributes();  
	      params.alpha=1f;  
	      getWindow().setAttributes(params);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (popupWindow != null && popupWindow.isShowing()) {
			closePopWindow();

			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	

	public void back(View v) {
		finish();
	}
}
