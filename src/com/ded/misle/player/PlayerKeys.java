package com.ded.misle.player;

import java.util.HashMap;

public class PlayerKeys {

	public HashMap<String, Boolean> keyPressed;
	private HashMap<String, Double> keyMaxCooldown = new HashMap<>();
	private HashMap<String, Double> keyCurrentCooldown = new HashMap<>();

	public PlayerKeys() {
		this.keyPressed = new HashMap<>();
		this.setKeyMaxCooldown("debug1", 150);
		this.setKeyMaxCooldown("debug2", 150);
		this.setKeyMaxCooldown("dodge", 1000);
		this.setKeyMaxCooldown("use", 100);
	}

	public HashMap<String, Boolean> getKeyPressed() {
		return keyPressed;
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
