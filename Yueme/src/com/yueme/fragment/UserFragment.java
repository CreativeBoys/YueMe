package com.yueme.fragment;

import java.io.InputStream;
import java.util.LinkedHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yueme.MyIntegrate;
import com.yueme.ParticipatedInfosActivity;
import com.yueme.R;
import com.yueme.SystemSettings;
import com.yueme.UserInformation;
import com.yueme.domain.ProtocalResponse;
import com.yueme.domain.User;
import com.yueme.fragment.base.BaseFragment;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

public class UserFragment extends BaseFragment {
	private ListView listView;
	private RelativeLayout head_layout;
	private TextView tv_name;
	private ImageView iv_head;
	private TextView tv_reputation;
	@Override
	protected void init() {
		listView = (ListView) findViewById(R.id.userListview);
		listView.setAdapter(new MyAdapter());		
		head_layout = (RelativeLayout) findViewById(R.id.head_layout);
		tv_reputation = (TextView) findViewById(R.id.tv_reputation);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		tv_name = (TextView) findViewById(R.id.tv_nickname);
		new GetUserHeadAsyncTask().execute();
	}

	@Override
	protected View initView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.fragment_user, null);
	}

	
	class MyAdapter extends BaseAdapter{
		int imgs[] = {R.drawable.sended	, R.drawable.joined, R.drawable.myscore	, R.drawable.setting};
		String names[] = {"发起的相约", "参与的相约", "我的积分", "系统设置"};
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imgs.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_fragment_listitem, null);
			ImageView icon = (ImageView)view.findViewById(R.id.itemIcon);
			TextView textView = (TextView)view.findViewById(R.id.itemTv);
			
			
			icon.setImageResource(imgs[position]);
			textView.setText(names[position]);
			return view;
		}
		
	}


	@Override
	protected void setListenerAndAdapter() {
		// TODO Auto-generated method stub
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				switch (position) {
				case 0:
					
					break;
				case 1:
					//参与的
					intent = new Intent(getActivity(),ParticipatedInfosActivity.class);
					startActivity(intent);
					break;
				case 2: //我的积分
					intent = new Intent(getActivity(),MyIntegrate.class);
					startActivity(intent);
					break;
				case 3:  			//系统设置		
					intent = new Intent(getActivity(),SystemSettings.class);
					startActivity(intent);
					break;
				default:
					break;
				}
				
			}
			
		});
		
		head_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),UserInformation.class);
				startActivity(intent);
			}
		});
	}
	private class GetUserHeadAsyncTask extends
	AsyncTask<Void, Void, ProtocalResponse> {

@Override
protected ProtocalResponse doInBackground(Void... params) {
	try {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put(ConstantValues.REQUESTPARAM,
				ConstantValues.GET_USER_ALL_INFO + "");
		map.put("userID", GlobalValues.USER_ID);
		HttpGet get = new HttpGet(NetUtil.getUrlString(map));
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		if (response.getStatusLine().getStatusCode() == 200) {
			InputStream inputStream = response.getEntity().getContent();
			String json = StreamUtil.getString(inputStream);
			Gson gson = new Gson();
			return gson.fromJson(json, ProtocalResponse.class);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

@Override
protected void onPostExecute(final ProtocalResponse result) {
	if (result.getResponseCode() == 0) {
		Gson gson = new Gson();
		final User user = gson.fromJson(result.getResponse(),
				User.class);
		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				
				return NetUtil.getBitmapFromServer(user
						.getHead_img_path());
			}

			protected void onPostExecute(Bitmap result) {
				Drawable drawable = new BitmapDrawable(result); // 转换成drawable
				if(result!=null)
					iv_head.setImageDrawable(drawable);
				tv_name.setText(user.getNickname());
			};
		}.execute();

	} else {
		ToastUtil.showToast(result.getResponse(), getActivity());
	}
}
}
}
