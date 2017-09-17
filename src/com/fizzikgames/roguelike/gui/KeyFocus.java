package com.fizzikgames.roguelike.gui;

/**
 * A Key focus is a GUI_Element that takes focus from the keyboard in conjunction with it's GUI_Container.
 * It's main responsibility is to have a method that performs context actions (such as deselecting itself) when the container tells it
 * focus is removed.
 * @author Maxim Tiourin
 * @version 1.00
 */
public interface KeyFocus {
	public void removeKeyFocus();
	public boolean isVisible();
	public void setVisible(boolean b);
}
