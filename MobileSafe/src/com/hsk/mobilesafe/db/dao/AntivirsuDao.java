package com.hsk.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author heshaokang	
 * 2014-12-27 ����9:45:28
 */
public class AntivirsuDao {
	/**
	 * ��ѯһ��md5�Ƿ��ڲ����ַ�����
	 */
	public static boolean isVirus(String md5) {
		String path = "/data/data/com.hsk.mobilesafe/files/antivirus.db";
		boolean result = false;
		//�򿪲������ݿ��ļ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
}
