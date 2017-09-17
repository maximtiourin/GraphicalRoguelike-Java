package com.fizzikgames.roguelike.entity.mechanics.ability.item;

import java.util.ArrayList;

import org.newdawn.slick.Image;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;
import com.fizzikgames.roguelike.entity.mechanics.TriggerDispatcher;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEventListener;

/**
 * An equippable item is an item that can be equipped into an equipment slot.
 * It will send any triggers and modifiers when equipped, and clean them up when unequipped
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class EquippableItem extends Item {
	public enum EquipType {
		Head("Head", new Equipment.SlotType[]{Equipment.SlotType.Head}), 
		Chest("Chest", new Equipment.SlotType[]{Equipment.SlotType.Chest}), 
		Legs("Legs", new Equipment.SlotType[]{Equipment.SlotType.Legs}), 
		Hands("Hands", new Equipment.SlotType[]{Equipment.SlotType.Hands}), 
		Feet("Feet", new Equipment.SlotType[]{Equipment.SlotType.Feet}), 
		OneHand("One Hand", new Equipment.SlotType[]{Equipment.SlotType.MainHand, Equipment.SlotType.OffHand}), 
		TwoHand("Two Hand", new Equipment.SlotType[]{Equipment.SlotType.MainHand}),
		MainHand("Main Hand", new Equipment.SlotType[]{Equipment.SlotType.MainHand}), 
		OffHand("Off Hand", new Equipment.SlotType[]{Equipment.SlotType.OffHand});
		
		private String type;
		private Equipment.SlotType[] slots;
		
		private EquipType(String type, Equipment.SlotType[] slots) {
			this.type = type;
			this.slots = slots;
		}
		
		public String getType() {
			return type;
		}
		
		public Equipment.SlotType[] getSlots() {
			return slots;
		}
	}
	
	protected boolean equipped; //Holds status of whether or not is is currently equipped to with unknown outside operations.
	protected EquipType equipType;
	protected ArrayList<ModifyTarget> modifyTargets;
	protected ArrayList<TriggerEventListener> triggers;
	protected ArrayList<Affix> affixes;
	
	public EquippableItem(GameCharacter gamechar, String name, Image gameWorldImage, Image iconImage, String targetType, 
			int totalCooldown, int cost, int range, int maxStacks, int currentStacks, Rarity rarity, ItemType itemType, GlobalCooldownGroup gcg,
			EquipType equipType, ArrayList<Affix> affixes) {
		super(gamechar, name, gameWorldImage, iconImage, targetType, 
				totalCooldown, cost, range, maxStacks, currentStacks, rarity, itemType, gcg, true);
		this.equipped = false;
		this.equipType = equipType;
		this.modifyTargets = new ArrayList<ModifyTarget>();
		this.triggers = new ArrayList<TriggerEventListener>();
		this.affixes = affixes;
		
		initialize();
	}
	/**
	 * Returns true if the gamecharacter can equip this item.
	 */
	public abstract boolean canEquip();
	
	@Override
	public void initialize() {
		for (Affix e : affixes) {
			modifyTargets.addAll(e.getModifyTargets());
			triggers.addAll(e.getTriggers());
		}
	}
	
	@Override
	public void activate() {
	}
	
	@Override
	public boolean isValidTarget(GameCharacter target) {
		return false;
	}

	@Override
	public boolean targetUsesLineOfSight() {
		return false;
	}

	@Override
	public boolean meetsActivateConditions() {
		return false;
	}
	
	/**
	 * Sends triggers and modifiers signifying that the character is wearing this item.
	 */
	public void equip() {
		sendTriggers(gamechar.getTriggerDispatcher());
		sendOutgoingModifiers();
		equipped = true;
		
		gamechar.calculateStatModifiers();
	}
	
	/**
	 * Cleans up sent triggers and modifiers, signifying that the character is no longer wearing this item.
	 */
	public void unequip() {
		cleanupOutgoingModifiers();
		cleanupTriggers();
		equipped = false;
		
		gamechar.calculateStatModifiers();
	}
	
	/**
	 * Sends all of this statModifier's modifiers to the appropriate targets. Character should call cleanupModifiers
	 * then this whenever a new stat is added to the character.
	 */
	public void sendOutgoingModifiers() {
		for (ModifyTarget e : modifyTargets) {
			gamechar.getStat(e.getReference()).addIncomingModifier(e.getModifier());
		}
	}
	
	/**
	 * Removes the outgoing modifiers of all modifyTargets for cleanup purposes.
	 */
	public void cleanupOutgoingModifiers() {
		for (ModifyTarget e : modifyTargets) {
			removeOutgoingModifier(e);
		}
	}
	
	/**
	 * Removes the outgoing modifier of the modify target.
	 */
	public void removeOutgoingModifier(ModifyTarget tar) {
		gamechar.getStat(tar.getReference()).removeIncomingModifier(tar.getModifier());
	}
	
	/**
	 * Sends all of this statmodifiers's triggers to the given dispatcher
	 */
	public void sendTriggers(TriggerDispatcher dispatcher) {
		for (TriggerEventListener e : triggers) {
			e.addToDispatcher(dispatcher);
		}
	}
	
	/**
	 * Removes this statmodifierss triggers (and clears their appropriate dispatchers) for cleanup purposes.
	 */
	public void cleanupTriggers() {
		for (TriggerEventListener e : triggers) {
			e.removeFromDispatcher();
		}
	}
	
	public EquipType getEquipType() {
		return equipType;
	}
	
	public boolean isEquipped() {
		return equipped;
	}
	
	/**
	 * Returns a string containing the descriptions of all affixes, and styled.
	 * Also includes attack range if a Ranged weapon
	 */
	protected String getAffixCompoundString() {
		String out = "";
		
		//Ranged Distance
		if (getItemType() == ItemType.Ranged) {
			out += "<6 + ";
			out += getRange() + " Meter Attack Range";
			out += ">";
			out += "<br>";
		}
		//Regular Affixes
		for (Affix e : affixes) {
			out += "<6 + ";
			out += e.getDescription();
			out += ">";
			out += "<br>";
		}
		
		return out;
	}
}
