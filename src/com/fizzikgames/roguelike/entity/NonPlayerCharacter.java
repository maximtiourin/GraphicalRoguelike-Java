package com.fizzikgames.roguelike.entity;

import com.fizzikgames.roguelike.entity.mechanics.ability.Ability;
import com.fizzikgames.roguelike.pathfinding.Node;
import com.fizzikgames.roguelike.world.Level;

public abstract class NonPlayerCharacter extends GameCharacter {
	public NonPlayerCharacter(String spriteref, Level level, int r, int c, int characterLevel) {
		super(spriteref, level, r, c, characterLevel);
	}
	
	@Override
	public void finalizeOutgoingDamage() {
	}

	@Override
	protected void enterTargetingMode(Ability ability) {
	}

	@Override
	protected void cancelTargetingMode() {
	}

	@Override
	public boolean isValidNode(Node srcNode, Node dstNode) {
	    if (level.movementImpededAt(true, dstNode.getRow(), dstNode.getColumn())) {
            return false;
        }
        
        return true;
	}

	@Override
	public int getMoveCost(Node srcNode, Node dstNode) {
		return 1;
	}
}
