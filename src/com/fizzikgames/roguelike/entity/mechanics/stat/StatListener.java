package com.fizzikgames.roguelike.entity.mechanics.stat;

/**
 * Notifies that the given stat has had an event occur.
 * @author Maxim Tiourin
 * @version 1.00
 */
public interface StatListener {
	/**
	 * Notification that the stat even occured for the given stat
	 */
	public void statEventOccured(Stat stat);
}
