package com.fizzikgames.roguelike.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.Renderable;
import com.fizzikgames.roguelike.entity.Chest;
import com.fizzikgames.roguelike.entity.Entity;
import com.fizzikgames.roguelike.entity.FloatingContext;
import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.ItemEntity;
import com.fizzikgames.roguelike.entity.PlayerCharacter;
import com.fizzikgames.roguelike.entity.PlayerTargetCursor;
import com.fizzikgames.roguelike.entity.Shrine;
import com.fizzikgames.roguelike.entity.Trap;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentHealth;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalHealth;

/**
 * The viewer is able to render any level using a flexible tile size, and can constrain itself to a specific viewport width and height.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class LevelViewer implements Runnable, Renderable {
	private static final int KEY_ZOOMIN = Input.KEY_EQUALS;	
	private static final int KEY_ZOOMOUT = Input.KEY_MINUS;
	private static final int INC_CELL_SIZE = 2;
	private static final int MIN_CELL_SIZE = 16;
	private static final int DEFAULT_CELL_SIZE = 32;
	private static final int MAX_CELL_SIZE = 64;
	private static final int ZOOM_COOLDOWN = 100; //In ms
	private World world;
	private Level level;
	private Entity focus;
	private int row;
	private int column;
	private int cellsize;
	private int viewportx;
	private int viewporty;
	private int maxViewportW;
	private int maxViewportH;
	private int horiCells; //how many horizontal cells to render
	private int vertCells; //how many vertical cells to render
	private int gridviewx; //Finalized leftmost x grid coordinate for view
	private int gridviewy; //Finalized topmost y grid coordinate for view
	private int gridvieww; //Finalized grid width of view
	private int gridviewh; //Finalized grid height of view
	private int rotMatrix[][]; //Matrix that holds angles of tiles should they need to be rotated;
	private int zoomCooldown; //Cooldown left before being able to zoom again in ms
	private int mouser; //mouse row
	private int mousec; //mouse column
	
	public LevelViewer(World world, int viewx, int viewy, int maxViewW, int maxViewH) {
		this.world = world;
		level = null;
		row = 0;
		column = 0;
		cellsize = DEFAULT_CELL_SIZE;
		viewportx = viewx;
		viewporty = viewy;
		maxViewportW = maxViewW;
		maxViewportH = maxViewH;
		horiCells = 0;
		vertCells = 0;
		gridviewx = 0;
		gridviewy = 0;
		gridvieww = 0;
		gridviewh = 0;
		rotMatrix = null;
		zoomCooldown = 0;
		mouser = 0;
		mousec = 0;
	}

	@Override
	public void run() {
		
	}
	
	public void update(GameContainer gc, int delta) {
		if (level != null) {
			if (focus != null) {
				//Set focus point if we have one
				row = focus.getRow();
				column = focus.getColumn();
			}
			
			// Determine how many grid cells to draw horizontally and vertically
			int modMaxW = maxViewportW - cellsize;
			int modMaxH = maxViewportH - cellsize;
			int remainder = modMaxW % cellsize;
			horiCells = ((int) Math.floor(modMaxW / cellsize)) + ((remainder > 0) ? 1 : 0);
			remainder = modMaxH % cellsize;
			vertCells = ((int) Math.floor(modMaxH / cellsize)) + ((remainder > 0) ? 1 : 0);
			
			// Determine how many of those gridcells should be on the different sides of the focus point.
			// Any remainder when divided by two means the right and bot half get an extra cell.
			int horiFloor = (int) Math.floor(horiCells / 2);
			int vertFloor = (int) Math.floor(vertCells / 2);			
			remainder = horiCells % 2;
			int leftHalf = horiFloor;
			int rightHalf = (horiFloor) + ((remainder > 0) ? 1 : 0);
			remainder = vertCells % 2;
			int topHalf = vertFloor;
			int botHalf = (vertFloor) + ((remainder > 0) ? 1 : 0);
			
			//Get bounds of the level, and impose wrap-around on any cells that go out of bounds so we are always drawing the maximum cells.
			int levelw = level.getWidth() - 1;
			int levelh = level.getHeight() - 1;
				//Check Left
			int leftoverflow = column - leftHalf;
			if (leftoverflow < 0) {
				leftoverflow = Math.abs(leftoverflow);
				leftHalf -= leftoverflow;
				rightHalf += leftoverflow;
			}
				//Check Right
			int rightoverflow = column + rightHalf - levelw;
			if (rightoverflow > 0) {
				rightHalf -= rightoverflow;
				leftHalf += rightoverflow;
			}
				//Check Top
			int topoverflow = row - topHalf;
			if (topoverflow < 0) {
				topoverflow = Math.abs(topoverflow);
				topHalf -= topoverflow;
				botHalf += topoverflow;
			}
				//Check Bot
			int botoverflow = row + botHalf - levelh;
			if (botoverflow > 0) {
				botHalf -= botoverflow;
				topHalf += botoverflow;
			}
			
			//Now if we still can't draw the maximum cells in 4 directions from center due to out of bounds, concede and constrain them.
				//Check Left
			if (column - leftHalf < 0) {
				leftHalf = column;
			}
				//Check Right
			if (column + rightHalf > levelw) {
				rightHalf = levelw - column;
			}
				//Check Top
			if (row - topHalf < 0) {
				topHalf = row;
			}
				//Check Bot
			if (row + botHalf > levelh) {
				botHalf = levelh - row;
			}
			
			//Set finalized values
			gridviewx = column - leftHalf;
			gridviewy = row - topHalf;
			gridvieww = leftHalf + rightHalf;
			gridviewh = topHalf + botHalf;
			
			//Check if zooming in or zooming out, and update cooldown
			if (gc.getInput().isKeyDown(KEY_ZOOMOUT)) {
				zoom(-1, true);
			}
			else if (gc.getInput().isKeyDown(KEY_ZOOMIN)) {
				zoom(1, true);
			}
			if (zoomCooldown > 0) { 
				zoomCooldown -= delta;
			}
			
			//Check mouse coordinates and set them to appropriate grid only if the world has focus.
			if (!world.guiHasFocus()) {
				int mx = gc.getInput().getMouseX();
				int my = gc.getInput().getMouseY();
				//Only update to grid coordinates if within viewport bounds
				if (((mx >= viewportx) && (mx < viewportx + maxViewportW)) && ((my >= viewporty) && (my < viewporty + maxViewportH))) {
					mouser = ((int) Math.floor((my - viewporty) / cellsize)) + gridviewy;
					mousec = ((int) Math.floor((mx - viewportx) / cellsize)) + gridviewx;
				}
			}
		}
	}
	
	@Override
	public void render(GameContainer gc, Graphics g) {		
		g.setWorldClip(viewportx, viewporty, maxViewportW, maxViewportH);
		if (level != null && rotMatrix != null) {
			//Tiles
			level.getTileSet().startUse();
			for (int r = gridviewy; r <= gridviewy + gridviewh; r++) {
				for (int c = gridviewx; c <= gridviewx + gridvieww; c++) {
					//Dont draw if no visibility
					short visibility = Tile.getVisibility(level.getTile(r, c));
					if (visibility != Tile.Visibility.NotVisible.getType()) {
						int newx = c - gridviewx;
						int newy = r - gridviewy;
						Point tilepos = Tile.getSpriteSheetPosition(Tile.getTileId(level.getTile(r, c)));
						
						//Get Scaled Image and Rotate if needed
						if (rotMatrix[r][c] != 0) {
							level.getTileSet().renderInUse(getScreenX(newx), getScreenY(newy), cellsize, cellsize, rotMatrix[r][c], (int) tilepos.getX(), (int) tilepos.getY());
						}
						else {
							level.getTileSet().renderInUse(getScreenX(newx), getScreenY(newy), cellsize, cellsize, (int) tilepos.getX(), (int) tilepos.getY());
						}
					}
				}
			}
			level.getTileSet().endUse();
			
			//Tile Extras like wall boundaries, target tiles
			level.getExtras().startUse();
			for (int r = gridviewy; r <= gridviewy + gridviewh; r++) {
				for (int c = gridviewx; c <= gridviewx + gridvieww; c++) {
					//Dont draw if no visibility
					short visibility = Tile.getVisibility(level.getTile(r, c));
					if (visibility != Tile.Visibility.NotVisible.getType()) {
						int newx = c - gridviewx;
						int newy = r - gridviewy;
						
						//Draw Wall Boundary
						if (Tile.isWall(Tile.getTileId(level.getTile(r, c)))) {
							//Create Wall matrix
							int[][] nearbyWalls = new int[3][3];
							nearbyWalls[1][1] = 1;
							int nwoffset = 1;
							for (int wr = -1; wr <= 1; wr++) {
								for (int wc = -1; wc <= 1; wc++) {
									int newr = r + wr;
									int newc = c + wc;
									
									//Check Bounds
									if (((newr >= 0) && (newr < level.getHeight())) && ((newc >= 0) && (newc < level.getWidth()))) {
										if (Tile.isWall(Tile.getTileId(level.getTile(newr, newc)))) {
											nearbyWalls[nwoffset + wr][nwoffset + wc] = 1;
										}
										else {
											nearbyWalls[nwoffset + wr][nwoffset + wc] = 0;
										}
									}
								}
							}
							
							//Check Wall Type
							int wallType = Tile.getWallType(nearbyWalls);
							if (wallType > 0) {
								Point tilepos = Tile.getSpriteSheetPosition(wallType);
								level.getExtras().renderInUse(getScreenX(newx), getScreenY(newy), 
										cellsize, cellsize, (int) tilepos.getX(), (int) tilepos.getY());
							}
						}
						
						//Draw target Tile flair
						if (level.pointIsInValidTargetArea(r, c)) {
							for (Vector2f e : level.getValidTargetTiles()) {
								if (e.equals(new Vector2f(c, r))) {
									Point tilepos = Tile.getSpriteSheetPosition(Tile.EXTRA_TARGETSELECTION_VALID);
									
									level.getExtras().renderInUse(getScreenX(newx), getScreenY(newy), 
											cellsize, cellsize, (int) tilepos.getX(), (int) tilepos.getY());
								}
							}
						}
					}
				}
			}
			level.getExtras().endUse();
			
			//Draw Visibility, can't perform this when tiles are rendered to due how spritesheet render-in-use works.
			for (int r = gridviewy; r <= gridviewy + gridviewh; r++) {
				for (int c = gridviewx; c <= gridviewx + gridvieww; c++) {
					int newx = c - gridviewx;
					int newy = r - gridviewy;
					
					Color notVisible = new Color(0, 0, 0, 255);
					Color wasVisible = new Color(0, 0, 0, 175);
					short visibility = Tile.getVisibility(level.getTile(r, c));
					if (visibility == Tile.Visibility.NotVisible.getType()) {
						g.setColor(notVisible);
						g.fillRect(getScreenX(newx), getScreenY(newy), cellsize, cellsize);
					}
					else if (visibility == Tile.Visibility.WasVisible.getType()) {
						g.setColor(wasVisible);
						g.fillRect(getScreenX(newx), getScreenY(newy), cellsize, cellsize);
					}
					
					g.setColor(Color.white);
				}
			}
			
			//Entities
			Rectangle renderRect = new Rectangle(gridviewx, gridviewy, gridvieww, gridviewh);
			ArrayList<Entity> entities = (ArrayList<Entity>) level.getEntitiesInRectangle(renderRect);
			entities.addAll(level.getContextStack().getContextsInRectangle(renderRect));
			Collections.sort(entities, new Entity.RenderPriorityComparator());
			for (Entity e : entities) {
				short visibility = Tile.getVisibility(level.getTile(e.getRow(), e.getColumn()));
				if (e instanceof PlayerCharacter) {
                    if (visibility == Tile.Visibility.IsVisible.getType()) {
                        GameCharacter gamechar = (GameCharacter) e;
                        
                        int newx = e.getColumn() - gridviewx;
                        int newy = e.getRow() - gridviewy;
                        
                        Image spriteImage = gamechar.getSpriteImage().getScaledCopy(cellsize, cellsize);
                        
                        g.drawImage(spriteImage, getScreenX(newx), getScreenY(newy));
                    }
                }
				else if (e instanceof GameCharacter) {
					if (visibility == Tile.Visibility.IsVisible.getType()) {
						GameCharacter gamechar = (GameCharacter) e;
						
						int newx = e.getColumn() - gridviewx;
						int newy = e.getRow() - gridviewy;
						
						Image spriteImage = gamechar.getSpriteImage().getScaledCopy(cellsize, cellsize);
						
						g.drawImage(spriteImage, getScreenX(newx), getScreenY(newy));
						
						//Draw Health Bar
						Stat chealth = gamechar.getStat(Stat_CurrentHealth.REFERENCE);
			            Stat thealth = gamechar.getStat(Stat_TotalHealth.REFERENCE);
			            
			            if (chealth.getModifiedValue() < thealth.getModifiedValue()) {
    			            int barwidth = (int) Math.floor((chealth.getModifiedValue() / thealth.getModifiedValue()) * cellsize);
    			            int barheight = 2;
    			            Color healthColor = new Color(0, 255, 0);
    			            Color bgColor = new Color(3, 29, 0);
    			            g.setColor(bgColor);
    			            g.fillRect(getScreenX(newx), getScreenY(newy), cellsize, barheight);
    			            g.setColor(healthColor);
    			            g.fillRect(getScreenX(newx), getScreenY(newy), barwidth, barheight);
			            }
					}
				}
				else if (e instanceof Trap) {
				    if (visibility == Tile.Visibility.IsVisible.getType()) {
				        Trap trap = (Trap) e;
				        
				        if (trap.isVisible()) {
				            int newx = e.getColumn() - gridviewx;
	                        int newy = e.getRow() - gridviewy;
	                        
	                        Image spriteImage = trap.getSpriteImage().getScaledCopy(cellsize, cellsize);
	                        
	                        g.drawImage(spriteImage, getScreenX(newx), getScreenY(newy));
				        }
				    }
				}
				else if (e instanceof Shrine) {
                    if (visibility == Tile.Visibility.IsVisible.getType()) {
                        Shrine shrine = (Shrine) e;
                        
                        int newx = e.getColumn() - gridviewx;
                        int newy = e.getRow() - gridviewy;
                        
                        Image spriteImage = shrine.getSpriteImage().getScaledCopy(cellsize, cellsize);
                        
                        g.drawImage(spriteImage, getScreenX(newx), getScreenY(newy));
                    }
                }
				else if (e instanceof ItemEntity) {
                    if (visibility == Tile.Visibility.IsVisible.getType()) {
                        ItemEntity item = (ItemEntity) e;
                        
                        int newx = e.getColumn() - gridviewx;
                        int newy = e.getRow() - gridviewy;
                        
                        Image spriteImage = item.getSpriteImage().getScaledCopy(cellsize, cellsize);
                        
                        g.drawImage(spriteImage, getScreenX(newx), getScreenY(newy));
                    }
                }
				else if (e instanceof Chest) {
                    if (visibility == Tile.Visibility.IsVisible.getType()) {
                        Chest chest = (Chest) e;
                        
                        int newx = e.getColumn() - gridviewx;
                        int newy = e.getRow() - gridviewy;
                        
                        Image spriteImage = chest.getSpriteImage().getScaledCopy(cellsize, cellsize);
                        
                        g.drawImage(spriteImage, getScreenX(newx), getScreenY(newy));
                    }
                }
				else if (e instanceof PlayerTargetCursor) {
					PlayerTargetCursor cursor = (PlayerTargetCursor) e;
					
					int newx = e.getColumn() - gridviewx;
					int newy = e.getRow() - gridviewy;
														
					//Draw Ability that is Targetting
					Image targetAbilityImage = cursor.getTargetAbilityImage();
					if (targetAbilityImage != null) {
						targetAbilityImage = targetAbilityImage.getScaledCopy(cellsize / 2, cellsize / 2);
						//targetAbilityImage.setAlpha(.65f);
						g.drawImage(targetAbilityImage, getScreenX(newx) + (cellsize / 4), getScreenY(newy) + (cellsize / 4));
					}

					//Draw Target Cursor
					Image spriteImage = cursor.getSpriteImage().getScaledCopy(cellsize, cellsize);					
					g.drawImage(spriteImage, getScreenX(newx), getScreenY(newy));
				}
				else if (e instanceof FloatingContext) {
				    FloatingContext context = (FloatingContext) e;
				    
				    int newx = e.getColumn() - gridviewx;
                    int newy = e.getRow() - gridviewy;
                    
                    context.render(g, cellsize, getScreenX(newx), getScreenY(newy));
				}
			}
		}
		
		//Debug Mouse row and column
		if (GameLogic.DEBUG) {
			g.setColor(Color.white);
			g.drawString("Mouse Grid: " + mouser + ", " + mousec, viewportx + 10, viewporty + 30);
		}
		
		g.clearWorldClip();
	}
	
	private void calculateRotationMatrix() {
		if (level != null) {
			Random rng = GameLogic.rng;
			int levelw = level.getWidth();
			int levelh = level.getHeight();
			
			rotMatrix = new int[levelh][levelw];
			
			for (int r = 0; r < levelh; r++) {
				for (int c = 0; c < levelw; c++) {
					rotMatrix[r][c] = 0;
					if (Tile.shouldRotate(level.getTile(r, c))) {
						rotMatrix[r][c] = 90 * rng.nextInt(4); //Random Angle in 90 degree increments between 0 and 270 inclusive
					}
				}
			}
		}
	}
	
	public void setEntityFocus(Entity e) {
		focus = e;
	}
	
	/**
	 * Sets the level and recalculates the rotation matrix for tiles that should be randomly roatated.
	 */
	public void setLevel(Level level) {
		this.level = level;
		calculateRotationMatrix();
	}
	
	public void setCellSize(int size) {
		cellsize = size;
	}
	
	public void setViewportSize(int w, int h) {
		maxViewportW = w;
		maxViewportH = h;
	}
	
	public void setViewportPosition(int x, int y) {
		viewportx = x;
		viewporty = y;
	}
	
	public void zoom(int factor, boolean considerCooldown) {
		if (zoomCooldown <= 0 || !considerCooldown) {
			cellsize += factor * INC_CELL_SIZE;
		
			//Constrain
			cellsize = Math.max(cellsize, MIN_CELL_SIZE);
			cellsize = Math.min(cellsize, MAX_CELL_SIZE);
			
			//Cooldown
			zoomCooldown = ZOOM_COOLDOWN;
		}
	}
	
	public int getMouseRow() {
		return mouser;
	}
	
	public int getMouseColumn() {
		return mousec;
	}
	
	private int getScreenX(int gridpos) {
		return (gridpos * cellsize) + viewportx;
	}
	
	private int getScreenY(int gridpos) {
		return (gridpos * cellsize) + viewporty;
	}
}
