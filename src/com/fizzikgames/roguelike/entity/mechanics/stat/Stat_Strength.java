package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;

public class Stat_Strength extends Stat {
	public static final String REFERENCE = "Strength";
	public static final int SUBGROUP = 2;
	private static final double TOTAL_HEALTH = 25;
	private static final double OUTGOING_MELEE_DAMAGE = 1;
	
	public Stat_Strength(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//Strength modifies Total Health, Outgoing Melee Damage
		final double thfactor = TOTAL_HEALTH; //(1 str = 25 Total Health)
		final double omdfactor = OUTGOING_MELEE_DAMAGE; //(1 str = 2 outgoing melee damage)
		
		final Stat stat = this;
		Modifier modifier;
		
		//Total Health
		modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false){
			@Override
			public double modify(double value) {
				return value + (stat.getModifiedValue() * thfactor);
			}			
		};
		modifyTargets.add(new ModifyTarget(Stat_TotalHealth.REFERENCE, modifier));
		
		//Outgoing Melee Damage
		modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false){
			@Override
			public double modify(double value) {
				return value + (stat.getModifiedValue() * omdfactor);
			}			
		};
		modifyTargets.add(new ModifyTarget(Stat_OutgoingMeleeDamage.REFERENCE, modifier));
	}
	
	@Override
	public String getDescription() {
		int value1 = (int) TOTAL_HEALTH;
		int value2 = (int) OUTGOING_MELEE_DAMAGE;
		
		return "Each point of Strength:" +
				"<br>" +
				"<1 - increases Total Health Points by ><2" + value1 + ">" +
				"<br>" +
				"<1 - increases Melee Damage by ><2" + value2 + ">";
	}
}
