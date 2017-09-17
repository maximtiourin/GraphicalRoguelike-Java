package com.fizzikgames.roguelike.entity.mechanics.stat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;
import com.fizzikgames.roguelike.entity.mechanics.TriggerDispatcher;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEventListener;

public abstract class Stat {
    public static final DecimalFormat formatter = new DecimalFormat("###.#");
	protected GameCharacter character;
	protected String reference;
	protected int subGroup; //The subgroup this stat should be paired with whenever displaying stats.
	protected double base; //The base stat before any modifiers, modify this if you want a permanent increase to the stat
	protected double modified; //The value after it was modified
	protected boolean percentage; //If stat should be interpreted as a percentage instead of a normal value.
	protected boolean hidden; //If stat should be hidden from player.
	protected ArrayList<ModifyTarget> modifyTargets; //List of references to stats this stat somehow modifies.
	protected ArrayList<Modifier> incomingModifiers; //Modifiers that affect this stat
	protected ArrayList<StatListener> modifyListeners;
	protected ArrayList<TriggerEventListener> triggers;
	
	public Stat(GameCharacter character, String reference, double startingAmount, boolean isPercentage) {
		this.character = character;
		this.reference = reference;
		this.base = startingAmount;
		this.percentage = isPercentage;
		this.subGroup = 9999;
		this.hidden = false;
		this.modifyTargets = new ArrayList<ModifyTarget>();
		this.incomingModifiers = new ArrayList<Modifier>();
		this.modifyListeners = new ArrayList<StatListener>();
		this.triggers = new ArrayList<TriggerEventListener>();
		
		initialize();
	}
	
	public Stat(GameCharacter character, String reference, double startingAmount, boolean isPercentage, boolean hidden) {
		this.character = character;
		this.reference = reference;
		this.base = startingAmount;
		this.percentage = isPercentage;
		this.subGroup = 9999;
		this.hidden = hidden;
		this.modifyTargets = new ArrayList<ModifyTarget>();
		this.incomingModifiers = new ArrayList<Modifier>();
		this.modifyListeners = new ArrayList<StatListener>();
		this.triggers = new ArrayList<TriggerEventListener>();
		
		initialize();
	}
	
	public Stat(GameCharacter character, String reference, double startingAmount, boolean isPercentage, int subGroup, boolean hidden) {
		this.character = character;
		this.reference = reference;
		this.base = startingAmount;
		this.percentage = isPercentage;
		this.subGroup = subGroup;
		this.hidden = hidden;
		this.modifyTargets = new ArrayList<ModifyTarget>();
		this.incomingModifiers = new ArrayList<Modifier>();
		this.modifyListeners = new ArrayList<StatListener>();
		this.triggers = new ArrayList<TriggerEventListener>();
		
		initialize();
	}
	
	/**
	 * Called by Stat constructor, should set modifyTargets of the stat, as well as triggers.
	 */
	protected abstract void initialize();
	
	/**
	 * Sends all of this stat's modifiers to the appropriate targets. Character should call cleanupModifiers
	 * then this whenever a new stat is added to the character.
	 */
	public void sendOutgoingModifiers() {
		for (ModifyTarget e : modifyTargets) {
			character.getStat(e.getReference()).addIncomingModifier(e.getModifier());
		}
	}
	
	/**
	 * Calculates the new stat modified value after running through its modifiers.
	 */
	public void determineModifiedValue() {
		boolean wasModified = false;
		double oldModified = modified;
		modified = base;
		
		//Sort modifiers by priority, highest priority modifies first.
		Collections.sort(incomingModifiers);
		
		for (Modifier e : incomingModifiers) {
			if (e.modifiesBase()) {
				base = e.modify(base);
				modified = base;
			}
			else {				
				modified = e.modify(modified);
			}
		}
		
		if (Double.compare(modified, oldModified) != 0) wasModified = true;
		
		//Send modify notification to listeners if stat was modified
		if (wasModified) {
			notifyModificationListeners();
		}
	}
	
	/**
	 * Removes all incoming modifiers. Helpful for cleaning the stat when expecting
	 * a resend of modifiers.
	 */
	public void cleanupModifiers() {
		incomingModifiers.clear();
		modified = base;
	}
	
	/**
	 * Removes the outgoing modifiers of this stat's modifyTargets for cleanup purposes.
	 */
	public void cleanupOutgoingModifiers() {
		for (ModifyTarget e : modifyTargets) {
			removeOutgoingModifier(e);
		}
	}
	
	/**
	 * Sends all of this stat's triggers to the given dispatcher
	 */
	public void sendTriggers(TriggerDispatcher dispatcher) {
		for (TriggerEventListener e : triggers) {
			e.addToDispatcher(dispatcher);
		}
	}
	
	/**
	 * Removes this stats triggers (and clears their appropriate dispatchers) for cleanup purposes.
	 */
	public void cleanupTriggers() {
		for (TriggerEventListener e : triggers) {
			e.removeFromDispatcher();
		}
	}
	
	/**
	 * Removes an outgoing modifier from this stat.
	 */
	public void removeOutgoingModifier(ModifyTarget tar) {
		character.getStat(tar.getReference()).removeIncomingModifier(tar.getModifier());
	}
	
	public void addIncomingModifier(Modifier modifier) {
		incomingModifiers.add(modifier);
	}
	
	/**
	 * Removes an incoming modifier from this stat
	 */
	public void removeIncomingModifier(Modifier modifier) {
		incomingModifiers.remove(modifier);
	}
	
	/**
	 * Returns true if the stat should be interpreted as a percentage value.
	 */
	public boolean isPercentage() {
		return percentage;
	}
	
	public int getSubGroup() {
		return subGroup;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	/**
	 * Returns a description of this stat.
	 * Default returns the stat reference.
	 * Stats that need a real description can override.
	 */
	public String getDescription() { 
		return getReference(); 
	}
	
	/**
	 * Returns an aesthetic name of this stat.
	 * Example: 'Total Health' becomes 'Health'.
	 * Default returns the stat reference.
	 * Stats that need a real aesthetic name can override.
	 */
	public String getAestheticName() {
		return getReference();
	}
	
	/**
	 * Returns the string reference of the stat.
	 */
	public String getReference() {
		return reference;
	}
	
	/**
	 * Returns the base value of the stat before any modifiers.
	 */
	public double getBaseValue() {
		return base;
	}
	
	/**
	 *  Sets the base value, notifies of a modification event
	 */
	public void setBaseValue(double value, boolean sendNotify) {
		base = value;
		if (sendNotify) notifyModificationListeners();
	}
	
	/**
	 * Adds the value to the base permanently, notifies of a modification event
	 */
	public void addToBaseValue(double value) {
		base += value;
		notifyModificationListeners();
	}
	
	public double getModifiedValue() {
		return modified;
	}
	
	public void notifyModificationListeners() {
		for (StatListener l : modifyListeners) {
			l.statEventOccured(this);
		}
	}
	
	/**
	 * Adds a listener that listens for when this stat is modified in any way.
	 */
	public void addModificationListener(StatListener l) {
		modifyListeners.add(l);
	}
	
	public boolean removeModificationListener(StatListener l) {
		return modifyListeners.remove(l);
	}
	
	/**
	 * Compares two stats using their subgroups, if they share a subgroup, compares
	 * their references.
	 * @author Maxim Tiourin
	 * @version 1.00
	 */
	public static class subGroupComparator implements Comparator<Stat> {
		@Override
		public int compare(Stat a, Stat b) {
			if (a.getSubGroup() == b.getSubGroup()) {
				return a.getReference().compareTo(b.getReference());
			}
			else {
				return a.getSubGroup() - b.getSubGroup();
			}
		}		
	}
}
