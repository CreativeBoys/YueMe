package com.hsk.mobilesafe;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hsk.mobilesafe.db.dao.AntivirsuDao;

/**
 * @author heshaokang	
 * 2014-12-27 ����9:15:40
 */
public class AntiVirusActivity extends Activity {
	private ImageView iv_scan;
	private ProgressBar progressBar;
	private TextView tv_scan_status;
	private LinearLayout ll_container;
	private PackageManager pm;
	protected static final int SCANING = 0;
	protected static final int FINISH = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		RotateAnimation ra = new RotateAnimation(
				0, 360, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.startAnimation(ra);
		scanVirus();
	}
	/**
	 * ɨ�財��
	 */
	private void scanVirus() {
		pm = getPackageManager();
		tv_scan_status.setText("���ڳ�ʼ��ɱ������");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				progressBar.setMax(infos.size());
				int progress = 0;
				for(PackageInfo info:infos) {
					//apk�ļ�������·��
					String sourceDir = info.applicationInfo.sourceDir;
					String md5 = getFileMd5(sourceDir);
					ScanInfo scanInfo = new ScanInfo();
					scanInfo.packname = info.packageName;
					scanInfo.name =  info.applicationInfo.loadLabel(pm).toString();
					if(AntivirsuDao.isVirus(md5)) {
						scanInfo.isvirus = true;
					}else {
						scanInfo.isvirus = false;
					}
					
					Message msg = Message.obtain();
					msg.obj = scanInfo;
					msg.what = SCANING;
					handler.sendMessage(msg);
					//Ϊ�˲��ò�ѯ��̫�� ��˯��200����
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
					progress++;
					progressBar.setProgress(progress);
					
				}
				//ɨ�����
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			}
		}).start();
	}
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCANING:
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_scan_status.setText("����ɨ��:"+scanInfo.name);
				TextView tv = new TextView(getApplicationContext());
				if(scanInfo.isvirus){
					tv.setTextColor(Color.RED);
					tv.setText("���ֲ�����"+scanInfo.name);
				}else{
					tv.setTextColor(Color.BLACK);
					tv.setText("ɨ�谲ȫ��"+scanInfo.name);
				}
				ll_container.addView(tv,0);
				break;
			case FINISH:
				tv_scan_status.setText("ɨ�����");
				iv_scan.clearAnimation();
				break;
			default:
				break;
			}
			
		};
	};
	/**
	 * ɨ����Ϣ���ڲ���
	 */
	class ScanInfo{
		String packname;
		String name;
		boolean isvirus;
	}
	private String getFileMd5(String path){
		try {
			// ��ȡһ���ļ���������Ϣ��ǩ����Ϣ��
			File file = new File(path);
			// md5
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			StringBuffer sb  = new StringBuffer();
			for (byte b : result) {
				// ������
				int number = b & 0xff;// ����
				String str = Integer.toHexString(number);
				// System.out.println(str);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			fis.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
