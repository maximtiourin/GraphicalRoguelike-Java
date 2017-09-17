package com.fizzikgames.roguelike.pathfinding;

/**
 * Calculates the heuristic cost of a node for the Astar algorithm
 * @author Maxim Tiourin
 * @version 1.00
 */
public interface AstarHeuristic {
	/**
	 * Returns the heuristic cost of a node for a TileMap taking in consideration
	 * the movement costs of the mover. 
	 * @param map The map to calculate costs for.
	 * @param mover The mover to consider for.
	 * @param evalNode The node that is being evaluated.
	 * @param destNode The node that is trying to be reached.
	 * @return The heuristic cost of the evaluation node.
	 */
	public int getCost(TileMap map, Mover mover, Node evalNode, Node destNode);
}
