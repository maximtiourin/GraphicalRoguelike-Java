package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

import com.fizzikgames.roguelike.GameLogic;

/**
 * A GUI Image
 * @author Maxim Tiourin
 */
public class GUI_Element_Image extends GUI_Element {
	protected Image image;
	protected int width;
	protected int height;
	protected float xratio;
	protected float yratio;
	
	public GUI_Element_Image(GUI_Container container, String id, Image image, Tooltip tooltip, float x, float y, int width, int height, int renderPriority, 
			boolean visible, Anchor anchor) {
		super(container, id, x, y, renderPriority, visible, anchor);
		this.tooltip = tooltip;
		
		if (image != null) {
    		if (width == 0) {
    			this.width = image.getWidth();
    		}
    		else {
    			this.width = width;
    		}
    		if (height == 0) {
    			this.height = image.getHeight();
    		}
    		else {
    			this.height = height;
    		}
    		
    		xratio = (float)image.getWidth() / (float)this.width;
    		yratio = (float)image.getHeight() / (float)this.height;
    		
    		if ((image.getWidth() != this.width) || (image.getHeight() != this.height)) {
    			this.image = image.getScaledCopy(this.width, this.height);
    		}
    		else {
    			this.image = image.copy();
    		}
		}
		else {
		    this.image = null;
		    this.width = width;
		    this.height = height;
		}
	}
	
	@Override
	public void update(GameContainer gc, GUI_Element caller) {
		if (visible() && image != null) {
			if ((caller == null
					|| Rectangle.contains(container.mousex(), container.mousey(), caller.x(), caller.y(), caller.getWidth(), caller.getHeight()))
					&& Rectangle.contains(container.mousex(), container.mousey(), x(), y(), getWidth(), getHeight())) {
				setFocus(true);				
			}
			else {
				setFocus(false);
			}
		}
	}
	
	@Override
	public void render(Graphics g) {
		if (visible() && image != null) {
			g.drawImage(image, x(), y());
			
			//Draw debug border
			if (GameLogic.DEBUG) {
				g.setColor(Color.white);
				g.setLineWidth(1);
				g.drawRect(x(), y(), getWidth() - 1, getHeight() - 1);
			}
			
			if ((tooltip != null) && hasFocus()) container.addTooltipToRenderQueue(tooltip);
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setImage(Image i, boolean conform) {		
		if (conform) {
		    image = i.getScaledCopy(width, height);
		    image.setAlpha(i.getAlpha());
		}
		else {
		    image = i.getScaledCopy(1f);
		    width = image.getWidth();
		    height = image.getHeight();
		}
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
}
