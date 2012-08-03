package com.netbean;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.netbean.scrollview.PageIndicator;
import com.netbean.scrollview.ScrollScreenLayout;
import com.netbean.scrollview.ScrollScreenLayout.IndicateListener;

public class ScrollViewDemo extends Activity implements IndicateListener {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screens);
		// init 
		mScrollView = (ScrollScreenLayout) findViewById(R.id.screens);
		addChild(R.drawable.froyo,0);
		addChild(R.drawable.gingerbread,1);
		addChild(R.drawable.honeycomb,2);
		addChild(R.drawable.icecream, 3);
		
		mIndicator = (PageIndicator) findViewById(R.id.indicatorview);
		mScrollView.setIndicator(this);
		mScrollView.setLoopScreen(true);
	}
	
	private void addChild(int nDrawableID, int nIndex)
	{
		ImageView image = new ImageView(this);
		image.setImageResource(nDrawableID);
		image.setTag(nIndex);
		mScrollView.addView(image);
	}

	@Override
	public void onIndicatorChange(float percent)
	{
		if (null != mIndicator)
		{
			int position = Math.round((mIndicator.getTotalItems() - 1) * percent);
			mIndicator.setCurrentItem(position);
		}
	}

	@Override
	public void onIndicatorFull(int index)
	{
		if (null != mIndicator)
		{
			mIndicator.setCurrentItem(index);
		}
	}

	@Override
	public void onIndicatorInit(int total)
	{
		if (total <= 1)
		{
			mIndicator.setVisibility(View.INVISIBLE);
		}
		else
		{
			mIndicator.setVisibility(View.VISIBLE);
		}
		mIndicator.setTotalItems(total);
	}

	private ScrollScreenLayout mScrollView;
	private PageIndicator mIndicator;
}
