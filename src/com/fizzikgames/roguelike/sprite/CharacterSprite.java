package com.fizzikgames.roguelike.sprite;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

public class CharacterSprite extends Sprite {
	private static final int CELL_DEFAULT_LEFT = 0;
	private static final int CELL_DEFAULT = 1;
	private static final int CELL_DEFAULT_RIGHT = 2;
	
	public CharacterSprite(String spritesheet) {
		super(spritesheet);
		this.current("stance_default");
	}

	@Override
	public void initAnimations() {
		animationList = new ArrayList<AnimationReference>();
		Image imageList[];
		int durationList[];
		Point pos;
		Animation anim;
		
		//Default Stance
		imageList = new Image[3];
		durationList = new int[3];		
		pos = getSpriteSheetPosition(CELL_DEFAULT_LEFT);
		imageList[0] = spritesheet.getSprite((int) pos.getX(), (int) pos.getY());
		durationList[0] = 150;
		pos = getSpriteSheetPosition(CELL_DEFAULT);
		imageList[1] = spritesheet.getSprite((int) pos.getX(), (int) pos.getY());
		durationList[1] = 300;
		pos = getSpriteSheetPosition(CELL_DEFAULT_RIGHT);
		imageList[2] = spritesheet.getSprite((int) pos.getX(), (int) pos.getY());
		durationList[2] = 150;
		anim = new Animation(imageList, durationList);		
		anim.setPingPong(true);
		animationList.add(new AnimationReference("stance_default", anim, true));
	}
}
