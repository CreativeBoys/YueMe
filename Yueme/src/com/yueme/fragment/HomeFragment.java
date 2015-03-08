package com.yueme.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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

import com.yueme.PubRequireActivity;
import com.yueme.R;
import com.yueme.SingleRequireDetailsActivity;
import com.yueme.fragment.base.BaseFragment;
import com.yueme.public_class.DemandItem;

public class HomeFragment extends BaseFragment {
	View fragmentView;
	List<DemandItem> demandListItems;
	PopupWindow popupWindow;
	Button homeAddBtn;
	ListView listView;

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		demandListItems = new ArrayList<DemandItem>();
		demandListItems.add(new DemandItem(R.drawable.user_head, "Mr He", "今天",
				"明天下午两点，在二教有木有一起要上自习的，可以联系我", "1小时"));
		demandListItems.add(new DemandItem(R.drawable.user_head, "Mrs SHE",
				"两小时前", "我是杭州的，有木有杭州的老乡啊，一块约会家吧", "2小时"));
		demandListItems.add(new DemandItem(R.drawable.user_head, "Mike", "昨天",
				"明天下午五点，有没有人一起去篮球场篮球的？", "1小时"));
		demandListItems.add(new DemandItem(R.drawable.user_head, "John",
				"1小时前", "今天上午12点有没有人一起约外卖，组队会省不少钱的..", "30分钟"));
		
		

	}

	@Override
	protected View initView(LayoutInflater inflater) {
		fragmentView = inflater.inflate(R.layout.fragment_home, null);
		homeAddBtn = (Button) fragmentView.findViewById(R.id.home_addBtn);
		listView = (ListView) fragmentView.findViewById(R.id.homeListView);
		return fragmentView;
	}

	@Override
	protected void setListenerAndAdapter() {
		// TODO Auto-generated method stub
		
		listView.setAdapter(new HomeListAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d("hello", "onitemclicked");
				
				if(popupWindow!=null && popupWindow.isShowing()) {
					closePopWindow();
				} else{
					Intent intent = new Intent(getActivity(), SingleRequireDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable("DEMAND_INFO", demandListItems.get(position));
					intent.putExtras(bundle);
					Log.d("hello", "position: "+position);
					Log.d("hello", ""+demandListItems.get(position));
					startActivity(intent);
					Log.d("hello", "itemclick");
				}
				
				
			}
		});

		homeAddBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopWindow();
			}
		});
		
		listView.setFocusable(false);

		fragmentView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				closePopWindow();
				return false;
			}
		});


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
			
			
			popupWindow.setOutsideTouchable(false);

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
					startActivity(intent);
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
					startActivity(intent);
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
					startActivity(intent);
					closePopWindow();
				}
			});
		}

	}

	public void closePopWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			listView.setFocusable(true);
		}
	}

	private class HomeListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return demandListItems.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.home_listitem, parent, false);
				viewHolder = new ViewHolder();

				viewHolder.img = (ImageView) convertView
						.findViewById(R.id.userHeadIcon);
				viewHolder.time = (TextView) convertView
						.findViewById(R.id.time);
				viewHolder.userName = (TextView) convertView
						.findViewById(R.id.userName);
				viewHolder.demandContent = (TextView) convertView
						.findViewById(R.id.demandContent);
				viewHolder.restTime = (TextView) convertView
						.findViewById(R.id.restTime);
				convertView.setTag(viewHolder);

			} else {

				viewHolder = (ViewHolder) convertView.getTag();
			}

			DemandItem demandItem = demandListItems.get(position);
			viewHolder.img.setImageResource(demandItem.icon_id);
			viewHolder.time.setText(demandItem.time);
			viewHolder.userName.setText(demandItem.userName);
			viewHolder.demandContent.setText(demandItem.demandContent);
			viewHolder.restTime.setText(demandItem.restTime);
			return convertView;
		}

		private class ViewHolder {
			ImageView img;
			TextView time;
			TextView userName;
			TextView demandContent;
			TextView restTime;

		}
	}

	

}
