package com.yueme;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yueme.domain.Info;
import com.yueme.domain.ProtocalResponse;
import com.yueme.domain.User;
import com.yueme.util.NetUtil;
import com.yueme.util.RestTimeUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;

public class ParticipantsActivity extends Activity {
	private List<User> users;
	private ListView lv_participants;
	private TextView tv_createTime;
	private TextView tv_destTime;
	private Info info;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private ParticipantsAdapter adapter;
	private TextView tv_info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_participants);
		initView();
		getNetInfo();
		setListenerAndAdapter();
	}

	private void setListenerAndAdapter() {
		adapter = new ParticipantsAdapter();
		lv_participants.setAdapter(adapter);
	}

	public void back(View v) {
		finish();
	}
	private void getNetInfo() {
		info = (Info) getIntent().getSerializableExtra("info");
		tv_info.setText(info.getContent());
		tv_createTime.setText("创建时间："
				+ DateFormat.format("yyyy-MM-dd-hh-mm-ss",info.getCreate_day()));
		tv_destTime.setText("开始时间："
				+ DateFormat.format("yyyy-MM-dd-hh-mm-ss",info.getDeadline()));
		new ParticipantsAsyncTast().execute();
	}

	private void initView() {
		lv_participants = (ListView) findViewById(R.id.lv_participants);
		tv_createTime = (TextView) findViewById(R.id.tv_createTime);
		tv_destTime = (TextView) findViewById(R.id.tv_destTime);
		tv_info = (TextView) findViewById(R.id.tv_info);
	}

	private class ParticipantsAsyncTast extends
			AsyncTask<Void, Void, ProtocalResponse> {

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM,
						ConstantValues.GET_PARTICIPANTS + "");
				map.put("infoID", info.getId());
				HttpGet get = new HttpGet(NetUtil.getUrlString(map));
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					String json = StreamUtil.getString(response.getEntity()
							.getContent());
					return new Gson().fromJson(json, ProtocalResponse.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ProtocalResponse result) {
			if (result == null) {
				ToastUtil.showToast("网络错误", ParticipantsActivity.this);
			} else {
				String json = result.getResponse();
				users = new Gson().fromJson(json, new TypeToken<List<User>>() {
				}.getType());
				for (final User user : users) {
					new AsyncTask<Void, Void, Bitmap>() {

						@Override
						protected Bitmap doInBackground(Void... params) {
							return NetUtil.getBitmapFromServer(user
									.getHead_img_path());
						}

						protected void onPostExecute(Bitmap result) {
							bitmaps.add(result);
							adapter.notifyDataSetChanged();
						}
					}.execute();
				}
			}
		}
	}

	private class ParticipantsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(users!=null)
			return users.size();
			else return 0;
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
			View view = View.inflate(ParticipantsActivity.this,
					R.layout.participant_liset_item, null);
			ImageView iv_head = (ImageView) view.findViewById(R.id.iv_participant_head);
			TextView tv_nickname = (TextView) view.findViewById(R.id.tv_participant_nickname);
			TextView tv_academy = (TextView) view.findViewById(R.id.tv_participant_academy);
			if(bitmaps.size()>position)
			iv_head.setImageBitmap(bitmaps.get(position));
			tv_academy.setText((users.get(position).getAcademy()==null?"无学院":users.get(position).getAcademy()));
			tv_nickname.setText(users.get(position).getNickname());
			return view;
		}

	}
}
