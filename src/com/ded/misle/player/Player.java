package com.ded.misle.player;

import com.ded.misle.PhysicsEngine;
import com.ded.misle.boxes.Box;

import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.boxes.BoxHandling.addBoxToCache;
import static com.ded.misle.player.PlayerAttributes.KnockbackDirection.NONE;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;

public class Player extends Box {

	public final PlayerKeys keys;
	public final PlayerPosition pos;
	public final PlayerAttributes attr;
	public final PlayerStats stats;
	public final Inventory inv;

	public Player() {
		this.setTexture("solid");
		this.setColor(defaultBoxColor);
		this.setObjectType(PhysicsEngine.ObjectType.PLAYER);
		this.setHasCollision(true);
		this.setBoxScaleHorizontal(tileSize * 0.91);
		this.setBoxScaleVertical(tileSize * 0.91);
		this.setKnockbackDirection(NONE);
		this.setEffect(new String[]{""});

		this.inv = new Inventory();
		this.keys = new PlayerKeys();
		this.pos = new PlayerPosition();
		this.attr = new PlayerAttributes();
		this.stats = new PlayerStats();
		addBoxToCache(this);
	}

	public void unloadPlayer() {
		this.keys.resetAllCooldowns();
		this.attr.unloadAttributes();
		this.inv.destroyGrabbedItem();
		this.inv.destroyTempItem();
		this.pos.setSpawnpoint(2);
	}
}