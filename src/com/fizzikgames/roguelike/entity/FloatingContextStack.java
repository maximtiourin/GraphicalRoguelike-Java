package com.fizzikgames.roguelike.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

/**
 * Contains all floating context information to be rendered in the game world.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class FloatingContextStack extends Entity {
    public enum ContextType {
        TakeDamage("font_expressway", new Color(200, 0, 0), 0, false, 1000, 1.5f, true, 0),
        DealDamage("font_expressway", Color.white, 2, true, 1500, 1f, true, 1000),
        DealCritical("font_expressway", Color.yellow, 4, true, 2000, 1f, true, 1000),
        HealHealth("font_expressway", new Color(42, 160, 48), 0, true, 1750, 1f, true, 1000),
        HealMana("font_expressway", new Color(0, 114, 255), 0, true, 1750, 1f, true, 1000),
        Info("font_expressway", Color.cyan, 0, true, 1500, 1f, false, 2000),
        Item("font_expressway", Color.cyan, 0, true, 1500, -1f, true, 2000),
        Level("font_expressway", new Color(255, 216, 0), 4, true, 2000, 1.25f, false, 3000),
        Xp("font_expressway", new Color(168, 41, 221), 0, true, 1000, -.75f, true, 0),
        Aggro("font_expressway", new Color(5, 156, 140), -1, true, 1250, .5f, false, 0);
        
        private String font;
        private Color color;
        private int fontSizeIncrease;
        private boolean outline;
        private int duration;
        private float yspeed;
        private boolean random;
        private int priorityOffset;
        
        private ContextType(String font, Color color, int fontSizeIncrease, boolean outline, int duration, float yspeed, boolean random,
                int priorityOffset) {
            this.font = font;
            this.color = color;
            this.fontSizeIncrease = fontSizeIncrease;
            this.outline = outline;
            this.duration = duration;
            this.yspeed = yspeed;
            this.random = random;
            this.priorityOffset = priorityOffset;
        }
        
        public String getFont() {
            return font;
        }
        
        public Color getColor() {
            return color;
        }
        
        public int getFontSizeIncrease() {
            return fontSizeIncrease;
        }
        
        public boolean getOutline() {
            return outline;
        }
        
        public int getDuration() {
            return duration;
        }
        
        public float getYSpeed() {
            return yspeed;
        }
        
        public boolean getRandom() {
            return random;
        }
        
        public int getPriorityOffset() {
            return priorityOffset;
        }
    }
    
    protected CopyOnWriteArrayList<FloatingContext> contexts;
    
    public FloatingContextStack() {
        super();
        
        contexts = new CopyOnWriteArrayList<FloatingContext>();
        
        setRenderPriority(RenderPriority.FloatingContextStack.getPriority());
    }

    @Override
    public void update(GameContainer gc, int delta) {
        for (FloatingContext e : contexts) {
            e.update(gc, delta);
        }
    }
    
    public int getNextContextRenderPriority() {
        if (contexts.size() <= 0) {
            return getRenderPriority();
        }
        else {
            return contexts.get(contexts.size() - 1).getRenderPriority() + 1;
        }
    }
    
    public List<FloatingContext> getContextsInRectangle(Rectangle rect) {
        ArrayList<FloatingContext> returnList = new ArrayList<FloatingContext>();
        
        for (FloatingContext e : contexts) {
            Vector2f pos = new Vector2f(e.getColumn(), e.getRow()); 
            
            if (((pos.getX() >= rect.getX()) && (pos.getX() <= rect.getX() + rect.getWidth())) 
                    && ((pos.getY() >= rect.getY()) && (pos.getY() <= rect.getY() + rect.getHeight()))) {
                returnList.add(e);
            }
        }
        
        return returnList;
    }
    
    public void addNewContext(ContextType type, Image image, String text, int r, int c) {
        contexts.add(new FloatingContext(this, image, text, type.getFont(), type.getColor(), type.getFontSizeIncrease(), type.getOutline(),
                type.getDuration(), type.getYSpeed(), type.getRandom(), type.getPriorityOffset(), r, c));
    }
    
    public boolean removeContext(FloatingContext c) {
        return contexts.remove(c);
    }
}
