package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_OutgoingRangedDamage extends Stat {
	public static final String REFERENCE = "Outgoing Ranged Damage";
	public static final int SUBGROUP = 5;
	
	public Stat_OutgoingRangedDamage(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//Amount of Ranged Damage to incur when dealing a ranged attack.
	}
	
	@Override
	public String getDescription() {
		return "The amount of damage done by ranged attacks.";
	}
	
	@Override
	public String getAestheticName() {
		return "Ranged Damage";
	}
}
