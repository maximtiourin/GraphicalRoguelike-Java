package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_IncomingDamage extends Stat {
	public static final String REFERENCE = "Incoming Damage";
	
	public Stat_IncomingDamage(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false, true);
	}

	@Override
	protected void initialize() {
		//Amount of Damage that will be incurred on character, base periodically wiped when not taking damage.
	}
}
