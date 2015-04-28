package com.yueme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.PathUtil;
import com.yueme.ChatGroupActivity.ChatItem;
import com.yueme.domain.ChatGroupInfo;
import com.yueme.ui.BitmapTool;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;

public class ChatGroupActivity extends Activity implements OnClickListener {

	private List<ChatItem> chatItems = new ArrayList<ChatItem>();
	private ListView chatListView;
	private ChatListAdapter chatListAdapter;
	private NewMessageBroadcastReceiver msgReceiver;
	private EditText et_msg;
	private String groupId;
	private ImageView iv_emotion;
	private EMConversation conversation;
	private InputMethodManager manager;
	private ChatGroupInfo chatGroupInfo;
	private ViewPager emotionsViewPager;
	private LinearLayout emotionsLayout;
	private ImageView ivState1, ivState2;
	private ImageView iv_more;
	private RelativeLayout moreLayout;
	private ImageView iv_chat_image, iv_chat_camera, iv_chat_location;
	private static final int REQUEST_CODE_LOCAL_IMAGE = 1;
	private static final int REQUEST_CODE_CAMERA = 2;
	private static final int REQUEST_CODE_LOCATION = 3;
	private File cameraFile;
	private ImageView deleteConversation;
	private Button btnSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d("yue", "oncreate");
		setContentView(R.layout.activity_chat_group);
		chatGroupInfo = (ChatGroupInfo) getIntent().getSerializableExtra(
				"chatGroupInfo");
		groupId = chatGroupInfo.getGroup_id();

		if (groupId == null) {
			ToastUtil.showToast("没有加入群组", ChatGroupActivity.this);
		}
		conversation = EMChatManager.getInstance().getConversation(groupId);
		initView();
		setEvents();
		new LoadConversationSynctask().execute();
	}

	private void initView() {
		chatListAdapter = new ChatListAdapter();
		chatListView = (ListView) findViewById(R.id.chatListView);
		et_msg = (EditText) findViewById(R.id.et_sendmessage);
		iv_emotion = (ImageView) findViewById(R.id.iv_emoticons_normal);
		emotionsLayout = (LinearLayout) findViewById(R.id.emotionsLayout);
		emotionsViewPager = (ViewPager) findViewById(R.id.emotionsViewPager);
		ivState1 = (ImageView) findViewById(R.id.ivState1);
		ivState2 = (ImageView) findViewById(R.id.ivState2);

		iv_more = (ImageView) findViewById(R.id.iv_more);
		moreLayout = (RelativeLayout) findViewById(R.id.moreLayout);
		iv_chat_camera = (ImageView) findViewById(R.id.iv_chat_camera);
		iv_chat_image = (ImageView) findViewById(R.id.iv_chat_image);
		iv_chat_location = (ImageView) findViewById(R.id.iv_chat_location);
		deleteConversation = (ImageView) findViewById(R.id.deleteBtn);
		btnSend = (Button) findViewById(R.id.btn_send);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	private void setEvents() {

		chatListView.setAdapter(chatListAdapter);
		chatListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				hideKeyboard();
				iv_emotion
						.setImageResource(R.drawable.chatting_biaoqing_btn_normal);
				moreLayout.setVisibility(View.GONE);
				emotionsLayout.setVisibility(View.GONE);
			}
		});

		/* chatListView.setOnClickListener(this); */
		emotionsViewPager.setAdapter(new EmotionsViewPagerAdapter());
		emotionsViewPager.setCurrentItem(0);
		emotionsViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				switch (arg0) {
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

		iv_emotion.setOnClickListener(this);
		iv_more.setOnClickListener(this);
		iv_chat_camera.setOnClickListener(this);
		iv_chat_image.setOnClickListener(this);
		iv_chat_location.setOnClickListener(this);
		et_msg.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		deleteConversation.setOnClickListener(this);
		
		// 注册message receiver 接收消息
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		registerReceiver(msgReceiver, intentFilter);

	}

	private class EmotionsViewPagerAdapter extends PagerAdapter {
		private List<View> pagerViews;

		public EmotionsViewPagerAdapter() {
			super();
			pagerViews = new ArrayList<View>();
			int emotions1[] = { 0x1F604, 0x1F60a, 0x1F603, 0x1F632, 0x1F609,
					0x1F60D, 0x1F618, 0x1F61A, 0x1F633, 0x1F601, 0x1F60C,
					0x1F61C, 0x1F61D, 0x1F612, 0x1F60F, 0x1F613, 0x1F614,
					0x1F61E, 0x1F616, 0x1F625, 0x1F630 };
			int emotions2[] = { 0x1F628, 0x1F623, 0x1F622, 0x1F62D, 0x1F602,
					0x1F632, 0x1F631, 0x1F620, 0x1F621, 0x1F62A, 0x1F637,
					0x1F37A, 0x270C, 0x1F4A9, 0x1F44D, 0x1F525, 0x2728,
					0x1F31F, 0x1F4AA, 0x1F4A4, 0x1F3B5 };
			View v1 = LayoutInflater.from(ChatGroupActivity.this).inflate(
					R.layout.emotions_gridview, null);
			View v2 = LayoutInflater.from(ChatGroupActivity.this).inflate(
					R.layout.emotions_gridview, null);
			GridView gv1 = (GridView) v1;
			GridView gv2 = (GridView) v2;

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
		public static final int SELF_TEXT_TYPE = 0;
		public static final int OTHERS_TEXT_TYPE = 1;
		public static final int SELF_IMAGE_TYPE = 2;
		public static final int OTHERS_IMAGE_TYPE = 3;
		public static final int SELT_LOCATION_TYPE = 4;
		public static final int OTHERS_LOCATION_TYPE = 5;
		String userName;
		String msg;
		int message_type;
		Bitmap bitmapMsg;
		String bitmapUri;
	}

	class ChatListAdapter extends BaseAdapter {
		private class ChatBasicViewHolder {
			ImageView iv_userIcon;
			TextView tv_userName;
			// TextView tv_msg;

		}

		private class TextMessageViewHolder extends ChatBasicViewHolder {
			TextView tv_text_msg;
		}

		private class ImageMessageViewHolder extends ChatBasicViewHolder {
			ImageView iv_image_msg;
		}

		private ChatBasicViewHolder viewHolder;
		private ChatItem chatItem;
		private int type;

		@Override
		public int getItemViewType(int position) {
			return chatItems.get(position).message_type;

		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 6;
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
			chatItem = chatItems.get(position);
			/*
			 * if (convertView != null) { viewHolder = (ChatBasicViewHolder)
			 * convertView.getTag(); } else {
			 */
			// viewHolder = new ChatBasicViewHolder();
			type = getItemViewType(position);
			switch (type) {
			case ChatItem.SELF_TEXT_TYPE:
			case ChatItem.OTHERS_TEXT_TYPE:

				if (type == ChatItem.SELF_TEXT_TYPE) {
					convertView = LayoutInflater.from(ChatGroupActivity.this)
							.inflate(R.layout.chat_listitem_metxt, null);

				} else {
					convertView = LayoutInflater.from(ChatGroupActivity.this)
							.inflate(R.layout.chat_listitem_otherstxt, null);

				}

				viewHolder = new TextMessageViewHolder();
				((TextMessageViewHolder) viewHolder).tv_text_msg = (TextView) convertView
						.findViewById(R.id.msg_content);
				((TextMessageViewHolder) viewHolder).tv_text_msg
						.setText(chatItem.msg);
				break;

			case ChatItem.SELF_IMAGE_TYPE:
			case ChatItem.OTHERS_IMAGE_TYPE:
				if (type == ChatItem.SELF_IMAGE_TYPE) {
					convertView = LayoutInflater.from(ChatGroupActivity.this)
							.inflate(R.layout.chat_listitem_me_image, null);
				} else {
					convertView = LayoutInflater.from(ChatGroupActivity.this)
							.inflate(R.layout.chat_listitem_others_image, null);
				}

				viewHolder = new ImageMessageViewHolder();
				((ImageMessageViewHolder) viewHolder).iv_image_msg = (ImageView) convertView
						.findViewById(R.id.msg_image_content);

				((ImageMessageViewHolder) viewHolder).iv_image_msg
						.setImageBitmap(chatItem.bitmapMsg);
				final String extraBitmapUri = chatItem.bitmapUri;
				((ImageMessageViewHolder) viewHolder).iv_image_msg
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Log.d("mychat", "clicked ddfddfdsafdsf");
								Intent intent = new Intent(
										ChatGroupActivity.this,
										ViewPictureActivity.class);
								intent.putExtra("bitmapUri", extraBitmapUri);
								startActivity(intent);
								Log.d("mychat", "started ddfddfdsafdsf");
							}
						});
				break;
			case ChatItem.SELT_LOCATION_TYPE:
			case ChatItem.OTHERS_LOCATION_TYPE:
				if (type == ChatItem.SELT_LOCATION_TYPE) {
					convertView = LayoutInflater.from(ChatGroupActivity.this)
							.inflate(R.layout.chat_listitem_me_location, null);
				} else {
					convertView = LayoutInflater.from(ChatGroupActivity.this)
							.inflate(R.layout.chat_listitem_others_location,
									null);
				}
				final String []locationInfos = chatItem.msg.split("-");
				final String extraLocationInfo = chatItem.msg;
				convertView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(ChatGroupActivity.this, LocationActivity.class);
						intent.putExtra("locationInfo", extraLocationInfo);
						startActivity(intent);
						
					}
				});
				viewHolder = new TextMessageViewHolder();
				((TextMessageViewHolder) viewHolder).tv_text_msg = (TextView) convertView
						.findViewById(R.id.tv_msg_location);
				if (chatItem.msg != null) {
					((TextMessageViewHolder) viewHolder).tv_text_msg
							.setText(locationInfos[0]);
				}

				break;
			default:
				break;
			}

			viewHolder.iv_userIcon = (ImageView) convertView
					.findViewById(R.id.user_icon);
			viewHolder.tv_userName = (TextView) convertView
					.findViewById(R.id.user_name);

			/*
			 * convertView.setTag(viewHolder); }
			 */

			Bitmap bitmap = chatGroupInfo.getHeadIcon(chatItem.userName);
			if (bitmap != null) {
				viewHolder.iv_userIcon.setImageBitmap(chatGroupInfo
						.getHeadIcon(chatItem.userName));
			} else {
				viewHolder.iv_userIcon.setImageResource(R.drawable.user_head);
			}
			viewHolder.tv_userName.setText(chatItem.userName);

			return convertView;
		}

	}

	private void onSendTxtMsg(int sendType, String msgContent) {
		// final String msgContent = et_msg.getText().toString().trim();
		et_msg.setText("");
		final EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
		msg.setChatType(ChatType.GroupChat);
		if (sendType == ChatItem.SELF_TEXT_TYPE) {
			msg.setAttribute(ConstantValues.MSG_CATEGAORY,
					ConstantValues.TEXT_MSG);
		} else {
			msg.setAttribute(ConstantValues.MSG_CATEGAORY,
					ConstantValues.LOCATION_MSG);
		}

		TextMessageBody body = new TextMessageBody(msgContent);
		msg.addBody(body);
		Log.d("mychat", "sendText groupId" + groupId);
		msg.setReceipt(groupId);

		ChatItem chatItem = new ChatItem();
		chatItem.userName = getSharedPreferences("data", 0).getString(
				"NICK_NAME", "");

		chatItem.msg = msgContent;
		chatItem.message_type = sendType;
		chatItems.add(chatItem);
		chatListAdapter.notifyDataSetChanged();
		conversation.addMessage(msg);
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

	private void sendPictureMsg(final String filePath) {
		final EMMessage message = EMMessage
				.createSendMessage(EMMessage.Type.IMAGE);
		message.setChatType(ChatType.GroupChat);
		message.setAttribute(ConstantValues.MSG_CATEGAORY,
				ConstantValues.IMAGE_MSG);
		message.setReceipt(groupId);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		message.addBody(body);
		conversation.addMessage(message);
		ChatItem chatItem = new ChatItem();
		chatItem.message_type = ChatItem.SELF_IMAGE_TYPE;
		chatItem.bitmapUri = filePath;
		chatItem.userName = getSharedPreferences("data", 0).getString(
				"NICK_NAME", "");
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		options.inSampleSize = BitmapTool.computeSampleSize(options, -1,
				128 * 128);
		options.inJustDecodeBounds = false;

		chatItem.bitmapMsg = BitmapFactory.decodeFile(filePath, options);
		/*
		 * try { FileOutputStream fos = new FileOutputStream(cameraFile);
		 * chatItem.bitmapMsg.compress(Bitmap.CompressFormat.JPEG, 20, fos);
		 * fos.flush(); fos.close(); } catch (FileNotFoundException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */

		chatItems.add(chatItem);
		chatListAdapter.notifyDataSetChanged();
		chatListView.setSelection(chatItems.size() - 1);
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.d("mychat", "chatgroup pic send success");
			}

			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

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
			int msg_catergory;
			try {
				Log.d("main", "chat group new message id:" + msgId + " from:"
						+ message.getFrom() + " type:" + message.getType());
				msg_catergory = message
						.getIntAttribute(ConstantValues.MSG_CATEGAORY);
				Log.d("main", "" + msg_catergory);
				if (msg_catergory == ConstantValues.NOTIFICATION) {
					return;
				}

				Log.d("main", "chatgroup get global type");
				if (message.getChatType() == ChatType.GroupChat) {
					Log.d("main", "chatgroup get grouptype");
					final ChatItem chatItem = new ChatItem();
					chatItem.userName = chatGroupInfo.getNickName(message
							.getFrom());

					switch (message.getType()) {

					case TXT:
						Log.d("main", "chatgroup  get message");

						TextMessageBody txtBody = (TextMessageBody) message
								.getBody();
						if (msg_catergory == ConstantValues.TEXT_MSG) {
							chatItem.message_type = ChatItem.OTHERS_TEXT_TYPE;
						} else {
							chatItem.message_type = ChatItem.OTHERS_LOCATION_TYPE;
						}

						chatItem.msg = txtBody.getMessage();
						chatItems.add(chatItem);
						chatListAdapter.notifyDataSetChanged();
						chatListView.setSelection(chatItems.size() - 1);
						break;
					case CMD:
						break;
					case FILE:
						break;
					case IMAGE:
						final ImageMessageBody imgBody = (ImageMessageBody) message
								.getBody();
						Log.d("mychat",
								"thumbnailUrl: " + imgBody.getThumbnailUrl()
										+ "; rmote url: "
										+ imgBody.getRemoteUrl());
						chatItem.message_type = ChatItem.OTHERS_IMAGE_TYPE;

						String imgPath = imgBody.getLocalUrl();
						final String thumnailPath = imgPath.replace("image/",
								"image/th");

						new LoadRmoteImageAsyntask(imgBody.getRemoteUrl(),
								chatItem, imgBody.getLocalUrl()).execute();

						Log.d("mychat", "bitmap img: " + chatItem.bitmapMsg);

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

					chatListAdapter.notifyDataSetChanged();
					chatListView.setSelection(chatItems.size() - 1);

					conversation.addMessage(message);

				}

			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private class LoadRmoteImageAsyntask extends AsyncTask<Void, Void, Bitmap> {
		private String url;
		private ChatItem chatItem;
		private String localUrl;

		public LoadRmoteImageAsyntask(String url, ChatItem chatItem,
				String localUrl) {
			this.url = url;
			this.chatItem = chatItem;
			this.localUrl = localUrl;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return loadRmoteImage(url);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			File file = new File(localUrl);

			try {
				// file.mkdirs();
				file.createNewFile();
				FileOutputStream outputStream = new FileOutputStream(file);

				result.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
				outputStream.flush();
				outputStream.close();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(localUrl, options);

				options.inSampleSize = BitmapTool.computeSampleSize(options,
						-1, 128 * 128);
				options.inJustDecodeBounds = false;

				chatItem.bitmapMsg = BitmapFactory
						.decodeFile(localUrl, options);
				chatItem.bitmapUri = localUrl;
				chatItems.add(chatItem);
				chatListAdapter.notifyDataSetChanged();
				chatListView.setSelection(chatItems.size() - 1);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private Bitmap loadRmoteImage(String imgUrl) {
		URL fileURL = null;
		Bitmap bitmap = null;
		try {
			fileURL = new URL(imgUrl);
		} catch (MalformedURLException err) {
			err.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) fileURL
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			int length = (int) conn.getContentLength();
			if (length != -1) {
				byte[] imgData = new byte[length];
				byte[] buffer = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(buffer)) > 0) {
					System.arraycopy(buffer, 0, imgData, destPos,

					readLen);
					destPos += readLen;
				}
				bitmap = BitmapFactory.decodeByteArray(imgData, 0,
						imgData.length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	private class LoadConversationSynctask extends
			AsyncTask<Void, ChatItem, Void> {
		EMConversation conversation = EMChatManager.getInstance()
				.getConversation(groupId);
		List<EMMessage> messages = conversation.getAllMessages();

		ChatItem chatItem;
		String currentUserNickName = getSharedPreferences("data", 0).getString(
				"NICK_NAME", "");

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			for (EMMessage message : messages) {
				chatItem = new ChatItem();
				chatItem.userName = chatGroupInfo
						.getNickName(message.getFrom());

				switch (message.getType()) {
				case TXT:
					try {

						int msg_attr = message
								.getIntAttribute(ConstantValues.MSG_CATEGAORY);

						if (chatItem.userName.equals(currentUserNickName)) {
							if (msg_attr == ConstantValues.TEXT_MSG) {
								chatItem.message_type = ChatItem.SELF_TEXT_TYPE;
							} else {
								chatItem.message_type = ChatItem.SELT_LOCATION_TYPE;
							}

						} else {
							if (msg_attr == ConstantValues.TEXT_MSG) {
								chatItem.message_type = ChatItem.OTHERS_TEXT_TYPE;
							} else {
								chatItem.message_type = ChatItem.OTHERS_LOCATION_TYPE;
							}
						}

						TextMessageBody txtBody = (TextMessageBody) message
								.getBody();
						chatItem.msg = txtBody.getMessage();

					} catch (EaseMobException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case CMD:
					break;
				case FILE:
					break;
				case IMAGE:
					if (chatItem.userName.equals(currentUserNickName)) {
						chatItem.message_type = ChatItem.SELF_IMAGE_TYPE;
					} else {
						chatItem.message_type = ChatItem.OTHERS_IMAGE_TYPE;
					}
					ImageMessageBody body = (ImageMessageBody) message
							.getBody();
					message.addBody(body);

					Log.d("mychat", "local url:　" + body.getLocalUrl());

					String imgPath = body.getLocalUrl();
					chatItem.bitmapUri = imgPath;
					// final String thumnailPath = imgPath.replace("image/",
					// "image/th");
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(imgPath, options);

					options.inSampleSize = BitmapTool.computeSampleSize(
							options, -1, 128 * 128);
					options.inJustDecodeBounds = false;

					chatItem.bitmapMsg = BitmapFactory.decodeFile(imgPath,
							options);

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

				// handler.sendEmptyMessage(0);
				publishProgress(chatItem);

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(ChatItem... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);

			chatItems.add(values[0]);
			chatListAdapter.notifyDataSetChanged();
			chatListView.setSelection(chatItems.size() - 1);
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 注销接收聊天消息的message receiver
		Log.d("yue", "onDestroy");
		if (msgReceiver != null) {
			try {
				unregisterReceiver(msgReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		chatItems.clear();
		chatListAdapter.notifyDataSetChanged();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btn_send:
			String msgContent = et_msg.getText().toString().trim();
			onSendTxtMsg(ChatItem.SELF_TEXT_TYPE, msgContent);
			break;
		case R.id.iv_emoticons_normal:
			if (emotionsLayout.getVisibility() == View.GONE) {
				hideKeyboard();
				moreLayout.setVisibility(View.GONE);
				iv_emotion
						.setImageResource(R.drawable.chatting_biaoqing_btn_enable);
				emotionsLayout.setVisibility(View.VISIBLE);

			} else {
				iv_emotion
						.setImageResource(R.drawable.chatting_biaoqing_btn_normal);
				emotionsLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.iv_more:
			if (moreLayout.getVisibility() == View.GONE) {
				moreLayout.setVisibility(View.VISIBLE);
				hideKeyboard();

				iv_emotion
						.setImageResource(R.drawable.chatting_biaoqing_btn_normal);
				emotionsLayout.setVisibility(View.GONE);
			} else {
				moreLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.et_sendmessage:
			iv_emotion
					.setImageResource(R.drawable.chatting_biaoqing_btn_normal);
			moreLayout.setVisibility(View.GONE);
			emotionsLayout.setVisibility(View.GONE);
			break;
		case R.id.iv_chat_camera:
			cameraFile = new File(PathUtil.getInstance().getImagePath(),
					System.currentTimeMillis() + ".jpg");
			cameraFile.getParentFile().mkdirs();
			startActivityForResult(
					new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
							MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
					REQUEST_CODE_CAMERA);
			break;
		case R.id.iv_chat_image:
			Intent intent;
			if (Build.VERSION.SDK_INT < 19) {
				intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");

			} else {
				intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			}
			startActivityForResult(intent, REQUEST_CODE_LOCAL_IMAGE);
			break;
		case R.id.iv_chat_location:
			// String testLocationInfo = "重庆邮电大学-66-66";
			// onSendTxtMsg(ChatItem.SELT_LOCATION_TYPE, testLocationInfo);

			Intent intentLocation = new Intent(ChatGroupActivity.this,
					LocationActivity.class);
			intentLocation.putExtra("locationInfo", "sendLocation");
			startActivityForResult(intentLocation, REQUEST_CODE_LOCATION);

			break;
		case R.id.deleteBtn:
			new AlertDialog.Builder(ChatGroupActivity.this)
					.setTitle("清空聊天记录")
					.setMessage("确认清空聊天记录？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									EMChatManager.getInstance()
											.clearConversation(groupId);
									chatItems.clear();
									chatListAdapter.notifyDataSetChanged();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).create().show();

			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_CAMERA:
				if (cameraFile != null && cameraFile.exists()) {
					sendPictureMsg(cameraFile.getAbsolutePath());
				}
				break;
			case REQUEST_CODE_LOCAL_IMAGE:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(
								selectedImage, null, null, null, null);
						if (cursor != null) {
							cursor.moveToFirst();
							int columnIndex = cursor.getColumnIndex("_data");
							String picturePath = cursor.getString(columnIndex);
							cursor.close();
							cursor = null;
							if (picturePath != null) {
								sendPictureMsg(picturePath);
							}

						}

					}
				}
				break;
			case REQUEST_CODE_LOCATION:
				//data.get
				String locationInfo = data.getExtras().getString("locationInfo");
				if(locationInfo!=null) {
					onSendTxtMsg(ChatItem.SELT_LOCATION_TYPE, locationInfo);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.d("yue", "onResume");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("yue", "onPause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("yue", "onStop");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(moreLayout.getVisibility()==View.VISIBLE || emotionsLayout.getVisibility()==View.VISIBLE){
				moreLayout.setVisibility(View.GONE);
				emotionsLayout.setVisibility(View.GONE);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	
}
