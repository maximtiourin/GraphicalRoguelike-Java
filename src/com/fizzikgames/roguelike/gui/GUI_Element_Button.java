package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;

import com.fizzikgames.roguelike.GameLogic;

/**
 * A GUI button is a image that is clickable
 * @author Maxim Tiourin
 */
public class GUI_Element_Button extends GUI_Element_Image {
	public enum ClickType {
		LEFT, RIGHT, BOTH, NONE;
	}
	protected Sound clickSound;
	protected int width;
	protected int height;
	protected boolean holdleft;
	protected boolean holdright;
	protected boolean instantClick; //Whether or not the registers clicks instantly, or after cooldown wears off
	protected boolean hasDragDropTarget; //Whether or not this button is paired with a dragdroptarget
	protected boolean simulateClick;
	protected boolean animated;
	protected int cooldown;
	protected int animation;
	protected Image highlight;
	protected Image icon;
	protected Image foreground;
	protected Image select;
	protected ClickType clickType;
	public static final int COOLDOWN = 15;
	public static final int ANIMATION = 15;
	public static final int SHRINK = 2;
	
	public GUI_Element_Button(GUI_Container container, String id, Image bgimage, Image highlightimage, Image iconimage,
			Tooltip tooltip, Sound clickSound, boolean instantClick, ClickType clickType, float x, float y, int width, int height, 
			int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, bgimage, tooltip, x, y, 0, 0, renderPriority, visible, anchor);
		this.clickSound = clickSound;
		this.width = width;
		this.height = height;
		this.holdleft = false;
		this.holdright = false;
		this.instantClick = instantClick;
		this.hasDragDropTarget = false;
		this.simulateClick = false;
		this.animated = true;
		this.clickType = clickType;
		this.cooldown = 0;
		this.animation = 0;
		this.highlight = highlightimage;
		this.icon = iconimage;
		this.foreground = null;
		this.select = null;
	}
	
	public GUI_Element_Button(GUI_Container container, String id, Image bgimage, Image foregroundimage, Image highlightimage, Image iconimage, 
			Image selectimage, Tooltip tooltip, Sound clickSound, boolean instantClick, ClickType clickType, 
			float x, float y, int width, int height, int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, bgimage, tooltip, x, y, 0, 0, renderPriority, visible, anchor);
		this.clickSound = clickSound;
		this.width = width;
		this.height = height;
		this.holdleft = false;
		this.holdright = false;
		this.instantClick = instantClick;
		this.hasDragDropTarget = false;
		this.simulateClick = false;
		this.animated = true;
		this.clickType = clickType;
		this.cooldown = 0;
		this.animation = 0;
		this.highlight = highlightimage;
		this.icon = iconimage;
		this.foreground = foregroundimage;
		this.select = selectimage;
	}
	
	public GUI_Element_Button(GUI_Container container, String id, Image bgimage, Image foregroundimage, Image highlightimage, Image iconimage, 
			Image selectimage, Tooltip tooltip, Sound clickSound, boolean instantClick, boolean animated, ClickType clickType, 
			float x, float y, int width, int height, int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, bgimage, tooltip, x, y, 0, 0, renderPriority, visible, anchor);
		this.clickSound = clickSound;
		this.width = width;
		this.height = height;
		this.holdleft = false;
		this.holdright = false;
		this.instantClick = instantClick;
		this.hasDragDropTarget = false;
		this.simulateClick = false;
		this.animated = animated;
		this.clickType = clickType;
		this.cooldown = 0;
		this.animation = 0;
		this.highlight = highlightimage;
		this.icon = iconimage;
		this.foreground = foregroundimage;
		this.select = selectimage;
	}
	
	@Override
	public void update(GameContainer gc, GUI_Element caller) {
		if (!gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			holdleft = false;
		}
		if (!gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
			holdright = false;
		}
		
		if (visible()) {			
			if ((caller == null
					|| Rectangle.contains(container.mousex(), container.mousey(), caller.x(), caller.y(), caller.getWidth(), caller.getHeight()))
					&& Rectangle.contains(container.mousex(), container.mousey(), x(), y(), width, height)) {
				setFocus(true);
				
				if (!container.isDragging() && !isPairedWithDragDropTarget()) {
					if ((clickType == ClickType.LEFT || clickType == ClickType.BOTH) 
							&& gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) 
							&& !holdleft && (cooldown <= 0)) {
						//Left Click Allowed
						simulateClick = false;
						if (clickSound != null) {
							clickSound.play(1.00f, 1.00f);
						}
						if (instantClick) {
							notifyListeners();
						}
						holdleft = true;
						cooldown = COOLDOWN;
						animation = ANIMATION;
					}
					else if ((clickType == ClickType.RIGHT || clickType == ClickType.BOTH) 
							&& gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON) 
							&& !holdright && (cooldown <= 0)) {
						//Right Click Allowed
						simulateClick = false;
						if (clickSound != null) {
							clickSound.play(1.00f, 1.00f);
						}
						if (instantClick) {
							notifyListeners();
						}
						holdright = true;
						cooldown = COOLDOWN;
						animation = ANIMATION;
					}
				}
			}
			else {
				if (gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && !holdleft) {
					holdleft = true;
				}
				if (gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON) && !holdright) {
					holdright = true;
				}
				
				setFocus(false);
			}
		}
		
		if (cooldown > 0) {
			cooldown--;
			if (!instantClick && cooldown <= 0 && !simulateClick) {
				notifyListeners(); //Notify of click event soon as the cooldown wears off, so the user can see the button clicked before the action is performed
			}
		}
		
		if (animation > 0) {
			animation --;
		}
	}
	
	public void render(Graphics g) {
		if (visible()) {
			//Draw BG
			if (image != null) {
				if (animation > 0 && animated) {
					g.drawImage(image.getScaledCopy(width - (SHRINK * 2), height - (SHRINK * 2)), x() + SHRINK, y() + SHRINK);
				}
				else {
					g.drawImage(image.getScaledCopy(width, height), x(), y());
				}
			}
			
			//Draw Foreground
			if (foreground != null) {
				if (animation > 0 && animated) {
					g.drawImage(foreground.getScaledCopy(width - (SHRINK * 2), height - (SHRINK * 2)), x() + SHRINK, y() + SHRINK);
				}
				else {
					g.drawImage(foreground.getScaledCopy(width, height), x(), y());
				}
			}
			
			//Draw Select & Highlight
			if (animation > 0 && animated) {
				if (GameLogic.DEBUG) {
					g.setColor(Color.yellow);
					g.setLineWidth(2);
					g.drawRect(x() + SHRINK, y() + SHRINK, width - (SHRINK * 2), height - (SHRINK * 2));
					g.setLineWidth(1);
				}
				if (select != null) select.getScaledCopy(width - (SHRINK * 2), height - (SHRINK * 2)).draw(x() + SHRINK, y() + SHRINK);
			}
			else if (hasFocus()) {
				if (highlight != null) highlight.getScaledCopy(width , height).draw(x(), y());
			}
			
			//Draw Icon
			if (icon != null) {
				if (animation > 0 && animated) {
					g.drawImage(icon.getScaledCopy(width - (SHRINK * 2), height - (SHRINK * 2)), x() + SHRINK, y() + SHRINK);
				}
				else {
					g.drawImage(icon.getScaledCopy(width, height), x(), y());
				}
			}
			
			//Tooltip
			if ((tooltip != null) && hasFocus()) container.addTooltipToRenderQueue(tooltip);
		}
	}
	
	public void setBackgroundImage(Image image) {
		this.image = image;
	}
	
	public void setHighlightImage(Image image) {
		highlight = image;
	}
	
	public void setIconImage(Image image) {
		icon = image;
	}
	
	public void setForegroundImage(Image image) {
		foreground = image;
	}
	
	public void setSelectImage(Image image) {
		select = image;
	}
	
	/**
	 * Simulates a click, can be useful when filling out a form and consolidating operations between
	 * the player pressing enter, or clicking the submit button.
	 */
	public void simulateClick() {
		simulateClick = true;
		
		notifyListeners();
		
		cooldown = COOLDOWN;
		if (GameLogic.DEBUG) {
			System.out.println(getId() + " simulated clicked!");
		}
	}
	
	/**
	 * Like Simulate Click, but also plays animation, sound
	 */
	public void simulateFullClick() {
		simulateClick = true;
		
		if (clickSound != null) {
			clickSound.play(1.00f, 1.00f);
		}
		
		notifyListeners();
		
		if (GameLogic.DEBUG) {
			System.out.println(getId() + " full simulated clicked!");
		}
		cooldown = COOLDOWN;
		animation = ANIMATION;
	}
	
	public boolean isPairedWithDragDropTarget() {
		return hasDragDropTarget;
	}
	
	public void setPairedWithDragDropTarget(boolean b) {
		hasDragDropTarget = b;
	}
	
	public ClickType getClickType() {
		return clickType;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
