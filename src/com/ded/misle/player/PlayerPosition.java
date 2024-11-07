package com.ded.misle.player;

import java.util.Arrays;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.Launcher.scale;

public class PlayerPosition {

	private double x;
	private double originalPlayerX;
	private double cameraOffsetX;
	private double y;
	private double originalPlayerY;
	private double cameraOffsetY;
	private double[] spawnpoint = new double[2];
	private String region = "";

	public PlayerPosition() {
		if (!Arrays.equals(getSpawnpoint(), new double[]{0, 0})) {
			reloadSpawnpoint();
		} else {
			setX(325);
			setY(325);
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

	public double[] getSpawnpoint() {
		return spawnpoint;
	}

	public void setSpawnpoint(double spawnpointX, double spawnpointY) {
		this.spawnpoint = new double[]{spawnpointX, spawnpointY};
	}

	public void reloadSpawnpoint() {
		if (spawnpoint != null && !Arrays.equals(spawnpoint, new double[]{0, 0})) {
			setX(spawnpoint[0] * scale);
			setY(spawnpoint[1] * scale);
		}
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
}
