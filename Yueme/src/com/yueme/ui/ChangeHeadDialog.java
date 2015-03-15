package com.yueme.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yueme.R;

/**
 * @author heshaokang	
 * 2015-3-14 下午3:54:09
 */
public class ChangeHeadDialog extends Dialog implements DialogInterface{
	private LinearLayout main;
	private TextView from_picture;
	private TextView from_camera;
	private TextView cancel;
	private View mDialogView;
	private static int mDuration=700;
	 private boolean isCancelable=true;
	 private static  int mOrientation=1;
	 private volatile static  ChangeHeadDialog instance;
	/**
	 * @param context
	 * @param cancelable
	 * @param cancelListener
	 */
	public ChangeHeadDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	/**
	 * @param context
	 * @param theme
	 */
	public ChangeHeadDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	/**
	 * @param context
	 */
	public ChangeHeadDialog(Context context) {
		super(context);
		init();
	}
	
	public static ChangeHeadDialog getInstance(Context context) {
		 int ort=context.getResources().getConfiguration().orientation;
	        if (mOrientation!=ort){
	            mOrientation=ort;
	            instance=null;
	        }
	        if (instance == null||((Activity) context).isFinishing()) {
	            synchronized (ChangeHeadDialog.class) {
	                if (instance == null) {
	                    instance = new ChangeHeadDialog(context,R.style.dialog_tran);
	                }
	            }
	        }
		return instance;
	}
	
	
	private void init() {
		mDialogView = View.inflate(getContext(), R.layout.change_head_alertdialog, null);
		main = (LinearLayout) mDialogView.findViewById(R.id.main);
		from_picture = (TextView) mDialogView.findViewById(R.id.from_picture);
		from_camera = (TextView) mDialogView.findViewById(R.id.from_camera);
		cancel = (TextView) mDialogView.findViewById(R.id.cancel);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(mDialogView);
		this.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				//开始动画
				start(main);
			}
		});
		main.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isCancelable) 
					dismiss();
			}
		});
	}

	
	public ChangeHeadDialog setFromPicture(View.OnClickListener click) {
		from_picture.setOnClickListener(click);
		return this;
	}
	public ChangeHeadDialog setFromCamera(View.OnClickListener click) {
		from_camera.setOnClickListener(click);
		return this;
	}
	public ChangeHeadDialog setCancel() {
		this.dismiss();
		return this;
	}
	
	
	@Override
	public void dismiss() {
		
		super.dismiss();
	}
	@Override
	public void show() {
		
		super.show();
	}
	
	protected void start(View view) {
		AnimatorSet anim = new AnimatorSet();
		 
		 anim.playTogether(
				 ObjectAnimator.ofFloat(view, "translationY", -300, 0).setDuration(mDuration),
	                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration*3/2)

	        );
		 anim.start();
		
	}
	//

}
