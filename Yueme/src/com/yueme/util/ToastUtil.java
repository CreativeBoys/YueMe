package com.yueme.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	private ToastUtil(){};
	public static void showToast(String str, Context context) {
		Toast.makeText(context, str, 0).show();
	}
}
