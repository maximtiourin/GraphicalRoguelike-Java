package com.fizzikgames.roguelike.world;

import java.util.ArrayList;
import java.util.Random;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.entity.Chest;
import com.fizzikgames.roguelike.entity.Monster;
import com.fizzikgames.roguelike.entity.MonsterFactory;
import com.fizzikgames.roguelike.entity.MonsterFactory.AttackType;
import com.fizzikgames.roguelike.entity.MonsterFactory.MonsterType;
import com.fizzikgames.roguelike.entity.Shrine.ShrineType;
import com.fizzikgames.roguelike.entity.PlayerCharacter;
import com.fizzikgames.roguelike.entity.Shrine;
import com.fizzikgames.roguelike.entity.Trap;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.ItemFactory;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.ItemFactory.BroadItemType;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.ItemFactory.ItemType;
import com.fizzikgames.roguelike.pathfinding.Astar;
import com.fizzikgames.roguelike.pathfinding.ManhattanHeuristic;
import com.fizzikgames.roguelike.util.RandomBag;

public class RandomLevel extends Level {
    private class Room {
        private int width;
        private int height;
        private int row;
        private int column;
        private boolean connected;
        private boolean[][] occupied;
        
        public Room(int width, int height) {
            this.width = width;
            this.height = height;
            this.row = 0;
            this.column = 0;
            this.connected = false;
            this.occupied = null;
        }
        
        public Cell getRandomCell() {
            Random rng = GameLogic.rng;
            return new Cell(row + rng.nextInt(height), column + rng.nextInt(width));
        }
        
        public void startOccupyTesting() {
            occupied = new boolean[height][width];
        }
        
        public boolean isOccupied(int relr, int relc) {
            return occupied[relr][relc];
        }
        
        public void setOccupied(int relr, int relc) {
            occupied[relr][relc] = true;
        }
        
        public int getNonOccupiedCount() {
            int count = 0;
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    if (!isOccupied(r, c)) {
                        count++;
                    }
                }
            }
            
            return count;
        }
        
        public Cell getRandomNonOccupiedCell() {
            Random rng = GameLogic.rng;
            
            //First check if completely full
            int count = getNonOccupiedCount();
            if (count <= 0) {
                return null;
            }
            
            //Get random cell from bag
            ArrayList<Cell> cells = new ArrayList<Cell>();
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    if (!isOccupied(r, c)) {
                        cells.add(new Cell(row + r, column + c));
                    }
                }
            }
            
            return cells.get(rng.nextInt(cells.size()));
        }
        
        public int getRow() { return row; }
        public void setRow(int row) { this.row = row; }
        public int getColumn() { return column; }
        public void setColumn(int column) { this.column = column; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public boolean isConnected() { return connected; }
        public void setConnected(boolean b) { this.connected = b; }
    }
    
    private static final int BOUND_PADDING = 10;
	private int minWidth;
	private int minHeight;
	private int maxWidth;
	private int maxHeight;
	private int minMonsters;
	private int maxMonsters;
	private int minChests;
	private int maxChests;
	private int minTraps;
	private int maxTraps;
	private int minShrines;
	private int maxShrines;
	private RandomBag<BroadItemType> chestItemPool;
	private int minChestItems;
	private int maxChestItems;
	private RandomBag<MonsterType> monsterTypePool;
	private RandomBag<AttackType> monsterAttackTypePool;
	private Room[] rooms;
	
	public RandomLevel(PlayerCharacter player, String tilesetref, String extratilesetref, int minWidth, int minHeight, int maxWidth, int maxHeight,
			int minMonsters, int maxMonsters, int minChests, int maxChests, int minTraps, int maxTraps,	int minShrines, int maxShrines, 
			RandomBag<BroadItemType> chestItemPool, int minChestItems, int maxChestItems, RandomBag<MonsterType> monsterTypePool, 
			RandomBag<AttackType> monsterAttackTypePool, RandomBag<BroadItemType> monsterLootPool) {
	    
		super(tilesetref, extratilesetref);
		
		this.setPlayerCharacter(player);
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.minMonsters = minMonsters;
		this.maxMonsters = maxMonsters;
		this.minChests = minChests;
		this.maxChests = maxChests;
		this.minTraps = minTraps;
		this.maxTraps = maxTraps;
		this.minShrines = minShrines;
		this.maxShrines = maxShrines;
		this.chestItemPool = chestItemPool;
		this.minChestItems = minChestItems;
		this.maxChestItems = maxChestItems;
		this.monsterTypePool = monsterTypePool;
		this.monsterAttackTypePool = monsterAttackTypePool;
		this.lootTable = monsterLootPool;
		this.rooms = null;
	}
	
	@Override
	public void generate() {
		Random rng = GameLogic.rng;
				
		//Determine Dimensions
		width = minWidth + rng.nextInt(maxWidth - minWidth + 1);
		height = minHeight + rng.nextInt(maxHeight - minHeight + 1);
		this.tiles = new short[height][width];
		
		final int ewidth = width - (2 * BOUND_PADDING);
		final int eheight = height - (2 * BOUND_PADDING);
		
		//Roll feature counts
		final int monsterCount = minMonsters + rng.nextInt(maxMonsters - minMonsters + 1);
		final int chestCount = minChests + rng.nextInt(maxChests - minChests + 1);
		final int trapCount = minTraps + rng.nextInt(maxTraps - minTraps + 1);
		final int shrineCount = minShrines + rng.nextInt(maxShrines - minShrines + 1);
		
		//Determine the max and min room width and height for a room.
        final int minRooms = (ewidth * eheight) / 300;
        final int maxRooms = (ewidth * eheight) / 150;
        final int roomCount = minRooms + rng.nextInt(maxRooms - minRooms + 1);
        final float widthsqrt = (float) Math.sqrt((float) ewidth * 2f);
        final float heightsqrt = (float) Math.sqrt((float) eheight * 2f);
        final float roomMinWidth = ((float) ewidth * .5f) / widthsqrt;
        final float roomMinHeight = ((float) eheight * .5f) / heightsqrt;
        final float roomMaxWidth = ((float) ewidth * 2f) / widthsqrt;
        final float roomMaxHeight = ((float) eheight * 2f) / heightsqrt;
        
        //Determine Wall and Floor Types
        final short[] wallTypes = {Tile.ID_r00c02, Tile.ID_r00c03, Tile.ID_r00c04};
        final short[] floorTypes = {Tile.ID_r00c00, Tile.ID_r00c01, Tile.ID_r00c05};
		
		//Determine Dungeon main Wall and floor type
		final short dungeonWallType = wallTypes[rng.nextInt(wallTypes.length)];
		final short dungeonFloorType = floorTypes[rng.nextInt(floorTypes.length)];
		
		//Fill Dungeon with walls		
		for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                setTile(Tile.createTile(dungeonWallType, Tile.Visibility.NotVisible.getType()), r, c);
            }
        }
		
		//Generate and Place Rooms
		rooms = new Room[roomCount];
		for (int i = 0; i < roomCount; i++) {
		    boolean placed = false;
		    while (!placed) {
		        int newr = BOUND_PADDING + 1 + rng.nextInt(eheight - (int) roomMaxWidth - 1);
		        int newc = BOUND_PADDING + 1 + rng.nextInt(ewidth - (int) roomMaxHeight - 1);
		        int neww = (int) roomMinWidth + rng.nextInt((int) roomMaxWidth - (int) roomMinWidth + 1);
		        int newh = (int) roomMinHeight + rng.nextInt((int) roomMaxHeight - (int) roomMinHeight + 1);
		        
		        Room testRoom = new Room(neww, newh);
		        testRoom.setRow(newr);
		        testRoom.setColumn(newc);
		        
		        if (areaWithinPaddedBounds(newr, newc, neww, newh)) {
		            if (!doesRoomOverlap(testRoom)) {
		                placed = true;
		                rooms[i] = testRoom;
		            }
		        }
		    }
		}
		
		//Connect Rooms
		int connectionCount = roomCount;
		ArrayList<Cell> cells = new ArrayList<Cell>();
		for (int i = 0; i < connectionCount; i++) {
		    Room a = rooms[i];
		    
		    ArrayList<Room> roomList = new ArrayList<Room>();
		    for (int ii = 0; ii < rooms.length; ii++) {
		        if (rooms[ii] != a) {
		            roomList.add(rooms[ii]);
		        }
		    }
		    
		    Room b = roomList.get(rng.nextInt(roomList.size()));
		    
		    final int sidestepChance = 10;
		    Cell apoint = a.getRandomCell();
		    Cell bpoint = b.getRandomCell();
		    
		    //Lazy Walk
		    while (!bpoint.equals(apoint)) {
		        int dc = 0; //Column Change
		        int dr = 0; //Row Change
		        
		        int randnum = rng.nextInt(101);
		        //Side Step
		        if (randnum < sidestepChance) {
		            //Change column
		            if (bpoint.getColumn() != apoint.getColumn()) {
		                if (apoint.getColumn() > bpoint.getColumn()) {
		                    dc = 1;
		                    
		                }
		                else {
		                    dc = -1;
		                }
		                
		                //Move Bpoint and add new connected cell
		                bpoint.addColumn(dc);
		                cells.add(new Cell(bpoint.getRow(), bpoint.getColumn()));
		            }
		            
		            //Change Row if we didnt change column
		            if (dc == 0) {
		                if (bpoint.getRow() != apoint.getRow()) {
		                    if (apoint.getRow() > bpoint.getRow()) {
	                            dr = 1;
	                            
	                        }
	                        else {
	                            dr = -1;
	                        }
		                    
		                    //Move Bpoint and add new connected cell
	                        bpoint.addRow(dr);
	                        cells.add(new Cell(bpoint.getRow(), bpoint.getColumn()));
		                }
		            }
		        }
		    }
		    
		    a.setConnected(true);
		    b.setConnected(true);
		}
		
		//Set Connected Tiles (Add all nearby tiles as well to create large corridors)
		for (Cell e : cells) {
		    //If border room
		    Room testRoom = new Room(3, 3);
		    testRoom.setRow(e.getRow() - 1);
		    testRoom.setColumn(e.getColumn() - 1);
		    if (doesRoomOverlap(testRoom)) {
		        fillRect(dungeonFloorType, e.getRow(), e.getColumn(), 1, 1);
		    }
		    else {
		        //If not border room
		        fillRect(dungeonFloorType, e.getRow() - 1, e.getColumn() - 1, 3, 3);
		    }
		}
		
		//Set Room Tiles
        for (int i = 0; i < rooms.length; i++) {
            Room room = rooms[i];
            
            fillRect(dungeonFloorType, room.getRow(), room.getColumn(), room.getWidth(), room.getHeight());
        }
		
		ArrayList<Room> connectedRooms = new ArrayList<Room>();
		for (Room e : rooms) {
		    if (e.isConnected() && areaWithinPaddedBounds(e.getRow(), e.getColumn(), e.getWidth(), e.getHeight())) {
		        connectedRooms.add(e);
		    }
		}
		
		//Place Player in a random Room
		Room playerRoom = connectedRooms.get(rng.nextInt(connectedRooms.size()));
		player.setRow(playerRoom.getRow() + (playerRoom.getHeight() / 2));
		player.setColumn(playerRoom.getColumn() + (playerRoom.getWidth() / 2));
		
		//Set Up Stair Tile for Flair
		fillRect(Tile.ID_r01c01, player.getRow(), player.getColumn(), 1, 1);
		
		//Pick Stair Down, and set tile
		connectedRooms.remove(playerRoom);
		Room stairRoom = connectedRooms.get(rng.nextInt(connectedRooms.size()));
		stairDown = new Cell(stairRoom.getRow() + (stairRoom.getHeight() / 2), stairRoom.getColumn() + (stairRoom.getWidth() / 2));
		fillRect(Tile.ID_r01c00, stairDown.getRow(), stairDown.getColumn(), 1, 1);
		
		//Set Traps
		connectedRooms.remove(stairRoom);
		for (Room e : connectedRooms) {
		    e.startOccupyTesting();
		}
		
		int fails = 0;
		final int maxfails = 100;
		int i = 0;
		while (i < trapCount) {
		    Room room = connectedRooms.get(rng.nextInt(connectedRooms.size()));
		    
		    Cell cell = room.getRandomNonOccupiedCell();
		    
		    if (cell != null) {
		        room.setOccupied(cell.getRow() - room.getRow(), cell.getColumn() - room.getColumn()); //Occupy Room
		        Trap trap = new Trap(this); //Create Trap
		        trap.setRow(cell.getRow());
		        trap.setColumn(cell.getColumn());
		        player.addListener(trap);
		        this.addEntity(trap);
		    }
		    else {
		        if (fails < maxfails) {
		            fails++;
		            i--;
		        }
		    }
		    
		    i++;
		}
		
		//Set Shrines
		ShrineType[] shrinetypes = {ShrineType.Allsight, ShrineType.Ragnar};
        fails = 0;
        i = 0;
        while (i < shrineCount) {
            Room room = connectedRooms.get(rng.nextInt(connectedRooms.size()));
            
            Cell cell = room.getRandomNonOccupiedCell();
            
            if (cell != null) {
                room.setOccupied(cell.getRow() - room.getRow(), cell.getColumn() - room.getColumn()); //Occupy Room
                Shrine shrine = new Shrine(this, shrinetypes[rng.nextInt(shrinetypes.length)]); //Create Shrine
                shrine.setRow(cell.getRow());
                shrine.setColumn(cell.getColumn());
                player.addListener(shrine);
                this.addEntity(shrine);
            }
            else {
                if (fails < maxfails) {
                    fails++;
                    i--;
                }
            }
            
            i++;
        }
        
        //Set Chests
        fails = 0;
        i = 0;
        while (i < chestCount) {
            Room room = connectedRooms.get(rng.nextInt(connectedRooms.size()));
            
            Cell cell = room.getRandomNonOccupiedCell();
            
            if (cell != null) {
                //Determine Items
                int itemCount = minChestItems + rng.nextInt(maxChestItems - minChestItems + 1);
                Item[] items = new Item[itemCount];
                for (int ii = 0; ii < itemCount; ii++) {
                    ItemType[] types = chestItemPool.getRandomObject().getTypes();
                    ItemType type = types[rng.nextInt(types.length)];
                    items[ii] = ItemFactory.createItem(type, player, player.getCharacterLevel());
                }
                
                //Place Chest
                room.setOccupied(cell.getRow() - room.getRow(), cell.getColumn() - room.getColumn()); //Occupy Room
                Chest chest = new Chest(this, items);
                chest.setRow(cell.getRow());
                chest.setColumn(cell.getColumn());
                player.addListener(chest);
                this.addEntity(chest);
            }
            else {
                if (fails < maxfails) {
                    fails++;
                    i--;
                }
            }
            
            i++;
        }
        
        //Set Monsters
        fails = 0;
        i = 0;
        while (i < monsterCount) {
            Room room = connectedRooms.get(rng.nextInt(connectedRooms.size()));
            
            Cell cell = room.getRandomNonOccupiedCell();
            
            if (cell != null) {
                //Determine Monster
                MonsterType type = monsterTypePool.getRandomObject();
                AttackType attackType = monsterAttackTypePool.getRandomObject();
                Monster monster = MonsterFactory.createMonster(type, attackType, this, player.getCharacterLevel());
                
                //Place Monster
                room.setOccupied(cell.getRow() - room.getRow(), cell.getColumn() - room.getColumn()); //Occupy Room
                monster.setRow(cell.getRow());
                monster.setColumn(cell.getColumn());
                this.addGameCharacter(monster);
            }
            else {
                if (fails < maxfails) {
                    fails++;
                    i--;
                }
            }
            
            i++;
        }
		
        //Initialize Astar
        astar = new Astar(new ManhattanHeuristic(), this, width * height, 0, true, false, false);
        
        //Set Generated
		playerTurn = true;
		generated = true;
	}
	
	/**
	 * Draws the tiletype across the given rectangle, constraining it to be within bounds.
	 */
	private void fillRect(short tileType, int r, int c, int w, int h) {
	    if (w < 0 || h < 0) {
	        return;
	    }
	    
	    for (int rr = r; rr < r + h; rr++) {
	        for (int cc = c; cc < c + w; cc++) {
	            setTile(Tile.createTile(tileType, Tile.Visibility.NotVisible.getType()), rr, cc);
	        }
	    }
	}
	
	private boolean areaWithinPaddedBounds(int r, int c, int w, int h) {
	    if ((r > BOUND_PADDING && r < height - BOUND_PADDING - 1)
	            && (c > BOUND_PADDING && c < width - BOUND_PADDING - 1)) {
	        return true;
	    }
	    
	    return false;
	}
	
	private boolean doesRoomOverlap(Room room) {
	    if (rooms == null) return false;
	    if (rooms.length <= 0) return false;
	    
	    int r = room.getRow();
	    int c = room.getColumn();
	    int w = room.getWidth();
	    int h = room.getHeight();
	    
	    for (int i = 0; i < rooms.length; i++) {
	        if (rooms[i] != null) {
    	        int checkr = rooms[i].getRow();
    	        int checkc = rooms[i].getColumn();
    	        int checkw = rooms[i].getWidth();
    	        int checkh = rooms[i].getHeight();
    	        
    	        if (!((checkr > r + h) || (checkr + checkh < r) || (checkc > c + w) || (checkc + checkw < c))) {
    	            return true;
    	        }
	        }
	    }
	    
	    return false;
	}
}
