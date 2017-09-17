package com.fizzikgames.roguelike.entity.mechanics.ability;

import com.fizzikgames.roguelike.asset.AssetLoader;
import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.GameCharacterEvent;
import com.fizzikgames.roguelike.entity.PlayerCharacter;
import com.fizzikgames.roguelike.entity.FloatingContextStack.ContextType;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_SightRadius;

public class Ability_Blink extends Ability {	
	public static final String REFERENCE = "Blink";
	
	public Ability_Blink(GameCharacter gamechar) {
		super(gamechar, REFERENCE, AssetLoader.image("ability_blink"), Ability.TargetType.TargetEnemy.getType(), 20, 50, 10, false);
	}

	@Override
	public void activate() {
	    //Teleport
	    gamechar.setRow(targetrow);
	    gamechar.setColumn(targetcolumn);
	    gamechar.getLevel().calculatePlayerVisibility((int) gamechar.getStat(Stat_SightRadius.REFERENCE).getModifiedValue());
	    gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Position_Modified.getType()));
	    
	    //Send floating context if player
        if (gamechar instanceof PlayerCharacter) {
            gamechar.getLevel().getContextStack().addNewContext(ContextType.Info, getIconImage(), getName(), 
                    gamechar.getRow(), gamechar.getColumn());
        }
		
		//Cleanup, like clearing target
		target = null;
	}

	@Override
	public String getDescription() {
		return "Teleports a short distance to an unoccupied visited tile.";
	}
	
	@Override
	public boolean isValidTarget(GameCharacter target) {
	    if (target != null) return false;
		
		return true;
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
