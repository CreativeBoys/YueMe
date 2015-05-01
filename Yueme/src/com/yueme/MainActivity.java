package com.yueme;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.yueme.fragment.BottomFragment;
import com.yueme.fragment.DiscoveryFragment;
import com.yueme.fragment.HomeFragment;
import com.yueme.fragment.TitleFragment;
import com.yueme.fragment.UserFragment;
import com.yueme.fragment.base.BaseFragment;
import com.yueme.interfaces.OnBottomClickListener;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

public class MainActivity extends FragmentActivity {
	private FrameLayout fl_title;
	private FrameLayout fl_bottom;
	private ViewPager vp_middle;
	private List<BaseFragment> fragments;
	private TitleFragment titleFragment;
	private BottomFragment bottomFragment;
	private HomePagerAdapter adapter;
	private HomeFragment homeFragment;
	private DiscoveryFragment discoveryFragment;
	private UserFragment userFragment;
	private NewMessageBroadcastReceiver msgReceiver;
	@Override
	protected void onResume() {
		super.onResume();
		if(GlobalValues.USER_ID==null) {
			this.finish();
			return;
		}
	}
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		if(GlobalValues.USER_ID==null) {
			this.finish();
			return;
		}
		fragments = new ArrayList<BaseFragment>();
		fl_bottom = (FrameLayout) findViewById(R.id.fl_bottom);
		fl_title = (FrameLayout) findViewById(R.id.fl_title);
		vp_middle = (ViewPager) findViewById(R.id.vp_middle);
		vp_middle.setOffscreenPageLimit(2);
		adapter = new HomePagerAdapter(getSupportFragmentManager());
		initFragments();
		setListenerAndAdapter();
	}

	private void initFragments() {
		titleFragment = new TitleFragment();
		bottomFragment = new BottomFragment();
		userFragment = new UserFragment();
		discoveryFragment = new DiscoveryFragment();
		homeFragment = new HomeFragment();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.add(R.id.fl_title, titleFragment);
		transaction.add(R.id.fl_bottom, bottomFragment);
		transaction.commit();
		fragments.add(homeFragment);
		fragments.add(userFragment);
		fragments.add(discoveryFragment);
	}

	private void setListenerAndAdapter() {
		vp_middle.setAdapter(adapter);
		bottomFragment.setOnBottomClickListener(new OnBottomClickListener() {

			@Override
			public void onBottomClick(int pos) {
				vp_middle.setCurrentItem((pos - 1) % 3);
			}
		});
		vp_middle.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int pos) {
				((RadioButton) bottomFragment.rg_bottom.getChildAt(pos))
						.setChecked(true);
				// 改变TitleFragment 内容 modify by heshaokang
				titleFragment.changeTitle(pos);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		// 注册message receiver 接收消息
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		registerReceiver(msgReceiver, intentFilter);

		// app初始化完毕
		EMChat.getInstance().setAppInited();
	}

	private class HomePagerAdapter extends FragmentPagerAdapter{
		public HomePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}

	/**
	 * 接收消息的BroadcastReceiver
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String msgId = intent.getStringExtra("msgid"); // 消息id
			// 从SDK 根据消息ID 可以获得消息对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			Log.d("main",
					"new message id:" + msgId + " from:" + message.getFrom()
							+ " type:" + message.getType() + " body:"
							+ message.getBody());
			try {

				int msg_catergory = message
						.getIntAttribute(ConstantValues.MSG_CATEGAORY);
				if (msg_catergory == ConstantValues.NOTIFICATION) {

					TextMessageBody txtBody = (TextMessageBody) message
							.getBody();
					String messageContent = txtBody.getMessage();

					String ns = Context.NOTIFICATION_SERVICE;
					NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
					int icon = R.drawable.ic_launcher;
					CharSequence tickerText = "约么提醒";
					long when = System.currentTimeMillis();
					Notification notification = new Notification(icon,
							tickerText, when);
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					CharSequence contentTitle = "约么提醒"; // 通知栏标题
					CharSequence contentText = messageContent; // 通知栏内容
					Intent notificationIntent = new Intent(
							getApplicationContext(),
							ParticipatedInfosActivity.class); // 点击该通知后要跳转的Activity
					PendingIntent contentIntent = PendingIntent.getActivity(
							MainActivity.this, 0, notificationIntent, 0);
					notification.setLatestEventInfo(getApplicationContext(),
							contentTitle, contentText, contentIntent);
					mNotificationManager.notify(0, notification);
				}

			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onDestroy() {
		// 注销接收聊天消息的message receiver
		if (msgReceiver != null) {
			try {
				unregisterReceiver(msgReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}
}
