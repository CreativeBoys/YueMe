package com.hsk.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.hsk.mobilesafe.utils.StreamTools;

public class SplashActivity extends Activity {
	protected static final String TAG = "SplashActivity";
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private String description;
	private TextView tv_update_info;
	//�°汾�����ص�ַ
	private String apkurl;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("�汾��:"+getVersionName());
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		boolean update = sp.getBoolean("update", false);
		//��һ������ʱ ������ݷ�ʽ 
		installShortCut();
		//�������ݿ�
		copyDB("address.db");
		copyDB("antivirus.db");
		if(update) {
			//�������
			checkUpdate();
		}else {
			//�Զ������ر�
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(500);
		findViewById(R.id.rl_root_splash).startAnimation(aa);
	}
	private void installShortCut() {
		boolean shortcut = sp.getBoolean("shortcut", false);
		if(shortcut)
			return;
		Editor editor = sp.edit();
		//���͹㲥����ͼ�� ���һ���������棬Ҫ�������ͼ����
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//��ݷ�ʽ  Ҫ����3����Ҫ����Ϣ 1������ 2.ͼ�� 3.��ʲô����
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ�С��ʿ");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.apppic));
		//������ͼ���Ӧ����ͼ��
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(), "com.hsk.mobilesafe.SplashActivity");


		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sendBroadcast(intent);
		editor.putBoolean("shortcut", true);
		editor.commit();
		
	}
	/**
	 * �������������ݿ�
	 */
	private void copyDB(String filename) {
		
		try {
			File file = new File(getFilesDir(),filename);
			if(file.exists()&& file.length()>0) {
				Log.i("SplasgActivity", "����Ҫ������");
			}else {
				InputStream is = getAssets().open(filename);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] bytes = new byte[1024];
				int len =0;
				while((len=is.read(bytes))!=-1) {
					fos.write(bytes,0,len);
				}
				is.close();
				fos.close();
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	private  Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ENTER_HOME:  //����������
				enterHome();
				
				break;
			case URL_ERROR:
				enterHome();
				Toast.makeText(getApplicationContext(), "url ����", Toast.LENGTH_SHORT).show();
				break;
			case JSON_ERROR:
				enterHome();
				Toast.makeText(getApplicationContext(), "json��������", Toast.LENGTH_SHORT).show();
				break;
			case NETWORK_ERROR:
				enterHome();
				Toast.makeText(getApplicationContext(), "�������", Toast.LENGTH_SHORT).show();
				break;
			case SHOW_UPDATE_DIALOG:
				Log.i(TAG, "��ʾ�����ĶԻ���");
				showUpdateDialog();
				break;
			default:
				break;
			}
			
		}
	};
	/**
	 * ��ʾ�����ĶԻ���
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
		builder.setTitle("������ʾ");
		builder.setMessage(description);
		
		
		builder.setNegativeButton("�´���˵",new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();
			}
		});
		builder.setPositiveButton("��������", new OnClickListener() {
			
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				
						//����apk ���滻��װ �����õ��˿�Դ���صĿ�� �ж��Ƿ���sd�� 
						if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
							FinalHttp finalHttp = new FinalHttp();
							//���ص�sd����ַ
							String target = Environment.getExternalStorageDirectory().getAbsolutePath()+"mobilesafe2.0.apk";
							System.out.println(apkurl);
							finalHttp.download(apkurl, target, new AjaxCallBack<File>() {
								//������ʧ�� ����ô˷���
								public void onFailure(Throwable t, int errorNo, String strMsg) {
									t.printStackTrace();
									Toast.makeText(getApplicationContext(), "����ʧ��", Toast.LENGTH_SHORT).show();
									super.onFailure(t, errorNo, strMsg);
									
								};
								//������
								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									tv_update_info.setVisibility(View.VISIBLE);
									//��ǰ���صİٷֱ�
									int progress = (int) (current*100/count);
									tv_update_info.setText("���ؽ���:"+progress+"%");
								};
								public void onSuccess(File t) {
									super.onSuccess(t);
									installApk(t);
									
								};
								//��װapk
								private void installApk(File t) {
									//����ϵͳ����ͼ����apk�İ�װ
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t),"application/vnd.android.package-archive");
									startActivity(intent);
								}
							});
						}else {
							Toast.makeText(getApplicationContext(), "û��sd������ȷ����װ��sd��", Toast.LENGTH_SHORT).show();
							dialog.dismiss();
							enterHome();
						}
						
					}
		});
		builder.show();
	}
	/**
	 * ����������
	 */
	private void enterHome() {
		Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
		startActivity(intent);
		//�رո�ҳ�� ������˼����ڴ���ʾ��ҳ�棻
		finish();
	};
	/**
	 * ���汾����
	 */
	private void checkUpdate() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				Message mes = Message.obtain();
				try {
					URL url = new URL(getString(R.string.server_url));
					//����
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					//��������ʽ һ��Ҫ��д
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					int code = conn.getResponseCode();
					if(code==200) {
						//�����ɹ�
						InputStream is = conn.getInputStream();
						//�������� װ��ΪString
						String result = StreamTools.readFromStream(is);
						//Log.i(TAG, "�����ɹ��ˣ�"+result);
						//json����
						//����֮ǰû�д�json�ַ�������  ����һֱ�д� ̫����
						JSONObject obj = new JSONObject(result);
						//�õ��������İ汾��Ϣ
						String version =  obj.getString("version");
						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");
						//�����Ƿ����°汾
						if(getVersionName().equals(version)) {
							mes.what = ENTER_HOME;
						}else {
							mes.what = SHOW_UPDATE_DIALOG;
						}
					}
				} catch (MalformedURLException e) {
					mes.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					mes.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					mes.what = JSON_ERROR;
					e.printStackTrace();
				}finally {
					long endTime = System.currentTimeMillis();
					long dTime = endTime-startTime;
					if(dTime<2000) {
						try {
							Thread.sleep(2000-dTime);
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
					}
					handler.sendMessage(mes);
				}
				
				
			}
		}).start();
		
	}
	/**
	 * �õ�Ӧ�ó���İ汾����
	 */
	private String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			return pi.versionName;
		}catch(NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
}
