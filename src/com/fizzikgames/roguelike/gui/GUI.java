package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import com.fizzikgames.roguelike.Event;
import com.fizzikgames.roguelike.Renderable;

public abstract class GUI extends Event implements Renderable {
	protected GUI_Container container;
	
	public GUI() {
		container = null;
	}
	
	protected abstract void init();
	
	public void update(GameContainer gc) {
		container.update(gc, null);
	}
	
	public void render(GameContainer gc, Graphics g) {
		container.render(g);
		
		for (Tooltip e : container.getTooltipRenderQueue()) {
			e.render(g, e.getParent().x(), e.getParent().y(), e.getParent().getWidth(), e.getParent().getHeight());
		}
		
		container.getTooltipRenderQueue().clear();
	}
	
	public GUI_Container getContainer() {
		return container;
	}
	
	public boolean hasFocus() {
		if (container.isVisible() && container.hasFocus()) return true;
		return false;
	}
	
	public boolean hasKeyFocus() {
		return container.hasKeyFocus();
	}
	
	public void setMouseWheelMoved(int change) {
		container.setMouseWheelMoved(change);
	}
}
