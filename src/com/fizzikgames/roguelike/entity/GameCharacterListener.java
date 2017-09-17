package com.fizzikgames.roguelike.entity;

import java.util.List;

/**
 * A Game Character Listener listens to a list of GameCharacterEvent types.
 * @author Maxim Tiourin
 * @version 1.00
 */
public interface GameCharacterListener {
	/**
	 * Performs any actions for the given event.
	 */
	public void eventPerformed(GameCharacterEvent e);
	/**
	 * A list containing strings that define the type of GameCharacterEvents this 
	 * listener wants to listen to, the event dispatcher will only send out events to
	 * appropriate listeners listening in on those events.
	 */
	public List<String> getEventTypes();
}
