package com.fizzikgames.roguelike.asset;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * An asset that contains a font resource.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class AssetFont extends Asset {
	private Font baseFont;
	
	public AssetFont(String id, String path) {
		super(id, path);
		baseFont = null;
	}
	
	@Override
	public void load() throws IOException {
		try {
			InputStream inputStream = ResourceLoader.getResourceAsStream(path);
			
			baseFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);			
		} catch (FontFormatException e) {
			Log.error("Unable to load font asset: " + path, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public UnicodeFont getFont(float size) {
		if (baseFont != null) {
			try {
				UnicodeFont unifont = new UnicodeFont(baseFont.deriveFont(size));
				unifont.addAsciiGlyphs();
				unifont.addGlyphs(400, 600);
				unifont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
				unifont.loadGlyphs();
				
				return unifont;
			} catch (SlickException e) {
				Log.error("Unable to load glyphs for font", e);
			}
		}
		return null;
	}
}
