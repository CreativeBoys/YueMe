package com.yueme.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * @author heshaokang	
 * 2015-3-14 下午3:54:09
 */
public class MyDialog extends Dialog implements DialogInterface{

	/**
	 * @param context
	 * @param cancelable
	 * @param cancelListener
	 */
	public MyDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		
	}

	/**
	 * @param context
	 * @param theme
	 */
	public MyDialog(Context context, int theme) {
		super(context, theme);
		
	}

	/**
	 * @param context
	 */
	public MyDialog(Context context) {
		super(context);
		
	}
	
	

}
