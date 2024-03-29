package com.netbean.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.netbean.R;
import com.netbean.model.AbstractTreeViewAdapter;
import com.netbean.model.TreeConfigurationException;

/**
 * Tree view, expandable multi-level.
 * 
 * <pre>
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_collapsible
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_src_expanded
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_src_collapsed
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_indent_width
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_handle_trackball_press
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_indicator_gravity
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_indicator_background
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_row_background
 * </pre>
 */
public class TreeViewList extends ListView {
	
	private static final int DEFAULT_COLLAPSED_RESOURCE = R.drawable.collapsed;
	private static final int DEFAULT_EXPANDED_RESOURCE = R.drawable.expanded;
	private static final int DEFAULT_INDENT = 0;
	private static final int DEFAULT_GRAVITY = Gravity.LEFT | Gravity.CENTER_VERTICAL;
	
	// UI params
	private Drawable expandedDrawable;
	private Drawable collapsedDrawable;
	private Drawable rowBackgroundDrawable;
	private Drawable indicatorBackgroundDrawable;
	private int indentWidth = 0;
	private int indicatorGravity = 0;
	private boolean collapsible;
	
	// list view adapter
	private AbstractTreeViewAdapter<?> treeAdapter;

	public TreeViewList(final Context context, final AttributeSet attrs)
	{
		this(context, attrs, R.style.treeViewListStyle);
	}

	public TreeViewList(final Context context)
	{
		this(context, null);
	}

	public TreeViewList(final Context context, final AttributeSet attrs, final int defStyle)
	{
		super(context, attrs, defStyle);
		parseAttributes(context, attrs);
	}

	private void parseAttributes(final Context context, final AttributeSet attrs)
	{
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TreeViewList);
		expandedDrawable = a.getDrawable(R.styleable.TreeViewList_src_expanded);
		if (expandedDrawable == null)
		{
			expandedDrawable = context.getResources().getDrawable(DEFAULT_EXPANDED_RESOURCE);
		}
		collapsedDrawable = a.getDrawable(R.styleable.TreeViewList_src_collapsed);
		if (collapsedDrawable == null)
		{
			collapsedDrawable = context.getResources().getDrawable(DEFAULT_COLLAPSED_RESOURCE);
		}
		indentWidth = a.getDimensionPixelSize(R.styleable.TreeViewList_indent_width, DEFAULT_INDENT);
		indicatorGravity = a.getInteger(R.styleable.TreeViewList_indicator_gravity, DEFAULT_GRAVITY);
		indicatorBackgroundDrawable = a.getDrawable(R.styleable.TreeViewList_indicator_background);
		rowBackgroundDrawable = a.getDrawable(R.styleable.TreeViewList_row_background);

		if (null == rowBackgroundDrawable)
		{
			rowBackgroundDrawable = context.getResources().getDrawable(R.drawable.categoryitem_bg);
		}

		collapsible = a.getBoolean(R.styleable.TreeViewList_collapsible, true);
	}

	@Override
	public void setAdapter(final ListAdapter adapter)
	{
		if (!(adapter instanceof AbstractTreeViewAdapter))
		{
			throw new TreeConfigurationException("The adapter is not of TreeViewAdapter type");
		}
		treeAdapter = (AbstractTreeViewAdapter<?>) adapter;
		syncAdapter();
		super.setAdapter(treeAdapter);
	}

	private void syncAdapter()
	{
		treeAdapter.setCollapsedDrawable(collapsedDrawable);
		treeAdapter.setExpandedDrawable(expandedDrawable);
		treeAdapter.setIndicatorGravity(indicatorGravity);
		treeAdapter.setIndentWidth(indentWidth);
		treeAdapter.setCollapsible(collapsible);
	}

	public void setExpandedDrawable(final Drawable expandedDrawable)
	{
		this.expandedDrawable = expandedDrawable;
		syncAdapter();
		treeAdapter.refresh();
	}

	public void setCollapsedDrawable(final Drawable collapsedDrawable)
	{
		this.collapsedDrawable = collapsedDrawable;
		syncAdapter();
		treeAdapter.refresh();
	}

	public void setRowBackgroundDrawable(final Drawable rowBackgroundDrawable)
	{
		this.rowBackgroundDrawable = rowBackgroundDrawable;
		syncAdapter();
		treeAdapter.refresh();
	}

	public void setIndicatorBackgroundDrawable(final Drawable indicatorBackgroundDrawable)
	{
		this.indicatorBackgroundDrawable = indicatorBackgroundDrawable;
		syncAdapter();
		treeAdapter.refresh();
	}

	public void setIndentWidth(final int indentWidth)
	{
		this.indentWidth = indentWidth;
		syncAdapter();
		treeAdapter.refresh();
	}

	public void setIndicatorGravity(final int indicatorGravity)
	{
		this.indicatorGravity = indicatorGravity;
		syncAdapter();
		treeAdapter.refresh();
	}

	public void setCollapsible(final boolean collapsible)
	{
		this.collapsible = collapsible;
		syncAdapter();
		treeAdapter.refresh();
	}

	public Drawable getExpandedDrawable()
	{
		return expandedDrawable;
	}

	public Drawable getCollapsedDrawable()
	{
		return collapsedDrawable;
	}

	public Drawable getRowBackgroundDrawable()
	{
		return rowBackgroundDrawable;
	}

	public Drawable getIndicatorBackgroundDrawable()
	{
		return indicatorBackgroundDrawable;
	}

	public int getIndentWidth()
	{
		return indentWidth;
	}

	public int getIndicatorGravity()
	{
		return indicatorGravity;
	}

	public boolean isCollapsible()
	{
		return collapsible;
	}

}
