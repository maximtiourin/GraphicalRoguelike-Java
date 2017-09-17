package com.fizzikgames.roguelike.gui;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

/**
 * A GUI_Element is the abstract base for any element of the GUI that is displayed or interactive with the user.
 * @author Maxim Tiourin
 */
public abstract class GUI_Element implements Comparable<GUI_Element> {
	protected GUI_Container container;
	protected boolean focus;
	protected boolean visible;
	protected String id;
	protected int renderPriority;
	protected Anchor anchor;
	protected float x;
	protected float y;
	protected float xprev;
	protected float yprev;
	protected Tooltip tooltip;
	public static final int RENDER_CONTAINER = 0;
	public static final int RENDER_IMAGE = 1000;
	public static final int RENDER_LABEL = 2000;
	public static final int RENDER_BUTTON = 3000;
	public static final int RENDER_TEXTBOX = 4000;
	public static final int RENDER_FOCUS = 9999;
	public static final int RENDER_DRAGDROP = 999999;
	private CopyOnWriteArrayList<ElementListener> listeners;
	
	public GUI_Element(GUI_Container container, String id, float x, float y, int renderPriority, boolean visible, Anchor anchor) {
		this.container = container;
		this.visible = visible;
		this.anchor = anchor;
		this.focus = false;
		this.id = id;
		this.renderPriority = renderPriority;
		this.x = x;
		this.y = y;
		this.xprev = x;
		this.yprev = y;
		
		listeners = new CopyOnWriteArrayList<ElementListener>();
	}
	
	/**
	 * Updates the element. Caller is a gui element who should be passed along to all children.
	 * Most elements should simply pass along any caller passed to it, however a vertical scroll area will pass
	 * itself as the caller so that any elements contained within only update if they are within the vsa's bounds.
	 */
	public abstract void update(GameContainer gc, GUI_Element caller);
	public abstract void render(Graphics g);
	public abstract int getWidth();
	public abstract int getHeight();
	
	public void addListener(ElementListener l) {
		listeners.add(l);
	}
	
	/**
	 * Notifies all listeners that an action for this element has been performed.
	 */
	protected void notifyListeners() {
		Iterator<ElementListener> it = listeners.iterator();
		while (it.hasNext()) {
			ElementListener l = it.next();
			l.elementActionPerformed();
		}
	}
	
	public void removeListener(ElementListener l) {
		listeners.remove(l);
	}
	
	public void clearListeners() {
		listeners.clear();
	}
	
	public int compareTo(GUI_Element e) {
		if (renderPriority < e.getRenderPriority()) return -1;
		else if (renderPriority == e.getRenderPriority()) return 0;
		return 1;
	}
	
	public GUI_Container getContainer() {
		return container;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean b) {
		visible = b;
	}
	
	public int getRenderPriority() {
		return renderPriority;
	}
	
	public String getId() {
		return id;
	}
	
	protected Anchor anchor() {
		return anchor;
	}
	
	/**
	 * Returns the true x position of this element based on whether or not it is anchored.
	 */
	protected float x() {
		if (container != null) {
			if ((anchor == Anchor.TOPLEFT) || (anchor == Anchor.LEFT) || (anchor == Anchor.BOTLEFT)) {
				return container.x() + x;
			}
			else if ((anchor == Anchor.TOP) || (anchor == Anchor.CENTER) || (anchor == Anchor.BOT)) {
				return container.x() + ((float) container.getWidth() / 2) + x;
			}
			else if ((anchor == Anchor.TOPRIGHT) || (anchor == Anchor.RIGHT) || (anchor == Anchor.BOTRIGHT)) {
				return container.x() + container.getWidth() + x;
			}
		}

		return x;
	}
	
	/**
	 * Returns the true y position of this element based on whether or not it is anchored.
	 */
	protected float y() {
		if (container != null) {
			if ((anchor == Anchor.TOPLEFT) || (anchor == Anchor.TOP) || (anchor == Anchor.TOPRIGHT)) {
				return container.y() + y;
			}
			else if ((anchor == Anchor.LEFT) || (anchor == Anchor.CENTER) || (anchor == Anchor.RIGHT)) {
				return container.y() + ((float) container.getHeight() / 2) + y;
			}
			else if ((anchor == Anchor.BOTLEFT) || (anchor == Anchor.BOT) || (anchor == Anchor.BOTRIGHT)) {
				return container.y() + container.getHeight() + y;
			}
		}

		return y;
	}
	
	protected void addX(float x) {
		this.xprev = this.x;
		this.x += x;
	}
	
	protected void addY(float y) {
		this.yprev = this.y;
		this.y += y;
	}
	
	protected float xprev() {
		return xprev;
	}
	
	protected float yprev() {
		return yprev;
	}
	
	/**
	 * Visibility check that uses all containers of the element as well
	 */
	protected boolean visible() {
		if (container != null) {
			if (!visible) return false;
			if (visible) return container.visible();
		}
		else {
			if (visible) return true;
		}
		return false;
	}
	
	public boolean hasFocus() {
		return focus;
	}
	
	public void setFocus(boolean f) {
		focus = f;
	}
	
	public static boolean inBounds(GUI_Element e, Rectangle bounds) {
		if (bounds.intersects(new Rectangle(e.x(), e.y(), e.getWidth(), e.getHeight()))) {
			return true;
		}
		
		return false;
	}
	
	public void setTooltip(Tooltip ttip) {
		tooltip = ttip;
	}
}
