package com.fizzikgames.roguelike.asset;

import java.io.IOException;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.util.Log;

/**
 * An asset that contains a Sound resource.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class AssetSound extends Asset {
	private Sound sound;
	
	public AssetSound(String id, String path) {
		super(id, path);
		sound = null;
	}
	
	@Override
	public void load() throws IOException {
		try {
			sound = new Sound(path);
		} catch (SlickException e) {
			Log.error("Unable to load sound asset: " + path, e);
		}
	}
	
	public Sound getSound() {
		return sound;
	}
}
