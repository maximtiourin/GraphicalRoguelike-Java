package com.fizzikgames.roguelike.entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import com.fizzikgames.roguelike.sprite.ExtraTargetCursorSprite;
import com.fizzikgames.roguelike.sprite.Sprite;
import com.fizzikgames.roguelike.world.Level;

public class PlayerTargetCursor extends Entity {
	private Sprite sprite;
	private Level level;
	private boolean w;
	private boolean a;
	private boolean s;
	private boolean d;
	private int actiontimer;
	
	public PlayerTargetCursor(Level level, int r, int c) {
		super(r, c);
		this.sprite = new ExtraTargetCursorSprite("extra_targetcursor");
		this.sprite.current("targetcursor");
		this.level = level;
		setRenderPriority(RenderPriority.TargetCursor.getPriority());
	}
	
	@Override
	public void update(GameContainer gc, int delta) {
		//Movement
	    final int actiontimerlength = 150;
        if (gc.getInput().isKeyDown(Input.KEY_W)) {
            w = true;
            s = false;
            if (actiontimer <= 0) actiontimer = actiontimerlength;
        }
        else if (gc.getInput().isKeyDown(Input.KEY_S)) {
            s = true;
            w = false;
            if (actiontimer <= 0) actiontimer = actiontimerlength;
        }
        if (gc.getInput().isKeyDown(Input.KEY_A)) {
            a = true;
            d = false;
            if (actiontimer <= 0) actiontimer = actiontimerlength;
        }
        else if (gc.getInput().isKeyDown(Input.KEY_D)) {
            d = true;
            a = false;
            if (actiontimer <= 0) actiontimer = actiontimerlength;
        }
        
        if (actiontimer > 0) {
            actiontimer -= delta;
            
            if (actiontimer <= 0) {
                int newr = getRow();
                int newc = getColumn();
                if (w) newr = getRow() - 1;
                if (a) newc = getColumn() - 1;
                if (s) newr = getRow() + 1;
                if (d) newc = getColumn() + 1;
                w = false;
                a = false;
                s = false;
                d = false;
                attemptSetCoordinate(newr, newc);
            }
        }
		
		//Selection/Cancellation
		if (gc.getInput().isKeyPressed(Input.KEY_E)) {
			//Confirm Selection
			confirmTarget(getRow(), getColumn());
		}
		else if (gc.getInput().isKeyPressed(Input.KEY_Q)) {
			//Cancel selection
			level.cancelPlayerTargettingMode();
		}
		
		sprite.step(delta);
	}
	
	/**
	 * Will set the coordinate of the cursor to the given coordinate if it is
	 * a valid tile target.
	 */
	public void attemptSetCoordinate(int newr, int newc) {
		if (level.tileIsValidTarget(newr, newc)) {
			setRow(newr);
			setColumn(newc);
		}
	}
	
	public void confirmTarget(int r, int c) {
		level.notifyPlayerTargetListeners(level.getGameCharacterAtPoint(r, c), r, c);
	}
	
	public Image getSpriteImage() {
		return sprite.image();
	}
	
	public Image getTargetAbilityImage() {
		return level.getPlayer().getTargetAbility().getIconImage();
	}
}
