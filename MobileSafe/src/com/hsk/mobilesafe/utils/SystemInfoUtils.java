package com.hsk.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

/**
 * @author heshaokang	
 * 2014-12-20 ����8:14:57
 * ϵͳ��Ϣ������
 */
public class SystemInfoUtils {
	/**
	 * ����������еĽ��̵ĸ���
	 */
	public static int getRunningProcessCount(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		return infos.size();
	}
	
	/**
	 * ��ȡ�ֻ����õ�ʣ���ڴ�
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	/**
	 * ��ȡ�ֻ��ܵ��ڴ�
	 */
	public static long getTotalMem(Context context) {
		/*ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.totalMem;
		4.0�汾һ�� �������벻����
		*/
		File file = new File("/proc/meminfo");
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			for(char c:line.toCharArray()) {
				if(c>='0'&&c<='9') {
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			
			e.printStackTrace();
			return 0;
		}
		
	}
}
