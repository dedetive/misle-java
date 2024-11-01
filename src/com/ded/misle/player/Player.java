package com.ded.misle.player;

import static com.ded.misle.items.Item.createItem;

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

		this.inv.addItem(createItem(0), 0, 0);
		this.inv.addItem(createItem(1, 3), 0, 1);
		this.inv.addItem(createItem(2, 5), 0, 6);
		this.inv.addItem(createItem(2, 48));
	}

	public void unloadPlayer() {
		this.keys.resetAllCooldowns();
		this.attr.unloadAttributes();
	}
}