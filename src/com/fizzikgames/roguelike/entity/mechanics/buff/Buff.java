package com.fizzikgames.roguelike.entity.mechanics.buff;

import org.newdawn.slick.Image;

import com.fizzikgames.roguelike.entity.GameCharacter;

/**
 * A buff is a beneficial temporary Stat Modifier
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Buff extends TemporaryStatModifier {
	public Buff(GameCharacter gamechar, String reference, Image iconImage,
			int duration, int maxStacks, int currentStacks, boolean hidden) {
		super(gamechar, reference, iconImage, duration, maxStacks, currentStacks, hidden);
	}
	
	public Buff(GameCharacter gamechar, String reference, Image iconImage,
			int maxStacks, int currentStacks, boolean hidden) {
		super(gamechar, reference, iconImage, maxStacks, currentStacks, hidden);
	}
	
	@Override
	public boolean remove() {
	    return character.removeBuff(this, true);
	}
}
