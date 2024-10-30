package com.ded.misle;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static com.ded.misle.GamePanel.player;

public class KeyHandler implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
		// THIS BAD
	}

	public static void initializeKeyHandler() {
		player.keys.keyPressed.put("pause", false);
		player.keys.keyPressed.put("up", false);
		player.keys.keyPressed.put("down", false);
		player.keys.keyPressed.put("left", false);
		player.keys.keyPressed.put("right", false);
		player.keys.keyPressed.put("debug1", false);
		player.keys.keyPressed.put("debug2", false);
		player.keys.keyPressed.put("inventory", false);
	}

	int KeyPause = KeyEvent.VK_ESCAPE;
	int KeyUp = KeyEvent.VK_UP;
	int KeyDown = KeyEvent.VK_DOWN;
	int KeyLeft = KeyEvent.VK_LEFT;
	int KeyRight = KeyEvent.VK_RIGHT;
	int KeyDebug1 = KeyEvent.VK_OPEN_BRACKET;
	int KeyDebug2 = KeyEvent.VK_CLOSE_BRACKET;
	int KeyInventory = KeyEvent.VK_I;

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyUp) {
			player.keys.keyPressed.put("up", true);
		}
		if (code == KeyDown) {
			player.keys.keyPressed.put("down", true);
		}
		if (code == KeyLeft) {
			player.keys.keyPressed.put("left", true);
		}
		if (code == KeyRight) {
			player.keys.keyPressed.put("right", true);
		}
		if (code == KeyDebug1) {
			handleCooldownPress("debug1");
		}
		if (code == KeyDebug2) {
			handleCooldownPress("debug2");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyPause) {
			player.keys.keyPressed.put("pause", true);
		}
		if (code == KeyUp) {
			player.keys.keyPressed.put("up", false);
		}
		if (code == KeyDown) {
			player.keys.keyPressed.put("down", false);
		}
		if (code == KeyLeft) {
			player.keys.keyPressed.put("left", false);
		}
		if (code == KeyRight) {
			player.keys.keyPressed.put("right", false);
		}
		if (code == KeyInventory) {
			player.keys.keyPressed.put("inventory", true);
		}
	}

	private void handleCooldownPress(String key) {
		long currentTime = System.currentTimeMillis();
		double cooldownEndTime = player.keys.getKeyCurrentCooldown(key);
		double cooldownDuration = player.keys.getKeyMaxCooldown(key);

		if (currentTime >= (long) cooldownEndTime && !player.keys.keyPressed.getOrDefault(key, false)) {
			player.keys.fillKeyCurrentCooldown(key);
			player.keys.keyPressed.put(key, true);
		}
	}
}
