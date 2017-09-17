package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;

public class Stat_Armor extends Stat {
	public static final String REFERENCE = "Armor";
	public static final int SUBGROUP = 7;
	private static final double DAMAGE_REDUCTION = 0.0001;
	
	public Stat_Armor(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//Armor reduces incoming damage by %.
		final double drfactor = DAMAGE_REDUCTION; //(1 armor = 0.01% damage reduction)
	}
	
	@Override
	public String getDescription() {
		double armorPerOne = 0.01 / 0.0001;
		int value1 = (int) armorPerOne;
		
		return "Every <2" + value1 + "> points of Armor increases" +
				"<br>" +
				"Damage Reduction by <21.0%>";
	}
}
