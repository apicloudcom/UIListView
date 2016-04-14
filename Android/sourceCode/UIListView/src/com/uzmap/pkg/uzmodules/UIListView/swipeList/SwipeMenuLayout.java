package com.uzmap.pkg.uzmodules.UIListView.swipeList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * 
 * @author baoyz
 * @date 2014-8-23
 * 
 */
public class SwipeMenuLayout extends FrameLayout {

	private static final int CONTENT_VIEW_ID = 1;
	private static final int MENU_VIEW_ID = 2;

	private static final int STATE_RIGHT_CLOSE = 0;
	private static final int STATE_RIGHT_OPEN = 1;

	private static final int STATE_LEFT_CLOSE = 2;
	private static final int STATE_LEFT_OPEN = 3;

	private View mContentView;
	private SwipeMenuView mMenuView;

	/**
	 * new added code
	 */
	// private SwipeMenuView mLeftMenuView;

	private int mDownX;
	private int state = STATE_RIGHT_CLOSE;
	private GestureDetectorCompat mGestureDetector;
	private OnGestureListener mGestureListener;
	private boolean isRightFling;
	
	// private boolean isLeftFling;

	private int MIN_FLING = dp2px(15);
	private int MAX_VELOCITYX = -dp2px(500);
	private ScrollerCompat mOpenScroller;
	private ScrollerCompat mCloseScroller;
	private int mBaseX;
	private int position;
	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;

	public SwipeMenuLayout(View contentView, SwipeMenuView menuView, SwipeMenuView mLeftMenuView) {
		this(contentView, menuView, mLeftMenuView, null, null);
	}
	
	public void updateSwipeMenuLayout(View contentView, SwipeMenuView menuView){
		
		this.mContentView = contentView;
		
		this.mMenuView = menuView;
		mMenuView.setLayout(this);
		
		init();
	}
	
	public void setMenuView(SwipeMenuView menuView){
		this.mMenuView = menuView;
	}

	public SwipeMenuLayout(View contentView, SwipeMenuView menuView, SwipeMenuView mLeftMenuView, Interpolator closeInterpolator, Interpolator openInterpolator) {
		super(contentView.getContext());
		mCloseInterpolator = closeInterpolator;
		mOpenInterpolator = openInterpolator;
		mContentView = contentView;
		mMenuView = menuView;

		/**
		 * new added code
		 */
//		this.mLeftMenuView = mLeftMenuView;
//		this.mLeftMenuView.setLayout(this);
		if (mMenuView != null) {
			mMenuView.setLayout(this);
		}
		init();
	}

	// private SwipeMenuLayout(Context context, AttributeSet attrs, int
	// defStyle) {
	// super(context, attrs, defStyle);
	// }
	

	private SwipeMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private SwipeMenuLayout(Context context) {
		super(context);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
		mMenuView.setPosition(position);
	}

	private void init() {
		setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				isRightFling = false;
				// isLeftFling = false;
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				// TODO
				if ((e1.getX() - e2.getX()) > MIN_FLING && velocityX < MAX_VELOCITYX) {
					isRightFling = true;
				}

				if ((e2.getX() - e1.getX()) > MIN_FLING) {
					// isLeftFling = true;
				}

				return super.onFling(e1, e2, velocityX, velocityY);
			}
		};
		mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);

		// mScroller = ScrollerCompat.create(getContext(), new
		// BounceInterpolator());
		if (mCloseInterpolator != null) {
			mCloseScroller = ScrollerCompat.create(getContext(), mCloseInterpolator);
		} else {
			mCloseScroller = ScrollerCompat.create(getContext());
		}
		if (mOpenInterpolator != null) {
			mOpenScroller = ScrollerCompat.create(getContext(), mOpenInterpolator);
		} else {
			mOpenScroller = ScrollerCompat.create(getContext());
		}

		LayoutParams contentParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mContentView.setLayoutParams(contentParams);
		if (mContentView.getId() < 1) {
			mContentView.setId(CONTENT_VIEW_ID);
		}

		mMenuView.setId(MENU_VIEW_ID);
		mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		/**
		 * new added code
		 */
		// addView(mLeftMenuView);
		
		addView(mContentView);
		addView(mMenuView);
		
	}
	
	public void cleanUp(){
		this.removeView(mContentView);
		this.removeView(mMenuView);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public boolean onSwipe(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			mDownX = (int) event.getX();
			isRightFling = false;
			// isLeftFling = false;

			break;
		case MotionEvent.ACTION_MOVE:

			int dis = (int) (mDownX - event.getX());
			if (state == STATE_RIGHT_OPEN) {
				dis += mMenuView.getWidth();
			}

			if (mMenuView.getWidth() == 0) {
				break;
			}
			
			swipe(dis);

			break;
		case MotionEvent.ACTION_UP:
			/*
			 * if (isLeftFling || (event.getX() - mDownX) >
			 * (mMenuView.getWidth() / 2)) { smoothOpenLeftMenu(); } else
			 */
			if (isRightFling || (mDownX - event.getX()) > (mMenuView.getWidth() / 2) && mDownX - event.getX() > 0) {
				// open
				smoothOpenMenu();
			} else {
				// close
				smoothCloseMenu();
				return false;
			}
			break;
		}
		return true;
	}

	public boolean isOpen() {
		return state == STATE_RIGHT_OPEN;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	private void swipe(int dis) {

		if (dis > mMenuView.getWidth()) {
			dis = mMenuView.getWidth();
		}

		/*
		 * if (state == STATE_LEFT_OPEN) { if (Math.abs(dis) >
		 * mLeftMenuView.getWidth()) { dis = mLeftMenuView.getWidth(); } }
		 */

		/*
		 * if (dis < 0) { dis = 0; }
		 */

		// mLeftMenuView.layout(mLeftMenuView.getLeft() + dis,
		// mContentView.getTop(), mLeftMenuView.getLeft() + dis +
		// mLeftMenuView.getMeasuredWidth(), getMeasuredHeight());

		if (dis >= 0) {
			mContentView.layout(-dis, mContentView.getTop(), mContentView.getWidth() - dis, getMeasuredHeight());
			mMenuView.layout(mContentView.getWidth() - dis, mMenuView.getTop(), mContentView.getWidth() + mMenuView.getWidth() - dis, mMenuView.getBottom());
		}

		/*
		 * if (dis < 0) { mContentView.layout(Math.abs(dis),
		 * mContentView.getTop(), mContentView.getWidth() + Math.abs(dis),
		 * getMeasuredHeight()); mLeftMenuView.layout(mContentView.getLeft() -
		 * mLeftMenuView.getMeasuredWidth(), mLeftMenuView.getTop(),
		 * mContentView.getLeft(), getMeasuredHeight()); }
		 */
	}

	@Override
	public void computeScroll() {

		if (state == STATE_RIGHT_OPEN) {
			if (mOpenScroller.computeScrollOffset()) {
				swipe(mOpenScroller.getCurrX());
				postInvalidate();
			}

		} else if (state == STATE_RIGHT_CLOSE) {
			if (mCloseScroller.computeScrollOffset()) {
				swipe(mBaseX - mCloseScroller.getCurrX());
				postInvalidate();
			}
		}

		if (state == STATE_LEFT_OPEN) {
			if (mOpenScroller.computeScrollOffset()) {
				swipe(mOpenScroller.getCurrX());
				// scrollTo(mOpenScroller.getCurrX(), 0);
				postInvalidate();
			}

		} else if (state == STATE_LEFT_CLOSE) {

		}

	}

	public void smoothCloseMenu() {
		state = STATE_RIGHT_CLOSE;
		mBaseX = -mContentView.getLeft();
		mCloseScroller.startScroll(0, 0, mBaseX, 0, 350);
		postInvalidate();
	}

	public void smoothOpenMenu() {
		state = STATE_RIGHT_OPEN;
		mOpenScroller.startScroll(-mContentView.getLeft(), 0, mMenuView.getWidth(), 0, 350);
		postInvalidate();
	}

	public void closeMenu() {
		if (mCloseScroller.computeScrollOffset()) {
			mCloseScroller.abortAnimation();
		}
		if (state == STATE_RIGHT_OPEN) {
			state = STATE_RIGHT_CLOSE;
			swipe(0);
		}
	}

	public void openMenu() {
		if (state == STATE_RIGHT_CLOSE) {
			state = STATE_RIGHT_OPEN;
			swipe(mMenuView.getWidth());
		}
	}

	/**
	 * 　　startX 水平方向滚动的偏移值，以像素为单位。正值表明滚动将向左滚动
	 * 
	 * 　　startY 垂直方向滚动的偏移值，以像素为单位。正值表明滚动将向上滚动
	 * 
	 * 　　dx 水平方向滑动的距离，正值会使滚动向左滚动
	 * 
	 * 　　dy 垂直方向滑动的距离，正值会使滚动向上滚动
	 * 
	 * 我的理解是：
	 * 
	 * startX 表示起点在水平方向到原点的距离（可以理解为X轴坐标，但与X轴相反），正值表示在原点左边，负值表示在原点右边。
	 * 
	 * dx 表示滑动的距离，正值向左滑，负值向右滑。
	 * 
	 * 这与我们感官逻辑相反，需要注意。
	 * 
	 * 
	 */

//	public void smoothCloseLeftMenu() {
//		state = STATE_LEFT_CLOSE;
//		mOpenScroller.startScroll(mLeftMenuView.getLeft(), 0, mLeftMenuView.getWidth(), 0, 350);
//		postInvalidate();
//	}

//	public void smoothOpenLeftMenu() {
//		state = STATE_LEFT_OPEN;
//		mOpenScroller.startScroll(mLeftMenuView.getLeft(), 0, -mLeftMenuView.getWidth(), 350);
//
//		Log.i(TAG, "mLeftMenuView.getWidth(): " + mLeftMenuView.getWidth());
//
//		postInvalidate();
//	}

	public void closeLeftMenu() {
		// TODO :
	}

	public void openLeftMenu() {
		// TODO :
	}

	public View getContentView() {
		return mContentView;
	}

	public SwipeMenuView getMenuView() {
		return mMenuView;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMenuView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));

		/**
		 * new added code
		 */
		// mLeftMenuView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		// mLeftMenuView.layout(-mLeftMenuView.getMeasuredWidth(), 0, 0, mContentView.getMeasuredHeight());
		mContentView.layout(0, 0, getMeasuredWidth(), mContentView.getMeasuredHeight());
		mMenuView.layout(getMeasuredWidth(), 0, getMeasuredWidth() + mMenuView.getMeasuredWidth(), mContentView.getMeasuredHeight());
	}

	public void setMenuHeight(int measuredHeight) {
		LayoutParams params = (LayoutParams) mMenuView.getLayoutParams();
		if (params.height != measuredHeight) {
			params.height = measuredHeight;
			mMenuView.setLayoutParams(mMenuView.getLayoutParams());
		}
	}
}
