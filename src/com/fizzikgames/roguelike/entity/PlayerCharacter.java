package com.fizzikgames.roguelike.entity;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.entity.mechanics.Formula;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEvent;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEventListener;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability_ArcaneExplosion;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability_Blink;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability_DefaultMeleeAttack;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability_DefaultRangedAttack;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability_Fireball;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability_Teleport;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Equipment;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Inventory;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.ItemFactory;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat;
import com.fizzikgames.roguelike.entity.mechanics.stat.StatListener;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_AbilityEffectiveness;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_Armor;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_BlockAmount;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_BlockChance;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CriticalHitChance;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CriticalHitDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentExperience;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentHealth;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentMana;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_Dexterity;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_DodgeChance;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_HealthRegeneration;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_IncomingDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_IncomingDamageReduction;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_Intelligence;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_MagicFind;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_ManaRegeneration;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_OutgoingMeleeDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_OutgoingRangedDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_SightRadius;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_Strength;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalExperience;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalHealth;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalMana;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TrapSightRadius;
import com.fizzikgames.roguelike.pathfinding.Node;
import com.fizzikgames.roguelike.world.Level;

public class PlayerCharacter extends GameCharacter {
	private static final int INVENTORY_ROWS = 8;
	private static final int INVENTORY_COLUMNS = 8;
	private static final int BASE_TOTAL_EXP = 1000;
	private static final int MAX_TOTAL_EXP = 1000000;
	protected Ability targetAbility;
	protected boolean w;
	protected boolean a;
	protected boolean s;
	protected boolean d;
	protected int actiontimer;
	protected boolean dead;
	
	public PlayerCharacter(String sprite, Level level, int r, int c) {
		super(sprite, level, r, c, 1);
		setRenderPriority(RenderPriority.PlayerCharacter.getPriority());
		w = false;
		a = false;
		s = false;
		d = false;
		actiontimer = 0;
		dead = false;
	}

	@Override
	protected void initialize() {
		final PlayerCharacter player = this;
		Stat stat;
		
		//Init Level
		this.setCharacterLevel(1, false);
		
		//Add Base Stats, wait to send modifiers until the final base stat is added.
		/* Current Experience */
		stat = new Stat_CurrentExperience(this, 0);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_CurrentExperience_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Total Experience */
		stat = new Stat_TotalExperience(this, BASE_TOTAL_EXP);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_TotalExperience_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Current Health */
		stat = new Stat_CurrentHealth(this, 0);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				//Here can check for states like death, as well as send out modification events
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_CurrentHealth_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Total Health */
		stat = new Stat_TotalHealth(this, 175);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_TotalHealth_Modified.getType()));
			}			
		});
		addNewStat(stat, true);
		/* Current Mana */
		stat = new Stat_CurrentMana(this, 0);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Total Mana */
		stat = new Stat_TotalMana(this, 375);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_TotalMana_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Strength */
		stat = new Stat_Strength(this, 1);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_Strength_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Dexterity */
		stat = new Stat_Dexterity(this, 1);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_Dexterity_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Intelligence */
		stat = new Stat_Intelligence(this, 1);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_Intelligence_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Health Regeneration */
		stat = new Stat_HealthRegeneration(this, 0.25);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_HealthRegeneration_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Mana Regeneration */
		stat = new Stat_ManaRegeneration(this, 2);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_ManaRegeneration_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Critical Hit Chance */
		stat = new Stat_CriticalHitChance(this, 0.08);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_CriticalHitChance_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Critical Hit Damage */
		stat = new Stat_CriticalHitDamage(this, 1.0);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_CriticalHitDamage_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Incoming Damage */
		stat = new Stat_IncomingDamage(this, 0);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_IncomingDamage_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Outgoing Melee Damage */
		stat = new Stat_OutgoingMeleeDamage(this, 0);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_OutgoingMeleeDamage_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Outgoing Ranged Damage */
		stat = new Stat_OutgoingRangedDamage(this, 0);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_OutgoingRangedDamage_Modified.getType()));
			}			
		});
		addNewStat(stat, false);
		/* Sight Radius */
		stat = new Stat_SightRadius(this, 6);
		stat.addModificationListener(new StatListener() {
			@Override
			public void statEventOccured(Stat stat) {
				player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_SightRadius_Modified.getType()));
			}			
		});
		addNewStat(stat, true);
		/* Trap Sight Radius */
        stat = new Stat_TrapSightRadius(this, 2);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                player.notifyListeners(new GameCharacterEvent(player, GameCharacterEvent.Type.Stat_TrapSightRadius_Modified.getType()));
            }           
        });
        addNewStat(stat, true);
		
		//Add Default Abilities
		addAbility(new Ability_DefaultMeleeAttack(this), false);
		addAbility(new Ability_DefaultRangedAttack(this), false);
		addAbility(new Ability_Fireball(this), false);
		addAbility(new Ability_ArcaneExplosion(this), false);
		addAbility(new Ability_Blink(this), false);
		addAbility(new Ability_Teleport(this), false);
		
		//Construct Inventory
		this.inventory = new Inventory(this, INVENTORY_ROWS, INVENTORY_COLUMNS);
	}
	
	@Override
	public void update(GameContainer gc, int delta) {
		if (initialized) {
		    if (!dead) {
    			super.update(gc, delta);
    			
    			//If level cancels targetting before player does, keep up
    			if (isTargeting()) {
    				if (!level.isInPlayerTargetMode()) {
    					cancelTargetingMode();
    				}
    			}
    			else {
    				if (level.isPlayerTurn()) {
    					//Movement				    
    				    final int actiontimerlength = 180;
    				    if (gc.getInput().isKeyDown(Input.KEY_W)) {
                            w = true;
                            s = false;
                            if (actiontimer <= 0) actiontimer = actiontimerlength;
                        }
                        else if (gc.getInput().isKeyDown(Input.KEY_S)) {
                            s = true;
                            w = false;
                            if (actiontimer <= 0) actiontimer = actiontimerlength;
                        }
                        if (gc.getInput().isKeyDown(Input.KEY_A)) {
                            a = true;
                            d = false;
                            if (actiontimer <= 0) actiontimer = actiontimerlength;
                        }
                        else if (gc.getInput().isKeyDown(Input.KEY_D)) {
                            d = true;
                            a = false;
                            if (actiontimer <= 0) actiontimer = actiontimerlength;
                        }
    					
    					if (actiontimer > 0) {
    					    actiontimer -= delta;
    					    
    					    if (actiontimer <= 0) {
    					        int newr = getRow();
    	                        int newc = getColumn();
    					        if (w) newr = getRow() - 1;
    					        if (a) newc = getColumn() - 1;
    					        if (s) newr = getRow() + 1;
    					        if (d) newc = getColumn() + 1;
    					        w = false;
    					        a = false;
    					        s = false;
    					        d = false;
    					        attemptMove(newr, newc);
    					    }
    					}
    					
    					//Enter Ranged Attack Targetting Mode
    					if (gc.getInput().isKeyPressed(Input.KEY_E)) {
    						Ability ability = this.getAbility(Ability_DefaultRangedAttack.REFERENCE);
    						//If Ranged Weapon Equipped and have ability
    						if (equipment.isRangedWeaponEquipped() && ability != null) {
    							ability.setRange(equipment.getItemInSlotType(Equipment.SlotType.MainHand).getRange());
    							ability.setIconImage(equipment.getItemInSlotType(Equipment.SlotType.MainHand).getIconImage());
    							enterTargetingMode(ability);
    						}
    					}
    					
    					//Use Stair
    		            if (gc.getInput().isKeyPressed(Input.KEY_X)) {
    		                if (level.isStairDown(this.getRow(), this.getColumn())) {
    		                    this.notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Used_Stairs.getType()));
    		                }
    		            }
    					
    					//Skip Turn
    					if (gc.getInput().isKeyPressed(Input.KEY_SPACE)) {
    						turnTaken(null);
    					}
    				}
    			}
    			
    			//Temporary stat modification.
    			/*if (gc.getInput().isKeyDown(Input.KEY_Y)) {
    				getStat(Stat_CurrentHealth.REFERENCE).addToBaseValue(-1);
    				calculateStatModifiers();
    			}
    			else if (gc.getInput().isKeyDown(Input.KEY_U)) {
    				getStat(Stat_CurrentHealth.REFERENCE).addToBaseValue(1);
    				calculateStatModifiers();
    			}
    			if (gc.getInput().isKeyDown(Input.KEY_R)) {
    				getStat(Stat_CurrentMana.REFERENCE).addToBaseValue(-1);
    				calculateStatModifiers();
    			}
    			else if (gc.getInput().isKeyDown(Input.KEY_T)) {
    				getStat(Stat_CurrentMana.REFERENCE).addToBaseValue(1);
    				calculateStatModifiers();
    			}			
    			//Temporary increase/decrease level
    			if (gc.getInput().isKeyPressed(Input.KEY_LBRACKET)) {
    				this.increaseCharacterLevel(-1, true);
    			}
    			else if (gc.getInput().isKeyPressed(Input.KEY_RBRACKET)) {
    				this.increaseCharacterLevel(1, true);
    			}
    			else if (gc.getInput().isKeyPressed(Input.KEY_BACKSLASH)) {
    				this.increaseCharacterLevel(99, true);
    			}
    			//Temporary increase/decrease current xp
    			if (gc.getInput().isKeyDown(Input.KEY_K)) {
    				getStat(Stat_CurrentExperience.REFERENCE).addToBaseValue(-25);
    				calculateStatModifiers();
    			}
    			else if (gc.getInput().isKeyDown(Input.KEY_L)) {
    				getStat(Stat_CurrentExperience.REFERENCE).addToBaseValue(25);
    				calculateStatModifiers();
    			}
    			//Temporary add item
    			if (gc.getInput().isKeyPressed(Input.KEY_J)) {
    				getInventory().attemptToInsertItem(ItemFactory.createItem(
    						ItemFactory.ItemType.Headpiece, this, this.getCharacterLevel()),
    						true);//test
    			}
    			//Temporary add item2
    			if (gc.getInput().isKeyPressed(Input.KEY_H)) {
    				getInventory().attemptToInsertItem(ItemFactory.createItem(
    						ItemFactory.ItemType.Bow, this, this.getCharacterLevel()),
    						true);//test
    			}
    			//Temporary add item1
    			if (gc.getInput().isKeyPressed(Input.KEY_G)) {
    				getInventory().attemptToInsertItem(ItemFactory.createItem(
    						ItemFactory.ItemType.Key, this, this.getCharacterLevel()),
    						true);//test
    			}*/
		    }
		    else {
		        //Continue after Death
		        if (gc.getInput().isKeyPressed(Input.KEY_SPACE)) {
		            notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Buried.getType()));
		        }
		    }
		}
		else {
			final GameCharacter self = this;
			//Officially initializes the character.			
			//Sends modification events for all new stats so any listeners can sync with their current status.
			for (Stat s : stats.values()) {
				s.notifyModificationListeners();
			}
			//Sends ability modification event for initial abilities
			notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Ability_Added.getType()));
			
			//Calculate Level Visibility and Add Sight Radius Listener
			level.calculatePlayerVisibility((int) this.getStat(Stat_SightRadius.REFERENCE).getModifiedValue());
			this.addListener(new GameCharacterListener() {
				@Override
				public void eventPerformed(GameCharacterEvent e) {
					if (e.getEventType().equals(GameCharacterEvent.Type.Stat_SightRadius_Modified.getType())) {
						level.calculatePlayerVisibility((int) e.getGameCharacter().getStat(Stat_SightRadius.REFERENCE).getModifiedValue());
					}
				}

				@Override
				public List<String> getEventTypes() {
					ArrayList<String> types = new ArrayList<String>();
					types.add(GameCharacterEvent.Type.Stat_SightRadius_Modified.getType());
					return types;
				}				
			});
			
			//Set Current Health and Mana to their totals for fresh stats
			getStat(Stat_CurrentHealth.REFERENCE).setBaseValue(getStat(Stat_TotalHealth.REFERENCE).getModifiedValue(), false);
			getStat(Stat_CurrentMana.REFERENCE).setBaseValue(getStat(Stat_TotalMana.REFERENCE).getModifiedValue(), false);
			calculateStatModifiers();
			
			//Add XP Listener for leveling
			this.addListener(new GameCharacterListener() {
				@Override
				public void eventPerformed(GameCharacterEvent e) {
					GameCharacter gamechar = e.getGameCharacter();
					if (e.getEventType().equals(GameCharacterEvent.Type.Stat_CurrentExperience_Modified.getType())) {
						//Check XP and levelup if it is >= total, saving any left overs.
						Stat current = gamechar.getStat(Stat_CurrentExperience.REFERENCE);
						Stat total = gamechar.getStat(Stat_TotalExperience.REFERENCE);
						double currentxp = current.getModifiedValue();
						double totalxp = total.getModifiedValue();
						
						if (currentxp >= totalxp) {
							double leftover = currentxp - totalxp;
							
							//Increase Level
							gamechar.increaseCharacterLevel(1, true);
							
							//Add Attributes Allocation Points.
							
							//Set new xp values
							total.setBaseValue(Formula.levelExperienceScalingValue((double) PlayerCharacter.BASE_TOTAL_EXP, 
									(double) PlayerCharacter.MAX_TOTAL_EXP, gamechar.getCharacterLevel(), GameCharacter.MAX_CHARACTERLEVEL), false);
							current.setBaseValue(leftover, false);
							
							calculateStatModifiers();
						}
					}
				}

				@Override
				public List<String> getEventTypes() {
					ArrayList<String> types = new ArrayList<String>();
					types.add(GameCharacterEvent.Type.Stat_CurrentExperience_Modified.getType());
					return types;
				}				
			});
			
			//Add Trigger for setting Critical Strike State
			this.getTriggerDispatcher().addTriggerListener(new TriggerEventListener(TriggerEvent.Type.GameChar_CriticalStrike.getType()) {
				@Override
				public void trigger(TriggerEvent e) {
					self.hadCriticalStrike = true;
				}				
			});
			
			//Add Trigger for Damage Done to notify of respective critical strikes
			this.getTriggerDispatcher().addTriggerListener(new TriggerEventListener(TriggerEvent.Type.GameChar_DamageDone.getType()) {
				@Override
				public void trigger(TriggerEvent e) {
					if (self.hadCriticalStrike()) {
						if (self.getDamageType() == Item.ItemType.Melee) {
							self.getTriggerDispatcher().notifyListeners(TriggerEvent.Type.GameChar_MeleeCriticalStrike.getType());
						}
						else if (self.getDamageType() == Item.ItemType.Ranged) {
							self.getTriggerDispatcher().notifyListeners(TriggerEvent.Type.GameChar_RangedCriticalStrike.getType());
						}
					}
				}				
			});
			
			//Add Death Listener
            //Check for Death
            this.addListener(new GameCharacterListener() {
                @Override
                public void eventPerformed(GameCharacterEvent e) {
                    if (e.getEventType().equals(GameCharacterEvent.Type.Stat_CurrentHealth_Modified.getType())) {
                        Stat stat = self.getStat(Stat_CurrentHealth.REFERENCE);
                        //Check for Death
                        if (stat.getModifiedValue() <= 0) {
                            dead = true;
                            self.notifyListeners(new GameCharacterEvent(self, GameCharacterEvent.Type.Died.getType()));
                        }
                    }
                }

                @Override
                public List<String> getEventTypes() {
                    ArrayList<String> types = new ArrayList<String>();
                    types.add(GameCharacterEvent.Type.Stat_CurrentHealth_Modified.getType());
                    return types;
                }                
            });
			
			//Send Initialization Event
			notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Initialized.getType()));
			
			initialized = true;
		}
	}
	
	/**
	 * Attempts to move, or context sensitive attack, takes up a turn
	 */
	public void attemptMove(int newr, int newc) {
		if (level.isWalkableTile(this, getPositionAsNode(), Level.getGridPositionAsNode(newr, newc))) {
			//Tile is walkable, first check if another context action is suitable, otherwise make the move.
			GameCharacter occupier = level.getGameCharacterAtPoint(newr, newc);
			Ability meleeability = this.getAbility(Ability_DefaultMeleeAttack.REFERENCE);
			Ability rangedability = this.getAbility(Ability_DefaultRangedAttack.REFERENCE);
			if (occupier != null) {
				//Check If we have a ranged weapon
				if (equipment.isRangedWeaponEquipped()) {
					//Ranged Attack Occupier
					rangedability.setTarget(occupier);
					rangedability.setIconImage(equipment.getItemInSlotType(Equipment.SlotType.MainHand).getIconImage());
					useAbility(rangedability);
				}
				else {
					//Melee Attack Occupier
					meleeability.setTarget(occupier);
					if (equipment.isMeleeWeaponEquipped()) {
						meleeability.setIconImage(equipment.getItemInSlotType(Equipment.SlotType.MainHand).getIconImage());
					}
					else {
						meleeability.setIconImage(null);
					}
					useAbility(meleeability);
				}
			}
			else {
				//Move
				setRow(newr);
				setColumn(newc);
				level.calculatePlayerVisibility((int) this.getStat(Stat_SightRadius.REFERENCE).getModifiedValue()); //Recalculate Level Visibility
				turnTaken(null);
				notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Position_Modified.getType()));
			}
		}
	}
	
	@Override
	public boolean useAbility(Ability a) {
		if (level.isPlayerTurn()) {
			if (super.useAbility(a)) {
				a.startCooldown();
				turnTaken(a);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Does all necessary processes for ending a turn.
	 * If an ability is passed, then it will not update its cooldown
	 */
	public void turnTaken(Ability ability) {
	    this.getTriggerDispatcher().notifyListeners(TriggerEvent.Type.GameChar_TurnEnded.getType());
	    
		//Cancel any targetting that might be going on
		cancelTargetingMode();
		
		//Update ability cooldowns
		for (Ability a : abilities) {
			if (ability == null || !ability.equals(a)) {
				a.cooldownTick();
			}
		}
		
		//Update item cooldowns
		Item item = null;
		if (ability instanceof Item) {
			item = (Item) ability;
		}
		getInventory().cooldownTick(item);
		
		//Update buff/debuff durations
		this.tickTemporaryStatModifiers(ability);
		
		//Set level state
		level.setPlayerTurn(false);
		
		//Reset critical strike state
		this.hadCriticalStrike = false;
		
		//Notify Listeners
		notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Turn_Taken.getType()));
	}
	
	public Ability getTargetAbility() {
		return targetAbility;
	}
	
	public void setNewLevel(Level level) {
	    this.level = level;
	    
	    this.notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Finished_Stairs.getType()));
	}
	
	@Override
	public void finalizeOutgoingDamage() {
		//Determine Primary Damage Type
		if (equipment.isRangedWeaponEquipped()) {
			damageType = Item.ItemType.Ranged;
		}
		else {
			damageType = Item.ItemType.Melee;
		}
		
		//Finalize Damage
		triggerDispatcher.notifyListeners(TriggerEvent.Type.GameChar_OutgoingDamageFinalized.getType());
	}
	
	@Override
	public void finalizeIncomingDamage() {
	    //Finalize Damage
        triggerDispatcher.notifyListeners(TriggerEvent.Type.GameChar_IncomingDamageFinalized.getType());
	}

	@Override
	protected void enterTargetingMode(Ability ability) {
	    final GameCharacter self = this;
	    
		if (!isTargeting()) {
			setTargeting(true);
			
			float range = ability.getRange();
			
			targetAbility = ability;
			
			level.addPlayerTargetListener(new PlayerTargetListener() {
				@Override
				public void targetSelected(GameCharacter target, int r, int c) {
					if (targetAbility.isValidTarget(target)) {
						targetAbility.setTarget(target);
						if (target == null) targetAbility.setTarget(self);
						targetAbility.setTargetRow(r);
						targetAbility.setTargetColumn(c);
						if (GameLogic.DEBUG) System.out.println(targetAbility.getName() + " valid target!");
						useAbility(targetAbility);
					}
					
					cancelTargetingMode();
				}				
			});
			
			level.enterPlayerTargetingMode((int) range, targetAbility.targetUsesLineOfSight());
		}
	}

	@Override
	protected void cancelTargetingMode() {
		if (isTargeting()) {			
			setTargeting(false);
			
			targetAbility = null;
			
			level.cancelPlayerTargettingMode();
		}
	}

	@Override
	public boolean isValidNode(Node srcNode, Node dstNode) {
		if (level.movementBlockedAt(dstNode.getRow(), dstNode.getColumn())) {
			return false;
		}
		
		return true;
	}

	@Override
	public int getMoveCost(Node srcNode, Node dstNode) {
		return 1;
	}
	
	public boolean isDead() {
	    return dead;
	}
	
	public void increaseExperience(int amount) {
	    getStat(Stat_CurrentExperience.REFERENCE).addToBaseValue(amount);
        calculateStatModifiers();
	}
}
