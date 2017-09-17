package com.fizzikgames.roguelike.gui;

import java.util.Collections;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

/**
 * A GUI Vertical Scroll Area is similar to a composite element except it has a maximum width and height, and will clip everything outside of
 * those dimensions, but also allowing the user to scroll the clipping vertically.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class GUI_Element_VerticalScrollArea extends GUI_Element_Composite {
	protected Image bg;
	protected int maxHeight;
	protected float yoffset;
	protected GUI_Element_Button scrollup;
	protected GUI_Element_Button scrolldown;
	protected Image scrollbarbackground;
	protected Image scrollbarforeground;
	protected int sbd; //Scroll button dimensions
	protected float scrollbarHeight;
	protected float scrollbarOffset;
	protected boolean scrollbarDrag;
	protected boolean hold;
	protected float prevdragy;
	protected boolean scrollUpClicked;
	protected boolean scrollDownClicked;
	private static final int HEIGHT_PADDING = 2;
	private static final int SCROLL_AMOUNT = 20;
	private static final int SBD_PADDING = 4;
	
	/**
	 * scrollUp Image order: Normal, Highlight, Select
	 * scrollDown Image order: Normal, Highlight, Select
	 * scrollBar Image order: Background, Foreground
	 */
	public GUI_Element_VerticalScrollArea(GUI_Container container, String id, Image bg, float x, float y, int aWidth, int aHeight, 
			Image[] scrollUpImages, Image[] scrollDownImages, Image[] scrollBarImages, int scrollButtonDimension, 
			int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, null, x, y, renderPriority, visible, anchor);
		this.width = aWidth;
		this.height = aHeight;
		this.bg = bg;
		this.maxHeight = 0;
		this.yoffset = 0;
		this.sbd = scrollButtonDimension;
		this.scrollbarHeight = 0;
		this.scrollbarOffset = 0;
		this.scrollbarDrag = false;
		this.hold = false;
		this.prevdragy = 0;
		this.scrollUpClicked = false;
		this.scrollDownClicked = false;
		this.scrollbarbackground = scrollBarImages[0];
		this.scrollbarforeground = scrollBarImages[1];
		this.scrollup = new GUI_Element_Button(container, id + "_button_scrollup", scrollUpImages[0], null, 
				scrollUpImages[1], null, scrollUpImages[2], null, null, true, 
				GUI_Element_Button.ClickType.LEFT, x() + width - sbd, y(), sbd, sbd, GUI_Element.RENDER_BUTTON, true, null);
		this.scrolldown = new GUI_Element_Button(container, id + "_button_scrolldown", scrollDownImages[0], null,
				scrollDownImages[1], null, scrollDownImages[2], null, null, true, 
				GUI_Element_Button.ClickType.LEFT, x() + width - sbd, y() + height - sbd, sbd, sbd, GUI_Element.RENDER_BUTTON, true, null);
		//Check scroll clicks
		this.scrollup.addListener(new ElementListener(){
			@Override
			public void elementActionPerformed() {
				scrollUpClicked = true;
			}					
		});
		this.scrolldown.addListener(new ElementListener(){
			@Override
			public void elementActionPerformed() {
				scrollDownClicked = true;
			}					
		});
	}
	
	public void updateElementPositions(float oldyoffset) {		
		//Update Elements
		if ((Math.abs(oldyoffset) - Math.abs(yoffset)) != 0) {
			for (GUI_Element e : elements) {
				e.addY(-(yoffset - oldyoffset));
			}
		}
	}
	
	@Override
	public void update(GameContainer gc, GUI_Element caller) {
		if (!gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			hold = false;
			scrollbarDrag = false;
		}
		
		if (visible() && (elements.size() > 0)) {
			float oldyoffset = yoffset;
			
			//Update scroll buttons
			scrollup.update(gc, this);
			scrolldown.update(gc, this);
			
			//Update Bar height
			scrollbarHeight = ((float) height / (float) maxHeight) * (height - (sbd * 2));
			scrollbarOffset = (yoffset / (float) maxHeight) * (height - (sbd * 2));
			
			if ((caller == null
					|| Rectangle.contains(container.mousex(), container.mousey(), caller.x(), caller.y(), caller.getWidth(), caller.getHeight()))
					&& Rectangle.contains(container.mousex(), container.mousey(), x(), y(), width, height)) {
				setFocus(true);
				
				//Check mousewheel
				int checkWheel = container.getMouseWheelState();
				
				if (checkWheel < 0) {
					if ((maxHeight - yoffset > height)) {
						//Allow scrolling down
						yoffset += Math.min(SCROLL_AMOUNT, maxHeight - (yoffset + height));
					}
				}
				else if (checkWheel > 0) {
					if (yoffset > 0) {
						//Allow scrolling up
						yoffset -= Math.min(SCROLL_AMOUNT, yoffset);
					}
				}
				
				//Check volatile clicks
				if (scrollUpClicked && yoffset > 0) {
					yoffset -= Math.min(SCROLL_AMOUNT, yoffset);
					scrollUpClicked = false;
				}
				else {
					scrollUpClicked = false;
				}
				if (scrollDownClicked && (maxHeight - yoffset > height)) {
					yoffset += Math.min(SCROLL_AMOUNT, maxHeight - (yoffset + height));
					scrollDownClicked = false;
				}
				else {
					scrollDownClicked = false;
				}
				
				//Check scroll drag
				if (gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
						&& !Rectangle.contains(container.mousex(), container.mousey(), x() + width - sbd + SBD_PADDING, y() + sbd + scrollbarOffset, sbd - (SBD_PADDING * 2), scrollbarHeight)) {
					hold = true;
				}
				else if (gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
						&& !hold
						&& Rectangle.contains(container.mousex(), container.mousey(), x() + width - sbd + SBD_PADDING, y() + sbd + scrollbarOffset, sbd - (SBD_PADDING * 2), scrollbarHeight) 
						&& !scrollbarDrag) {
					scrollbarDrag = true;
					hold = true;
					prevdragy = container.mousey();
				}
				else if (!gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && scrollbarDrag) {
					scrollbarDrag = false;
				}
				
				//Update scrollbar drag position
				if (scrollbarDrag) {
					float soff = (container.mousey() - prevdragy);
					
					float scrollAmount = (int) (Math.abs(soff) / ((float) height / (float) maxHeight));
					
					soff = Math.abs(soff) / soff; //Get unit velocity
					
					if (soff > 0) {						
						if ((maxHeight - yoffset > height)) {
							//Allow scrolling down
							yoffset += Math.min(scrollAmount, maxHeight - (yoffset + height));
						}
					}
					else if (soff < 0) {
						if (yoffset > 0) {
							//Allow scrolling up
							yoffset -= Math.min(scrollAmount, yoffset);
						}
					}
					
					prevdragy = container.mousey();
				}
				
				//Update Element positions
				updateElementPositions(oldyoffset);
			}
			else {
				if (gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && !hold) {
					hold = true;
				}
				else if (scrollbarDrag) {
					//Update scrollbar drag position
					if (scrollbarDrag) {
						float soff = (container.mousey() - prevdragy);
						
						float scrollAmount = (int) (Math.abs(soff) / ((float) height / (float) maxHeight));
						
						soff = Math.abs(soff) / soff; //Get unit velocity
						
						if (soff > 0) {						
							if ((maxHeight - yoffset > height)) {
								//Allow scrolling down
								yoffset += Math.min(scrollAmount, maxHeight - (yoffset + height));
								updateElementPositions(oldyoffset);
							}
						}
						else if (soff < 0) {
							if (yoffset > 0) {
								//Allow scrolling up
								yoffset -= Math.min(scrollAmount, yoffset);
								updateElementPositions(oldyoffset);
							}
						}
						
						prevdragy = container.mousey();
					}
				}
				
				setFocus(false);				
				for (GUI_Element e : elements) {
					//Clear focus for elements in bounds when the area doesnt have focus
					if (GUI_Element.inBounds(e, new Rectangle(x(), y(), width, height))) {
						if (!(e instanceof GUI_Element_TextBox)) e.setFocus(false);
						else e.update(gc, this);
					}
				}
			}
			
			for (GUI_Element e : elements) {
				//Update all elements in viewbounds
				if (GUI_Element.inBounds(e, new Rectangle(x(), y(), width, height))) e.update(gc, this);
			}
		}
	}
	
	@Override
	public void render(Graphics g) {
		if (visible()) {
			g.setColor(Color.white);
			
			if (bg != null) bg.getScaledCopy(width, height).draw(x(), y());
			
			//Draw Elements and scrollbar if we have any elements.
			if (elements.size() > 0) {
				g.setWorldClip((int) x(), (int) y(), width, height);
				for (GUI_Element e : elements) {
					if (GUI_Element.inBounds(e, new Rectangle(x(), y(), width, height))) e.render(g);
				}
				g.clearWorldClip();
				
				scrollup.render(g);
				scrolldown.render(g);
				
				//Draw scrollbar
					//background
				g.drawImage(scrollbarbackground.getScaledCopy(sbd - (SBD_PADDING * 2), height - (sbd * 2)), 
						x() + width - sbd + SBD_PADDING, y() + sbd);
					//foreground
				g.drawImage(scrollbarforeground.getScaledCopy(sbd - (SBD_PADDING * 2) + 2, (int) Math.floor(scrollbarHeight)), 
						x() + width - sbd + SBD_PADDING - 1, y() + sbd + scrollbarOffset);
				
				//Draw Debug rectangle
				/*g.setColor(Color.white);
				g.drawRect(x(), y(), width, maxHeight);
				g.setColor(Color.yellow);
				g.drawRect(x(), y() + yoffset, width, height);*/
			}
		}
	}
	
	@Override
	public void addElement(GUI_Element e) {
		elements.add(e);
		Collections.sort(elements);
		
		calculateMaxHeight();
	}
	
	public void clearElements() {
		elements.clear();
		yoffset = 0;
		calculateMaxHeight();
	}
	
	@Override
	public boolean removeElement(String id) {
		if (elements.size() <= 0) return false;
		for (GUI_Element e : elements) {
			if (id.equals(e.getId())) {
				elements.remove(e);
				
				calculateMaxHeight();
				
				return true;
			}
		}
		
		return false;
	}
	
	private void calculateMaxHeight() {
		maxHeight = height;
		for (GUI_Element e : elements) {
			int th = (int) (e.y() + e.getHeight() - y());
			maxHeight = Math.max(maxHeight, th) + HEIGHT_PADDING;
		}
	}
}
