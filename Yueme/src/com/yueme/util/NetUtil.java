package com.yueme.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yueme.values.ConstantValues;

public class NetUtil {
	private NetUtil() {
	};

	public static String getUrlString(Map<String, String> map) {
		String urlString = ConstantValues.DISPATCHING_URL + "?";
		boolean isFirst = true;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (!isFirst)
				urlString += "&";
			urlString += entry.getKey() + "=";
			urlString += entry.getValue();
			isFirst = false;
		}
		return urlString;
	}

	public static Bitmap getBitmapFromServer(final String path) {
		try {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put(ConstantValues.REQUESTPARAM, ConstantValues.GET_IMAGE+"");
			map.put("imgUrl", path);
			HttpGet get = new HttpGet(NetUtil.getUrlString(map));
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream inputStream = response.getEntity().getContent();
				return BitmapFactory.decodeStream(inputStream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	};
}
