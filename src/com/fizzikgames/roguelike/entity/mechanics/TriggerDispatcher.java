package com.fizzikgames.roguelike.entity.mechanics;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A trigger dispatcher maintains a synchronized list of TriggerEventListeners
 * that it notifies whenever a triggering event occurs within its owner's scope.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class TriggerDispatcher {
	private Object owner;
	private CopyOnWriteArrayList<TriggerEventListener> listeners;
	
	public TriggerDispatcher(Object owner) {
		this.owner = owner;
		this.listeners = new CopyOnWriteArrayList<TriggerEventListener>();
	}
	
	public void addTriggerListener(TriggerEventListener e) {
		listeners.add(e);
	}
	
	public void notifyListeners(String eventType) {
		for (TriggerEventListener l : listeners) {
			if (l.getTriggerType().equals(eventType)) {
				l.trigger(new TriggerEvent(owner, eventType));
			}
		}
	}
	
	public boolean removeTriggerListener(TriggerEventListener e) {
		return listeners.remove(e);
	}
}
