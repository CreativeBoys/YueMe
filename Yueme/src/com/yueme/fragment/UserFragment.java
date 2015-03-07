package com.yueme.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yueme.R;
import com.yueme.fragment.base.BaseFragment;

public class UserFragment extends BaseFragment {
	private ListView listView;
	@Override
	protected void init() {
		listView = (ListView) findViewById(R.id.userListview);
		listView.setAdapter(new MyAdapter());	
		
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
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_item, null);
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
		
	}
}
