package com.hsk.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hsk.mobilesafe.service.UpdateWidgetService;

/**
 * @author heshaokang	
 * 2014-12-22 ����3:40:00
 * ������ϲ���ʱ û����ʾ���� ������֪�� ԭ�����밲װ���ֻ��ڴ��ϲ��� ֮ǰ���嵥�ļ���д�����ⲿ��װ preferExternal
 */
public class MyWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("MyWidget","onReceive....");
		Intent i = new Intent(context,UpdateWidgetService.class);
		context.startService(i);
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i("MyWidget","OnUdate....");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	/**
	 * ���ؼ���ק����Ļ��ʱ ���� ֻҪ����Ļ����ʾ ��ֻ����һ��
	 */
	@Override
	public void onEnabled(Context context) {
		Log.i("MyWidget","��������....");
		Intent intent = new Intent(context,UpdateWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}
	/**
	 * ���ؼ�ɾ��ʱ����
	 */
	@Override
	public void onDisabled(Context context) {
		Log.i("MyWidget","�رշ���....");
		Intent intent = new Intent(context,UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}
	
}
