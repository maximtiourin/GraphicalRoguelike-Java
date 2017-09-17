package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_MagicFind extends Stat {
	public static final String REFERENCE = "Magic Find";
	
	public Stat_MagicFind(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, true);
	}

	@Override
	protected void initialize() {
		//Additional % chance to find every loot tier when performing loot rules.
	}
	
	@Override
	public String getDescription() {
		float value = (float) (this.getModifiedValue() * 100);
		
		return "<2[ " + value + "% ]>" +
				"<br>" +
				"Increases the chances of finding rare loot by <2" + value + "%>.";
	}
}
