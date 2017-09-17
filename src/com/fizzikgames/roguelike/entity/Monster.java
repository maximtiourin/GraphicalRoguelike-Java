package com.fizzikgames.roguelike.entity;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;

import com.fizzikgames.roguelike.entity.FloatingContextStack.ContextType;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEvent;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEventListener;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability_DefaultMeleeAttack;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability_DefaultRangedAttack;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat;
import com.fizzikgames.roguelike.entity.mechanics.stat.StatListener;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CriticalHitChance;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CriticalHitDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentHealth;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_DodgeChance;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_IncomingDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_IncomingDamageReduction;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_OutgoingMeleeDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_OutgoingRangedDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_SightRadius;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalExperience;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalHealth;
import com.fizzikgames.roguelike.pathfinding.Node;
import com.fizzikgames.roguelike.pathfinding.Path;
import com.fizzikgames.roguelike.world.Level;

public abstract class Monster extends NonPlayerCharacter {
    public static final int ATTACK_TURNAMOUNT = 15;
    public static final int MOVE_TURNAMOUNT = 10;
    protected int turns; //How much of a turn this monster has remaining.
    protected int chaseCounter; //How many turns to chase for
    protected boolean ranged;
    protected float attackRange;
    
    public Monster(String spriteref, Level level, int r, int c, int characterLevel, boolean ranged, float attackRange) {
        super(spriteref, level, r, c, characterLevel);
        this.chaseCounter = 0;
        this.ranged = ranged;
        this.attackRange = attackRange;
    }
    
    @Override
    /**
     * Adds default monster stats, Override this and call super.initialize()
     * to add any additional stats.
     */
    public void initialize() {
        Stat stat = null;
        final Monster monster = this;
        
        /* Current Health */
        stat = new Stat_CurrentHealth(this, 0);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                monster.notifyListeners(new GameCharacterEvent(monster, GameCharacterEvent.Type.Stat_CurrentHealth_Modified.getType()));
            }           
        });
        addNewStat(stat, false);
        /* Total Health */
        stat = new Stat_TotalHealth(this, 10);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                monster.notifyListeners(new GameCharacterEvent(monster, GameCharacterEvent.Type.Stat_TotalHealth_Modified.getType()));
            }           
        });
        addNewStat(stat, true);
        /* Critical Hit Chance */
        stat = new Stat_CriticalHitChance(this, 0.08);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                monster.notifyListeners(new GameCharacterEvent(monster, GameCharacterEvent.Type.Stat_CriticalHitChance_Modified.getType()));
            }           
        });
        addNewStat(stat, false);
        /* Critical Hit Damage */
        stat = new Stat_CriticalHitDamage(this, 1.0);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                monster.notifyListeners(new GameCharacterEvent(monster, GameCharacterEvent.Type.Stat_CriticalHitDamage_Modified.getType()));
            }           
        });
        addNewStat(stat, false);
        /* Dodge Chance */
        stat = new Stat_DodgeChance(this, 0.05);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                monster.notifyListeners(new GameCharacterEvent(monster, GameCharacterEvent.Type.Stat_DodgeChance_Modified.getType()));
            }           
        });
        addNewStat(stat, false);
        /* Incoming Damage */
        stat = new Stat_IncomingDamage(this, 0);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                monster.notifyListeners(new GameCharacterEvent(monster, GameCharacterEvent.Type.Stat_IncomingDamage_Modified.getType()));
            }           
        });
        addNewStat(stat, false);
        /* Incoming Damage Reduction */
        stat = new Stat_IncomingDamageReduction(this, 0);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                monster.notifyListeners(new GameCharacterEvent(monster, GameCharacterEvent.Type.Stat_IncomingDamageReduction_Modified.getType()));
            }           
        });
        addNewStat(stat, false);
        /* Outgoing Melee Damage */
        stat = new Stat_OutgoingMeleeDamage(this, 0);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                monster.notifyListeners(new GameCharacterEvent(monster, GameCharacterEvent.Type.Stat_OutgoingMeleeDamage_Modified.getType()));
            }           
        });
        addNewStat(stat, false);
        /* Outgoing Ranged Damage */
        stat = new Stat_OutgoingRangedDamage(this, 0);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                monster.notifyListeners(new GameCharacterEvent(monster, GameCharacterEvent.Type.Stat_OutgoingRangedDamage_Modified.getType()));
            }           
        });
        addNewStat(stat, false);
        /* Sight Radius */
        stat = new Stat_SightRadius(this, 8);
        stat.addModificationListener(new StatListener() {
            @Override
            public void statEventOccured(Stat stat) {
                monster.notifyListeners(new GameCharacterEvent(monster, GameCharacterEvent.Type.Stat_SightRadius_Modified.getType()));
            }           
        });
        addNewStat(stat, true);
        
        //Add Attack Abilities
        addAbility(new Ability_DefaultRangedAttack(this), false);
        addAbility(new Ability_DefaultMeleeAttack(this), false);   
    }
    
    public void doMove() {
        this.startTurn();
        turns = getTurnDuration();
        
        final PlayerCharacter player = level.getPlayer();
        final GameCharacter monster = this;
        Vector2f playerpos = new Vector2f(player.getColumn(), player.getRow());
        Vector2f monsterpos = new Vector2f(this.getColumn(), this.getRow());
        
        //Do turns
        while (turns >= MOVE_TURNAMOUNT && !player.isDead()) {
            //Check Line of Sight with Player
            boolean los = level.hasLineOfSight(monsterpos, playerpos, (int) this.getStat(Stat_SightRadius.REFERENCE).getModifiedValue());
            if (los || (chaseCounter > 0)) {
                //Has line of sight, begin chasing
                if (chaseCounter <= 0) level.getContextStack().addNewContext(ContextType.Aggro, null, "Aggro", getRow(), getColumn());
                if (los) chaseCounter = getChaseDuration();
                
                boolean inAttackRange = (monsterpos.distance(playerpos) <= attackRange);
                
                if (inAttackRange && turns >= ATTACK_TURNAMOUNT && (los)) {
                    //Determine Attack and perform it
                    Ability meleeability = monster.getAbility(Ability_DefaultMeleeAttack.REFERENCE);
                    Ability rangedability = monster.getAbility(Ability_DefaultRangedAttack.REFERENCE);
                    if (ranged) {
                        rangedability.setTarget(player);
                        rangedability.activate();
                    }
                    else {
                        meleeability.setTarget(player);
                        meleeability.activate();
                    }
                    turns -= ATTACK_TURNAMOUNT;
                }
                else {
                    if (!(ranged && inAttackRange && los)) {
                        //Chase    
                        //Generate path to player
                        Path path = level.getPathfinder().findPath(this, new Node(null, this.getColumn(), this.getRow()), 
                                new Node(null, player.getColumn(), player.getRow()));
                        if (path.getSize() > 0) {
                            path.getNextNode(); //Pop off starting location
                            
                            //Move towards next Node in Path if not occupied, use up move_turnamount regardless.
                            if (path.getSize() > 0) {
                                Node nextNode = path.getNextNode();
                                attemptMove(false, nextNode.getRow(), nextNode.getColumn());
                            }
                        }
                    }
                    
                    turns -= MOVE_TURNAMOUNT;
                }
            }
            else {
                turns -= MOVE_TURNAMOUNT; //TEMP UNTIL there is actions to place here for no los
            }
        }     
        
        if (chaseCounter > 0) chaseCounter--;
        turnTaken();
    }
    
    @Override
    public void update(GameContainer gc, int delta) {
        if (initialized) {
            super.update(gc, delta);
        }
        else {
            final Monster self = this;
            final PlayerCharacter player = level.getPlayer();
            //Officially initializes the character.          
            //Sends modification events for all new stats so any listeners can sync with their current status.
            for (Stat s : stats.values()) {
                s.notifyModificationListeners();
            }
            
            //Set Current Health and Mana to their totals for fresh stats
            getStat(Stat_CurrentHealth.REFERENCE).setBaseValue(getStat(Stat_TotalHealth.REFERENCE).getModifiedValue(), false);
            calculateStatModifiers();
            
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
                            int xp = getExperienceAmount();
                            
                            level.dropItemsFromLootTable(self.getItemDropAmount(), self.getRow(), self.getColumn());
                            level.removeGameCharacter(self);
                            player.increaseExperience(xp);
                            level.getContextStack().addNewContext(ContextType.Xp, null, xp + " xp", self.getRow(), self.getColumn());
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
    
    @Override
    public void finalizeOutgoingDamage() {
        //Determine Primary Damage Type
        if (ranged) {
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
    
    public void turnTaken() {
        this.getTriggerDispatcher().notifyListeners(TriggerEvent.Type.GameChar_TurnEnded.getType());
        
        //Update buff/debuff durations
        this.tickTemporaryStatModifiers(null);
        
        //Reset critical strike state
        this.hadCriticalStrike = false;
        
        notifyListeners(new GameCharacterEvent(this, GameCharacterEvent.Type.Turn_Taken.getType()));
    }
    
    public boolean attemptMove(boolean pathfind, int r, int c) {
        if (!level.movementImpededAt(pathfind, r, c)) {
            this.setRow(r);
            this.setColumn(c);
        }
            
        return false;
    }
    
    /**
     * Override to to set different value, default = 15;
     * How long a turn last for this monster.
     * Performing actions uses up turn duration.
     * When no more actions can be performed with remaining duration,
     * turn is over.
     */
    public int getTurnDuration() {
        return 15;
    }
    
    /**
     * Override to set how many turns to chase for
     * Default is 10
     */
    public int getChaseDuration() {
        return 10;
    }
    
    /**
     * Override to set how many items this monster drops
     * Default is 0
     */
    public int getItemDropAmount() {
        return 0;
    }
    
    /**
     * Override to set how much experience this monster gives
     * Default is 0
     */
    public int getExperienceAmount() {
        return 0;
    }
}
