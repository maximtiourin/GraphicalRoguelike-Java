package com.fizzikgames.roguelike.pathfinding;

public class ManhattanHeuristic implements AstarHeuristic {
	@Override
	public int getCost(TileMap map, Mover mover, Node srcNode, Node dstNode) {
		int cost = 0, sx, sy, dx, dy, xdist, ydist;
		dx = dstNode.getX();
		dy = dstNode.getY();
		sx = srcNode.getX();
		sy = srcNode.getY();
		xdist = Math.abs(sx - dx);
		ydist = Math.abs(sy - dy);
		cost = MOVECOST * ((xdist) + (ydist));
		return cost;
	}

	private final int MOVECOST = 10;
}
