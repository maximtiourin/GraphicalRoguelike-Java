package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEvent;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEventListener;
import com.fizzikgames.roguelike.entity.mechanics.buff.Buff;

public class Stat_CriticalHitChance extends Stat {
	public static final String REFERENCE = "Critical Hit Chance";
	public static final int SUBGROUP = 5;
	private static final double SOFT_CAP = .5;
	private static final double HARD_CAP = .75;
	
	public Stat_CriticalHitChance(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, true, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		//Critical Hit Chance is the chance that a successful damage roll will cause an additional Critical Hit Damage % of outgoing damage.
		//Has Self imposing diminishing Returns.
		TriggerEventListener trigger;
		
		//Add Damage Finalized Trigger
		trigger = new TriggerEventListener(TriggerEvent.Type.GameChar_OutgoingDamageFinalized.getType()) {
			@Override
			public void trigger(TriggerEvent e) {
				final GameCharacter gamecharobj = (GameCharacter) e.getTriggeringObject();
				
				double critchance = gamecharobj.getStat(Stat_CriticalHitChance.REFERENCE).getModifiedValue();
				double roll = GameLogic.rng.nextDouble();
				
				if (roll <= critchance) {
					//System.out.println("Critical Strike! (Roll: " + (roll * 100) + ", Chance: " + (critchance * 100) + "%)");
					gamecharobj.getTriggerDispatcher().notifyListeners(TriggerEvent.Type.GameChar_CriticalStrike.getType());
					gamecharobj.addBuff(new Buff(gamecharobj, "Critical Strike", null, 0, 1, 1, true) {
						@Override
						protected void initialize() {
							Modifier modifier;
							
							final double critdmg = gamecharobj.getStat(Stat_CriticalHitDamage.REFERENCE).getModifiedValue();
							
							modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false){
								@Override
								public double modify(double value) {
									return value + (value * critdmg);
								}			
							};
							modifyTargets.add(new ModifyTarget(Stat_OutgoingMeleeDamage.REFERENCE, modifier));
							modifyTargets.add(new ModifyTarget(Stat_OutgoingRangedDamage.REFERENCE, modifier));
							
							this.setFresh(false); //We want it being ticked immediately
						}
					});
				}
			}			
		};
		triggers.add(trigger);
		
		this.sendTriggers(character.getTriggerDispatcher());
	}
	
	@Override
	public String getDescription() {
		float value3 = (float) (SOFT_CAP * 100);
		float value4 = (float) (HARD_CAP * 100);
		final String rvalue = formatter.format(this.getModifiedValue() * 100);
		final String r2value = formatter.format(character.getStat(Stat_CriticalHitDamage.REFERENCE).getModifiedValue() * 100);
		
		return "<2[ " + rvalue + "% ]>" +
				"<br>" +
				"Gives a <2" + rvalue + "%> chance to Critically Hit with" +
				"<br>" +
				"Melee or Ranged attacks causing an" +
				"<br>" +
				"additional <2" + r2value + "%> amount of outgoing" +
				"<br>" +
				"damage based on the Critical Hit Damage" +
				"<br>" +
				"percentage." +
				"<br>" +
				"<br>" +
				"<3[Diminishing Returns]>" +
				"<br>" +
				"<3 - Soft Cap:    ><1" + value3 + "%>" +
				"<br>" +
				"<3 - Hard Cap:   ><1" + value4 + "%>";
	}
}
