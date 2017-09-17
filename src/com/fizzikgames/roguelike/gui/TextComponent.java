package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;

/**
 * A text component is a font, color, and string with an xoffset and yoffset anchored to a position.
 * For use with things like advanced tooltips or labels.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class TextComponent extends Component {
	private String string;
	private UnicodeFont font;
	private Color color;
	
	public TextComponent(String string, UnicodeFont font, Color color, float xoffset, float yoffset, Anchor anchor) {
		super(xoffset, yoffset, anchor);
		this.string = string;
		this.font = font;
		this.color = color;
	}
	
	@Override
	public void render(Graphics g, float x, float y) {
		font.drawString(x + drawx, y + drawy, string, color);
	}
	
	public String getString() {	return string; }
	public UnicodeFont getFont() { return font; }
	public Color getColor() { return color; }

	@Override
	public int getWidth() {
		return font.getWidth(string);
	}

	@Override
	public int getHeight() {
		return font.getHeight(string);
	}
}
