package com.ded.misle.player;

public class PlayerPosition {

	private double x;
	private double originalPlayerX;
	private double cameraOffsetX;
	private double y;
	private double originalPlayerY;
	private double cameraOffsetY;

	public PlayerPosition(double x, double y) {
		this.x = x;
		this.y = y;
		this.originalPlayerX = x;
		this.originalPlayerY = y;
		this.cameraOffsetX = 0;
		this.cameraOffsetY = 0;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getOriginalPlayerX() {
		return originalPlayerX;
	}

	public void setOriginalPlayerX(double originalPlayerX) {
		this.originalPlayerX = originalPlayerX;
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
	}

	public double getOriginalPlayerY() {
		return originalPlayerY;
	}

	public void setOriginalPlayerY(double originalPlayerY) {
		this.originalPlayerY = originalPlayerY;
	}

	public double getCameraOffsetY() {
		return cameraOffsetY;
	}

	public void setCameraOffsetY(double cameraOffsetY) {
		this.cameraOffsetY = cameraOffsetY;
	}
}
