package com.fizzikgames.roguelike.entity;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.asset.AssetLoader;

public class FloatingContext extends Entity {
    private static final int MILLISECONDS_IN_A_SECOND = 1000;
    private Image image;
    private String text;
    private String font;
    private Color fontColor;
    private int fontSizeIncrease;
    private boolean outline;
    private int startDuration; //length in milliseconds to stay alive
    private int currentDuration; //remaining in milliseconds to stay alive
    private float yspeed; //cells a second
    private FloatingContextStack stack;
    private float randomxoffset;
    private float randomyoffset;
    private boolean random;
    
    public FloatingContext (FloatingContextStack stack, Image image, String text, String font, Color fontColor, int fontSizeIncrease, 
            boolean outline, int startDuration, float yspeed, boolean random, int priorityOffset, int r, int c) {
        super(r, c);
        this.stack = stack;
        this.startDuration = startDuration;
        this.currentDuration = startDuration;
        this.yspeed = yspeed;
        this.image = image;
        this.text = text;
        this.font = font;
        this.fontColor = fontColor;
        this.fontSizeIncrease = fontSizeIncrease;
        this.outline = outline;
        this.random = random;
        
        randomxoffset = GameLogic.rng.nextFloat();
        randomyoffset = GameLogic.rng.nextFloat();
        
        setRenderPriority(RenderPriority.FloatingContextStack.getPriority() + priorityOffset);
    }

    @Override
    public void update(GameContainer gc, int delta) {
        currentDuration -= delta;
        
        if (currentDuration <= 0) {
            stack.removeContext(this);
        }
    }
    
    public void render(final Graphics g, final int cellsize, final float screenx, final float screeny) {
        final int timePassed = startDuration - currentDuration;
        
        final int FADE_TIME = 500;
        int alpha = 255;
        float alpharatio = 1f;
        if (currentDuration < FADE_TIME) {
            alpharatio = (float) currentDuration / (float) FADE_TIME;
            alpha *= alpharatio;
        }
        
        final float dy = ((float) timePassed / (float) MILLISECONDS_IN_A_SECOND) * yspeed;
        final float scaled_dy = dy * cellsize;
        
        final float centerdrawx = screenx + (cellsize / 2);
        final float centerdrawy = screeny + (cellsize / 2) - scaled_dy;
        float rxoffset = 0;
        float ryoffset = 0;
        if (random) {
            rxoffset = -(cellsize / 4) + (randomxoffset * (cellsize / 2));
            ryoffset = -(cellsize / 4) + (randomyoffset * (cellsize / 2));
        }
        
        UnicodeFont drawfont = AssetLoader.font(font, ((float) cellsize / 3) + fontSizeIncrease);
        
        final int imagexoffset = cellsize / 8;
        final int fontwidth = drawfont.getWidth(text);
        final int fontheight = drawfont.getHeight(text);
        
        int width = fontwidth;
        int height = fontheight;
        
        Image drawimage = null;
        if (image != null) {
            drawimage = image.getScaledCopy(cellsize / 2, cellsize / 2);
            width += drawimage.getWidth() + imagexoffset;
            height = Math.max(drawimage.getHeight(), height);
        }
        
        float drawx = centerdrawx - (width / 2) + rxoffset;
        float drawy = centerdrawy - (height / 2) + ryoffset;
        
        //Draw
        float xoffset = 0;
        if (image != null) {
            drawimage.setAlpha(alpharatio);
            g.drawImage(drawimage, drawx, drawy);
            xoffset += drawimage.getWidth() + imagexoffset;
        }
        if (outline) {
            g.setColor(new Color(0, 0, 0, alpha));
            g.setFont(drawfont);
            g.drawString(text, drawx + xoffset - 1, drawy - 1);
            g.drawString(text, drawx + xoffset + 0, drawy - 1);
            g.drawString(text, drawx + xoffset + 1, drawy - 1);
            g.drawString(text, drawx + xoffset - 1, drawy + 0);
            g.drawString(text, drawx + xoffset + 1, drawy + 0);
            g.drawString(text, drawx + xoffset - 1, drawy + 1);
            g.drawString(text, drawx + xoffset + 0, drawy + 1);
            g.drawString(text, drawx + xoffset + 1, drawy + 1);
        }
        g.setColor(new Color(fontColor.getRed(), fontColor.getGreen(), fontColor.getBlue(), alpha));
        g.setFont(drawfont);
        g.drawString(text, drawx + xoffset, drawy);
    }
}
