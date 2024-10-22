package com.ded.misle.player;

import static com.ded.misle.Launcher.scale;

public class Player {

	public final PlayerKeys keys;
	public final PlayerPosition pos;
	public final PlayerAttributes attr;
	public final PlayerStats stats;

	public Player() {
		this.keys = new PlayerKeys();
		this.pos = new PlayerPosition(250 * scale, 200 * scale);
		this.attr = new PlayerAttributes();
		this.stats = new PlayerStats();
	}
}