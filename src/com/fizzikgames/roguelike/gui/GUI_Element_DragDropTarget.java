package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

import com.fizzikgames.roguelike.GameLogic;

/**
 * A Drag target is a gui element that can either initiate a container drag, or be the drop target of one.
 * It is in charge of checking all of the logic to see if it is a valid drag initiation or finish, and then 
 * calling the appropriate methods from it's container to actually perform the drag operations.
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class GUI_Element_DragDropTarget extends GUI_Element {
	private static final int PASS_DRAG_PRIORITY_LIMIT = 2; //ticks, average of (x * 10) ms
	protected GUI_Element_Button buttontarget;
	protected int width;
	protected int height;
	protected boolean holdleft;
	protected boolean holdright;
	protected boolean startDrag;
	protected boolean beingDragged;
	protected boolean highlighted;
	protected boolean swappable;
	protected float dragstartx;
	protected float dragstarty;
	protected float dragx;
	protected float dragy;
	protected Image dragImage;
	protected int passDragPriorityCounter;
	protected long passDragPriorityDebugTime;
	protected Object storage;
	
	public GUI_Element_DragDropTarget(GUI_Container container, String id, GUI_Element_Button buttontarget, boolean swappable, Image dragImage, 
			float x, float y, int width, int height, int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, x, y, renderPriority, visible, anchor);
		
		this.swappable = swappable;
		this.buttontarget = buttontarget;
		this.width = width;
		this.height = height;
		this.holdleft = false;
		this.holdright = false;
		this.startDrag = false;
		this.beingDragged = false;
		this.highlighted = false;
		this.dragstartx = 0;
		this.dragstarty = 0;
		this.dragx = 0;
		this.dragy = 0;
		this.dragImage = null;
		if (dragImage != null) {
			setDragImage(dragImage);
		}
		this.passDragPriorityCounter = 0;
		this.passDragPriorityDebugTime = 0;
		
		if (this.buttontarget != null) {
			this.buttontarget.setPairedWithDragDropTarget(true);
		}
	}
	
	/**
	 * Whether or not this Drag target should currently allow initiating a drag.
	 */
	public abstract boolean allowsStartDrag();
	/**
	 * Whether or not this Drag target should currently allow a drag to finish on it originating from the given drag target.
	 */
	public abstract boolean allowsFinishDragFrom(GUI_Element_DragDropTarget e);
	/**
	 * Notifies this dragtarget has finished dragging outside of this dragdroptarget and should perform context action acoordingly
	 */
	public abstract void finishDraggingContextActions(GUI_Element_DragDropTarget e, Object storage);
	/**
	 * Performs the custom operations that should occur when this target is the drop target of a valid drag target.
	 */
	public abstract void performValidFinishDragFrom(GUI_Element_DragDropTarget e);

	@Override
	public void update(GameContainer gc, GUI_Element caller) {
		if (!gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			holdleft = false;
		}
		if (!gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
			holdright = false;
		}
		
		if (visible) {
			if ((caller == null
					|| Rectangle.contains(container.mousex(), container.mousey(), caller.x(), caller.y(), caller.getWidth(), caller.getHeight()))
					&& Rectangle.contains(container.mousex(), container.mousey(), x(), y(), getWidth(), getHeight())) {
				//Check Start Drag
				if (dragImage != null && allowsStartDrag()) {
					if (!container.isDragging()) {
						if (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON) && !holdleft) {
							holdleft = true;
							startDrag = true;
							dragstartx = container.mousex();
							dragstarty = container.mousey();
							passDragPriorityCounter = PASS_DRAG_PRIORITY_LIMIT;
							
							if (GameLogic.DEBUG) passDragPriorityDebugTime = System.currentTimeMillis();
						}
						else if (gc.getInput().isMousePressed(Input.MOUSE_RIGHT_BUTTON) && !holdright) {
							holdright = true;
							if (buttontarget != null 
							        && (buttontarget.getClickType() == GUI_Element_Button.ClickType.RIGHT 
							        || buttontarget.getClickType() == GUI_Element_Button.ClickType.BOTH)) {
								buttontarget.simulateFullClick();
							}
						}
					}
				}
				
				if (startDrag) {
					if ((buttontarget != null) 
							&& (buttontarget.getClickType() == GUI_Element_Button.ClickType.LEFT 
							|| buttontarget.getClickType() == GUI_Element_Button.ClickType.BOTH) 
							&& (passDragPriorityCounter > 0) 
							&& !gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
						//Pass the left click to the button instead of dragging if we still have priority time
						startDrag = false;
						dragstartx = 0;
						dragstarty = 0;
						dragx = 0;
						dragy = 0;
						buttontarget.simulateFullClick();
					}
					else if ((buttontarget != null) 
							&& (buttontarget.getClickType() == GUI_Element_Button.ClickType.LEFT 
							|| buttontarget.getClickType() == GUI_Element_Button.ClickType.BOTH)
							&& !gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
							&& (Double.compare(dragstartx, container.mousex()) == 0 && Double.compare(dragstarty, container.mousey()) == 0)) {
						//Pass the left click to the button instead of dragging even if we dont have priority time
						//but the mouse hasnt moved since the initial left click.
						startDrag = false;
						dragstartx = 0;
						dragstarty = 0;
						dragx = 0;
						dragy = 0;
						buttontarget.simulateFullClick();
					}
					else if ((buttontarget == null || passDragPriorityCounter <= 0)
							&& gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
							&& (Double.compare(dragstartx, container.mousex()) != 0 || Double.compare(dragstarty, container.mousey()) != 0)){
						//Mouse has moved and conditions for passing priority were not met, initiate the drag
						startDrag = false;
						beingDragged = true;
						container.startDrag(this);
						dragx = container.mousex() - x();
						dragy = container.mousey() - y();
					}
				}
				
				if (container.isDragging() && !beingDragged) highlighted = true;
				
				setFocus(true);
			}
			else {
				if (gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && !holdleft) {
					holdleft = true;
				}
				if (gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON) && !holdright) {
					holdright = true;
				}
				
				if (highlighted) highlighted = false;
				
				setFocus(false);
			}
			
			if (!container.isDragging()) {
				if (beingDragged) {
						beingDragged = false;
						dragx = 0;
						dragy = 0;
				}
				
				if (highlighted) highlighted = false;
			}
		}
		
		if (passDragPriorityCounter > 0) {
			passDragPriorityCounter--;
		}
	}

	@Override
	public void render(Graphics g) {
		if (visible) {
			if (GameLogic.DEBUG) {
				g.setColor(Color.cyan);
				g.drawRect(x(), y(), getWidth(), getHeight());
			}
			
			if (beingDragged) {
				//if (GameLogic.DEBUG) {
					g.setColor(new Color(35, 35, 35, 185));
					g.fillRect(x() + 2, y() + 2, getWidth() - 4, getHeight() - 4);
				//}
				//Container draws the drag image
			}
			else if (highlighted) {
				if (GameLogic.DEBUG) {
					g.setColor(Color.white);
					g.drawRect(x() + 3, y() + 3, getWidth() - 6, getHeight() - 6);
					g.drawRect(x() + 5, y() + 5, getWidth() - 10, getHeight() - 10);
				}
			}
		}
	}
	
	/**
	 * Notifies that dragging has finish, and this should check where dragging finished and act accordingly.
	 */
	public void finishDragging(GUI_Element_DragDropTarget e, Object storage) {
		//Dragged anywhere outside of dragdroptarget
		if (!Rectangle.contains(container.mousex(), container.mousey(), x(), y(), getWidth(), getHeight())) {
			finishDraggingContextActions(e, storage);
		}
	}
	
	public Object getStorage() {
		return storage;
	}
	
	public void setStorage(Object o) {
		storage = o;
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	public float getDragX() {
		return dragx;
	}
	
	public float getDragY() {
		return dragy;
	}
	
	public Image getDragImage() {
		return dragImage;
	}
	
	public void setDragImage(Image image) {
		if (image == null) {
			dragImage = null;
		}
		else {
			dragImage = image.getScaledCopy(getWidth(), getHeight());
			dragImage.setAlpha(.5f);
		}
	}
	
	public boolean isSwappable() {
		return swappable;
	}
	
	public void setSwappable(boolean b) {
		swappable = b;
	}
	
	public GUI_Element_Button getButtonTarget() {
		return buttontarget;
	}
	
	/**
	 * Returns a deep copy of a drag drop target creating an almost identical but new version.
	 */
	public static GUI_Element_DragDropTarget getDeepCopy(GUI_Element_DragDropTarget e) {
		final GUI_Element_DragDropTarget efinal = e;
		GUI_Element_DragDropTarget copy = new GUI_Element_DragDropTarget(e.getContainer(), e.getId(), null, e.isSwappable(),
				e.getDragImage(), e.x() - e.getContainer().x(), e.y() - e.getContainer().y(), e.getWidth(), e.getHeight(), e.getRenderPriority(),
				e.isVisible(), e.anchor()) {
					@Override
					public boolean allowsStartDrag() {
						return efinal.allowsStartDrag();
					}

					@Override
					public boolean allowsFinishDragFrom(
							GUI_Element_DragDropTarget e) {
						return efinal.allowsFinishDragFrom(e);
					}

					@Override
					public void finishDraggingContextActions(
							GUI_Element_DragDropTarget e, Object storage) {
						efinal.finishDragging(e, storage);
					}

					@Override
					public void performValidFinishDragFrom(
							GUI_Element_DragDropTarget e) {
						efinal.performValidFinishDragFrom(e);
					}			
		};
		copy.setStorage(e.getStorage());
		
		return copy;
	}
}
