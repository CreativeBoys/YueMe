package com.yueme.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yueme.PubRequireActivity;
import com.yueme.R;
import com.yueme.fragment.base.BaseFragment;

public class HomeFragment extends BaseFragment {
	View fragmentView;
	List<DemandItem> demandListItems;
	PopupWindow popupWindow;
	Button homeAddBtn;

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		demandListItems = new ArrayList<HomeFragment.DemandItem>();
		demandListItems.add(new DemandItem(R.drawable.user_head, "Mr He", "今天",
				"约自习", "1小时"));

	}

	@Override
	protected View initView(LayoutInflater inflater) {
		fragmentView = inflater.inflate(R.layout.fragment_home, null);
		homeAddBtn = (Button) fragmentView.findViewById(R.id.home_addBtn);
		return fragmentView;
	}

	@Override
	protected void setListenerAndAdapter() {
		// TODO Auto-generated method stub
		ListView listView = (ListView) fragmentView
				.findViewById(R.id.homeListView);
		listView.setAdapter(new HomeListAdapter());
		homeAddBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopWindow();
			}
		});

		fragmentView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				closePopWindow();
				return false;
			}
		});
		
		listView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				closePopWindow();
				return false;
			}
		});
	}

	private void showPopWindow() {
		if(popupWindow!=null && !popupWindow.isShowing() ){
			popupWindow.showAtLocation(fragmentView, Gravity.LEFT, 300, 0);
			
		} else if(popupWindow==null){
			View popView = LayoutInflater.from(getActivity()).inflate(
					R.layout.home_center_popwindow, null);
			popupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			popupWindow.showAtLocation(fragmentView, Gravity.LEFT, 300, 0);
			popupWindow.setFocusable(false);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setOutsideTouchable(true);
			
			Button yue_learningBtn = (Button)popView.findViewById(R.id.yue_learning);
			Button yue_homeBtn = (Button)popView.findViewById(R.id.yue_home);
			Button yue_moreBtn = (Button)popView.findViewById(R.id.yue_more);
			
			yue_learningBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),PubRequireActivity.class);
					intent.putExtra("CLASSIFY", "约自习");
					startActivity(intent);
				}
			});
			
			yue_homeBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),PubRequireActivity.class);
					intent.putExtra("CLASSIFY", "约回家");
					startActivity(intent);
					
				}
			});
			
			yue_moreBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),PubRequireActivity.class);
					intent.putExtra("CLASSIFY", "约其他");
					startActivity(intent);
				}
			});
		}
		

	}

	public void closePopWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	class HomeListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return demandListItems.size();
		}

		@Override
		public Object getItem(int position) {
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
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.home_listitem, null);
			ImageView img = (ImageView) view.findViewById(R.id.userHeadIcon);
			TextView time = (TextView) view.findViewById(R.id.time);
			TextView userName = (TextView) view.findViewById(R.id.userName);
			TextView demandContent = (TextView) view
					.findViewById(R.id.demandContent);
			TextView restTime = (TextView) view.findViewById(R.id.restTime);

			DemandItem demandItem = demandListItems.get(position);
			img.setImageResource(demandItem.icon_id);
			time.setText(demandItem.time);
			userName.setText(demandItem.userName);
			demandContent.setText(demandItem.demandContent);
			restTime.setText("还剩" + demandItem.restTime);

			return view;
		}

	}

	class DemandItem {
		int icon_id;
		String userName;
		String time;
		String demandContent;
		String restTime;

		public DemandItem(int icon_id, String userName, String time,
				String demandContent, String restTime) {
			this.icon_id = icon_id;
			this.userName = userName;
			this.time = time;
			this.demandContent = demandContent;
			this.restTime = restTime;
		}

	}

}
