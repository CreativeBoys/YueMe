package com.yueme;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.yueme.domain.ChatGroupInfo;
import com.yueme.values.ConstantValues;

public class ChatGroupActivity extends Activity {
	
	private List<ChatItem> chatItems = new ArrayList<ChatItem>();
	private ListView chatListView;
	private int msgCount;
	private ChatAdapter chatAdapter;
	private NewMessageBroadcastReceiver msgReceiver;
	private EditText et_msg;
	private String groupId;
	private ImageView iv_emoticon;
	// 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
	EMConversation conversation;
	private InputMethodManager manager;
	private ChatGroupInfo chatGroupInfo;
	private ViewPager emotionsViewPager;
	private LinearLayout emotionsLayout;
	private ImageView ivState1, ivState2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_group);
		/*chatGroupInfo = (ChatGroupInfo) getIntent().getSerializableExtra("chatGroupInfo");
		groupId = chatGroupInfo.getGroup_id();

		if (groupId == null) {
			ToastUtil.showToast("没有加入群组", ChatGroupActivity.this);
		}
		loadConversation();*/
		initView();
		setEvents();
		
	}

	private void initView() {
		chatAdapter = new ChatAdapter();
		chatListView = (ListView) findViewById(R.id.chatListView);
		et_msg = (EditText) findViewById(R.id.et_sendmessage);
		iv_emoticon = (ImageView) findViewById(R.id.iv_emoticons_normal);
		emotionsLayout = (LinearLayout) findViewById(R.id.emotionsLayout);
		emotionsViewPager = (ViewPager) findViewById(R.id.emotionsViewPager);
		ivState1 = (ImageView) findViewById(R.id.ivState1);
		ivState2 = (ImageView)findViewById(R.id.ivState2);
		
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	

	private void setEvents() {

		/*chatListView.setAdapter(chatAdapter);
		chatListView.setSelection(msgCount-1);*/
		iv_emoticon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (emotionsLayout.getVisibility() == View.GONE) {
					emotionsLayout.setVisibility(View.VISIBLE);
					hideKeyboard();
				} else {
					emotionsLayout.setVisibility(View.GONE);
				}
			}
		});
		
		emotionsViewPager.setAdapter(new EmotionsViewPagerAdapter());
		emotionsViewPager.setCurrentItem(0);
		emotionsViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				switch(arg0){
				case 0:
					ivState1.setImageResource(R.drawable.point_checked);
					ivState2.setImageResource(R.drawable.point_uncheck);
					break;
				case 1:
					ivState1.setImageResource(R.drawable.point_uncheck);
					ivState2.setImageResource(R.drawable.point_checked);
					break;
				}
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
		/*msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		registerReceiver(msgReceiver, intentFilter);*/

	}

	private class EmotionsViewPagerAdapter extends PagerAdapter{
		private List<View> pagerViews;
		public EmotionsViewPagerAdapter() {
			super();
			pagerViews = new ArrayList<View>();
			int emotions1[] = { 0x1F604, 0x1F60a, 0x1F603, 0x1F632, 0x1F609,
					0x1F60D, 0x1F618, 0x1F61A, 0x1F633, 0x1F601, 0x1F60C, 0x1F61C,
					0x1F61D, 0x1F612, 0x1F60F, 0x1F613, 0x1F614, 0x1F61E, 0x1F616,
					0x1F625, 0x1F630 };
			int emotions2[] = { 0x1F628, 0x1F623, 0x1F622, 0x1F62D, 0x1F602,
					0x1F632, 0x1F631, 0x1F620, 0x1F621, 0x1F62A, 0x1F637, 0x1F37A,
					0x270C, 0x1F4A9, 0x1F44D, 0x1F525, 0x2728, 0x1F31F, 0x1F4AA,
					0x1F4A4, 0x1F3B5 };
			View v1 = LayoutInflater.from(ChatGroupActivity.this).inflate(R.layout.emotions_gridview, null);
			View v2 = LayoutInflater.from(ChatGroupActivity.this).inflate(R.layout.emotions_gridview, null);
			GridView gv1 = (GridView) v1;
			GridView gv2 = (GridView)v2;
			
			gv1.setAdapter(new EmotionsGridViewAdapter(emotions1));
			gv2.setAdapter(new EmotionsGridViewAdapter(emotions2));
			gv1.setOnItemClickListener(new OnItemClickListener() {

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
			});
			pagerViews.add(v1);
			pagerViews.add(v2);
		}
		

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			super.destroyItem(container, position, object);
			((ViewPager)container).removeView(pagerViews.get(position));
		}


		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			((ViewPager)container).addView(pagerViews.get(position));
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
			return arg0==arg1;
		}
		
	}
	
	private String emotionCode(int hax) {
		return String.valueOf(Character.toChars(hax));
	}

	class EmotionsGridViewAdapter extends BaseAdapter {
		private int emotions[];
		
		

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
				convertView = LayoutInflater.from(ChatGroupActivity.this)
						.inflate(R.layout.emotion_griditem, null);
			}
			String emotion = emotionCode(emotions[position]);
			((TextView) convertView).setText(emotion);
			return convertView;
		}

	}

	public void back(View v) {
		finish();
	}

	public void editText_check(View v) {
		if (emotionsViewPager.getVisibility() == View.VISIBLE) {
			emotionsViewPager.setVisibility(View.GONE);
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	class ChatItem {
		String userName;
		String msg;
	}

	class ChatAdapter extends BaseAdapter {
		private class ViewHolder {
			ImageView iv_userIcon;
			TextView tv_userName;
			TextView tv_msg;

		}

		private final int SELF_TEXT_TYPE = 0;
		private final int OTHERS_TEXT_TYPE = 1;

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			String currentNickName = chatItems.get(position).userName;
			String currentUserNickName = getSharedPreferences("data", 0)
					.getString("NICK_NAME", "");

			if (currentNickName.equals(currentUserNickName)) {
				return SELF_TEXT_TYPE;
			} else {
				return OTHERS_TEXT_TYPE;
			}
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return chatItems.size();
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
			ViewHolder viewHolder;
			if (convertView != null) {
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				int type = getItemViewType(position);
				switch (type) {
				case SELF_TEXT_TYPE:
					convertView = LayoutInflater.from(ChatGroupActivity.this)
							.inflate(R.layout.chat_listitem_metxt, null);
					break;
				case OTHERS_TEXT_TYPE:
					convertView = LayoutInflater.from(ChatGroupActivity.this)
							.inflate(R.layout.chat_listitem_otherstxt, null);
					break;

				default:
					break;
				}

				viewHolder = new ViewHolder();
				viewHolder.iv_userIcon = (ImageView) convertView
						.findViewById(R.id.user_icon);
				viewHolder.tv_userName = (TextView) convertView
						.findViewById(R.id.user_name);
				viewHolder.tv_msg = (TextView) convertView
						.findViewById(R.id.msg_content);
				convertView.setTag(viewHolder);
			}
			ChatItem chatItem = chatItems.get(position);
			Bitmap bitmap = chatGroupInfo.getHeadIcon(chatItem.userName);
			if(bitmap!=null) {
				viewHolder.iv_userIcon.setImageBitmap(chatGroupInfo.getHeadIcon(chatItem.userName));
			} else{
				viewHolder.iv_userIcon.setImageResource(R.drawable.user_head);
			}
			
			// viewHolder.iv_userIcon.setImageBitmap(chatItem.bitmap);
			viewHolder.tv_userName.setText(chatItem.userName);
			viewHolder.tv_msg.setText(chatItem.msg);
			return convertView;
		}

	}

	public void onSendTxtMsg(View view) {
		final String msgContent = et_msg.getText().toString().trim();
		et_msg.setText("");
		final EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
		msg.setChatType(ChatType.GroupChat);
		msg.setAttribute(ConstantValues.MSG_CATEGAORY, ConstantValues.MSG);

		TextMessageBody body = new TextMessageBody(msgContent);
		msg.addBody(body);
		Log.d("mychat", "sendText groupId" + groupId);
		msg.setReceipt(groupId);

		ChatItem chatItem = new ChatItem();
		chatItem.userName = getSharedPreferences("data", 0).getString("NICK_NAME", "");
		
		chatItem.msg = msgContent;
		chatItems.add(chatItem);
		chatAdapter.notifyDataSetChanged();
		// conversation.addMessage(msg);
		// 发送消息
		EMChatManager.getInstance().sendMessage(msg, new EMCallBack() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.d("mychat", "onsend msg onError");
			}

			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.d("mychat", "onsend msg success");

			}
		});

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

			try {
				Log.d("main", "chat group new message id:" + msgId + " from:"
						+ message.getFrom() + " type:" + message.getType());
				int msg_catergory = message
						.getIntAttribute(ConstantValues.MSG_CATEGAORY);
				Log.d("main", "" + msg_catergory);
				if (msg_catergory == ConstantValues.NOTIFICATION) {
					return;
				}

				Log.d("main", "chatgroup get global type");
				if (message.getChatType() == ChatType.GroupChat) {
					Log.d("main", "chatgroup get grouptype");
					switch (message.getType()) {
					case TXT:
						Log.d("main", "chatgroup  get message");
						ChatItem chatItem = new ChatItem();
						chatItem.userName = chatGroupInfo.getNickName(message.getFrom());
						
						TextMessageBody txtBody = (TextMessageBody) message
								.getBody();
						chatItem.msg = txtBody.getMessage();
						chatItems.add(chatItem);
						chatAdapter.notifyDataSetChanged();
						break;
					case CMD:
						break;
					case FILE:
						break;
					case IMAGE:
						break;
					case LOCATION:
						break;
					case VIDEO:
						break;
					case VOICE:
						break;
					default:
						break;

					}
				}

			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void loadConversation() {
		EMConversation conversation = EMChatManager.getInstance()
				.getConversation(groupId);
		List<EMMessage> messages = conversation.getAllMessages();
		Log.d("mychat",
				"Chat group activity:  messages.size()" + messages.size());
		msgCount = messages.size();
		ChatItem chatItem;
		for (EMMessage message : messages) {
			chatItem = new ChatItem();
			switch (message.getType()) {
			case TXT:
				
				chatItem.userName = chatGroupInfo.getNickName(message.getFrom());
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				chatItem.msg = txtBody.getMessage();
				chatItems.add(chatItem);
				break;
			case CMD:
				break;
			case FILE:
				break;
			case IMAGE:
				break;
			case LOCATION:
				break;
			case VIDEO:
				break;
			case VOICE:
				break;
			default:
				break;

			}
		}

	}

	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
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
