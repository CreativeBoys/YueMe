package com.yueme.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yueme.PubRequireActivity;
import com.yueme.R;
import com.yueme.SingleRequireDetailsActivity;
import com.yueme.domain.Info;
import com.yueme.domain.ProtocalResponse;
import com.yueme.fragment.base.BaseFragment;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.values.ConstantValues;

public class HomeFragment extends BaseFragment {
	public static List<Info> infos;
	private HomeListAdapter adapter;
	View fragmentView;
	PopupWindow popupWindow;
	Button homeAddBtn;
	ListView listView;
	SwipeRefreshLayout swipeRefresh;
	Handler refreshHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_COMPLETED:
				swipeRefresh.setRefreshing(false);
				break;
			}
		}

	};
	private static final int REFRESH_COMPLETED = 1;
	public static final int PUBLISH_COMPLETED = 10;

	@Override
	protected void init() {
		listView = (ListView) findViewById(R.id.homeListView);
		infos = new ArrayList<Info>();
		getInfo();

		swipeRefresh
				.setColorScheme(android.R.color.holo_blue_bright,
						android.R.color.holo_green_light,
						android.R.color.holo_blue_light,
						android.R.color.holo_red_light);

	}

	@Override
	protected View initView(LayoutInflater inflater) {
		fragmentView = inflater.inflate(R.layout.fragment_home, null);
		homeAddBtn = (Button) fragmentView.findViewById(R.id.home_addBtn);
		listView = (ListView) fragmentView.findViewById(R.id.homeListView);
		swipeRefresh = (SwipeRefreshLayout) fragmentView
				.findViewById(R.id.home_refresh);

		return fragmentView;
	}

	@Override
	protected void setListenerAndAdapter() {
		adapter = new HomeListAdapter();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (popupWindow != null && popupWindow.isShowing()) {
					closePopWindow();
				} else {
					Intent intent = new Intent(getActivity(),
							SingleRequireDetailsActivity.class);
					intent.putExtra("info", infos.get(position));
					startActivity(intent);
				}
			}
		});
		
		swipeRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getInfo();
			}
		});
		
		
		homeAddBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopWindow();
			}
		});
	}

	class HomeListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
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
			View view;
			ViewHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = LayoutInflater.from(getActivity()).inflate(
						R.layout.home_fragment_listitem, null);
				holder = new ViewHolder();
				holder.iv_head = (ImageView) view
						.findViewById(R.id.userHeadIcon);
				holder.tv_create_time = (TextView) view.findViewById(R.id.time);
				holder.tv_nickname = (TextView) view
						.findViewById(R.id.userName);
				holder.tv_rest_time = (TextView) view
						.findViewById(R.id.restTime);
				holder.tv_content = (TextView) view
						.findViewById(R.id.tv_content);
				view.setTag(holder);
			}
			holder.tv_content.setText(infos.get(position).getContent());
			String createTime;
			long create_day = infos.get(position).getCreate_day();
			if (DateUtils.isToday(create_day)) {
				createTime = "今天";
			} else {
				createTime = (String) DateFormat.format("yyyy-MM-dd",
						create_day);
			}
			holder.tv_create_time.setText(createTime);
			long deadline = infos.get(position).getDeadline();
			holder.tv_rest_time.setText("还有" + (deadline - new Date().getTime()) / (3600*1000)
					+ "小时");
			holder.tv_nickname.setText(infos.get(position).getNickname());
			return view;
		}

		private class ViewHolder {
			private ImageView iv_head;
			private TextView tv_nickname;
			private TextView tv_create_time;
			private TextView tv_content;
			private TextView tv_rest_time;
		}
	}

	private void getInfo() {
		new HomeAsyncTask().execute();
	}
	
	private class HomeAsyncTask extends AsyncTask<Void, Void, List<Info>> {
		@Override
		protected void onPreExecute() {
			swipeRefresh.setRefreshing(true);
		}
		@Override
		protected List<Info> doInBackground(Void... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM,
						ConstantValues.GET_HOME_PAGE_INFO + "");
				HttpGet get = new HttpGet(NetUtil.getUrlString(map));
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					String json = StreamUtil.getString(response.getEntity()
							.getContent());
					Gson gson = new Gson();
					return gson.fromJson(json, new TypeToken<List<Info>>() {
					}.getType());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Info> result) {
			for (Info info : result) {
				System.out.println(info.toString());
			}
			infos = result;
			listView.setAdapter(adapter);
			swipeRefresh.setRefreshing(false);
		}
		
	}
	
	
	private void showPopWindow() {
		if (popupWindow != null && !popupWindow.isShowing()) {
			popupWindow.showAtLocation(fragmentView, Gravity.LEFT, 300, 0);

		} else if (popupWindow == null) {
			View popView = LayoutInflater.from(getActivity()).inflate(
					R.layout.home_center_popwindow, null);
			popupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			popupWindow.showAtLocation(fragmentView, Gravity.LEFT, 300, 0);
			popupWindow.setOutsideTouchable(true);
			Button yue_learningBtn = (Button) popView
					.findViewById(R.id.yue_learning);
			Button yue_homeBtn = (Button) popView.findViewById(R.id.yue_home);
			Button yue_moreBtn = (Button) popView.findViewById(R.id.yue_more);

			yue_learningBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							PubRequireActivity.class);
					Bundle bundle = new Bundle();

					bundle.putString("CLASSIFY", "约自习");
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
					closePopWindow();
				}
			});

			yue_homeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							PubRequireActivity.class);
					intent.putExtra("CLASSIFY", "约回家");
					startActivityForResult(intent, 1);
					closePopWindow();

				}
			});

			yue_moreBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							PubRequireActivity.class);
					intent.putExtra("CLASSIFY", "约其他");
					startActivityForResult(intent, 1);
					closePopWindow();
				}
			});
		}

	}
	public boolean closePopWindow() {

		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			listView.setFocusable(true);
			return true;
		}
		return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		new HomeAsyncTask().execute();
	}
	
}
