package com.yueme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yueme.domain.Comment;
import com.yueme.domain.Info;
import com.yueme.domain.ProtocalResponse;
import com.yueme.domain.Subcomment;
import com.yueme.domain.User;
import com.yueme.util.DensityUtil;
import com.yueme.util.EncodeUtil;
import com.yueme.util.NetUtil;
import com.yueme.util.RestTimeUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

public class SingleRequireDetailsActivity extends Activity implements
		OnClickListener {
	protected static final int COUNT_DOWN = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case COUNT_DOWN:
				tv_counter
						.setText(RestTimeUtil
								.getRestTimeBySeconds((deadline - new Date()
										.getTime())));
				handler.sendEmptyMessageDelayed(COUNT_DOWN, 1000);
				break;

			default:
				break;
			}
		};
	};
	private static List<Comment> comments;
	private TextView tv_counter;
	private LinearLayout ll_reply;
	private Button btn_reply;
	private Button btn_participate;
	private long deadline;
	private Info info;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private EditText et_reply;
	private ListView lv_comments;
	private static boolean isMainComment = true;
	private CommentAdapter commentAdapter;
	private List<User> users;
	private static int commentPos = 0;
	private static int subCommentPos = 0;

	private String groupId;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_require_activity);
		btn_participate = (Button) findViewById(R.id.yueBtn);
		btn_reply = (Button) findViewById(R.id.replyBtn);
		ll_reply = (LinearLayout) findViewById(R.id.ll_reply);
		lv_comments = (ListView) findViewById(R.id.lv_comments);
		btn_participate.setOnClickListener(this);
		btn_reply.setOnClickListener(this);
		commentAdapter = new CommentAdapter();
		lv_comments.setAdapter(commentAdapter);
		lv_comments.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				isMainComment = false;
				commentPos = position;
				showReplyBox();
			}
		});
		info = (Info) getIntent().getSerializableExtra("info");
		groupId = info.getGroup_id();
		initView();
		getComments();
	}

	private void getComments() {
		new GetCommentsAsyncTask().execute();
	}

	@Override
	public void onBackPressed() {
		if (ll_reply.getVisibility() != View.GONE)
			ll_reply.setVisibility(View.GONE);
		else
			super.onBackPressed();
	}

	private void initView() {
		ImageView img = (ImageView) findViewById(R.id.userHeadIcon);
		TextView time = (TextView) findViewById(R.id.time);
		TextView userName = (TextView) findViewById(R.id.userName);
		TextView demandContent = (TextView) findViewById(R.id.demandContent);
		tv_counter = (TextView) findViewById(R.id.tv_counter);
		info = (Info) getIntent().getSerializableExtra("info");
		
		String createTime = "";
		long create_day = info.getCreate_day();
		if (DateUtils.isToday(create_day)) {
			createTime = "今天";
		} else {
			createTime = (String) DateFormat.format("yyyy-MM-dd", create_day);
		}
		time.setText(createTime);
		deadline = info.getDeadline();
		userName.setText(info.getNickname());
		demandContent.setText(info.getContent());
		img.setImageBitmap((Bitmap) getIntent().getParcelableExtra("bitmap"));
		handler.sendEmptyMessageDelayed(COUNT_DOWN, 1000);
		new CheckIsParticipatedAsyncTask().execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.yueBtn:
			if (btn_participate.getText().toString().equals("约起")) {
				btn_participate.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.cancel_bg));
				btn_participate.setText("取消");
				new AsyncTask<Void, Void, ProtocalResponse>() {

					@Override
					protected ProtocalResponse doInBackground(Void... params) {
						try {
							LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
							map.put(ConstantValues.REQUESTPARAM,
									ConstantValues.ADD_PARTICIPANTS + "");
							map.put("userID", GlobalValues.USER_ID);
							map.put("infoID", info.getId());
							HttpGet get = new HttpGet(NetUtil.getUrlString(map));
							HttpClient client = new DefaultHttpClient();
							HttpResponse response = client.execute(get);
							if (response.getStatusLine().getStatusCode() == 200) {
								String json = StreamUtil.getString(response
										.getEntity().getContent());
								Gson gson = new Gson();
								return gson.fromJson(json,
										ProtocalResponse.class);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(ProtocalResponse result) {
						if (result != null) {
							new JoinGroupAsyncTask().execute();
							ToastUtil.showToast(result.getResponse(),
									SingleRequireDetailsActivity.this);
						} else {
							ToastUtil.showToast("网络错误",
									SingleRequireDetailsActivity.this);
						}
						Intent intent = new Intent(
								SingleRequireDetailsActivity.this,
								ParticipantsActivity.class);
						intent.putExtra("info", info);
						startActivity(intent);
						finish();
						
					}
				}.execute();
				new ParticipantsAsyncTast().execute();
			} else {

				new AsyncTask<Void, Void, ProtocalResponse>() {

					@Override
					protected ProtocalResponse doInBackground(Void... params) {
						try {
							LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
							map.put(ConstantValues.REQUESTPARAM,
									ConstantValues.DELETE_PARTICIPANTS + "");
							map.put("userID", GlobalValues.USER_ID);
							map.put("infoID", info.getId());
							HttpGet get = new HttpGet(NetUtil.getUrlString(map));
							HttpClient client = new DefaultHttpClient();
							HttpResponse response = client.execute(get);
							if (response.getStatusLine().getStatusCode() == 200) {
								String json = StreamUtil.getString(response
										.getEntity().getContent());
								Gson gson = new Gson();
								return gson.fromJson(json,
										ProtocalResponse.class);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(ProtocalResponse result) {
						if (result != null) {
							ToastUtil.showToast(result.getResponse(),
									SingleRequireDetailsActivity.this);
						} else {
							ToastUtil.showToast("网络错误",
									SingleRequireDetailsActivity.this);
						}
					}
				}.execute();
				btn_participate.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.details_yue_bg));
				btn_participate.setText("约起");
			}

			break;
		case R.id.replyBtn:
			isMainComment = true;
			showReplyBox();
			break;
		default:
			break;
		}
	}

	private class JoinGroupAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				EMGroupManager.getInstance().joinGroup(groupId);
				return "OK";
			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (result != null) {
				new ParticipantsAsyncTast().execute();
				Intent intent = new Intent(SingleRequireDetailsActivity.this,
						ParticipantsActivity.class);
				intent.putExtra("info", info);
				startActivity(intent);

			}
			super.onPostExecute(result);
		}

	}
	private void showReplyBox() {
		ll_reply.setVisibility(View.VISIBLE);
		et_reply = (EditText) ll_reply.findViewById(R.id.et_reply);
		Button btn_publish = (Button) ll_reply.findViewById(R.id.btn_publish);
		et_reply.setFocusable(true);
		et_reply.setFocusableInTouchMode(true);
		et_reply.requestFocus();
		if (isMainComment)
			et_reply.setHint("我也说一句...");
		else if (subCommentPos < 0) {
			et_reply.setHint("回复@" + comments.get(commentPos).getNickname()
					+ ":");
		} else {
			et_reply.setHint("回复@"
					+ comments.get(commentPos).getSubcomments()
							.get(subCommentPos).getNickname() + ":");
		}
		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		btn_publish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imm.hideSoftInputFromInputMethod(
						SingleRequireDetailsActivity.this.getCurrentFocus()
								.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				if (isMainComment)
					new AddMainCommentAsyncTask().execute();
				else
					new AddSubCommentAyncTask().execute();

			}
		});
	}

	private class CheckIsParticipatedAsyncTask extends
			AsyncTask<Void, Void, ProtocalResponse> {

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM,
						ConstantValues.GET_USER_IS_PARTICIPATED + "");
				map.put("userID", GlobalValues.USER_ID);
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
				ToastUtil
						.showToast("网络连接错误", SingleRequireDetailsActivity.this);
			} else {
				if (result.getResponseCode() == 0) {
					// 参加了
					btn_participate.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.cancel_bg));
					btn_participate.setText("取消");
				} else {
					// 未参加
				}
			}
		}
	}

	/**
	 * 点击相约后推送消息
	 * 
	 * @param view
	 */

	public void onSendTxtMsg(String user_id, String userName) {
		EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);

		msg.setReceipt(user_id);
		msg.setAttribute(ConstantValues.MSG_CATEGAORY,
				ConstantValues.NOTIFICATION);
		TextMessageBody body = new TextMessageBody(userName);
		msg.addBody(body);

		try {
			EMChatManager.getInstance().sendMessage(msg);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void back(View v) {
		finish();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	private class AddMainCommentAsyncTask extends
			AsyncTask<Void, Void, ProtocalResponse> {
		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				Map<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM, ConstantValues.ADD_COMMENT
						+ "");
				map.put("infoID", info.getId());
				map.put("userID", GlobalValues.USER_ID);
				map.put("content", EncodeUtil.chinese2URLEncode(et_reply
						.getText().toString()));
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
			if (result != null) {
				if (result.getResponseCode() == 0) {
					ToastUtil.showToast(result.getResponse(),
							SingleRequireDetailsActivity.this);
					getComments();
				}
			} else {
				ToastUtil.showToast("网络错误", SingleRequireDetailsActivity.this);
			}
			clearReplyBox();
		}

	}

	private void clearReplyBox() {
		et_reply.setText("");
		ll_reply.setVisibility(View.GONE);
	}

	private class GetCommentsAsyncTask extends
			AsyncTask<Void, Void, ProtocalResponse> {

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				Map<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM, ConstantValues.GET_COMMENT
						+ "");
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
				ToastUtil.showToast("网络错误", SingleRequireDetailsActivity.this);
			} else {
				if (result.getResponseCode() == 0) {
					comments = new Gson().fromJson(result.getResponse(),
							new TypeToken<List<Comment>>() {
							}.getType());
					commentAdapter.notifyDataSetChanged();
				} else {
					ToastUtil.showToast(result.getResponse(),
							SingleRequireDetailsActivity.this);
				}
				// lv_comments.setSelection(lv_comments.getCount() - 1);
			}
		}
	}

	private class CommentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (comments != null)
				return comments.size();
			else
				return 0;
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			if (convertView != null) {
				view = convertView;
			} else {
				view = View.inflate(SingleRequireDetailsActivity.this,
						R.layout.lv_comment_item, null);
			}
			ListView lv_sub_comment = (ListView) view
					.findViewById(R.id.lv_sub_comment);
			LayoutParams params = lv_sub_comment.getLayoutParams();
			params.height = DensityUtil.dip2px(
					SingleRequireDetailsActivity.this, 19)
					* (comments.get(position).getSubcomments().size() + 1);
			lv_sub_comment.setLayoutParams(params);
			// tv_reply.setText(Html.fromHtml("<font color=blue>"+comments.get(position).getNickname()+"</font>回复:"+comments.get(position).getContent()));
			lv_sub_comment.setAdapter(new BaseAdapter() {

				@Override
				public View getView(int pos, View convertView, ViewGroup parent) {
					TextView tv_reply;
					if (convertView != null) {
						tv_reply = (TextView) convertView;
					} else {
						tv_reply = (TextView) View.inflate(
								SingleRequireDetailsActivity.this,
								R.layout.subcomment_list_item, null);
					}
					if (pos == 0) {
						tv_reply.setText(Html.fromHtml("<font color=blue>"
								+ comments.get(position).getNickname()
								+ "</font>回复:"
								+ comments.get(position).getContent()));
					} else {
						Subcomment subcomment = comments.get(position)
								.getSubcomments().get(pos - 1);
						tv_reply.setText(Html
								.fromHtml("&nbsp;&nbsp;&nbsp<font color=blue>"
										+ subcomment.getNickname()
										+ "</font>回复<font color=blue>"
										+ subcomment.getT_nickname()
										+ "</font>:" + subcomment.getContent()));
					}
					return tv_reply;
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public Object getItem(int position) {
					return position;
				}

				@Override
				public int getCount() {
					if (comments.get(position).getSubcomments() != null)
						return comments.get(position).getSubcomments().size() + 1;
					else
						return 1;
				}
			});
			lv_sub_comment.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int pos, long id) {
					isMainComment = false;
					commentPos = position;
					subCommentPos = pos - 1;
					showReplyBox();
				}
			});
			return view;
		}

	}

	private class AddSubCommentAyncTask extends
			AsyncTask<Void, Void, ProtocalResponse> {

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				Map<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM,
						ConstantValues.ADD_SUB_COMMENT + "");
				map.put("content", EncodeUtil.chinese2URLEncode(et_reply
						.getText().toString()));
				map.put("userID", GlobalValues.USER_ID);
				if(subCommentPos<0) {
					map.put("t_userID", comments.get(commentPos).getU_id());
				} else {
					map.put("t_userID", comments.get(commentPos).getSubcomments()
							.get(subCommentPos).getU_id());
					
				}
				map.put("commentID", comments.get(commentPos).getId() + "");
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
			if (result != null) {
				ToastUtil.showToast(result.getResponse(),
						SingleRequireDetailsActivity.this);
			} else {
				ToastUtil.showToast("网络错误", SingleRequireDetailsActivity.this);
			}
			clearReplyBox();
			getComments();
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
				ToastUtil.showToast("网络错误", SingleRequireDetailsActivity.this);
			} else {
				String json = result.getResponse();
				users = new Gson().fromJson(json, new TypeToken<List<User>>() {
				}.getType());
				for (final User user : users) {
					onSendTxtMsg(user.getId(), user.getNickname());
				}
			}
		}
	}
}
