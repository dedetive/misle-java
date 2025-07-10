package com.ded.misle.world.entities.player;

import com.ded.misle.renderer.smoother.SyncedValue;
import com.ded.misle.renderer.smoother.ValueModifier;
import com.ded.misle.renderer.smoother.modifiers.BounceModifier;
import com.ded.misle.renderer.smoother.modifiers.ShakeModifier;
import com.ded.misle.world.logic.RoomManager;
import com.ded.misle.world.logic.World;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.ded.misle.core.Setting.screenShake;
import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.world.data.Direction.RIGHT;
import static com.ded.misle.world.data.Direction.UP;

public class PlayerPosition {

    private SyncedValue cameraOffsetX;
    private SyncedValue cameraOffsetY;
	private int spawnpointRoom = 1;
	private Region region = Region.VOID;
	private int roomID;
	private double rotation;
	public World world;

	public enum Region {
		VOID,
		CHAIN_OF_LIES
	}

	public PlayerPosition() {
		this.cameraOffsetX = new SyncedValue(0);
		this.cameraOffsetY = new SyncedValue(0);
		setRegion(Region.CHAIN_OF_LIES);
	}

	public float getCameraOffsetX() {
		return cameraOffsetX.getVisual();
	}

	public void setCameraOffsetX(float cameraOffsetX) {
		this.cameraOffsetX.set(cameraOffsetX);
	}

	public void invalidateCameraOffsetX() {
		this.cameraOffsetX.invalidateVisual();
	}

	public float getCameraOffsetY() {
		return cameraOffsetY.getVisual();
	}

	public void setCameraOffsetY(float cameraOffsetY) {
		this.cameraOffsetY.set(cameraOffsetY);
	}

	public void invalidateCameraOffsetY() {
		this.cameraOffsetY.invalidateVisual();
	}

	public void addCameraEffect(ValueModifier... modifier) {
		this.cameraOffsetX.addModifier(modifier);
		this.cameraOffsetY.addModifier(modifier);
	}

	public void shakeScreen(float intensity) {
		if (!screenShake.bool()) return;

		addCameraEffect(
			new ShakeModifier(
				1.25f * intensity,
				0.1f
			),
			new BounceModifier(3f * intensity, 0.5f, 8f)
		);
	}

	private static final double MOUSE_INFLUENCE_X = 1.0 / 3.0;
	private static final double MOUSE_INFLUENCE_Y = 1.0 / 2.0;

	private double lastCameraOffsetX;
	public double calculateCameraOffsetX() {
		Planner planner = player.getPlanner();
		int targetX = planner.isPlanning() && !planner.isExecuting()
			? planner.getEnd().x
			: player.getX();

		double result = Math.clamp((double) mouseHandler.getDistanceFromScreenCenter().x * MOUSE_INFLUENCE_X + targetX * originalTileSize - (double) originalScreenWidth / 2,
			- originalTileSize,
			Math.max(originalWorldWidth - originalScreenWidth + originalTileSize, 0));

		if (Math.abs(lastCameraOffsetX - result) > originalTileSize * 2 + Math.abs((double) mouseHandler.getDistanceFromScreenCenter().x * MOUSE_INFLUENCE_X)) {
			lastCameraOffsetX = result;
			result = Integer.MIN_VALUE;
		} else lastCameraOffsetX = result;

		return result;
	}
	public double calculateCameraOffsetY() {
		Planner planner = player.getPlanner();
		int targetY = planner.isPlanning() && !planner.isExecuting()
			? planner.getEnd().y
			: player.getY();

		return Math.clamp((double) mouseHandler.getDistanceFromScreenCenter().y * MOUSE_INFLUENCE_Y + targetY * originalTileSize - (double) originalScreenHeight / 2,
			(double) - originalTileSize / 2,
			originalWorldHeight - originalScreenHeight + (double) originalTileSize / 2);
	}

	public void updateCameraOffset(float speed) {
		this.cameraOffsetX.update(speed);
		this.cameraOffsetY.update(speed);
	}

	public int getSpawnpoint() {
		return spawnpointRoom;
	}

	public void setSpawnpoint(int roomID) {
		this.spawnpointRoom = roomID;
	}

	public void reloadSpawnpoint() {
		setRoomID(spawnpointRoom);
		RoomManager.Room room = RoomManager.roomIDToName(spawnpointRoom);
		if (room == null) room = RoomManager.roomIDToName(1);
        assert room != null;

		int[] spawnpointCoordinates = room.spawnpointPos;
		player.setPos(spawnpointCoordinates[0], spawnpointCoordinates[1]);
		lastCameraOffsetX = Integer.MIN_VALUE;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public void delayedRotate(double angle, double delay) {
		int frames = (int)(delay / 1000 * 60);
		double dangle = angle / frames;
		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					player.pos.setRotation(player.pos.getRotation() + dangle);
					count++;
				}
			}
		});
		timer.start();
	}


}
