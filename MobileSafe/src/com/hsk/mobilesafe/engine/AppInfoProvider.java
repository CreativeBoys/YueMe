package com.hsk.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.hsk.mobilesafe.domain.AppInfo;

/**
 * @author heshaokang	
 * 2014-12-18 ����12:32:58
 * ��ȡ�ֻ�������Ӧ�õ���Ϣ
 */
public class AppInfoProvider {
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<>();
		for(PackageInfo packageInfo:packageInfos) {
			AppInfo appInfo = new AppInfo();
			//packageInfo �൱��һ��Ӧ�õ��嵥�ļ�
			String packageName = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();
			int flags = packageInfo.applicationInfo.flags;
			if((flags&ApplicationInfo.FLAG_SYSTEM)==0) {
				//�û�����
				appInfo.setUserApp(true);
			}else {
				appInfo.setUserApp(false);
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0) {
				appInfo.setInRom(true);
			}else {
				//�ֻ��ⲿ�洢�豸
				appInfo.setInRom(false);
			}
			
			appInfo.setPackageName(packageName);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfos.add(appInfo);
		}
		return appInfos;
	}
}
