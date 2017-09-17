package com.fizzikgames.roguelike.entity.mechanics;

/**
 * A Trigger Event Listeners listens for a Trigger Event to occur before firing off
 * its trigger actions.
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class TriggerEventListener {
	private String triggerType;
	private TriggerDispatcher dispatcher;
	
	public TriggerEventListener(String triggerType) {
		this.triggerType = triggerType;
		this.dispatcher = null;
	}
	
	public abstract void trigger(TriggerEvent e);
	
	/**
	 * Adds the listener to the given dispatcher, this should always
	 * be used instead of calling addTriggerListener from the dispatcher directly.
	 */
	public void addToDispatcher(TriggerDispatcher e) {
		dispatcher = e;
		e.addTriggerListener(this);
	}
	
	public boolean removeFromDispatcher() {
		if (dispatcher != null) {
			return dispatcher.removeTriggerListener(this);
		}
		
		return false;
	}
	
	public String getTriggerType() {
		return triggerType;
	}
}
