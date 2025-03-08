package com.ded.misle.world.player;

import com.ded.misle.input.KeyHandler.Key;
import static com.ded.misle.input.KeyHandler.Key.*;
import java.util.HashMap;

public class PlayerKeys {

	public HashMap<Key, Boolean> keyPressed;
	private HashMap<Key, Double> keyMaxCooldown = new HashMap<>();
	private HashMap<Key, Double> keyCurrentCooldown = new HashMap<>();

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

	public HashMap<Key, Boolean> getKeyPressed() {
		return keyPressed;
	}

	public double getKeyMaxCooldown(Key key) {
		try {
			return Math.max(keyMaxCooldown.get(key), 0);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void setKeyMaxCooldown(Key key, double cooldownMS) {
		this.keyMaxCooldown.put(key, Math.max(cooldownMS, 0));
	}

	public double getKeyCurrentCooldown(Key key) {
		try {
			return Math.max(keyCurrentCooldown.get(key), 0);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void setKeyCurrentCooldown(Key key, double cooldownMS) {
		this.keyCurrentCooldown.put(key, Math.max(cooldownMS, 0));
	}

	public void fillKeyCurrentCooldown(Key key) {
		this.keyCurrentCooldown.put(key, System.currentTimeMillis() + getKeyMaxCooldown(key));
	}

	public void resetAllCooldowns() {
		this.keyCurrentCooldown.replaceAll((k, v) -> v = 0.0);
	}
}
