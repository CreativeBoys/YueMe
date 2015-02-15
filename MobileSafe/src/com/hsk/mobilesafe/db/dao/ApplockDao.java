package com.hsk.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hsk.mobilesafe.db.ApplockDBOpenHelper;

/**
 * @author heshaokang	
 * 2014-12-23 ����6:39:46
 * ����������ɾ�ò��ҵ���߼�
 */
public class ApplockDao {
	private ApplockDBOpenHelper helper;
	private Context context;
	/**
	 * ���췽��
	 * @param context
	 */
	public ApplockDao(Context context) {
		helper = new ApplockDBOpenHelper(context);
		this.context = context;
	}
	/**
	 * ���Ҫ�����ĳ���İ��������ݿ���
	 */
	public void add(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		db.insert("applock", null, values);
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.hsk.mobilesafe.applockchange");
		context.sendBroadcast(intent);
	}
	/**
	 * ���� ��ɾ�����ݿ���Ӧ�ó���İ���
	 */
	public void delete(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packname=?", new String[]{packname});
		db.close();
		
		Intent intent = new Intent();
		intent.setAction("com.hsk.mobilesafe.applockchange");
		context.sendBroadcast(intent);
	}
	/**
	 * ��ѯһ�������� �����Ƿ����
	 */
	public boolean find(String packname) {
		boolean flag = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
		if(cursor.moveToNext()) {
			flag = true;
		}
		cursor.close();
		db.close();
		return flag;
	}
	public List<String> findAll(){
		List<String> protectPacknames = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packname"}, null,null, null, null, null);
		while(cursor.moveToNext()){
			protectPacknames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return protectPacknames;
	}
	
} 
