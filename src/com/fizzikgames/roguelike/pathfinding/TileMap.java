package com.fizzikgames.roguelike.pathfinding;

/**
 * A tile map is a world that is stored in coordinates referred to as rows or columns,
 * and can even have a depth of traversable layers.
 * @author Maxim Tiourin
 * @version 1.00
 */
public interface TileMap {
	/**
	 * Returns whether or not the destination node is a valid move on the TileMap
	 * taking in consideration mover.
	 * @param mover The mover object to consider for.
	 * @param srcNode The starting node.
	 * @param dstNode The destination node.
	 * @return boolean of whether or not the destination node is a valid move.
	 */
	public boolean isWalkableTile(Mover mover, Node srcNode, Node dstNode);
	/**
	 * Returns the cost of the move on the TileMap taking in consideration the cost
	 * of the mover.
	 * @param mover The mover object to consider for/
	 * @param srcNode The starting node.
	 * @param dstNode The destination node.
	 * @return cost
	 */
	public int getCost(Mover mover, Node srcNode, Node dstNode);
	/**
	 * Returns the width (x or columns) of the TileMap
	 * @return width
	 */
	public int getWidth();
	/**
	 * Returns the height (y or rows) of the TileMap
	 * @return height
	 */
	public int getHeight();
	/**
	 * Returns the depth of the map (or how many z levels it has)<br>Maps with only
	 * 2 dimensions should return 1;
	 * @return depth of the map.
	 */
	public int getDepth();
}
