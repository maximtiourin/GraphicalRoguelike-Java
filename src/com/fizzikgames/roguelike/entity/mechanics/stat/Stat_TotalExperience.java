package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;

public class Stat_TotalExperience extends Stat {
	public static final String REFERENCE = "Total Experience";
	
	public Stat_TotalExperience(GameCharacter gamechar, double startingAmount) {
		super(gamechar, REFERENCE, startingAmount, false, true);
	}

	@Override
	protected void initialize() {
		Modifier modifier;
		
		//Limit the current experience to be stay between [0, CurrentExperience]
		//The Reason it doesn't limit the upper bound to be totalExperience is so that
		//Left over experience can be conserved when clearing.
		modifier = new Modifier(Modifier.Priority.LIMIT.getPriority(), true){
			@Override
			public double modify(double value) {
				if (value < 0) return 0;
				return value;
			}			
		};
		modifyTargets.add(new ModifyTarget(Stat_CurrentExperience.REFERENCE, modifier));
	}
}
