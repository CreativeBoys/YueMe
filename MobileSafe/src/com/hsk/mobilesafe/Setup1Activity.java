package com.hsk.mobilesafe;

import android.content.Intent;
import android.os.Bundle;

/**
 * @author heshaokang	
 * 2014-12-5 ����2:30:55
 */
public class Setup1Activity extends BaseSetupActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	@Override
	public void showPre() {
		
	}

	@Override
	public void showNext() {
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();
		//Ҫ����finish��startActivity��ִ�ж���Ч��
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}
	
}
