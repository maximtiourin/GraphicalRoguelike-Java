package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_CurrentHealth extends Stat {
	public static final String REFERENCE = "Current Health";
	
	public Stat_CurrentHealth(GameCharacter gamechar, double startingAmount) {
		super(gamechar, REFERENCE, startingAmount, false, true);
	}

	@Override
	protected void initialize() {
		
	}
}
