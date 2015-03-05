package com.yueme.util;

import java.util.Map;

import com.yueme.values.ConstantValues;

public class NetUtil {
	private NetUtil(){};
	public static String getUrlString(Map<String, String> map) {
		String urlString = ConstantValues.DISPATCHING_URL+"?";
		boolean isFirst = true;
		for(Map.Entry<String, String> entry : map.entrySet()) {
			if(!isFirst)
				urlString += "&";
			urlString+=entry.getKey()+"=";
			urlString+=entry.getValue();
			isFirst = false;
		}
		return urlString;
	}
}
