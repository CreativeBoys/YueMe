package com.yueme;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yueme.domain.ProtocalResponse;
import com.yueme.domain.User;
import com.yueme.ui.ChangeHeadDialog;
import com.yueme.util.EncodeUtil;
import com.yueme.util.NetUtil;
import com.yueme.util.StreamUtil;
import com.yueme.util.ToastUtil;
import com.yueme.values.ConstantValues;
import com.yueme.values.GlobalValues;

/**
 * @author heshaokang 2015-3-7 下午3:51:48
 */
public class UserInformation extends SwipeBackActivity implements
		OnClickListener {
	private TextView save_information;
	private TextView backlogin; // 退出登录
	private ImageView user_back; // 返回键
	private TextView gender; // 性别
	// 头像
	private ImageView user_head;
	private Bitmap head;
	private String[] items = new String[] { "从相册中选取", "拍照" };
	@SuppressLint("SdCardPath")
	private File path; // sd卡路径

	// 请求码
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	private ListView mListView;
	private List<UserInfor> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uer_information);
		path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		initView();
		initEvent();

	}

	private void initView() {
		user_head = (ImageView) findViewById(R.id.user_head);
		mListView = (ListView) findViewById(R.id.user_information_list);
		save_information = (TextView) findViewById(R.id.save_information);
		backlogin = (TextView) findViewById(R.id.backlogin);
		user_back = (ImageView) findViewById(R.id.user_back);
		gender = (TextView) findViewById(R.id.user_gender_value);
		bt = BitmapFactory.decodeFile(path + "/head.jpg");
		if (bt != null) {
			@SuppressWarnings("deprecation")
			Drawable drawable = new BitmapDrawable(bt); // 转换成drawable
			user_head.setImageDrawable(drawable);
		} else {
			// 从服务器取图片，再保存到sd卡中
			new GetUserHeadAsyncTask().execute();
		}

		mList = new ArrayList<UserInformation.UserInfor>();
		mList.add(new UserInfor(R.drawable.user_head, "昵称", "请填写你的昵称"));
		mList.add(new UserInfor(R.drawable.user_head, "姓名", "填写姓名，方便好友找到你"));
		mList.add(new UserInfor(R.drawable.user_head, "学校", "填写学校，方便找到你的校友"));
		mList.add(new UserInfor(R.drawable.user_head, "学院", "填写学院"));
		mList.add(new UserInfor(R.drawable.user_head, "年级", "填写年级"));

		mListView.setAdapter(new MyAdapter());
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
		});
		new GetUserInfoAsyncTask().execute();

	}

	private AlertDialog changGender;

	private void showChangeGender() {

		AlertDialog.Builder builder = new Builder(UserInformation.this);
		View view = LayoutInflater.from(UserInformation.this).inflate(
				R.layout.change_gender, null);
		TextView man = (TextView) view.findViewById(R.id.man);
		TextView woman = (TextView) view.findViewById(R.id.woman);
		man.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changGender.dismiss();
				gender.setText("男");
			}
		});
		woman.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changGender.dismiss();
				gender.setText("女");

			}
		});
		changGender = builder.create();
		changGender.setView(view, 0, 0, 0, 0);
		changGender.show();
		

	}

	private void initEvent() {
		user_head.setOnClickListener(this);
		save_information.setOnClickListener(this);
		backlogin.setOnClickListener(this);
		user_back.setOnClickListener(this);
		gender.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_head:
			showDialog();
			break;
		case R.id.save_information: // 保存用户信息
			saveInformation();
			break;
		case R.id.backlogin: // 退出登录
			SharedPreferences sp = getSharedPreferences("yueme", MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.remove("userID");
			GlobalValues.USER_ID = null;
			edit.commit();
			this.finish();
			break;
		case R.id.user_back:
			overridePendingTransition(0, R.anim.base_slide_right_out);
			finish();
			break;
		case R.id.user_gender_value:
			showChangeGender(); // 更改性别
			break;
		default:
			break;
		}
	}

	/**
	 * 得到用户输入的信息
	 */
	public void getEditText() {

	}

	/**
	 * 保存信息到服务器
	 */
	public void saveInformation() {
		new SaveInformationAsyncTask().execute();
	}

	private TextView from_picture;
	private TextView from_camera;
	private TextView cancel;
	private AlertDialog dialog;
	private Bitmap bt;

	// 显示对话框
	private void showDialog() {

//		AlertDialog.Builder builder = new Builder(UserInformation.this);
//		View view = View.inflate(UserInformation.this,
//				R.layout.change_head_alertdialog, null);
//		from_picture = (TextView) view.findViewById(R.id.from_picture);
//		from_camera = (TextView) view.findViewById(R.id.from_camera);
//		cancel = (TextView) view.findViewById(R.id.cancel);
//
//		from_picture.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
//				intentFromGallery
//						.setDataAndType(
//								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//								"image/*");
//				startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
//			}
//		});
//		from_camera.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent cameraIntent = new Intent(
//						MediaStore.ACTION_IMAGE_CAPTURE);
//				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
//						.fromFile(new File(Environment
//								.getExternalStorageDirectory(), "head.jpg")));
//				startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
//			}
//		});
//		cancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//		});
//		dialog = builder.create();
//		dialog.setView(view, 0, 0, 0, 0);
//		dialog.show();
		
		ChangeHeadDialog dialog = ChangeHeadDialog.getInstance(this);
		dialog.setFromPicture(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
				intentFromGallery
						.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
				startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
			}
		}).setFromCamera(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
						.fromFile(new File(Environment
								.getExternalStorageDirectory(), "head.jpg")));
				startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
			}
		}).setCancel().show();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				File temp = new File(Environment.getExternalStorageDirectory()
						+ "/head.jpg");
				startPhotoZoom(Uri.fromFile(temp));
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					Bundle extras = data.getExtras();
					head = extras.getParcelable("data");
					if (head != null) {
						// 上传到服务器
						new UploadAsyncTask().execute();
						// 保存到sd卡
						setPicToView(head);
						user_head.setImageBitmap(head);
					}
					;
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 裁剪图片
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 宽高比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪图片大小
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);

	}

	/**
	 * 保存裁剪后图片的数据
	 */
	private void setPicToView(Bitmap mBitmap) {
		// String sdStatus = Environment.getExternalStorageState();
		// //检测sd是否可用
		// if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
		// return ;
		// }
		FileOutputStream fos = null;
		String fileName = path.getAbsolutePath() + "/head.jpg"; // 图片名称
		try {
			fos = new FileOutputStream(fileName);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 将数据写入文件
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return mList.size();
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
			View view = LayoutInflater.from(UserInformation.this).inflate(
					R.layout.user_information_listitem, parent, false);
			ImageView user_head = (ImageView) view.findViewById(R.id.user_head);
			TextView name = (TextView) view.findViewById(R.id.user_name);
			EditText value = (EditText) view.findViewById(R.id.user_value);
			UserInfor userinfor;
			userinfor = mList.get(position);
			user_head.setImageResource(userinfor.icon);
			name.setText(userinfor.name);
			value.setHint(userinfor.value);
			return view;
		}

	}

	class UserInfor {
		private int icon;
		private String name;
		private String value;

		public UserInfor(int icon, String name, String value) {
			this.icon = icon;
			this.name = name;
			this.value = value;
		}
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	private class UploadAsyncTask extends
			AsyncTask<Void, Void, ProtocalResponse> {
		String prefix = "--";
		String boundary = UUID.randomUUID().toString();
		String end = "\r\n";

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				URL url = new URL(ConstantValues.UPLOAD_HEAD_IMG);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				conn.setReadTimeout(5 * 1000);
				conn.setConnectTimeout(5 * 1000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=" + boundary);
				conn.setRequestProperty("Connection", "Keep-Alive");
				// User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64)
				// AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.187
				// Safari/535.1
				conn.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.187 Safari/535.1");
				conn.setRequestProperty("Charset", "UTF-8");
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				dos.writeBytes(prefix + boundary + end);
				baos.write((prefix + boundary + end).getBytes());
				dos.writeBytes("Content-Disposition: form-data; name=\"head\"; filename=\"head.jpg\""
						+ end);
				baos.write(("Content-Disposition: form-data; name=\"head\"; filename=\"head.jpg\"" + end)
						.getBytes());
				dos.writeBytes("Content-Type: image/jpeg" + end);
				baos.write(("Content-Type: image/jpeg" + end).getBytes());
				dos.writeBytes(end);
				baos.write(end.getBytes());
				baos.close();
				System.out.println(baos.toString());
				FileInputStream inputStream = new FileInputStream(new File(
						path, "head.jpg"));
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inputStream.read(buffer)) > 0) {
					dos.write(buffer, 0, len);
				}
				inputStream.close();
				dos.writeBytes(end);
				dos.writeBytes(prefix + boundary + end);
				dos.writeBytes("Content-Disposition: form-data; name=\"userID\""
						+ end);
				dos.writeBytes(end);
				dos.writeBytes(GlobalValues.USER_ID + end);
				dos.writeBytes(prefix + boundary + prefix + end);
				dos.flush();
				conn.connect();
				if (200 == conn.getResponseCode()) {
					InputStream is = conn.getInputStream();
					String json = StreamUtil.getString(is);
					Gson gson = new Gson();
					return gson.fromJson(json, ProtocalResponse.class);
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ProtocalResponse result) {
			if (result != null) {
				ToastUtil.showToast(result.getResponse(), UserInformation.this);
			} else {
				ToastUtil.showToast("网络出了点小问题，请稍后再试", UserInformation.this);
			}
		}

	}

	private class SaveInformationAsyncTask extends
			AsyncTask<Void, Void, ProtocalResponse> {
		private String[] keys = new String[] { "nickname", "real_name",
				"school", "academy", "grade" };

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put(ConstantValues.REQUESTPARAM, ConstantValues.SAVE_USER_INFO
					+ "");
			map.put("userID", GlobalValues.USER_ID);
			map.put("gender", EncodeUtil.chinese2URLEncode(gender.getText().toString()));
			for (int i = 0; i < mListView.getCount(); i++) {
				String value = ((EditText) mListView.getChildAt(i)
						.findViewById(R.id.user_value)).getText().toString();
				map.put(keys[i], EncodeUtil.chinese2URLEncode(value));
			}
			try {
				HttpGet get = new HttpGet(NetUtil.getUrlString(map));
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					InputStream inputStream = response.getEntity().getContent();
					String json = StreamUtil.getString(inputStream);
					Gson gson = new Gson();
					return gson.fromJson(json, ProtocalResponse.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ProtocalResponse result) {
			if (result != null) {
				ToastUtil.showToast(result.getResponse(), UserInformation.this);
			} else {
				ToastUtil.showToast("网络错误，请检查网络是否畅通", UserInformation.this);
			}
		}
	}

	private class GetUserHeadAsyncTask extends
			AsyncTask<Void, Void, ProtocalResponse> {

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM,
						ConstantValues.GET_USER_HEAD_IMG + "");
				map.put("userID", GlobalValues.USER_ID);
				HttpGet get = new HttpGet(NetUtil.getUrlString(map));
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					InputStream inputStream = response.getEntity().getContent();
					String json = StreamUtil.getString(inputStream);
					Gson gson = new Gson();
					return gson.fromJson(json, ProtocalResponse.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(final ProtocalResponse result) {
			if (result.getResponseCode() == 0) {
				new AsyncTask<Void, Void, Bitmap>() {

					@Override
					protected Bitmap doInBackground(Void... params) {
						return NetUtil
								.getBitmapFromServer(result.getResponse());
					}

					protected void onPostExecute(Bitmap result) {
						bt = result;
						Drawable drawable = new BitmapDrawable(bt); // 转换成drawable
						user_head.setImageDrawable(drawable);
						setPicToView(result);// 保存bitmap到手机
					};
				}.execute();

			} else {
				ToastUtil.showToast(result.getResponse(), UserInformation.this);
			}
		}
	}

	private class GetUserInfoAsyncTask extends
			AsyncTask<Void, Void, ProtocalResponse> {

		@Override
		protected ProtocalResponse doInBackground(Void... params) {
			try {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(ConstantValues.REQUESTPARAM,
						ConstantValues.GET_USER_ALL_INFO + "");
				map.put("userID", GlobalValues.USER_ID);
				HttpGet get = new HttpGet(NetUtil.getUrlString(map));
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					InputStream inputStream = response.getEntity().getContent();
					String json = StreamUtil.getString(inputStream);
					Gson gson = new Gson();
					return gson.fromJson(json, ProtocalResponse.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ProtocalResponse result) {
			if (result == null) {
				ToastUtil.showToast("网络连接异常，请稍后再试", UserInformation.this);
			} else {
				Gson gson = new Gson();
				User user = gson.fromJson(result.getResponse(), User.class);
				String values[] = new String[] { user.getNickname(),
						user.getReal_name(), user.getSchool(),
						user.getAcademy(), user.getGrade() };
				for (int i = 0; i < mListView.getCount(); i++) {
					((EditText) mListView.getChildAt(i).findViewById(
							R.id.user_value)).setText(values[i]);
				}
				if (user.getGender() != null)
					gender.setText(user.getGender());
			}
		}

	}
}
