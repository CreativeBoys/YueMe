package com.yueme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.yueme.domain.Info;

public class ChatGroupActivity extends Activity {
	Info info;
	ListView chatListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_group);
		init();
		setEvents();
	}

	private void init() {
		chatListView = (ListView)findViewById(R.id.chatListView);
		info = (Info) getIntent().getSerializableExtra("info");
		
	}

	private void setEvents(){
		
	}
	
	public void back(View v) {
		finish();
	}
	
	class ChatAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
