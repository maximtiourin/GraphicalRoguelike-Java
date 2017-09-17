package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_BlockChance extends Stat {
	public static final String REFERENCE = "Block Chance";
	public static final int SUBGROUP = 7;
	
	public Stat_BlockChance(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, true, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//Block chance is the chance to block incoming damage and reduces it by Block Amount when wearing a shield.
	}
	
	@Override
	public String getDescription() {
		float value = (float) (this.getModifiedValue() * 100);
		float dvalue = (float) (character.getStat(Stat_BlockAmount.REFERENCE).getModifiedValue());
		
		return "<2[ " + value + "% ]>" +
				"<br>" +
				"Gives a <2" + value + "%> chance to Block <2" + dvalue + "> Damage with a shield equipped.";
	}
}
