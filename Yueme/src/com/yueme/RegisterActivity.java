package com.yueme;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yueme.domain.ProtocalResponse;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterActivity extends Activity {
	private ImageView backBtn;
	private EditText phoneNumEt, verifiEt, passwordEText, et_nickname;
	private Button verifyBtn,btn_register;
	private TextView hadAcccountTv, forgetPasswordTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		init();
	}

	private void init() {
		backBtn = (ImageView) findViewById(R.id.backBtn);
		phoneNumEt = (EditText) findViewById(R.id.phoneNumEt);
		verifiEt = (EditText) findViewById(R.id.verifiEt);
		passwordEText = (EditText) findViewById(R.id.passwordEt);
		verifyBtn = (Button) findViewById(R.id.verifiBtn);
		hadAcccountTv = (TextView) findViewById(R.id.hadAccountTv);
		forgetPasswordTv = (TextView) findViewById(R.id.forgetPasswordTv);
		et_nickname = (EditText) findViewById(R.id.et_nickname);
		btn_register = (Button) findViewById(R.id.btn_register);
		setListenerAndAdapter();

	}

	private void setListenerAndAdapter() {
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		hadAcccountTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});

		forgetPasswordTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RegisterActivity.this,
						FindPasswordActiviity.class);
				startActivity(intent);
				finish();
			}
		});
		
		btn_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AsyncTask<Void,Void, ProtocalResponse>() {
					@Override
					protected ProtocalResponse doInBackground(Void... params) {
						try {
							String phone_number = phoneNumEt.getText().toString();
							String passsword = passwordEText.getText().toString();
							String nickname = et_nickname.getText().toString();
							HashMap<String, String> map = new HashMap<String, String>();
							map.put(ConstantValues.REQUESTPARAM, ConstantValues.REGISTER+"");
							map.put("phone_number", phone_number);
							map.put("password", passsword);
							map.put("nickname", nickname);
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
						} catch (JsonSyntaxException e) {
							e.printStackTrace();
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}
				
					@Override
					protected void onPostExecute(ProtocalResponse result) {
						if(result==null) {
							ToastUtil.showToast("网络异常", RegisterActivity.this);
							return;
						}
						if(result.getResponseCode()==0) {
							GlobalValues.USER_ID = result.getResponse();
							Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
							startActivity(intent);
						} else {
							ToastUtil.showToast(result.getResponse(), RegisterActivity.this);
						}
					}
				}.execute();
			}
		});
		
	}

}
