package com.fizzikgames.roguelike.pathfinding;

/**
 * A mover is any object that will be moving with the help of path-finding. It will
 * have its own information on which moves are valid and how much they cost and will
 * supply this information to pathfinders to help determine optimal paths.
 * @author Maxim Tiourin
 * @version 1.00
 */
public interface Mover {
	/**
	 * Returns whether or not the move is valid for this mover.
	 * @param srcNode The starting node.
	 * @param dstNode The destination node.
	 * @return boolean of whether or not the move is valid.
	 */
	public boolean isValidNode(Node srcNode, Node dstNode);
	/**
	 * Returns the move cost for this mover to the destination node.
	 * @param srcNode The starting node.
	 * @param dstNode The destination node.
	 * @return The move cost of the mover to the destination node.
	 */
	public int getMoveCost(Node srcNode, Node dstNode);
}
