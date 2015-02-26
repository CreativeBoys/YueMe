package com.hsk.mobilesafe;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hsk.mobilesafe.utils.MD5Utils;
/**
 * @author heshaokang	
 * 2014-11-29 ����10:49:36
 * �������д���֮ǰһֱ���� ԭ������д��findViewByIdǰ��д��view �����Ҳ����Ǹ�item ���Կ�ָ���쳣
 * TextView tv_item = (TextView)view.findViewById(R.id.tv_item);
 * ֮ǰû��д��仰 ���ո����� ��˵Editor editor = sp.edit() ����ָ���쳣 ���
 * ������SharedPreferences�������а�
 * sp = getSharedPreferences("config", MODE_PRIVATE);
 * 
 */
public class HomeActivity extends Activity{
	protected static final String TAG = "HomeActivity";
	private GridView gridView;
	private MyAdapter myAdapter;
	private SharedPreferences sp;
	private static String [] names = {
		"�ֻ�����","ͨѶ��ʿ","�������",
		"���̹���","����ͳ��","�ֻ�ɱ��",
		"��������","�߼�����","��������"
		
	};
	private static int[] ids = {
		R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
		R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
		R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gridView = (GridView) findViewById(R.id.gridView);
		myAdapter = new MyAdapter();
		gridView.setAdapter(myAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent ;
				switch (position) {
				case 0:
					//�����ֻ�����ҳ��
					showLostFindDialog();
					break;
				case 1:
					intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
					startActivity(intent);
					break;
				case 2:
					intent = new Intent(HomeActivity.this,AppManager.class);
					startActivity(intent);
					break;
				case 3:
					intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 5:
					intent = new Intent(HomeActivity.this,AntiVirusActivity.class);
					startActivity(intent);
					break;
				case 6:
					intent = new Intent(HomeActivity.this,CleanCacheActivity.class);
					startActivity(intent);
					break;
				case 7:
					 intent = new Intent(HomeActivity.this,AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8:
					 intent = new Intent(HomeActivity.this,SettingActivity.class);
					startActivity(intent);
					break;
			
				
				default:
					break;
				}
			}
		});
	}
	/**
	 * �ж��Ƿ����ù�����
	 */
	private boolean isSetupPwd() {
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}
	protected void showLostFindDialog() {
		//�ж��Ƿ����ù�����
		if(isSetupPwd()) {
			showEnterDialog();
		}else {
			showSetupPwdDialog();
		}
		
		
	}
	

	private EditText et_setup_pwd;
	private EditText et_setup_confirm;
	private Button ok;
	private Button cancel;
	private AlertDialog dialog;
	/**
	 * ��������Ի���
	 */
	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//�õ����������
				String password = MD5Utils.md5Password(et_setup_pwd.getText().toString().trim());
				//ȡ���洢������
				String savePassword = sp.getString("password", null);
				if(password.equals(savePassword)) {
					dialog.dismiss();
					Log.i(TAG, "�ѶԻ��������������ֻ�����ҳ��");
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
				}else {
					Toast.makeText(HomeActivity.this, "�������", Toast.LENGTH_SHORT).show();
					et_setup_pwd.setText("");
					return;
				}
			}
		});
		dialog = builder.create();
		dialog.setView(view);
		dialog.show();
	}
	/**
	 * 
	 * @author heshaokang	
	 * 2014-12-4 ����10:45:09
	 * ��������Ի���
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		//�Զ���һ�������ļ�
		View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//�õ����������
				String password = et_setup_pwd.getText().toString().trim();
				String pwd_confirm = et_setup_confirm.getText().toString().trim();
				if(TextUtils.isEmpty(password)||TextUtils.isEmpty(pwd_confirm)) {
					Toast.makeText(HomeActivity.this, "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
					return ;
				}
				//�ж��Ƿ�һ��
				if(password.equals(pwd_confirm)) {
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.md5Password(password));//������ܺ��
					editor.commit();
					dialog.dismiss();
					Log.i(TAG, "һ�µĻ����ͱ������룬�ѶԻ�����������Ҫ�����ֻ�����ҳ��");
					//����һ�µĻ� �����ֻ�����ҳ��
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
					
				}else {
					Toast.makeText(HomeActivity.this, "���벻һ��", Toast.LENGTH_SHORT).show();
					return ;
				}
			}
		});
		dialog = builder.create();
		dialog.setView(view,0,0,0,0);
		dialog.show();
	}
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			
			return null;
		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view= View.inflate(HomeActivity.this, R.layout.list_item_home, null);
			TextView tv_item = (TextView)view.findViewById(R.id.tv_item);
			tv_item.setText(names[position]);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			iv_item.setImageResource(ids[position]);
			return view;
		}
		
	}
}
