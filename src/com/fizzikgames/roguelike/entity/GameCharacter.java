package com.fizzikgames.roguelike.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;

import com.fizzikgames.roguelike.entity.FloatingContextStack.ContextType;
import com.fizzikgames.roguelike.entity.mechanics.TriggerDispatcher;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEvent;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Equipment;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Inventory;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item;
import com.fizzikgames.roguelike.entity.mechanics.buff.Buff;
import com.fizzikgames.roguelike.entity.mechanics.buff.Debuff;
import com.fizzikgames.roguelike.entity.mechanics.buff.TemporaryStatModifier;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentHealth;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentMana;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_IncomingDamage;
import com.fizzikgames.roguelike.pathfinding.Mover;
import com.fizzikgames.roguelike.sprite.CharacterSprite;
import com.fizzikgames.roguelike.world.Level;

/**
 * A GameCharacter can be anything from a playerCharacter to an npc monster in the game world. It contains
 * it's own stats as well as game rules, and any other things useful for a character to contain.
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class GameCharacter extends Entity implements Mover {
	public static final int MAX_CHARACTERLEVEL = 99; //Maximum level for equation purposes.
	protected boolean initialized;
	protected boolean targeting;
	protected CharacterSprite sprite;
	protected HashMap<String, Stat> stats;
	protected ArrayList<Ability> abilities;
	protected ArrayList<TemporaryStatModifier> buffs;
	protected ArrayList<TemporaryStatModifier> debuffs;
	protected Inventory inventory;
	protected Equipment equipment;
	protected Level level;
	protected Item.ItemType damageType; //The current damage type of a physical attack by the player
	protected boolean hadCriticalStrike; //Whether or not the last attack critically struck
	protected int characterLevel;
	protected CopyOnWriteArrayList<GameCharacterListener> listeners;
	protected TriggerDispatcher triggerDispatcher;
	
	public GameCharacter(String spriteref, Level level, int r, int c, int characterLevel) {
		super(r, c);
		this.initialized = false;
		this.targeting = false;
		this.sprite = new CharacterSprite(spriteref);
		this.level = level;
		this.stats = new HashMap<String, Stat>();
		this.abilities = new ArrayList<Ability>();
		this.buffs = new ArrayList<TemporaryStatModifier>();
		this.debuffs = new ArrayList<TemporaryStatModifier>();
		this.inventory = null;
		this.equipment = new Equipment(this);
		this.characterLevel = characterLevel;
		this.damageType = Item.ItemType.Melee;
		this.hadCriticalStrike = false;
		this.listeners = new CopyOnWriteArrayList<GameCharacterListener>();
		this.triggerDispatcher = new TriggerDispatcher(this);
		setRenderPriority(RenderPriority.GameCharacter.getPriority());
		
		initialize();
	}
	
	/**
	 * Initializes the character, this is where things like adding generic base stats should occur.
	 */
	protected abstract void initialize();
	/**
	 * Tells the character that it should run final damage calculations for melee/ranged before damage
	 * is applied to a context.
	 */
	public abstract void finalizeOutgoingDamage();
	/**
	 * Tells the character that it should run final damage calculations for incoming damage before it is applied;
	 */
	public abstract void finalizeIncomingDamage();
	protected abstract void enterTargetingMode(Ability ability);
	protected abstract void cancelTargetingMode();
	
	@Override
	public void update(GameContainer gc, int delta) {
		sprite.step(delta);
	}
	
	public void dealDamage(double amount) {
	    Stat incoming =  getStat(Stat_IncomingDamage.REFERENCE);
	    Stat health = getStat(Stat_CurrentHealth.REFERENCE);
	    
	    incoming.addToBaseValue(amount); //add damage to incoming
        finalizeIncomingDamage(); //finalize incoming
        calculateStatModifiers(); //Calculate new incoming
        health.addToBaseValue(-incoming.getModifiedValue()); //reduce current health by incoming damage
        if (this instanceof PlayerCharacter) {
            level.getContextStack().addNewContext(ContextType.TakeDamage, null, (int) incoming.getModifiedValue() + "", getRow(), getColumn());
        }
        incoming.setBaseValue(0, false); //Set incoming to 0
        getTriggerDispatcher().notifyListeners(TriggerEvent.Type.GameChar_DamageTaken.getType()); //notify of damage taken
        calculateStatModifiers();
	}
	
	public void healHealth(double amount, boolean displayFloating) {
	    if (this instanceof PlayerCharacter && displayFloating) {
            level.getContextStack().addNewContext(ContextType.HealHealth, null, (int) amount + "", getRow(), getColumn());
        }
	    getStat(Stat_CurrentHealth.REFERENCE).addToBaseValue(amount);
        calculateStatModifiers();
	}
	
	public void healMana(double amount, boolean displayFloating) {
	    if (this instanceof PlayerCharacter && displayFloating) {
            level.getContextStack().addNewContext(ContextType.HealMana, null, (int) amount + "", getRow(), getColumn());
        }
        getStat(Stat_CurrentMana.REFERENCE).addToBaseValue(amount);
        calculateStatModifiers();
    }
	
	/**
	 * Returns the stat with the given reference
	 */
	public Stat getStat(String reference) {
		return stats.get(reference);
	}
	
	/**
	 * Returns a list of this character's stats.
	 * Will only return non-hidden stats if desired.
	 */
	public List<Stat> getStats(boolean pruneHidden) {
		Collection<Stat> list = stats.values();
		ArrayList<Stat> outlist = new ArrayList<Stat>();
		for (Stat e : list) {
			if (!(pruneHidden && e.isHidden())) {
				outlist.add(e);
			}
		}
		
		return outlist;
	}
	
	/**
	 * Adds a new stat to the character. If sendModifiers then
	 * the character will clear all modifiers and resend them with all of it's stats.
	 */
	public void addNewStat(Stat stat, boolean sendModifiers) {
		stats.put(stat.getReference(), stat);
		
		if (sendModifiers) {
			sendFreshStatModifiers();
		}
	}
	
	/**
	 * Adds a buff to the character
	 */
	public void addBuff(Buff buff) {
		this.addTemporaryStatModifier(buffs, buff, GameCharacterEvent.Type.Buff_Modified.getType());
	}
	
	public TemporaryStatModifier getBuff(String reference) {
		for (TemporaryStatModifier e : buffs) {
			if (e.getReference().equals(reference)) {
				return e;
			}
		}
		
		return null;
	}
	
	public List<TemporaryStatModifier> getBuffs(boolean pruneHidden) {
		if (pruneHidden) {
			ArrayList<TemporaryStatModifier> list = new ArrayList<TemporaryStatModifier>();
			for (TemporaryStatModifier e : buffs) {
				if (!e.isHidden()) {
					list.add(e);
				}
			}
			
			return list;
		}
		
		return buffs;
	}
	
	/**
	 * Removes the buff and recalculates modifiers.
	 * If the buff is not removing itself, then also remove the outgoing modifiers from buff before removing.
	 */
	public boolean removeBuff(TemporaryStatModifier buff, boolean selfRemoved) {		
		boolean removed = buffs.remove(buff);
		
		if (!selfRemoved) {
			buff.cleanupOutgoingModifiers();
			buff.cleanupTriggers();
		}
		
		calculateStatModifiers();
		
		return removed;
	}
	
	/**
	 * Adds a debuff to the character
	 */
	public void addDebuff(Debuff debuff) {
		this.addTemporaryStatModifier(debuffs, debuff, GameCharacterEvent.Type.Debuff_Modified.getType());
	}
	
	public TemporaryStatModifier getDebuff(String reference) {
		for (TemporaryStatModifier e : debuffs) {
			if (e.getReference().equals(reference)) {
				return e;
			}
		}
		
		return null;
	}
	
	public List<TemporaryStatModifier> getDebuffs(boolean pruneHidden) {
		if (pruneHidden) {
			ArrayList<TemporaryStatModifier> list = new ArrayList<TemporaryStatModifier>();
			for (TemporaryStatModifier e : debuffs) {
				if (!e.isHidden()) {
					list.add(e);
				}
			}
			
			return list;
		}
		
		return debuffs;
	}
	
	/**
	 * Removes the debuff and recalculates modifiers.
	 * If the debuff is not removing itself, then also remove the outgoing modifiers from buff before removing.
	 */
	public boolean removeDebuff(TemporaryStatModifier debuff, boolean selfRemoved) {		
		boolean removed = debuffs.remove(debuff);
		
		if (!selfRemoved) {
			debuff.cleanupOutgoingModifiers();
			debuff.cleanupTriggers();
		}
		
		calculateStatModifiers();
		
		return removed;
	}
	
	/**
	 * Ticks all buffs and debuffs and sends out modifications events,
	 * will not tick a buff that was just added from the given ability
	 */
	public void tickTemporaryStatModifiers(Ability ability) {
		ArrayList<TemporaryStatModifier> statmods = new ArrayList<TemporaryStatModifier>();
		statmods.addAll(buffs);
		statmods.addAll(debuffs);
		for (TemporaryStatModifier e : statmods) {
			if ((ability == null) || (!e.isFresh())) {
				e.tickDuration();
			}
			else if (e.isFresh()) {
				e.setFresh(false);
			}
		}
		
		this.notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Buff_Modified.getType()));
		this.notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Debuff_Modified.getType()));
	}
	
	/**
	 * Adds the ability to the character's list of abilities.
	 * Sends the Ability_Added event notification. 
	 */
	public void addAbility(Ability ability, boolean sendNotification) {
		abilities.add(ability);
		if (sendNotification) notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Ability_Added.getType()));
	}
	
	/**
	 * Tells the gamecharacter to use this ability, and if it passes all condition checks it will and then use up a turn.
	 * Returns true if the ability was successfully activated and a turn should be used.
	 */
	public boolean useAbility(Ability ability) {
		//Check if meets activateConditions
		if (ability.meetsActivateConditions()) {
			//Check if cooldown
			if (!ability.getTargetType().equals(Ability.TargetType.Passive)) {
				if (ability.getCurrentCooldown() <= 0) {
					//Check if enough mana
					if (getStat(Stat_CurrentMana.REFERENCE).getModifiedValue() >= ability.getCost()) {
						//Check Target Types
						if ((ability.getTarget() == null) && ability.getTargetType().equals(Ability.TargetType.TargetEnemy.getType())) {
							//Enter targetting mode and wait for target for ability before activating it.
							enterTargetingMode(ability);
						}
						else {
							getStat(Stat_CurrentMana.REFERENCE).addToBaseValue(-ability.getCost()); //Subtract cost
							
							ability.activate();
							
							calculateStatModifiers();
							
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Calculates all of the modified stat values of this character's stats.
	 * Should be called any time the character's stats/buffs/debuffs/etc change and modifiers
	 * have already been dispatched.
	 */
	public void calculateStatModifiers() {
		Iterator<Stat> it = stats.values().iterator();
		while (it.hasNext()) {
			Stat s = it.next();
			s.determineModifiedValue();		
		}
	}
	
	/**
	 * Returns a list of all abilities.
	 */
	public List<Ability> getAbilities(boolean pruneHidden) {
		if (pruneHidden) {
			ArrayList<Ability> list = new ArrayList<Ability>();
			
			for (Ability e : abilities) {
				if (!e.isHidden()) {
					list.add(e);
				}
			}
			
			return list;
		}		
		
		return abilities;
	}
	
	/**
	 * Returns the first ability with the given name.
	 */
	public Ability getAbility(String reference) {
		for (Ability e : abilities) {
			if (e.getName().equals(reference)) {
				return e;
			}
		}
		
		return null;
	}
	
	public void addListener(GameCharacterListener l) {
		listeners.add(l);
	}
	
	/**
	 * Notifies all interested listeners that the event has occured.
	 */
	public void notifyListeners(GameCharacterEvent e) {
		for (GameCharacterListener l : listeners) {
			if (l.getEventTypes().contains(e.getEventType())) {
				l.eventPerformed(e);
			}
		}
	}
	
	public boolean removeListener(GameCharacterListener l) {
		return listeners.remove(l);
	}
	
	public Level getLevel() {
		return level;
	}
	
	public Image getSpriteImage() {
		return sprite.image();
	}
	
	public int getCharacterLevel() {
		return characterLevel;
	}
	
	public void increaseCharacterLevel(int amount, boolean sendNotification) {
		setCharacterLevel(getCharacterLevel() + amount, sendNotification);
		if (this instanceof PlayerCharacter) {
		    level.getContextStack().addNewContext(ContextType.Level, null, "[" + getCharacterLevel() + "] Level Up!", getRow(), getColumn());
		}
	}
	
	public void setCharacterLevel(int level, boolean sendNotification) {
		int oldLevel = characterLevel;
		characterLevel = level;
		characterLevel = Math.max(1, characterLevel);
		characterLevel = Math.min(characterLevel, MAX_CHARACTERLEVEL);
		
		//System.out.println("Character Level: " + characterLevel);
		
		if (sendNotification && (characterLevel != oldLevel)) {
			notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Level_Modified.getType()));
		}
	}
	
	public boolean isTargeting() {
		return targeting;
	}
	
	public void setTargeting(boolean b) {
		targeting = b;
	}
	
	public TriggerDispatcher getTriggerDispatcher() {
		return triggerDispatcher;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public Equipment getEquipment() {
		return equipment;
	}
	
	public Item.ItemType getDamageType() {
		return damageType;
	}
	
	public boolean hadCriticalStrike() {
		return hadCriticalStrike;
	}
	
	public void startTurn() {
	    this.getTriggerDispatcher().notifyListeners(TriggerEvent.Type.GameChar_TurnStarted.getType());
	}
	
	/**
	 * Clears all incoming modifiers from stats, then resends fresh ones from all possible
	 * sources.
	 */
	protected void sendFreshStatModifiers() {
		//Clear modifiers
		Iterator<Stat> it = stats.values().iterator();
		while (it.hasNext()) {
			Stat s = it.next();
			s.cleanupModifiers();				
		}
		
		//Resend modifiers
			//Stats
		it = stats.values().iterator();
		while (it.hasNext()) {
			Stat s = it.next();
			s.sendOutgoingModifiers();				
		}
			//Buffs
		for (TemporaryStatModifier e : buffs) {
			e.sendOutgoingModifiers();
		}
			//Debuffs
		for (TemporaryStatModifier e : debuffs) {
			e.sendOutgoingModifiers();
		}
			//Equipment
		equipment.resendOutgoingModifiers();
		
		//Calculate new stats
		calculateStatModifiers();
	}
	
	protected void addTemporaryStatModifier(List<TemporaryStatModifier> list, TemporaryStatModifier statmod, String eventType) {
		//Add to list if list doesn't already contain it, otherwise add stack
		boolean contains = false;
		for (TemporaryStatModifier e : list) {
			if (e.getReference().equals(statmod.getReference())) {
				e.addStacks(statmod.getCurrentStacks());
				contains = true;
			}
		}
		
		if (!contains) {
			list.add(statmod);
			statmod.sendOutgoingModifiers();
			statmod.sendTriggers(this.getTriggerDispatcher());
		}
		
		//Sends modifiers and Recalculates Stat Modifiers
		calculateStatModifiers();
		
		//Notify of appropriate game character event
		this.notifyListeners(new GameCharacterEvent(this, eventType));
	}
}
