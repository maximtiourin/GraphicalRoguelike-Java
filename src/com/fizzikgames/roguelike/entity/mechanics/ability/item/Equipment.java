package com.fizzikgames.roguelike.entity.mechanics.ability.item;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.GameCharacterEvent;

/**
 * An equipment encapsulates a mini-inventory that maps each of it's slots to a specific position, and maintains
 * slot type information.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class Equipment {
	public enum SlotType {
		Head, Chest, Legs, Hands, Feet, MainHand, OffHand;
	}
	
	public static final SlotType[] indexMapping = {SlotType.Head, SlotType.Chest, SlotType.Legs, SlotType.Hands, SlotType.Feet, 
		SlotType.MainHand, SlotType.OffHand};
	public static final int INDEX_COUNT = indexMapping.length;
	protected GameCharacter gamechar;
	protected Inventory inv;
	
	public Equipment(GameCharacter gamechar) {
		this.gamechar = gamechar;
		this.inv = new Inventory(gamechar, 1, INDEX_COUNT);
	}
	
	/**
	 * Attempts to equip the item, returns true if successful. If a slot is provided then
	 * it will compare the given slot with the item type, otherwise it will try to find an
	 * appropriate slot to compare with.
	 */
	public boolean equipItem(EquippableItem item, Inventory.Slot targetSlot, boolean notify) {
		//First Check if we meet equip requirements and make sure it's not an already equipped item
		if (item.canEquip() && !item.isEquipped()) {
			//Attempt to equip
			for (SlotType e : item.getEquipType().getSlots()) {
				//Get the item in the requested slot type
				Inventory.Slot slot;
				EquippableItem slotItem;
				if (targetSlot == null) {
					slot = getSlotOfType(e);
					slotItem = getItemInSlot(slot);
				}
				else {
					slot = targetSlot;
					slotItem = getItemInSlot(slot);
				}
				//If this is a item going into the OffHand Slot, check to see if a twohand is equipped, and store it
				if (e == SlotType.OffHand) {
					EquippableItem twohanditem = getItemInSlot(getSlotOfType(SlotType.MainHand));
					if (twohanditem != null && (twohanditem.getEquipType() == EquippableItem.EquipType.TwoHand)) {
						slotItem = twohanditem;
					}
				}
				else if (item.getEquipType() == EquippableItem.EquipType.OneHand) {
					//If this is a onehanded item being equipped, and mainhand is full but not twohand, but offhand is free, then equip to offhand
					EquippableItem mainhand = getItemInSlot(getSlotOfType(SlotType.MainHand));
					EquippableItem offhandcheck = getItemInSlot(getSlotOfType(SlotType.OffHand));
					if (mainhand != null && offhandcheck == null && (mainhand.getEquipType() != EquippableItem.EquipType.TwoHand)) {
						slot = getSlotOfType(SlotType.OffHand);
						slotItem = null;
					}
				}
				//If this is a twohanded item, also get the item in the offhand slot
				Inventory.Slot offhandslot = getSlotOfType(SlotType.OffHand);
				EquippableItem offhand = (item.getEquipType() == EquippableItem.EquipType.TwoHand) ? getItemInSlot(offhandslot) : null;
				
				if (slotItem == null && offhand == null) {
					//Equip The Item, no slots taken up!
					gamechar.getInventory().removeItem(item, true); //Remove the equipped Item from the gamechars inventory.
					setItemInSlot(item, slot, false); //Set the equipment slot to hold the equipped item
					item.equip(); //Officially "equip" the item
					
					if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Equipment_Modified.getType()));
					
					return true;
				}
				else if (slotItem != null ^ offhand != null) {
					/* 
					 * Attempted Slot is taken up, or attempted Slot is not taken up and this is a twohand item and the offhand slot is taken.
					 * Swap the two items by equipping the intended one, and setting the old equipped one back into the inventory.
					 */
					EquippableItem moveItem = (slotItem != null) ? slotItem : offhand; //Set the item we want to move
					
					unequipItem(moveItem, false); //unequip old equipped item
					Inventory.Slot iteminvslot = gamechar.getInventory().findSlotWithItem(item); //Get the current inv slot of the current item
					gamechar.getInventory().setItemInSlot(null, iteminvslot, false); //remove current item from inventory
					gamechar.getInventory().setItemInSlot(moveItem, iteminvslot, true); //Add old equipped item to inventory
					setItemInSlot(item, slot, false); //Set the desired equipment slot to hold the equipped item
					item.equip(); //Officially "equip" the item
					
					if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Equipment_Modified.getType()));
					
					return true;
				}
				else {
					/* 
					 * Both attempted slot is taken up, and the item is two hand and the offhand slot is taken.
					 * Check if we have atleast one empty space in inventory to move the second item, and then perform desired
					 * swap operation with attempted slot, while also removing the offhand item from equipment and adding to inventory.
					 */
					if (gamechar.getInventory().getEmptySlotCount() > 0) {
						//Have room, perform equip operations
						unequipItem(slotItem, false); //Unequip attempted slot
						unequipItem(offhand, false); //Unequip offhand slot
						Inventory.Slot iteminvslot = gamechar.getInventory().findSlotWithItem(item); //Get the current inv slot of the current item
						gamechar.getInventory().setItemInSlot(null, iteminvslot, false); //remove current item from inventory
						gamechar.getInventory().setItemInSlot(slotItem, iteminvslot, false); //Add old equipped item to inventory
						gamechar.getInventory().attemptToInsertItem(offhand, true); //Insert the offhand at the next available position.
						setItemInSlot(item, slot, false); //Set the desired equipment slot to hold the equipped item
						item.equip(); //Officially "equip" the item
						
						if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Equipment_Modified.getType()));
						
						return true;
					}					
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Attempts to unequip the item, returns true if successful.
	 */
	public boolean unequipItem(EquippableItem item, boolean notify) {
		//Check if the item is even equipped first
		if (item.isEquipped()) {
			Inventory.Slot slot = getSlotContainingItem(item);
			setItemInSlot(null, slot, false); //Set the equipment slot to be null
			item.unequip(); //Officially "unequip" the item
			
			if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Equipment_Modified.getType()));
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Attempts to swap item a with item b in their respective slots, only works
	 * if they are compatible EquipTypes and the items are equipped in this equipment.
	 */
	public boolean attemptSwap(EquippableItem a, EquippableItem b, boolean notify) {
		if (a == b) return false; //Easy check to see these are two different items
		
		Inventory.Slot aslot = getSlotContainingItem(a);
		Inventory.Slot bslot = getSlotContainingItem(b);
		
		//Check if items are contained within.
		if (aslot != null && bslot != null) {
			//Check if items are compatible swaps
			if ((a == null ^ b == null) || (a.getEquipType() == b.getEquipType())) {
				//Swap Items
				setItemInSlot(b, aslot, false);
				setItemInSlot(a, bslot, false);
				
				if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Equipment_Modified.getType()));
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the slot corresponding with the equipment type
	 */
	public Inventory.Slot getSlotOfType(SlotType type) {
		for (int i = 0; i < INDEX_COUNT; i++) {
			if (indexMapping[i] == type) {
				return inv.new Slot(0, i);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the slot containing the item if there is one.
	 */
	public Inventory.Slot getSlotContainingItem(EquippableItem item) {
		for (int i = 0; i < INDEX_COUNT; i++) {
			Inventory.Slot slot = inv.new Slot(0, i);
			if (getItemInSlot(slot) == item) {
				return slot;
			}
		}
		
		return null;
	}
	
	public boolean isSlotTypeValidTargetForItem(SlotType slotType, EquippableItem item) {
		for (SlotType e : item.getEquipType().getSlots()) {
			if (e == slotType) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the equipped item in the given slot
	 */
	public EquippableItem getItemInSlot(Inventory.Slot slot) {
		return (EquippableItem) inv.getItemInSlot(slot);
	}
	
	public void setItemInSlot(Item item, Inventory.Slot slot, boolean notify) {
		inv.setItemInSlot(item, slot, false);
		
		if (notify) gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Equipment_Modified.getType()));
	}
	
	/**
	 * Resends all outgoing modifiers of equipped items
	 */
	public void resendOutgoingModifiers() {
		for (int i = 0; i < INDEX_COUNT; i++) {
			EquippableItem item = (EquippableItem) inv.getItemInSlot(inv.new Slot(0, i));
			if (item != null && item.isEquipped()) {
				item.sendOutgoingModifiers();
			}
		}
	}
	
	/**
	 * Returns true if a ranged weapon is equipped in the main slot
	 */
	public boolean isRangedWeaponEquipped() {
		EquippableItem item = getItemInSlot(getSlotOfType(SlotType.MainHand));
		
		if (item != null) {
			if (item.getItemType() == Item.ItemType.Ranged) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns true if a melee is equipped in the main slot
	 */
	public boolean isMeleeWeaponEquipped() {
		EquippableItem item = getItemInSlot(getSlotOfType(SlotType.MainHand));
		
		if (item != null) {
			if (item.getItemType() == Item.ItemType.Melee) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Quality of life method that reduces a method call by allowing to ask for an item
	 * in a slot type, instead of asking for the item in a specific slot.
	 */
	public EquippableItem getItemInSlotType(SlotType slotType) {
		return getItemInSlot(getSlotOfType(slotType));
	}
}
