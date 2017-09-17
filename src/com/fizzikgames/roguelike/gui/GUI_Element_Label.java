package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;

/**
 * A GUI Label is a dynamic or static text element.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class GUI_Element_Label extends GUI_Element {
	protected String label;
	protected UnicodeFont font;
	protected Color color;
	protected Color outlineColor;
	protected boolean centered;
	protected int outlineSize;
	
	public GUI_Element_Label(GUI_Container container, String id, String label, UnicodeFont font, Color color, Tooltip tooltip, 
			float x, float y, int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, x, y, renderPriority, visible, anchor);
		this.label = label;
		this.font = font;
		this.color = color;
		this.tooltip = tooltip;
		this.centered = false;
		this.outlineColor = null;
		this.outlineSize = 0;
	}
	
	public GUI_Element_Label(GUI_Container container, String id, String label, UnicodeFont font, Color color, Color outlineColor, 
			Tooltip tooltip, float x, float y, int outlineSize, boolean drawAtCenter, int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, x, y, renderPriority, visible, anchor);
		this.label = label;
		this.font = font;
		this.color = color;
		this.tooltip = tooltip;
		this.centered = drawAtCenter;
		this.outlineColor = outlineColor;
		this.outlineSize = outlineSize;
	}
	
	@Override
	public void update(GameContainer gc, GUI_Element caller) {
		if (visible()) {
			if ((caller == null
					|| Rectangle.contains(container.mousex(), container.mousey(), caller.x(), caller.y(), caller.getWidth(), caller.getHeight()))
					&& Rectangle.contains(container.mousex(), container.mousey(), x(), y(), font.getWidth(label), font.getHeight(label))) {
				setFocus(true);
			}
			else {
				setFocus(false);
			}
		}
	}
	
	@Override
	public void render(Graphics g) {
		if (visible()) {
			//Label
			int s = outlineSize;
			g.setFont(font);
			g.setColor(color);
			if (centered) {
				int fw = getWidth();
				float newx = x() - ((float)fw / 2);
				int fh = font.getLineHeight();
				float newy = y() - ((float)fh / 2);
				
				if (outlineColor != null) {
					g.setColor(outlineColor);
					g.drawString(label, newx - s, newy - s);
					g.drawString(label, newx - s, newy + s);
					g.drawString(label, newx + s, newy + s);
					g.drawString(label, newx + s, newy - s);
					g.drawString(label, newx + s, newy);
					g.drawString(label, newx - s, newy);
					g.drawString(label, newx, newy + s);
					g.drawString(label, newx, newy - s);
					g.setColor(color);
				}
				
				g.drawString(label, newx, newy);
			}
			else {
				if (outlineColor != null) {
					g.setColor(outlineColor);
					g.drawString(label, x() - s, y() - s);
					g.drawString(label, x() - s, y() + s);
					g.drawString(label, x() + s, y() + s);
					g.drawString(label, x() + s, y() - s);
					g.drawString(label, x() + s, y());
					g.drawString(label, x() - s, y());
					g.drawString(label, x(), y() + s);
					g.drawString(label, x(), y() - s);
					g.setColor(color);
				}
				
				g.drawString(label, x(), y());
			}
			
			if ((tooltip != null) && hasFocus()) container.addTooltipToRenderQueue(tooltip);
		}
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
	public UnicodeFont getFont() {
		return font;
	}
	
	public void setFont(UnicodeFont font) {
		this.font = font;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String s) {
		label = s;
	}
	
	public int getWidth() {
		return font.getWidth(label);
	}
	
	public int getHeight() {
		return font.getHeight(label);
	}
}
