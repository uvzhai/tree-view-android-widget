package com.netbean.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.netbean.view.TreeNodeInfo;

import android.database.DataSetObserver;
import android.util.Log;

/**
 * In-memory manager of tree state.
 * 
 * @param <T> type of identifier
 */
public class InMemoryTreeStateManager<T> implements TreeStateManager<T> {
	private static final String TAG = InMemoryTreeStateManager.class.getSimpleName();
	private static final long serialVersionUID = 1L;
	// store all nodes
	private final Map<T, InMemoryTreeNode<T>> allNodes = new HashMap<T, InMemoryTreeNode<T>>();

	// level = -1 virtual root
	private final InMemoryTreeNode<T> topSentinel = new InMemoryTreeNode<T>(null, null, -1, true);
	
	private transient List<T> visibleListCache = null; // lazy initialized
	private transient List<T> unmodifiableVisibleList = null;
	
	private boolean visibleByDefault = true;
	// for observer data change
	private final transient Set<DataSetObserver> observers = new HashSet<DataSetObserver>();
	
	private synchronized void internalDataSetChanged()
	{
		// reset
		visibleListCache = null;
		unmodifiableVisibleList = null;
		if(null != observers)
		{
			for (final DataSetObserver observer : observers)
			{
				observer.onChanged();
			}
		}
	}

	/**
	 * If true new nodes are visible by default.
	 * 
	 * @param visibleByDefault if true, then newly added nodes are expanded by
	 *            default
	 */
	public void setVisibleByDefault(final boolean visibleByDefault)
	{
		this.visibleByDefault = visibleByDefault;
	}

	private InMemoryTreeNode<T> getNodeFromTreeOrThrow(final T id)
	{
		if (id == null)
		{
			throw new NodeNotInTreeException("(null)");
		}
		final InMemoryTreeNode<T> node = allNodes.get(id);
		if (node == null)
		{
			throw new NodeNotInTreeException(id.toString());
		}
		return node;
	}

	private InMemoryTreeNode<T> getNodeFromTreeOrThrowAllowRoot(final T id)
	{
		if (id == null)
		{
			return topSentinel;
		}
		return getNodeFromTreeOrThrow(id);
	}

	private void expectNodeNotInTreeYet(final T id)
	{
		final InMemoryTreeNode<T> node = allNodes.get(id);
		if (node != null)
		{
			throw new NodeAlreadyInTreeException(id.toString(), node.toString());
		}
	}

	@Override
	public synchronized TreeNodeInfo<T> getNodeInfo(final T id)
	{
		final InMemoryTreeNode<T> node = getNodeFromTreeOrThrow(id);
		final List<InMemoryTreeNode<T>> children = node.getChildren();
		boolean expanded = false;
		if (!children.isEmpty() && children.get(0).isVisible())
		{
			expanded = true;
		}
		return new TreeNodeInfo<T>(id, node.getLevel(), !children.isEmpty(), node.isVisible(), expanded);
	}

	@Override
	public synchronized List<T> getChildren(final T id)
	{
		final InMemoryTreeNode<T> node = getNodeFromTreeOrThrowAllowRoot(id);
		return node.getChildIdList();
	}

	@Override
	public synchronized T getParent(final T id)
	{
		final InMemoryTreeNode<T> node = getNodeFromTreeOrThrowAllowRoot(id);
		return node.getParent();
	}

	private boolean getChildrenVisibility(final InMemoryTreeNode<T> node)
	{
		boolean visibility;
		final List<InMemoryTreeNode<T>> children = node.getChildren();
		if (null == children || children.isEmpty())
		{
			visibility = visibleByDefault;
		}
		else
		{
			visibility = children.get(0).isVisible();
		}
		return visibility;
	}

	@Override
	public synchronized void addBeforeChild(final T parent, final T newChild, final T beforeChild)
	{
		expectNodeNotInTreeYet(newChild);
		final InMemoryTreeNode<T> node = getNodeFromTreeOrThrowAllowRoot(parent);
		final boolean visibility = getChildrenVisibility(node);
		// top nodes are always expanded.
		if (beforeChild == null)
		{
			final InMemoryTreeNode<T> added = node.add(0, newChild, visibility);
			allNodes.put(newChild, added);
		}
		else
		{
			final int index = node.indexOf(beforeChild);
			final InMemoryTreeNode<T> added = node.add(index == -1 ? 0 : index, newChild, visibility);
			allNodes.put(newChild, added);
		}
		if (visibility)
		{
			internalDataSetChanged();
		}
	}

	@Override
	public synchronized void addAfterChild(final T parent, final T newChild, final String label, final T afterChild)
	{
		expectNodeNotInTreeYet(newChild);
		
		// 1.find parent node
		final InMemoryTreeNode<T> node = getNodeFromTreeOrThrowAllowRoot(parent);
		final boolean visibility = getChildrenVisibility(node);
		// 2.add node to parent's children
		if (afterChild == null)
		{
			final InMemoryTreeNode<T> added = node.add(node.getChildrenListSize(), newChild, visibility);
			added.setLabel(label);
			allNodes.put(newChild, added);
		}
		else
		{
			final int index = node.indexOf(afterChild);
			final InMemoryTreeNode<T> added = node.add(index == -1 ? node.getChildrenListSize() : index + 1, newChild,
					visibility);
			added.setLabel(label);
			allNodes.put(newChild, added);
		}
		if (visibility)
		{
			internalDataSetChanged();
		}
	}

	@Override
	public synchronized void removeNodeRecursively(final T id)
	{
		// 1.remove children Node and Node self
		final InMemoryTreeNode<T> node = getNodeFromTreeOrThrowAllowRoot(id);
		final boolean visibleNodeChanged = removeNodeRecursively(node);
		
		// 2.remove self from parent Node
		final T parent = node.getParent();
		final InMemoryTreeNode<T> parentNode = getNodeFromTreeOrThrowAllowRoot(parent);
		parentNode.removeChild(id);
		if (visibleNodeChanged)
		{
			internalDataSetChanged();
		}
	}

	private boolean removeNodeRecursively(final InMemoryTreeNode<T> node)
	{
		boolean visibleNodeChanged = false;
		for (final InMemoryTreeNode<T> child : node.getChildren())
		{
			if (removeNodeRecursively(child))
			{
				visibleNodeChanged = true;
			}
		}
		node.clearChildren();
		if (node.getId() != null)
		{
			allNodes.remove(node.getId());
			if (node.isVisible())
			{
				visibleNodeChanged = true;
			}
		}
		return visibleNodeChanged;
	}

	private void setChildrenVisibility(final InMemoryTreeNode<T> node, final boolean visible, final boolean recursive)
	{
		for (final InMemoryTreeNode<T> child : node.getChildren())
		{
			child.setVisible(visible);
			if (recursive)
			{
				setChildrenVisibility(child, visible, true);
			}
		}
	}

	@Override
	public synchronized void expandDirectChildren(final T id)
	{
		Log.d(TAG, "Expanding direct children of " + id);
		final InMemoryTreeNode<T> node = getNodeFromTreeOrThrowAllowRoot(id);
		setChildrenVisibility(node, true, false);
		internalDataSetChanged();
	}

	@Override
	public synchronized void expandEverythingBelow(final T id)
	{
		Log.d(TAG, "Expanding all children below " + id);
		final InMemoryTreeNode<T> node = getNodeFromTreeOrThrowAllowRoot(id);
		setChildrenVisibility(node, true, true);
		internalDataSetChanged();
	}

	@Override
	public synchronized void collapseChildren(final T id)
	{
		final InMemoryTreeNode<T> node = getNodeFromTreeOrThrowAllowRoot(id);
		if (node == topSentinel)
		{
			for (final InMemoryTreeNode<T> cnode : topSentinel.getChildren())
			{
				setChildrenVisibility(cnode, false, true);
			}
		}
		else
		{
			setChildrenVisibility(node, false, true);
		}
		internalDataSetChanged();
	}

	@Override
	public synchronized T getNextSibling(final T id)
	{
		final T parent = getParent(id);
		final InMemoryTreeNode<T> parentNode = getNodeFromTreeOrThrowAllowRoot(parent);
		boolean returnNext = false;
		for (final InMemoryTreeNode<T> child : parentNode.getChildren())
		{
			// find and return next
			if (returnNext)
			{
				return child.getId();
			}
			if (child.getId().equals(id))
			{
				returnNext = true;
			}
		}
		return null;
	}

	@Override
	public synchronized T getPreviousSibling(final T id)
	{
		final T parent = getParent(id);
		final InMemoryTreeNode<T> parentNode = getNodeFromTreeOrThrowAllowRoot(parent);
		final T previousSibling = null;
		for (final InMemoryTreeNode<T> child : parentNode.getChildren())
		{
			if (child.getId().equals(id))
			{
				return previousSibling;
			}
		}
		return null;
	}

	@Override
	public synchronized boolean isInTree(final T id)
	{
		return allNodes.containsKey(id);
	}

	@Override
	public synchronized int getVisibleCount()
	{
		return getVisibleList().size();
	}
	
	@Override
	public synchronized List<T> getVisibleList()
	{
		if(visibleListCache == null)
		{
			visibleListCache = new ArrayList<T>();
			for(T nodeID : allNodes.keySet())
			{
				InMemoryTreeNode<T> node = allNodes.get(nodeID);
				if(node != null && node.isVisible())
				{
					visibleListCache.add(nodeID);
				}
			}
		}
		
		if(unmodifiableVisibleList == null)
		{
			unmodifiableVisibleList = Collections.unmodifiableList(visibleListCache);
		}
		return unmodifiableVisibleList;
	}

	public synchronized List<T> getVisibleList2()
	{
		T currentId = null;
		if (visibleListCache == null)
		{
			visibleListCache = new ArrayList<T>(allNodes.size());
			do
			{
				currentId = getNextVisible(currentId);
				if (currentId == null)
				{
					break;
				}
				else
				{
					visibleListCache.add(currentId);
				}
			}
			while (true);
		}
		
		if (unmodifiableVisibleList == null)
		{
			unmodifiableVisibleList = Collections.unmodifiableList(visibleListCache);
		}
		return unmodifiableVisibleList;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public synchronized T getNextVisible(final T id)
	{
		final InMemoryTreeNode<T> node = getNodeFromTreeOrThrowAllowRoot(id);
		if (!node.isVisible())
		{
			return null;
		}
		
		// 1. children
		final List<InMemoryTreeNode<T>> children = node.getChildren();
		if (!children.isEmpty())
		{
			final InMemoryTreeNode<T> firstChild = children.get(0);
			if (firstChild.isVisible())
			{
				return firstChild.getId();
			}
		}
		
		// 2. sibling
		final T sibl = getNextSibling(id);
		if (sibl != null)
		{
			return sibl;
		}
		
		// 3. parent
		T parent = node.getParent();
		do
		{
			if (parent == null)
			{
				return null;
			}
			final T parentSibling = getNextSibling(parent);
			if (parentSibling != null)
			{
				return parentSibling;
			}
			parent = getNodeFromTreeOrThrow(parent).getParent();
		}
		while (true);
	}

	@Override
	public synchronized void registerDataSetObserver(final DataSetObserver observer)
	{
		if(null != observers)
		{
			observers.add(observer);
		}
	}

	@Override
	public synchronized void unregisterDataSetObserver(final DataSetObserver observer)
	{
		if(null != observers)
		{
			observers.remove(observer);
		}
	}

	@Override
	public int getLevel(final T id)
	{
		return getNodeFromTreeOrThrow(id).getLevel();
	}

	@Override
	public Integer[] getHierarchyDescription(final T id)
	{
		final int level = getLevel(id);
		final Integer[] hierarchy = new Integer[level + 1];
		int currentLevel = level;
		T currentId = id;
		T parent = getParent(currentId);
		while (currentLevel >= 0)
		{
			hierarchy[currentLevel--] = getChildren(parent).indexOf(currentId);
			currentId = parent;
			parent = getParent(parent);
		}
		return hierarchy;
	}

	public String getLabel(final T id)
	{
		String result = null;
		// final TreeNodeInfo<T> node = getNodeInfo(id);
		final InMemoryTreeNode<T> node = allNodes.get(id);
		if (null != node)
		{
			result = node.getLabel();
		}
		return result;
	};

	private void appendToSb(final StringBuilder sb, final T id)
	{
		if (id != null)
		{
			final TreeNodeInfo<T> node = getNodeInfo(id);
			final int indent = node.getLevel() * 4;
			final char[] indentString = new char[indent];
			Arrays.fill(indentString, ' ');
			sb.append(indentString);
			sb.append(node.toString());
			sb.append(Arrays.asList(getHierarchyDescription(id)).toString());
			sb.append("\n");
		}
		final List<T> children = getChildren(id);
		for (final T child : children)
		{
			appendToSb(sb, child);
		}
	}

	@Override
	public synchronized String toString()
	{
		final StringBuilder sb = new StringBuilder();
		appendToSb(sb, null);
		return sb.toString();
	}

	@Override
	public synchronized void clear()
	{
		allNodes.clear();
		topSentinel.clearChildren();
		internalDataSetChanged();
	}

	@Override
	public void refresh()
	{
		internalDataSetChanged();
	}

}
