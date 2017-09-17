package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_OutgoingMeleeDamage extends Stat {
	public static final String REFERENCE = "Outgoing Melee Damage";
	public static final int SUBGROUP = 5;
	
	public Stat_OutgoingMeleeDamage(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//Amount of Melee Damage to incur when dealing a melee attack.
	}
	
	@Override
	public String getDescription() {
		return "The amount of damage done by melee attacks.";
	}
	
	@Override
	public String getAestheticName() {
		return "Melee Damage";
	}
}
