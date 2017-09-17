package com.fizzikgames.roguelike.entity.mechanics.stat;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEvent;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEventListener;

public class Stat_ManaRegeneration extends Stat {
	public static final String REFERENCE = "Mana Regeneration";
	public static final int SUBGROUP = 8;
	
	public Stat_ManaRegeneration(GameCharacter character, double startingAmount) {
		super(character, REFERENCE, startingAmount, false, SUBGROUP, false);
	}

	@Override
	protected void initialize() {
	    final Stat stat = this;
        
        //Sends a trigger that regens mana at turn start.
        TriggerEventListener trigger = new TriggerEventListener(TriggerEvent.Type.GameChar_TurnStarted.getType()) {
            @Override
            public void trigger(TriggerEvent e) {
                character.healMana(stat.getModifiedValue(), false);
            }           
        };
        triggers.add(trigger);
        
        this.sendTriggers(character.getTriggerDispatcher());
	}

	public String getDescription() {
	    final String rvalue = formatter.format(this.getModifiedValue());
		
		return "<2[ " + rvalue + " ]>" +
				"<br>" +
				"Regenerates <2" + rvalue + "> mana at the start every turn.";
	}
}
