package com.fizzikgames.roguelike.gui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Vector2f;

import com.fizzikgames.roguelike.asset.AssetLoader;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item;
import com.fizzikgames.roguelike.util.StringUtil;

/**
 * A component is an object that can be rendered in something like an advanced tooltip or advanced label.
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Component {
	public static final String LTAG = "<";
	public static final String RTAG = ">";
	public static final String NEWLINE = LTAG + "br" + RTAG;
	public static final Color C_WHITE = new Color(255, 255, 255);
	public static final Color C_LIGHTGREEN = new Color(110, 228, 118);
	public static final Color C_GREEN = new Color(44, 188, 54);
	public static final Color C_RED = new Color(223, 28, 28);
	public static final Color C_DARKRED = new Color(139, 19, 19);
	public static final Color C_LIGHTPURPLE = new Color(197, 183, 236);
	public static final Color C_GOLD = new Color(235, 191, 57);
	public static final Color C_LIGHTBLUE = new Color(168, 231, 255);
	public static final Color C_GREY = new Color(129, 129, 129);
	protected float xoffset;
	protected float yoffset;
	protected Anchor anchor;
	protected float drawx;
	protected float drawy;
	
	public Component(float xoffset, float yoffset, Anchor anchor) {
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		this.anchor = anchor;
		this.drawx = 0;
		this.drawy = 0;
	}
	
	/**
	 * Renders the component to the graphics context at x and y where x and y are the the draw origin of the element calling render.
	 */
	public abstract void render(Graphics g, float x, float y);
	public abstract int getWidth();
	public abstract int getHeight();
	
	public Vector2f getPositionInRectangle(float rectx, float recty, int rectw, int recth) {
		int fwidth = getWidth();
		int fheight = getHeight();
		
		float x = rectx;
		float y = recty;
		if (anchor == Anchor.TOPLEFT || anchor == Anchor.LEFT || anchor == Anchor.BOTLEFT) {
			x = rectx + xoffset;
		}
		else if (anchor == Anchor.TOP || anchor == Anchor.CENTER || anchor == Anchor.BOT) {
			x = rectx + ((float) rectw / 2) - ((float) fwidth / 2) + xoffset;
		}
		else if (anchor == Anchor.TOPRIGHT || anchor == Anchor.RIGHT || anchor == Anchor.BOTRIGHT) {
			x = rectx + rectw - fwidth + xoffset;
		}
		if (anchor == Anchor.TOPLEFT || anchor == Anchor.TOP || anchor == Anchor.TOPRIGHT) {
			y = recty + yoffset;
		}
		else if (anchor == Anchor.LEFT || anchor == Anchor.CENTER || anchor == Anchor.RIGHT) {
			y = recty + ((float) recth / 2) - ((float) fheight / 2) + yoffset;
		}
		else if (anchor == Anchor.BOTLEFT || anchor == Anchor.BOT || anchor == Anchor.BOTRIGHT) {
			y = recty + recth - fheight + yoffset;
		}
		
		x = Math.max(rectx, x);
		y = Math.max(recty, y);
		
		return new Vector2f(x, y);
	}
	
	public Anchor getAnchor() { return anchor; }
	public void setAnchor(Anchor a) { anchor = a; }
	public float drawx() { return drawx; }
	public float drawx(float x) { drawx = x; return drawx; }
	public float drawy() { return drawy; }
	public float drawy(float y) { drawy = y; return drawy; }
	
	public static final List<Component> createStyleAbilityDescription(String name, String type, String desc, int cost, int cooldown, 
			int targetRange) {
		Component c = null;		
		
		Color colorAbility = Color.white;
		Color colorType = new Color(125, 125, 125, 255);
		Color colorCost = new Color(125, 175, 225, 255);
		Color colorCooldown = new Color(150, 225, 250, 255);
		UnicodeFont font1 = AssetLoader.font("font_nightserif", 18); //Ability
		UnicodeFont font2 = AssetLoader.font("font_expressway", 13); //Range
		UnicodeFont font3 = AssetLoader.font("font_expressway", 11); //Cooldown//Desc
		UnicodeFont font4 = AssetLoader.font("font_coolserif", 13); //Type
		UnicodeFont font5 = AssetLoader.font("font_expressway", 13); //Cost
		UnicodeFont font6 = AssetLoader.font("font_expressway", 13); //Desc decoration
		
		ArrayList<Component> tcs = new ArrayList<Component>();
		
		int addHeight = 0;
		
		//Add Ability Name
		c = new TextComponent(name, font1, colorAbility, 0, 0, Anchor.TOP);
		tcs.add(c);
		
		//Add Seperator
		int sepyoffset = 5;
		tcs.add(new ImageComponent(AssetLoader.image("ui_tooltip_horizontalseperator"), 200, 2, 0, sepyoffset + c.getHeight(), Anchor.TOP));
		
		int line1yoffset = 20 + sepyoffset;
		int line2yoffset = 35 + sepyoffset;
		
		//Add Target Type
		tcs.add(new TextComponent(type, font4, colorType, 0, line1yoffset, Anchor.TOPLEFT));
		
		//Add Target Range
		if (targetRange > 0) {
			tcs.add(new TextComponent(targetRange + "m", font2, colorType, 0, line2yoffset - 2, Anchor.TOPLEFT));
			addHeight = 13;
		}
		
		//Add Mana Cost
		if (cost > 0) {
			tcs.add(new TextComponent(cost + " mana", font5, colorCost, 0, line1yoffset + 0, Anchor.TOPRIGHT));
		}
		
		//Add Cooldown
		if (cooldown > 0) {
			int offset = line1yoffset;
			if (cost > 0) offset = line2yoffset;
			c = new TextComponent(cooldown + "", font3, colorCooldown, 0, offset, Anchor.TOPRIGHT);
			tcs.add(c);
			tcs.add(new ImageComponent(AssetLoader.image("ui_tooltip_cooldowntimer"), 
					10, 10, -(c.getWidth()) - 4, offset + 3, Anchor.TOPRIGHT));
			if (cost > 0) addHeight = 13;
		}
		
		//Add Seperator
		int sep2yoffset = 5;
		tcs.add(new ImageComponent(AssetLoader.image("ui_tooltip_horizontalseperator"), 200, 2, 0, 
				line2yoffset + 1 + sep2yoffset + addHeight, Anchor.TOP));
		
		int line3yoffset = 40 + sepyoffset + sep2yoffset;
		
		//Add Description
		tcs.addAll(parseDescriptionTags(desc, font3, font6, 
				new Color[]{C_WHITE, C_GREEN, C_RED}, 
				0, line3yoffset + addHeight, Anchor.TOPLEFT));
		
		return tcs;
	}
	
	public static final List<Component> createStyleItemDescription(Item item) {
		Component c = null;
		Component img = null;	
		
		Color colorItem = item.getRarity().getColor();
		Color colorType = new Color(125, 125, 125, 255);
		Color colorCost = new Color(125, 175, 225, 255);
		Color colorCooldown = new Color(150, 225, 250, 255);
		UnicodeFont font1 = AssetLoader.font("font_nightserif", 14); //Item
		UnicodeFont font2 = AssetLoader.font("font_expressway", 13); //Range
		UnicodeFont font3 = AssetLoader.font("font_expressway", 12); //Cooldown//Desc
		UnicodeFont font4 = AssetLoader.font("font_coolserif", 13); //Type
		UnicodeFont font5 = AssetLoader.font("font_expressway", 13); //Cost
		
		ArrayList<Component> tcs = new ArrayList<Component>();
		
		int addHeight = 0;
		
		//Add Icon Image
		img = new ImageComponent(item.getIconImage(),	font1.getLineHeight(), font1.getLineHeight(), 0, addHeight, Anchor.TOPLEFT);
		tcs.add(img);
		
		//Add Ability Name
		c = new TextComponent(item.getName(), font1, colorItem, img.getWidth() + 2, addHeight, Anchor.TOPLEFT);
		tcs.add(c);
		addHeight += font1.getLineHeight();
		
		//Add Item Type
		if (item.getItemType() == Item.ItemType.Consumable) {
			tcs.add(new TextComponent(item.getItemType().getType() + " : " + item.getTargetType(), font4, colorType, 0, addHeight, Anchor.TOPLEFT));
			addHeight += font4.getLineHeight();
		}
		
		//Add Target Range
		if (item.getRange() > 0 && item.getItemType() != Item.ItemType.Ranged) {
			tcs.add(new TextComponent(item.getRange() + "m", font2, colorType, 0, addHeight, Anchor.TOPLEFT));
			addHeight += font2.getLineHeight();
		}
		
		//Add Mana Cost
		if (item.getCost() > 0) {
			tcs.add(new TextComponent(item.getCost() + " mana", font5, colorCost, 0, addHeight, Anchor.TOPLEFT));
			addHeight += font5.getLineHeight();
		}
		
		//Add Cooldown
		if (item.getTotalCooldown() > 0) {
			/*c = new ImageComponent(AssetLoader.image("ui_tooltip_cooldowntimer"), 
					10, 10, 0, addHeight + 3, Anchor.TOPLEFT);
			tcs.add(c);
			tcs.add(new TextComponent(item.getTotalCooldown() + "", font3, colorCooldown, c.getWidth() + 4, addHeight, Anchor.TOPLEFT));
			addHeight += Math.max(10, font3.getLineHeight());*/
			c = new TextComponent(item.getTotalCooldown() + "", font3, colorCooldown, 0, 
					(font1.getLineHeight() / 2) - (font3.getLineHeight() / 2) - 1, Anchor.TOPRIGHT);
			tcs.add(c);
			tcs.add(new ImageComponent(AssetLoader.image("ui_tooltip_cooldowntimer"), 
					10, 10, -(c.getWidth()) - 4, (font1.getLineHeight() / 2) - 5, Anchor.TOPRIGHT));
		}
		
		//Add Description
		tcs.addAll(parseDescriptionTags(item.getDescription(), font3, null, 
				new Color[]{C_WHITE, C_GREEN, C_RED, C_GREY, C_LIGHTBLUE, C_GOLD, C_LIGHTGREEN, C_LIGHTPURPLE}, 
				0, addHeight, Anchor.TOPLEFT));
		
		return tcs;
	}
	
	public static final List<Component> createStyleStatDescription(String description) {
		final UnicodeFont font1 = AssetLoader.font("font_expressway", 12);
		
		ArrayList<Component> tcs = new ArrayList<Component>();
		
		//Add Description
		tcs.addAll(parseDescriptionTags(description, font1, null, 
				new Color[]{C_LIGHTBLUE, C_GOLD, C_LIGHTGREEN, C_LIGHTPURPLE}, 
				0, 0, Anchor.TOPLEFT));
		
		return tcs;
	}
	
	public static final List<Component> createStyleBuffDebuffDescription(String description) {
		final UnicodeFont font1 = AssetLoader.font("font_expressway", 11);
		final UnicodeFont font2 = AssetLoader.font("font_expressway", 14);
		
		ArrayList<Component> tcs = new ArrayList<Component>();
		
		//Add Description
		tcs.addAll(parseDescriptionTags(description, font1, font2, 
				new Color[]{C_WHITE, C_GOLD, C_GREY, C_GREEN, C_RED}, 
				0, 0, Anchor.TOPLEFT));
		
		return tcs;
	}
	
	public static final List<Component> createStyleFixedContextDescription(String description) {
		final UnicodeFont font1 = AssetLoader.font("font_expressway", 14);
		
		ArrayList<Component> tcs = new ArrayList<Component>();
		
		//Add Description
		tcs.addAll(parseDescriptionTags(description, font1, null, 
				new Color[]{C_WHITE, C_GOLD, C_LIGHTBLUE, C_GREY, C_RED, C_DARKRED}, 
				0, 0, Anchor.TOPLEFT));
		
		return tcs;
	}
	
	/**
	 * Parses the string creating a list of TextComponents while using a different color and font for parts
	 * of the string within opening and closing LTAG and RTAG. Also starts a new line whenever a NEWLINE is found.
	 * 
	 * How to newline: (string on oldline) + NEWLINE + (string on newline)
	 * How to color: LTAG + (color# where 0 = default) + (string to color) + RTAG
	 */
	private static final List<Component> parseDescriptionTags(String description,
			UnicodeFont font1, UnicodeFont font2, Color[] colors, float xoffset, float yoffset, Anchor anchor) {
		final Color defaultColor = colors[0];
		String desc = description;
		float xoff = xoffset;
		float yoff = yoffset;
		int lineheight = font1.getLineHeight();
		int fontdiff = 0;
		if (font2 != null) fontdiff = font2.getLineHeight() - lineheight;
		int cursor = 0;
		boolean done = false;
		boolean lineBeforeHadDiff = false;
		
		ArrayList<Component> tcs = new ArrayList<Component>();
		
		while ((cursor < description.length()) && !done) {
			desc = StringUtil.substring(description, cursor, description.length(), true);
			
			int descLTI = desc.indexOf(LTAG);
			int descRTI = desc.indexOf(RTAG, descLTI + 1);
			int leftTagIndex = cursor + descLTI;
			int rightTagIndex = cursor + descRTI;
			
			//Check if we have a LTAG and matching RTAG as well as a RTAG that is after LTAG + 1 to allow for color number
			if ((leftTagIndex >= 0) && (rightTagIndex >= 0) && (rightTagIndex > leftTagIndex + 1)) {
				int newLineIndex = cursor + desc.indexOf(NEWLINE);
				
				//Check if NEWLINE, or just LTAG
				if (leftTagIndex == newLineIndex) {
					/* NEWLINE */
					//Add line using [cursor, newLineIndex)
					desc = StringUtil.substring(description, cursor, newLineIndex, true); //Substring [cursor, newLineIndex)
					if (desc.length() > 0) {
						tcs.add(new TextComponent(desc, font1, defaultColor, xoff, yoff, anchor));
					}
					//Set xoff back to start, Increment Yoffset by lineheight and put cursor at newLineIndex + NEWLINE.length
					xoff = xoffset;
					yoff += ((font2 != null) ? ((lineBeforeHadDiff ? font2.getLineHeight() : lineheight)) : lineheight);
					lineBeforeHadDiff = false;
					cursor = newLineIndex + NEWLINE.length();
				}
				else {
					/* JUST LTAG */
					//Add line using [cursor, leftTagIndex) and add desc.length to xoff
					desc = StringUtil.substring(description, cursor, leftTagIndex, true); //Substring [cursor, leftTagIndex)
					if (desc.length() > 0) {
						tcs.add(new TextComponent(desc, font1, defaultColor, xoff, yoff, anchor));
					}
					xoff += font1.getWidth(desc);
					//Add special line using (leftTagIndex + 1, rightTagIndex) and add desc.length to xoff
					String colorString = StringUtil.substring(description, leftTagIndex + 1, leftTagIndex + 2, true); //Substring(lTI, lTI + 2)
						//Check that the colorString is a valid color index, otherwise set it default
					Color color = defaultColor;
					UnicodeFont font = font1;
					if (StringUtil.isNumeric(colorString, false)) {
						int colorIndex = Integer.valueOf(colorString);
						
						if (colorIndex >= 0 && colorIndex < colors.length) {
							color = colors[colorIndex];
						}
					}
					if (font2 != null) font = font2;
						//Add the line	
					desc = StringUtil.substring(description, leftTagIndex + 2, rightTagIndex, true); //Substring (leftTagIndex + 1, rightTagIndex)
					if (desc.length() > 0) {
						tcs.add(new TextComponent(desc, font, color, xoff, yoff - ((font2 != null) ? fontdiff : 0), anchor));
						lineBeforeHadDiff = (font2 != null) ? true : false;
					}
					xoff += font.getWidth(desc);
					//Put cursor at rightTagIndex + 1
					cursor = rightTagIndex + 1;
				}
			}
			else {
				done = true;
			}
		}
		
		//Add remaining text component if there is any string remaining
		if (cursor < description.length()) {
			desc = StringUtil.substring(description, cursor, description.length(), true); //Substring [cursor, description.length)
			
			if (desc.length() > 0) {
				tcs.add(new TextComponent(desc, font1, defaultColor, xoff, yoff, anchor));
			}
		}
		
		return tcs;
	}
}
