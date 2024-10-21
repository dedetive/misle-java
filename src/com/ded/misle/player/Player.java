package com.ded.misle.player;

import com.ded.misle.KeyHandler;

import java.awt.*;
import java.util.HashMap;

import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.Launcher.scale;

public class Player {

	public HashMap<String, Boolean> keyPressed;
	private HashMap<String, Double> keyMaxCooldown = new HashMap<>();
	public HashMap<String, Double> keyCurrentCooldown = new HashMap<>();
	private double x;
	private double originalPlayerX;
	private double cameraOffsetX;
	private double y;
	private double originalPlayerY;
	private double cameraOffsetY;
	private double playerSpeed;
	private double playerSpeedModifier;
	private double width;
	private double height;
	private double HP;
	private double maxHP;

	public Player() {
		this.keyPressed = new HashMap<>();
		this.setX(250 * scale);
		this.setY(200 * scale);
		this.setOriginalPlayerX(getX() / scale);
		this.setOriginalPlayerY(getY() / scale);
		this.setCameraOffsetX(0);
		this.setCameraOffsetY(0);
		this.setPlayerSpeedModifier(1);
		this.setPlayerWidth(tileSize);
		this.setPlayerHeight(tileSize);
		this.setPlayerMaxHP(100);
		this.setPlayerHP(getPlayerMaxHP());
		this.setKeyMaxCooldown("debug1", 150);
		this.setKeyMaxCooldown("debug2", 150);
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
		return width;
	}

	public void setPlayerWidth(double playerWidth) {
		this.width = playerWidth;
	}

	public double getPlayerHeight() {
		return height;
	}

	public void setPlayerHeight(double playerHeight) {
		this.height = playerHeight;
	}

	public double getPlayerHP() {
		return HP;
	}

	public void setPlayerHP(double HP) {
		this.HP = HP;
	}

	public double getPlayerMaxHP() {
		return maxHP;
	}

	public void setPlayerMaxHP(double maxHP) {
		this.maxHP = maxHP;
	}

	public double getKeyMaxCooldown(String key) {
		try {
			return Math.max(keyMaxCooldown.get(key), 0);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void setKeyMaxCooldown(String key, double cooldownMS) {
		this.keyMaxCooldown.put(key, Math.max(cooldownMS, 0));
	}

	public double getKeyCurrentCooldown(String key) {
		try {
			return Math.max(keyCurrentCooldown.get(key), 0);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void setKeyCurrentCooldown(String key, double cooldownMS) {
		this.keyCurrentCooldown.put(key, Math.max(cooldownMS, 0));
	}

	public void fillKeyCurrentCooldown(String key) {
		this.keyCurrentCooldown.put(key, System.currentTimeMillis() + getKeyMaxCooldown(key));
	}

	public void resetAllCooldowns() {
		this.keyCurrentCooldown.replaceAll((k, v) -> v = 0.0);
	}
}
