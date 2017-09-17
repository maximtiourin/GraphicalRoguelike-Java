package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_CurrentExperience extends Stat {
	public static final String REFERENCE = "Current Experience";
	
	public Stat_CurrentExperience(GameCharacter gamechar, double startingAmount) {
		super(gamechar, REFERENCE, startingAmount, false, true);
	}

	@Override
	protected void initialize() {
		
	}
}
