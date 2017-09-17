package com.fizzikgames.roguelike.entity;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import com.fizzikgames.roguelike.asset.AssetLoader;
import com.fizzikgames.roguelike.entity.FloatingContextStack.ContextType;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEvent;
import com.fizzikgames.roguelike.entity.mechanics.TriggerEventListener;
import com.fizzikgames.roguelike.entity.mechanics.buff.Debuff;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalHealth;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TrapSightRadius;
import com.fizzikgames.roguelike.sprite.CharacterSprite;
import com.fizzikgames.roguelike.sprite.Sprite;
import com.fizzikgames.roguelike.world.Cell;
import com.fizzikgames.roguelike.world.Level;

public class Trap extends Entity implements GameCharacterListener {
    private Sprite sprite;
    private Level level;
    private boolean visible;
    
    public Trap(Level level) {
        this.level = level;
        this.visible = false;
        this.sprite = new CharacterSprite("extra_trap");
        this.sprite.current("stance_default");
        setRenderPriority(RenderPriority.Trap.getPriority());
    }
    
    @Override
    public void eventPerformed(GameCharacterEvent e) {
        final GameCharacter gamechar = e.getGameCharacter();
        if (e.getEventType().equals(GameCharacterEvent.Type.Position_Modified.getType())) {
            // Check Position against character, and then trigger the trap if necessary or set visibility
            if (level.hasLineOfSight(new Vector2f(this.getColumn(), this.getRow()), new Vector2f(gamechar.getColumn(), gamechar.getRow()), 
                    (int) gamechar.getStat(Stat_TrapSightRadius.REFERENCE).getModifiedValue())) {
                visible = true;
            }
            else {
                visible = false;
            }
            
            final int damage = (int) Math.floor(gamechar.getStat(Stat_TotalHealth.REFERENCE).getModifiedValue() / 100);
            if (new Cell(this.getRow(), this.getColumn()).equals(new Cell(gamechar.getRow(), gamechar.getColumn()))) {
                level.getContextStack().addNewContext(ContextType.TakeDamage, AssetLoader.image("ability_hemorrhage"), "Activated Trap!", 
                        getRow(), getColumn());
                
                //Apply Trap Debuff
                gamechar.addDebuff(new Debuff(gamechar, "Trap Debuff 1", AssetLoader.image("ability_hemorrhage"),
                        10, 99, 1, false) {
                    @Override
                    protected void initialize() {
                        final Debuff debuff = this;
                        TriggerEventListener trigger = new TriggerEventListener(TriggerEvent.Type.GameChar_TurnEnded.getType()) {
                            @Override
                            public void trigger(TriggerEvent e) {
                                gamechar.dealDamage(damage * debuff.getCurrentStacks());
                            }                            
                        };
                        triggers.add(trigger);
                    }
                    
                    @Override
                    public String getAestheticName() {
                        return "Gaping Wound";
                    }
                    
                    @Override
                    public String getDescription() {
                        return "<4" + getAestheticName() + ((this.getCurrentStacks() > 1) ? (" (" + this.getCurrentStacks() + ")") : "") + ">" + 
                                "<br>" +
                                "Impaled by a Trap." +
                                "<br>" +
                                "Deals " + damage * this.getCurrentStacks() + " damage per turn.";
                    }
                });
                
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
    
    public boolean isVisible() {
        return visible;
    }
    
    public Image getSpriteImage() {
        return sprite.image();
    }
}
