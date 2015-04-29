package com.yueme.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.yueme.ChatGroupActivity;
import com.yueme.R;

public abstract class BaseEmotionsViewPagerAdapter extends PagerAdapter {
	private List<View> pagerViews;
	private Context context;
	protected GridView gv1;
	protected GridView gv2;

	public BaseEmotionsViewPagerAdapter(Context context) {
		super();
		this.context = context;
		pagerViews = new ArrayList<View>();
		int emotions1[] = { 0x1F604, 0x1F60a, 0x1F603, 0x1F632, 0x1F609,
				0x1F60D, 0x1F618, 0x1F61A, 0x1F633, 0x1F601, 0x1F60C, 0x1F61C,
				0x1F61D, 0x1F612, 0x1F60F, 0x1F613, 0x1F614, 0x1F61E, 0x1F616,
				0x1F625, 0x1F630 };
		int emotions2[] = { 0x1F628, 0x1F623, 0x1F622, 0x1F62D, 0x1F602,
				0x1F632, 0x1F631, 0x1F620, 0x1F621, 0x1F62A, 0x1F637, 0x1F37A,
				0x270C, 0x1F4A9, 0x1F44D, 0x1F525, 0x2728, 0x1F31F, 0x1F4AA,
				0x1F4A4, 0x1F3B5 };
		View v1 = LayoutInflater.from(context).inflate(
				R.layout.emotions_gridview, null);
		View v2 = LayoutInflater.from(context).inflate(
				R.layout.emotions_gridview, null);
		gv1 = (GridView) v1;
		gv2 = (GridView) v2;

		gv1.setAdapter(new EmotionsGridViewAdapter(emotions1));
		gv2.setAdapter(new EmotionsGridViewAdapter(emotions2));
		/*gv1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView tv = (TextView) view;
				et_msg.append(tv.getText());
				int len = et_msg.getText().length();
				et_msg.setSelection(len);
			}
		});

		gv2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView tv = (TextView) view;
				et_msg.append(tv.getText());
				int len = et_msg.getText().length();
				et_msg.setSelection(len);
			}
		});*/
		setEvents();
		pagerViews.add(v1);
		pagerViews.add(v2);
	}
	
	protected abstract void setEvents();

	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager) container).removeView(pagerViews.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub
		((ViewPager) container).addView(pagerViews.get(position));
		return pagerViews.get(position);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pagerViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	class EmotionsGridViewAdapter extends BaseAdapter {
		private int emotions[];

		private String emotionCode(int hax) {
			return String.valueOf(Character.toChars(hax));
		}

		public EmotionsGridViewAdapter(int[] emotions) {
			super();
			this.emotions = emotions;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return emotions.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return emotionCode(emotions[position]);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.emotion_griditem, null);
			}
			String emotion = emotionCode(emotions[position]);
			((TextView) convertView).setText(emotion);
			return convertView;
		}

	}

}