package com.fizzikgames.roguelike.pathfinding;

import java.util.Stack;

/**
 * A path is a simple stack of nodes that allows nodes to be added 
 * and retrieved in the most useful manner.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class Path {
	/**
	 * Initializes the path by creating a new stack of nodes.
	 */
	public Path() {
		nodes = new Stack<Node>();
	}
	
	/**
	 * Adds a new node to the path by pushing it on to the stack.
	 * @param node
	 */
	public void addNode(Node node) {
		nodes.push(node);
	}
	
	/**
	 * Retrieves the next node in the path by popping it off of the stack.
	 * @return Node node
	 */
	public Node getNextNode() {
		return nodes.pop();
	}
	
	/**
	 * Peeks at the node at the bottom of the stack, which is the destination node of the path.
	 * @return Node node
	 */
	public Node peekDestNode() {
		return nodes.get(nodes.size() - 1);
	}
	
	/**
	 * Returns the size of the path
	 * @return int size
	 */
	public int getSize() {
		return nodes.size();
	}
	
	/**
	 * Clears the path of all nodes
	 */
	public void clear() {
		nodes.clear();
	}
	
	/**
	 * Returns if the path is empty
	 * @return boolean is empty.
	 */
	public boolean isEmpty() {
		return getSize() == 0;
	}
	
	private Stack<Node> nodes;
}
