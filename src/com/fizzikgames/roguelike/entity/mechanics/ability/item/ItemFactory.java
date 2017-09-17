package com.fizzikgames.roguelike.entity.mechanics.ability.item;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.asset.AssetLoader;
import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Formula;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.AffixFactory.AffixType;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.AffixFactory.BroadAffixType;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentHealth;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentMana;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalHealth;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalMana;
import com.fizzikgames.roguelike.util.RandomBag;
import com.fizzikgames.roguelike.util.StringUtil;

/**
 * The item factory is in charge of creating items of a given type and randomizing their values based on their level.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class ItemFactory {
	public enum ItemType {
	    Key,
		HealthElixir, ManaElixir, 
		Headpiece, Chestpiece, Legpiece, Footpiece, Handpiece,
		Bow, Battleaxe, Dagger;
	}
	
	public enum BroadItemType {
	    Key(new ItemType[]{ItemType.Key}),
	    Elixir(new ItemType[]{ItemType.HealthElixir, ItemType.ManaElixir}),
	    Gear(new ItemType[]{ItemType.Headpiece, ItemType.Chestpiece, ItemType.Legpiece, ItemType.Footpiece, ItemType.Handpiece}),
	    Weapon(new ItemType[]{ItemType.Bow, ItemType.Battleaxe, ItemType.Dagger});
	    
	    private ItemType[] types;
	    
	    private BroadItemType(ItemType[] types) {
	        this.types = types;
	    }
	    
	    public ItemType[] getTypes() {
	        return types;
	    }
	}
	
	private static final ItemFactory self = new ItemFactory();
	
	public ItemFactory() {
		
	}
	
	/**
	 * Returns a random item of random rarity of the given type.
	 * If it rolls legendary, then it will pull from a premade list of legendaries of the given type.
	 */
	public static Item createItem(ItemType type, GameCharacter gamechar, int itemLevel) {
	    if (type == ItemType.Key) {
            return createKey(gamechar);
        }
	    else if (type == ItemType.HealthElixir) {
			return createHealthElixir(gamechar, itemLevel);
		}
		else if (type == ItemType.ManaElixir) {
			return createManaElixir(gamechar, itemLevel);
		}
		else if (type == ItemType.Bow) {
			return createBow(gamechar, itemLevel);
		}
		else if (type == ItemType.Battleaxe) {
            return createBattleaxe(gamechar, itemLevel);
        }
		else if (type == ItemType.Dagger) {
            return createDagger(gamechar, itemLevel);
        }
		else if (type == ItemType.Headpiece) {
            return createHeadpiece(gamechar, itemLevel);
        }
		else if (type == ItemType.Chestpiece) {
            return createChestpiece(gamechar, itemLevel);
        }
		else if (type == ItemType.Legpiece) {
            return createLegpiece(gamechar, itemLevel);
        }
		else if (type == ItemType.Footpiece) {
            return createFootpiece(gamechar, itemLevel);
        }
		else if (type == ItemType.Handpiece) {
            return createHandpiece(gamechar, itemLevel);
        }
		
		return null;
	}
	
	/**
	 * Key
	 */
	private static Item createKey(GameCharacter gamechar) {
	    final String baseName = "Chest Key";
        Image image = AssetLoader.image("item_key");
        final int cooldown = 0;
        final int cost = 0;
        final int range = 0;
        final int maxStacks = 99;
        final int currentStacks = 1;
	    
	    Item item = new Item(gamechar, baseName, null, image, Ability.TargetType.Passive.getType(), 
                cooldown, cost, range, maxStacks, currentStacks, Item.Rarity.Common, Item.ItemType.Key, 
                Item.GlobalCooldownGroup.None) {
                    @Override
                    protected void initialize() {
                    }

                    @Override
                    public void activate() {                        
                    }

                    @Override
                    public String getDescription() {                     
                        return "Opens a Chest.";
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
        };
        
        return item;
	}
	
	/**
	 * Health Elixir
	 */
	private static Item createHealthElixir(GameCharacter gamechar, int level) {
		final int typeCount = 10;
		final int itemLevel = 1 + (level / typeCount);
		final int maxLevel = 1 + (GameCharacter.MAX_CHARACTERLEVEL / typeCount);
		
		final String baseName = "Health Elixir";
		final String suffix = StringUtil.convertToRomanNumerals(itemLevel);
		Image baseimage = AssetLoader.image("item_healthelixir");
		Image image = createLevelScalingImage(baseimage, itemLevel, maxLevel);
		final int cooldown = 10;
		final int cost = 0;
		final int range = 0;
		final int maxStacks = 5;
		final int currentStacks = 1;
		final int healAmount = 25 + Formula.levelExponentialScalingValue(10.2, 100, itemLevel, maxLevel);
		
		Item item = new Item(gamechar, baseName + " " + suffix, null, image, Ability.TargetType.TargetSelf.getType(), 
				cooldown, cost, range, maxStacks, currentStacks, Item.Rarity.Common, Item.ItemType.Consumable, 
				Item.GlobalCooldownGroup.HealthElixir) {
					@Override
					protected void initialize() {
					}

					@Override
					public void activate() {
					    gamechar.healHealth(healAmount, true);					
						consume();
					}

					@Override
					public String getDescription() {
						int value = (int) Math.floor(healAmount);						
						return "Heals the character for <1" + StringUtil.addUSNumberCommas(value + "") + "> health points.";
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
						if (gamechar.getStat(Stat_CurrentHealth.REFERENCE).getModifiedValue() 
								< gamechar.getStat(Stat_TotalHealth.REFERENCE).getModifiedValue()) {
							return true;
						}
						
						return false;
					}			
		};
		
		return item;
	}
	
	/**
	 * Mana Elixir
	 */
	private static Item createManaElixir(GameCharacter gamechar, int level) {
		final int typeCount = 10;
		final int itemLevel = 1 + (level / typeCount);
		final int maxLevel = 1 + (GameCharacter.MAX_CHARACTERLEVEL / typeCount);
		
		final String baseName = "Mana Elixir";
		final String suffix = StringUtil.convertToRomanNumerals(itemLevel);
		Image baseimage = AssetLoader.image("item_manaelixir");
		Image image = createLevelScalingImage(baseimage, itemLevel, maxLevel);
		final int cooldown = 10;
		final int cost = 0;
		final int range = 0;
		final int maxStacks = 5;
		final int currentStacks = 1;
		final int healAmount = 75 + Formula.levelExponentialScalingValue(11.1, 200, itemLevel, maxLevel);
		
		Item item = new Item(gamechar, baseName + " " + suffix, null, image, Ability.TargetType.TargetSelf.getType(), 
				cooldown, cost, range, maxStacks, currentStacks, Item.Rarity.Common, Item.ItemType.Consumable, 
				Item.GlobalCooldownGroup.ManaElixir) {
					@Override
					protected void initialize() {
					}

					@Override
					public void activate() {						
					    gamechar.healMana(healAmount, true);					
						consume();
					}

					@Override
					public String getDescription() {
						int value = (int) Math.floor(healAmount);						
						return "Heals the character for <1" + StringUtil.addUSNumberCommas(value + "") + "> mana points.";
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
						if (gamechar.getStat(Stat_CurrentMana.REFERENCE).getModifiedValue() 
								< gamechar.getStat(Stat_TotalMana.REFERENCE).getModifiedValue()) {
							return true;
						}
						
						return false;
					}			
		};
		
		return item;
	}
	
	/**
	 * Bow
	 */
	private static Item createBow(GameCharacter gamechar, int level) {
	    Random rng = GameLogic.rng;
		
		final Item.Rarity rarity = rollRarity();
		final String baseName = rarity.getType() + " Bow";
		Image baseImage = AssetLoader.image("item_bow");
		Image image = createRarityImage(baseImage, rarity.getColor());
		final int cooldown = 0;
		final int cost = 0;
		final int baseRange = 4;
		final int rarityRange = (int) ((rarity.getMultiplier() - 1f) * 12);
		final int range = baseRange + rarityRange;
		final int maxStacks = 1;
		final int currentStacks = 1;
		final int baseDamage = 2;
		final int maxDamage = 4;
		final int levelDamage = (level - 1);	
		final int lowdamage = (int) ((baseDamage + levelDamage) * rarity.getMultiplier());
		final int highdamage = (int) ((maxDamage + levelDamage) * rarity.getMultiplier());
		final int damage = lowdamage + rng.nextInt(highdamage - lowdamage + 1);
		final int minAttr = (int) ((2 * (level)) * rarity.getMultiplier());
		final int maxAttr = (int) ((6 * (level)) * rarity.getMultiplier());
		final int rangedAffix = (lowdamage / 2) + rng.nextInt((highdamage / 2) - (lowdamage / 2) + 1);
		
		ArrayList<Affix> affixes = new ArrayList<Affix>();
		//Gauranteed Affixes
		affixes.add(AffixFactory.createAffix(AffixFactory.AffixType.RangedDamage, damage));
		//Rolled Affixes
		RandomBag<BroadAffixType> bag = new RandomBag<BroadAffixType>(rng, 1000);
		bag.addPairing(BroadAffixType.RangedWeapon, 100);
		bag.addPairing(BroadAffixType.Extra, 450);
		bag.addPairing(BroadAffixType.Attribute, 1000);
		
		int i = 0;
		while (i < rarity.getAffixes()) {
		    BroadAffixType broadAffixType = bag.getRandomObject();
		    AffixType affixType = broadAffixType.getTypes()[rng.nextInt(broadAffixType.getTypes().length)];
		    
		    if (broadAffixType == BroadAffixType.Attribute) {
		        affixes.add(AffixFactory.createAffix(affixType, minAttr + rng.nextInt(maxAttr - minAttr + 1)));
		        i++;
		    }
		    else if (broadAffixType == BroadAffixType.RangedWeapon) {
		        affixes.add(AffixFactory.createAffix(affixType, rangedAffix, baseName));
		        i++;
		    }
		    else if (broadAffixType == BroadAffixType.Extra) {
		        if (affixType == AffixType.HealthRegen || affixType == AffixType.ManaRegen) {
		            float min = .25f * (float) level;
		            float max = 8f * (float) level;
		            affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextFloat() * (max - min)))));
		            i++;
		        }
		        else if (affixType == AffixType.CritChance) {
		            float min = .01f;
                    float max = .08f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                    i++;
                }
		        else if (affixType == AffixType.CritDamage) {
                    float min = .1f;
                    float max = 1f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                    i++;
                }
		        else if (affixType == AffixType.SightRadius) {
		            affixes.add(AffixFactory.createAffix(affixType, 1));
		            i++;
                }
		    }
		}
		
		EquippableItem item = new EquippableItem(gamechar, baseName, null, image, Ability.TargetType.Passive.getType(), 
				cooldown, cost, range, maxStacks, currentStacks, rarity, Item.ItemType.Ranged, 
				Item.GlobalCooldownGroup.None, EquippableItem.EquipType.TwoHand, affixes) {
					@Override
					public String getDescription() {
						return this.getAffixCompoundString() +
								"<7A two handed bow used to safely attack from range.>";
					}

					@Override
					public boolean canEquip() {
						return true;
					}			
		};
		
		return item;
	}
	
	/**
     * Battleaxe
     */
    private static Item createBattleaxe(GameCharacter gamechar, int level) {
        Random rng = GameLogic.rng;
        
        final Item.Rarity rarity = rollRarity();
        final String baseName = rarity.getType() + " Battleaxe";
        Image baseImage = AssetLoader.image("item_battleaxe");
        Image image = createRarityImage(baseImage, rarity.getColor());
        final int cooldown = 0;
        final int cost = 0;
        final int range = 0;
        final int maxStacks = 1;
        final int currentStacks = 1;
        final int baseDamage = 4;
        final int maxDamage = 6;
        final int levelDamage = (level - 1);    
        final int lowdamage = (int) ((baseDamage + levelDamage) * rarity.getMultiplier());
        final int highdamage = (int) ((maxDamage + levelDamage) * rarity.getMultiplier());
        final int damage = lowdamage + rng.nextInt(highdamage - lowdamage + 1);
        final int minAttr = (int) ((2 * (level)) * rarity.getMultiplier());
        final int maxAttr = (int) ((6 * (level)) * rarity.getMultiplier());
        
        ArrayList<Affix> affixes = new ArrayList<Affix>();
        //Gauranteed Affixes
        affixes.add(AffixFactory.createAffix(AffixFactory.AffixType.MeleeDamage, damage));
        //Rolled Affixes
        RandomBag<BroadAffixType> bag = new RandomBag<BroadAffixType>(rng, 1000);
        bag.addPairing(BroadAffixType.Extra, 450);
        bag.addPairing(BroadAffixType.Attribute, 1000);
        
        for (int i = 0; i < rarity.getAffixes(); i++) {
            BroadAffixType broadAffixType = bag.getRandomObject();
            AffixType affixType = broadAffixType.getTypes()[rng.nextInt(broadAffixType.getTypes().length)];
            
            if (broadAffixType == BroadAffixType.Attribute) {
                affixes.add(AffixFactory.createAffix(affixType, minAttr + rng.nextInt(maxAttr - minAttr + 1)));
            }
            else if (broadAffixType == BroadAffixType.Extra) {
                if (affixType == AffixType.HealthRegen || affixType == AffixType.ManaRegen) {
                    float min = .25f * (float) level;
                    float max = 8f * (float) level;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextFloat() * (max - min)))));
                }
                else if (affixType == AffixType.CritChance) {
                    float min = .01f;
                    float max = .08f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.CritDamage) {
                    float min = .1f;
                    float max = 1f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.SightRadius) {
                    affixes.add(AffixFactory.createAffix(affixType, 1));
                }
            }
        }
        
        EquippableItem item = new EquippableItem(gamechar, baseName, null, image, Ability.TargetType.Passive.getType(), 
                cooldown, cost, range, maxStacks, currentStacks, rarity, Item.ItemType.Melee, 
                Item.GlobalCooldownGroup.None, EquippableItem.EquipType.TwoHand, affixes) {
                    @Override
                    public String getDescription() {
                        return this.getAffixCompoundString() +
                                "<7A two handed battleaxe used to destroy enemies up close.>";
                    }

                    @Override
                    public boolean canEquip() {
                        return true;
                    }           
        };
        
        return item;
    }
    
    /**
     * Dagger
     */
    private static Item createDagger(GameCharacter gamechar, int level) {
        Random rng = GameLogic.rng;
        
        final Item.Rarity rarity = rollRarity();
        final String baseName = rarity.getType() + " Dagger";
        Image baseImage = AssetLoader.image("item_dagger");
        Image image = createRarityImage(baseImage, rarity.getColor());
        final int cooldown = 0;
        final int cost = 0;
        final int range = 0;
        final int maxStacks = 1;
        final int currentStacks = 1;
        final int baseDamage = 2;
        final int maxDamage = 3;
        final int levelDamage = (level - 1);    
        final int lowdamage = (int) ((baseDamage + levelDamage) * rarity.getMultiplier());
        final int highdamage = (int) ((maxDamage + levelDamage) * rarity.getMultiplier());
        final int damage = lowdamage + rng.nextInt(highdamage - lowdamage + 1);
        final int minAttr = (int) ((1 * (level)) * rarity.getMultiplier());
        final int maxAttr = (int) ((3 * (level)) * rarity.getMultiplier());
        
        ArrayList<Affix> affixes = new ArrayList<Affix>();
        //Gauranteed Affixes
        affixes.add(AffixFactory.createAffix(AffixFactory.AffixType.MeleeDamage, damage));
        //Rolled Affixes
        RandomBag<BroadAffixType> bag = new RandomBag<BroadAffixType>(rng, 1000);
        bag.addPairing(BroadAffixType.Extra, 450);
        bag.addPairing(BroadAffixType.Attribute, 1000);
        
        for (int i = 0; i < rarity.getAffixes(); i++) {
            BroadAffixType broadAffixType = bag.getRandomObject();
            AffixType affixType = broadAffixType.getTypes()[rng.nextInt(broadAffixType.getTypes().length)];
            
            if (broadAffixType == BroadAffixType.Attribute) {
                affixes.add(AffixFactory.createAffix(affixType, minAttr + rng.nextInt(maxAttr - minAttr + 1)));
            }
            else if (broadAffixType == BroadAffixType.Extra) {
                if (affixType == AffixType.HealthRegen || affixType == AffixType.ManaRegen) {
                    float min = .12f * (float) level;
                    float max = 4f * (float) level;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextFloat() * (max - min)))));
                }
                else if (affixType == AffixType.CritChance) {
                    float min = .01f;
                    float max = .04f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.CritDamage) {
                    float min = .05f;
                    float max = .5f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.SightRadius) {
                    affixes.add(AffixFactory.createAffix(affixType, 1));
                }
            }
        }
        
        EquippableItem item = new EquippableItem(gamechar, baseName, null, image, Ability.TargetType.Passive.getType(), 
                cooldown, cost, range, maxStacks, currentStacks, rarity, Item.ItemType.Melee, 
                Item.GlobalCooldownGroup.None, EquippableItem.EquipType.OneHand, affixes) {
                    @Override
                    public String getDescription() {
                        return this.getAffixCompoundString() +
                                "<7A one handed dagger that can be dual-wielded>" +
                                "<br>" +
                                "<7for up close and personal attacks.>";
                    }

                    @Override
                    public boolean canEquip() {
                        return true;
                    }           
        };
        
        return item;
    }
    
    /**
     * Headpiece
     */
    private static Item createHeadpiece(GameCharacter gamechar, int level) {
        Random rng = GameLogic.rng;
        
        final Item.Rarity rarity = rollRarity();
        final String baseName = rarity.getType() + " Headpiece";
        Image baseImage = AssetLoader.image("item_headpiece");
        Image image = createRarityImage(baseImage, rarity.getColor());
        final int cooldown = 0;
        final int cost = 0;
        final int range = 0;
        final int maxStacks = 1;
        final int currentStacks = 1;
        final int baseDamage = 2;
        final int maxDamage = 3;
        final int levelDamage = (level - 1);    
        final int lowdamage = (int) ((baseDamage + levelDamage) * rarity.getMultiplier());
        final int highdamage = (int) ((maxDamage + levelDamage) * rarity.getMultiplier());
        final int minAttr = (int) ((4 * (level)) * rarity.getMultiplier());
        final int maxAttr = (int) ((12 * (level)) * rarity.getMultiplier());
        
        ArrayList<Affix> affixes = new ArrayList<Affix>();
        //Rolled Affixes
        RandomBag<BroadAffixType> bag = new RandomBag<BroadAffixType>(rng, 1000);
        bag.addPairing(BroadAffixType.Extra, 450);
        bag.addPairing(BroadAffixType.Attribute, 1000);
        
        for (int i = 0; i < rarity.getAffixes(); i++) {
            BroadAffixType broadAffixType = bag.getRandomObject();
            AffixType affixType = broadAffixType.getTypes()[rng.nextInt(broadAffixType.getTypes().length)];
            
            if (broadAffixType == BroadAffixType.Attribute) {
                affixes.add(AffixFactory.createAffix(affixType, minAttr + rng.nextInt(maxAttr - minAttr + 1)));
            }
            else if (broadAffixType == BroadAffixType.Extra) {
                if (affixType == AffixType.HealthRegen || affixType == AffixType.ManaRegen) {
                    float min = .25f * (float) level;
                    float max = 8f * (float) level;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextFloat() * (max - min)))));
                }
                else if (affixType == AffixType.CritChance) {
                    float min = .01f;
                    float max = .08f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.CritDamage) {
                    float min = .1f;
                    float max = 1f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.SightRadius) {
                    affixes.add(AffixFactory.createAffix(affixType, 1));
                }
            }
        }
        
        EquippableItem item = new EquippableItem(gamechar, baseName, null, image, Ability.TargetType.Passive.getType(), 
                cooldown, cost, range, maxStacks, currentStacks, rarity, Item.ItemType.Gear, 
                Item.GlobalCooldownGroup.None, EquippableItem.EquipType.Head, affixes) {
                    @Override
                    public String getDescription() {
                        return this.getAffixCompoundString() +
                                "<7A wearable headpiece.>";
                    }

                    @Override
                    public boolean canEquip() {
                        return true;
                    }           
        };
        
        return item;
    }
    
    /**
     * Chestpiece
     */
    private static Item createChestpiece(GameCharacter gamechar, int level) {
        Random rng = GameLogic.rng;
        
        final Item.Rarity rarity = rollRarity();
        final String baseName = rarity.getType() + " Chestpiece";
        Image baseImage = AssetLoader.image("item_chestpiece");
        Image image = createRarityImage(baseImage, rarity.getColor());
        final int cooldown = 0;
        final int cost = 0;
        final int range = 0;
        final int maxStacks = 1;
        final int currentStacks = 1;
        final int baseDamage = 2;
        final int maxDamage = 3;
        final int levelDamage = (level - 1);    
        final int lowdamage = (int) ((baseDamage + levelDamage) * rarity.getMultiplier());
        final int highdamage = (int) ((maxDamage + levelDamage) * rarity.getMultiplier());
        final int minAttr = (int) ((6 * (level)) * rarity.getMultiplier());
        final int maxAttr = (int) ((14 * (level)) * rarity.getMultiplier());
        
        ArrayList<Affix> affixes = new ArrayList<Affix>();
        //Rolled Affixes
        RandomBag<BroadAffixType> bag = new RandomBag<BroadAffixType>(rng, 1000);
        bag.addPairing(BroadAffixType.Extra, 450);
        bag.addPairing(BroadAffixType.Attribute, 1000);
        
        for (int i = 0; i < rarity.getAffixes(); i++) {
            BroadAffixType broadAffixType = bag.getRandomObject();
            AffixType affixType = broadAffixType.getTypes()[rng.nextInt(broadAffixType.getTypes().length)];
            
            if (broadAffixType == BroadAffixType.Attribute) {
                affixes.add(AffixFactory.createAffix(affixType, minAttr + rng.nextInt(maxAttr - minAttr + 1)));
            }
            else if (broadAffixType == BroadAffixType.Extra) {
                if (affixType == AffixType.HealthRegen || affixType == AffixType.ManaRegen) {
                    float min = .25f * (float) level;
                    float max = 8f * (float) level;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextFloat() * (max - min)))));
                }
                else if (affixType == AffixType.CritChance) {
                    float min = .01f;
                    float max = .08f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.CritDamage) {
                    float min = .1f;
                    float max = 1f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.SightRadius) {
                    affixes.add(AffixFactory.createAffix(affixType, 1));
                }
            }
        }
        
        EquippableItem item = new EquippableItem(gamechar, baseName, null, image, Ability.TargetType.Passive.getType(), 
                cooldown, cost, range, maxStacks, currentStacks, rarity, Item.ItemType.Gear, 
                Item.GlobalCooldownGroup.None, EquippableItem.EquipType.Chest, affixes) {
                    @Override
                    public String getDescription() {
                        return this.getAffixCompoundString() +
                                "<7A wearable chestpiece.>";
                    }

                    @Override
                    public boolean canEquip() {
                        return true;
                    }           
        };
        
        return item;
    }
    
    /**
     * Legpiece
     */
    private static Item createLegpiece(GameCharacter gamechar, int level) {
        Random rng = GameLogic.rng;
        
        final Item.Rarity rarity = rollRarity();
        final String baseName = rarity.getType() + " Legpiece";
        Image baseImage = AssetLoader.image("item_legpiece");
        Image image = createRarityImage(baseImage, rarity.getColor());
        final int cooldown = 0;
        final int cost = 0;
        final int range = 0;
        final int maxStacks = 1;
        final int currentStacks = 1;
        final int baseDamage = 2;
        final int maxDamage = 3;
        final int levelDamage = (level - 1);    
        final int lowdamage = (int) ((baseDamage + levelDamage) * rarity.getMultiplier());
        final int highdamage = (int) ((maxDamage + levelDamage) * rarity.getMultiplier());
        final int minAttr = (int) ((6 * (level)) * rarity.getMultiplier());
        final int maxAttr = (int) ((14 * (level)) * rarity.getMultiplier());
        
        ArrayList<Affix> affixes = new ArrayList<Affix>();
        //Rolled Affixes
        RandomBag<BroadAffixType> bag = new RandomBag<BroadAffixType>(rng, 1000);
        bag.addPairing(BroadAffixType.Extra, 450);
        bag.addPairing(BroadAffixType.Attribute, 1000);
        
        for (int i = 0; i < rarity.getAffixes(); i++) {
            BroadAffixType broadAffixType = bag.getRandomObject();
            AffixType affixType = broadAffixType.getTypes()[rng.nextInt(broadAffixType.getTypes().length)];
            
            if (broadAffixType == BroadAffixType.Attribute) {
                affixes.add(AffixFactory.createAffix(affixType, minAttr + rng.nextInt(maxAttr - minAttr + 1)));
            }
            else if (broadAffixType == BroadAffixType.Extra) {
                if (affixType == AffixType.HealthRegen || affixType == AffixType.ManaRegen) {
                    float min = .25f * (float) level;
                    float max = 8f * (float) level;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextFloat() * (max - min)))));
                }
                else if (affixType == AffixType.CritChance) {
                    float min = .01f;
                    float max = .08f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.CritDamage) {
                    float min = .1f;
                    float max = 1f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.SightRadius) {
                    affixes.add(AffixFactory.createAffix(affixType, 1));
                }
            }
        }
        
        EquippableItem item = new EquippableItem(gamechar, baseName, null, image, Ability.TargetType.Passive.getType(), 
                cooldown, cost, range, maxStacks, currentStacks, rarity, Item.ItemType.Gear, 
                Item.GlobalCooldownGroup.None, EquippableItem.EquipType.Legs, affixes) {
                    @Override
                    public String getDescription() {
                        return this.getAffixCompoundString() +
                                "<7A wearable legpiece.>";
                    }

                    @Override
                    public boolean canEquip() {
                        return true;
                    }           
        };
        
        return item;
    }
    
    /**
     * Footpiece
     */
    private static Item createFootpiece(GameCharacter gamechar, int level) {
        Random rng = GameLogic.rng;
        
        final Item.Rarity rarity = rollRarity();
        final String baseName = rarity.getType() + " Footpiece";
        Image baseImage = AssetLoader.image("item_footpiece");
        Image image = createRarityImage(baseImage, rarity.getColor());
        final int cooldown = 0;
        final int cost = 0;
        final int range = 0;
        final int maxStacks = 1;
        final int currentStacks = 1;
        final int baseDamage = 2;
        final int maxDamage = 3;
        final int levelDamage = (level - 1);    
        final int lowdamage = (int) ((baseDamage + levelDamage) * rarity.getMultiplier());
        final int highdamage = (int) ((maxDamage + levelDamage) * rarity.getMultiplier());
        final int minAttr = (int) ((4 * (level)) * rarity.getMultiplier());
        final int maxAttr = (int) ((10 * (level)) * rarity.getMultiplier());
        
        ArrayList<Affix> affixes = new ArrayList<Affix>();
        //Rolled Affixes
        RandomBag<BroadAffixType> bag = new RandomBag<BroadAffixType>(rng, 1000);
        bag.addPairing(BroadAffixType.Extra, 450);
        bag.addPairing(BroadAffixType.Attribute, 1000);
        
        for (int i = 0; i < rarity.getAffixes(); i++) {
            BroadAffixType broadAffixType = bag.getRandomObject();
            AffixType affixType = broadAffixType.getTypes()[rng.nextInt(broadAffixType.getTypes().length)];
            
            if (broadAffixType == BroadAffixType.Attribute) {
                affixes.add(AffixFactory.createAffix(affixType, minAttr + rng.nextInt(maxAttr - minAttr + 1)));
            }
            else if (broadAffixType == BroadAffixType.Extra) {
                if (affixType == AffixType.HealthRegen || affixType == AffixType.ManaRegen) {
                    float min = .25f * (float) level;
                    float max = 8f * (float) level;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextFloat() * (max - min)))));
                }
                else if (affixType == AffixType.CritChance) {
                    float min = .01f;
                    float max = .08f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.CritDamage) {
                    float min = .1f;
                    float max = 1f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.SightRadius) {
                    affixes.add(AffixFactory.createAffix(affixType, 1));
                }
            }
        }
        
        EquippableItem item = new EquippableItem(gamechar, baseName, null, image, Ability.TargetType.Passive.getType(), 
                cooldown, cost, range, maxStacks, currentStacks, rarity, Item.ItemType.Gear, 
                Item.GlobalCooldownGroup.None, EquippableItem.EquipType.Feet, affixes) {
                    @Override
                    public String getDescription() {
                        return this.getAffixCompoundString() +
                                "<7A wearable footpiece.>";
                    }

                    @Override
                    public boolean canEquip() {
                        return true;
                    }           
        };
        
        return item;
    }
    
    /**
     * Handpiece
     */
    private static Item createHandpiece(GameCharacter gamechar, int level) {
        Random rng = GameLogic.rng;
        
        final Item.Rarity rarity = rollRarity();
        final String baseName = rarity.getType() + " Handpiece";
        Image baseImage = AssetLoader.image("item_handpiece");
        Image image = createRarityImage(baseImage, rarity.getColor());
        final int cooldown = 0;
        final int cost = 0;
        final int range = 0;
        final int maxStacks = 1;
        final int currentStacks = 1;
        final int baseDamage = 2;
        final int maxDamage = 3;
        final int levelDamage = (level - 1);    
        final int lowdamage = (int) ((baseDamage + levelDamage) * rarity.getMultiplier());
        final int highdamage = (int) ((maxDamage + levelDamage) * rarity.getMultiplier());
        final int minAttr = (int) ((4 * (level)) * rarity.getMultiplier());
        final int maxAttr = (int) ((10 * (level)) * rarity.getMultiplier());
        
        ArrayList<Affix> affixes = new ArrayList<Affix>();
        //Rolled Affixes
        RandomBag<BroadAffixType> bag = new RandomBag<BroadAffixType>(rng, 1000);
        bag.addPairing(BroadAffixType.Extra, 450);
        bag.addPairing(BroadAffixType.Attribute, 1000);
        
        for (int i = 0; i < rarity.getAffixes(); i++) {
            BroadAffixType broadAffixType = bag.getRandomObject();
            AffixType affixType = broadAffixType.getTypes()[rng.nextInt(broadAffixType.getTypes().length)];
            
            if (broadAffixType == BroadAffixType.Attribute) {
                affixes.add(AffixFactory.createAffix(affixType, minAttr + rng.nextInt(maxAttr - minAttr + 1)));
            }
            else if (broadAffixType == BroadAffixType.Extra) {
                if (affixType == AffixType.HealthRegen || affixType == AffixType.ManaRegen) {
                    float min = .25f * (float) level;
                    float max = 8f * (float) level;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextFloat() * (max - min)))));
                }
                else if (affixType == AffixType.CritChance) {
                    float min = .01f;
                    float max = .08f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.CritDamage) {
                    float min = .1f;
                    float max = 1f;
                    affixes.add(AffixFactory.createAffix(affixType, (float) (min + (rng.nextDouble() * (max - min)))));
                }
                else if (affixType == AffixType.SightRadius) {
                    affixes.add(AffixFactory.createAffix(affixType, 1));
                }
            }
        }
        
        EquippableItem item = new EquippableItem(gamechar, baseName, null, image, Ability.TargetType.Passive.getType(), 
                cooldown, cost, range, maxStacks, currentStacks, rarity, Item.ItemType.Gear, 
                Item.GlobalCooldownGroup.None, EquippableItem.EquipType.Hands, affixes) {
                    @Override
                    public String getDescription() {
                        return this.getAffixCompoundString() +
                                "<7A wearable handpiece.>";
                    }

                    @Override
                    public boolean canEquip() {
                        return true;
                    }           
        };
        
        return item;
    }
	
	/**
	 * Chooses a random rarity using weights
	 */
	private static Item.Rarity rollRarity() {
	    RandomBag<Item.Rarity> bag = new RandomBag<Item.Rarity>(GameLogic.rng, 1000);
	    bag.addPairing(Item.Rarity.Legendary, 5);
	    bag.addPairing(Item.Rarity.Epic, 20);
	    bag.addPairing(Item.Rarity.Rare, 80);
	    bag.addPairing(Item.Rarity.Uncommon, 250);
	    bag.addPairing(Item.Rarity.Common, 750);
	    bag.addPairing(Item.Rarity.Junk, 1000);
	    return bag.getRandomObject();
	}
	
	/**
	 * Creates an image that grows brighter and less transparent with level
	 */
	private static Image createLevelScalingImage(Image baseImage, int level, int maxLevel) {
		float scale = (float) level / (float) maxLevel;
		
		ImageBuffer image = new ImageBuffer(baseImage.getWidth(), baseImage.getHeight());
		
		//Set Pixels to get increasingly brighter
		for (int r = 0; r < image.getHeight(); r++) {
			for (int c = 0; c < image.getWidth(); c++) {
				Color color = baseImage.getColor(c, r);
				if (level < (maxLevel / 2)) {
					color = color.darker(.5f - (scale * 1.25f));
				}
				else {
					color = color.brighter(scale * 1.25f);
				}
				
				//Erase black background, replace with increasingly white and less transparent
				if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
					//Change Background Color Based on Level
					int amount = (int) (255f * scale);
					color = new Color(amount, amount, amount, amount);
				}
				
				image.setRGBA(c, r, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
			}
		}
		
		//Set beauty mark
		/*for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 5; c++) {
				if (r != 0 && r != 5 && c != 0 && c != 5) {
					int amount = 255 - (int) (255f * scale);
					image.setRGBA(c + 5 + (int) (scale * (float) (image.getWidth() - 10)), r + 5 + (int) (scale * (float) (image.getHeight() - 10)), amount, amount, amount, 255);
				}
			}
		}*/
		
		return image.getImage();
	}
	
	/**
	 * Creates an image colored by rarity
	 */
	private static Image createRarityImage(Image baseImage, Color rarityColor) {
        ImageBuffer image = new ImageBuffer(baseImage.getWidth(), baseImage.getHeight());
        
        //Set Background pixels to rarity
        for (int r = 0; r < image.getHeight(); r++) {
            for (int c = 0; c < image.getWidth(); c++) {
                Color color = baseImage.getColor(c, r);
                
                //Erase black background, replace with rarity color
                if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                    //Change Background Color Based on Level
                    color = rarityColor;
                }
                
                image.setRGBA(c, r, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            }
        }
        
        return image.getImage();
    }
	
	public static ItemFactory get() {
		return self;
	}
}
