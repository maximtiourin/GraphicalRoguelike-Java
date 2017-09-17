package com.fizzikgames.roguelike.entity;

import java.util.Comparator;

import org.newdawn.slick.GameContainer;

import com.fizzikgames.roguelike.pathfinding.Node;

/**
 * Game Entity
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Entity {
    public enum RenderPriority {
        Default(0), Trap(10), Shrine(20), Chest(30), Item(40), GameCharacter(50), PlayerCharacter(999), TargetCursor(1000), FloatingContextStack(1001);
        
        private int priority;
        
        private RenderPriority(int priority) {
            this.priority = priority;
        }
        
        public int getPriority() {
            return priority;
        }
    }
	private int row;
	private int column;
	private int renderPriority;
	
	public Entity() {
		row = 0;
		column = 0;
		renderPriority = RenderPriority.Default.getPriority();
	}
	
	public Entity(int r, int c) {
		row = r;
		column = c;
		renderPriority = RenderPriority.Default.getPriority();
	}
	
	public abstract void update(GameContainer gc, int delta);
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
	public void setRow(int r) {
		row = r;
	}
	
	public void setColumn(int c) {
		column = c;
	}
	
	public Node getPositionAsNode() {
		return new Node(null, getColumn(), getRow());
	}
	
	public int getRenderPriority() {
	    return renderPriority;
	}
	
	public void setRenderPriority(int renderPriority) {
	    this.renderPriority = renderPriority;
	}
	
	public static final class RenderPriorityComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity a, Entity b) {
            return a.getRenderPriority() - b.getRenderPriority();
        }	    
	}
}
