package com.fizzikgames.roguelike.gui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item;

/**
 * An advanced tooltip is able to display different font colors, sizes, and faces as well as unique positions for
 * strings using anchor points to create an aesthetically pleasing tooltip.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class GUI_AdvancedTooltip implements Tooltip {	
	protected GUI_Element parent;
	protected ArrayList<Component> components;
	protected Color bgColor;
	protected Color borderColor;
	protected boolean aboveMouse;
	protected boolean fixed;
	protected boolean adjustUpwards;
	protected int anchorwidth;
	protected int anchorheight;
	protected int actualwidth;
	protected int actualheight;
	protected float x;
	protected float y;
	protected float fixx;
	protected float fixy;
	protected static final int MOUSEXOFF = 15;
	protected static final int MOUSEYOFF = 5;
	protected static final int OFFSET = 5;
	private static final int PAD = 0;
	
	public GUI_AdvancedTooltip(List<Component> components, int anchorwidth, int anchorheight, 
			Color bgColor, Color borderColor, boolean positionAboveMouse) {
		parent = null;
		this.components = (ArrayList<Component>) components;
		this.bgColor = bgColor;
		this.borderColor = borderColor;
		this.aboveMouse = positionAboveMouse;
		this.fixed = false;
		this.adjustUpwards = false;
		this.anchorwidth = anchorwidth;
		this.anchorheight = anchorheight;
		this.actualwidth = this.anchorwidth;
		this.actualheight = this.anchorheight;
		this.x = 0;
		this.y = 0;
		
		construct();
	}
	
	public GUI_AdvancedTooltip(List<Component> components, int anchorwidth, int anchorheight, 
			Color bgColor, Color borderColor, boolean fixed, boolean adjustUpwards, float x, float y) {
		parent = null;
		this.components = (ArrayList<Component>) components;
		this.bgColor = bgColor;
		this.borderColor = borderColor;
		this.aboveMouse = false;
		this.fixed = fixed;
		this.adjustUpwards = adjustUpwards;
		this.anchorwidth = anchorwidth;
		this.anchorheight = anchorheight;
		this.actualwidth = this.anchorwidth;
		this.actualheight = this.anchorheight;
		this.x = 0;
		this.y = 0;
		this.fixx = x;
		this.fixy = y;
		
		construct();
	}
	
	/**
	 * Determines the contents of the tooltip, and creates a true list of text components that are appropriately wrapped.
	 */
	public void construct() {
		
		float truex = x + OFFSET;
		float truey = y + OFFSET;
		int truew = anchorwidth - (2 * OFFSET);
		int trueh = anchorheight - (2 * OFFSET);
		
		float xmax = 0;
		float ymax = 0;
		//Start Test "Drawing" and determine the maxWidth and Height;
		for (Component c : components) {
			Vector2f pos = c.getPositionInRectangle(truex, truey, truew, trueh);
			int w = c.getWidth();
			int h = c.getHeight();
			xmax = Math.max(xmax, pos.getX() + w);
			ymax = Math.max(ymax, pos.getY() + h);
		}
		
		//Start "Drawing" using the determined maxWidth and Height;
		for (Component c : components) {
			Vector2f pos = c.getPositionInRectangle(truex, truey, (int) (xmax - truex), (int) (ymax - truey));
			c.drawx(pos.getX());
			c.drawy(pos.getY());
		}
		
		actualwidth = (int) (xmax - x);
		actualheight = (int) (ymax - y);
	}
	
	@Override
	public void render(Graphics g, float x, float y, int w, int h) {
		if (components != null && components.size() > 0) {
			int ww = GameLogic.WINDOW_WIDTH;
			int wh = GameLogic.WINDOW_HEIGHT;
			float mousex = parent.getContainer().mousex();
			float mousey = parent.getContainer().mousey();
			
			if (Rectangle.contains(mousex, mousey, x - PAD, y - PAD, w + (2 * PAD), h + (2 * PAD))) {
				int twidth = actualwidth + (2 * OFFSET);
				int theight = actualheight + (2 * OFFSET);
				float txoff;
				float tyoff;
				
				//Draw Fixed Tooltip, else draw nonfixed
				if (fixed) {
					txoff = this.fixx;
					tyoff = this.fixy - (adjustUpwards ? theight : 0);
					
					//Move tooltip within bounds.
					if ((txoff + twidth) > ww) {
						txoff = ww - twidth - OFFSET;
					}
					if (txoff < 0) {
						txoff = OFFSET;
					}
					if ((tyoff + theight) > wh) {
						tyoff = wh - theight - OFFSET;
					}
					if (tyoff < 0) {
						tyoff = OFFSET;
					}
					
					
					//g.setWorldClip(txoff, tyoff, twidth + 1, theight + 1);
					g.setLineWidth(1);
					g.setColor(bgColor);
					g.fillRect(txoff, tyoff, twidth, theight);
					g.setColor(borderColor);
					g.drawRect(txoff, tyoff, twidth, theight);
					
					//Draw text
					for (Component e : components) {
						e.render(g, txoff, tyoff);
					}
					
					//g.clearWorldClip();
				}
				else {
					//Move tooltip within bounds.
					txoff = mousex + MOUSEXOFF;
					if (aboveMouse) {
						tyoff = mousey - theight - MOUSEYOFF;
					}
					else {
						tyoff = mousey + (MOUSEYOFF * 4);
					}
					
					if ((txoff + twidth) > ww) {
						txoff = ww - twidth - OFFSET;
					}
					if (txoff < 0) {
						txoff = OFFSET;
					}
					if ((tyoff + theight) > wh) {
						tyoff = wh - theight - OFFSET;
					}
					if (tyoff < 0) {
						tyoff = OFFSET;
					}
					
					g.setWorldClip(txoff, tyoff, twidth + 1, theight + 1);
					g.setLineWidth(1);
					g.setColor(bgColor);
					g.fillRect(txoff, tyoff, twidth, theight);
					g.setColor(borderColor);
					g.drawRect(txoff, tyoff, twidth, theight);
					
					//Draw text
					for (Component e : components) {
						e.render(g, txoff, tyoff);
					}
					
					g.clearWorldClip();
				}
			}
		}
	}
	
	public GUI_Element getParent() {
		return parent;
	}
	
	public void setParent(GUI_Element parent) {
		this.parent = parent;
	}
	
	/**
	 * Creates an tooltip in the default ability tooltip style
	 */
	public static final GUI_AdvancedTooltip createAbilityTooltip(String name, String type, String desc, int cost, int cooldown, int targetRange) {
		ArrayList<Component> tcs = (ArrayList<Component>) Component.createStyleAbilityDescription(name, type, desc,	cost, cooldown, targetRange);
		
		return new GUI_AdvancedTooltip(tcs, 200, 300, new Color(0, 0, 0, 215), new Color(200, 200, 200, 225), true);
	}
	
	/**
	 * Creates an tooltip in the default item tooltip style
	 */
	public static final GUI_AdvancedTooltip createItemTooltip(Item item) {
		ArrayList<Component> tcs = (ArrayList<Component>) Component.createStyleItemDescription(item);
		
		return new GUI_AdvancedTooltip(tcs, 200, 300, new Color(0, 0, 0, 215), new Color(200, 200, 200, 225), false);
	}
	
	/**
	 * Creates an tooltip in the default stat tooltip style
	 */
	public static final GUI_AdvancedTooltip createStatTooltip(String description) {
		ArrayList<Component> tcs = (ArrayList<Component>) Component.createStyleStatDescription(description);
		
		return new GUI_AdvancedTooltip(tcs, 400, 25, new Color(0, 0, 0, 225), new Color(155, 0, 0, 225), true);
	}
	
	/**
	 * Creates an tooltip in the default fixed context style
	 */
	public static final GUI_AdvancedTooltip createFixedContextTooltip(Vector2f pos, boolean adjustUpwards, String description) {
		ArrayList<Component> tcs = (ArrayList<Component>) Component.createStyleFixedContextDescription(description);
		
		return new GUI_AdvancedTooltip(tcs, 200, 25, new Color(0, 0, 0, 180), new Color(75, 75, 75, 225), 
				true, adjustUpwards, pos.getX(), pos.getY());
	}
	
	/**
	 * Creates an tooltip in the default buff/debuff tooltip style
	 */
	public static final GUI_AdvancedTooltip createBuffDebuffTooltip(String description) {
		ArrayList<Component> tcs = (ArrayList<Component>) Component.createStyleBuffDebuffDescription(description);
		
		return new GUI_AdvancedTooltip(tcs, 200, 25, new Color(0, 0, 0, 180), new Color(200, 200, 200, 225), true);
	}
}
