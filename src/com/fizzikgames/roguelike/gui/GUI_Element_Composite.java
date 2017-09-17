package com.fizzikgames.roguelike.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * A GUI Composite is a combination of elements layered on one another to produce a dynamic element, but without the overhead functionality
 * of a GUI container.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class GUI_Element_Composite extends GUI_Element {
	protected ArrayList<GUI_Element> elements;
	protected int width;
	protected int height;
	
	public GUI_Element_Composite(GUI_Container container, String id, Tooltip tooltip, float x, float y, int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, x, y, renderPriority, visible, anchor);
		this.elements = new ArrayList<GUI_Element>();
		this.tooltip = tooltip;
		this.width = 0;
		this.height = 0;
	}
	
	@Override
	public void update(GameContainer gc, GUI_Element caller) {
		if (visible()) {
			setFocus(false);
			for (GUI_Element e : elements) {
				e.update(gc, caller);
				if (e.hasFocus()) setFocus(true);
			}
		}
	}
	
	@Override
	public void render(Graphics g) {
		if (visible()) {			
			for (GUI_Element e : elements) {
				e.render(g);
			}
			
			if ((tooltip != null) && hasFocus()) container.addTooltipToRenderQueue(tooltip);
		}
	}
	
	public void addElement(GUI_Element e) {
		elements.add(e);
		Collections.sort(getElements());
		
		calculateWidthHeight();
	}
	
	/**
	 * Returns the element with the given id, or null if it wasn't found.
	 */
	public GUI_Element getElement(String id) {
		if (elements.size() <= 0) return null;
		for (GUI_Element e : elements) {
			if (id.equals(e.getId())) {
				return e;
			}
		}
		
		return null;
	}
	
	public List<GUI_Element> getElements() {
		return elements;
	}
	
	/**
	 * Removes the element with the given id, or returns false if no element found.
	 */
	public boolean removeElement(String id) {
		if (elements.size() <= 0) return false;
		for (GUI_Element e : elements) {
			if (id.equals(e.getId())) {
				elements.remove(e);
				
				calculateWidthHeight();
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Sets the element with the id to the given visibility, returns false if no element with the id is found.
	 */
	public boolean setElementVisible(String id, boolean visibility) {
		for (GUI_Element e : elements) {
			if (id.equals(e.getId())) {
				e.setVisible(visibility);
				return true;
			}
		}
		
		return false;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	protected void calculateWidthHeight() {
		for (GUI_Element e : elements) {
			int tw = (int) (e.x() + e.getWidth() - x());
			width = Math.max(width, tw);
			int th = (int) (e.y() + e.getHeight() - y());
			height = Math.max(height, th);
		}
	}
}
