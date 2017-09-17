package com.fizzikgames.roguelike.asset;

import java.io.IOException;

import org.newdawn.slick.loading.DeferredResource;

/**
 * An asset is a resource to be loaded by the Assetloader
 * @author Maxim Tiourin
 * @version 1.00
 */
public abstract class Asset implements DeferredResource {
	protected String id;
	protected String path;
	
	public Asset(String id, String path) {
		this.id = id;
		this.path = path;
		AssetLoader.getLoadingList().add(this);
	}
	
	public String getDescription() {
		return path;
	}
	
	public abstract void load() throws IOException;
	
	public String getId() {
		return id;
	}
}
