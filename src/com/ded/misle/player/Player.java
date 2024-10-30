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

		Item exampleSword = new Item("Sword", 1, "Weapon");
		Item examplePotion = new Item("Potion", 3, "Consumable");
		Item exampleShield = new Item("Shield", 1, "Shield");

		this.inv.addItem(exampleSword, 0, 0);
		this.inv.addItem(examplePotion, 0, 1);
		this.inv.addItem(exampleShield);
	}

	public void unloadPlayer() {
		this.keys.resetAllCooldowns();
		this.attr.unloadAttributes();
	}
}