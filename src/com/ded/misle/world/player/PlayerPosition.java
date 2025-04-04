package com.ded.misle.world.player;

import com.ded.misle.world.World;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.*;

public class PlayerPosition {

    private double cameraOffsetX;
    private double cameraOffsetY;
	private int spawnpointRoom;
	private Region region = Region.VOID;
	private int roomID;
	private double rotation;
	public World world;
	public enum Region {
		VOID,
		CHAIN_OF_LIES
	}

	public PlayerPosition() {
		if (player != null) {
            spawnpointRoom = 1;
            reloadSpawnpoint();
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

	public double calculateCameraOffsetX() {
		return Math.clamp(0, player.getX() * tileSize - screenWidth / 2 + player.getBoxScaleHorizontal() / 2, worldWidth - screenWidth);
	}
	public double calculateCameraOffsetY() {
		return Math.clamp(0, player.getY() * tileSize - screenWidth / 2 + player.getBoxScaleVertical() / 2, worldHeight - screenHeight);
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

			case 1 -> new int[]{25, 27};
			case 2 -> new int[]{13, 2};
			default -> new int[]{0, 0};
		};
		player.setX(spawnpointCoordinates[0]);
		player.setY(spawnpointCoordinates[1]);
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
