package com.yueme.util;

import java.net.URLEncoder;

public class EncodeUtil {
	private EncodeUtil(){};
	public static String chinese2URLEncode(String str) {
		return URLEncoder.encode(URLEncoder.encode(str));
	}
}
