package com.yueme;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author heshaokang	
 * 2015-3-17 上午10:21:06
 * 信誉达人
 */
public class CreditPeople extends SwipeBackActivity {
	private ListView mListView ;
	private List<PeopleInfo> data;
	private MyAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creditpeople);
		init();
	}
	
	private void init() {
		mListView = (ListView) findViewById(R.id.listView);
		data = new ArrayList<CreditPeople.PeopleInfo>();
		PeopleInfo p1 = new PeopleInfo(R.drawable.user_head,R.drawable.level_num,"一级星","56","Mr He");
		PeopleInfo p2 = new PeopleInfo(R.drawable.user_head,R.drawable.level_num,"一级星","36","Mr Hh");
		PeopleInfo p3 = new PeopleInfo(R.drawable.user_head,R.drawable.level_num,"二级星","32","Mr Li");
		data.add(p1);
		data.add(p2);
		data.add(p3);
		adapter = new MyAdapter();
		mListView.setAdapter(adapter);
	}
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			
			return data.size();
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
			ViewHolder holder = null;
			if(convertView==null) {
				convertView = View.inflate(getApplicationContext(),R.layout.creditpeople_item, null);
				holder = new ViewHolder();
				holder.level_num = (ImageView) convertView.findViewById(R.id.level_num);
				holder.user_head = (ImageView) convertView.findViewById(R.id.user_head);
				holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
				holder.level = (TextView) convertView.findViewById(R.id.level);
				holder.integrate = (TextView) convertView.findViewById(R.id.integrate);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.level_num.setImageResource(data.get(position).level_num);
			holder.user_head.setImageResource(data.get(position).id);
			holder.nickname.setText(data.get(position).nickname);
			holder.level.setText(data.get(position).level);
			holder.integrate.setText(data.get(position).integrate);
			
			return convertView;
		}
		
	}
	private static class ViewHolder {
		ImageView level_num;
		ImageView user_head;
		TextView nickname;
		TextView level;
		TextView integrate;
	}
	
	class PeopleInfo{
		private int id;  //头像id
		private int level_num; //名次
		private String level; //等级
		private String integrate; //积分
		private String nickname;  //昵称
		public PeopleInfo(int id,int level_num,String level,String integrate,String nickname) {
			this.id = id ;
			this.level_num = level_num;
			this.level = level;
			this.nickname = nickname;
			this.integrate = integrate;
			
		}
		
	}
}
