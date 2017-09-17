package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;

public class Stat_SightRadius extends Stat {
	public static final String REFERENCE = "Sight Radius";
	
	public Stat_SightRadius(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false);
	}

	@Override
	protected void initialize() {
		Modifier modifier;
		
		//Floor the sight radius and limit it to stay at minimum 1;
		modifier = new Modifier(Modifier.Priority.LIMIT.getPriority(), false){
			@Override
			public double modify(double value) {
				if (value < 1) return 1;
				else return Math.floor(value);
			}			
		};
		modifyTargets.add(new ModifyTarget(REFERENCE, modifier));
	}
	
	@Override
	public String getDescription() {
		return "How many meters the character can see in any direction.";
	}
}
