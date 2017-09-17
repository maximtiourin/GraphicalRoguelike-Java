package com.fizzikgames.roguelike.entity.mechanics.ability.item;

import java.util.Map;
import java.util.Set;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.GameCharacterEvent;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item.ItemType;

/**
 * An inventory holds a collections of items and facilitates operations with them.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class Inventory {
	public class Slot {
		private int row;
		private int column;
		
		public Slot(int r, int c) {
			this.row = r;
			this.column = c;
		}
		
		public int getRow() {
			return row;
		}
		
		public int getColumn() {
			return column;
		}
	}
	
	protected GameCharacter gamechar;
	protected Item[][] inventory;
	protected int rows;
	protected int columns;
	
	public Inventory(GameCharacter gamechar, int rows, int columns) {
		inventory = new Item[rows][columns];
		this.rows = rows;
		this.columns = columns;
		this.gamechar = gamechar;
	}
	
	/**
	 * Attempts to insert the item into the inventory, returns true on success.
	 * Will attempt to stack like-items if possible
	 */
	public boolean attemptToInsertItem(Item item, boolean notify) {
		//First check for stacks, then available slot.
		Slot next = findSlotWithItemNameAndStackRoom(item.getName());
		boolean transferAfterNext = false;
		if (next != null) {
			Item nextItem = getItemInSlot(next);
			
			if (areStackCandidates(item, nextItem)) {
				int bmaxstacks = nextItem.getMaxStacks();
				int astacks = item.getCurrentStacks();
				int bstacks = nextItem.getCurrentStacks();
				
				if (canFullyStack(item, nextItem)) {					
					nextItem.setCurrentStacks(astacks + bstacks);
					if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Inventory_Modified.getType()));
					return true;
				}
				else {					
					if (bstacks < bmaxstacks) {
						//After finding next available slot, stack to fill b.
						transferAfterNext = true;
					}
				}
			}
		}
		
		//Check next available
		Slot next2 = getNextAvailableSlot();
			
		if (next2 != null) {
			setItemInSlot(item, next2, notify);
			
			if (next != null && transferAfterNext) {
				//Add remaining stack
				transferStacks(next2, next, false);
			}
			
			if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Inventory_Modified.getType()));
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the next available slot in a Top-Down Left-Right direction.
	 * Returns null if no available slot.
	 */
	public Slot getNextAvailableSlot() {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				if (inventory[r][c] == null) {
					return new Slot(r, c);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Swaps the contents of two slots, can also notify of an inventory modification event
	 */
	public void swapSlots(Slot a, Slot b, boolean notify) {
		Item ia = getItemInSlot(a);
		Item ib = getItemInSlot(b);
		
		if (areStackCandidates(ia, ib)) {
			transferStacks(a, b, false);
		}
		else {
			setItemInSlot(ia, b, false);
			setItemInSlot(ib, a, false);
		}
		
		if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Inventory_Modified.getType()));
	}
	
	/**
	 * Attempts to transfer stacks from item a to item b,
	 * performing specific operations based on their stack information.
	 */
	public void transferStacks(Slot aSlot, Slot bSlot, boolean notify) {
		Item a = getItemInSlot(aSlot);
		Item b = getItemInSlot(bSlot);
		
		if (areStackCandidates(a, b)) {
			int bmaxstacks = b.getMaxStacks();
			int astacks = a.getCurrentStacks();
			int bstacks = b.getCurrentStacks();
			
			if (bstacks == bmaxstacks) {
				//B has max stacks, just swap stacks
				a.setCurrentStacks(bstacks);
				b.setCurrentStacks(astacks);
			}
			else if ((astacks + bstacks) <= bmaxstacks) {
				//Adding both will keep B in bounds, so set B and remove A
				b.setCurrentStacks(astacks + bstacks);
				setItemInSlot(null, aSlot, false);
			}
			else {
				//B is not at max, but adding A will bring it over, so add part of A
				int diff = bmaxstacks - bstacks;
				a.setCurrentStacks(astacks - diff);
				b.setCurrentStacks(bmaxstacks);
			}
		}
		
		if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Inventory_Modified.getType()));
	}
	
	/**
	 * Returns true if the two items are able to stack with each other
	 */
	public boolean areStackCandidates(Item a, Item b) {
		if (a != null && b != null) {
			if (a.getName().equals(b.getName())) {
				if (a.isStackable() && b.isStackable()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Returns true if item a can fully stack onto item b
	 */
	public boolean canFullyStack(Item a, Item b) {
		int bmaxstacks = b.getMaxStacks();
		int astacks = a.getCurrentStacks();
		int bstacks = b.getCurrentStacks();
		
		if ((astacks + bstacks) <= bmaxstacks) {
			return true;
		}
		
		return false;
	}
	
	public Item getItemInSlot(Slot slot) {
		if (slot == null) {
			return null;
		}
		
		return inventory[slot.getRow()][slot.getColumn()];
	}
	
	/**
	 * Sets the contents of a slot, can also notify of an inventory modification event
	 * Is not stack-aware.
	 */
	public void setItemInSlot(Item item, Slot slot, boolean notify) {		
		inventory[slot.getRow()][slot.getColumn()] = item;
		
		if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Inventory_Modified.getType()));
	}
	
	/**
	 * Returns the slot with the item if the inventory contains the item
	 */
	public Slot findSlotWithItem(Item findItem) {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				Item item = getItemInSlot(new Slot(r, c));
				if (item == findItem) {
					return new Slot(r, c);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the first slot with the item of given name if the inventory contains an item 
	 * with the given reference name.
	 */
	public Slot findSlotWithItemName(String name) {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				Item item = getItemInSlot(new Slot(r, c));
				if (item != null && item.getName().equals(name)) {
					return new Slot(r, c);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the first slot with the item of given name if the inventory contains
	 * an item with the given reference name, and it has stack room left.
	 */
	public Slot findSlotWithItemNameAndStackRoom(String name) {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				Item item = getItemInSlot(new Slot(r, c));
				if (item != null && item.getName().equals(name) && (item.getCurrentStacks() < item.getMaxStacks())) {
					return new Slot(r, c);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the first item found with the given item type
	 */
	public Item findItemWithItemType(ItemType type) {
	    for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Item item = getItemInSlot(new Slot(r, c));
                if (item != null && item.getItemType() == type) {
                    return item;
                }
            }
        }
	    
	    return null;
	}
	
	/**
	 * Attempts to remove the item object from the inventory. Can send notification event.
	 */
	public boolean removeItem(Item item, boolean notify) {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				if (inventory[r][c] == item) {
					setItemInSlot(null, new Slot(r, c), false);
					
					if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Inventory_Modified.getType()));
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Will tick item cooldowns, and the global cooldown of all groups.
	 */
	public void cooldownTick(Item item) {
		//Tick individual items
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				if (inventory[r][c] != null) {
					if ((item == null) || (item != inventory[r][c])) {
						inventory[r][c].cooldownTick();
					}
				}
			}
		}
		
		//Tick cooldown groups
		Set<Map.Entry<Item.GlobalCooldownGroup, Integer>> gcgs = Item.GlobalCooldown.entrySet();
		for (Map.Entry<Item.GlobalCooldownGroup, Integer> e : gcgs) {
			if ((item == null) || (item.getGlobalCooldownGroup() != e.getKey())) {
				int value = e.getValue();
				if (value > 0) value--;
				e.setValue(value);
			}
		}
	}
	
	public int getEmptySlotCount() {
		int count = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				if (inventory[r][c] == null) {
					count++;
				}
			}
		}
		
		return count;
	}
	
	public int getRowCount() {
		return rows;
	}
	
	public int getColumnCount() {
		return columns;
	}
}
