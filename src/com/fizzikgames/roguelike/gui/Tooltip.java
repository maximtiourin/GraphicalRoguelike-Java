package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.Graphics;

public interface Tooltip {
	public void render(Graphics g, float x, float y, int w, int h);
	public GUI_Element getParent();
	public void setParent(GUI_Element parent);
}
