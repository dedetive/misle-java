package com.ded.misle.player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.Launcher.scale;

public class PlayerPosition {

	private double x;
	private double originalPlayerX;
	private double cameraOffsetX;
	private double y;
	private double originalPlayerY;
	private double cameraOffsetY;
	private int spawnpointRoom;
	private Region region = Region.VOID;
	private int roomID;
	private double rotation;
	public enum Region {
		VOID,
		CHAIN_OF_LIES
	}

	public PlayerPosition() {
		if (player != null) {
			if (spawnpointRoom == 0) {
				spawnpointRoom = 1;
				reloadSpawnpoint();
			} else {
				reloadSpawnpoint();
			}
		}
		setCameraOffsetX(0);
		setCameraOffsetY(0);
		setRegion(Region.CHAIN_OF_LIES);
	}

	public double getCameraOffsetX() {
		return cameraOffsetX;
	}

	public void setCameraOffsetX(double cameraOffsetX) {
		this.cameraOffsetX = cameraOffsetX;
	}

	public double getCameraOffsetY() {
		return cameraOffsetY;
	}

	public void setCameraOffsetY(double cameraOffsetY) {
		this.cameraOffsetY = cameraOffsetY;
	}

	public int getSpawnpoint() {
		return spawnpointRoom;
	}

	public void setSpawnpoint(int roomID) {
		this.spawnpointRoom = roomID;
	}

	public void reloadSpawnpoint() {
		setRoomID(spawnpointRoom);
		int[] spawnpointCoordinates = switch (spawnpointRoom) {

			// These should be the positions the spawnpoints are located

			case 1 -> new int[]{500, 540};
			default -> new int[]{0, 0};
		};
		player.setX(spawnpointCoordinates[0] * scale);
		player.setY(spawnpointCoordinates[1] * scale);
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
