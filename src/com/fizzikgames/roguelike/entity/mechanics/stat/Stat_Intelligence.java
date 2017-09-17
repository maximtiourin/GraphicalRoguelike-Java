package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;

public class Stat_Intelligence extends Stat {
	public static final String REFERENCE = "Intelligence";
	public static final int SUBGROUP = 4;
	private static final double TOTAL_MANA = 25;
	private static final double ABILITY_EFFECT = 0.25;
	
	public Stat_Intelligence(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//Intelligence modifies Total Mana, Ability Effectiveness
		final double tmfactor = TOTAL_MANA; //(1 int = 25 Total Mana)
		final double aefactor = ABILITY_EFFECT; //(1 int = 0.25 Ability Effectiveness)
		
		final Stat stat = this;
		Modifier modifier;
		
		//Total Mana
		modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false){
			@Override
			public double modify(double value) {
				return value + (stat.getModifiedValue() * tmfactor);
			}			
		};
		modifyTargets.add(new ModifyTarget(Stat_TotalMana.REFERENCE, modifier));
		
		//Ability Effectiveness
		/*modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false){
			@Override
			public double modify(double value) {
				return value + (stat.getModifiedValue() * aefactor);
			}			
		};
		modifyTargets.add(new ModifyTarget(Stat_AbilityEffectiveness.REFERENCE, modifier));*/
	}
	
	@Override
	public String getDescription() {
		int value1 = (int) TOTAL_MANA;
		float value2 = (float) ABILITY_EFFECT;
		
		return "Each point of Intelligence:" +
				"<br>" +
				"<1 - increases Total Mana Points by ><2" + value1 + ">";
				//"<br>" +
				//"<1 - increases Ability Effectiveness by ><2" + value2 + ">";
	}
}
