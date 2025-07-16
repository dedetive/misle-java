package com.ded.misle.world.entities.player;

import com.ded.misle.input.KeyDep;

import static com.ded.misle.input.KeyDep.*;

import java.util.HashMap;

public class PlayerKeys {

	public HashMap<KeyDep, Boolean> keyPressed;
	private HashMap<KeyDep, Double> keyMaxCooldown = new HashMap<>();
	private HashMap<KeyDep, Double> keyCurrentCooldown = new HashMap<>();

	public PlayerKeys() {
		this.keyPressed = new HashMap<>();
		this.setKeyMaxCooldown(DEBUG1, 150);
		this.setKeyMaxCooldown(DEBUG2, 150);
		this.setKeyMaxCooldown(BACKSPACE, 75);
		this.setKeyMaxCooldown(DODGE, 1000);
		this.setKeyMaxCooldown(USE, 100);
		this.setKeyMaxCooldown(RIGHT_MENU, 150);
		this.setKeyMaxCooldown(LEFT_MENU, 150);

	}

	public HashMap<KeyDep, Boolean> getKeyPressed() {
		return keyPressed;
	}

	public double getKeyMaxCooldown(KeyDep key) {
		try {
			return Math.max(keyMaxCooldown.get(key), 0);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void setKeyMaxCooldown(KeyDep key, double cooldownMS) {
		this.keyMaxCooldown.put(key, Math.max(cooldownMS, 0));
	}

	public double getKeyCurrentCooldown(KeyDep key) {
		try {
			return Math.max(keyCurrentCooldown.get(key), 0);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void setKeyCurrentCooldown(KeyDep key, double cooldownMS) {
		this.keyCurrentCooldown.put(key, Math.max(cooldownMS, 0));
	}

	public void fillKeyCurrentCooldown(KeyDep key) {
		this.keyCurrentCooldown.put(key, System.currentTimeMillis() + getKeyMaxCooldown(key));
	}

	public void resetAllCooldowns() {
		this.keyCurrentCooldown.replaceAll((k, v) -> v = 0.0);
	}
}
