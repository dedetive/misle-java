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
		player.keys.keyPressed.put("1", false);
		player.keys.keyPressed.put("2", false);
		player.keys.keyPressed.put("3", false);
		player.keys.keyPressed.put("4", false);
		player.keys.keyPressed.put("5", false);
		player.keys.keyPressed.put("6", false);
		player.keys.keyPressed.put("7", false);
	}

	int KeyPause = KeyEvent.VK_ESCAPE;
	int KeyUp = KeyEvent.VK_UP;
	int KeyDown = KeyEvent.VK_DOWN;
	int KeyLeft = KeyEvent.VK_LEFT;
	int KeyRight = KeyEvent.VK_RIGHT;
	int KeyDebug1 = KeyEvent.VK_OPEN_BRACKET;
	int KeyDebug2 = KeyEvent.VK_CLOSE_BRACKET;
	int KeyInventory = KeyEvent.VK_I;
	int Key1 = KeyEvent.VK_1;
	int Key2 = KeyEvent.VK_2;
	int Key3 = KeyEvent.VK_3;
	int Key4 = KeyEvent.VK_4;
	int Key5 = KeyEvent.VK_5;
	int Key6 = KeyEvent.VK_6;
	int Key7 = KeyEvent.VK_7;

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();

		if (code == Key1) {
			player.inv.setSelectedSlot(0);
		}
		if (code == Key2) {
			player.inv.setSelectedSlot(1);
		}
		if (code == Key3) {
			player.inv.setSelectedSlot(2);
		}
		if (code == Key4) {
			player.inv.setSelectedSlot(3);
		}
		if (code == Key5) {
			player.inv.setSelectedSlot(4);
		}
		if (code == Key6) {
			player.inv.setSelectedSlot(5);
		}
		if (code == Key7) {
			player.inv.setSelectedSlot(6);
		}
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
