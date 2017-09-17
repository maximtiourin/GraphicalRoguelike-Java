package com.fizzikgames.roguelike.sprite;

import org.newdawn.slick.Animation;

public class AnimationReference {
	private String reference;
	private Animation animation;
	private boolean affectedByTime; //If the animation speed should changeable
	private int durationBase[]; //The default durations of the frames in the animation
	
	public AnimationReference(String reference, Animation animation, boolean affectedByTime) {
		this.reference = reference;
		this.animation = animation;
		this.affectedByTime = affectedByTime;
		this.durationBase = animation.getDurations();
	}
	
	public String getReference() {
		return reference;
	}
	
	public Animation getAnimation() {
		return animation;
	}
	
	public boolean isAffectedByTime() {
		return affectedByTime;
	}
	
	public int[] getDurationBase() {
		return durationBase;
	}
}
