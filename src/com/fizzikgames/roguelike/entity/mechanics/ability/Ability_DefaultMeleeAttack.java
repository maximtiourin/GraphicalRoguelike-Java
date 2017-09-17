package com.fizzikgames.roguelike.entity.mechanics.ability;

import com.fizzikgames.roguelike.entity.FloatingContextStack.ContextType;
import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.PlayerCharacter;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEvent;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_OutgoingMeleeDamage;

public class Ability_DefaultMeleeAttack extends Ability {
	public static final String REFERENCE = "Default Melee Attack";
	
	public Ability_DefaultMeleeAttack(GameCharacter gamechar) {
		super(gamechar, REFERENCE, null, Ability.TargetType.TargetEnemy.getType(), 0, 0, 1,
				true);
	}

	@Override
	public void activate() {		
		//Finalize Damage before applying
		gamechar.finalizeOutgoingDamage();
		
		//Send floating context if player
		if (gamechar instanceof PlayerCharacter) {
		    int outgoing = (int) gamechar.getStat(Stat_OutgoingMeleeDamage.REFERENCE).getModifiedValue();
		    
		    if (gamechar.hadCriticalStrike()) {
		        gamechar.getLevel().getContextStack().addNewContext(ContextType.DealCritical, null, outgoing + "", 
		                target.getRow(), target.getColumn());
		    }
		    else {
		        gamechar.getLevel().getContextStack().addNewContext(ContextType.DealDamage, null, outgoing + "", 
                        target.getRow(), target.getColumn());
		    }
		}
		
		//Deal Damage
		//System.out.println("MELEE: " + gamechar.getStat(Stat_OutgoingMeleeDamage.REFERENCE).getModifiedValue());
		target.dealDamage(gamechar.getStat(Stat_OutgoingMeleeDamage.REFERENCE).getModifiedValue());
		
		//Notify of Damage Done
		gamechar.getTriggerDispatcher().notifyListeners(TriggerEvent.Type.GameChar_DamageDone.getType());
		
		//Cleanup, like clearing target
		target = null;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public boolean isValidTarget(GameCharacter target) {
	    if (target == null) return false;
	    
		if (!gamechar.equals(target)) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean targetUsesLineOfSight() {
		return true;
	}

	@Override
	public boolean meetsActivateConditions() {
		return true;
	}
}
