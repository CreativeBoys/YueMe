package com.yueme.task;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yueme.domain.ProtocalResponse;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.values.ConstantValues;

public abstract class GeneralGetAsyncTask extends AsyncTask<Object, Void, ProtocalResponse>{
	public abstract void doOnPost(ProtocalResponse response);
	@Override
	protected ProtocalResponse doInBackground(Object... params) {
		try {
			HttpClient client = new DefaultHttpClient();
			Map<String, String> map = (Map<String, String>) params[0];
			HttpGet get = new HttpGet(NetUtil.getUrlString(map));
			HttpResponse response = client.execute(get);
			if(response.getStatusLine().getStatusCode()==200) {
				return new Gson().fromJson(StreamUtil.getString(response.getEntity().getContent()),ProtocalResponse.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(ProtocalResponse result) {
		super.onPostExecute(result);
		doOnPost(result);
	}
}
