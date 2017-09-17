package com.fizzikgames.roguelike.entity.mechanics.ability.item;

import java.util.ArrayList;
import java.util.List;

import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEventListener;

/**
 * An affix contains a list of modifiers and triggers and can return a descriptive
 * String describing the affix.
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Affix {
	protected ArrayList<ModifyTarget> modifyTargets;
	protected ArrayList<TriggerEventListener> triggers;
	
	public Affix() {
		this.modifyTargets = new ArrayList<ModifyTarget>();
		this.triggers = new ArrayList<TriggerEventListener>();
	}
	
	/**
	 * Returns the string describing this affix.
	 */
	public abstract String getDescription();
	
	public void addNewModifyTarget(ModifyTarget modify) {
		modifyTargets.add(modify);
	}
	
	public List<ModifyTarget> getModifyTargets() {
		return modifyTargets;
	}
	
	public void addNewTrigger(TriggerEventListener trigger) {
		triggers.add(trigger);
	}
	
	public List<TriggerEventListener> getTriggers() {
		return triggers;
	}
}
