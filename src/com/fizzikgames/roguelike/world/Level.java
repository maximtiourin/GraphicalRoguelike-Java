package com.fizzikgames.roguelike.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.Log;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.asset.AssetLoader;
import com.fizzikgames.roguelike.entity.Entity;
import com.fizzikgames.roguelike.entity.FloatingContextStack;
import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.GameCharacterEvent;
import com.fizzikgames.roguelike.entity.ItemEntity;
import com.fizzikgames.roguelike.entity.Monster;
import com.fizzikgames.roguelike.entity.PlayerCharacter;
import com.fizzikgames.roguelike.entity.PlayerTargetCursor;
import com.fizzikgames.roguelike.entity.PlayerTargetListener;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.ItemFactory;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.ItemFactory.BroadItemType;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.ItemFactory.ItemType;
import com.fizzikgames.roguelike.pathfinding.Astar;
import com.fizzikgames.roguelike.pathfinding.Mover;
import com.fizzikgames.roguelike.pathfinding.Node;
import com.fizzikgames.roguelike.pathfinding.TileMap;
import com.fizzikgames.roguelike.util.RandomBag;

public abstract class Level implements TileMap {
	protected boolean generated;
	protected Cell stairDown;
	protected int width;
	protected int height;
	protected int playerTargetLeftClicks;
	protected int playerTargetRightClicks;
	protected short tiles[][];
	protected SpriteSheet tileset;
	protected SpriteSheet extras;
	protected PlayerCharacter player;
	protected PlayerTargetCursor targetCursor;
	protected boolean playerTurn;
	protected boolean isPlayerTargeting;
	protected CopyOnWriteArrayList<Entity> entities;
	protected CopyOnWriteArrayList<GameCharacter> characters;
	protected CopyOnWriteArrayList<PlayerTargetListener> targetListeners;
	protected ArrayList<Vector2f> validTargetTiles;
	protected Rectangle validTargetArea;
	protected LevelViewer viewer;
	protected Astar astar;
	protected RandomBag<BroadItemType> lootTable;
	protected FloatingContextStack contextStack;
	
	public Level(String tileset, String extras) {
		generated = false;
		this.stairDown = null;
		this.width = 0;
		this.height = 0;
		this.playerTargetLeftClicks = 0;
		this.playerTargetRightClicks = 0;
		this.tiles = null;
		this.tileset = null;
		this.extras = null;
		this.player = null;
		this.targetCursor = null;
		this.playerTurn = false;
		this.isPlayerTargeting = false;
		this.entities = new CopyOnWriteArrayList<Entity>();
		this.characters = new CopyOnWriteArrayList<GameCharacter>();
		this.targetListeners = new CopyOnWriteArrayList<PlayerTargetListener>();
		this.validTargetTiles = new ArrayList<Vector2f>();
		this.validTargetArea = null;
		this.viewer = null;
		this.astar = null;
		contextStack = new FloatingContextStack();
		
		SpriteSheet loadTileSet = AssetLoader.spritesheet(tileset);
		SpriteSheet loadExtraSet = AssetLoader.spritesheet(extras);
		if (loadTileSet != null) {
			this.tileset = loadTileSet;
		}
		else {
			Log.error("Unable to find tileset: " + tileset);
		}
		if (loadExtraSet != null) {
			this.extras = loadExtraSet;
		}
		else {
			Log.error("Unable to find extras tileset: " + tileset);
		}
	}
	
	public void update(GameContainer gc, int delta) {
		if (generated) {
			for (Entity e : entities) {
				e.update(gc, delta);
			}
			
			contextStack.update(gc, delta);
			
			//Monster AI Moves
			if (!isPlayerTurn()) {
			    for (GameCharacter e : characters) {
			        if (e instanceof Monster) {
			            Monster monster = (Monster) e;
			            monster.doMove();
			        }
			    }
			    
			    //Force player to send position modification to update monster position for player position listeners
			    player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Position_Modified.getType()));
			    
			    //Player Turn
			    setPlayerTurn(true);
			}
		}
	}
	
	/**
	 * Generates the contents of the level, will not update a level until it is
	 * generated. Rendering the level is handled by a LevelViewer, and the level
	 * is in charge of being able to pass it relevant information.
	 * Must initialize astar.
	 */
	public abstract void generate();
	
	/**
	 * Decides what context action to perform after a left click at the given grid coordinate
	 */
	public void processLeftClick(int r, int c) {
		if (isInPlayerTargetMode()) {
			if (playerTargetLeftClicks == 0) {
				//Set Cursor pos
				targetCursor.attemptSetCoordinate(r, c);
				playerTargetLeftClicks++;
			}
			else if (playerTargetLeftClicks == 1) {
				//confirm if same, set new pos if different
				Vector2f oldv = new Vector2f(targetCursor.getColumn(), targetCursor.getRow());
				Vector2f newv = new Vector2f(c, r);
				
				if (newv.equals(oldv)) {
					//Confirm
					targetCursor.confirmTarget(r, c);
				}
				else {
					//Set new pos
					targetCursor.attemptSetCoordinate(r, c);
				}
			}
		}
	}
	
	/**
	 * Decides what context action to perform after a right click at the given grid coordinate
	 */
	public void processRightClick(int r, int c) {
		if (isInPlayerTargetMode()) {
			cancelPlayerTargettingMode();
		}
	}
	
	/**
	 * Call to calculate which tiles the player can see and to set tiles the player has seen.
	 */
	public void calculatePlayerVisibility(int sightRadius) {
	   //Set Past Visibility Flags in an area slightly more than sightRadius
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				int row = r;
				int column = c;				
				short visibility = Tile.getVisibility(getTile(row, column));
				if (visibility == Tile.Visibility.IsVisible.getType()) {
					setTile(Tile.setVisibility(getTile(row, column), Tile.Visibility.WasVisible.getType()), row, column);
				}
			}
		}
		
		//Set new sight radius tiles
		int startr = player.getRow() - sightRadius;
        int endr = player.getRow() + sightRadius;
        int startc = player.getColumn() - sightRadius;
        int endc = player.getColumn() + sightRadius;
        startr = Math.max(0, startr);
        startc = Math.max(0, startc);
        endr = Math.min(endr, getHeight() - 1);
        endc = Math.min(endc, getWidth() - 1);
		
		//Set Visibility Flags for any visibile tiles
		for (int r = startr; r <= endr; r++) {
			for (int c = startc; c <= endc; c++) {
			    Vector2f p = new Vector2f(player.getColumn(), player.getRow());
                Vector2f t = new Vector2f(c, r);
			    calculateVisibilityLineOfSight(p, t, sightRadius);
			}
		}
	}
	
	/**
	 * Returns true if the src position has line of sight of the dest position within the sight radius
	 */
	public boolean hasLineOfSight(Vector2f src, Vector2f dest, int sightRadius) {		
		//First check easy case if dest is within sightradius of src
		double distance = src.distance(dest);
		if (distance > sightRadius) {
			return false;
		}
		
		//Trace
		int x0 = (int) src.getX();
		int y0 = (int) src.getY();
		int x1 = (int) dest.getX();
		int y1 = (int) dest.getY();	
		
		int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int x = x0;
        int y = y0;
        int n = 1 + dx + dy;
        int x_inc = (x1 > x0) ? 1 : -1;
        int y_inc = (y1 > y0) ? 1 : -1;
        int error = dx - dy;
        dx *= 2;
        dy *= 2;

        for (; n > 0; --n) {
            if (movementBlockedAt(y, x)) {
            	return false;
            }

            if (error > 0) {
                x += x_inc;
                error -= dy;
            }
            else {
                y += y_inc;
                error += dx;
            }
        }
		
		return true;
	}
	
	/**
     * Uses bresenham's line algorithm to trace line of sight and set visibility of tiles from src to dest.
     */
    private boolean calculateVisibilityLineOfSight(Vector2f src, Vector2f dest, int sightRadius) {       
        //First check easy case if dest is within sightradius of src
        double distance = src.distance(dest);
        if (distance > sightRadius) {
            return false;
        }
        
        //Trace
        int x0 = (int) src.getX();
        int y0 = (int) src.getY();
        int x1 = (int) dest.getX();
        int y1 = (int) dest.getY(); 
        
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int x = x0;
        int y = y0;
        int n = 1 + dx + dy;
        int x_inc = (x1 > x0) ? 1 : -1;
        int y_inc = (y1 > y0) ? 1 : -1;
        int error = dx - dy;
        dx *= 2;
        dy *= 2;

        for (; n > 0; --n) {
            if (movementBlockedAt(y, x)) {
                return false;
            }
            else {
                setTile(Tile.setVisibility(getTile(y, x), Tile.Visibility.IsVisible.getType()), y, x);
                
                for (int rr = y - 1; rr <= y + 1; rr++) {
                    for (int cc = x - 1; cc <= x + 1; cc++) {
                        int row = Math.max(rr, 0);
                        row = Math.min(row, getHeight() - 1);
                        int column = Math.max(cc, 0);
                        column = Math.min(column, getWidth() - 1);
                        
                        if (Tile.isWall(Tile.getTileId(getTile(row, column)))) {
                            setTile(Tile.setVisibility(getTile(row, column), Tile.Visibility.IsVisible.getType()), row, column);
                        }
                    }
                }
            }

            if (error > 0) {
                x += x_inc;
                error -= dy;
            }
            else {
                y += y_inc;
                error += dx;
            }
        }
        
        return true;
    }
	
	/**
	 * Returns true if movement is blocked by an impassable cell at the given location.
	 */
	public boolean movementBlockedAt(int r, int c) {
		if (((r < 0) || (r >= getHeight())) || ((c < 0) || (c >= getWidth()))) {
			//Out of Bounds
			return true;
		}
		else if (Tile.isWall(Tile.getTileId(getTile(r, c)))) {
			//Wall
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if a gamecharacter is currently located at the given location
	 */
	public boolean movementImpededAt(boolean pathfind, int r, int c) {
	    if (movementBlockedAt(r, c)) {
	        return true;
	    }
	    
	    for (GameCharacter e : characters) {
	        if (!(e instanceof PlayerCharacter) || !pathfind) {
    	        if (e.getRow() == r && e.getColumn() == c) {
    	            return true;
    	        }
	        }
	    }
	    
	    return false;
	}
	
	/**
	 * Returns the first game character found at the given grid coordinate if there is one.
	 */
	public GameCharacter getGameCharacterAtPoint(int r, int c) {
		for (GameCharacter e : characters) {
			if (e.getRow() == r && e.getColumn() == c) {
				return e;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a list of entities contained within the grid coordinate rectangle
	 */
	public List<Entity> getEntitiesInRectangle(Rectangle rect) {
		ArrayList<Entity> returnList = new ArrayList<Entity>();
		
		for (Entity e : entities) {
			Vector2f pos = new Vector2f(e.getColumn(), e.getRow()); 
			
			if (((pos.getX() >= rect.getX()) && (pos.getX() <= rect.getX() + rect.getWidth())) 
					&& ((pos.getY() >= rect.getY()) && (pos.getY() <= rect.getY() + rect.getHeight()))) {
				returnList.add(e);
			}
		}
		
		return returnList;
	}
	
	/**
	 * Enters player targetting mode
	 */
	public void enterPlayerTargetingMode(int range, boolean lineofsight) {
		if (!isInPlayerTargetMode()) {
			//Create cursor
			targetCursor = new PlayerTargetCursor(this, player.getRow(), player.getColumn());
			viewer.setEntityFocus(targetCursor);
			
			//Create Valid Tile Group
			int startr = player.getRow() - range;
			int endr = player.getRow() + range;
			int startc = player.getColumn() - range;
			int endc = player.getColumn() + range;
			startr = Math.max(0, startr);
			startc = Math.max(0, startc);
			endr = Math.min(endr, getHeight() - 1);
			endc = Math.min(endc, getWidth() - 1);
			
			validTargetArea = new Rectangle(startc, startr, endc - startc, endr - startr);
			for (int r = startr; r <= endr; r++) {
				for (int c = startc; c <= endc; c++) {
					Vector2f p = new Vector2f(player.getColumn(), player.getRow());
					Vector2f t = new Vector2f(c, r);
					
					short visibility = Tile.getVisibility(getTile(r, c));
					if (p.distance(t) <= range) {
						if (!lineofsight || hasLineOfSight(p, t, range)) {
							if (!Tile.isWall(Tile.getTileId(getTile(r, c))) && (visibility != Tile.Visibility.NotVisible.getType())) {
								validTargetTiles.add(t);
							}
						}
					}
				}
			}
			
			entities.add(targetCursor);
			
			//Reset click tracking
			playerTargetLeftClicks = 0;
			playerTargetRightClicks = 0;
			
			isPlayerTargeting = true;
			
			if (GameLogic.DEBUG) System.out.println("Level Player Targetting Mode Entered");
		}
	}
	
	/**
	 * Leaves player targetting mode
	 */	
	public void cancelPlayerTargettingMode() {
		if (isInPlayerTargetMode()) {
			//Remove cursor, clear listeners
			viewer.setEntityFocus(player);
			entities.remove(targetCursor);
			targetCursor = null;
			clearPlayerTargetListeners();
			
			//Clear validTargetTiles
			validTargetTiles.clear();
			validTargetArea = null;
			
			//Reset click tracking
			playerTargetLeftClicks = 0;
			playerTargetRightClicks = 0;
			
			isPlayerTargeting = false;
			
			if (GameLogic.DEBUG) System.out.println("Level Player Targetting Mode Exited");
		}
	}
	
	/**
	 * Returns all valid target tiles in player targetting mode
	 */
	public ArrayList<Vector2f> getValidTargetTiles() {
		return validTargetTiles;
	}
	
	/**
	 * Returns true if the given grid coordinate is within the general valid target area.
	 */
	public boolean pointIsInValidTargetArea(int r, int c) {
		if (validTargetArea == null) return false;
		
		if (((r >= validTargetArea.getY()) && (r <= validTargetArea.getY() + validTargetArea.getHeight())) 
				&& ((c >= validTargetArea.getX()) && (c <= validTargetArea.getX() + validTargetArea.getWidth()))) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if the given grid coordinate is a valid target coordinate.
	 */
	public boolean tileIsValidTarget(int r, int c) {
		if (pointIsInValidTargetArea(r, c)) {
			for (Vector2f e : getValidTargetTiles()) {
				if (e.equals(new Vector2f(c, r))) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Notifies all player target listeners that a target was selected.
	 */
	public void notifyPlayerTargetListeners(GameCharacter target, int r, int c) {
		if (isInPlayerTargetMode()) {
			for (PlayerTargetListener e : targetListeners) {
				e.targetSelected(target, r, c);
			}	
		}
	}
	
	public void addPlayerTargetListener(PlayerTargetListener l) {
		targetListeners.add(l);
	}
	
	public void removePlayerTargetListener(PlayerTargetListener l) {
		targetListeners.remove(l);
	}
	
	public void clearPlayerTargetListeners() {
		targetListeners.clear();
	}
	
	public boolean isInPlayerTargetMode() {
		return isPlayerTargeting;
	}
	
	public PlayerCharacter getPlayer() {
		return player;
	}
	
	public void setPlayerCharacter(PlayerCharacter player) {
		removeGameCharacter(this.player);
		this.player = player;
		addGameCharacter(this.player);
	}
	
	public void addGameCharacter(GameCharacter gamechar) {
		characters.add(gamechar);
		entities.add(gamechar);
	}
	
	public boolean removeGameCharacter(GameCharacter gamechar) {
		entities.remove(gamechar);
		return characters.remove(gamechar);
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	public boolean removeEntitiy(Entity e) {
		return entities.remove(e);
	}
	
	public short getTile(int r, int c) {
		return tiles[r][c];
	}
	
	public void setTile(short tile, int r, int c) {
		tiles[r][c] = tile;
	}
	
	public SpriteSheet getTileSet() {
		return tileset;
	}
	
	public SpriteSheet getExtras() {
		return extras;
	}
	
	public boolean isPlayerTurn() {
		return playerTurn;
	}
	
	public void setPlayerTurn(boolean b) {
		playerTurn = b;
		if (playerTurn) player.startTurn();
		//if (b) System.out.println("************** Player's Turn **************");
		//else System.out.println("************** Dungeon's Turn **************");
	}
	
	public void setViewer(LevelViewer viewer) {
		this.viewer = viewer;
	}
	
	public boolean isStairDown(int r, int c) {
	    if (stairDown == null) return false;
	    
	    if (new Cell(r, c).equals(stairDown)) {
	        return true;
	    }
	    
	    return false;
	}
	
	/**
	 * Creates a new item entity for the given item at the location
	 */
	public void dropItem(Item item, int r, int c) {
	    ItemEntity eitem = new ItemEntity(this, item);
	    eitem.setRow(r);
	    eitem.setColumn(c);
	    player.addListener(eitem);
	    this.addEntity(eitem);
	}
	
	/**
	 * Drops random items from the loot table at the location
	 */
	public void dropItemsFromLootTable(int amount, int r, int c) {
	    Random rng = GameLogic.rng;
	    
	    for (int i = 0; i < amount; i++) {
	        ItemType[] types = lootTable.getRandomObject().getTypes();
	        ItemType type = types[rng.nextInt(types.length)];
	        
	        dropItem(ItemFactory.createItem(type, player, player.getCharacterLevel()), r, c);
	    }
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public int getDepth() {
		return 1;
	}
	
	@Override
	public boolean isWalkableTile(Mover mover, Node srcNode, Node dstNode) {
		return mover.isValidNode(srcNode, dstNode);
	}

	@Override
	public int getCost(Mover mover, Node srcNode, Node dstNode) {
		return mover.getMoveCost(srcNode, dstNode);
	}
	
	public static Node getGridPositionAsNode(int r, int c) {
		return new Node(null, c, r);
	}
	
	public Astar getPathfinder() {
	    return astar;
	}
	
	public FloatingContextStack getContextStack() {
	    return contextStack;
	}
	
	/**
	 * Returns an image where each pixel is equivelant to one tile
	 */
	public Image getPixelImageOfLevelState() {
	    ImageBuffer buffer = new ImageBuffer(width, height);
	    
	    //Draw Tile Color
	    for (int r = 0; r < buffer.getHeight(); r++) {
	        for (int c = 0; c < buffer.getWidth(); c++) {
	            //Get Color Of Tile
	            if (Tile.getVisibility(getTile(r, c)) == Tile.Visibility.IsVisible.getType()) {
	                if (getGameCharacterAtPoint(r, c) != null) {
	                    Color color = Color.red;
	                    buffer.setRGBA(c, r, color.getRed(), color.getGreen(), color.getBlue(), 255);
	                }
	                else {
	                    Point tilepos = Tile.getSpriteSheetPosition(Tile.getTileId(getTile(r, c)));
	                    Color color = getTileSet().getSubImage((int) tilepos.getX(), (int) tilepos.getY()).getColor(0, 0);
	                    buffer.setRGBA(c, r, color.getRed(), color.getGreen(), color.getBlue(), 255);
	                }
	            }
	            else if (Tile.getVisibility(getTile(r, c)) == Tile.Visibility.WasVisible.getType()) {
                    Point tilepos = Tile.getSpriteSheetPosition(Tile.getTileId(getTile(r, c)));
                    Color color = getTileSet().getSubImage((int) tilepos.getX(), (int) tilepos.getY()).getColor(0, 0).darker(.4f);
                    buffer.setRGBA(c, r, color.getRed(), color.getGreen(), color.getBlue(), 255);
                }
	            else {
	                buffer.setRGBA(c, r, 0, 0, 0, 255);
	            }
	        }
	    }
	    
	    //Draw Player
	    buffer.setRGBA(player.getColumn(), player.getRow(), 0, 255, 0, 255);
	    
	    //Draw Stair If Visible
	    if (Tile.getVisibility(getTile(stairDown.getRow(), stairDown.getColumn())) == Tile.Visibility.IsVisible.getType()
	            || Tile.getVisibility(getTile(stairDown.getRow(), stairDown.getColumn())) == Tile.Visibility.WasVisible.getType()) {
	        buffer.setRGBA(stairDown.getColumn(), stairDown.getRow(), 0, 255, 255, 255);
	    }
	    
	    return buffer.getImage(Image.FILTER_NEAREST);
	}
}

