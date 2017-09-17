package com.fizzikgames.roguelike.sprite;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

public class ExtraTargetCursorSprite extends Sprite {
	private static final int CELL_0 = 0;
	private static final int CELL_1 = 1;
	private static final int CELL_2 = 2;

	public ExtraTargetCursorSprite(String spritesheet) {
		super(spritesheet);
	}

	@Override
	public void initAnimations() {
		animationList = new ArrayList<AnimationReference>();
		Image imageList[];
		int durationList[];
		Point pos;
		Animation anim;
		
		imageList = new Image[3];
		durationList = new int[3];		
		pos = getSpriteSheetPosition(CELL_0);
		imageList[0] = spritesheet.getSprite((int) pos.getX(), (int) pos.getY());
		durationList[0] = 300;
		pos = getSpriteSheetPosition(CELL_1);
		imageList[1] = spritesheet.getSprite((int) pos.getX(), (int) pos.getY());
		durationList[1] = 150;
		pos = getSpriteSheetPosition(CELL_2);
		imageList[2] = spritesheet.getSprite((int) pos.getX(), (int) pos.getY());
		durationList[2] = 150;
		anim = new Animation(imageList, durationList);		
		anim.setPingPong(true);
		animationList.add(new AnimationReference("targetcursor", anim, true));
	}
}
