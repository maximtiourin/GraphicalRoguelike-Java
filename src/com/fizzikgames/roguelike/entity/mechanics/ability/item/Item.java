package com.fizzikgames.roguelike.entity.mechanics.ability.item;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.GameCharacterEvent;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability;

/**
 * An item is a higher function ability that can be held in an inventory
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Item extends Ability {
	/*public enum Type {
		Head, Chest, Shoulders, Back, Legs, Waist, Hands, Feet, Neck, Fingers, MainHand, OffHand
	}
	
	public enum SubType {
		OneHand, TwoHand
	}*/
	public enum ItemType {
		Melee("Melee"),
		Ranged("Ranged"),
		Gear("Gear"),
		Key("Key"),
		Consumable("Consumable");
		
		private String type;
		
		private ItemType(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
	}
	
	public enum GlobalCooldownGroup {
		HealthElixir, ManaElixir, None;
	}
	
	public enum Rarity {
		Legendary("Legendary", Color.orange, 8, 2f),
		Epic("Epic", new Color(162, 0, 255), 6, 1.5f),
		Rare("Rare", new Color(8, 84, 255), 4, 1.35f),
		Uncommon("Uncommon", new Color(100, 255, 84), 3, 1.2f),
		Common("Common", Color.white, 1, 1.1f),
		Junk("Junk", Color.gray, 0, 1f);
		
		private Color color;
		private String type;
		private int affixes;
		private float multiplier;
		
		private Rarity(String type, Color color, int affixes, float multiplier) {
			this.type = type;
			this.color = color;
			this.affixes = affixes;
			this.multiplier = multiplier;
		}
		
		public Color getColor() {
			return color;
		}
		
		public String getType() {
			return type;
		}
		
		public int getAffixes() {
		    return affixes;
		}
		
		public float getMultiplier() {
		    return multiplier;
		}
	}
	
	public static HashMap<GlobalCooldownGroup, Integer> GlobalCooldown = new HashMap<GlobalCooldownGroup, Integer>();
	protected Image gameWorldImage;
	protected Rarity rarity;
	protected GlobalCooldownGroup gcg;
	protected ItemType itemType;
	protected int maxStacks;
	protected int currentStacks;
	
	public Item(GameCharacter gamechar, String name, Image gameWorldImage, Image iconImage, String targetType, 
			int totalCooldown, int cost, int range, int maxStacks, int currentStacks, Rarity rarity, ItemType itemType, GlobalCooldownGroup gcg) {
		super(gamechar, name, iconImage, targetType, totalCooldown, cost, range, false);
		this.gameWorldImage = gameWorldImage;
		this.maxStacks = maxStacks;
		this.currentStacks = currentStacks;
		this.rarity = rarity;
		this.itemType = itemType;
		this.gcg = gcg;
		
		if (gcg != GlobalCooldownGroup.None) {
			if (GlobalCooldown.containsKey(gcg)) {
				this.currentCooldown = GlobalCooldown.get(gcg);
			}
			else {
				GlobalCooldown.put(gcg, this.currentCooldown);
			}
		}
		
		initialize();
	}
	
	public Item(GameCharacter gamechar, String name, Image gameWorldImage, Image iconImage, String targetType, 
			int totalCooldown, int cost, int range, int maxStacks, int currentStacks, Rarity rarity, ItemType itemType, GlobalCooldownGroup gcg,
			boolean affixes) {
		super(gamechar, name, iconImage, targetType, totalCooldown, cost, range, false);
		this.gameWorldImage = gameWorldImage;
		this.maxStacks = maxStacks;
		this.currentStacks = currentStacks;
		this.rarity = rarity;
		this.itemType = itemType;
		this.gcg = gcg;
		
		if (gcg != GlobalCooldownGroup.None) {
			if (GlobalCooldown.containsKey(gcg)) {
				this.currentCooldown = GlobalCooldown.get(gcg);
			}
			else {
				GlobalCooldown.put(gcg, this.currentCooldown);
			}
		}
	}
	
	/**
	 * Called by the Item constructor, should initialize the modifyTargets of this item, as well as triggers.
	 */
	protected abstract void initialize();
	
	@Override
	public void startCooldown() {
		currentCooldown = getTotalCooldown();
		if (gcg != GlobalCooldownGroup.None) GlobalCooldown.put(gcg, this.currentCooldown);
	}
	
	@Override
	public int getCurrentCooldown() {
		if (gcg != GlobalCooldownGroup.None) return GlobalCooldown.get(gcg);
		else return currentCooldown;
	}
	
	@Override
	/**
	 * Will tick the cooldown if this item does not share a cooldown group, otherwise
	 * the inventory is in charge of ticking global cooldowns.
	 */
	public void cooldownTick() {
		if (gcg == GlobalCooldownGroup.None) {
			if (currentCooldown > 0) {
				currentCooldown--;
			}
		}
	}
	
	/**
	 * Returns the image of the item used to render it inside the game world.
	 */
	public Image getGameWorldImage() {
		return gameWorldImage;
	}
	
	public ItemType getItemType() {
		return itemType;
	}
	
	public boolean isStackable() {
		return (maxStacks > 1);
	}
	
	public int getMaxStacks() {
		return maxStacks;
	}
	
	public int getCurrentStacks() {
		return currentStacks;
	}
	
	public void setCurrentStacks(int stacks) {
		currentStacks = stacks;
	}
	
	/**
	 * Attempts to consume the item, if it is a consumable it will use up a stack, if it runs out of stacks
	 * it will destroy itself. Sends an inventory modified event.
	 */
	public void consume() {
		if (getItemType() == ItemType.Consumable || getItemType() == ItemType.Key) {
			currentStacks--;
			
			if (currentStacks <= 0) {
				gamechar.getInventory().removeItem(this, false);
			}
			
			gamechar.notifyListeners(new GameCharacterEvent(gamechar, GameCharacterEvent.Type.Inventory_Modified.getType()));
		}
	}
	
	public GlobalCooldownGroup getGlobalCooldownGroup() {
		return gcg;
	}
	
	public Rarity getRarity() {
		return rarity;
	}
	
	public String getAestheticName() {
		return getName();
	}
}
