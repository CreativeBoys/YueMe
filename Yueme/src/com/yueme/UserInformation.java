package com.yueme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author heshaokang	
 * 2015-3-7 下午3:51:48
 */
public class UserInformation extends SwipeBackActivity implements OnClickListener{
	private EditText nickname; 	 //昵称
	private EditText username ;  //真实姓名
	private EditText school;	 //学校
	private EditText academy;    //学院
	private EditText grade;      //年级
	private EditText signature;  //个性签名
	private TextView save_information;
	private TextView backlogin;  //退出登录
	private ImageView user_back;  //返回键
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
		nickname = (EditText) findViewById(R.id.nickname);
		username = (EditText) findViewById(R.id.username);
		school = (EditText) findViewById(R.id.school);
		academy = (EditText) findViewById(R.id.academy);
		grade = (EditText) findViewById(R.id.grade);
		signature = (EditText) findViewById(R.id.signature);
		save_information = (TextView) findViewById(R.id.save_information);
		backlogin = (TextView) findViewById(R.id.backlogin);
		user_back = (ImageView) findViewById(R.id.user_back);
		Bitmap bt = BitmapFactory.decodeFile(path+"head.jpg");
		if(bt!=null) {
			@SuppressWarnings("deprecation")
			Drawable drawable = new BitmapDrawable(bt); //转换成drawable
			user_head.setImageDrawable(drawable);
		}else {
			//从服务器取图片，再保存到sd卡中
		}
	}
	
	private void initEvent() {
		user_head.setOnClickListener(this);
		save_information.setOnClickListener(this);
		backlogin.setOnClickListener(this);
		user_back.setOnClickListener(this);
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
		default:
			break;
		}
	}
	
	/**
	 * 得到用户输入的信息
	 */
	public void getEditText() {
		final String nicknameEdit = nickname.getText().toString().trim();
		final String usernameEdit = username.getText().toString().trim();
		final String schoolEdit = school.getText().toString().trim();
		final String academyEdit = academy.getText().toString().trim();
		final String gradEdit = grade.getText().toString().trim();
		final String signatureEdit = signature.getText().toString().trim();
	}
	
	/**
	 * 保存信息到服务器
	 */
	public void saveInformation() {
		
	}
	//显示对话框
	private void showDialog() {
		new AlertDialog.Builder(this)
		.setTitle("设置头像")
		.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				switch (which) {
				case 0:
					Intent intentFromGallery = new Intent(Intent.ACTION_PICK,null);
					intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
					break;
				case 1:
					Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"head.jpg")));
					startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
					break;
				default:
					break;
				}
			}

			
		}).setNegativeButton("取消", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		}).show();
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
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
	
}
