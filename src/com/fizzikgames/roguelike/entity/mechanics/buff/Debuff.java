package com.fizzikgames.roguelike.entity.mechanics.buff;

import org.newdawn.slick.Image;

import com.fizzikgames.roguelike.entity.GameCharacter;

/**
 * A debuff is a detrimental temporary Stat Modifier
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Debuff extends TemporaryStatModifier {
	public Debuff(GameCharacter gamechar, String reference, Image iconImage,
			int duration, int maxStacks, int currentStacks, boolean hidden) {
		super(gamechar, reference, iconImage, duration, maxStacks, currentStacks, hidden);
	}
	
	public Debuff(GameCharacter gamechar, String reference, Image iconImage,
			int maxStacks, int currentStacks, boolean hidden) {
		super(gamechar, reference, iconImage, maxStacks, currentStacks, hidden);
	}
	
	@Override
    public boolean remove() {
        return character.removeDebuff(this, true);
    }
}
