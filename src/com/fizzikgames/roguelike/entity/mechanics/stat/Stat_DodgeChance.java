package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Formula;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;

public class Stat_DodgeChance extends Stat {
	public static final String REFERENCE = "Dodge Chance";
	public static final int SUBGROUP = 7;
	private static final double SOFT_CAP = .5;
	private static final double HARD_CAP = .75;
	
	public Stat_DodgeChance(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, true, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//% chance to completely negate incoming damage. Has Self Imposing Diminishing Returns.
		final double softCap = SOFT_CAP; //50% chance to dodge
		final double hardCap = HARD_CAP; //75% chance to dodge
		final double softCapScale = .45; //At Base 100% dodge chance, Modified would be 75%.
		final double hardCapScale = .15; //At Base 225% dodge chance, Modified would be 75%
		
		//final Stat stat = this;
		Modifier modifier;
		
		//Diminishing Return Limit
		modifier = new Modifier(Modifier.Priority.LIMIT.getPriority(), false){
			@Override
			public double modify(double value) {
				return Formula.complexDiminishingReturns(value, softCap, hardCap, softCapScale, hardCapScale, 0.0, 1.0);
			}			
		};
		modifyTargets.add(new ModifyTarget(REFERENCE, modifier));
	}
	
	@Override
	public String getDescription() {
		float value = (float) (this.getModifiedValue() * 100);
		float value2 = (float) (SOFT_CAP * 100);
		float value3 = (float) (HARD_CAP * 100);
		
		return "<2[ " + value + "% ]>" +
				"<br>" +
				"Has a <2" + value + "%> chance to completely" +
				"<br>" +
				"avoid Incoming Damage." +
				"<br>" +
				"<br>" +
				"<3[Diminishing Returns]>" +
				"<br>" +
				"<3 - Soft Cap:    ><1" + value2 + "%>" +
				"<br>" +
				"<3 - Hard Cap:   ><1" + value3 + "%>";
	}
}
