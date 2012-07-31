/**
 * Copyright (C) 2011 Tencent Inc. All rights reserved, for internal usage only.
 * 
 * Project: Soso_Client FileName: PageIndicator.java
 * 
 * Description: Author: oopsli (oopsli@tencent.com) Created: 2011-9-20
 */
package com.netbean.scrollview;

import com.netbean.R;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PageIndicator extends ViewGroup {
	private int mTotalItems = 0;
	private int mCurrentItem = 0;
	private int mDotDrawableId;

	public PageIndicator(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initPager();
	}

	public PageIndicator(Context context)
	{
		super(context);
		initPager();
	}

	private void initPager()
	{
		setFocusable(false);
		setWillNotDraw(false);
		mDotDrawableId = R.drawable.page_indicator;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		if (mTotalItems <= 0)
			return;
		createLayout();
	}

	private void updateLayout()
	{
		for (int i = 0; i < getChildCount(); i++)
		{
			final ImageView img = (ImageView) getChildAt(i);
			TransitionDrawable tmp = (TransitionDrawable) img.getDrawable();
			if (i == mCurrentItem)
			{
				tmp.startTransition(50);
			}
			else
			{
				tmp.resetTransition();
			}
		}
	}

	private void createLayout()
	{
		detachAllViewsFromParent();

		int dotWidth = getResources().getDrawable(mDotDrawableId).getIntrinsicWidth();
		int separation = dotWidth;
		int marginLeft = ((getWidth()) / 2) - (((mTotalItems * dotWidth) / 2) + (((mTotalItems - 1) * separation) / 2));
		int marginTop = ((getHeight()) / 2) - (dotWidth / 2);
		for (int i = 0; i < mTotalItems; i++)
		{

			ImageView dot = new ImageView(getContext());
			TransitionDrawable td;
			td = (TransitionDrawable) getResources().getDrawable(mDotDrawableId);
			td.setCrossFadeEnabled(true);
			dot.setImageDrawable(td);

			ViewGroup.LayoutParams p;
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
			dot.setLayoutParams(p);

			int childHeightSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(dotWidth, MeasureSpec.UNSPECIFIED),
					0, p.height);
			int childWidthSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(dotWidth, MeasureSpec.EXACTLY), 0,
					p.width);
			dot.measure(childWidthSpec, childHeightSpec);

			int left = marginLeft + (i * (dotWidth + separation));

			dot.layout(left, marginTop, left + dotWidth, marginTop + dotWidth);
			addViewInLayout(dot, getChildCount(), p, true);

			if (i == mCurrentItem)
			{
				TransitionDrawable tmp = (TransitionDrawable) dot.getDrawable();
				tmp.startTransition(200);
			}
		}
		postInvalidate();
	}

	public int getTotalItems()
	{
		return mTotalItems;
	}

	public void setTotalItems(int totalItems)
	{
		if (totalItems != mTotalItems)
		{
			this.mTotalItems = totalItems;
			createLayout();
		}
	}

	public int getCurrentItem()
	{
		return mCurrentItem;
	}

	public void setCurrentItem(int currentItem)
	{
		if (currentItem != mCurrentItem)
		{
			this.mCurrentItem = currentItem;
			updateLayout();
		}
	}
}
