package com.ded.misle;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.GameRenderer.levelDesignerGrid;
import static com.ded.misle.GameRenderer.pauseGame;
import static com.ded.misle.Launcher.scale;
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
		player.keys.keyPressed.put("shift", false);
		player.keys.keyPressed.put("dodge", false);
		player.keys.keyPressed.put("use", false);
		player.keys.keyPressed.put("equal", false); // LEVEL DESIGNER EXCLUSIVE
		player.keys.keyPressed.put("minus", false); // LEVEL DESIGNER EXCLUSIVE
		player.keys.keyPressed.put("grid", false);  // LEVEL DESIGNER EXCLUSIVE
		player.keys.keyPressed.put("0", false);     // LEVEL DESIGNER EXCLUSIVE
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
	int KeyInventory = KeyEvent.VK_E;
	int KeyDrop = KeyEvent.VK_Q;
	int KeyCtrl = KeyEvent.VK_CONTROL;
	int KeyShift = KeyEvent.VK_SHIFT;
	int KeyDodge = KeyEvent.VK_C;
	int KeyUse = KeyEvent.VK_Z;
	int KeyEqual = KeyEvent.VK_EQUALS;
	int KeyMinus = KeyEvent.VK_MINUS;
	int KeyGrid = KeyEvent.VK_G;
	int Key0 = KeyEvent.VK_0;
	int Key1 = KeyEvent.VK_1;
	int Key2 = KeyEvent.VK_2;
	int Key3 = KeyEvent.VK_3;
	int Key4 = KeyEvent.VK_4;
	int Key5 = KeyEvent.VK_5;
	int Key6 = KeyEvent.VK_6;
	int Key7 = KeyEvent.VK_7;

	static double baseDesignerSpeed = 1.67;
	static double designerSpeed = baseDesignerSpeed * scale;
	static {
		updateDesignerSpeed();
	}

	public static void updateDesignerSpeed() {
		designerSpeed = baseDesignerSpeed * scale;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();

		if (code == Key0) {
			player.keys.keyPressed.put("0", true);
		}
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
		if (code == KeyShift) {
			player.keys.keyPressed.put("shift", true);
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
		if (code == Key0) {
			player.keys.keyPressed.put("0", false);
		}
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
		if (code == KeyShift) {
			player.keys.keyPressed.put("shift", false);
		}
		if (code == KeyEqual) {
			player.keys.keyPressed.put("equal", true);
		}
		if (code == KeyMinus) {
			player.keys.keyPressed.put("minus", true);
		}
		if (code == KeyGrid) {
			player.keys.keyPressed.put("grid", true);
		}
		if (code == KeyDodge) {
			handleCooldownPress("dodge");
		}
		if (code == KeyUse) {
			handleCooldownPress("use");
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

	public void updateKeys(MouseHandler mouseHandler) {
		// GLOBAL

		if (player.keys.keyPressed.get("ctrl") && player.keys.keyPressed.get("shift") && player.keys.keyPressed.get("minus")) {
			// Ctrl + Shift + - results in crashing without saving

			System.exit(0);
		}

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
					willMovePlayer[1] -= player.attr.getSpeed();
				} else {
					willMovePlayer[1] -= (player.attr.getSpeed() * Math.sqrt(2) / 3);
				}
			}
			if (player.keys.keyPressed.get("down")) {
				if (!player.keys.keyPressed.get("left") || !player.keys.keyPressed.get("right")) {
					willMovePlayer[1] += player.attr.getSpeed();
				} else {
					willMovePlayer[1] += player.attr.getSpeed() * Math.sqrt(2) / 3;
				}
			}
			if (player.keys.keyPressed.get("left")) {
				if (!player.keys.keyPressed.get("up") || !player.keys.keyPressed.get("down")) {
					willMovePlayer[0] -= player.attr.getSpeed();
				} else {
					willMovePlayer[0] -= player.attr.getSpeed() * Math.sqrt(2) / 3;
				}
			}
			if (player.keys.keyPressed.get("right")) {
				willMovePlayer[0] += player.attr.getSpeed();
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
			if (player.keys.keyPressed.get("dodge")) {
				int delay = 100;
				player.pos.delayedRotate(-360, delay * 5);
				player.attr.setIsInvulnerable(true);
				Timer timer = new Timer(delay, e -> {
					player.attr.setIsInvulnerable(false);
				});
				timer.setRepeats(false); // Ensure the timer only runs once
				timer.start();

				player.keys.keyPressed.put("dodge", false);
			}
			if (player.keys.keyPressed.get("use")) {
				player.inv.useItem();
				player.keys.keyPressed.put("use", false);
			}

			// MOVING

			if (!player.attr.isDead()) {
				double range = (tileSize + 1) * Math.max(1, player.attr.getSpeed());
				if (willMovePlayer[0] != 0 || willMovePlayer[1] != 0) {
					if (!isPixelOccupied((player.pos.getX() + willMovePlayer[0]), player.pos.getY(), player.attr.getWidth(), player.attr.getHeight(), range, 12, true)) {
						movePlayer(willMovePlayer[0], 0);
					}
					if (!isPixelOccupied(player.pos.getX(), (player.pos.getY() + willMovePlayer[1]), player.attr.getWidth(), player.attr.getHeight(), range, 12, true)) {
						movePlayer(0, willMovePlayer[1]);
					}
				}
			}
		}

		// EITHER PLAYING OR INVENTORY

		if (player.keys.keyPressed.get("inventory")) {
			if (gameState == GamePanel.GameState.PLAYING) {
				gameState = GamePanel.GameState.INVENTORY;
			} else if (gameState == GamePanel.GameState.INVENTORY) {
				gameState = GamePanel.GameState.PLAYING;
			}
			player.keys.keyPressed.put("inventory", false);
		}

		// INVENTORY EXCLUSIVE

		if (gameState == GameState.INVENTORY) {
			if (player.keys.keyPressed.get("pause")) {
				gameState = GamePanel.GameState.PLAYING;
				player.keys.keyPressed.put("pause", false);
			}

			if (player.keys.keyPressed.get("drop")) {
				if (mouseHandler.getHoveredSlot()[0] >= 0 && mouseHandler.getHoveredSlot()[1] >= 0 && player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]) != null) {
					if (player.keys.keyPressed.get("ctrl")) {
						player.inv.dropItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1], player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]).getCount());
					} else {
						player.inv.dropItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1], 1);
					}
					player.keys.keyPressed.put("drop", false);
				}
			}
		}

		// LEVEL DESIGNER EXCLUSIVE

		if (gameState == GameState.LEVEL_DESIGNER) {

			// Designer speed manipulation

			if (player.keys.keyPressed.get("debug1")) {
				if (!player.keys.keyPressed.get("shift")) {
					baseDesignerSpeed = Math.min(3, baseDesignerSpeed + 0.4);
					updateDesignerSpeed();
				} else {
					baseDesignerSpeed = Math.max(0.4, baseDesignerSpeed - 0.4);
					updateDesignerSpeed();
				}
				player.keys.keyPressed.put("debug1", false);
			}

			// Movement

			double[] willMovePlayer = {0, 0};

			if (player.keys.keyPressed.get("up")) {
				if (!player.keys.keyPressed.get("left") || !player.keys.keyPressed.get("right")) {
					willMovePlayer[1] -= designerSpeed;
				} else {
					willMovePlayer[1] -= (designerSpeed * Math.sqrt(2) / 3);
				}
			}
			if (player.keys.keyPressed.get("down")) {
				if (!player.keys.keyPressed.get("left") || !player.keys.keyPressed.get("right")) {
					willMovePlayer[1] += designerSpeed;
				} else {
					willMovePlayer[1] += designerSpeed * Math.sqrt(2) / 3;
				}
			}
			if (player.keys.keyPressed.get("left")) {
				if (!player.keys.keyPressed.get("up") || !player.keys.keyPressed.get("down")) {
					willMovePlayer[0] -= designerSpeed;
				} else {
					willMovePlayer[0] -= designerSpeed * Math.sqrt(2) / 3;
				}
			}
			if (player.keys.keyPressed.get("right")) {
				willMovePlayer[0] += designerSpeed;
			}

			if (willMovePlayer[0] != 0 || willMovePlayer[1] != 0) {
				movePlayer(willMovePlayer[0], willMovePlayer[1]);
			}

			// Pause

			if (player.keys.keyPressed.get("pause")) {
				pauseGame();
				player.keys.keyPressed.put("pause", false);
			}

			// Zooming

			if (player.keys.keyPressed.get("equal")) {
				gameScale = Math.min(8, gameScale + 0.25);
				updateTileSize();
				player.keys.keyPressed.put("equal", false);
			}
			if (player.keys.keyPressed.get("minus")) {
				gameScale = Math.max(0.75, gameScale - 0.25);
				updateTileSize();
				player.keys.keyPressed.put("minus", false);
			}
			if (player.keys.keyPressed.get("0") && player.keys.keyPressed.get("ctrl")) {
				gameScale = scale;
			}
			if (player.keys.keyPressed.get("grid")) {
				levelDesignerGrid = !levelDesignerGrid;
				player.keys.keyPressed.put("grid", false);
			}
		}

		// DEBUG KEYS '[' AND ']'

		if (gameState != GameState.LEVEL_DESIGNER) {
			if (player.keys.keyPressed.get("debug1")) {

				player.inv.addItem(createItem(1));
				player.inv.addItem(createItem(2));
				player.inv.addItem(createItem(3, 20));

				player.keys.keyPressed.put("debug1", false);
			}
			if (player.keys.keyPressed.get("debug2")) {

				player.inv.clearInventory();

				player.keys.keyPressed.put("debug2", false);
			}
		}
	}
}
