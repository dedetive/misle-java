package com.ded.misle.world.player;

import com.ded.misle.core.PhysicsEngine;
import com.ded.misle.world.boxes.HPBox;

import static com.ded.misle.core.GamePanel.tileSize;
import static com.ded.misle.world.boxes.BoxHandling.addBoxToCache;
import static com.ded.misle.world.player.PlayerAttributes.KnockbackDirection.NONE;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;

public class Player extends HPBox {

	public final PlayerKeys keys;
	public final PlayerPosition pos;
	public final PlayerAttributes attr;
	public final PlayerStats stats;
	public final Inventory inv;

	public int currentSaveSlot;
	public String name = "";

	public Player() {
		this.setTexture("invisible");
		this.setColor(defaultBoxColor);
		this.setObjectType(PhysicsEngine.ObjectType.PLAYER);
		this.setHasCollision(true);
		this.setBoxScaleHorizontal(0.91);
		this.setBoxScaleVertical(0.91);
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