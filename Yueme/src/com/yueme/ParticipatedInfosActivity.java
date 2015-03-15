package com.yueme;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yueme.domain.Info;
import com.yueme.domain.ProtocalResponse;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

public class ParticipatedInfosActivity extends Activity {
	private ListView lv_participated_infos;
	private List<Info> infos;
	private ParticipatedInfoAdapter adapter;
	private TextView tv_no_infos;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_participated_infos);
		lv_participated_infos = (ListView) findViewById(R.id.lv_participated_infos);
		tv_no_infos = (TextView) findViewById(R.id.tv_no_infos);
		setListenerAndAdapter();
		new AllParticipatedInfosAsyncTask().execute();
	}
	
	private void setListenerAndAdapter() {
		adapter = new ParticipatedInfoAdapter();
		lv_participated_infos.setAdapter(adapter);
		lv_participated_infos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ParticipatedInfosActivity.this,ParticipantsActivity.class);
				intent.putExtra("info", infos.get(position));
				startActivity(intent);
			}
		});
	}

	private class ParticipatedInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(infos==null) {
				return 0;
			}
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(ParticipatedInfosActivity.this, R.layout.participated_items,null);
			TextView tv_content = (TextView) view.findViewById(R.id.tv_participant_nickname);
			TextView tv_startTime = (TextView) view.findViewById(R.id.tv_start_time);
			ImageView iv_head = (ImageView) view.findViewById(R.id.iv_participant_head);
			tv_content.setText(infos.get(position).getContent());
			tv_startTime.setText(DateFormat.format("yyyy-MM-dd-hh时mm分ss秒", infos.get(position).getDeadline()));
			if(bitmaps.size()>position)
				iv_head.setImageBitmap(bitmaps.get(position));
			return view;
		}
	}
	
	private class AllParticipatedInfosAsyncTask extends AsyncTask<Void, Void,ProtocalResponse> {

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM, ConstantValues.GET_USER_ALL_PARTICIPATED_INFO+"");
				map.put("userID", GlobalValues.USER_ID);
				HttpGet get = new HttpGet(NetUtil.getUrlString(map));
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(get);
				if(response.getStatusLine().getStatusCode()==200) {
					String json = StreamUtil.getString(response.getEntity().getContent());
					return new Gson().fromJson(json, ProtocalResponse.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(ProtocalResponse result) {
			if(result!=null) {
				if(result.getResponseCode()==1) {
					//尚未参加
					tv_no_infos.setVisibility(View.VISIBLE);
				} else {
					infos = new Gson().fromJson(result.getResponse(), new TypeToken<List<Info>>(){}.getType());
					for (final Info info : infos) {
						new AsyncTask<Void, Void, Bitmap>() {

							@Override
							protected Bitmap doInBackground(Void... params) {
								try {
									String id = info.getId();
									LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
									map.put(ConstantValues.REQUESTPARAM,
											ConstantValues.GET_INFO_HEAD_IMG + "");
									map.put("infoID", id);
									HttpGet get = new HttpGet(NetUtil.getUrlString(map));
									HttpClient client = new DefaultHttpClient();
									HttpResponse response = client.execute(get);
									if (response.getStatusLine().getStatusCode() == 200) {
										InputStream inputStream = response.getEntity()
												.getContent();
										return BitmapFactory.decodeStream(inputStream);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								return null;
							}

							protected void onPostExecute(Bitmap result) {
								bitmaps.add(result);
								adapter.notifyDataSetChanged();
							};
						}.execute();
					}
				}
				adapter.notifyDataSetChanged();
			} else {
				ToastUtil.showToast("网络错误", ParticipatedInfosActivity.this);
			}
		}
	}
	
	public void back(View view) {
		finish();
	}
}
