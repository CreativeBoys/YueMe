package com.yueme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import com.yueme.domain.Info;

public class CountingService extends Service {
	private static List<Info> infos = new ArrayList<Info>();
	private static List<Boolean> hasShow5Minutes = new ArrayList<Boolean>();
	private static boolean isCounting = true;
	@Override
	public IBinder onBind(Intent intent) {
		return new CountingManager();
	}

	@Override
	public void onCreate() {
		new Thread(){
			public void run() {
				while(isCounting) {
					SystemClock.sleep(1000);
					if(infos!=null&&infos.size()>0) {
						for(int i = 0; i < infos.size();i++) {
							if(infos.get(i).getDeadline()-new Date().getTime()<5*60*1000&&!hasShow5Minutes.get(i)) {
								showFiveMinutesNotification(i);
								hasShow5Minutes.set(i, true);
								break;
							} else if(infos.get(i).getDeadline()-new Date().getTime()<=0) {
								showBeginTip(i);
								hasShow5Minutes.remove(i);
								infos.remove(i);
							}
						}
					}
				}
			}

		}.start();
		super.onCreate();
	}
	
	private void showBeginTip(int i) {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, "您参与的相约开始了", System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL;
		Intent intent = new Intent(this,ParticipantsActivity.class);
		intent.putExtra("info", infos.get(i));
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
		notification.setLatestEventInfo(this, "约么提醒", "您参与的"+infos.get(i).getCategory()+"开始了", contentIntent);
		nm.notify(497626362, notification);
	};
	private void showFiveMinutesNotification(int i) {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, "您参与的相约还有5分钟就要开始了哦", System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL;
		Intent intent = new Intent(this,ParticipantsActivity.class);
		intent.putExtra("info", infos.get(i));
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
		notification.setLatestEventInfo(this, "约么提醒", "您参与的"+infos.get(i).getCategory()+"还有5分钟就要开始了", contentIntent);
		nm.notify(497626363, notification);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	public class CountingManager extends Binder {
		public void addInfo(Info info) {
			for (Info i : infos) {
				if(i.getId()==info.getId())
					return;
			}
			infos.add(info);
			hasShow5Minutes.add(false);
		}
	}
}
