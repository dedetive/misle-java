package com.ded.misle.player;

import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.Launcher.scale;

public class Player {

	public final PlayerKeys keys;
	public final PlayerPosition pos;
	public final PlayerAttributes attr;


	public Player() {
		this.keys = new PlayerKeys();
		this.pos = new PlayerPosition(250 * scale, 200 * scale);
		this.attr = new PlayerAttributes();
	}

	public PlayerPosition getPosition() {
		return pos;
	}

	public PlayerKeys getKeys() {
		return keys;
	}


}
