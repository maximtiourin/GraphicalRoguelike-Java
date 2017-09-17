package com.fizzikgames.roguelike.entity.mechanics.ability;

import com.fizzikgames.roguelike.asset.AssetLoader;
import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.PlayerCharacter;
import com.fizzikgames.roguelike.entity.FloatingContextStack.ContextType;
import com.fizzikgames.roguelike.entity.mechanics.Formula;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEvent;
import com.fizzikgames.roguelike.util.StringUtil;

public class Ability_Fireball extends Ability {	
	public static final String REFERENCE = "Fireball";
	
	public Ability_Fireball(GameCharacter gamechar) {
		super(gamechar, REFERENCE, AssetLoader.image("ability_fireball"), Ability.TargetType.TargetEnemy.getType(), 10, 150, 5, false);
	}

	@Override
	public void activate() {
	    //Deal Damage
        target.dealDamage(getDamage());
		
        //Send floating context if player
        if (gamechar instanceof PlayerCharacter) {
            int outgoing = getDamage();
            
            gamechar.getLevel().getContextStack().addNewContext(ContextType.DealDamage, getIconImage(), outgoing + "", 
                    target.getRow(), target.getColumn());
        }
        
		//Notify of Damage Done
		gamechar.getTriggerDispatcher().notifyListeners(TriggerEvent.Type.GameChar_DamageDone.getType());
		
		//Cleanup, like clearing target
		target = null;
	}

	@Override
	public String getDescription() {
		return "Launches a giant fireball at the target<br>enemy that deals <2" + StringUtil.addUSNumberCommas(getDamage() + "") + "> damage.";
	}
	
	@Override
	public boolean isValidTarget(GameCharacter target) {
	    if (target == null) return false;
	    
		if (!gamechar.equals(target)) {
			return true;
		}
		
		return false;
	}
	
	public int getDamage() {
		final double baseDamage = 15;
		final double baseIncrease = 5;
		final double multiplier = 6.5;
		//Also need to add things like spell effectiveness, modifiers, etc.
		
		//calculate output
		double output = baseDamage;
		output += Formula.levelExponentialScalingValue(multiplier, baseIncrease, gamechar.getCharacterLevel(), GameCharacter.MAX_CHARACTERLEVEL);
		
		return (int) Math.floor(output);
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
