package com.fizzikgames.roguelike.entity;

public interface PlayerTargetListener {
	public void targetSelected(GameCharacter target, int r, int c);
}
