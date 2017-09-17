package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

/**
 * A Rollover Constraint is a GUI_Element that is paired with another GUI_Element. Whenever the rollover constraint has
 * focus within it's defined bounds, its partner element becomes visible and vice-versa.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class GUI_Element_RollOverConstraint extends GUI_Element {
	private int width;
	private int height;
	private GUI_Element element;
	
	public GUI_Element_RollOverConstraint(GUI_Container container, String id,
			GUI_Element element, float x, float y, int width, int height, int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, x, y, renderPriority, visible, anchor);
		this.width = width;
		this.height = height;
		this.element = element;
	}

	@Override
	public void update(GameContainer gc, GUI_Element caller) {
		if (visible()) {
			if ((caller == null
					|| Rectangle.contains(container.mousex(), container.mousey(), caller.x(), caller.y(), caller.getWidth(), caller.getHeight()))
					&& Rectangle.contains(container.mousex(), container.mousey(), x(), y(), getWidth(), getHeight())) {
				setFocus(true);				
			}
			else {
				setFocus(false);
			}
			
			element.setVisible(this.hasFocus());
		}
		else {
			element.setVisible(false);
		}
	}

	@Override
	public void render(Graphics g) {
		
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
}
