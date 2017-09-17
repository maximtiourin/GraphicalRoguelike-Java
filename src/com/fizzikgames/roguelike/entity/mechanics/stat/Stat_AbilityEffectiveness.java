package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_AbilityEffectiveness extends Stat {
	public static final String REFERENCE = "Ability Effectiveness";
	public static final int SUBGROUP = 5;
	
	public Stat_AbilityEffectiveness(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//Amount of ability modifier to apply to every ability used, every ability uses ability effectiveness differently.
	}
	
	@Override
	public String getDescription() {
	    final String rvalue = formatter.format(this.getModifiedValue());
		return "<2[ " + rvalue + " ]>" + 
				"<br>" +
				"Improves abilities based on their individual" +
				"<br>" +
				"scaling with Ability Effectiveness." +
				"<br>" +
				"<3Can improve aspects such as effect values,>" +
				"<br>" +
				"<3cooldown, range, and cost.>";
	}
}
