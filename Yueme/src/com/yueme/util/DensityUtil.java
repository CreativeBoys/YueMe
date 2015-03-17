package com.yueme.util;


import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DensityUtil {
    public static int dip2px(Context context, float dpValue) {  
    	WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    	DisplayMetrics metrics = new DisplayMetrics();
    	manager.getDefaultDisplay().getMetrics(metrics);
        final float scale = metrics.density;
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
}
