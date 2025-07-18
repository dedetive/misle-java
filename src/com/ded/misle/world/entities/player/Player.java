package com.ded.misle.world.entities.player;

import com.ded.misle.game.GamePanel;
import com.ded.misle.net.NetClient;
import com.ded.misle.renderer.AnimatedStepCounter;
import com.ded.misle.world.data.Difficulty;
import com.ded.misle.world.data.PersistentUUIDTimerData;
import com.ded.misle.world.logic.PhysicsEngine;
import com.ded.misle.world.entities.Entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;

import static com.ded.misle.game.GamePanel.isRunning;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;

public class Player extends Entity<Player> {

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

	private Difficulty difficulty = Difficulty.MEDIUM;

	long lastSendTime;
	private List<NetClient.Player> onlinePlayerList = new ArrayList<>();
	private byte[] uuid = new byte[16];

	private PersistentUUIDTimerData uuidData;

	private boolean waiting;

	public Player() {
		this.setTexture("invisible");
		this.setColor(defaultBoxColor);
		this.setObjectType(PhysicsEngine.ObjectType.PLAYER);
		this.setCollision(true);
		this.setVisualScaleHorizontal(0.91);
		this.setVisualScaleVertical(0.91);
		this.effect = null;

		this.inv = new Inventory();
		this.pos = new PlayerPosition();
		this.attr = new PlayerAttributes();
		this.stats = new PlayerStats();

		this.startNetThread();
	}

	public void unloadPlayer() {
		this.invalidateUUID();
		this.attr.unloadAttributes();
		this.inv.destroyGrabbedItem();
		this.inv.destroyTempItem();
		this.pos.setSpawnpoint(1);
		this.getPlanner().killExecution();
	}

	@SuppressWarnings("unchecked")
	public Player setWaiting(boolean waiting) {
		this.waiting = waiting;
		return this;
	}

	public boolean isWaiting() {
		return waiting;
	}

	/**
	 * When used, player unleashes an attack immediately, but only if held item is a valid weapon.<p>
	 * A valid weapon is defined as having "weapon" as item type.
	 */
	public void attack() {
		attack(1f);
	}

	public void attack(float intensity) {
		if (this.inv.getSelectedItem() == null) {
			this.inv.useWeapon(intensity);
			return;
		}
		String heldItemType = this.inv.getSelectedItem().getType();

		this.inv.useWeapon(intensity);
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

	private void startNetThread() {
		Thread netThread = new Thread(() -> {
			int check = 500;

			while (isRunning())
				if (System.currentTimeMillis() - lastSendTime >= check) {
					if (NetClient.isServerOnline()) {
						/* TODO: please for the sake of Erosius change this ugly check asap */
						if (GamePanel.gameState == GamePanel.GameState.PLAYING ||
								GamePanel.gameState == GamePanel.GameState.INVENTORY ||
								GamePanel.gameState == GamePanel.GameState.FROZEN_PLAYING ||
								GamePanel.gameState == GamePanel.GameState.DIALOG ||
								GamePanel.gameState == GamePanel.GameState.PAUSE_MENU
						) {
							String uuid = getUUIDString();
							int heldItemID = inv.getSelectedItem() == null ? 0 : inv.getSelectedItem().getId();
							NetClient.sendPosition(uuid, name, getX(), getY(), this.pos.getRoomID(), icon, heldItemID);
							onlinePlayerList = NetClient.fetchOnlinePlayers(uuid);
						}
						check = 500;
					} else {
						if (!onlinePlayerList.isEmpty()) onlinePlayerList = new ArrayList<>();
						check = 3000;
					}
					lastSendTime = System.currentTimeMillis();
				}
		});
		netThread.start();
	}

	public List<NetClient.Player> getOnlinePlayerList() {
		return onlinePlayerList;
	}

	public void setUUID(byte[] uuid) {
		if (!Arrays.equals(this.uuid, new byte[16])) return;

		this.uuid = uuid;
		ensureUUIDDataLoaded();
	}

	public void invalidateUUID() {
		this.uuid = new byte[16];
	}

	public byte[] generateUUID() {
		UUID uuid = UUID.randomUUID();

		ByteBuffer bb = ByteBuffer.allocate(16);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());

		return bb.array();
	}

	public byte[] getUUIDBytes() {
		if (Arrays.equals(uuid, new byte[16])) {
			this.uuid = generateUUID();
		}

		return this.uuid;
	}

	public String getUUIDString() {
		ByteBuffer bb = ByteBuffer.wrap(this.getUUIDBytes());
		long high = bb.getLong();
		long low = bb.getLong();
		UUID uuid = new UUID(high, low);
		return uuid.toString();
	}

	public UUID getUUID() {
		ByteBuffer bb = ByteBuffer.wrap(this.getUUIDBytes());
		long high = bb.getLong();
		long low = bb.getLong();
		return new UUID(high, low);
	}

	private void ensureUUIDDataLoaded() {
		if (uuidData == null || !uuidData.uuid.equals(this.getUUID())) {
			uuidData = new PersistentUUIDTimerData(getUUID());
			uuidData.load();
		}
	}

	public void storeTimerInUUID(String id, int turns) {
		ensureUUIDDataLoaded();
		this.uuidData.setTurns(id, turns);
	}

	public void removeTimerFromUUID(String id) {
		ensureUUIDDataLoaded();
		this.uuidData.remove(id);
	}

	public int loadTimerFromUUID(String id) {
		ensureUUIDDataLoaded();
		return this.uuidData.getTurns(id);
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	@SuppressWarnings("unchecked")
	public Player setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
		return this;
	}
}