package com.fizzikgames.roguelike.entity.mechanics.ability;

import java.util.Comparator;

import org.newdawn.slick.Image;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Activatable;

/**
 * An ability is an activatable (although sometimes it doesnt need to be activated) that performs context game actions.
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Ability extends Activatable {
	public enum TargetType {
		TargetEnemy("Target Enemy"),
		TargetSelf("Target Self"),
		Passive("Passive");
		
		private String type;
		
		private TargetType(String s) {
			type = s;
		}
		
		public String getType() {
			return type;
		}
	}
	
	protected GameCharacter gamechar;
	protected GameCharacter target;
	protected String name;
	protected String targetType;
	protected float range;
	protected boolean hidden;
	protected int targetrow;
	protected int targetcolumn;
	
	public Ability(GameCharacter gamechar, String name, Image iconImage, String targetType, int totalCooldown, int cost, float range, 
			boolean hidden) {
		super(iconImage, totalCooldown, 0, cost);
		this.gamechar = gamechar;
		this.target = null;
		this.name = name;
		this.targetType = targetType;
		this.range = range;
		this.hidden = hidden;
		this.targetrow = 0;
		this.targetcolumn = 0;
	}
	
	public abstract void activate();
	
	/**
	 * Returns a dynamic string giving the description of this ability.
	 * It is dynamic because things like damage are not always constant.
	 */
	public abstract String getDescription();
	public abstract boolean isValidTarget(GameCharacter target);
	public abstract boolean targetUsesLineOfSight();
	
	public String getName() {
		return name;
	}
	
	public String getTargetType() {
		return targetType;
	}
	
	public GameCharacter getOwner() {
		return gamechar;
	}
	
	public GameCharacter getTarget() {
		return target;
	}
	
	public void setTarget(GameCharacter target) {
		this.target = target;
	}
	
	public int getTargetRow() {
	    return targetrow;
	}
	
	public void setTargetRow(int r) {
	    targetrow = r;
	}
	
	public int getTargetColumn() {
	    return targetcolumn;
	}
	
	public void setTargetColumn(int c) {
	    targetcolumn = c;
	}
	
	public float getRange() {
		return range;
	}
	
	public void setRange(float range) {
		this.range = range;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public static final class NameComparator implements Comparator<Ability> {
		@Override
		public int compare(Ability a, Ability b) {
			return a.getName().compareTo(b.getName());
		}		
	}
	
	public static final class CostComparator implements Comparator<Ability> {
		@Override
		public int compare(Ability a, Ability b) {
			return (a.getCost() - b.getCost());
		}		
	}
	
	public static final class RangeComparator implements Comparator<Ability> {
		@Override
		public int compare(Ability a, Ability b) {
			return (int) (a.getRange() - b.getRange());
		}		
	}
	
	public static final class TotalCooldownComparator implements Comparator<Ability> {
		@Override
		public int compare(Ability a, Ability b) {
			return (a.getTotalCooldown() - b.getTotalCooldown());
		}		
	}
	
	public static final class CurrentCooldownComparator implements Comparator<Ability> {
		@Override
		public int compare(Ability a, Ability b) {
			return (a.getCurrentCooldown() - b.getCurrentCooldown());
		}		
	}
}
