package com.fizzikgames.roguelike.world;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import com.fizzikgames.roguelike.Event;
import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.Renderable;
import com.fizzikgames.roguelike.entity.GameCharacterEvent;
import com.fizzikgames.roguelike.entity.GameCharacterListener;
import com.fizzikgames.roguelike.entity.PlayerCharacter;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_SightRadius;
import com.fizzikgames.roguelike.gui.PlayerGUI;

public class World extends Event implements Runnable, Renderable {
	private Level level;
	private LevelViewer viewer;
	private RandomLevelGenerator levelgen;
	private PlayerGUI gui;
	private GameContainer gameContainer;
	private boolean holdleft;
	private boolean holdright;
	
	public World(GameLogic gl, GameContainer gc) {
		gameContainer = gc;
		
		holdleft = false;
		holdright = false;
		
		final PlayerCharacter player = new PlayerCharacter("character_player_001", null, 0, 0);
		levelgen = new RandomLevelGenerator();
		level = levelgen.generateLevel(player);
		player.setNewLevel(level);
		
		viewer = new LevelViewer(this, 0, 0, GameLogic.WINDOW_WIDTH, GameLogic.WINDOW_HEIGHT - PlayerGUI.ACTIONBARPANE_HEIGHT);
		viewer.setLevel(level);
		viewer.setEntityFocus(player);
		level.setViewer(viewer);
		
		gui = new PlayerGUI(player);
		
		player.addListener(gui);
		
		gameContainer.getInput().addListener(gui);
		gameContainer.getInput().addListener(this);
		
		//Add Player Stair Listener for Level Changes
		player.addListener(new GameCharacterListener() {
            @Override
            public void eventPerformed(GameCharacterEvent e) {
                if (e.getEventType().equals(GameCharacterEvent.Type.Used_Stairs.getType())) {
                    level = levelgen.generateLevel(player);
                    player.setNewLevel(level);
                    viewer.setLevel(level);
                    level.calculatePlayerVisibility((int) e.getGameCharacter().getStat(Stat_SightRadius.REFERENCE).getModifiedValue());
                    level.setViewer(viewer);
                }
            }

            @Override
            public List<String> getEventTypes() {
                ArrayList<String> types = new ArrayList<String>();
                types.add(GameCharacterEvent.Type.Used_Stairs.getType());
                return types;
            }		    
		});
		
		player.addListener(gl);
	}
	
	public void restart(GameLogic gl) {
	    final PlayerCharacter player = new PlayerCharacter("character_player_001", null, 0, 0);
	    gui.setPlayer(player);
        player.addListener(gui);
	    level = levelgen.generateLevel(player);
        player.setNewLevel(level);
        viewer.setLevel(level);
        viewer.setEntityFocus(player);
        level.setViewer(viewer);
        
        player.addListener(new GameCharacterListener() {
            @Override
            public void eventPerformed(GameCharacterEvent e) {
                if (e.getEventType().equals(GameCharacterEvent.Type.Used_Stairs.getType())) {
                    level = levelgen.generateLevel(player);
                    player.setNewLevel(level);
                    viewer.setLevel(level);
                    level.calculatePlayerVisibility((int) e.getGameCharacter().getStat(Stat_SightRadius.REFERENCE).getModifiedValue());
                    level.setViewer(viewer);
                }
            }

            @Override
            public List<String> getEventTypes() {
                ArrayList<String> types = new ArrayList<String>();
                types.add(GameCharacterEvent.Type.Used_Stairs.getType());
                return types;
            }           
        });
        
        player.addListener(gl);
	}

	@Override
	public void run() {
		new Thread(viewer, "LevelViewerThread").start();
	}
	
	public void update(GameContainer gc, int delta) {
		if (level != null) {
			if (!gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
				holdleft = false;
			}
			if (!gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
				holdright = false;
			}
			
			level.update(gc, delta);
			viewer.update(gc, delta);
			gui.update(gc);
			
			if (!guiHasFocus()) {
				if (!holdleft && gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
					holdleft = true;
					level.processLeftClick(viewer.getMouseRow(), viewer.getMouseColumn());
				}
				if (!holdright && gc.getInput().isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
					holdright = true;
					level.processRightClick(viewer.getMouseRow(), viewer.getMouseColumn());
				}
			}
			else {
				if (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
					holdleft = true;
				}
				if (gc.getInput().isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
					holdright = true;
				}
			}
		}
	}
	
	@Override
	public void render(GameContainer gc, Graphics g) {
		if (level != null) {
			viewer.render(gc, g);
			gui.render(gc, g);
		}
	}
	
	/**
	 * Returns true if the player gui currently has focus.
	 */
	public boolean guiHasFocus() {
		return gui.hasFocus();
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		if (!guiHasFocus()) {
			//Zoom Level Viewer
			viewer.zoom(change / Math.abs(change), false);
		}
	}
}
