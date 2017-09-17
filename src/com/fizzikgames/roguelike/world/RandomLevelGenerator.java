package com.fizzikgames.roguelike.world;

import java.util.Random;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.entity.MonsterFactory.AttackType;
import com.fizzikgames.roguelike.entity.PlayerCharacter;
import com.fizzikgames.roguelike.entity.MonsterFactory.MonsterType;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.ItemFactory.BroadItemType;
import com.fizzikgames.roguelike.util.RandomBag;

/**
 * Decides on level constraints and passes them along to a new random level.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class RandomLevelGenerator {    
    public RandomLevelGenerator() {
        
	}
	
	public RandomLevel generateLevel(PlayerCharacter player) {
	    Random rng = GameLogic.rng;
	    
	    //MinMax counts
	    final int minWidth = 50;
	    final int maxWidth = 125;
	    final int minHeight = 50;
	    final int maxHeight = 125;
	    final int minMonsters = 12;
	    final int maxMonsters = 24;
	    final int minChests = 1;
	    final int maxChests = 8;
	    final int minTraps = 8;
	    final int maxTraps = 16;
	    final int minShrines = 2;
	    final int maxShrines = 8;
	    final int minChestItems = 1;
        final int maxChestItems = 6;
        
        //Chest Item Pool
	    final RandomBag<BroadItemType> chestItemPool = new RandomBag<BroadItemType>(rng, 1000);
	    chestItemPool.addPairing(BroadItemType.Weapon, 250);
	    chestItemPool.addPairing(BroadItemType.Gear, 350);
	    chestItemPool.addPairing(BroadItemType.Elixir, 1000);
	    
	    //Monster Types
	    final RandomBag<MonsterType> monsterTypePool = new RandomBag<MonsterType>(rng, 1000);
	    monsterTypePool.addPairing(MonsterType.Golem, 50);
	    monsterTypePool.addPairing(MonsterType.Spider, 150);
	    monsterTypePool.addPairing(MonsterType.Goblin, 450);
	    monsterTypePool.addPairing(MonsterType.Skeleton, 1000);
	    
	    //Monster Attack Types
	    final RandomBag<AttackType> monsterAttackTypePool = new RandomBag<AttackType>(rng, 1000);
	    monsterAttackTypePool.addPairing(AttackType.Ranged, 500);
	    monsterAttackTypePool.addPairing(AttackType.Melee, 1000);
	    
	    //Monster Loot Pool
	    final RandomBag<BroadItemType> monsterLootPool = new RandomBag<BroadItemType>(rng, 1000);
	    monsterLootPool.addPairing(BroadItemType.Key, 100);
	    monsterLootPool.addPairing(BroadItemType.Weapon, 150);
	    monsterLootPool.addPairing(BroadItemType.Gear, 250);
	    monsterLootPool.addPairing(BroadItemType.Elixir, 1000);
	    
	    //Create Level
	    RandomLevel level = new RandomLevel(player, "tileset_001", "tileset_extras", minWidth, minHeight, maxWidth, maxHeight,
	            minMonsters, maxMonsters, minChests, maxChests, minTraps, maxTraps, minShrines, maxShrines, chestItemPool, 
	            minChestItems, maxChestItems, monsterTypePool, monsterAttackTypePool, monsterLootPool);
	    
	    level.generate();
	    
	    return level;
	}
}
