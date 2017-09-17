package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_BlockAmount extends Stat {
	public static final String REFERENCE = "Block Amount";
	public static final int SUBGROUP = 7;
	
	public Stat_BlockAmount(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//Block amount is the amount of damage blocked when a block occurs.
	}
	
	@Override
	public String getDescription() {
		float value = (float) this.getModifiedValue();
		
		return "<2[ " + value + " ]>" +
				"<br>" +
				"Reduces Incoming Damage by <2" + value + "> when a Block occurs.";
	}
}
