package com.netbean.scrollview;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.netbean.R;

public class PageIndicator2 extends ViewGroup {

	public PageIndicator2(Context context)
	{
		super(context);
		init();
	}
	
	public PageIndicator2(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public void setTotalItems(int nTotalItems)
	{
		if (nTotalItems != mTotalItems)
		{
			this.mTotalItems = nTotalItems;
			createLayout();
		}
	}

	public int getTotalItems()
	{
		return mTotalItems;
	}
	
	public void setCurrentItem(int nCurrentItem)
	{
		if(nCurrentItem != mCurrentItem)
		{
			this.mCurrentItem = nCurrentItem;
			updateLayout();
		}
	}
	
	public int getCurrentItem()
	{
		return mCurrentItem;
	}
	
	private void updateLayout()
	{
		for(int idx =0; idx < getChildCount(); idx++)
		{
			ImageView dot = (ImageView) this.getChildAt(idx);
			TransitionDrawable drawable = (TransitionDrawable) dot.getDrawable();
			if(idx == mCurrentItem)
			{
				drawable.startTransition(50);
			}
			else
			{
				drawable.resetTransition();
			}
		}
	}

	private void createLayout()
	{
		detachAllViewsFromParent();

		// 1.dot width,separation
		int dotWidth = this.getResources().getDrawable(mDotDrawableID).getIntrinsicWidth();
		int separation = dotWidth;

		// 2.left,top
		int marginLeft = getWidth() / 2 - (mTotalItems * dotWidth) / 2 - ((mTotalItems - 1) * separation) / 2;
		int marginTop = getHeight() / 2 - dotWidth / 2;

		// 3.layout
		for (int idx = 0; idx < mTotalItems; idx++)
		{
			ImageView dot = new ImageView(getContext());
			TransitionDrawable drawable = (TransitionDrawable) this.getResources().getDrawable(mDotDrawableID);
			drawable.setCrossFadeEnabled(true);
			dot.setImageDrawable(drawable);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			dot.setLayoutParams(params);

			int left = marginLeft + idx * (dotWidth + separation);
			dot.layout(left, marginTop, left + dotWidth, marginTop + dotWidth);
			
			addViewInLayout(dot, getChildCount(), params, true);
			
			if(idx == mCurrentItem)
			{
				drawable.startTransition(200);
			}
//			else
//			{
//				drawable.resetTransition();
//			}
		}
		
		postInvalidate();
	}

	private void init()
	{
		mDotDrawableID = R.drawable.page_indicator;
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
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		if(mTotalItems <= 0)
		{
			return;
		}
		createLayout();
	}
	
	private int mDotDrawableID;
	private int mCurrentItem = 0;
	private int mTotalItems = 0;
}
