package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;

public class Stat_TotalHealth extends Stat {
	public static final String REFERENCE = "Total Health";
	public static final int SUBGROUP = 1;
	
	public Stat_TotalHealth(GameCharacter gamechar, double startingAmount) {
		super(gamechar, REFERENCE, startingAmount, false, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
		final Stat stat = this;
		Modifier modifier;
		
		//Limit the current health to be stay between [0, TotalHealth]
		modifier = new Modifier(Modifier.Priority.LIMIT.getPriority(), true){
			@Override
			public double modify(double value) {
				if (value < 0) return 0;
				else if (value > stat.getModifiedValue()) return stat.getModifiedValue();
				return value;
			}			
		};
		modifyTargets.add(new ModifyTarget(Stat_CurrentHealth.REFERENCE, modifier));
	}
	
	@Override
	public String getDescription() {
		return "The Total Health Points of the character.<br><3Losing all of them results in death!>";
	}
	
	@Override
	public String getAestheticName() {
		return "Health";
	}
}
