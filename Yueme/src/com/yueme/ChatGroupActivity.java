package com.yueme;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yueme.domain.Info;
import com.yueme.domain.ProtocalResponse;
import com.yueme.domain.User;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;

public class ChatGroupActivity extends Activity {
	private Info info;
	private List<User> users;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private List<ChatItem> chatItems = new ArrayList<ChatItem>();
	private ListView chatListView;
	private ChatAdapter chatAdapter;
	private NewMessageBroadcastReceiver msgReceiver;
	private EditText et_msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_group);
		info = (Info) getIntent().getSerializableExtra("info");
		initView();

		getNetInfo();
		setEvents();
	}

	private void initView() {
		chatAdapter = new ChatAdapter();
		chatListView = (ListView) findViewById(R.id.chatListView);
		et_msg = (EditText) findViewById(R.id.et_sendmessage);
		

	}

	private void setEvents() {
		chatListView.setAdapter(chatAdapter);
		// 注册message receiver 接收消息
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		registerReceiver(msgReceiver, intentFilter);
		
	}

	public void back(View v) {
		finish();
	}

	class ChatItem{
		Bitmap bitmap;
		String userName;
		String msg;
	}
	class ChatAdapter extends BaseAdapter {
		private class ViewHolder {
			ImageView iv_userIcon;
			TextView tv_userName;
			TextView tv_msg;
			
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
				convertView = LayoutInflater.from(ChatGroupActivity.this)
						.inflate(R.layout.chat_listitem, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_userIcon = (ImageView) convertView
						.findViewById(R.id.user_icon);
				viewHolder.tv_userName = (TextView)convertView.findViewById(R.id.user_name);
				viewHolder.tv_msg = (TextView) convertView
						.findViewById(R.id.msg_content);
				convertView.setTag(viewHolder);
			}
			ChatItem chatItem = chatItems.get(position);
			viewHolder.iv_userIcon.setImageBitmap(chatItem.bitmap);
			viewHolder.tv_userName.setText(chatItem.userName);
			viewHolder.tv_msg.setText(chatItem.msg);
			return convertView;
		}

	}

	private void getNetInfo() {
		

		new ParticipantsAsyncTast().execute();
	}

	public void onSendTxtMsg(View view) {
		String msgContent = ""+et_msg.getText().toString().trim();
		et_msg.setText("");
		EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
		for (User user : users) {
			msg.setReceipt(user.getId());
			msg.setAttribute(ConstantValues.MSG_CATEGAORY, ConstantValues.MSG);
			TextMessageBody body = new TextMessageBody(msgContent);
			msg.addBody(body);

			try {
				EMChatManager.getInstance().sendMessage(msg);

			} catch (Exception e) {
				e.printStackTrace();
			}
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
				if (msg_catergory != ConstantValues.NOTIFICATION) {

					switch (message.getType()) {
					case TXT:
						ChatItem chatItem = new ChatItem();
						User user;
						for(int i=0; i< users.size();i++){
							user = users.get(i);
							if(user.getId().equals(message.getFrom())) {
								chatItem.bitmap = bitmaps.get(i);
								chatItem.userName = user.getNickname();
								break;
							}
						}
						TextMessageBody txtBody = (TextMessageBody) message
								.getBody();
						chatItem.msg = txtBody.getMessage();
						chatItems.add(chatItem);
						chatAdapter.notifyDataSetChanged();
						break;

					}

				}

			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private class ParticipantsAsyncTast extends
			AsyncTask<Void, Void, ProtocalResponse> {

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM,
						ConstantValues.GET_PARTICIPANTS + "");
				map.put("infoID", info.getId());
				HttpGet get = new HttpGet(NetUtil.getUrlString(map));
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					String json = StreamUtil.getString(response.getEntity()
							.getContent());
					return new Gson().fromJson(json, ProtocalResponse.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ProtocalResponse result) {
			if (result == null) {
				ToastUtil.showToast("网络错误", ChatGroupActivity.this);
			} else {
				String json = result.getResponse();
				users = new Gson().fromJson(json, new TypeToken<List<User>>() {
				}.getType());
				for (final User user : users) {
					new AsyncTask<Void, Void, Bitmap>() {

						@Override
						protected Bitmap doInBackground(Void... params) {
							return NetUtil.getBitmapFromServer(user
									.getHead_img_path());
						}

						protected void onPostExecute(Bitmap result) {
							bitmaps.add(result);
							/* adapter.notifyDataSetChanged(); */
						}
					}.execute();
				}
			}
		}
	}
}
