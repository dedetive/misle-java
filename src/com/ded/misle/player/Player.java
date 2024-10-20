package com.ded.misle.player;

import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.Launcher.scale;

public class Player {

	private double x;
	private double originalPlayerX;
	private double cameraOffsetX;
	private double y;
	private double originalPlayerY;
	private double cameraOffsetY;
	private double playerSpeed;
	private double playerSpeedModifier;
	private double playerWidth;
	private double playerHeight;

	public void initializePlayer() {
		this.setX(250 * scale);
		this.setY(200 * scale);
		this.setOriginalPlayerX(250);
		this.setOriginalPlayerY(200);
		this.setCameraOffsetX(0);
		this.setCameraOffsetY(0);
		this.setPlayerSpeedModifier(1);
		System.out.println(getPlayerSpeedModifier());
		this.setPlayerWidth(tileSize);
		this.setPlayerHeight(tileSize);
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

	public double getPlayerSpeed() {
		return playerSpeed;
	}

	public double getPlayerSpeedModifier() {
		return playerSpeedModifier;
	}

	public void setPlayerSpeedModifier(double playerSpeedModifier) {
		this.playerSpeedModifier = playerSpeedModifier;
		this.playerSpeed = playerSpeedModifier * (scale * 2 + 0.166) / 3;
	}

	public double getPlayerWidth() {
		return playerWidth;
	}

	public void setPlayerWidth(double playerWidth) {
		this.playerWidth = playerWidth;
	}

	public double getPlayerHeight() {
		return playerHeight;
	}

	public void setPlayerHeight(double playerHeight) {
		this.playerHeight = playerHeight;
	}
}
