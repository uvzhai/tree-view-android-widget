package com.netbean;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.netbean.scrollview.PageIndicator2;
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
		addChild(R.drawable.froyo);
		addChild(R.drawable.gingerbread);
		addChild(R.drawable.honeycomb);
		
		mIndicator = (PageIndicator2) findViewById(R.id.indicatorview);
		mScrollView.setIndicator(this);
	}
	
	private void addChild(int nDrawableID)
	{
		ImageView image = new ImageView(this);
		image.setImageResource(nDrawableID);
		mScrollView.addView(image);
	}

	@Override
	public void onIndicatorChange(float percent)
	{
		// TODO Auto-generated method stub

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
	private PageIndicator2 mIndicator;
}
