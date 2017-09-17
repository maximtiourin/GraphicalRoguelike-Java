package com.fizzikgames.roguelike.entity;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;

import com.fizzikgames.roguelike.asset.AssetLoader;
import com.fizzikgames.roguelike.entity.FloatingContextStack.ContextType;
import com.fizzikgames.roguelike.entity.mechanics.Modifier;
import com.fizzikgames.roguelike.entity.mechanics.ModifyTarget;
import com.fizzikgames.roguelike.entity.mechanics.buff.Buff;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_HealthRegeneration;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_OutgoingMeleeDamage;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_SightRadius;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TrapSightRadius;
import com.fizzikgames.roguelike.sprite.CharacterSprite;
import com.fizzikgames.roguelike.sprite.Sprite;
import com.fizzikgames.roguelike.world.Cell;
import com.fizzikgames.roguelike.world.Level;

public class Shrine extends Entity implements GameCharacterListener {
    public enum ShrineType {
        Allsight("extra_shrine"), Ragnar("extra_shrine02");
        
        private String sprite;
        
        private ShrineType(String sprite) {
            this.sprite = sprite;
        }
        
        public String getSprite() {
            return sprite;
        }
    }
    
    private Sprite sprite;
    private Level level;
    private ShrineType type;
    
    public Shrine(Level level, ShrineType type) {
        this.type = type;
        this.level = level;
        this.sprite = new CharacterSprite(type.getSprite());
        this.sprite.current("stance_default");
        setRenderPriority(RenderPriority.Shrine.getPriority());
    }
    
    @Override
    public void eventPerformed(GameCharacterEvent e) {
        final GameCharacter gamechar = e.getGameCharacter();
        if (e.getEventType().equals(GameCharacterEvent.Type.Position_Modified.getType())) {
            // Check Position against character, and then trigger the shrine       
            final int sightRadius = 10;
            final int trapSightRadius = 6;
            final float healthregen = 6;
            final int meleedamage = 30;
            if (new Cell(this.getRow(), this.getColumn()).equals(new Cell(gamechar.getRow(), gamechar.getColumn()))) {
                if (type == ShrineType.Allsight) {                
                    gamechar.getLevel().getContextStack().addNewContext(ContextType.Info, AssetLoader.image("ability_allsight"), "Blessing of Allsight", 
                            gamechar.getRow(), gamechar.getColumn());
                    
                    //Apply Shrine Buff
                    gamechar.addBuff(new Buff(gamechar, "Shrine Buff 1", AssetLoader.image("ability_allsight"),
                            180, 1, 1, false) {
                        @Override
                        protected void initialize() {
                            final Buff buff = this;
                            Modifier modifier;
                            modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
                                @Override
                                public double modify(double value) {
                                    return value += (sightRadius * buff.getCurrentStacks());
                                }                            
                            };
                            modifyTargets.add(new ModifyTarget(Stat_SightRadius.REFERENCE, modifier));
                            modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
                                @Override
                                public double modify(double value) {
                                    return value += (trapSightRadius * buff.getCurrentStacks());
                                }                            
                            };
                            modifyTargets.add(new ModifyTarget(Stat_TrapSightRadius.REFERENCE, modifier));
                        }
                        
                        @Override
                        public String getAestheticName() {
                            return "Blessing of Allsight";
                        }
                        
                        @Override
                        public String getDescription() {
                            return "<1" + getAestheticName() + ((this.getCurrentStacks() > 1) ? (" (" + this.getCurrentStacks() + ")") : "") + ">" + 
                                    "<br>" +
                                    "Sight Radius increased by " + sightRadius * this.getCurrentStacks() + "." +
                                    "<br>" +
                                    "Trap Sight Radius increased by " + trapSightRadius * this.getCurrentStacks() + ".";
                        }
                    });
                }
                else if (type == ShrineType.Ragnar) {                
                    gamechar.getLevel().getContextStack().addNewContext(ContextType.Info, AssetLoader.image("ability_overwhelmingpower"), 
                            "Blessing of Ragnar", gamechar.getRow(), gamechar.getColumn());
                    
                    //Apply Shrine Buff
                    gamechar.addBuff(new Buff(gamechar, "Shrine Buff 2", AssetLoader.image("ability_overwhelmingpower"),
                            60, 1, 1, false) {
                        @Override
                        protected void initialize() {
                            final Buff buff = this;
                            Modifier modifier;
                            modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
                                @Override
                                public double modify(double value) {
                                    return value += (healthregen * buff.getCurrentStacks());
                                }                            
                            };
                            modifyTargets.add(new ModifyTarget(Stat_HealthRegeneration.REFERENCE, modifier));
                            modifier = new Modifier(Modifier.Priority.ADD.getPriority(), false) {
                                @Override
                                public double modify(double value) {
                                    return value += (meleedamage * buff.getCurrentStacks());
                                }                            
                            };
                            modifyTargets.add(new ModifyTarget(Stat_OutgoingMeleeDamage.REFERENCE, modifier));
                        }
                        
                        @Override
                        public String getAestheticName() {
                            return "Blessing of Ragnar";
                        }
                        
                        @Override
                        public String getDescription() {
                            return "<1" + getAestheticName() + ((this.getCurrentStacks() > 1) ? (" (" + this.getCurrentStacks() + ")") : "") + ">" + 
                                    "<br>" +
                                    "Health Regeneration increased by " + healthregen * this.getCurrentStacks() + "." +
                                    "<br>" +
                                    "Melee Damage increased by " + meleedamage * this.getCurrentStacks() + ".";
                        }
                    });
                }
                
                //Remove Trap
                remove(gamechar);
            }
        }
    }
    
    public void remove(GameCharacter gamechar) {
        gamechar.removeListener(this);
        level.removeEntitiy(this);
    }

    @Override
    public List<String> getEventTypes() {
        ArrayList<String> types = new ArrayList<String>();
        types.add(GameCharacterEvent.Type.Position_Modified.getType());
        return types;
    }

    @Override
    public void update(GameContainer gc, int delta) {
        sprite.step(delta);
    }
    
    public Image getSpriteImage() {
        return sprite.image();
    }
}
