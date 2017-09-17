package com.fizzikgames.roguelike.entity.mechanics;

/**
 * A Trigger Event contains information that gives the type of event that is triggering,
 * and the object that is triggering it.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class TriggerEvent {
	public enum Type {
	    GameChar_TurnStarted("GameChar_TurnStarted"),
	    GameChar_TurnEnded("GameChar_TurnEnded"),
		GameChar_OutgoingDamageFinalized("GameChar_OutgoingDamageFinalized"),
		GameChar_DamageDone("GameChar_DamageDone"),
		GameChar_IncomingDamageFinalized("GameChar_IncomingDamageFinalized"),
		GameChar_DamageTaken("GameChar_DamageTaken"),
		GameChar_CriticalStrike("GameChar_CriticalStrike"),
		GameChar_MeleeCriticalStrike("GameChar_MeleeCriticalStrike"),
		GameChar_RangedCriticalStrike("GameChar_RangedCriticalStrike");
		
		private String type;
		
		private Type(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
	}
	
	private Object object;
	private String type;
	
	public TriggerEvent(Object object, String type) {
		this.object = object;
		this.type = type;
	}
	
	public Object getTriggeringObject() {
		return object;
	}
	
	public String getEventType() {
		return type;
	}
}
