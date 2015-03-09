package com.yueme.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.yueme.R;


/**
 * @author heshaokang	
 * 2015-3-8 下午6:42:58
 */
public class SwipeBackLayout extends FrameLayout{
	//SwipeBackLayout所在的父布局
	private View mParentView;
	//划定的最小距离
	private int mTouchSlop;
	//手指按下的位置
	private int downX;
	private int downY;
	//临时存储x坐标
	private int tempX;
	private Scroller mScroller;
	private int viewWidth;
	//是否滑动
	private boolean isSliding;
	//是否关闭
	private boolean isFinish;
	//处理滑动逻辑的view
	private Activity mActivity;
	private Drawable mShadowDrawable;
	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context);
		mShadowDrawable = getResources().getDrawable(R.drawable.shadow_left);
	}
	
	public void attachToActivity(Activity activity) {
		mActivity = activity;
		TypedArray a = activity.getTheme().obtainStyledAttributes(
				new int[] { android.R.attr.windowBackground });
		int background = a.getResourceId(0, 0);
		a.recycle();

		ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
		ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
		decorChild.setBackgroundResource(background);
		decor.removeView(decorChild);
		addView(decorChild);
		setContentView(decorChild);
		decor.addView(this);
	}
	private void setContentView(View decorChild) {
		mParentView = (View) decorChild.getParent();
	}
	/**
	 * @param context
	 * @param attrs
	 */
	public SwipeBackLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * @param context
	 */
	public SwipeBackLayout(Context context) {
		super(context);
	}
	
	/**
	 * 事件拦截操作
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = tempX = (int) ev.getRawX();
			downY = (int) ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getRawX();
			// 满足此条件屏蔽SildingFinishLayout里面子类的touch事件
			if (moveX - downX > mTouchSlop
					&& Math.abs((int) ev.getRawY() - downY) < mTouchSlop) {
				return true;
			}
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		
		super.onLayout(changed, left, top, right, bottom);
		if(changed) {
			
			viewWidth = this.getWidth();
		}
		
	}
	
	
	/**
	 * 滑出屏幕 
	 */
	private void scrollRight() {
		final int delta = (int) (viewWidth+mParentView.getScaleX());
		//设置滚动参数
		mScroller.startScroll(mParentView.getScrollX(), 0, -delta+1, 0,Math.abs(delta));
		postInvalidate();
	}
	
	/**
	 * 回复原状
	 */
	private void scrollOrigin() {
		int delta = mParentView.getScrollX();
		mScroller.startScroll(mParentView.getScrollX(), 0, -delta,0, Math.abs(delta));
		postInvalidate();
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mShadowDrawable != null && mParentView != null) {

			int left = mParentView.getLeft()
					- mShadowDrawable.getIntrinsicWidth();
			int right = left + mShadowDrawable.getIntrinsicWidth();
			int top = mParentView.getTop();
			int bottom = mParentView.getBottom();

			mShadowDrawable.setBounds(left, top, right, bottom);
			mShadowDrawable.draw(canvas);
		}

	}
	
	@Override
	public void computeScroll() {
		//调用startScroll的时候 scroller.computeScrollOffset返回true
		if(mScroller.computeScrollOffset()) {
			//调用scrollerTo() 来更新子view 位置
			mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
			if (mScroller.isFinished() && isFinish) {
				mActivity.finish();
			}
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) event.getRawX();
			int deltaX = tempX - moveX;
			tempX = moveX;
			if (moveX - downX > mTouchSlop
					&& Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
				isSliding = true;
			}

			if (moveX - downX >= 0 && isSliding) {
				mParentView.scrollBy(deltaX, 0);
			}
			break;
		case MotionEvent.ACTION_UP:
			isSliding = false;
			if (mParentView.getScrollX() <= -viewWidth / 2) {
				isFinish = true;
				scrollRight();
			} else {
				scrollOrigin();
				isFinish = false;
			}
			break;
		}

		return true;
	}
	
}
