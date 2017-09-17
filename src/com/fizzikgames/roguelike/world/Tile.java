package com.fizzikgames.roguelike.world;

import java.awt.Point;

public class Tile {
	public enum Visibility {
		NotVisible((short) 0), IsVisible((short) 1), WasVisible((short) 2);
		
		private short visibility;
		
		private Visibility(short vis) {
			visibility = vis;
		}
		
		public short getType() {
			return visibility;
		}
	}
	
	//Primary Variables//////////////////////////////
	public static final int DIVISIONS = 1; //How many steps can be made inside of a tile (Ex: 32/10 = how much character coordinates need to be moved to move up a tile)
	public static final int WIDTH = 32;
	public static final int HEIGHT = 32;
	public static final int SPRITESHEET_WIDTH = 512;
	public static final int SPRITESHEET_HEIGHT = 512;
	
	//Tile Constant ids
	public static final short ID_r00c00 = 							0; //Tile Floor
	public static final short ID_r00c01 = 							1; //Stone Floor
	public static final short ID_r00c02 =                           2; //Cobblestone Wall
	public static final short ID_r00c03 =                           3; //Purple Dungeon Wall
	public static final short ID_r00c04 =                           4; //Mud Wall
	public static final short ID_r00c05 =                           5; //Mud Floor
	public static final short ID_r00c06 =                           6;
	public static final short ID_r01c00 =                           16; //Stair Down
    public static final short ID_r01c01 =                           17; //Stair Up
	
	//Tile extras
	public static final short EXTRA_ARROW_UP =	 					0;
	public static final short EXTRA_ARROW_DOWN =	 				1;
	public static final short EXTRA_TARGETSELECTION_VALID =	 		2;
	public static final short EXTRA_WALL_JLEFTTOPRIGHT =	 		205;
	public static final short EXTRA_WALL_JLEFTBOTRIGHT =	 		206;
	public static final short EXTRA_WALL_JRIGHTTOPLEFT =	 		207;
	public static final short EXTRA_WALL_JRIGHTBOTLEFT =	 		208;
	public static final short EXTRA_WALL_JTOPBOTLEFT =	 			209;
	public static final short EXTRA_WALL_JTOPBOTRIGHT =	 			210;
	public static final short EXTRA_WALL_JBOTTOPLEFT =	 			211;
	public static final short EXTRA_WALL_JBOTTOPRIGHT =	 			212;
	public static final short EXTRA_WALL_2CTOPRIGHTBOTLEFT =	 	213;
	public static final short EXTRA_WALL_2CTOPLEFTBOTRIGHT =	 	214;
	public static final short EXTRA_WALL_4C =	 					215;
	public static final short EXTRA_WALL_3CBOTLEFT =	 			216;
	public static final short EXTRA_WALL_3CTOPLEFT = 				217;
	public static final short EXTRA_WALL_3CTOPRIGHT = 				218;
	public static final short EXTRA_WALL_3CBOTRIGHT = 				219;
	public static final short EXTRA_WALL_2CLEFT = 					220;
	public static final short EXTRA_WALL_2CTOP = 					221;
	public static final short EXTRA_WALL_2CRIGHT = 					222;
	public static final short EXTRA_WALL_2CBOT = 					223;
	public static final short EXTRA_WALL_CTOPLEFT = 				224;
	public static final short EXTRA_WALL_CTOPRIGHT = 				225;
	public static final short EXTRA_WALL_VERTICAL = 				226;
	public static final short EXTRA_WALL_TOP = 						227;
	public static final short EXTRA_WALL_BOT = 						228;
	public static final short EXTRA_WALL_HORIZONTAL_TOP = 			229;
	public static final short EXTRA_WALL_VERTICAL_LEFT = 			230;
	public static final short EXTRA_WALL_CENTER = 					231;
	public static final short EXTRA_WALL_TLEFT = 					232;
	public static final short EXTRA_WALL_TRIGHT = 					233;
	public static final short EXTRA_WALL_TOPLEFT = 					234;
	public static final short EXTRA_WALL_TOPRIGHT = 				235;
	public static final short EXTRA_WALL_1CTOPLEFT = 				236;
	public static final short EXTRA_WALL_1CTOPRIGHT = 				237;
	public static final short EXTRA_WALL_1CBOTLEFT = 				238;
	public static final short EXTRA_WALL_1CBOTRIGHT = 				239;
	public static final short EXTRA_WALL_CBOTLEFT = 				240;
	public static final short EXTRA_WALL_CBOTRIGHT = 				241;
	public static final short EXTRA_WALL_HORIZONTAL = 				242;
	public static final short EXTRA_WALL_LEFT = 					243;
	public static final short EXTRA_WALL_RIGHT = 					244;
	public static final short EXTRA_WALL_HORIZONTAL_BOT = 			245;
	public static final short EXTRA_WALL_VERTICAL_RIGHT = 			246;
	public static final short EXTRA_WALL_SINGLE = 					247;
	public static final short EXTRA_WALL_TTOP = 					248;
	public static final short EXTRA_WALL_TBOT = 					249;
	public static final short EXTRA_WALL_BOTLEFT = 					250;
	public static final short EXTRA_WALL_BOTRIGHT = 				251;
	public static final short EXTRA_SELECTED = 						253;
	public static final short EXTRA_HIGHLIGHTED = 					254;
	public static final short EXTRA_EMPTY = 						255;
	
	/**
	 * Returns the x and y position of the tile based on its tileid in the spritesheet
	 */
	public static Point getSpriteSheetPosition(int tileid) {
		//Quick case
		if (tileid == 0) return new Point(0, 0);
		
		int left = tileid;
		int x = 0, y = 0;
		
		while ((left > 0) && (Math.floor(SPRITESHEET_WIDTH / WIDTH) <= left)) {
			left -= Math.floor(SPRITESHEET_WIDTH / WIDTH);
			y++;
		}
		x = left;
		
		return new Point(x, y);
	}
	
	/**
	 * Returns true if the tile should rotate randomly, false if not
	 */
	public static boolean shouldRotate(short tile) {
		short id = getTileId(tile);
		switch (id) {
			case ID_r00c00: {
				return false;
			}
			case ID_r01c00: {
                return false;
            }
			case ID_r01c01: {
                return false;
            }
		}
		
		return true;
	}
	
	/**
	 * Creates a new tile with the given information
	 */
	public static short createTile(short tileid, short visibility) {
		short tile = tileid;
		tile = (short) (tile << 2); //Shift 8 bits of tileid left twice, 2 least significant bits become visibility 0-3 (but only 0-2 are needed)
		tile = (short) (tile | visibility); //Set visibility at 2 least significant bits.
		
		return tile;
	}
	
	public static short getTileId(short tile) {
		short tileid = (short) (tile >> 2); //Shift right twice to set 8 tileid bits in 8 least significant bits.
		
		return tileid;
	}
	
	public static short getVisibility(short tile) {
		short visibility = (short) (tile & 3); //Bitmask least two significant bits to get the visibility value
		
		return visibility;
	}
	
	public static short setVisibility(short tile, short visibility) {
		return createTile(getTileId(tile), visibility);
	}
	
	/**
	 * Returns true if the tile id is a wall
	 */
	public static boolean isWall(short tileid) {
		switch (tileid) {
			case ID_r00c02: {
				return true;
			}
			case ID_r00c03: {
                return true;
            }
			case ID_r00c04: {
                return true;
            }
		}
		
		return false;
	}
	
	/**
	 * Returns the wall type
	 */
	public static int getWallType(int[][] w) {
		if (isCenter(w, 1, 1)) {
			return EXTRA_WALL_4C;
		}
		else if (is1CTopLeft(w, 1, 1)) {
			return EXTRA_WALL_1CTOPLEFT;
		}
		else if (is1CTopRight(w, 1, 1)) {
			return EXTRA_WALL_1CTOPRIGHT;
		}
		else if (is1CBotLeft(w, 1, 1)) {
			return EXTRA_WALL_1CBOTLEFT;
		}
		else if (is1CBotRight(w, 1, 1)) {
			return EXTRA_WALL_1CBOTRIGHT;
		}
		else if (is2CBot(w, 1, 1)) {
			return EXTRA_WALL_2CBOT;
		}
		else if (is2CRight(w, 1, 1)) {
			return EXTRA_WALL_2CRIGHT;
		}
		else if (is2CTop(w, 1, 1)) {
			return EXTRA_WALL_2CTOP;
		}
		else if (is2CLeft(w, 1, 1)) {
			return EXTRA_WALL_2CLEFT;
		}
		else if (is2CTopLeftBotRight(w, 1, 1)) {
			return EXTRA_WALL_2CTOPLEFTBOTRIGHT;
		}
		else if (is2CTopRightBotLeft(w, 1, 1)) {
			return EXTRA_WALL_2CTOPRIGHTBOTLEFT;
		}
		else if (is3CBotRight(w, 1, 1)) {
			return EXTRA_WALL_3CBOTRIGHT;
		}
		else if (is3CTopRight(w, 1, 1)) {
			return EXTRA_WALL_3CTOPRIGHT;
		}
		else if (is3CTopLeft(w, 1, 1)) {
			return EXTRA_WALL_3CTOPLEFT;
		}
		else if (is3CBotLeft(w, 1, 1)) {
			return EXTRA_WALL_3CBOTLEFT;
		}
		else if (isTBot(w, 1, 1)) {
			return EXTRA_WALL_TBOT;
		}
		else if (isTTop(w, 1, 1)) {
			return EXTRA_WALL_TTOP;
		}
		else if (isTLeft(w, 1, 1)) {
			return EXTRA_WALL_TLEFT;
		}
		else if (isTRight(w, 1, 1)) {
			return EXTRA_WALL_TRIGHT;
		}
		else if (isJLeftTopRight(w, 1, 1)) {
			return EXTRA_WALL_JLEFTTOPRIGHT;
		}
		else if (isJLeftBotRight(w, 1, 1)) {
			return EXTRA_WALL_JLEFTBOTRIGHT;
		}
		else if (isJRightTopLeft(w, 1, 1)) {
			return EXTRA_WALL_JRIGHTTOPLEFT;
		}
		else if (isJRightBotLeft(w, 1, 1)) {
			return EXTRA_WALL_JRIGHTBOTLEFT;
		}
		else if (isJTopBotLeft(w, 1, 1)) {
			return EXTRA_WALL_JTOPBOTLEFT;
		}
		else if (isJTopBotRight(w, 1, 1)) {
			return EXTRA_WALL_JTOPBOTRIGHT;
		}
		else if (isJBotTopLeft(w, 1, 1)) {
			return EXTRA_WALL_JBOTTOPLEFT;
		}
		else if (isJBotTopRight(w, 1, 1)) {
			return EXTRA_WALL_JBOTTOPRIGHT;
		}
		else if (isTopLeft(w, 1, 1)) {
			return EXTRA_WALL_TOPLEFT;
		}
		else if (isTopRight(w, 1, 1)) {
			return EXTRA_WALL_TOPRIGHT;
		}
		else if (isBotLeft(w, 1, 1)) {
			return EXTRA_WALL_BOTLEFT;
		}
		else if (isBotRight(w, 1, 1)) {
			return EXTRA_WALL_BOTRIGHT;
		}
		else if (isCTopLeft(w, 1, 1)) {
			return EXTRA_WALL_CTOPLEFT;
		}
		else if (isCTopRight(w, 1, 1)) {
			return EXTRA_WALL_CTOPRIGHT;
		}
		else if (isCBotLeft(w, 1, 1)) {
			return EXTRA_WALL_CBOTLEFT;
		}
		else if (isCBotRight(w, 1, 1)) {
			return EXTRA_WALL_CBOTRIGHT;
		}
		else if (isHorizontalBot(w, 1, 1)) {
			return EXTRA_WALL_HORIZONTAL_BOT;
		}
		else if (isHorizontalTop(w, 1, 1)) {
			return EXTRA_WALL_HORIZONTAL_TOP;
		}
		else if (isVerticalLeft(w, 1, 1)) {
			return EXTRA_WALL_VERTICAL_LEFT;
		}
		else if (isVerticalRight(w, 1, 1)) {
			return EXTRA_WALL_VERTICAL_RIGHT;
		}
		else if (isHorizontal(w, 1, 1)) {
			return EXTRA_WALL_HORIZONTAL;
		}
		else if (isVertical(w, 1, 1)) {
			return EXTRA_WALL_VERTICAL;
		}
		else if (isBot(w, 1, 1)) {
			return EXTRA_WALL_BOT;
		}
		else if (isTop(w, 1, 1)) {
			return EXTRA_WALL_TOP;
		}
		else if (isLeft(w, 1, 1)) {
			return EXTRA_WALL_LEFT;
		}
		else if (isRight(w, 1, 1)) {
			return EXTRA_WALL_RIGHT;
		}
		else if (isSingle(w, 1, 1)) {
			return EXTRA_WALL_SINGLE;
		}
		
		return -1;
	}
	
	/**
	 * +x+
	 * ooo
	 * +x+
	 * 
	 */
	private static boolean isHorizontal(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 0) && (w[r][c + 1] == 1)) {
			if ((w[r + 1][c] == 0) && ((w[r][c - 1] == 1))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * +o+
	 * xox
	 * +o+
	 * 
	 */
	private static boolean isVertical(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1) && (w[r][c + 1] == 0)) {
			if ((w[r + 1][c] == 1) && ((w[r][c - 1] == 0))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * +o+
	 * xox
	 * +x+
	 * 
	 */
	private static boolean isBot(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1) && (w[r][c + 1] == 0)) {
			if ((w[r + 1][c] == 0) && ((w[r][c - 1] == 0))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * +x+
	 * xox
	 * +o+
	 * 
	 */
	private static boolean isTop(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 0) && (w[r][c + 1] == 0)) {
			if ((w[r + 1][c] == 1) && ((w[r][c - 1] == 0))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * +x+
	 * xoo
	 * +x+
	 * 
	 */
	private static boolean isLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 0) && (w[r][c + 1] == 1)) {
			if ((w[r + 1][c] == 0) && ((w[r][c - 1] == 0))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * +x+
	 * oox
	 * +x+
	 * 
	 */
	private static boolean isRight(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 0) && (w[r][c + 1] == 0)) {
			if ((w[r + 1][c] == 0) && ((w[r][c - 1] == 1))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * ooo
	 * ooo
	 * xxx
	 * 
	 */
	private static boolean isHorizontalBot(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1) && (w[r][c + 1] == 1)) {
			if ((w[r + 1][c] == 0) && ((w[r][c - 1] == 1))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * xxx
	 * ooo
	 * ooo
	 * 
	 */
	private static boolean isHorizontalTop(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 0) && (w[r][c + 1] == 1)) {
			if ((w[r + 1][c] == 1) && ((w[r][c - 1] == 1))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * xoo
	 * xoo
	 * xoo
	 * 
	 */
	private static boolean isVerticalLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1) && (w[r][c + 1] == 1)) {
			if ((w[r + 1][c] == 1) && ((w[r][c - 1] == 0))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * oox
	 * oox
	 * oox
	 * 
	 */
	private static boolean isVerticalRight(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1) && (w[r][c + 1] == 0)) {
			if ((w[r + 1][c] == 1) && ((w[r][c - 1] == 1))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * xox
	 * ooo
	 * ?x?
	 * 
	 */
	private static boolean isTBot(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && (w[r + 1][c] == 0)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ?x?
	 * ooo
	 * xox
	 * 
	 */
	private static boolean isTTop(int[][] w, int r, int c) {
		if ((w[r + 1][c - 1] == 0) && (w[r + 1][c] == 1)) {
			if ((w[r + 1][c + 1] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && (w[r - 1][c] == 0)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ?ox
	 * xoo
	 * ?ox
	 * 
	 */
	private static boolean isTLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c + 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r + 1][c + 1] == 0) && ((w[r][c - 1] == 0))) {
				if ((w[r][c + 1] == 1) && (w[r + 1][c] == 1)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xo?
	 * oox
	 * xo?
	 * 
	 */
	private static boolean isTRight(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r][c - 1] == 1) && ((w[r][c + 1] == 0))) {
				if ((w[r + 1][c - 1] == 0) && (w[r + 1][c] == 1)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * +x+
	 * xox
	 * +x+
	 * 
	 */
	private static boolean isSingle(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 0) && (w[r][c + 1] == 0)) {
			if ((w[r + 1][c] == 0) && ((w[r][c - 1] == 0))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * xox
	 * ooo
	 * xox
	 * 
	 */
	private static boolean isCenter(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1) && (w[r][c + 1] == 1)) {
			if ((w[r + 1][c] == 1) && ((w[r][c - 1] == 1))) {
				if ((w[r - 1][c - 1] == 0) && ((w[r - 1][c + 1] == 0))) {
					if ((w[r + 1][c - 1] == 0) && ((w[r + 1][c + 1] == 0))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xoo
	 * ooo
	 * ooo
	 * 
	 */
	private static boolean is1CTopLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 1) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 1))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 1))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * oox
	 * ooo
	 * ooo
	 * 
	 */
	private static boolean is1CTopRight(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 1) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 1))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 1))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ooo
	 * ooo
	 * xoo
	 * 
	 */
	private static boolean is1CBotLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 1) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 1) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 0))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 1))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ooo
	 * ooo
	 * oox
	 * 
	 */
	private static boolean is1CBotRight(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 1) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 1) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 1))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 0))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ooo
	 * ooo
	 * xox
	 * 
	 */
	private static boolean is2CBot(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 1) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 1) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 0))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 0))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * oox
	 * ooo
	 * oox
	 * 
	 */
	private static boolean is2CRight(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 1) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 1))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 0))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xox
	 * ooo
	 * ooo
	 * 
	 */
	private static boolean is2CTop(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 1))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 1))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xoo
	 * ooo
	 * xoo
	 * 
	 */
	private static boolean is2CLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 1) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 0))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 1))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xoo
	 * ooo
	 * oox
	 * 
	 */
	private static boolean is2CTopLeftBotRight(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 1) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 1))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 0))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * oox
	 * ooo
	 * xoo
	 * 
	 */
	private static boolean is2CTopRightBotLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 1) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 0))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 1))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * oox
	 * ooo
	 * xox
	 * 
	 */
	private static boolean is3CBotRight(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 1) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 0))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 0))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xox
	 * ooo
	 * oox
	 * 
	 */
	private static boolean is3CTopRight(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 1))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 0))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xox
	 * ooo
	 * xoo
	 * 
	 */
	private static boolean is3CTopLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 0))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 1))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xoo
	 * ooo
	 * xox
	 * 
	 */
	private static boolean is3CBotLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 1) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 0))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 0))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ?ox
	 * xoo
	 * ?oo
	 * 
	 */
	private static boolean isJLeftTopRight(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 0) && ((w[r][c - 1] == 0))) {
				if ((w[r][c + 1] == 1)) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 1))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ?oo
	 * xoo
	 * ?ox
	 * 
	 */
	private static boolean isJLeftBotRight(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 1) && ((w[r][c - 1] == 0))) {
				if ((w[r][c + 1] == 1)) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 0))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xo?
	 * oox
	 * oo?
	 * 
	 */
	private static boolean isJRightTopLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if (((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 0) && ((w[r + 1][c - 1] == 1))) {
					if ((w[r + 1][c] == 1)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * oo?
	 * oox
	 * xo?
	 * 
	 */
	private static boolean isJRightBotLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 1) && (w[r - 1][c] == 1)) {
			if (((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 0) && ((w[r + 1][c - 1] == 0))) {
					if ((w[r + 1][c] == 1)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ?x?
	 * ooo
	 * xoo
	 * 
	 */
	private static boolean isJTopBotLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 0)) {
			if (((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 0))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 1))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ?x?
	 * ooo
	 * oox
	 * 
	 */
	private static boolean isJTopBotRight(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 0)) {
			if (((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1) && ((w[r + 1][c - 1] == 1))) {
					if ((w[r + 1][c] == 1) && ((w[r + 1][c + 1] == 0))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xoo
	 * ooo
	 * ?x?
	 * 
	 */
	private static boolean isJBotTopLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 0) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 1) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1)) {
					if ((w[r + 1][c] == 0)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * oox
	 * ooo
	 * ?x?
	 * 
	 */
	private static boolean isJBotTopRight(int[][] w, int r, int c) {
		if ((w[r - 1][c - 1] == 1) && (w[r - 1][c] == 1)) {
			if ((w[r - 1][c + 1] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r][c + 1] == 1)) {
					if ((w[r + 1][c] == 0)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xxx
	 * xoo
	 * xoo
	 * 
	 */
	private static boolean isTopLeft(int[][] w, int r, int c) {
		if ((w[r + 1][c] == 1) && (w[r][c + 1] == 1)) {
			if ((w[r - 1][c] == 0) && ((w[r][c - 1] == 0))) {
				if ((w[r + 1][c + 1] == 1)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ?x?
	 * oox
	 * oo?
	 * 
	 */
	private static boolean isTopRight(int[][] w, int r, int c) {
		if ((w[r + 1][c] == 1) && (w[r][c + 1] == 0)) {
			if ((w[r - 1][c] == 0) && ((w[r][c - 1] == 1))) {
				if ((w[r + 1][c - 1] == 1)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * ?oo
	 * xoo
	 * ?x?
	 * 
	 */
	private static boolean isBotLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1) && (w[r - 1][c + 1] == 1)) {
			if ((w[r][c - 1] == 0) && ((w[r][c + 1] == 1))) {
				if ((w[r + 1][c] == 0)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * oo?
	 * oox
	 * ?x?
	 * 
	 */
	private static boolean isBotRight(int[][] w, int r, int c) {
		if ((w[r + 1][c] == 0) && (w[r][c + 1] == 0)) {
			if ((w[r - 1][c] == 1) && ((w[r][c - 1] == 1))) {
				if ((w[r - 1][c - 1] == 1)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xxx
	 * xoo
	 * xox
	 * 
	 */
	private static boolean isCTopLeft(int[][] w, int r, int c) {
		if ((w[r + 1][c] == 1) && (w[r][c + 1] == 1)) {
			if ((w[r - 1][c] == 0) && ((w[r][c - 1] == 0))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * xxx
	 * oox
	 * xox
	 * 
	 */
	private static boolean isCTopRight(int[][] w, int r, int c) {
		if ((w[r + 1][c] == 1) && (w[r][c - 1] == 1)) {
			if ((w[r - 1][c] == 0) && ((w[r][c + 1] == 0))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * ?ox
	 * xoo
	 * ?x?
	 * 
	 */
	private static boolean isCBotLeft(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1) && (w[r - 1][c + 1] == 0)) {
			if ((w[r][c - 1] == 0) && ((w[r][c + 1] == 1))) {
				if ((w[r + 1][c] == 0)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * xo?
	 * oox
	 * ?xx
	 * 
	 */
	private static boolean isCBotRight(int[][] w, int r, int c) {
		if ((w[r - 1][c] == 1) && (w[r][c - 1] == 1)) {
			if ((w[r + 1][c] == 0) && ((w[r][c + 1] == 0))) {
				return true;
			}
		}
		
		return false;
	}
}
