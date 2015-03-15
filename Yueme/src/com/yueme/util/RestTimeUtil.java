package com.yueme.util;

public class RestTimeUtil {
	private RestTimeUtil(){};
	public static String getRestTime(long time) {
		long day, hour, minutes;
		day =  (time/1000/3600/24);
		time -= (day * 1000 * 3600 * 24);
		hour =  (time/3600/1000);
		time -= (hour * 3600*1000);
		minutes =  (time/1000/60);
		StringBuffer buffer = new StringBuffer();
		if(day>0) buffer.append(day+"天");
		if(hour>0) buffer.append(hour+"小时");
		if(minutes>0) buffer.append(minutes+"分");
		return buffer.toString();
	}
	public static String getRestTimeBySeconds(long time) {
		long day, hour, minutes,seconds;
		day =  (time/1000/3600/24);
		time -= (day * 1000 * 3600 * 24);
		hour =  (time/3600/1000);
		time -= (hour * 3600*1000);
		minutes =  (time/1000/60);
		time -= (minutes*1000*60);
		seconds = time/1000;
		StringBuffer buffer = new StringBuffer();
		buffer.append(day+"天");
		buffer.append(hour+"小时");
		buffer.append(minutes+"分");
		buffer.append(seconds+"秒");
		return buffer.toString();
	}
}
