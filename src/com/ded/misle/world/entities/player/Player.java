package com.ded.misle.world.entities.player;

import com.ded.misle.net.NetClient;
import com.ded.misle.renderer.AnimatedStepCounter;
import com.ded.misle.world.logic.PhysicsEngine;
import com.ded.misle.world.entities.Entity;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.ded.misle.world.boxes.BoxHandling.addBoxToCache;
import static com.ded.misle.world.entities.player.PlayerAttributes.KnockbackDirection.NONE;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;

public class Player extends Entity {

	public final PlayerKeys keys;
	public final PlayerPosition pos;
	public final PlayerAttributes attr;
	public final PlayerStats stats;
	public final Inventory inv;
	private Planner planner;
	public HandItemAnimator animator = new HandItemAnimator();
	public final AnimatedStepCounter stepCounter = new AnimatedStepCounter();

	public int currentSaveSlot;
	public String name = "";
	public BufferedImage icon;
	public boolean isIconActive;
	public boolean isIconTexture;

	long lastSendTime;

	private boolean waiting;

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

		Thread netThread = new Thread(() -> {
			while (isRunning())
				if (System.currentTimeMillis() - lastSendTime >= 500) {
					NetClient.sendPosition(name, getX(), getY(), this.pos.getRoomID());
					lastSendTime = System.currentTimeMillis();
				}
		});
		netThread.start();
	}

	public void unloadPlayer() {
		this.keys.resetAllCooldowns();
		this.attr.unloadAttributes();
		this.inv.destroyGrabbedItem();
		this.inv.destroyTempItem();
		this.pos.setSpawnpoint(1);
		this.getPlanner().killExecution();
	}

	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}

	public boolean isWaiting() {
		return waiting;
	}

	/**
	 * When used, player unleashes an attack immediately, but only if held item is a valid weapon.<p>
	 * A valid weapon is defined as having "weapon" as item type.
	 */
	public void attack() {
		String heldItemType = this.inv.getSelectedItem().getType();

		if (heldItemType.equals("weapon")) {
			this.inv.useWeapon(System.currentTimeMillis());
		}
	}

	/**
	 * Returns the current Planner instance.
	 * If no planner exists yet, a new one is created.
	 * This method is used to access or modify the player's planning state.
	 *
	 * @return the active Planner instance
	 */
	public Planner getPlanner() {
		return planner =
			planner != null
				? planner
				: new Planner(new Point(getX(), getY()));
	}

	/**
	 * Creates a new Planner, replacing any existing planner.
	 * This is useful when starting a fresh planning session.
	 *
	 * @return the newly created Planner instance
	 */
	public Planner getNewPlanner() {
		return planner = new Planner(new Point(getX(), getY()));
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