package com.ded.misle;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.GameRenderer.pauseGame;
import static com.ded.misle.items.Item.createDroppedItem;
import static com.ded.misle.items.Item.createItem;

public class KeyHandler implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
		// THIS BAD
	}

	public KeyHandler() {
		player.keys.keyPressed.put("pause", false);
		player.keys.keyPressed.put("up", false);
		player.keys.keyPressed.put("down", false);
		player.keys.keyPressed.put("left", false);
		player.keys.keyPressed.put("right", false);
		player.keys.keyPressed.put("debug1", false);
		player.keys.keyPressed.put("debug2", false);
		player.keys.keyPressed.put("inventory", false);
		player.keys.keyPressed.put("drop", false);
		player.keys.keyPressed.put("ctrl", false);
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
	int KeyDrop = KeyEvent.VK_Q;
	int KeyCtrl = KeyEvent.VK_CONTROL;
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
			player.keys.keyPressed.put("1", true);
		}
		if (code == Key2) {
			player.keys.keyPressed.put("2", true);
		}
		if (code == Key3) {
			player.keys.keyPressed.put("3", true);
		}
		if (code == Key4) {
			player.keys.keyPressed.put("4", true);
		}
		if (code == Key5) {
			player.keys.keyPressed.put("5", true);
		}
		if (code == Key6) {
			player.keys.keyPressed.put("6", true);
		}
		if (code == Key7) {
			player.keys.keyPressed.put("7", true);
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
		if (code == KeyCtrl) {
			player.keys.keyPressed.put("ctrl", true);
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
		if (code == Key1) {
			player.keys.keyPressed.put("1", false);
		}
		if (code == Key2) {
			player.keys.keyPressed.put("2", false);
		}
		if (code == Key3) {
			player.keys.keyPressed.put("3", false);
		}
		if (code == Key4) {
			player.keys.keyPressed.put("4", false);
		}
		if (code == Key5) {
			player.keys.keyPressed.put("5", false);
		}
		if (code == Key6) {
			player.keys.keyPressed.put("6", false);
		}
		if (code == Key7) {
			player.keys.keyPressed.put("7", false);
		}
		if (code == KeyUp) {
			player.keys.keyPressed.put("up", true);
		}
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
		if (code == KeyDrop) {
			player.keys.keyPressed.put("drop", true);
		}
		if (code == KeyCtrl) {
			player.keys.keyPressed.put("ctrl", false);
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

	public void updateKeys() {

		// PLAYING EXCLUSIVE

		if (gameState == GameState.PLAYING) {
			if (player.keys.keyPressed.get("1")) {
				player.inv.setSelectedSlot(0);
			}
			if (player.keys.keyPressed.get("2")) {
				player.inv.setSelectedSlot(1);
			}
			if (player.keys.keyPressed.get("3")) {
				player.inv.setSelectedSlot(2);
			}
			if (player.keys.keyPressed.get("4")) {
				player.inv.setSelectedSlot(3);
			}
			if (player.keys.keyPressed.get("5")) {
				player.inv.setSelectedSlot(4);
			}
			if (player.keys.keyPressed.get("6")) {
				player.inv.setSelectedSlot(5);
			}
			if (player.keys.keyPressed.get("7")) {
				player.inv.setSelectedSlot(6);
			}
			if (player.keys.keyPressed.get("pause")) {
				pauseGame();
				player.keys.keyPressed.put("pause", false);
			}
			double[] willMovePlayer = {0, 0};
			if (player.keys.keyPressed.get("up")) {
				if (!player.keys.keyPressed.get("left") || !player.keys.keyPressed.get("right")) {
					willMovePlayer[1] -= player.attr.getPlayerSpeed();
				} else {
					willMovePlayer[1] -= (player.attr.getPlayerSpeed() * Math.sqrt(2) / 3);
				}
			}
			if (player.keys.keyPressed.get("down")) {
				if (!player.keys.keyPressed.get("left") || !player.keys.keyPressed.get("right")) {
					willMovePlayer[1] += player.attr.getPlayerSpeed();
				} else {
					willMovePlayer[1] += player.attr.getPlayerSpeed() * Math.sqrt(2) / 3;
				}
			}
			if (player.keys.keyPressed.get("left")) {
				if (!player.keys.keyPressed.get("up") || !player.keys.keyPressed.get("down")) {
					willMovePlayer[0] -= player.attr.getPlayerSpeed();
				} else {
					willMovePlayer[0] -= player.attr.getPlayerSpeed() * Math.sqrt(2) / 3;
				}
			}
			if (player.keys.keyPressed.get("right")) {
				willMovePlayer[0] += player.attr.getPlayerSpeed();
			}
			if (player.keys.keyPressed.get("drop")) {
				if (player.inv.hasHeldItem()) {
					if (player.keys.keyPressed.get("ctrl")) {
						player.inv.dropItem(0, player.inv.getSelectedSlot(), player.inv.getSelectedItem().getCount());
					} else {
						player.inv.dropItem(0, player.inv.getSelectedSlot(), 1);
					}
					player.keys.keyPressed.put("drop", false);
				}
			}

			// MOVING

			if (!player.attr.isDead()) {
				double range = (tileSize + 1) * Math.max(1, player.attr.getPlayerSpeed());
				if (willMovePlayer[0] != 0 || willMovePlayer[1] != 0) {
					if (!isPixelOccupied((player.pos.getX() + willMovePlayer[0]), player.pos.getY(), player.attr.getPlayerWidth(), player.attr.getPlayerHeight(), range, 8)) {
						movePlayer(willMovePlayer[0], 0);
					}
					if (!isPixelOccupied(player.pos.getX(), (player.pos.getY() + willMovePlayer[1]), player.attr.getPlayerWidth(), player.attr.getPlayerHeight(), range, 8)) {
						movePlayer(0, willMovePlayer[1]);
					}
				}
			}
		}

		// EITHER PLAYING OR INVENTORY

		if (player.keys.keyPressed.get("inventory")) {
			if (gameState != GamePanel.GameState.PLAYING) {
				gameState = GamePanel.GameState.PLAYING;
			} else {
				gameState = GamePanel.GameState.INVENTORY;
			}
			player.keys.keyPressed.put("inventory", false);
		}

		// INVENTORY EXCLUSIVE

		if (gameState == GameState.INVENTORY) {
			if (player.keys.keyPressed.get("pause")) {
				gameState = GamePanel.GameState.PLAYING;
				player.keys.keyPressed.put("pause", false);
			}
		}

		// DEBUG KEYS '[' AND ']'

		if (player.keys.keyPressed.get("debug1")) {

			player.inv.addItem(createItem(3, 300));

			player.keys.keyPressed.put("debug1", false);
		}
		if (player.keys.keyPressed.get("debug2")) {

			player.inv.clearInventory();

			player.keys.keyPressed.put("debug2", false);
		}
	}
}
