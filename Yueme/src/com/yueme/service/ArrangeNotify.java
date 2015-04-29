package com.yueme.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.yueme.R;
import com.yueme.util.RestTimeUtil;

/**
 * @author heshaokang	
 * 2015-3-21 下午2:21:11
 */
public class ArrangeNotify extends Service {
	private List<Map<String,Object>> list;
	private long deadline=0;
	private NotificationManager manager;
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
	@Override
	public void onCreate() {
		
		super.onCreate();
	   
	    list = new ArrayList<Map<String,Object>>();
		System.out.println("onCreate");
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = new Bundle();
		bundle = intent.getExtras();
		String info_id = bundle.getString("info_id");
		deadline = bundle.getLong("deadline");
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("info_id", info_id);
		map.put("deadline", deadline);
		list.add(map);
		for(int i=0;i<list.size();i++) {
			System.out.println("get---"+list.get(i));
			HashMap<String,Object> map1 ;
			map1 = (HashMap<String, Object>) list.get(i);
			info_id = (String) map1.get("info_id");
			deadline = (Long) map1.get("deadline");
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					String restTime = RestTimeUtil.getRestTimeBySeconds((deadline-new Date().getTime()));
					if(restTime.equals("1800")) {
					manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					Notification notification = new Notification();
					notification.icon = R.drawable.ic_launcher;
					notification.tickerText = "约么提醒您，您参与的活动还有半个小时结束哦";
					}
				}
			}).start();
			
		}
		return super.onStartCommand(intent, flags, startId);
		
	}
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}
}
