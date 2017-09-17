package com.fizzikgames.roguelike;

import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;

/**
 * A Wrapper for input listener that allows you to only implement 
 * the methods you are interested in listening for.
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Event implements InputListener {
	@Override
	public void inputEnded() {}

	@Override
	public void inputStarted() {}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void setInput(Input input) {}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {}

	@Override
	public void mouseMoved(int arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void mousePressed(int arg0, int arg1, int arg2) {}

	@Override
	public void mouseReleased(int arg0, int arg1, int arg2) {}

	@Override
	public void mouseWheelMoved(int change) {}

	@Override
	public void keyPressed(int key, char c) {}

	@Override
	public void keyReleased(int key, char c) {}

	@Override
	public void controllerButtonPressed(int arg0, int arg1) {}

	@Override
	public void controllerButtonReleased(int arg0, int arg1) {}

	@Override
	public void controllerDownPressed(int arg0) {}

	@Override
	public void controllerDownReleased(int arg0) {}

	@Override
	public void controllerLeftPressed(int arg0) {}

	@Override
	public void controllerLeftReleased(int arg0) {}

	@Override
	public void controllerRightPressed(int arg0) {}

	@Override
	public void controllerRightReleased(int arg0) {}

	@Override
	public void controllerUpPressed(int arg0) {}

	@Override
	public void controllerUpReleased(int arg0) {}
}
