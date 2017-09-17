package com.fizzikgames.roguelike.gui;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.util.StringUtil;

/**
 * A GUI Tooltip is textual information that can be displayed when an element is hovered over.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class GUI_Tooltip implements Tooltip {
	protected GUI_Element parent;
	protected String text;
	protected ArrayList<String> wraptext;
	protected UnicodeFont font;
	protected int maxWidth;
	protected Color bgColor;
	protected Color borderColor;
	protected Color textColor;
	protected boolean wraps;
	protected boolean aboveMouse;
	protected boolean fixed;
	protected boolean adjustUpwards;
	protected float x;
	protected float y;
	protected static final int MOUSEXOFF = 15;
	protected static final int MOUSEYOFF = 5;
	protected static final int OFFSET = 5;
	protected static final int PAD = 0;
	
	public GUI_Tooltip(String text, UnicodeFont font, int maxWidth, Color bgColor, Color borderColor, Color textColor, boolean positionAboveMouse) {
		this.text = text;
		this.font = font;
		this.maxWidth = maxWidth;
		this.bgColor = bgColor;
		this.borderColor = borderColor;
		this.textColor = textColor;
		this.aboveMouse = positionAboveMouse;
		this.fixed = false;
		this.adjustUpwards = false;
		this.x = 0;
		this.y = 0;
		wraptext = new ArrayList<String>();
		setWrapping(true);
	}
	
	public GUI_Tooltip(String text, UnicodeFont font, int maxWidth, Color bgColor, Color borderColor, Color textColor, 
			boolean fixed, boolean adjustUpwards, float x, float y) {
		this.text = text;
		this.font = font;
		this.maxWidth = maxWidth;
		this.bgColor = bgColor;
		this.borderColor = borderColor;
		this.textColor = textColor;
		this.aboveMouse = false;
		this.fixed = fixed;
		this.adjustUpwards = adjustUpwards;
		this.x = x;
		this.y = y;
		wraptext = new ArrayList<String>();
		setWrapping(true);
	}
	
	/**
	 * Renders the tooltip.
	 * @param g the graphics context
	 * @param x the x position of the parent
	 * @param y the y position of the parent
	 * @param w the width of the parent
	 * @param h the height of the parent
	 */
	public void render(Graphics g, float x, float y, int w, int h) {
		if (!text.equals("")) {
			int wwidth = GameLogic.WINDOW_WIDTH;
			int wheight = GameLogic.WINDOW_HEIGHT;
			float mousex = parent.getContainer().mousex();
			float mousey = parent.getContainer().mousey();
			int lwidth = w;
			int lheight = h;
			
			if (Rectangle.contains(mousex, mousey, x - PAD, y - PAD, lwidth + (2 * PAD), lheight + (2 * PAD))) {
				int twidth = highestWrapWidth() + (2 * OFFSET);
				int theight = (wraptext.size() * font.getHeight(text)) + (2 * OFFSET);
				float txoff;
				float tyoff;
				
				//Draw Fixed Tooltip, else draw nonfixed
				if (fixed) {
					txoff = this.x;
					tyoff = this.y - (adjustUpwards ? theight : 0);
					
					//Move tooltip within bounds.
					if ((txoff + twidth) > wwidth) {
						txoff = wwidth - twidth - OFFSET;
					}
					if (txoff < 0) {
						txoff = OFFSET;
					}
					if ((tyoff + theight) > wheight) {
						tyoff = wheight - theight - OFFSET;
					}
					if (tyoff < 0) {
						tyoff = OFFSET;
					}
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
					
					if ((txoff + twidth) > wwidth) {
						txoff = wwidth - twidth - OFFSET;
					}
					if (txoff < 0) {
						txoff = OFFSET;
					}
					if ((tyoff + theight) > wheight) {
						tyoff = wheight - theight - OFFSET;
					}
					if (tyoff < 0) {
						tyoff = OFFSET;
					}
				}
				
				g.setLineWidth(1);
				g.setColor(bgColor);
				g.fillRect(txoff, tyoff, twidth, theight);
				g.setColor(borderColor);
				g.drawRect(txoff, tyoff, twidth, theight);
				
				//Draw text		
				int i = 0;
				for (String e : wraptext) {
					font.drawString(txoff + OFFSET, tyoff + i + OFFSET, e, textColor);

					i += font.getHeight(text);
				}
			}
		}
	}
	
	public void setWrapping(boolean b) {
		final String token = " ";
		
		wraps = b;
		
		wraptext.clear();
		if (wraps) {
			//Tokenize lines so that each line fits the maximum width;
			String str = text;
			boolean fail = false;
			
			while ((font.getWidth(str) > maxWidth) && (!fail)) {
				int i = str.length() - 1;
				
				while ((i > 0) && (font.getWidth(StringUtil.substring(str, 0, i, true)) > maxWidth)) {
					i--;
				}
				
				if (i == 0) {
					undoWrapping();
					fail = true;
				}
				else {
					int pos = StringUtil.firstOccurenceBeforePos(str, token, i);
					
					wraptext.add(StringUtil.substring(str, 0, pos, true));
					str = StringUtil.substring(str, pos + 1, str.length(), true);
				}
			}
			wraptext.add(str);
		}
		else {
			undoWrapping();
		}
	}
	
	private void undoWrapping() {
		wraptext.clear();
		wraptext.add(text);
	}
	
	private int highestWrapWidth() {
		int width = 0;
		
		for (String e : wraptext) {
			int w = font.getWidth(e);
			if (w > width) width = w;
		}
		
		return width;
	}
	
	public GUI_Element getParent() {
		return parent;
	}
	
	public void setParent(GUI_Element parent) {
		this.parent = parent;
	}
}
