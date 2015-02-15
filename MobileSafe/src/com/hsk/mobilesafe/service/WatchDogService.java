package com.hsk.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.hsk.mobilesafe.EnterPwdActivity;
import com.hsk.mobilesafe.db.dao.ApplockDao;

/**
 * @author heshaokang	
 * 2014-12-23 ����9:14:12
 * �����ֻ������г��������״̬
 */
public class WatchDogService extends Service {
	private ActivityManager am;
	private boolean flag;
	private ApplockDao dao;
	private InnerReceiver innerReceiver;
	private String tempStopProtectPackname;
	private List<String> protectPacknames;
	private ScreenOffReceiver offreceiver;
	private Intent intent;
	private DataChangeReceiver dataChangeReceiver;
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
	private class ScreenOffReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectPackname = null;
		}
	}
	
	private class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���յ�����ʱֹͣ�����Ĺ㲥�¼�");
			tempStopProtectPackname = intent.getStringExtra("packname");
		}
	}
	
	private class DataChangeReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���ݿ�����ݱ仯�ˡ�����");
			protectPacknames = dao.findAll();
		}
	}
	@Override
	public void onCreate() {
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, new IntentFilter("com.hsk.mobilesafe.tempstop"));
		dataChangeReceiver = new DataChangeReceiver();
		registerReceiver(dataChangeReceiver, new IntentFilter("com.hsk.mobilesafe.applockchange"));
		 am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		 flag = true;
		 dao = new ApplockDao(this);
		 protectPacknames = dao.findAll();
		 intent = new Intent(WatchDogService.this,EnterPwdActivity.class);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(flag) {
					List<RunningTaskInfo> infos = am.getRunningTasks(100);
					String name = infos.get(0).topActivity.getPackageName();
					//System.out.println("��ǰ������Ӧ�ó���Ϊ:"+name);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
						
					if(protectPacknames.contains(name)) {
						if(name.equals(tempStopProtectPackname)) {
							
						}else {
							intent.putExtra("packname", name);
							startActivity(intent);
						}
					}
				}
				
			}
		}).start();
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(innerReceiver);
		innerReceiver = null;
		unregisterReceiver(offreceiver);
		offreceiver = null;
		unregisterReceiver(dataChangeReceiver);
		dataChangeReceiver = null;
		super.onDestroy();
	}
}
