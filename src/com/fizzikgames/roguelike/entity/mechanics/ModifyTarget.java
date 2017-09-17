package com.fizzikgames.roguelike.entity.mechanics;

/**
 * Defines a stat reference and modifier pairing for modifying a stat.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class ModifyTarget {
	private String reference;
	private Modifier modifier;
	
	public ModifyTarget(String reference, Modifier modifier) {
		this.reference = reference;
		this.modifier = modifier;
	}
	
	public String getReference() {
		return reference;
	}
	
	public Modifier getModifier() {
		return modifier;
	}
}
