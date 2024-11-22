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
	private int spawnpointRoom = 1;
	private String region = "";
	private int roomID;
	private double rotation;

	public PlayerPosition() {
		if (spawnpointRoom != 0) {
			reloadSpawnpoint();
		} else {
			setRoomID(1);
		}
		setCameraOffsetX(0);
		setCameraOffsetY(0);
		setRegion("Chain of Lies");
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
		this.originalPlayerX = x / scale;
	}

	public double getOriginalPlayerX() {
		return originalPlayerX;
	}

	public double getCameraOffsetX() {
		return cameraOffsetX;
	}

	public void setCameraOffsetX(double cameraOffsetX) {
		this.cameraOffsetX = cameraOffsetX;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		this.originalPlayerY = y / scale;
	}

	public double getOriginalPlayerY() {
		return originalPlayerY;
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
//		setX(spawnpoint[0] * scale);
//		setY(spawnpoint[1] * scale);
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
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
