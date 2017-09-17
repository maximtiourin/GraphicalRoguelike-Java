package com.fizzikgames.roguelike.entity.mechanics;

/**
 * A modifier takes a double value as input, and outputs a modified value. Can also be
 * sorted by it's priority where the highest priority is given precedence.
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Modifier implements Comparable<Modifier> {
	public enum Priority {
		LIMIT(0), ADD(1), MULTIPLY(2);
		
		private int priority;
		
		private Priority(int p) {
			priority = p;
		}
		
		public int getPriority() {
			return priority;
		}
	}
	
	private int priority;
	private boolean modifyBase;
	
	public Modifier(int p, boolean modifiesBase) {
		priority = p;
		modifyBase = modifiesBase;
	}
	
	public abstract double modify(double value);
	
	/**
	 * Returns an ordering so that the highest priority is first in the list.
	 */
	public int compareTo(Modifier m) {
		return m.getPriority() - priority;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public boolean modifiesBase() {
		return modifyBase;
	}
}
