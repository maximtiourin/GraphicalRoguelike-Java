package com.fizzikgames.roguelike.asset;

import java.io.IOException;

import org.lwjgl.Sys;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;
import org.newdawn.slick.util.Log;

public class AssetShader extends Asset {
	private ShaderProgram shader;
	private String path2;
	
	public AssetShader(String id, String vertexShader, String fragmentShader) {
		super(id, vertexShader);
		this.shader = null;
		this.path2 = fragmentShader;
	}

	@Override
	public void load() throws IOException {
		if (ShaderProgram.isSupported()) {
			try {
				shader = ShaderProgram.loadProgram(path, path2);
			} catch (SlickException e) {
				Log.error("Unable to load shader (" + getId() + "):\n" + path + "\n" + path2);
				e.printStackTrace();
			}
		}
		else {
			Sys.alert("Error", "OpenGL Shaders not supported by your graphics card!");
		}
	}
	
	@Override
	public String getDescription() {
		return path + path2;
	}
	
	public ShaderProgram getShader() {
		return shader;
	}
}
