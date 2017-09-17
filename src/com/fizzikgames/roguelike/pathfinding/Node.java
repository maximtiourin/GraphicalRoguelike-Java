package com.fizzikgames.roguelike.pathfinding;

/**
 * A node is a point in space that has its own coordinates, and can be used to
 * denote points in both 2d and 3d space.
 * @author Maxim Tiourin
 * @version 1.01
 */
public class Node implements Comparable<Node> {
	/**
	 * Creates a 2-dimensional node
	 * @param aParent parent node of this node, set to null if initial node
	 * @param ax x coordinate of this node
	 * @param ay y coordinate of this node
	 */
	public Node(Node aParent, int ax, int ay) {
		parent = aParent;
		closedList = false;
		openList = false;
		depth = false;
		x = ax;
		y = ay;
		z = 0;
		gCost = 0;
		hCost = 0;
		parent = null;
	}
	
	/**
	 * Creates a 3-dimensional node
	 * @param aParent parent node of this node, set to null if initial node
	 * @param ax x coordinate of this node
	 * @param ay y coordinate of this node
	 * @param az z coordinate of this node
	 */
	public Node(Node aParent, int ax, int ay, int az) {
		parent = aParent;
		closedList = false;
		openList = false;
		depth = true;
		x = ax;
		y = ay;
		z = az;
		gCost = 0;
		hCost = 0;
		parent = null;
	}
	
	@Override
	public boolean equals(Object e) {
	    if (e == this) return true; //Object Identity
	    
		if (e instanceof Node) {
			if (x == ((Node) e).getX()) {
				if (y == ((Node) e).getY()) {
					if (z == ((Node) e).getZ()) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
	    return (((x + y + z) / x) + ((x + y + z) / y) + ((x + y + z) / z)) * (x + y + z);
	}

	@Override
	public int compareTo(Node e) {
		if (getValue() < e.getValue()) {
			return -1;
		}
		else if (getValue() == e.getValue()) {
			return 0;
		}

		return 1;
	}

	/**
	 * Returns the combined movement cost and heuristic cost of the node.
	 * @return F cost
	 */
	public int getValue() {
		return gCost + hCost;
	}
	
	/**
	 * Returns the movement cost to get to this node.
	 * @return G cost
	 */
	public int getGCost() {
		return gCost;
	}
	
	/**
	 * Sets the movement cost to get to this node.
	 * @param g movement cost
	 */
	public void setGCost(int g) {
		gCost = g;
	}
	
	/**
	 * Returns the heuristic cost to get to the destination node from this node.
	 * @return H cost
	 */
	public int getHCost() {
		return hCost;
	}
	
	/**
	 * Sets the heuristic cost to get to the destination node from this node.
	 * @param h movement cost
	 */
	public void setHCost(int h) {
		hCost = h;
	}
	
	/**
	 * Gets the node's x coordinate
	 * @return x coordinate
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Sets the node's x coordinate
	 * @param ax coordinate to set to
	 */
	public void setX(int ax) {
		x = ax;
	}
	
	/**
	 * Gets the node's y coordinate
	 * @return y coordinate
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Sets the node's y coordinate
	 * @param ay coordinate to set to
	 */
	public void setY(int ay) {
		y = ay;
	}
	
	/**
	 * Gets the node's z coordinate
	 * @return z coordinate
	 */
	public int getZ() {
		return z;
	}
	
	/**
	 * Sets the node's z coordinate
	 * @param az coordinate to set to
	 */
	public void setZ(int az) {
		z = az;
	}
	
	/**
	 * Gets the node's row
	 * @return row
	 */
	public int getRow() {
		return y;
	}
	
	/**
	 * Sets the node's row
	 * @param row
	 */
	public void setRow(int row) {
		y = row;
	}
	
	/**
	 * Gets the node's column
	 * @return column
	 */
	public int getColumn() {
		return x;
	}
	
	/**
	 * Sets the node's column
	 * @param column
	 */
	public void setColumn(int column) {
		x = column;
	}
	
	/**
	 * Gets the node's depth
	 * @return depth
	 */
	public int getDepth() {
		return z;
	}
	
	/**
	 * Sets the node's depth
	 * @param aDepth coordinate to set to
	 */
	public void setDepth(int aDepth) {
		z = aDepth;
	}
	
	/**
	 * Checks if the node has a third dimension
	 * @return depth
	 */
	public boolean hasDepth() {
		return depth;
	}
	
	/**
	 * Returns the parent node of this node.
	 * @return parent
	 */
	public Node getParent() {
		return parent;
	}
	
	/**
	 * Sets the parent node of this node
	 * @param p parent node
	 */
	public void setParent(Node p) {
		parent = p;
	}
	
	/**
	 * Returns whether the node is on the open list
	 * @return boolean
	 */
	public boolean isOnOpenList() {
		return openList;
	}
	
	/**
	 * Sets whether the node is on the open list
	 * @param b boolean
	 */
	public void setOnOpenList(boolean b) {
		openList = b;
	}
	
	/**
	 * Returns whether the node is on the closed list
	 * @return boolean
	 */
	public boolean isOnClosedList() {
		return closedList;
	}
	
	/**
	 * Sets whether the node is on the closed list
	 * @param b boolean
	 */
	public void setOnClosedList(boolean b) {
		closedList = b;
	}
	
	/**
	 * Resets the node to default values
	 */
	public void reset() {
		parent = null;
		openList = false;
		closedList = false;
		gCost = 0;
		hCost = 0;
	}
	
	private Node parent;
	private boolean closedList;
	private boolean openList;
	private int x;
	private int y;
	private int z;
	private int gCost;
	private int hCost;
	private boolean depth; //Whether the node is 3dimensional or not
}
