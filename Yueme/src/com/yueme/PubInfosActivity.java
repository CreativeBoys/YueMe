package com.yueme;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yueme.domain.Info;
import com.yueme.domain.ProtocalResponse;
import com.yueme.task.GeneralGetAsyncTask;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

public class PubInfosActivity extends Activity {
	private ListView lv_pub_infos;
	private List<Info> infos = new ArrayList<Info>();
	private TextView tv_infos;
	private PubInfoAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pub_infos);
		tv_infos = (TextView) findViewById(R.id.tv_no_infos);
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put(ConstantValues.REQUESTPARAM, ConstantValues.GET_ALL_PUB_INFO + "");
		map.put("userID", GlobalValues.USER_ID);
		new GeneralGetAsyncTask() {
			@Override
			public void doOnPost(ProtocalResponse response) {
				if (response != null) {
						infos = new Gson().fromJson(response.getResponse(),
								new TypeToken<List<Info>>() {
								}.getType());
						adapter.notifyDataSetChanged();
				} else {
					tv_infos.setVisibility(View.VISIBLE);
				}
					
				
			}
		}.execute(map);
		lv_pub_infos = (ListView) findViewById(R.id.lv_pub_infos);
		adapter = new PubInfoAdapter();
		lv_pub_infos.setAdapter(adapter);
		lv_pub_infos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(PubInfosActivity.this,ParticipantsActivity.class);
				intent.putExtra("info", infos.get(position));
				intent.putExtra("showButton", false);
				startActivity(intent);
			}
		});

	}
	private class PubInfoAdapter extends BaseAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(PubInfosActivity.this,
					R.layout.lv_pub_info_item, null);
			TextView tv_content = (TextView) view
					.findViewById(R.id.tv_pub_content);
			TextView tv_startTime = (TextView) view
					.findViewById(R.id.tv_pub_time);
			TextView tv_end_time = (TextView) view
					.findViewById(R.id.tv_end_time);
			tv_content.setText(infos.get(position).getContent());
			tv_startTime.setText("创建时间:"+DateFormat.format("yyyy-MM-dd:hh时mm分",
					infos.get(position).getCreate_day()));
			tv_end_time.setText("结束时间:"+DateFormat.format("yyyy-MM-dd:hh时mm分",
					infos.get(position).getDeadline()));
			return view;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return infos.size();
		}
	}
	
	public void back(View v) {
		this.finish();
	}
}
