package com.fizzikgames.roguelike.entity;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;

import com.fizzikgames.roguelike.entity.FloatingContextStack.ContextType;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item;
import com.fizzikgames.roguelike.sprite.CharacterSprite;
import com.fizzikgames.roguelike.sprite.Sprite;
import com.fizzikgames.roguelike.world.Cell;
import com.fizzikgames.roguelike.world.Level;

public class ItemEntity extends Entity implements GameCharacterListener {
    private Sprite sprite;
    private Level level;
    private Item item;
    
    public ItemEntity(Level level, Item item) {
        this.level = level;
        this.sprite = new CharacterSprite("extra_item");
        this.sprite.current("stance_default");
        this.item = item;
        setRenderPriority(RenderPriority.Item.getPriority());
    }
    
    @Override
    public void eventPerformed(GameCharacterEvent e) {
        final GameCharacter gamechar = e.getGameCharacter();
        if (e.getEventType().equals(GameCharacterEvent.Type.Position_Modified.getType())) {
            // Check Position against character, and then trigger the item
            if (new Cell(this.getRow(), this.getColumn()).equals(new Cell(gamechar.getRow(), gamechar.getColumn()))) {
                boolean looted = gamechar.getInventory().attemptToInsertItem(item, true);
                
                if (looted) {
                    level.getContextStack().addNewContext(ContextType.Item, item.getIconImage(), item.getAestheticName(), getRow(), getColumn());
                    
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
