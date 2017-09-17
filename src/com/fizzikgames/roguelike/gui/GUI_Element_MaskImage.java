package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

/**
 * A Mask Image is just an image that has a rectangular clip of where the image is actually drawn.
 * I.E. a 200 width health bar image only drawing from 0->100 width when the player has 50% hp.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class GUI_Element_MaskImage extends GUI_Element_Image {
	private float maskx;
	private float masky;
	private int maskwidth;
	private int maskheight;
	
	public GUI_Element_MaskImage(GUI_Container container, String id, Image image, Tooltip tooltip, float x, float y, int width, int height, 
			float maskx, float masky, int maskwidth, int maskheight, int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, image, tooltip, x, y, width, height, renderPriority, visible, anchor);
		
		this.maskx = maskx;
		this.masky = masky;
		this.maskwidth = maskwidth;
		this.maskheight = maskheight;
	}
	
	public GUI_Element_MaskImage(GUI_Container container, String id, Image image, Tooltip tooltip, float x, float y, int width, int height, 
			int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, image, tooltip, x, y, width, height, renderPriority, visible, anchor);
		
		this.maskx = x();
		this.masky = y();
		this.maskwidth = getWidth();
		this.maskheight = getHeight();
	}
	
	@Override
	public void update(GameContainer gc, GUI_Element caller) {
	    if (visible()) {
	        if ((caller == null
                    || Rectangle.contains(container.mousex(), container.mousey(), caller.x(), caller.y(), caller.getWidth(), caller.getHeight()))
                    && Rectangle.contains(container.mousex(), container.mousey(), maskx, masky, maskwidth, maskheight)) {
                setFocus(true);             
            }
            else {
                setFocus(false);
            }
	    }
	}
	
	@Override
	public void render(Graphics g) {
		if (visible) {
			g.setWorldClip(maskx, masky, maskwidth, maskheight);
			super.render(g);
			g.clearWorldClip();
		}
	}
	
	public float getMaskX() {
		return maskx;
	}
	
	public void setMaskX(float ax) {
		maskx = ax;
	}
	
	public float getMaskY() {
		return masky;
	}
	
	public void setMaskY(float ay) {
		masky = ay;
	}
	
	public int getMaskWidth() {
		return maskwidth;
	}
	
	public void setMaskWidth(int w) {
		maskwidth = w;
	}
	
	public int getMaskHeight() {
		return maskheight;
	}
	
	public void setMaskHeight(int h) {
		maskheight = h;
	}
}
