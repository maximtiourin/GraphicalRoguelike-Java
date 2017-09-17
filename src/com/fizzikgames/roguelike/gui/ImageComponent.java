package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * A image component is a image with a with, height, and an xoffset and yoffset anchored to a position.
 * For use with things like advanced tooltips or labels.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class ImageComponent extends Component {
	private Image image;
	private int width;
	private int height;
	
	public ImageComponent(Image image, int width, int height, float xoffset, float yoffset, Anchor anchor) {
		super(xoffset, yoffset, anchor);
		
		this.image = image.getScaledCopy(width, height);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void render(Graphics g, float x, float y) {
		g.drawImage(image, x + drawx, y + drawy);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
}
