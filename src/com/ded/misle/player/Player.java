package com.ded.misle.player;

import com.ded.misle.items.Item;

import static com.ded.misle.items.Item.getItem;

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

		this.inv.addItem(getItem(0), 0, 0);
		this.inv.addItem(getItem(1), 0, 5);
		this.inv.addItem(getItem(2, 5));
	}

	public void unloadPlayer() {
		this.keys.resetAllCooldowns();
		this.attr.unloadAttributes();
	}
}