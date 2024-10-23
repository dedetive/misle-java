package com.ded.misle.player;

import static com.ded.misle.Launcher.scale;

public class PlayerPosition {

	private double x;
	private double originalPlayerX;
	private double cameraOffsetX;
	private double y;
	private double originalPlayerY;
	private double cameraOffsetY;

	public PlayerPosition(double x, double y) {
		setX(x);
		setY(y);
		setCameraOffsetX(0);
		setCameraOffsetY(0);
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
}
