package com.yueme;

import com.yueme.ui.BitmapTool;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ViewPictureActivity extends Activity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_picture);
		String bitmapUri = getIntent().getStringExtra("bitmapUri");
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapUri,options);
		
		options.inSampleSize = BitmapTool.computeSampleSize(options, -1, 1000*2000);
		options.inJustDecodeBounds = false;
		
		Bitmap bitmap =  BitmapFactory.decodeFile(bitmapUri,options);
	
		ImageView img = (ImageView) findViewById(R.id.imageView1);
		img.setImageBitmap(bitmap);
		RelativeLayout layout =(RelativeLayout) findViewById(R.id.contentLayout);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		finish();
		return super.onKeyDown(keyCode, event);
	}
	
	

}
