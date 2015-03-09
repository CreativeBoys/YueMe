package com.yueme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author heshaokang	
 * 2015-3-7 下午3:51:48
 */
public class UserInformation extends SwipeBackActivity implements OnClickListener{
	private TextView save_information;
	private TextView backlogin;  //退出登录
	private ImageView user_back;  //返回键
	private TextView gender; //性别
	//头像
	private ImageView user_head;
	private Bitmap head;
	private String[] items = new String[]{
		"从相册中选取","拍照"	
	};
	@SuppressLint("SdCardPath")
	private static String path = "/sdcard/myHead/"; //sd卡路径
	
	//请求码
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int  RESULT_REQUEST_CODE = 2;
	
	private ListView mListView;
	private List<UserInfor> mList ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uer_information);
	
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
		Bitmap bt = BitmapFactory.decodeFile(path+"head.jpg");
		if(bt!=null) {
			@SuppressWarnings("deprecation")
			Drawable drawable = new BitmapDrawable(bt); //转换成drawable
			user_head.setImageDrawable(drawable);
		}else {
			//从服务器取图片，再保存到sd卡中
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
		
	}
	private AlertDialog changGender;
	
	private void showChangeGender() {
		 
		AlertDialog.Builder builder = new Builder(UserInformation.this);
		View view = LayoutInflater.from(UserInformation.this).inflate(R.layout.change_gender, null);
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
		changGender.setView(view,0,0,0,0);
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
		case R.id.save_information:    //保存用户信息
			saveInformation();
			break;
		case R.id.backlogin:     	  //退出登录
			
			break;
		case R.id.user_back:
			finish();
			break; 
		case R.id.user_gender_value:
			showChangeGender();   //更改性别
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
		
	}
	
	private TextView from_picture;
	private TextView from_camera;
	private TextView cancel;
	private AlertDialog dialog;
	//显示对话框
	private void showDialog() {
		
		AlertDialog.Builder builder = new Builder(UserInformation.this);
		View view = View.inflate(UserInformation.this, R.layout.change_head_alertdialog, null);
		from_picture = (TextView) view.findViewById(R.id.from_picture);
		from_camera = (TextView) view.findViewById(R.id.from_camera);
		cancel = (TextView) view.findViewById(R.id.cancel);
		
		from_picture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentFromGallery = new Intent(Intent.ACTION_PICK,null);
				intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
			}
		});
		from_camera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"head.jpg")));
				startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode!=RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				File temp = new File(Environment.getExternalStorageDirectory()+"/head.jpg");
				startPhotoZoom(Uri.fromFile(temp));
				break;
			case RESULT_REQUEST_CODE:
				if(data!=null) {
					Bundle extras = data.getExtras();
					head = extras.getParcelable("data");
					if(head!=null) {
						//上传到服务器
						
						//保存到sd卡
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
		//宽高比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY",1);
		//裁剪图片大小
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
		
	}
	
	/**
	 * 保存裁剪后图片的数据
	 */
	private void setPicToView(Bitmap mBitmap) {
		String sdStatus = Environment.getExternalStorageState();
		//检测sd是否可用
		if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			return ;
		}
		FileOutputStream fos = null;
		File file = new File(path);
		file.mkdirs(); //创建dir
		String fileName = path+"head.jpg";  //图片名称
		try {
			fos = new FileOutputStream(fileName);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); //将数据写入文件
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}finally {
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
			View view = LayoutInflater.from(UserInformation.this).inflate(R.layout.user_information_listitem , parent,false);
			ImageView user_head = (ImageView) view.findViewById(R.id.user_head);
			TextView name = (TextView)  view.findViewById(R.id.user_name);
			EditText value = (EditText)  view.findViewById(R.id.user_value);
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
		public UserInfor(int icon,String name,String value) {
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
	
}
