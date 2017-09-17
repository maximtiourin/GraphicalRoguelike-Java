package com.fizzikgames.roguelike.entity.mechanics.buff;

import java.util.ArrayList;
import java.util.Comparator;

import org.newdawn.slick.Image;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;
import com.fizzikgames.roguelike.entity.mechanics.TriggerDispatcher;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEventListener;

/**
 * A temporary stat modifier applies modifications to stat for a determined time period before removing
 * those modifications and itself from the game character.
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class TemporaryStatModifier {
	protected GameCharacter character;
	protected String reference;
	protected Image image;
	protected int maxDuration;
	protected int currentDuration;
	protected ArrayList<ModifyTarget> modifyTargets;
	protected int maxStacks;
	protected int currentStacks;
	protected boolean hidden;
	protected boolean permanent;
	protected boolean fresh;
	protected ArrayList<TriggerEventListener> triggers;
	
	public TemporaryStatModifier(GameCharacter gamechar, String reference, Image iconImage, int duration, 
			int maxStacks, int currentStacks, boolean hidden) {
		this.character = gamechar;
		this.reference = reference;
		this.image = iconImage;
		this.maxDuration = duration;
		this.currentDuration = duration;
		this.modifyTargets = new ArrayList<ModifyTarget>();
		this.triggers = new ArrayList<TriggerEventListener>();
		this.maxStacks = maxStacks;
		this.currentStacks = currentStacks;
		this.hidden = hidden;
		this.permanent = false;
		this.fresh = true;
		
		initialize();
	}
	
	public TemporaryStatModifier(GameCharacter gamechar, String reference, Image iconImage, 
			int maxStacks, int currentStacks, boolean hidden) {
		this.character = gamechar;
		this.reference = reference;
		this.image = iconImage;
		this.maxDuration = Integer.MAX_VALUE;
		this.currentDuration = Integer.MAX_VALUE;
		this.modifyTargets = new ArrayList<ModifyTarget>();
		this.triggers = new ArrayList<TriggerEventListener>();
		this.maxStacks = maxStacks;
		this.currentStacks = currentStacks;
		this.hidden = hidden;
		this.permanent = true;
		this.fresh = true;
		
		initialize();
	}
	
	/**
	 * Called by the TemporaryStatModifier constructor, should initialize the modifyTargets of this modifier, as well as triggers.
	 */
	protected abstract void initialize();
	/**
	 * Removes this temporary stat modifier from the character.
	 */
	protected abstract boolean remove();
	
	/**
	 * Ticks the duration, and removes itself if out of time and stacks.
	 */
	public void tickDuration() {
		if (!permanent) {
			currentDuration--;
			
			if (currentDuration <= 0) {
				if (currentStacks > 1) {
					currentStacks--;
					currentDuration = maxDuration;
					character.calculateStatModifiers();
				}
				else {
					//Remove this statModifier
					cleanupOutgoingModifiers();
					cleanupTriggers();
					//Call characters remove method to remove this from the appropriate list.
					remove();
				}
			}
		}
	}
	
	/**
	 * Sends all of this statModifier's modifiers to the appropriate targets. Character should call cleanupModifiers
	 * then this whenever a new stat is added to the character.
	 */
	public void sendOutgoingModifiers() {
		for (ModifyTarget e : modifyTargets) {
			character.getStat(e.getReference()).addIncomingModifier(e.getModifier());
		}
	}
	
	/**
	 * Removes the outgoing modifiers of all modifyTargets for cleanup purposes.
	 */
	public void cleanupOutgoingModifiers() {
		for (ModifyTarget e : modifyTargets) {
			removeOutgoingModifier(e);
		}
	}
	
	/**
	 * Removes the outgoing modifier of the modify target.
	 */
	public void removeOutgoingModifier(ModifyTarget tar) {
		character.getStat(tar.getReference()).removeIncomingModifier(tar.getModifier());
	}
	
	/**
	 * Sends all of this statmodifiers's triggers to the given dispatcher
	 */
	public void sendTriggers(TriggerDispatcher dispatcher) {
		for (TriggerEventListener e : triggers) {
			e.addToDispatcher(dispatcher);
		}
	}
	
	/**
	 * Removes this statmodifierss triggers (and clears their appropriate dispatchers) for cleanup purposes.
	 */
	public void cleanupTriggers() {
		for (TriggerEventListener e : triggers) {
			e.removeFromDispatcher();
		}
	}
	
	/**
	 * Returns the total duration remaining, factoring in stacks
	 */
	public int getTotalDurationLeft() {
		if (!permanent) {
			int dur = 0;
			
			//Stacks
			dur += (currentStacks - 1) * maxDuration;
			
			//Current
			dur += currentDuration;
			
			return dur;
		}
		
		return maxDuration;
	}
	
	/**
	 * Returns the current duration remaining, ignoring stacks.
	 */
	public int getCurrentDurationLeft() {
		return currentDuration;
	}
	
	public int getCurrentStacks() {
		return currentStacks;
	}
	
	public String getDescription() {
		return getReference();
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public boolean isPermanent() {
		return permanent;
	}
	
	public Image getIconImage() {
		return image;
	}
	
	public void setIconImage(Image iconImage) {
		this.image = iconImage;
	}
	
	public String getReference() {
		return reference;
	}
	
	public String getAestheticName() {
		return getReference();
	}
	
	public boolean isStackable() {
		return (maxStacks > 1);
	}
	
	public boolean isFresh() {
		return fresh;
	}
	
	public void setFresh(boolean b) {
		fresh = b;
	}
	
	/**
	 * Attempts to add a stack, otherwise sets currentDuration to maxDuration.
	 */
	public void addStacks(int stacks) {
		if (isStackable() && (currentStacks < maxStacks)) {
			currentStacks += stacks;
			
			if (currentStacks > maxStacks) {
				currentStacks = maxStacks;
				currentDuration = maxDuration;
				setFresh(true);
			}
		}
		else {
			currentDuration = maxDuration;
			setFresh(true);
		}
	}
	
	public static final class DurationComparator implements Comparator<TemporaryStatModifier> {
		@Override
		public int compare(TemporaryStatModifier a, TemporaryStatModifier b) {
			return a.getTotalDurationLeft() - b.getTotalDurationLeft();
		}		
	}
}
