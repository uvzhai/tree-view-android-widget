package com.netbean.scrollview;

import com.netbean.R;

import android.content.Context;
import android.view.ViewGroup;

public class PageIndicator2 extends ViewGroup {

	public PageIndicator2(Context context)
	{
		super(context);
		init();
	}
	
	private void init()
	{
		mDotDrawableID = R.drawable.page_indicator;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{

	}
	
	private int mDotDrawableID;
	private int 	 mCurrentItem = 0;
	private int 	 mTotalItems = 0;
	

}
