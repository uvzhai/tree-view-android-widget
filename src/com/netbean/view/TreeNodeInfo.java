package com.netbean.view;

/**
 * Information about the node.
 * For UI use.
 * 
 * @param <T>
 *            type of the id for the tree
 */
public class TreeNodeInfo<T> {
    private  T id;
    private  int level;
    private  String label;
    private  boolean withChildren;
    private  boolean visible;
    private  boolean expanded;
    
    public TreeNodeInfo(int level, String label)
	{
		super();
		this.level = level;
		this.setLabel(label);
	}

	/**
     * Creates the node information.
     * 
     * @param id
     *            id of the node
     * @param level
     *            level of the node
     * @param withChildren
     *            whether the node has children.
     * @param visible
     *            whether the tree node is visible.
     * @param expanded
     *            whether the tree node is expanded
     * 
     */
    public TreeNodeInfo(final T id, final int level,
            final boolean withChildren, final boolean visible,
            final boolean expanded) {
        super();
        this.id = id;
        this.level = level;
        this.withChildren = withChildren;
        this.visible = visible;
        this.expanded = expanded;
    }

    public T getId() {
        return id;
    }

    public boolean isWithChildren() {
        return withChildren;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "TreeNodeInfo [id=" + id + ", level=" + level
                + ", withChildren=" + withChildren + ", visible=" + visible
                + ", expanded=" + expanded + "]";
    }

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

}