package com.yueme.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yueme.CreditPeople;
import com.yueme.R;
import com.yueme.fragment.base.BaseFragment;

public class DiscoveryFragment extends BaseFragment{
	private ListView listView;
	@Override
	protected void init() {
		listView = (ListView) findViewById(R.id.discoveryListView);
		listView.setAdapter(new MyAdapter());		
	}

	@Override
	protected View initView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.fragment_discovery, null);
	}
	
	class MyAdapter extends BaseAdapter{
		int imgs[] = {R.drawable.score_star, R.drawable.creditworthiness, R.drawable.new_thing, R.drawable.surroud};
		String names[] = {"积分商城", "信誉达人", "新鲜事", "附近"};
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
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.discovery_fragment_listitem, null);
			ImageView icon = (ImageView)view.findViewById(R.id.itemIcon);
			TextView textView = (TextView)view.findViewById(R.id.itemTv);
			ImageView inImageView = (ImageView)view.findViewById(R.id.itemInImg);
			
			icon.setImageResource(imgs[position]);
			textView.setText(names[position]);
			inImageView.setImageResource(R.drawable.in);
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
				case 1:   //信誉达人
					intent = new Intent(getActivity(),CreditPeople.class);
					startActivity(intent);
					
					break;
				case 2:
					
					break;
				case 3:
					
					break;
				default:
					break;
				}
			}
		});
	}

}
