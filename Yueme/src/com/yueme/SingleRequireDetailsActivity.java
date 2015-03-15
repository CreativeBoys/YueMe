package com.yueme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Global;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.yueme.domain.Info;
import com.yueme.domain.ProtocalResponse;
import com.yueme.domain.User;
import com.yueme.util.NetUtil;
import com.yueme.util.RestTimeUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

public class SingleRequireDetailsActivity extends Activity implements OnClickListener{
	protected static final int COUNT_DOWN = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case COUNT_DOWN:
				tv_counter.setText(RestTimeUtil.getRestTimeBySeconds((deadline - new Date().getTime())));
				handler.sendEmptyMessageDelayed(COUNT_DOWN, 1000);
				break;

			default:
				break;
			}
		};
	};
	private TextView tv_counter;
	private Button btn_reply;
	private Button btn_participate;
	private long deadline;
	private Info info;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	public List<User> users; 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_require_activity);
		btn_participate = (Button) findViewById(R.id.yueBtn);
		btn_reply = (Button) findViewById(R.id.replyBtn);
		btn_participate.setOnClickListener(this);
		initView();
	}
	
	private void initView(){
		ImageView img = (ImageView) findViewById(R.id.userHeadIcon);
		TextView time = (TextView) findViewById(R.id.time);
		TextView userName = (TextView) findViewById(R.id.userName);
		TextView demandContent = (TextView)findViewById(R.id.demandContent);
		tv_counter = (TextView) findViewById(R.id.tv_counter); 
		info = (Info) getIntent().getSerializableExtra("info");
		String createTime="";
		long create_day = info.getCreate_day();
		if (DateUtils.isToday(create_day)) {
			createTime = "今天";
		} else {
			createTime = (String) DateFormat.format("yyyy-MM-dd",
					create_day);
		}
		time.setText(createTime);
		deadline = info.getDeadline();
		userName.setText(info.getNickname());
		demandContent.setText(info.getContent());
		img.setImageBitmap((Bitmap) getIntent().getParcelableExtra("bitmap"));
		handler.sendEmptyMessageDelayed(COUNT_DOWN, 1000);
		new CheckIsParticipatedAsyncTask().execute();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.yueBtn:
			if(btn_participate.getText().toString().equals("约起")) {
			btn_participate.setBackgroundDrawable(getResources().getDrawable(R.drawable.cancel_bg));
			btn_participate.setText("取消");
			new AsyncTask<Void, Void, ProtocalResponse>() {

				@Override
				protected ProtocalResponse doInBackground(Void... params) {
					try {
						LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
						map.put(ConstantValues.REQUESTPARAM, ConstantValues.ADD_PARTICIPANTS+"");
						map.put("userID", GlobalValues.USER_ID);
						map.put("infoID", info.getId());
						HttpGet get = new HttpGet(NetUtil.getUrlString(map));
						HttpClient client = new DefaultHttpClient();
						HttpResponse response = client.execute(get);
						if(response.getStatusLine().getStatusCode()==200) {
							String json = StreamUtil.getString(response.getEntity().getContent());
							Gson gson = new Gson();
							return gson.fromJson(json, ProtocalResponse.class);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(ProtocalResponse result) {
					if(result!=null) {
						ToastUtil.showToast(result.getResponse(), SingleRequireDetailsActivity.this);
					} else {
						ToastUtil.showToast("网络错误", SingleRequireDetailsActivity.this);
					}
					new ParticipantsAsyncTast().execute();
					Intent intent = new Intent(SingleRequireDetailsActivity.this,ParticipantsActivity.class);
					intent.putExtra("info", info);
					startActivity(intent);
					finish();
				}
			}.execute();
			} else {
				
				new AsyncTask<Void, Void, ProtocalResponse>() {

					@Override
					protected ProtocalResponse doInBackground(Void... params) {
						try {
							LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
							map.put(ConstantValues.REQUESTPARAM, ConstantValues.DELETE_PARTICIPANTS+"");
							map.put("userID", GlobalValues.USER_ID);
							map.put("infoID", info.getId());
							HttpGet get = new HttpGet(NetUtil.getUrlString(map));
							HttpClient client = new DefaultHttpClient();
							HttpResponse response = client.execute(get);
							if(response.getStatusLine().getStatusCode()==200) {
								String json = StreamUtil.getString(response.getEntity().getContent());
								Gson gson = new Gson();
								return gson.fromJson(json, ProtocalResponse.class);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(ProtocalResponse result) {
						if(result!=null) {
							ToastUtil.showToast(result.getResponse(), SingleRequireDetailsActivity.this);
						} else {
							ToastUtil.showToast("网络错误", SingleRequireDetailsActivity.this);
						}
					}
				}.execute();
				btn_participate.setBackgroundDrawable(getResources().getDrawable(R.drawable.details_yue_bg));
				btn_participate.setText("约起");
			}
			
			break;
			
		default:
			break;
		}
	}
	
	
	
	private class CheckIsParticipatedAsyncTask extends AsyncTask<Void, Void, ProtocalResponse> {

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM, ConstantValues.GET_USER_IS_PARTICIPATED+"");
				map.put("userID", GlobalValues.USER_ID);
				map.put("infoID", info.getId());
				HttpGet get = new HttpGet(NetUtil.getUrlString(map));
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(get);
				if(response.getStatusLine().getStatusCode()==200) {
					String json = StreamUtil.getString(response.getEntity().getContent());
					return new Gson().fromJson(json, ProtocalResponse.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(ProtocalResponse result) {
			if(result==null) {
				ToastUtil.showToast("网络连接错误", SingleRequireDetailsActivity.this);
			} else {
				if(result.getResponseCode()==0) {
					//参加了
					btn_participate.setBackgroundDrawable(getResources().getDrawable(R.drawable.cancel_bg));
					btn_participate.setText("取消");
				} else {
					//未参加
				}
			}
		}
	}
	/**
	 * 点击相约后推送消息
	 * @param view
	 */
	
	public void onSendTxtMsg(String user_id,String userName) {
		EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
		
		msg.setReceipt(user_id);
		TextMessageBody body = new TextMessageBody(userName);
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
	
	private class ParticipantsAsyncTast extends
	AsyncTask<Void, Void, ProtocalResponse> {

@Override
protected ProtocalResponse doInBackground(Void... params) {
	try {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put(ConstantValues.REQUESTPARAM,
				ConstantValues.GET_PARTICIPANTS + "");
		map.put("infoID", info.getId());
		HttpGet get = new HttpGet(NetUtil.getUrlString(map));
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		if (response.getStatusLine().getStatusCode() == 200) {
			String json = StreamUtil.getString(response.getEntity()
					.getContent());
			return new Gson().fromJson(json, ProtocalResponse.class);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

@Override
protected void onPostExecute(ProtocalResponse result) {
	if (result == null) {
		ToastUtil.showToast("网络错误", SingleRequireDetailsActivity.this);
	} else {
		String json = result.getResponse();
		users = new Gson().fromJson(json, new TypeToken<List<User>>() {
		}.getType());
		for (final User user : users) {
			onSendTxtMsg(user.getId(),user.getNickname());
		}
	}
}
}
}
