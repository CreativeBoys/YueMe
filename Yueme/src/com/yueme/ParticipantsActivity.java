package com.yueme;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yueme.domain.ChatGroupInfo;
import com.yueme.domain.Info;
import com.yueme.domain.ProtocalResponse;
import com.yueme.domain.User;
import com.yueme.task.GeneralGetAsyncTask;
import com.yueme.util.DialogUtil;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;

public class ParticipantsActivity extends Activity {
	private List<User> users;
	private ListView lv_participants;
	private TextView tv_createTime;
	private TextView tv_destTime;
	private Info info;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private ParticipantsAdapter adapter;
	private int userPos = 0;
	private TextView tv_info;
	private ChatGroupInfo chatGroupInfo;
	private boolean isNetInfoLoaded;
	private Button btn_group_talk;
	private LinearLayout ll_btn_container;
	private Button btn_begin;
	private Button btn_cancel;
	private boolean showButton;
	private boolean showEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_participants);
		info = (Info) getIntent().getSerializableExtra("info");
		showButton = getIntent().getBooleanExtra("showButton", true);
		showEnd = getIntent().getBooleanExtra("showEnd", false);
		chatGroupInfo = new ChatGroupInfo(info.getGroup_id());
		initView();
		getNetInfo();
		setListenerAndAdapter();
	}

	private void setListenerAndAdapter() {
		adapter = new ParticipantsAdapter();
		lv_participants.setAdapter(adapter);

		btn_group_talk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isNetInfoLoaded) {
					Intent intent = new Intent(ParticipantsActivity.this,
							ChatGroupActivity.class);
					intent.putExtra("chatGroupInfo", chatGroupInfo);
					startActivity(intent);
				} else {
					ToastUtil.showToast("信息尚未加载完毕，请稍等。。。",
							ParticipantsActivity.this);
				}

			}
		});
	}

	public void back(View v) {
		finish();
	}

	private void getNetInfo() {

		tv_info.setText(info.getContent());
		tv_createTime.setText("创建时间："
				+ DateFormat.format("yyyy-MM-dd:hh:mm", info.getCreate_day()));
		tv_destTime.setText("开始时间："
				+ DateFormat.format("yyyy-MM-dd:hh:mm", info.getDeadline()));
		new ParticipantsAsyncTast().execute();
	}

	private void initView() {
		btn_group_talk = (Button) findViewById(R.id.btn_group_talk);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		lv_participants = (ListView) findViewById(R.id.lv_participants);
		tv_createTime = (TextView) findViewById(R.id.tv_createTime);
		tv_destTime = (TextView) findViewById(R.id.tv_destTime);
		tv_info = (TextView) findViewById(R.id.tv_info);
		ll_btn_container = (LinearLayout) findViewById(R.id.ll_btn_container);
		btn_begin = (Button) findViewById(R.id.btn_begin);
		if (!showButton) {
			ll_btn_container.setVisibility(View.GONE);
			if (info.getDeadline() - new Date().getTime() > 0) {
				btn_begin.setVisibility(View.VISIBLE);
				btn_begin.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						DialogUtil.showConfirmDialog(ParticipantsActivity.this,
								"确定立即开始吗?", new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 提前开始
										Map<String, String> map = new LinkedHashMap<String, String>();
										map.put(ConstantValues.REQUESTPARAM, ConstantValues.BEGIN_NOW+"");
										map.put("infoID", info.getId());
										new GeneralGetAsyncTask() {
											@Override
											public void doOnPost(ProtocalResponse response) {
												if(response!=null&&response.getResponseCode()==0) {
													Intent intent = new Intent(ParticipantsActivity.this,ParticipantsActivity.class);
													intent.putExtra("info", info);
													intent.putExtra("showEnd", true);
													startActivity(intent);
													
													ParticipantsActivity.this.finish();
												} else {
													ToastUtil.showToast("网络错误", ParticipantsActivity.this);
												}
											}
										}.execute(map);
									}
								});
					}
				});
			}
		}
		
		if(showEnd) {
			btn_cancel.setText("结束");
			
			btn_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					DialogUtil.showConfirmDialog(ParticipantsActivity.this, "确定要结束吗？",new AlertDialog.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//结束，发推送给其他的参与者，并弹框选择评分
//							synchronized (users) {
//								boolean first = true;
//								if(!first) {
//									try {
//										users.wait();
//									} catch (InterruptedException e1) {
//										e1.printStackTrace();
//									}
//								}
								if(users!=null&&users.size()>0)
								showVerticalDialog();
								
//								first = false;
							}
//						}

						private void showVerticalDialog() {
							DialogUtil.showVerticalRadioDialog(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									DialogUtil.closeVerticalRadioDialog();
									userPos++;
									if(users.size()>userPos)
									showVerticalDialog();
									else 
										ParticipantsActivity.this.finish();
//										notifyAll();
								}
							}, new OnClickListener() {
								@Override
								public void onClick(View v) {
									DialogUtil.closeVerticalRadioDialog();
									userPos++;
									if(users.size()>userPos)
										showVerticalDialog();
									else 
										ParticipantsActivity.this.finish();
//										notifyAll();
								}
							}, "请对参与者"+users.get(userPos).getNickname()+"进行评价", ParticipantsActivity.this, "非常好","好","普通","不太好","很差劲");
						}
					});
				}
			});
		}
	}

	private class ParticipantsAsyncTast extends
			AsyncTask<Void, Void, ProtocalResponse> {
		int i;

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
				ToastUtil.showToast("网络错误", ParticipantsActivity.this);
			} else {
				String json = result.getResponse();
				users = new Gson().fromJson(json, new TypeToken<List<User>>() {
				}.getType());
				for (i = 0; i < users.size(); i++) {
					final User user = users.get(i);
					new AsyncTask<Void, Void, Bitmap>() {

						@Override
						protected Bitmap doInBackground(Void... params) {
							return NetUtil.getBitmapFromServer(user
									.getHead_img_path());
						}

						protected void onPostExecute(Bitmap result) {
							bitmaps.add(result);
							adapter.notifyDataSetChanged();
							chatGroupInfo.addMember(user.getId(),
									user.getNickname(), result);
							if (i + 1 >= users.size()) {
								isNetInfoLoaded = true;
							}
						}
					}.execute();
				}
			}
		}
	}

	private class ParticipantsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (users != null)
				return users.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(ParticipantsActivity.this,
					R.layout.participant_liset_item, null);
			ImageView iv_head = (ImageView) view
					.findViewById(R.id.iv_participant_head);
			TextView tv_nickname = (TextView) view
					.findViewById(R.id.tv_participant_nickname);
			TextView tv_academy = (TextView) view
					.findViewById(R.id.tv_participant_academy);
			if (bitmaps.size() > position) {
				Bitmap bitmap = bitmaps.get(position);
				if (bitmap != null) {
					iv_head.setImageBitmap(bitmaps.get(position));
				} else {
					iv_head.setImageResource(R.drawable.user_head);
				}
			}

			tv_academy
					.setText((users.get(position).getAcademy() == null ? "无学院"
							: users.get(position).getAcademy()));
			tv_nickname.setText(users.get(position).getNickname());
			return view;
		}

	}
}
