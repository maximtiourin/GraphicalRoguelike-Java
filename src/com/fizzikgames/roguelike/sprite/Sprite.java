package com.fizzikgames.roguelike.sprite;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

import com.fizzikgames.roguelike.asset.AssetLoader;

/**
 * A sprite is a collection of animations/images in use with entities
 * @author Maxim Tiourin
 */
public abstract class Sprite {
	//Structure vars
	protected SpriteSheet spritesheet;
	protected ArrayList<AnimationReference> animationList;
	protected String reference;
	protected Animation currentAnimation;
	//Static info
	public static final int SPRITESHEET_WIDTH = 512;
	public static final int SPRITESHEET_HEIGHT = 512;
	public static final int WIDTH = 32;
	public static final int HEIGHT = 32;
	
	public Sprite(String spritesheet) {
		this.spritesheet = null;
		this.reference = "";
		
		SpriteSheet loadSheet = AssetLoader.spritesheet(spritesheet);
		if (loadSheet != null) {
			this.spritesheet = loadSheet;
		}
		else {
			Log.error("Unable to find spritesheet: " + spritesheet);
		}
		
		initAnimations();
	}
	
	/**
	 * Steps through the current animation
	 */
	public void step(int delta) {
		currentAnimation.update(delta);
	}
	
	/**
	 * Initializes the animations for the sprite, called by Sprite constructor.
	 */
	public abstract void initAnimations();
	
	/**
	 * Returns the x and y position of the image based on its cell id in the spritesheet
	 */
	public static Point getSpriteSheetPosition(int cellid) {
		//Quick case
		if (cellid == 0) return new Point(0, 0);
		
		int left = cellid;
		int x = 0, y = 0;
		
		while ((left > 0) && (Math.floor(SPRITESHEET_WIDTH / WIDTH) <= left)) {
			left -= Math.floor(SPRITESHEET_WIDTH / WIDTH);
			y++;
		}
		x = left;
		
		return new Point(x, y);
	}
	
	/**
	 * Updates the sprite's animation speed to the given speed
	 */
	public void updateSpeed(double speed) {
		for (AnimationReference e : animationList) {
			if (e.isAffectedByTime()) {
				Animation anim = e.getAnimation();
				
				int db[] = e.getDurationBase();
				
				for (int i = 0; i < anim.getFrameCount(); i++) {
					anim.setDuration(i, (int) Math.ceil(db[i] / speed)); 
				}
			}
		}
	}
	
	/**
	 * Returns the animation with the given reference.
	 */
	public Animation get(String reference) {
		for (AnimationReference e : animationList) {
			if (e.getReference().equals(reference)) {
				this.reference = reference;
				return e.getAnimation();
			}
		}
		
		return null;
	}
	
	/**
	 * Sets the sprites current animation
	 */
	public void current(String reference) {
		currentAnimation = get(reference);
	}
	
	public String reference() {
		return reference;
	}
	
	/**
	 * Returns the image of the current frame
	 */
	public Image image() {
		return currentAnimation.getCurrentFrame();
	}
	
	public SpriteSheet getSpriteSheet() {
		return spritesheet;
	}
	
	public void setSpriteSheet(SpriteSheet ss) {
		spritesheet = ss;
	}
}
