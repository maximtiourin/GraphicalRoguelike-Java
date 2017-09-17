package com.fizzikgames.roguelike.entity;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;

import com.fizzikgames.roguelike.entity.FloatingContextStack.ContextType;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item.ItemType;
import com.fizzikgames.roguelike.sprite.CharacterSprite;
import com.fizzikgames.roguelike.sprite.Sprite;
import com.fizzikgames.roguelike.world.Cell;
import com.fizzikgames.roguelike.world.Level;

public class Chest extends Entity implements GameCharacterListener {
    private Sprite sprite;
    private Level level;
    private Item[] items;
    
    public Chest(Level level, Item[] items) {
        this.level = level;
        this.sprite = new CharacterSprite("extra_chest");
        this.sprite.current("stance_default");
        this.items = items;
        setRenderPriority(RenderPriority.Chest.getPriority());
    }
    
    @Override
    public void eventPerformed(GameCharacterEvent e) {
        final GameCharacter gamechar = e.getGameCharacter();
        if (e.getEventType().equals(GameCharacterEvent.Type.Position_Modified.getType())) {
            // Check Position against character, and then trigger the chest
            if (new Cell(this.getRow(), this.getColumn()).equals(new Cell(gamechar.getRow(), gamechar.getColumn()))) {
                Item key = gamechar.getInventory().findItemWithItemType(ItemType.Key);
                
                if (key != null) {
                    key.consume();
                    
                    for (int i = 0; i < items.length; i++) {
                        boolean looted = gamechar.getInventory().attemptToInsertItem(items[i], true);
                        
                        if (!looted) {
                            level.dropItem(items[i], gamechar.getRow(), gamechar.getColumn());
                        }
                        else {
                            level.getContextStack().addNewContext(ContextType.Item, items[i].getIconImage(), items[i].getAestheticName(), 
                                    getRow(), getColumn());
                        }
                    }
                                        
                    remove(gamechar);
                }
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
