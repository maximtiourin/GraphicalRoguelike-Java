package com.fizzikgames.roguelike.pathfinding;

import java.util.ArrayList;

import com.fizzikgames.roguelike.util.BinaryHeap;

/**
 * The Astar pathfinder will implement the Astar algorithm to find the most ideal
 * path from a starting position to an ending position. Only one Astar pathfinder
 * needs to be created for every Map, and can then be used to calculate paths for
 * multiple movers.
 * @author Maxim Tiourin
 * @version 1.02
 */
public class Astar implements Runnable {
	private BinaryHeap<Node> openList;
	private ArrayList<Node> closedList;
	private AstarHeuristic heuristic;
	private TileMap map;
	private Node[][][] nodes;
	private int searchRange; //How far to search before giving up
	private boolean diagonal; //Whether or not the pathfinder can search diagonally (only horizontal)
	private boolean diagonalClip; //Whether or not to clip through corners if moving diagonally.
	private boolean vertical; //Whether or not the pathfinder can search up and down vertically
	
	/**
	 * Initializes the Astar pathfinder for a specific Tilemap and heuristic, with specific
	 * flags on whether to allow diagonal and vertical movement (in case of z levels). It also asks you for
	 * an open list size value so that it can immediately allocate that much memory for the Binary Heap instead
	 * of starting with a small value, and then having to reallocate constantly, so choose a value you think will
	 * be best suited for your map size.
	 * @param aHeuristic The heuristic to use when calculating the paths
	 * @param aMap The TileMap to use to look up movementCosts when calculating paths
	 * @param anOpenListSize The initial open list size, larger values save time if you expect lots of tiles to be searched
	 * @param aSearchRange The maximum amount of nodes to search before giving up, a value of 0 means no limit.
	 * @param allowDiagonal Whether or not to allow diagonal movement checks on the same z level
	 * @param allowDiagonalClip Whether or not to allow clipping through corners if diagonal movement is enabled.
	 * @param allowVertical Whether or not to allow vertical movement checks above and below 1 z level
	 */
	public Astar(AstarHeuristic aHeuristic, TileMap aMap, int anOpenListSize, int aSearchRange, boolean allowDiagonal, boolean allowDiagonalClip, boolean allowVertical) {
		openList = new BinaryHeap<Node>(anOpenListSize);
		closedList = new ArrayList<Node>();
		heuristic = aHeuristic;
		map = aMap;
		searchRange = aSearchRange;
		diagonal = allowDiagonal;
		diagonalClip = allowDiagonalClip;
		vertical = allowVertical;
		nodes = new Node[map.getHeight()][map.getWidth()][map.getDepth()];
		for (int d = 0; d < map.getDepth(); d++) {
			for (int r = 0; r < map.getHeight(); r++) {
				for (int c = 0; c < map.getWidth(); c++) {
					nodes[r][c][d] = new Node(null, c, r, d);
				}
			}
		}
	}
	
	@Override
	public void run() {
		
	}
	
	/**
	 * Finds and returns the optimal path from startNode to destNode if one exists,
	 * accounting for movement costs based on the mover's moveCosts and heuristic
	 * costs for the nodes.
	 * @param mover object that will help determine movement costs
	 * @param aStartNode starting node
	 * @param aDestNode destination node
	 * @return path
	 */
	public Path findPath(Mover mover, Node aStartNode, Node aDestNode) {
		Path path = new Path(); //Initialize path
		
		/**
		 * Reset data
		 */
		openList.clear();
		closedList.clear();
		for (int d = 0; d < map.getDepth(); d++) {
			for (int r = 0; r < map.getHeight(); r++) {
				for (int c = 0; c < map.getWidth(); c++) {
					nodes[r][c][d].reset();
				}
			}
		}
		
		Node destNode = nodes[aDestNode.getRow()][aDestNode.getColumn()][aDestNode.getDepth()];
		Node startNode = nodes[aStartNode.getRow()][aStartNode.getColumn()][aStartNode.getDepth()];
		boolean depth = startNode.hasDepth(); //Check if we are searching in 3 dimensions
		addToOpenList(startNode); //Add starting node to the open list
		
		//Check if destination is valid
		if (!(map.isWalkableTile(mover, startNode, destNode))) {
			return path;
		}
		
		/*
		 * Begin Search;
		 * Stop when destNode can't be reached or destNode was reached
		 */
		int search = 0;
		while (openList.getSize() > 0) {
			if (searchRange > 0 && search > searchRange) return path; //Exceeded search range, return empty path.
			
			Node currentNode = openList.pop(); //Remove lowest cost node, make it current node
			
			if (currentNode.equals(destNode)) break; //Break out of loop because destination was added to closed list
			
			addToClosedList(currentNode); //Add current node to closed list
			
			int x = currentNode.getX(), y = currentNode.getY(), z = currentNode.getZ();
			
			/* Check for nodes to add to the open list */
			Node checkNode = null;
			int ax, ay, az = 0;
			// Check Nodes in 8 cardinal directions (if diagonal flag), does not check vertical		
			for (ay = -1; ay <= 1; ay++) {
				for (ax = -1; ax <= 1; ax++) {
					//Check if current node
					if ((ax == 0) && (ay == 0)) {
						continue;
					}
					//Check if diagonal
					if (!diagonal) {
						if ((ax != 0) && (ay != 0)) {
							continue;
						}
					}
					
					//Else, evaluate the node
					if (withinBounds(x + ax, y + ay, z + az)) {
						checkNode = nodes[y + ay][x + ax][z + az];
						checkTheNode(mover, ax, ay, az, x, y, z, currentNode, checkNode, startNode, destNode);
					}
				}
			}
			// Check Nodes below and above for vertical movement (if vertical flag && nodes have depth)
			ax = 0;
			ay = 0;
			if (depth && vertical) {
				//Below
				az = -1;
				if (withinBounds(x + ax, y + ay, z + az)) {
					checkNode = nodes[y + ay][x + ax][z + az];
					checkTheNode(mover, ax, ay, az, x, y, z, currentNode, checkNode, startNode, destNode);
				}
				//Above
				az = 1;
				if (withinBounds(x + ax, y + ay, z + az)) {
					checkNode = nodes[y + ay][x + ax][z + az];
					checkTheNode(mover, ax, ay, az, x, y, z, currentNode, checkNode, startNode, destNode);
				}
			}
			
			search++;
		}
		
		//If we couldn't find a path return empty
		if (destNode.getParent() == null) return path;
		
		//Generate path
		Node currentNode = destNode;
		while(currentNode.getParent() != null) {
			path.addNode(currentNode);
			currentNode = currentNode.getParent();
		}
		path.addNode(currentNode); //Adds the starting position
		
		return path;
	}
	
	/**
	 * Checks the node and finds out what to do with it, used to clean up the findPath function
	 */
	private void checkTheNode(Mover mover, int ax, int ay, int az, int x, int y, int z, 
		Node currentNode, Node checkNode, Node startNode, Node destNode) {
		if (!shouldIgnoreNode(mover, currentNode, checkNode)) {
				int cost = currentNode.getGCost() + map.getCost(mover, currentNode, checkNode);
				
				//If new cost is lower then old cost on node being checked, discard it
				if (cost < checkNode.getGCost()) {
					if (checkNode.isOnOpenList()) {
						removeFromOpenList(checkNode);
					}
					if (checkNode.isOnClosedList()) {
						removeFromClosedList(checkNode);
					}
				}
				//If node hasn't been processed add it to open list
				if ((!checkNode.isOnOpenList()) && (!checkNode.isOnClosedList())) {
					checkNode.setGCost(cost);
					checkNode.setHCost(heuristic.getCost(map, mover, checkNode, destNode));
					checkNode.setParent(currentNode);
					addToOpenList(checkNode);
				}
		}
	}
	
	private boolean withinBounds(int x, int y, int z) {
		if (((y >= 0) && (y < map.getHeight()))
				&& (x >= 0) && (x < map.getWidth())
				&& (z >= 0) && (z < map.getDepth())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Add to the open list
	 * @param node node to add
	 */
	private void addToOpenList(Node node) {
		node.setOnOpenList(true);
		openList.add(node);
	}
	
	/**
	 * Removes from the open list
	 * @param node
	 */
	private void removeFromOpenList(Node node) {
		node.setOnOpenList(false);
		openList.remove(node);
	}
	
	/**
	 * Add to the closed list
	 * @param node node to add
	 */
	private void addToClosedList(Node node) {
		node.setOnClosedList(true);
		closedList.add(node);
	}
	
	/**
	 * Removes from the closed list
	 * @param node
	 */
	private void removeFromClosedList(Node node) {
		node.setOnClosedList(false);
		closedList.remove(node);
	}
	
	/**
	 * Returns whether or not this node should be excluded from the check
	 * @return whether or not node should be excluded
	 */
	private boolean shouldIgnoreNode(Mover mover, Node currentNode, Node checkNode) {
		if (!map.isWalkableTile(mover, currentNode, checkNode)) {
			return true;
		}
		else if (diagonal && !diagonalClip) {
			//If enabled diagonal movement, but clipping is disabled, check to see if we are about to clip.
			int r1 = currentNode.getRow(), c1 = currentNode.getColumn();
			int r2 = checkNode.getRow(), c2 = checkNode.getColumn();
			
			/* Corner Case TL -> BR
			 * 0#
			 * #1
			 */
			if (((r1 - r2) < 0) && ((c1 - c2) < 0)) {
				if (!map.isWalkableTile(mover, currentNode, new Node(null, currentNode.getX() + 1, currentNode.getY()))) {
					return true;
				}
				else if (!map.isWalkableTile(mover, currentNode, new Node(null, currentNode.getX(), currentNode.getY() + 1))) {
					return true;
				}
			}
			/* Corner Case BR -> TL
			 * 1#
			 * #0
			 */
			else if (((r1 - r2) > 0) && ((c1 - c2) > 0)) {
				if (!map.isWalkableTile(mover, currentNode, new Node(null, currentNode.getX() - 1, currentNode.getY()))) {
					return true;
				}
				else if (!map.isWalkableTile(mover, currentNode, new Node(null, currentNode.getX(), currentNode.getY() - 1))) {
					return true;
				}
			}
			/* Corner Case BL -> TR
			 * #1
			 * 0#
			 */
			else if (((r1 - r2) > 0) && ((c1 - c2) < 0)) {
				if (!map.isWalkableTile(mover, currentNode, new Node(null, currentNode.getX() + 1, currentNode.getY()))) {
					return true;
				}
				else if (!map.isWalkableTile(mover, currentNode, new Node(null, currentNode.getX(), currentNode.getY() - 1))) {
					return true;
				}
			}
			/* Corner Case TR -> BL
			 * #0
			 * 1#
			 */
			else if (((r1 - r2) < 0) && ((c1 - c2) > 0)) {
				if (!map.isWalkableTile(mover, currentNode, new Node(null, currentNode.getX() - 1, currentNode.getY()))) {
					return true;
				}
				else if (!map.isWalkableTile(mover, currentNode, new Node(null, currentNode.getX(), currentNode.getY() + 1))) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the type of Heuristic currently being used by the pathfinder.
	 * @return heuristic
	 */
	public AstarHeuristic getHeuristic() {
		return heuristic;
	}
	
	/**
	 * Sets the type of Heuristic to use by the pathfinder.
	 * @param h heuristic
	 */
	public void setHeuristic(AstarHeuristic h) {
		heuristic = h;
	}
	
	/**
	 * Returns whether or not the pathfinder is allowed to search diagonally on
	 * the same z-level.
	 * @return boolean of diagonal search permission
	 */
	public boolean isSearchingDiagonally() {
		return diagonal;
	}
	
	/**
	 * Set whether or not the pathfinder is allowed to search diagonally on the 
	 * same z-level.
	 * @param b boolean
	 */
	public void setSearchingDiagonally(boolean b) {
		diagonal = b;
	}
	
	/**
	 * Returns whether or not the pathfinder is allowed to search vertically 
	 * above and below 1 z-level. (Does not do diagonal/adjacent vertical checks)
	 * @return boolean of vertical search permission
	 */
	public boolean isSearchingVertically() {
		return vertical;
	}
	
	/**
	 * Set whether or not the pathfinder is allowed to search vetically above and
	 * below 1 z-level. (Does not do diagonal/adjacent vertical checks)
	 * @param b boolean
	 */
	public void setSearchingVertically(boolean b) {
		vertical = b;
	}
}
