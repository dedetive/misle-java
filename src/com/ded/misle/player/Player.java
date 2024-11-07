package com.ded.misle.player;

public class Player {

	public final PlayerKeys keys;
	public final PlayerPosition pos;
	public final PlayerAttributes attr;
	public final PlayerStats stats;
	public final Inventory inv;

	public Player() {
		this.keys = new PlayerKeys();
		this.pos = new PlayerPosition();
		this.attr = new PlayerAttributes();
		this.stats = new PlayerStats();
		this.inv = new Inventory();
	}

	public void unloadPlayer() {
		this.keys.resetAllCooldowns();
		this.attr.unloadAttributes();
	}
}