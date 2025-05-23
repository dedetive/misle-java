package com.ded.misle.world.entities.player;

import com.ded.misle.world.logic.PhysicsEngine;
import com.ded.misle.world.entities.HPBox;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.ded.misle.world.boxes.BoxHandling.addBoxToCache;
import static com.ded.misle.world.entities.player.PlayerAttributes.KnockbackDirection.NONE;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;

public class Player extends HPBox {

	public final PlayerKeys keys;
	public final PlayerPosition pos;
	public final PlayerAttributes attr;
	public final PlayerStats stats;
	public final Inventory inv;
	private PlayerPlanner planner;

	public int currentSaveSlot;
	public String name = "";
	public BufferedImage icon;
	public boolean isIconActive;
	public boolean isIconTexture;

	public Player() {
		this.setTexture("invisible");
		this.setColor(defaultBoxColor);
		this.setObjectType(PhysicsEngine.ObjectType.PLAYER);
		this.setCollision(true);
		this.setVisualScaleHorizontal(0.91);
		this.setVisualScaleVertical(0.91);
		this.setKnockbackDirection(NONE);
		this.effect = null;

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
		this.pos.setSpawnpoint(1);
	}

	/**
	 * Returns the current PlayerPlanner instance.
	 * If no planner exists yet, a new one is created.
	 * This method is used to access or modify the player's planning state.
	 *
	 * @return the active PlayerPlanner instance
	 */
	public PlayerPlanner getPlanner() {
		return planner =
			planner != null
				? planner
				: new PlayerPlanner(new Point(getX(), getY()));
	}

	/**
	 * Creates a new PlayerPlanner, replacing any existing planner.
	 * This is useful when starting a fresh planning session.
	 *
	 * @return the newly created PlayerPlanner instance
	 */
	public PlayerPlanner getNewPlanner() {
		return planner = new PlayerPlanner(new Point(getX(), getY()));
	}

	/**
	 * Retrieves the list of points that were added during the current planning session.
	 *
	 * @return an array of Points representing the current planned path
	 */
	public Point[] getPlannerState() {
		return getPlanner().getPoints();
	}
}