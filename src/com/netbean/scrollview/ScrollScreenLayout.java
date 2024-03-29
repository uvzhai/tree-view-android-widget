package com.netbean.scrollview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollScreenLayout extends ViewGroup {

	public ScrollScreenLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public void setIndicator(IndicateListener aIndicator)
	{
		if (null != aIndicator)
		{
			this.mIndicateListener = aIndicator;
			final int childCount = this.getChildCount();
			this.mIndicateListener.onIndicatorInit(childCount);
		}
	}

	/**
	 * set is loop screen or not
	 * 
	 * @param isLoopScreen
	 */
	public void setLoopScreen(boolean isLoopScreen)
	{
		this.mIsLoopScreen = isLoopScreen;
	}
	
	public void setAutoScroll(boolean isAutoScroll)
	{
		if(mIsAutoScroll != isAutoScroll)
		{
			this.mIsAutoScroll = isAutoScroll;
			
			if(mIsAutoScroll)
			{
				// start auto scroll
				startScroll();
			}
		}
	}
	
	private void startScroll()
	{
		mHandler.sendMessageDelayed(mHandler.obtainMessage(KScrollScreen), FREQUENCE);
	}
	

	private void init(Context context)
	{
		mScroller = new Scroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();

		// mDetector = new GestureDetector(new
		// GestureDetector.SimpleOnGestureListener());
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		
		mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if(msg.what == KScrollScreen)
				{
					snapToScreen(mCurrentScreen + 1);
					ScrollScreenLayout.this.startScroll();
				}
				super.handleMessage(msg);
			}
		};
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int childLeft = 0;
		final int mTop = getPaddingTop();
		final int mBottom = getPaddingBottom();
		final int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE)
			{
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, mTop, childLeft + childWidth, mTop + child.getMeasuredHeight() - mBottom);
				childLeft += childWidth;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	public void computeScroll()
	{
		super.computeScroll();
		if (mScroller.computeScrollOffset())
		{
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
		}
		else
		{
//			Log.d(TAG, "computeScroll() update indicator: " + mCurrentScreen);
//			// update indicator
//			if (null != mIndicateListener)
//			{
//				if (mIsLoopScreen)
//				{
//					View aChild = this.getChildAt(mCurrentScreen);
//					int index = (Integer)aChild.getTag();
//					mIndicateListener.onIndicatorFull(index);
//				}
//				else
//				{
//					mIndicateListener.onIndicatorFull(mCurrentScreen);
//				}
//			}
		}
	}
	
//	@Override
//	public void scrollTo(int x, int y)
//	{
//		if(mIsLoopScreen)
//		{
//			final int width = getWidth();
//			final int count = getChildCount();
//			if( x <= -width/2)
//			{
//				x = width*count + x;
//			}
//			else if( x >= width*count - width/2)
//			{
//				x = x - width*count;
//			}
//		}
//		super.scrollTo(x, y);
//	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);

		//update indicator
		if (null != mIndicateListener)
		{
			float range = computeHorizontalScrollRange();
			float offset = getScrollX();
			float percent = offset / range;
			int position = Math.round((getChildCount() - 1) * percent);
			
			Log.d(TAG, "onScrollChanged: " + position);
			if (mIsLoopScreen)
			{
				// key point method
				View aChild = this.getChildAt(position);
				int index = (Integer)aChild.getTag();
				mIndicateListener.onIndicatorFull(index);
			}
			else
			{
				mIndicateListener.onIndicatorFull(position);
			}
		}
	}

	@Override
	protected int computeHorizontalScrollRange()
	{
		final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - getWidth();
		return availableToScroll;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST))
		{
			return true;
		}

		final float x = ev.getX();
		switch (action)
		{
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			final int touchSlop = mTouchSlop;
			boolean xMoved = xDiff > touchSlop;
			if (xMoved)
			{
				mTouchState = TOUCH_STATE_SCROLLING;
			}
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		boolean isIntercepted = (mTouchState != TOUCH_STATE_REST);
		return isIntercepted;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// filter unused gesture TODO
		final int action = event.getAction();
		final float x = event.getX();

		if (mVelocityTracker == null)
		{
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished())
			{
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			final int deltaX = (int) (x - mLastMotionX);
			final int touchSlop = mTouchSlop;

			boolean xMoved = Math.abs(deltaX) > touchSlop;
			if (xMoved)
			{
				mTouchState = TOUCH_STATE_SCROLLING;
			}

			if (TOUCH_STATE_SCROLLING == mTouchState)
			{
				mLastMotionX = x;

				Log.d(TAG, "getScrollX: " + getScrollX());
				final int scrollX = getScrollX();
				// scroll to Right
				if (deltaX > 0)
				{
					if (scrollX > 0)
					{
						scrollBy(Math.max(-scrollX, -deltaX), 0);
					}
				}
				// scroll to Left
				else if (deltaX < 0)
				{
					if (getChildCount() > 0)
					{
						final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - getScrollX()
								- getWidth();
						if (availableToScroll > 0)
						{
							scrollBy(Math.min(availableToScroll, -deltaX), 0);
						}
					}
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING)
			{
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();

				// Fling hard enough to move left
				if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0)
				{
					snapToScreen(mCurrentScreen - 1);
				}
				// Fling hard enough to move right
				else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - 1)
				{
					snapToScreen(mCurrentScreen + 1);
				}
				else
				{
					snapToDestination();
				}

				if (mVelocityTracker != null)
				{
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}

	private void setMWidth()
	{
		if (mWidth == 0)
		{
			mWidth = getWidth();
		}
	}

	public void snapToScreen(int whichScreen)
	{
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

		// set current screen
		mCurrentScreen = whichScreen;
		Log.d(TAG, "snapToScreen: " + whichScreen);
		setMWidth();

		// Loop screen
		if (mIsLoopScreen)
		{
			int scrollX = getScrollX();
			int startWidth = whichScreen * mWidth;
			if (scrollX != startWidth)
			{
				int startX = 0;
				int delta = 0;

				// lastScreen - 1 , lastScreen = count - 1;
				if (whichScreen > this.getChildCount() - 2)
				{
					startX = (this.getChildCount() - 2)*mWidth - startWidth + scrollX;
					delta = startWidth - scrollX;
					setPre();
				}
				// firstScreen + 1 === 1, firstScreen = 0;
				else if (whichScreen < 1)
				{
					startX = scrollX + mWidth;
					delta = -scrollX;        
					setNext();
				}
				else
				{
					startX = scrollX;
					delta = startWidth - scrollX;
				}

				mScroller.startScroll(startX, 0, delta, 0);
				invalidate(); // Redraw the layout
			}
		}
		else
		{
			final int newX = whichScreen * getWidth();
			// final int newXX = getChildAt(whichScreen).getRight() -
			// getWidth();
			// Log.d(TAG, "newX: " + newX + "newXX: " + newXX);

			final int delta = newX - getScrollX();
			Log.d(TAG, "delta: " + delta);
			mScroller.startScroll(getScrollX(), 0, delta, 0);
			invalidate();
		}
	}

	/**
	 * take first view to last index
	 */
	private void setPre()
	{
		int count = getChildCount();
		View view = getChildAt(0);
		removeViewAt(0);
		addView(view, count - 1);
	}

	/**
	 * take last view to first index
	 */
	private void setNext()
	{
		int count = this.getChildCount();
		View view = getChildAt(count - 1);
		removeViewAt(count - 1);
		addView(view, 0);
	}

	private void snapToDestination()
	{
		final int screenWidth = getWidth();
		final int whichScreen = (getScrollX() + (screenWidth / 2)) / screenWidth;
		Log.d(TAG, "snapToDestination: " + whichScreen);
		snapToScreen(whichScreen);
	}
	
	public interface IndicateListener {
		/**
		 * On indicator change.
		 * 
		 * @param percent the percent
		 */
		public abstract void onIndicatorChange(float percent);

		/**
		 * On indicator full.
		 * 
		 * @param index the index
		 */
		public abstract void onIndicatorFull(int index);

		/**
		 * On indicator init.
		 * 
		 * @param total the total
		 */
		public abstract void onIndicatorInit(int total);
	}

	private static final String TAG = "ScrollView";
	private static final int SNAP_VELOCITY = 1000;

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;

	private Scroller mScroller;
	private float mLastMotionX;

	private VelocityTracker mVelocityTracker;
	private int mMaximumVelocity;

	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;

	private int mCurrentScreen;
	private int mWidth;
	private IndicateListener mIndicateListener;

	private boolean mIsLoopScreen;
	
	private boolean mIsAutoScroll = false;
	private Handler mHandler;
	private static final int KScrollScreen = 0x001;
	// frequency time 2s
	private static final int FREQUENCE = 2000;
}
