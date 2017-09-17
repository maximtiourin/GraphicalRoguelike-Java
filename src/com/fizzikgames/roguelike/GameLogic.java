package com.fizzikgames.roguelike;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;

import com.fizzikgames.roguelike.asset.AssetLoader;
import com.fizzikgames.roguelike.entity.GameCharacterEvent;
import com.fizzikgames.roguelike.entity.GameCharacterListener;
import com.fizzikgames.roguelike.gui.ElementListener;
import com.fizzikgames.roguelike.gui.MainMenuGUI;
import com.fizzikgames.roguelike.world.World;

public class GameLogic implements Game, GameCharacterListener {
	private enum State {
		LoadAssets, Splash, Main, Play, DeathScreen, Quit;
	}
	private static final String TITLE = "Maxim Tiourin Roguelike";
	private static final int MIN_LOGIC_INTERVAL = 10;
	private static final int MAX_LOGIC_INTERVAL = 33;
	private static final boolean FULLSCREEN = false;
	public static final boolean DEBUG = false;
	public static final boolean FPSDEBUG = false;
	public static AppGameContainer CONTAINER;
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;
	public static final int IMAGE_FILTER = Image.FILTER_NEAREST;
	public static Random rng = new Random(System.currentTimeMillis() * System.currentTimeMillis());
	private ArrayList<Renderable> renderables;
	private State state;
	private MainMenuGUI mainmenu;
	private World world;
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		renderables = new ArrayList<Renderable>();
		state = State.LoadAssets;
		mainmenu = null;
		world = null;
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		Iterator<Renderable> it = renderables.iterator();
		while (it.hasNext()) {
			Renderable r = it.next();
			r.render(gc, g);
		}
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		if (state == State.LoadAssets) {
			if (!AssetLoader.isReady()) {
				AssetLoader.load();
				renderables.add(AssetLoader.get());
			}
			
			AssetLoader.update(gc);
			
			if (AssetLoader.isLoaded()) {
				renderables.remove(AssetLoader.get());
				state = State.Splash;
			}
		}
		else if (state == State.Splash) {
			state = State.Main;
		}
		else if (state == State.Main) {
			if (mainmenu == null) {
				mainmenu = new MainMenuGUI();
				mainmenu.getContainer().setVisible(true);
				renderables.add(mainmenu);
				
				//StartGame listener
				mainmenu.getButtonStartGame().addListener(new ElementListener(){
					@Override
					public void elementActionPerformed() {
						renderables.remove(mainmenu);
						if (world != null) renderables.add(world);
						
						state = State.Play;
					}					
				});
				//QuitGame listener
				mainmenu.getButtonQuitGame().addListener(new ElementListener(){
					@Override
					public void elementActionPerformed() {
						renderables.remove(mainmenu);
						
						state = State.Quit;
					}					
				});
			}
			else {
				//Check Menu States
				mainmenu.update(gc);
			}			
		}
		else if (state == State.Play) {
			if (world == null) {
				world = new World(this, gc);
				new Thread(world, "WorldThread").start();
				renderables.add(world);
			}
			
			world.update(gc, delta);
		}
		else if (state == State.DeathScreen) {		    
		    state = State.Main;
		}
		else if (state == State.Quit) {
			CONTAINER.exit();
		}
	}
	
	@Override
	public boolean closeRequested() {
		return true;
	}

	@Override
	public String getTitle() {
		return TITLE;
	}
	
	public static void main(String[] args) {
		try {
			Renderer.setRenderer(Renderer.VERTEX_ARRAY_RENDERER);
			GameLogic game = new GameLogic();
			AppGameContainer container = new AppGameContainer(game);
			CONTAINER = container;
			container.setShowFPS(DEBUG || FPSDEBUG);
			container.setDisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT, FULLSCREEN);
			container.setMinimumLogicUpdateInterval(MIN_LOGIC_INTERVAL);
			container.setMaximumLogicUpdateInterval(MAX_LOGIC_INTERVAL);
			container.setClearEachFrame(false);
			//container.setIcons(String[]); TODO
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void eventPerformed(GameCharacterEvent e) {
        if (e.getEventType().equals(GameCharacterEvent.Type.Buried.getType())) {
            state = State.DeathScreen;
            renderables.remove(world);
            renderables.add(mainmenu);
            //world.restart(this);
            world = null;
        }
    }

    @Override
    public List<String> getEventTypes() {
        ArrayList<String> types = new ArrayList<String>();
        types.add(GameCharacterEvent.Type.Buried.getType());
        return types;
    }
}
