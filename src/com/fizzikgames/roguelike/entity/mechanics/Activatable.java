package com.fizzikgames.roguelike.entity.mechanics;

import org.newdawn.slick.Image;

/**
 * An activatable object fires off certain actions whenever it is manually activated, as well as keeping a cooldown
 * of when it can be activated again.
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Activatable {
	protected int totalCooldown;
	protected int currentCooldown;
	protected int cost;
	protected Image iconImage;
	
	public Activatable(Image iconImage, int totalCd, int currentCd, int cost) {
		this.totalCooldown = totalCd;
		this.currentCooldown = currentCd;
		this.cost = cost;
		this.iconImage = iconImage;
	}
	
	public abstract void activate();
	public abstract boolean meetsActivateConditions();
	
	/**
	 * The maximum cooldown of this activatable
	 */
	public int getTotalCooldown() {
		return totalCooldown;
	}
	
	/**
	 * The cooldown remaining before this activatable can be activated again
	 */
	public int getCurrentCooldown() {
		return currentCooldown;
	}
	
	/**
	 * Start the cooldown for this activatable
	 */
	public void startCooldown() {
		currentCooldown = getTotalCooldown();
	}
	
	/**
	 * Calling this tells the activatable that a turn has happened and it should
	 * tick the cooldown.
	 */
	public void cooldownTick() {
		if (currentCooldown > 0) currentCooldown--;
	}
	
	/**
	 * The iconImage of the activatable to be used by any interested objects that want to draw it.
	 */
	public Image getIconImage() {
		return iconImage;
	}
	
	public void setIconImage(Image iconImage) {
		this.iconImage = iconImage;
	}
	
	/**
	 * Returns the cost to activate
	 */
	public int getCost() {
		return cost;
	}
}
