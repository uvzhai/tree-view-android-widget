package com.netbean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.netbean.datasource.ITreeParser;
import com.netbean.datasource.TreeBuilder;
import com.netbean.datasource.TreeParser4Json;
import com.netbean.model.InMemoryTreeStateManager;
import com.netbean.model.TreeStateManager;
import com.netbean.view.TreeNodeInfo;
import com.netbean.view.TreeViewList;

/**
 * Demo activity showing how the tree view can be used.
 * 
 */
public class TreeViewListDemo extends Activity implements OnItemClickListener {
	private enum TreeType implements Serializable {
		SIMPLE, FANCY
	}

	private static final String TAG = TreeViewListDemo.class.getSimpleName();
	private static final int LEVEL_NUMBER = 4;

	// node level
	// private static final int[] DEMO_NODES = new int[] {0, 0, 1, 1, 1, 0, 1,
	// 2, 2, 2, 1, 1};
	// node label
	// private static final String[] NODES_LABEL = new
	// String[]{"profile","xuexiao","xiaoxue","zhongxue","daxue","chengshi","shanghai","minhang","xuhui","changning","hk","taibei"};

	private TreeViewList treeView;
	private TreeStateManager<Long> manager = null;
	private SimpleStandardAdapter simpleAdapter;
	private TreeType treeType;
	private boolean collapsible;
	private final Set<Long> selected = new HashSet<Long>();

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		TreeType newTreeType = null;
		boolean newCollapsible = true;
		if (savedInstanceState == null)
		{
			//ITreeParser treeParser = new TreeParser4Xml();
			ITreeParser treeParser = new TreeParser4Json();
			try
			{
				List<TreeNodeInfo<?>> result = null;
				// result =
				// treeParser.parse(this.getAssets().open("tree_json.json"));
				result = treeParser.parse(this.getResources().openRawResource(R.raw.tree_json));
				// result =
				// treeParser.parse(this.getResources().openRawResource(R.raw.tree));
				if (null != result)
				{
					manager = new InMemoryTreeStateManager<Long>();
					final TreeBuilder<Long> treeBuilder = new TreeBuilder<Long>(manager);
					for (int i = 0; i < result.size(); i++)
					{
						TreeNodeInfo<?> nodeInfo = result.get(i);
						treeBuilder.sequentiallyAddNextNode((long) i, nodeInfo.getLabel(), nodeInfo.getLevel());
					}
					newTreeType = TreeType.SIMPLE;
					newCollapsible = true;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			manager = (TreeStateManager<Long>) savedInstanceState.getSerializable("treeManager");
			newTreeType = (TreeType) savedInstanceState.getSerializable("treeType");
			newCollapsible = savedInstanceState.getBoolean("collapsible");
		}

		setContentView(R.layout.main_demo);
		
		simpleAdapter = new SimpleStandardAdapter(this, selected, manager, LEVEL_NUMBER);
		
		treeView = (TreeViewList) findViewById(R.id.mainTreeView);
		treeView.setOnItemClickListener(this);
		// set list view adapter
		setTreeAdapter(newTreeType);
		
		setCollapsible(newCollapsible);
		registerForContextMenu(treeView);
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState)
	{
		outState.putSerializable("treeManager", manager);
		outState.putSerializable("treeType", treeType);
		outState.putBoolean("collapsible", this.collapsible);
		super.onSaveInstanceState(outState);
	}

	private final void setTreeAdapter(final TreeType newTreeType)
	{
		this.treeType = newTreeType;
		switch (newTreeType)
		{
		case SIMPLE:
			treeView.setAdapter(simpleAdapter);
			break;
		case FANCY:
		default:
			treeView.setAdapter(simpleAdapter);
		}
	}

	protected final void setCollapsible(final boolean newCollapsible)
	{
		this.collapsible = newCollapsible;
		treeView.setCollapsible(this.collapsible);
	}

	// @Override
	// public boolean onCreateOptionsMenu(final Menu menu) {
	// final MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.main_menu, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onPrepareOptionsMenu(final Menu menu) {
	// final MenuItem collapsibleMenu = menu
	// .findItem(R.id.collapsible_menu_item);
	// if (collapsible) {
	// collapsibleMenu.setTitle(R.string.collapsible_menu_disable);
	// collapsibleMenu.setTitleCondensed(getResources().getString(
	// R.string.collapsible_condensed_disable));
	// } else {
	// collapsibleMenu.setTitle(R.string.collapsible_menu_enable);
	// collapsibleMenu.setTitleCondensed(getResources().getString(
	// R.string.collapsible_condensed_enable));
	// }
	// return super.onPrepareOptionsMenu(menu);
	// }

	// @Override
	// public boolean onOptionsItemSelected(final MenuItem item) {
	// if (item.getItemId() == R.id.simple_menu_item) {
	// setTreeAdapter(TreeType.SIMPLE);
	// } else if (item.getItemId() == R.id.fancy_menu_item) {
	// setTreeAdapter(TreeType.FANCY);
	// } else if (item.getItemId() == R.id.collapsible_menu_item) {
	// setCollapsible(!this.collapsible);
	// } else if (item.getItemId() == R.id.expand_all_menu_item) {
	// manager.expandEverythingBelow(null);
	// } else if (item.getItemId() == R.id.collapse_all_menu_item) {
	// manager.collapseChildren(null);
	// } else {
	// return false;
	// }
	// return true;
	// }

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo)
	{
		final AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) menuInfo;
		// row id of the item
		final long id = adapterInfo.id;
		final TreeNodeInfo<Long> info = manager.getNodeInfo(id);
		final MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.context_menu, menu);
		if (info.isWithChildren())
		{
			if (info.isExpanded())
			{
				menu.findItem(R.id.context_menu_expand_item).setVisible(false);
				menu.findItem(R.id.context_menu_expand_all).setVisible(false);
			}
			else
			{
				menu.findItem(R.id.context_menu_collapse).setVisible(false);
			}
		}
		else
		{
			menu.findItem(R.id.context_menu_expand_item).setVisible(false);
			menu.findItem(R.id.context_menu_expand_all).setVisible(false);
			menu.findItem(R.id.context_menu_collapse).setVisible(false);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item)
	{
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final long id = info.id;
		if (item.getItemId() == R.id.context_menu_collapse)
		{
			manager.collapseChildren(id);
			return true;
		}
		else if (item.getItemId() == R.id.context_menu_expand_all)
		{
			manager.expandEverythingBelow(id);
			return true;
		}
		else if (item.getItemId() == R.id.context_menu_expand_item)
		{
			manager.expandDirectChildren(id);
			return true;
		}
		else if (item.getItemId() == R.id.context_menu_delete)
		{
			manager.removeNodeRecursively(id);
			return true;
		}
		else
		{
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// business logic
		if(0 == position)
		{
			Intent pIntent = new Intent(this, ScrollViewDemo.class);
			startActivity(pIntent);
		}
		else
		{
			// view.getTag()
			simpleAdapter.handleItemClick(view, view.getTag());
		}
		
	}
}