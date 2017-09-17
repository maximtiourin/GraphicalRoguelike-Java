package com.fizzikgames.roguelike.entity.mechanics.ability;

import java.util.ArrayList;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.fizzikgames.roguelike.asset.AssetLoader;
import com.fizzikgames.roguelike.entity.Entity;
import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.PlayerCharacter;
import com.fizzikgames.roguelike.entity.FloatingContextStack.ContextType;
import com.fizzikgames.roguelike.entity.mechanics.Formula;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEvent;
import com.fizzikgames.roguelike.util.StringUtil;

public class Ability_ArcaneExplosion extends Ability {	
	public static final String REFERENCE = "Arcane Explosion";
	
	public Ability_ArcaneExplosion(GameCharacter gamechar) {
		super(gamechar, REFERENCE, AssetLoader.image("ability_arcaneexplosion"), Ability.TargetType.TargetSelf.getType(), 1, 25, 3, false);
	}

	@Override
	public void activate() {
	    //Deal Damage
	    
	    final float halfrange = getRange() / 2f;
        ArrayList<Entity> entities = (ArrayList<Entity>) gamechar.getLevel().getEntitiesInRectangle(new Rectangle(gamechar.getColumn() - halfrange, 
                gamechar.getRow() - halfrange, getRange(), getRange()));
        
        for (Entity e : entities) {
            if (e instanceof GameCharacter) {
                GameCharacter targ = (GameCharacter) e; 
                if (isValidTarget(targ)) {
                    Vector2f tpos = new Vector2f(targ.getColumn(), targ.getRow());
                    Vector2f cpos = new Vector2f(gamechar.getColumn(), gamechar.getRow());
                    
                    if (tpos.distance(cpos) <= halfrange) {
                        //Send floating context if player
                        if (gamechar instanceof PlayerCharacter) {
                            int outgoing = getDamage();
                            
                            
                            gamechar.getLevel().getContextStack().addNewContext(ContextType.DealDamage, getIconImage(), outgoing + "", 
                                    targ.getRow(), targ.getColumn());
                        }
                        
                        targ.dealDamage(getDamage());
                    }
                }
            }
        }		
		
		//Notify of Damage Done
		gamechar.getTriggerDispatcher().notifyListeners(TriggerEvent.Type.GameChar_DamageDone.getType());
		
		//Cleanup, like clearing target
		target = null;
	}

	@Override
	public String getDescription() {
		return "Arcane Energy Explodes in an area" +
				"<br>" +
				"around the caster dealing <2" + StringUtil.addUSNumberCommas(getDamage() + "") + "> damage" +
				"<br>" +
				"to all enemies within a " + (getRange() / 2f) + " meter radius.";
	}
	
	@Override
	public boolean isValidTarget(GameCharacter target) {
		if (!gamechar.equals(target)) {
			return true;
		}
		
		return false;
	}
	
	public int getDamage() {
		final double baseDamage = 2;
		final double baseIncrease = 2;
		final double multiplier = 6.5;
		//Also need to add things like spell effectiveness, modifiers, etc.
		
		//calculate output
		double output = baseDamage;
		output += Formula.levelExponentialScalingValue(multiplier, baseIncrease, gamechar.getCharacterLevel(), GameCharacter.MAX_CHARACTERLEVEL);
		
		return (int) Math.floor(output);
	}

	@Override
	public boolean targetUsesLineOfSight() {
		return false;
	}
	
	@Override
	public boolean meetsActivateConditions() {
		return true;
	}
}
