package com.fizzikgames.roguelike.entity.mechanics.ability.item;

import java.text.DecimalFormat;

import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEvent;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEventListener;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability_DefaultRangedAttack;
import com.fizzikgames.roguelike.entity.mechanics.buff.Buff;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CriticalHitChance;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CriticalHitDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_Dexterity;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_HealthRegeneration;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_Intelligence;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_ManaRegeneration;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_OutgoingMeleeDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_OutgoingRangedDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_SightRadius;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_Strength;

public class AffixFactory {
	public enum AffixType {
	    Strength, Dexterity, Intelligence,
	    HealthRegen, ManaRegen,
		MeleeDamage, RangedDamage, CritChance, CritDamage,
		SightRadius,
		TriggerRangedCrit1;
	}
	
	public enum BroadAffixType {
	    Attribute(new AffixType[]{AffixType.Strength, AffixType.Dexterity, AffixType.Intelligence}),
	    WeaponDamage(new AffixType[]{AffixType.MeleeDamage, AffixType.RangedDamage}),
	    RangedWeapon(new AffixType[]{AffixType.TriggerRangedCrit1}),
	    Extra(new AffixType[]{AffixType.HealthRegen, AffixType.ManaRegen, AffixType.SightRadius, AffixType.CritChance, AffixType.CritDamage});
	    
	    private AffixType[] types;
	    
	    private BroadAffixType(AffixType[] types) {
	        this.types = types;
	    }
	    
	    public AffixType[] getTypes() {
	        return types;
	    }
	}
	
	private static final AffixFactory self = new AffixFactory();
	private static final DecimalFormat formatter = new DecimalFormat("###.#");
	
	public AffixFactory() {
		
	}
	
	public static Affix createAffix(AffixType type, double value) {
	    return createAffixFull(type, value, 0, 0, 0, 0, "", "", "");
	}
	
	public static Affix createAffix(AffixType type, double value, double value2) {
        return createAffixFull(type, value, value2, 0, 0, 0, "", "", "");
    }
	
	public static Affix createAffix(AffixType type, double value, double value2, double value3) {
        return createAffixFull(type, value, value2, value3, 0, 0, "", "", "");
    }
	
	public static Affix createAffix(AffixType type, double value, double value2, double value3, double value4) {
        return createAffixFull(type, value, value2, value3, value4, 0, "", "", "");
    }
	
	public static Affix createAffix(AffixType type, double value, double value2, double value3, double value4, double value5) {
        return createAffixFull(type, value, value2, value3, value4, value5, "", "", "");
    }
	
	public static Affix createAffix(AffixType type, double value, String string) {
        return createAffixFull(type, value, 0, 0, 0, 0, string, "", "");
    }
	
	private static Affix createAffixFull(AffixType type, double value, double value2, double value3, double value4, double value5,
	        String stringval, String stringval2, String stringval3) {
	    if (type == AffixType.Strength) {
            return createStrengthAffix(value);
        }
	    else if (type == AffixType.Dexterity) {
            return createDexterityAffix(value);
        }
	    else if (type == AffixType.Intelligence) {
            return createIntelligenceAffix(value);
        }
	    else if (type == AffixType.MeleeDamage) {
			return createMeleeDamageAffix(value);
		}
		else if (type == AffixType.RangedDamage) {
			return createRangedDamageAffix(value);
		}
		else if (type == AffixType.CritChance) {
            return createCritChanceAffix(value);
        }
		else if (type == AffixType.CritDamage) {
            return createCritDamageAffix(value);
        }
		else if (type == AffixType.HealthRegen) {
            return createHealthRegenAffix(value);
        }
		else if (type == AffixType.ManaRegen) {
            return createManaRegenAffix(value);
        }
		else if (type == AffixType.SightRadius) {
            return createSightRadiusAffix(value);
        }
		else if (type == AffixType.TriggerRangedCrit1) {
			return createTriggerRangedCrit1Affix(value, stringval);
		}
		
		return null;
	}
	
	/**
     * Strength
     */
    private static Affix createStrengthAffix(final double value) {
        Modifier modifier;      
        
        final double pvalue = value;
        final String rvalue = formatter.format(pvalue);
        
        Affix affix = new Affix() {
            @Override
            public String getDescription() {
                return rvalue + " Strength";
            }           
        };
        
        modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
            @Override
            public double modify(double value) {
                return value + pvalue;
            }           
        };
        affix.addNewModifyTarget(new ModifyTarget(Stat_Strength.REFERENCE, modifier));
        
        return affix;
    }
    
    /**
     * Dexterity
     */
    private static Affix createDexterityAffix(final double value) {
        Modifier modifier;      
        
        final double pvalue = value;
        final String rvalue = formatter.format(pvalue);
        
        Affix affix = new Affix() {
            @Override
            public String getDescription() {
                return rvalue + " Dexterity";
            }           
        };
        
        modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
            @Override
            public double modify(double value) {
                return value + pvalue;
            }           
        };
        affix.addNewModifyTarget(new ModifyTarget(Stat_Dexterity.REFERENCE, modifier));
        
        return affix;
    }
    
    /**
     * Intelligence
     */
    private static Affix createIntelligenceAffix(final double value) {
        Modifier modifier;      
        
        final double pvalue = value;
        final String rvalue = formatter.format(pvalue);
        
        Affix affix = new Affix() {
            @Override
            public String getDescription() {
                return rvalue + " Intelligence";
            }           
        };
        
        modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
            @Override
            public double modify(double value) {
                return value + pvalue;
            }           
        };
        affix.addNewModifyTarget(new ModifyTarget(Stat_Intelligence.REFERENCE, modifier));
        
        return affix;
    }
	
	/**
	 * Melee Damage
	 */
	private static Affix createMeleeDamageAffix(final double value) {
		Modifier modifier;		
		
		final double pvalue = value;
		final String rvalue = formatter.format(pvalue);
		
		Affix affix = new Affix() {
			@Override
			public String getDescription() {
				return rvalue + " Melee Damage";
			}			
		};
		
		modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
			@Override
			public double modify(double value) {
				return value + pvalue;
			}			
		};
		affix.addNewModifyTarget(new ModifyTarget(Stat_OutgoingMeleeDamage.REFERENCE, modifier));
		
		return affix;
	}
	
	/**
	 * Ranged Damage
	 */
	private static Affix createRangedDamageAffix(final double value) {
		Modifier modifier;		
		
		final double pvalue = value;
		final String rvalue = formatter.format(pvalue);
		
		Affix affix = new Affix() {
			@Override
			public String getDescription() {
				return rvalue + " Ranged Damage";
			}			
		};
		
		modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
			@Override
			public double modify(double value) {
				return value + pvalue;
			}			
		};
		affix.addNewModifyTarget(new ModifyTarget(Stat_OutgoingRangedDamage.REFERENCE, modifier));
		
		return affix;
	}
	
	/**
     * Critical Hit Chance
     */
    private static Affix createCritChanceAffix(final double value) {
        Modifier modifier;      
        
        final double pvalue = value;
        final String rvalue = formatter.format(pvalue * 100);
        
        Affix affix = new Affix() {
            @Override
            public String getDescription() {
                return rvalue + "% Critical Hit Chance";
            }           
        };
        
        modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
            @Override
            public double modify(double value) {
                return value + pvalue;
            }           
        };
        affix.addNewModifyTarget(new ModifyTarget(Stat_CriticalHitChance.REFERENCE, modifier));
        
        return affix;
    }
    
    /**
     * Critical Hit Damage
     */
    private static Affix createCritDamageAffix(final double value) {
        Modifier modifier;      
        
        final double pvalue = value;
        final String rvalue = formatter.format(pvalue * 100);
        
        Affix affix = new Affix() {
            @Override
            public String getDescription() {
                return rvalue + "% Critical Hit Damage";
            }           
        };
        
        modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
            @Override
            public double modify(double value) {
                return value + pvalue;
            }           
        };
        affix.addNewModifyTarget(new ModifyTarget(Stat_CriticalHitDamage.REFERENCE, modifier));
        
        return affix;
    }
    
    /**
     * Health Regen
     */
    private static Affix createHealthRegenAffix(final double value) {
        Modifier modifier;      
        
        final double pvalue = value;
        final String rvalue = formatter.format(pvalue);
        
        Affix affix = new Affix() {
            @Override
            public String getDescription() {
                return rvalue + " Health Regeneration";
            }           
        };
        
        modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
            @Override
            public double modify(double value) {
                return value + pvalue;
            }           
        };
        affix.addNewModifyTarget(new ModifyTarget(Stat_HealthRegeneration.REFERENCE, modifier));
        
        return affix;
    }
    
    /**
     * Mana Regen
     */
    private static Affix createManaRegenAffix(final double value) {
        Modifier modifier;      
        
        final double pvalue = value;
        final String rvalue = formatter.format(pvalue);
        
        Affix affix = new Affix() {
            @Override
            public String getDescription() {
                return rvalue + " Mana Regeneration";
            }           
        };
        
        modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
            @Override
            public double modify(double value) {
                return value + pvalue;
            }           
        };
        affix.addNewModifyTarget(new ModifyTarget(Stat_ManaRegeneration.REFERENCE, modifier));
        
        return affix;
    }
    
    /**
     * Sight Radius
     */
    private static Affix createSightRadiusAffix(final double value) {
        Modifier modifier;      
        
        final double pvalue = value;
        final String rvalue = formatter.format(pvalue);
        
        Affix affix = new Affix() {
            @Override
            public String getDescription() {
                return rvalue + " Sight Radius";
            }           
        };
        
        modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
            @Override
            public double modify(double value) {
                return value + pvalue;
            }           
        };
        affix.addNewModifyTarget(new ModifyTarget(Stat_SightRadius.REFERENCE, modifier));
        
        return affix;
    }
	
	/**
	 * Buff - Ranged Critical Hits Increase Ranged Damage temporarily
	 */
	private static Affix createTriggerRangedCrit1Affix(final double damageIncrease, final String reference) {
		TriggerEventListener trigger;
		
		Affix affix = new Affix() {
			@Override
			public String getDescription() {
				return "Ranged Critical Hits Temporarily Increase Ranged Damage";
			}			
		};
		
		trigger = new TriggerEventListener(TriggerEvent.Type.GameChar_RangedCriticalStrike.getType()) {
			@Override
			public void trigger(TriggerEvent e) {
				final GameCharacter gamecharobj = (GameCharacter) e.getTriggeringObject();
				
				Ability ability = gamecharobj.getAbility(Ability_DefaultRangedAttack.REFERENCE);
				
				gamecharobj.addBuff(new Buff(gamecharobj, reference, 
						ability.getIconImage(), 10, 3, 3, false) {
					@Override
					protected void initialize() {
						final Buff buff = this;
						Modifier modifier;
												
						modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false){
							@Override
							public double modify(double value) {
								return value + (damageIncrease * buff.getCurrentStacks());
							}			
						};
						modifyTargets.add(new ModifyTarget(Stat_OutgoingRangedDamage.REFERENCE, modifier));
					}
					
					@Override
					public String getAestheticName() {
						return this.getReference() + " - Deadly Efficiency";
					}
					
					@Override
					public String getDescription() {
						return "<1" + getAestheticName() + ((this.getCurrentStacks() > 1) ? (" (" + this.getCurrentStacks() + ")") : "") + ">" + 
								"<br>" +
								"Increases Ranged Damage by " + (damageIncrease * this.getCurrentStacks()) + "." +
								"<br>" +
								"Gains Stacks on every Ranged Critical Hit.";
					}
				});
			}			
		};
		affix.addNewTrigger(trigger);
		
		return affix;
	}
	
	public static AffixFactory get() {
		return self;
	}
}
