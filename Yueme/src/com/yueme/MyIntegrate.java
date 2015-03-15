package com.yueme;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author heshaokang	
 * 2015-3-15 下午3:25:45
 */
public class MyIntegrate extends SwipeBackActivity{
	private List<Integrate> mlist;
	private ListView mListView;
	private MyAdapter mAdapter;
	private ImageView backBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_integrate);
		init();
	}
	
	
	private void init() {
		Integrate itme1 = new Integrate("登陆成功", "2014年1月15号", "+5");
		Integrate itme2 = new Integrate("成功赴约","2014年2月12号","+10");
		mlist = new ArrayList<Integrate>();
		mlist.add(itme1);
		mlist.add(itme2);
		mListView = (ListView) findViewById(R.id.integrate_list);
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
	}
	
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			
			return mlist.size();
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
			ViewHolder holder=null;
			if(convertView==null) {
				holder = new ViewHolder();
				convertView = View.inflate(MyIntegrate.this, R.layout.integrate_item, null);
				holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				holder.tv_score = (TextView) convertView.findViewById(R.id.tv_score);
				
				
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_content.setText(mlist.get(position).content);
			holder.tv_time.setText(mlist.get(position).time);
			holder.tv_score.setText(mlist.get(position).score);
			return convertView;
		}
		
	}
	private static class ViewHolder {
		private TextView tv_content;
		private TextView tv_time;
		private TextView tv_score;
	}
	class Integrate {
		private String content;
		private String time;
		private String score;
		public Integrate(String content,String time,String score) {
			this.content = content;
			this.time = time;
			this.score = score;
		}
	}
}
