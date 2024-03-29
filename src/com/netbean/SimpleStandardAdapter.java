package com.netbean;

import java.util.Set;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netbean.model.TreeStateManager;
import com.netbean.view.TreeNodeInfo;

/**
 * This is a very simple adapter that provides very basic tree view with a
 * checkboxes and simple item description.
 * 
 */
class SimpleStandardAdapter extends com.netbean.model.AbstractTreeViewAdapter<Long> {

	private final Set<Long> selected;

	private final OnCheckedChangeListener onCheckedChange = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
		{
			final Long id = (Long) buttonView.getTag();
			changeSelected(isChecked, id);
		}
	};

	private void changeSelected(final boolean isChecked, final Long id)
	{
		if (isChecked)
		{
			selected.add(id);
		}
		else
		{
			selected.remove(id);
		}
	}

	public SimpleStandardAdapter(final TreeViewListDemo treeViewListDemo, final Set<Long> selected,
			final TreeStateManager<Long> treeStateManager, final int numberOfLevels)
	{
		super(treeViewListDemo, treeStateManager, numberOfLevels);
		this.selected = selected;
	}

	// private String getDescription(final long id)
	// {
	// final Integer[] hierarchy = getManager().getHierarchyDescription(id);
	// return "Node " + id + Arrays.asList(hierarchy);
	// }

	private String getLabel(final long id)
	{
		String label = getManager().getLabel(id);
		return label;
	}

	@Override
	public View getNewChildView(final TreeNodeInfo<Long> treeNodeInfo)
	{
		final LinearLayout viewLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(
				R.layout.demo_list_item, null);
		return updateView(viewLayout, treeNodeInfo);
	}

	@Override
	public LinearLayout updateView(final View view, final TreeNodeInfo<Long> treeNodeInfo)
	{
		final LinearLayout viewLayout = (LinearLayout) view;
		
		final ImageView icon = (ImageView) viewLayout.findViewById(R.id.node_icon);
		int resID = 0;
		if(treeNodeInfo.isWithChildren())
		{
			if(treeNodeInfo.isExpanded())
			{
				resID = R.drawable.parent_expand;
			}
			else
			{
				resID = R.drawable.parent_collapse;
			}
		}
		else
		{
			resID = R.drawable.child;
		}
		icon.setImageResource(resID);

		final TextView descriptionView = (TextView) viewLayout.findViewById(R.id.demo_list_item_description);
		descriptionView.setText(getLabel(treeNodeInfo.getId()));

		final TextView levelView = (TextView) viewLayout.findViewById(R.id.demo_list_item_level);
		levelView.setText(Integer.toString(treeNodeInfo.getLevel()));

		final CheckBox box = (CheckBox) viewLayout.findViewById(R.id.demo_list_checkbox);

		box.setTag(treeNodeInfo.getId());

		if (treeNodeInfo.isWithChildren())
		{
			box.setVisibility(View.GONE);
		}
		else
		{
			box.setVisibility(View.VISIBLE);
			box.setChecked(selected.contains(treeNodeInfo.getId()));
		}
		box.setOnCheckedChangeListener(onCheckedChange);
		return viewLayout;
	}

	@Override
	public void handleItemClick(final View view, final Object id)
	{
		//Node ID
		final Long longId = (Long) id;
		final TreeNodeInfo<Long> info = getManager().getNodeInfo(longId);
		if (info.isWithChildren())
		{
			super.handleItemClick(view, id);
		}
		else
		{
			final ViewGroup vg = (ViewGroup) view;
			final CheckBox cb = (CheckBox) vg.findViewById(R.id.demo_list_checkbox);
			cb.performClick();
		}
	}

	@Override
	public long getItemId(final int position)
	{
		return getTreeId(position);
	}
}