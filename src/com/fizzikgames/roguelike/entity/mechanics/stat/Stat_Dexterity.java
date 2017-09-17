package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;

public class Stat_Dexterity extends Stat {
	public static final String REFERENCE = "Dexterity";
	public static final int SUBGROUP = 3;
	private static final double DODGE_CHANCE = 0.001;
	private static final double CRIT_CHANCE = 0.001;
	private static final double OUTGOING_RANGED_DAMAGE = 1;
	
	public Stat_Dexterity(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//Dexterity increases dodge chance, melee/ranged critical hit chance
		final double dcfactor = DODGE_CHANCE; //(1 dex = 0.1% dodge chance)
		final double chcfactor = CRIT_CHANCE; //(1 dex = 0.1% crit chance)
		final double ordfactor = OUTGOING_RANGED_DAMAGE; //(1 dex = 1 outgoing melee damage)
		
		final Stat stat = this;
		Modifier modifier;
		
		//Dodge Chance
		/*modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false){
			@Override
			public double modify(double value) {
				return value + (stat.getModifiedValue() * dcfactor);
			}			
		};
		modifyTargets.add(new ModifyTarget(Stat_DodgeChance.REFERENCE, modifier));*/
		
		//Melee/Ranged Crit Chance
		modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false){
			@Override
			public double modify(double value) {
				return value + (stat.getModifiedValue() * chcfactor);
			}			
		};
		modifyTargets.add(new ModifyTarget(Stat_CriticalHitChance.REFERENCE, modifier));
		
		//Outgoing Ranged Damage
        modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false){
            @Override
            public double modify(double value) {
                return value + (stat.getModifiedValue() * ordfactor);
            }           
        };
        modifyTargets.add(new ModifyTarget(Stat_OutgoingRangedDamage.REFERENCE, modifier));
	}
	
	@Override
	public String getDescription() {
		float value1 = (float) (CRIT_CHANCE * 100);
		float value2 = (float) (DODGE_CHANCE * 100);
		int value3 = (int) OUTGOING_RANGED_DAMAGE;
		
		return "Each point of Dexterity:" +
				"<br>" +
				"<1 - increases Critical Strike Chance by ><2" + value1 + "%>" +
				"<br>" +
                "<1 - increases Ranged Damage by ><2" + value3 + ">";
				//"<br>" +
				//"<1 - increases Dodge Chance by ><2" + value2 + "%>";
	}
}
