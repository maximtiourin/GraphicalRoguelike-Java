package com.fizzikgames.roguelike.gui;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

import com.fizzikgames.roguelike.GameLogic;

/**
 * A GUI_Container holds a list of elements, and does operations on them to ease with rendering and updating.
 * @author Maxim Tiourin
 */
public class GUI_Container extends GUI_Element_Composite {
	protected ArrayList<Tooltip> tooltipRenderQueue;
	protected KeyFocus keyFocus;
	protected int mouseWheelMoved;
	protected float mousex;
	protected float mousey;
	protected ArrayList<GUI_Element_DragDropTarget> dragDropTargets;
	protected GUI_Element_DragDropTarget dragSource;
	protected boolean dragging;
	
	public GUI_Container(String id, float x, float y, int width, int height) {
		super(null, id, null, x, y, GUI_Element.RENDER_CONTAINER, false, null);
		this.width = width;
		this.height = height;
		tooltipRenderQueue = new ArrayList<Tooltip>();
		mouseWheelMoved = 0;
		keyFocus = null;
		dragDropTargets = new ArrayList<GUI_Element_DragDropTarget>();
		dragSource = null;
		dragging = false;
	}
	
	public GUI_Container(GUI_Container container, String id, Tooltip tooltip, float x, float y, int width, int height, int renderPriority, boolean visible, Anchor anchor) {
		super(container, id, tooltip, x, y, renderPriority, visible, anchor);
		this.width = width;
		this.height = height;
		tooltipRenderQueue = new ArrayList<Tooltip>();
		mouseWheelMoved = 0;
		keyFocus = null;
		dragDropTargets = new ArrayList<GUI_Element_DragDropTarget>();
		dragSource = null;
		dragging = false;
	}
	
	@Override
	public void update(GameContainer gc, GUI_Element caller) {
		if (visible) {
			if (container == null) {
				mousex = gc.getInput().getMouseX();
				mousey = gc.getInput().getMouseY();
				
				//Dragging
				if (isDragging()) {
					if (!gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
						//Find where the mouse was released
						GUI_Element_DragDropTarget target = findDragDropTargetAtPosition(mousex(), mousey());
						if (target != null && target.visible()) {
							GUI_Element_DragDropTarget targetCopy = GUI_Element_DragDropTarget.getDeepCopy(target);
							Object storage = target.getStorage();
							finishDrag(target);
							dragSource.finishDragging(targetCopy, storage);
							dragSource = null;
							dragging = false;
						}
						else {
							if (GameLogic.DEBUG) System.out.println("Dragging Finished, but no drop target found at mouse: " + mousex() + ", " + mousey());
							dragSource.finishDragging(null, null);
							dragging = false;
							dragSource = null;
						}
					}
				}
			}
					
			super.update(gc, caller);
		}
		
		if (container == null) mouseWheelMoved = 0;
	}
	
	@Override
	public void render(Graphics g) {
		if (visible) {
			super.render(g);
			
			if (isDragging() && dragSource != null && dragSource.getDragImage() != null) {
				g.drawImage(dragSource.getDragImage(), mousex() - dragSource.getDragX(), mousey() - dragSource.getDragY());
			}
		}
	}
	
	/**
	 * Start dragging from the given dragtarget
	 */
	public void startDrag(GUI_Element_DragDropTarget e) {
		if (container == null) {
			//Determine Source
			if (!dragging) {
				dragSource = e;
				dragging = true;
			}
		}
		else {
			container.startDrag(e);
		}
	}
	
	/**
	 * Finish dragging on the given droptarget
	 */
	public void finishDrag(GUI_Element_DragDropTarget e) {
		if (container == null) {
			//Determine Target
			if (dragging) {
				if (e.allowsFinishDragFrom(dragSource)) {
					e.performValidFinishDragFrom(dragSource);
				}
			}
		}
		else {
			container.startDrag(e);
		}
	}
	
	/**
	 * Returns the dragdrop target at the given position using the dragDropTargets of all parent containers of this container
	 * if it has any.
	 */
	public GUI_Element_DragDropTarget findDragDropTargetAtPosition(float x, float y) {
		for (GUI_Element_DragDropTarget e : dragDropTargets) {
			if (e.isVisible() && Rectangle.contains(x, y, e.x(), e.y(), e.getWidth(), e.getHeight())) {
				return e;
			}
		}
		
		if (container != null) {
			return container.findDragDropTargetAtPosition(x, y);
		}
		
		return null;
	}
	
	public boolean isDragging() {
		if (container == null) {
			return dragging;
		}
		else {
			return container.isDragging();
		}
	}
	
	public float mousex() {
		if (container == null) {
			return mousex;
		}
		else {
			return container.mousex();
		}
	}
	
	public float mousey() {
		if (container == null) {
			return mousey;
		}
		else {
			return container.mousey();
		}
	}
	
	public boolean hasFocus() {
		for (GUI_Element e : elements) {
			if (e.hasFocus() && e.isVisible()) return true;
		}
		
		return false;
	}
	
	public boolean hasKeyFocus() {		
		if (container == null) {
			if (getKeyFocus() == null) return false;
			else if (getKeyFocus().isVisible()) return true;
			return false;
		}
		else {
			return container.hasKeyFocus();
		}
	}
	
	public KeyFocus getKeyFocus() {
		if (container == null) {
			return keyFocus;
		}
		else {
			return container.getKeyFocus();
		}
	}
	
	public void changeKeyFocus(KeyFocus e) {
		if (container == null) {
			if (getKeyFocus() != null) {
				if (getKeyFocus() instanceof GUI_Element_TextBox) {
					((GUI_Element_TextBox) getKeyFocus()).removeKeyFocus();
				}
			}
			
			keyFocus = e;
		}
		else {
			container.changeKeyFocus(e);
		}
	}
	
	public ArrayList<Tooltip> getTooltipRenderQueue() {
		if (container == null) {
			return tooltipRenderQueue;
		}
		else {
			return container.getTooltipRenderQueue();
		}
	}
	
	public void addTooltipToRenderQueue(Tooltip t) {
		if (container == null) {
			tooltipRenderQueue.add(t);
		}
		else {
			container.addTooltipToRenderQueue(t);
		}
	}
	
	public void setMouseWheelMoved(int change) {
		if (container == null) {
			mouseWheelMoved = change;
		}
		else {
			container.setMouseWheelMoved(change);
		}
	}
	
	public int getMouseWheelState() {
		if (container == null) {
			return mouseWheelMoved;
		}
		else {
			return container.getMouseWheelState();
		}
	}
	
	@Override
	public void addElement(GUI_Element e) {
		if (e.getContainer().equals(this)) { 
			super.addElement(e);
		}
		
		if (container == null) {
			if (e instanceof GUI_Element_DragDropTarget) {
				dragDropTargets.add((GUI_Element_DragDropTarget) e);
			}
		}
		else {
			container.addElement(e);
		}
	}
	
	@Override
	public boolean removeElement(String id) {
		boolean removed = super.removeElement(id);
		if (removed) {
			if (container == null) {
				for (GUI_Element_DragDropTarget e : dragDropTargets) {
					if (id.equals(e.getId())) {
						dragDropTargets.remove(e);
						
						return removed;
					}
				}
			}
			else {
				container.removeElement(id);
			}
		}
		
		return removed;
	}
	
	@Override
	protected void calculateWidthHeight() {
		//Blank Override to remove composite's dynamic scaling.
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
}
