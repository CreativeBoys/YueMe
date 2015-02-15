package com.hsk.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.hsk.mobilesafe.service.AddressService;
import com.hsk.mobilesafe.service.CallSmsSafeService;
import com.hsk.mobilesafe.service.WatchDogService;
import com.hsk.mobilesafe.ui.SettingClickView;
import com.hsk.mobilesafe.ui.SettingItemView;
import com.hsk.mobilesafe.utils.ServiceUtils;

/**
 * @author heshaokang	
 * 2014-12-1 ����2:43:13
 */
public class SettingActivity extends Activity{
	private SettingItemView siv_update;
	private SharedPreferences sp;
	//�����Ƿ�����ʾ������
	private SettingItemView siv_show_address;
	private Intent showAddress;
	//���ù�������ʾ�򱳾�
	private SettingClickView scv_changebg;
	//���ú���������
	private SettingItemView siv_callsms_safe;
	private Intent callSms;
	//������������
	private SettingItemView siv_watchdog;
	private Intent watchDogInetnt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		boolean update = sp.getBoolean("update", false);
		if(update) {
			if(update){
				//�Զ������Ѿ�����
				siv_update.setChecked(true);
				//siv_update.setDesc("�Զ������Ѿ�����");
			}else{
				//�Զ������Ѿ��ر�
				siv_update.setChecked(false);
				//siv_update.setDesc("�Զ������Ѿ��ر�");
			}
		}
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				//�����Ƿ��Ѿ�ѡ��
				if(siv_update.isChecked()) {
					siv_update.setChecked(false);
					//siv_update.setDesc("�Զ������Ѿ��ر�");
					editor.putBoolean("update", false);
					
					
				}else {
					siv_update.setChecked(true);
					//siv_update.setDesc("�Զ������Ѿ�����");
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		
		
		
				//���ú���������
				siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
				callSms = new Intent(this,CallSmsSafeService.class);
				boolean iscallSmsServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, 
						"com.hsk.mobilesafe.service.CallSmsSafeService");
				if(iscallSmsServiceRunning) {
					siv_callsms_safe.setChecked(true);
				}else {
					siv_callsms_safe.setChecked(false);
				}
				siv_callsms_safe.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(siv_callsms_safe.isChecked()) {
							siv_callsms_safe.setChecked(false);
							stopService(callSms);
						}else {
							siv_callsms_safe.setChecked(true);
							startService(callSms);
						}
					}
				});
		
		
		
		
		
		//���ú����������ʾ
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		showAddress = new Intent(this,AddressService.class);
		boolean isServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, 
				"com.hsk.mobilesafe.service.AddressService");
		if(isServiceRunning) {
			siv_show_address.setChecked(true);
		}else {
			siv_show_address.setChecked(false);
		}
		siv_show_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(siv_show_address.isChecked()) {
					siv_show_address.setChecked(false);
					stopService(showAddress);
				}else {
					siv_show_address.setChecked(true);
					startService(showAddress);
				}
			}
		});
		
		
		
		//���ú�������صı���
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("�����ر�����ʾ���");
		final String [] items = {"��͸��","������","��ʿ��","������","ƻ����"};
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int dd = sp.getInt("which", 0);
				//�����Ի���
				AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
				builder.setTitle("�����ط��ѡ���");
				builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						//����ѡ��Ĳ���
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						scv_changebg.setDesc(items[which]);
						dialog.dismiss();
					}
					
				});
				builder.setNegativeButton("cancel", null);
				builder.show();
			}
		});
		
		//����������Ŀ���
				siv_watchdog = (SettingItemView) findViewById(R.id.siv_watchdog);
				watchDogInetnt = new Intent(this,WatchDogService.class);
				boolean isWatchDogServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, 
						"com.hsk.mobilesafe.service.WatchDogService");
				if(isWatchDogServiceRunning) {
					siv_watchdog.setChecked(true);
				}else {
					siv_watchdog.setChecked(false);
				}
				siv_watchdog.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(siv_watchdog.isChecked()) {
							siv_watchdog.setChecked(false);
							stopService(watchDogInetnt);
						}else {
							siv_watchdog.setChecked(true);
							startService(watchDogInetnt);
						}
					}
				});
				
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showAddress = new Intent(this, AddressService.class);
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.hsk.mobilesafe.service.AddressService");
		
		if(isServiceRunning){
			//��������ķ����ǿ�����
			siv_show_address.setChecked(true);
		}else{
			siv_show_address.setChecked(false);
		}
		
		boolean iscallSmsServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.hsk.mobilesafe.service.CallSmsSafeService");
		siv_callsms_safe.setChecked(iscallSmsServiceRunning);
		
	}
}
