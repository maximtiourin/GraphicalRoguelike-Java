package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_CriticalHitDamage extends Stat {
	public static final String REFERENCE = "Critical Hit Damage";
	public static final int SUBGROUP = 5;
	
	public Stat_CriticalHitDamage(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, true, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//additional % of outgoing damage to add when a critical hit occurs.
	}
	
	public String getDescription() {
		final String rvalue = formatter.format(this.getModifiedValue() * 100);
		
		return "<2[ " + rvalue + "% ]>" +
				"<br>" +
				"Increases Melee or Ranged attack damage by" +
				"<br>" +
				"an additional <2" + rvalue + "%> if a Critical Hit occurs.";
	}
}
