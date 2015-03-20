package com.yueme;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.google.gson.JsonSyntaxException;
import com.verificationcodelib.api.VerificationCode;
import com.verificationcodelib.listener.UcsReason;
import com.verificationcodelib.listener.VerificationCodeListener;
import com.yueme.domain.ProtocalResponse;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

public class RegisterActivity extends SwipeBackActivity {
	private ImageView backBtn;
	private EditText phoneNumEt, verifiEt, passwordEText, et_nickname;
	private Button btn_register,verifyBtn;
	private TextView hadAcccountTv, forgetPasswordTv;
	private boolean vfc = true;
	private boolean vfc_result = false;
	private Timer mTimer = null;
	private ProgressDialog progressDialog;
	private static boolean FLAG=false; //用来判断能否注册的条件
	private String phone_number;
	private String password;
	private String nickname;
	private String verifyCode; //短信验证码
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
				overridePendingTransition(0, R.anim.base_slide_right_out);
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
		//发送验证码
		verifyBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCallTimer();
				verifyBtn.setClickable(false);
				RequestVerificationCode();
				System.out.println("发送验证码");
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
				justForm();
				StartVerificationCode();
				if(FLAG==false) {
					ToastUtil.showToast("请将信息填写完整", RegisterActivity.this);
					return ;
				}
				showRegisterProgressDialog();
				new CreateYueMeAccountTask().execute();
				
				
			}
		});
		
	}
	/**
	 * 
	 * @author heshaokang	
	 * 2015-3-19 下午7:02:35
	 * 判断所有表单是否已填写
	 */
	private void justForm() {
		phone_number = phoneNumEt.getText().toString();
		password = passwordEText.getText().toString();
		nickname = et_nickname.getText().toString();
		verifyCode = verifiEt.getText().toString().trim();
		if(phone_number==null||password==null||nickname==null||verifyCode==null) {
			FLAG = false;
		}
	}
	private class CreateYueMeAccountTask extends AsyncTask<Void,Void, ProtocalResponse>{
		;
		
		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				
				phone_number = phoneNumEt.getText().toString();
				password = passwordEText.getText().toString();
				nickname = et_nickname.getText().toString();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM, ConstantValues.REGISTER+"");
				map.put("phone_number", phone_number);
				map.put("password", password);
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
				Log.d("hello", "注册成功");
				CreateChatAccountTask task = new CreateChatAccountTask();
				
				task.execute(GlobalValues.USER_ID, password);
				
			} else {
				ToastUtil.showToast(result.getResponse(), RegisterActivity.this);
			}
		}
	}
	
	private class CreateChatAccountTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... args) {
			String userid = args[0];
			String pwd = args[1];
			try {
				EMChatManager.getInstance().createAccountOnServer(userid, pwd);
				Log.d("hello", "创建chat账号成功！");
				//progressDialog.setMessage("正在登录...");
				Log.d("hello", "uid: "+userid+";pwd: "+pwd);
				// 登录到聊天服务器
				EMChatManager.getInstance().login(userid, pwd, new EMCallBack() {

					@Override
					public void onError(int arg0, final String errorMsg) {
						runOnUiThread(new Runnable() {
							public void run() {
								Log.d("hello","登录聊天服务器失败");
								closeRegisterProgressDialog();
								Toast.makeText(RegisterActivity.this, "登录聊天服务器失败：" + errorMsg, Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void onProgress(int arg0, String arg1) {
						Log.d("hello","onProgress");
					}

					@Override
					public void onSuccess() {
						runOnUiThread(new Runnable() {
							public void run() {
								
								closeRegisterProgressDialog();
								SharedPreferences sp = getSharedPreferences("yueme", MODE_PRIVATE);
								Editor edit = sp.edit();
								edit.putString("userID", GlobalValues.USER_ID);
								edit.commit();
								startActivity(new Intent(RegisterActivity.this, MainActivity.class));
								Log.d("hello", "登录成功！");
								//Toast.makeText(RegisterActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
								finish();
							}
						});

					}
				});
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return userid;
		}
	}
	
	private void showRegisterProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在注册...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	
	private void closeRegisterProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	/**
	 * 请求验证码
	 */
	private void RequestVerificationCode() {
		
		final String phoneNumber = phoneNumEt.getText().toString().trim();
		if(vfc) {
			vfc = false; //防止对此点击发送按钮
			verifyBtn.setText("60秒后重发");
			if(phoneNumber!=null && phoneNumber.length()>0) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							System.out.println("请求验证码");
							System.out.println("phone="+phoneNumber);
							StringBuffer sbf = new StringBuffer();
							sbf.append(ConstantValues.HOST+"/servlet/VerifyPhoneNumberServlet?phoneNumber="+phoneNumber);   //验证手机号是否注册接口
							//若未注册 返回签名,作为请求验证码的参数
							HttpGet get = new HttpGet(sbf.toString());
							HttpClient client = new DefaultHttpClient();
							HttpResponse response = client.execute(get);
							if(response.getStatusLine().getStatusCode()==200) {
								ProtocalResponse resp = new Gson().fromJson(StreamUtil.getString(response.getEntity().getContent()), ProtocalResponse.class);
								if(resp.getResponseCode()==0) {
									//未注册
									String key = resp.getResponse();
									System.out.println("返回签名"+key);
									getVerificationCode(phoneNumber,key);
								} else {
									//已注册
									
									ToastUtil.showToast("该手机号已注册", RegisterActivity.this);
									return;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
		
	}
	
	/**
	 * 获得验证码
	 */
	private void  getVerificationCode(String phone,String sign) {
		VerificationCode.getVerificationCode(RegisterActivity.this, sign,"39f696d3d0bf962335af97293a5200d0", "35f6f0033d354885a76bc495ae874d42", "com.yueme", 1, 1, phone, new VerificationCodeListener() {
			
			@Override
			public void onVerificationCode(int arg0, UcsReason arg1) {
				vfc = true;
				if(arg1.getReason()== 300250) {
					Message message = Message.obtain();
					switch (arg0) {
					case 0:
						vfc_result=true;
						mUiHandler.sendEmptyMessage(1);
						message.obj = "短信";
						message.what = 1;
						mUiHandler.sendMessage(message);
						break;
				
					default:
						break;
					}
				}else {
					switch (arg1.getReason()) {
					case 300251:
						mRequestHandler.sendEmptyMessage(1);
						break;
					case 300252:
						mRequestHandler.sendEmptyMessage(2);
						break;
					case 300253:
						mRequestHandler.sendEmptyMessage(3);
						break;
					case 300254:
						mRequestHandler.sendEmptyMessage(4);
						break;
					case 300255:
						mRequestHandler.sendEmptyMessage(5);
						break;
					case 300256:
						mRequestHandler.sendEmptyMessage(6);
						break;
					case 300257:
						mRequestHandler.sendEmptyMessage(7);
						break;
					case 300258:
						mRequestHandler.sendEmptyMessage(8);
						break;
					case 300259:
						mRequestHandler.sendEmptyMessage(9);
						break;
					case 300260:
						mRequestHandler.sendEmptyMessage(10);
						break;
					default:
						mRequestHandler.sendEmptyMessage(99);
						break;
					}
				}
			}
		});
	}
	
	/**
	 * 开始对验证码进行验证
	 */
	private void StartVerificationCode() {
		if(vfc_result) {
			vfc_result = false;
			String phone = phoneNumEt.getText().toString().trim();
			String verify_code = verifiEt.getText().toString().trim();
			VerificationCode.doVerificationCode(RegisterActivity.this, phone, verify_code, "39f696d3d0bf962335af97293a5200d0", "35f6f0033d354885a76bc495ae874d42", new VerificationCodeListener() {
				
				@Override
				public void onVerificationCode(int arg0, UcsReason arg1) {
					switch (arg1.getReason()) {
					case 300250:
						//验证成功
						mRequestHandler.sendEmptyMessage(0);
						mUiHandler.sendEmptyMessage(1);
						break;
					case 300251:
						mRequestHandler.sendEmptyMessage(1);
						break;
					case 300252:
						mRequestHandler.sendEmptyMessage(2);
						break;
					case 300253:
						mRequestHandler.sendEmptyMessage(3);
						break;
					case 300254:
						mRequestHandler.sendEmptyMessage(4);
						break;
					case 300255:
						mRequestHandler.sendEmptyMessage(5);
						break;
					case 300256:
						mRequestHandler.sendEmptyMessage(6);
						break;
					case 300257:
						mRequestHandler.sendEmptyMessage(7);
						break;
					case 300258:
						mRequestHandler.sendEmptyMessage(8);
						break;
					case 300259:
						mRequestHandler.sendEmptyMessage(9);
						break;
					case 300260:
						mRequestHandler.sendEmptyMessage(10);
						break;
					default:
						mRequestHandler.sendEmptyMessage(99);
						break;
					}
					vfc_result=true;
				}
			});
		}
	}
	/**
	 * http 请求反馈
	 */
	private Handler mRequestHandler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			System.out.println("Message:"+msg.what);
			switch(msg.what){
				case 0:
					Toast.makeText(RegisterActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(RegisterActivity.this, "开发者账号无效", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
					return;
				case 3:
					Toast.makeText(RegisterActivity.this, "验证码过期", Toast.LENGTH_SHORT).show();
					return ;
				case 4:
					Toast.makeText(RegisterActivity.this, "30秒内重复请求", Toast.LENGTH_SHORT).show();
					return;
				case 5:
					Toast.makeText(RegisterActivity.this, "签名错误", Toast.LENGTH_SHORT).show();
					break;
				case 6:
					Toast.makeText(RegisterActivity.this, "手机号码无效", Toast.LENGTH_SHORT).show();
					return ;
				case 7:
					Toast.makeText(RegisterActivity.this, "已经注册过", Toast.LENGTH_SHORT).show();
					return;
				case 8:
					Toast.makeText(RegisterActivity.this, "未创建智能短信模板", Toast.LENGTH_SHORT).show();
					break;
				case 9:
					Toast.makeText(RegisterActivity.this, "短信模板有误，需要检查是否创建智能验证短信模板，模板审核、参数", Toast.LENGTH_SHORT).show();
					break;
				case 10:
					Toast.makeText(RegisterActivity.this, "应用状态有误，需要检查应用是否审核通过、是否上线", Toast.LENGTH_SHORT).show();
					break;
				case 99:
					Toast.makeText(RegisterActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};
	
	private Handler mUiHandler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
			
			switch (msg.what) {
			case 0:
				int sencond = 60-(Integer) msg.obj;
				verifyBtn.setText(sencond+"后重新发送");
				if(sencond==0) {
					verifyBtn.setText("重新获取验证码");
				}
				break;
			case 1: //验证成功
				FLAG = true;
				break;
			default:
				break;
			}
		}
		
	};
	
	/**
	 * 验证码定时器
	 */
	private int sencond = 0;
	public void startCallTimer() {
		sencond = 0;
		
		if(mTimer==null) {
			mTimer = new Timer();
		}
		mTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				sencond++;
				if(sencond>60) {
					sencond = 60;
					stopCallTimer();
					vfc = true;
					mUiHandler.sendEmptyMessage(0);
					verifyBtn.setClickable(true);
				}
				Message message = Message.obtain();
				message.what = 0;
				message.obj = sencond;
				mUiHandler.sendMessage(message);
			}
		}, 0, 1000);
	}
	
	/**
	 * 停止计时
	 */
	private void stopCallTimer() {
		if(mTimer!=null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
}
