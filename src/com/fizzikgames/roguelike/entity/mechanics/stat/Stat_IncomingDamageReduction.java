package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_IncomingDamageReduction extends Stat {
	public static final String REFERENCE = "Incoming Damage Reduction";
	public static final int SUBGROUP = 6;
	private static final double SOFT_CAP = 0.6;
	private static final double HARD_CAP = 0.85;
	
	public Stat_IncomingDamageReduction(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, true, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//% Amount of Incoming damage to negate. Self imposing diminishing returns.
	}
	
	@Override
	public String getDescription() {
		float value = (float) (this.getModifiedValue() * 100);
		float value2 = (float) (SOFT_CAP * 100);
		float value3 = (float) (HARD_CAP * 100);
		
		return "<2[ " + value + "% ]>" +
				"<br>" +
				"Reduces all Incoming Damage by <2" + value + "%>." +
				"<br>" +
				"<br>" +
				"<3[Diminishing Returns]>" +
				"<br>" +
				"<3 - Soft Cap:    ><1" + value2 + "%>" +
				"<br>" +
				"<3 - Hard Cap:   ><1" + value3 + "%>";
	}
	
	@Override
	public String getAestheticName() {
		return "Damage Reduction";
	}
}
