package com.yueme;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.google.gson.Gson;
import com.yueme.domain.ProtocalResponse;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

public class LoginActivity extends SwipeBackActivity {
	ImageView backBtn;
	EditText userNameEt, passwordEt;
	Button loginBtn;
	TextView quickRegTv, forgetPasswordTv;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		backBtn = (ImageView) findViewById(R.id.backBtn);
		userNameEt = (EditText) findViewById(R.id.username_editText);
		passwordEt = (EditText) findViewById(R.id.password_editText);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		quickRegTv = (TextView) findViewById(R.id.quickRegisterTv);
		forgetPasswordTv = (TextView) findViewById(R.id.forgetPasswordTv);
		setListenerAndAdapter();

	}

	private void setListenerAndAdapter() {
		loginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, ProtocalResponse>() {

					@Override
					protected ProtocalResponse doInBackground(Void... params) {
						try {
							String password = passwordEt.getText().toString();
							String username = userNameEt.getText().toString();
							Map<String,String> map = new HashMap<String,String>();
							map.put(ConstantValues.REQUESTPARAM, ConstantValues.LOGIN+"");
							map.put("phone_number", username);
							map.put("password", password);
							System.out.println(NetUtil.getUrlString(map));
							HttpGet get = new HttpGet(NetUtil.getUrlString(map));
							HttpClient client = new DefaultHttpClient();
							HttpResponse response = client.execute(get);
							if(response.getStatusLine().getStatusCode()==200) {
								InputStream inputStream = response.getEntity().getContent();
								String json = StreamUtil.getString(inputStream);
								Gson gson = new Gson();
								return gson.fromJson(json, ProtocalResponse.class);
							}
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(ProtocalResponse result) {
						if(result==null) {
							ToastUtil.showToast("网络错误", LoginActivity.this);
							return;
						}
						if(result.getResponseCode()==0) {
							GlobalValues.USER_ID = result.getResponse();

							// 登录到聊天服务器
							EMChatManager.getInstance().login(userNameEt.getText().toString(), passwordEt.getText().toString(), new EMCallBack() {

								@Override
								public void onError(int arg0, final String errorMsg) {
									runOnUiThread(new Runnable() {
										public void run() {
											closeLoginProgressDialog();
											Toast.makeText(LoginActivity.this, "登录聊天服务器失败：" + errorMsg, Toast.LENGTH_SHORT).show();
										}
									});
								}

								@Override
								public void onProgress(int arg0, String arg1) {
								}

								@Override
								public void onSuccess() {
									runOnUiThread(new Runnable() {
										public void run() {
											closeLoginProgressDialog();
											ToastUtil.showToast("登陆成功", LoginActivity.this);
											Intent intent = new Intent(LoginActivity.this,MainActivity.class);
											startActivity(intent);
											finish();
										}
									});

								}
							});
							
							
						} else {
							ToastUtil.showToast(result.getResponse(), LoginActivity.this);
						}
					}
				}.execute();
				
				
			}
		});

		quickRegTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);

			}
		});

		forgetPasswordTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						FindPasswordActiviity.class);
				startActivity(intent);
			}
		});
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
	}
	
	private void showLoginProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在登陆...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	
	private void closeLoginProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

}
