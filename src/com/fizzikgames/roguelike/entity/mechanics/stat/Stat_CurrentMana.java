package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_CurrentMana extends Stat {
	public static final String REFERENCE = "Current Mana";
	
	public Stat_CurrentMana(GameCharacter gamechar, double startingAmount) {
		super(gamechar, REFERENCE, startingAmount, false, true);
	}

	@Override
	protected void initialize() {
		
	}
}
