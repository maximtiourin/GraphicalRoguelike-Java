package com.fizzikgames.roguelike.entity;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.entity.mechanics.Formula;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_OutgoingMeleeDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_OutgoingRangedDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalExperience;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalHealth;
import com.fizzikgames.roguelike.util.RandomBag;
import com.fizzikgames.roguelike.world.Level;

public class MonsterFactory {
    public enum MonsterType {
        Goblin, Spider, Skeleton, Golem;
    }
    
    public enum AttackType {
        Melee, Ranged;
    }
    
    private static final MonsterFactory self = new MonsterFactory();
    
    public MonsterFactory() {
        
    }
    
    public static Monster createMonster(MonsterType type, AttackType attackType, Level gameLevel, int monsterLevel) {
        if (type == MonsterType.Goblin) {
            return createGoblin(attackType, gameLevel, monsterLevel);
        }
        else if (type == MonsterType.Spider) {
            return createSpider(gameLevel, monsterLevel);
        }
        else if (type == MonsterType.Skeleton) {
            return createSkeleton(gameLevel, monsterLevel);
        }
        else if (type == MonsterType.Golem) {
            return createGolem(gameLevel, monsterLevel);
        }
        
        return null;
    }   
    
    private static Monster createGoblin(AttackType attackType, Level gameLevel, int monsterLevel) {
        final int health = Math.max(10, Formula.baseToThePowerOfNaturalLogOf(10, monsterLevel));
        final int meleeDamage = Math.max(2, Formula.baseToThePowerOfNaturalLogOf(2, monsterLevel));
        final int rangedDamage = Math.max(1, Formula.baseToThePowerOfNaturalLogOf(1, monsterLevel));
        boolean ranged;
        float attackRange;
        String spriteref;
        if (attackType == AttackType.Ranged) {
            ranged = true;
            attackRange = 4;
            spriteref = "character_monster_goblin02";
        }
        else {
            ranged = false;
            attackRange = 1.75f;
            spriteref = "character_monster_goblin01";
        }
        
        Monster monster = new Monster(spriteref, gameLevel, 0, 0, monsterLevel, ranged, attackRange) {
            @Override
            public int getItemDropAmount() {
                RandomBag<Integer> bag = new RandomBag<Integer>(GameLogic.rng, 1000);
                bag.addPairing(1, 750);
                bag.addPairing(0, 1000);
                return bag.getRandomObject();
            }
            
            @Override
            public int getExperienceAmount() {
                GameCharacter player = level.getPlayer();
                return (int) (player.getStat(Stat_TotalExperience.REFERENCE).getModifiedValue() / (10 * player.getCharacterLevel()));
            }
        };
        
        monster.getStat(Stat_TotalHealth.REFERENCE).setBaseValue(health, false);
        monster.getStat(Stat_OutgoingRangedDamage.REFERENCE).setBaseValue(rangedDamage, false);
        monster.getStat(Stat_OutgoingMeleeDamage.REFERENCE).setBaseValue(meleeDamage, false);
        monster.calculateStatModifiers();
        
        return monster;
    }
    
    private static Monster createSpider(Level gameLevel, int monsterLevel) {
        final int health = Math.max(15, Formula.baseToThePowerOfNaturalLogOf(15, monsterLevel));
        final int meleeDamage = Math.max(4, Formula.baseToThePowerOfNaturalLogOf(4, monsterLevel));
        
        Monster monster = new Monster("character_monster_spider01", gameLevel, 0, 0, monsterLevel, false, 1.75f) {
            @Override
            public int getItemDropAmount() {
                RandomBag<Integer> bag = new RandomBag<Integer>(GameLogic.rng, 1000);
                bag.addPairing(2, 250);
                bag.addPairing(1, 1000);
                return bag.getRandomObject();
            }
            
            @Override
            public int getExperienceAmount() {
                GameCharacter player = level.getPlayer();
                return (int) (player.getStat(Stat_TotalExperience.REFERENCE).getModifiedValue() / (6.66 * player.getCharacterLevel()));
            }
        };
        
        monster.getStat(Stat_TotalHealth.REFERENCE).setBaseValue(health, false);
        monster.getStat(Stat_OutgoingMeleeDamage.REFERENCE).setBaseValue(meleeDamage, false);
        monster.calculateStatModifiers();
        
        return monster;
    }
    
    private static Monster createSkeleton(Level gameLevel, int monsterLevel) {
        final int health = Math.max(6, Formula.baseToThePowerOfNaturalLogOf(6, monsterLevel));
        final int meleeDamage = Math.max(3, Formula.baseToThePowerOfNaturalLogOf(3, monsterLevel));
        
        Monster monster = new Monster("character_monster_skeleton01", gameLevel, 0, 0, monsterLevel, false, 1.75f) {
            @Override
            public int getItemDropAmount() {
                RandomBag<Integer> bag = new RandomBag<Integer>(GameLogic.rng, 1000);
                bag.addPairing(1, 250);
                bag.addPairing(0, 1000);
                return bag.getRandomObject();
            }
            
            @Override
            public int getExperienceAmount() {
                GameCharacter player = level.getPlayer();
                return (int) (player.getStat(Stat_TotalExperience.REFERENCE).getModifiedValue() / (13.33 * player.getCharacterLevel()));
            }
        };
        
        monster.getStat(Stat_TotalHealth.REFERENCE).setBaseValue(health, false);
        monster.getStat(Stat_OutgoingMeleeDamage.REFERENCE).setBaseValue(meleeDamage, false);
        monster.calculateStatModifiers();
        
        return monster;
    }
    
    private static Monster createGolem(Level gameLevel, int monsterLevel) {
        final int health = Math.max(30, Formula.baseToThePowerOfNaturalLogOf(30, monsterLevel));
        final int meleeDamage = Math.max(8, Formula.baseToThePowerOfNaturalLogOf(8, monsterLevel));
        
        Monster monster = new Monster("character_monster_golem01", gameLevel, 0, 0, monsterLevel, false, 1.75f) {
            @Override
            public int getItemDropAmount() {
                RandomBag<Integer> bag = new RandomBag<Integer>(GameLogic.rng, 1000);
                bag.addPairing(5, 25);
                bag.addPairing(4, 150);
                bag.addPairing(3, 250);
                bag.addPairing(2, 1000);
                return bag.getRandomObject();
            }
            
            @Override
            public int getExperienceAmount() {
                GameCharacter player = level.getPlayer();
                return (int) (player.getStat(Stat_TotalExperience.REFERENCE).getModifiedValue() / (3.33 * player.getCharacterLevel()));
            }
        };
        
        monster.getStat(Stat_TotalHealth.REFERENCE).setBaseValue(health, false);
        monster.getStat(Stat_OutgoingMeleeDamage.REFERENCE).setBaseValue(meleeDamage, false);
        monster.calculateStatModifiers();
        
        return monster;
    }
    
    public static MonsterFactory get() {
        return self;
    }
}
