package com.ded.misle;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.GameRenderer.levelDesignerGrid;
import static com.ded.misle.GameRenderer.pauseGame;
import static com.ded.misle.KeyHandler.Key.*;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.items.Item.createItem;

public class KeyHandler implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
		// THIS BAD
	}

	public enum Key {
		PAUSE,
		UP,
		DOWN,
		LEFT,
		RIGHT,
		DEBUG1,
		DEBUG2,
		INVENTORY,
		DROP,
		CTRL,
		SHIFT,
		DODGE,
		USE,
		EQUAL,
		MINUS,
		GRID,
		NUM_0,
		NUM_1,
		NUM_2,
		NUM_3,
		NUM_4,
		NUM_5,
		NUM_6,
		NUM_7
	}

	public KeyHandler() {
		player.keys.keyPressed.put(PAUSE, false);
		player.keys.keyPressed.put(UP, false);
		player.keys.keyPressed.put(DOWN, false);
		player.keys.keyPressed.put(LEFT, false);
		player.keys.keyPressed.put(RIGHT, false);
		player.keys.keyPressed.put(DEBUG1, false);
		player.keys.keyPressed.put(DEBUG2, false);
		player.keys.keyPressed.put(INVENTORY, false);
		player.keys.keyPressed.put(DROP, false);
		player.keys.keyPressed.put(CTRL, false);
		player.keys.keyPressed.put(SHIFT, false);
		player.keys.keyPressed.put(DODGE, false);
		player.keys.keyPressed.put(USE, false);
		player.keys.keyPressed.put(EQUAL, false); // LEVEL DESIGNER EXCLUSIVE
		player.keys.keyPressed.put(MINUS, false); // LEVEL DESIGNER EXCLUSIVE
		player.keys.keyPressed.put(GRID, false);  // LEVEL DESIGNER EXCLUSIVE
		player.keys.keyPressed.put(NUM_0, false); // LEVEL DESIGNER EXCLUSIVE
		player.keys.keyPressed.put(NUM_1, false);
		player.keys.keyPressed.put(NUM_2, false);
		player.keys.keyPressed.put(NUM_3, false);
		player.keys.keyPressed.put(NUM_4, false);
		player.keys.keyPressed.put(NUM_5, false);
		player.keys.keyPressed.put(NUM_6, false);
		player.keys.keyPressed.put(NUM_7, false);
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
			player.keys.keyPressed.put(NUM_0, true);
		}
		if (code == Key1) {
			player.keys.keyPressed.put(NUM_1, true);
		}
		if (code == Key2) {
			player.keys.keyPressed.put(NUM_2, true);
		}
		if (code == Key3) {
			player.keys.keyPressed.put(NUM_3, true);
		}
		if (code == Key4) {
			player.keys.keyPressed.put(NUM_4, true);
		}
		if (code == Key5) {
			player.keys.keyPressed.put(NUM_5, true);
		}
		if (code == Key6) {
			player.keys.keyPressed.put(NUM_6, true);
		}
		if (code == Key7) {
			player.keys.keyPressed.put(NUM_7, true);
		}
		if (code == KeyUp) {
			player.keys.keyPressed.put(UP, true);
		}
		if (code == KeyDown) {
			player.keys.keyPressed.put(DOWN, true);
		}
		if (code == KeyLeft) {
			player.keys.keyPressed.put(LEFT, true);
		}
		if (code == KeyRight) {
			player.keys.keyPressed.put(RIGHT, true);
		}
		if (code == KeyCtrl) {
			player.keys.keyPressed.put(CTRL, true);
		}
		if (code == KeyShift) {
			player.keys.keyPressed.put(SHIFT, true);
		}
		if (code == KeyDebug1) {
			handleCooldownPress(DEBUG1);
		}
		if (code == KeyDebug2) {
			handleCooldownPress(DEBUG2);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == Key0) {
			player.keys.keyPressed.put(NUM_0, false);
		}
		if (code == Key1) {
			player.keys.keyPressed.put(NUM_1, false);
		}
		if (code == Key2) {
			player.keys.keyPressed.put(NUM_2, false);
		}
		if (code == Key3) {
			player.keys.keyPressed.put(NUM_3, false);
		}
		if (code == Key4) {
			player.keys.keyPressed.put(NUM_4, false);
		}
		if (code == Key5) {
			player.keys.keyPressed.put(NUM_5, false);
		}
		if (code == Key6) {
			player.keys.keyPressed.put(NUM_6, false);
		}
		if (code == Key7) {
			player.keys.keyPressed.put(NUM_7, false);
		}
		if (code == KeyPause) {
			player.keys.keyPressed.put(PAUSE, true);
		}
		if (code == KeyUp) {
			player.keys.keyPressed.put(UP, false);
		}
		if (code == KeyDown) {
			player.keys.keyPressed.put(DOWN, false);
		}
		if (code == KeyLeft) {
			player.keys.keyPressed.put(LEFT, false);
		}
		if (code == KeyRight) {
			player.keys.keyPressed.put(RIGHT, false);
		}
		if (code == KeyInventory) {
			player.keys.keyPressed.put(INVENTORY, true);
		}
		if (code == KeyDrop) {
			player.keys.keyPressed.put(DROP, true);
		}
		if (code == KeyCtrl) {
			player.keys.keyPressed.put(CTRL, false);
		}
		if (code == KeyShift) {
			player.keys.keyPressed.put(SHIFT, false);
		}
		if (code == KeyEqual) {
			player.keys.keyPressed.put(EQUAL, true);
		}
		if (code == KeyMinus) {
			player.keys.keyPressed.put(MINUS, true);
		}
		if (code == KeyGrid) {
			player.keys.keyPressed.put(GRID, true);
		}
		if (code == KeyDodge) {
			handleCooldownPress(DODGE);
		}
		if (code == KeyUse) {
			handleCooldownPress(USE);
		}
	}

	private void handleCooldownPress(Key key) {
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

		if (player.keys.keyPressed.get(CTRL) && player.keys.keyPressed.get(SHIFT) && player.keys.keyPressed.get(MINUS)) {
			// Ctrl + Shift + - results in crashing without saving

			System.exit(0);
		}

		// PLAYING EXCLUSIVE

		if (gameState == GameState.PLAYING) {
			if (player.keys.keyPressed.get(NUM_1)) {
				player.inv.setSelectedSlot(0);
			}
			if (player.keys.keyPressed.get(NUM_2)) {
				player.inv.setSelectedSlot(1);
			}
			if (player.keys.keyPressed.get(NUM_3)) {
				player.inv.setSelectedSlot(2);
			}
			if (player.keys.keyPressed.get(NUM_4)) {
				player.inv.setSelectedSlot(3);
			}
			if (player.keys.keyPressed.get(NUM_5)) {
				player.inv.setSelectedSlot(4);
			}
			if (player.keys.keyPressed.get(NUM_6)) {
				player.inv.setSelectedSlot(5);
			}
			if (player.keys.keyPressed.get(NUM_7)) {
				player.inv.setSelectedSlot(6);
			}
			if (player.keys.keyPressed.get(PAUSE)) {
				pauseGame();
				player.keys.keyPressed.put(PAUSE, false);
			}
			double[] willMovePlayer = {0, 0};
			if (player.keys.keyPressed.get(UP)) {
				if (!player.keys.keyPressed.get(LEFT) || !player.keys.keyPressed.get(RIGHT)) {
					willMovePlayer[1] -= player.attr.getSpeed();
				} else {
					willMovePlayer[1] -= Math.sqrt(player.attr.getSpeed());
				}
			}
			if (player.keys.keyPressed.get(DOWN)) {
				if (!player.keys.keyPressed.get(LEFT) || !player.keys.keyPressed.get(RIGHT)) {
					willMovePlayer[1] += player.attr.getSpeed();
				} else {
					willMovePlayer[1] += Math.sqrt(player.attr.getSpeed());
				}
			}
			if (player.keys.keyPressed.get(LEFT)) {
				if (!player.keys.keyPressed.get(UP) || !player.keys.keyPressed.get(DOWN)) {
					willMovePlayer[0] -= player.attr.getSpeed();
				} else {
					willMovePlayer[0] -= Math.sqrt(player.attr.getSpeed());
				}
			}
			if (player.keys.keyPressed.get(RIGHT)) {
				willMovePlayer[0] += player.attr.getSpeed();
			}
			if (player.keys.keyPressed.get(DROP)) {
				if (player.inv.hasHeldItem()) {
					if (player.keys.keyPressed.get(CTRL)) {
						player.inv.dropItem(0, player.inv.getSelectedSlot(), player.inv.getSelectedItem().getCount());
					} else {
						player.inv.dropItem(0, player.inv.getSelectedSlot(), 1);
					}
					player.keys.keyPressed.put(DROP, false);
				}
			}
			if (player.keys.keyPressed.get(DODGE)) {
				int delay = 100;
				player.pos.delayedRotate(-360, delay * 5);
				player.attr.setIsInvulnerable(true);
				Timer timer = new Timer(delay, e -> {
					player.attr.setIsInvulnerable(false);
				});
				timer.setRepeats(false); // Ensure the timer only runs once
				timer.start();

				player.keys.keyPressed.put(DODGE, false);
			}
			if (player.keys.keyPressed.get(USE)) {
				player.inv.useItem();
				player.keys.keyPressed.put(USE, false);
			}

			// MOVING

			if (!player.attr.isDead()) {
				double range = (tileSize + 1) * Math.max(1, player.attr.getSpeed());
				if (willMovePlayer[0] != 0 || willMovePlayer[1] != 0) {
					if (!Physics.isPixelOccupied((player.getX() + willMovePlayer[0]), player.getY(), player.attr.getWidth(), player.attr.getHeight(), range, 12, Physics.ObjectType.PLAYER)) {
						Physics.movePlayer(willMovePlayer[0], 0);
					}
					if (!Physics.isPixelOccupied(player.getX(), (player.getY() + willMovePlayer[1]), player.attr.getWidth(), player.attr.getHeight(), range, 12, Physics.ObjectType.PLAYER)) {
						Physics.movePlayer(0, willMovePlayer[1]);
					}
				}
			}
		}

		// EITHER PLAYING OR INVENTORY

		if (player.keys.keyPressed.get(INVENTORY)) {
			if (gameState == GamePanel.GameState.PLAYING) {
				gameState = GamePanel.GameState.INVENTORY;
			} else if (gameState == GamePanel.GameState.INVENTORY) {
				gameState = GamePanel.GameState.PLAYING;
			}
			player.keys.keyPressed.put(INVENTORY, false);
		}

		// INVENTORY EXCLUSIVE

		if (gameState == GameState.INVENTORY) {
			if (player.keys.keyPressed.get(PAUSE)) {
				gameState = GamePanel.GameState.PLAYING;
				player.keys.keyPressed.put(PAUSE, false);
			}

			if (player.keys.keyPressed.get(DROP)) {
				if (mouseHandler.getHoveredSlot()[0] >= 0 && mouseHandler.getHoveredSlot()[1] >= 0 && player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]) != null) {
					if (player.keys.keyPressed.get(CTRL)) {
						player.inv.dropItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1], player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]).getCount());
					} else {
						player.inv.dropItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1], 1);
					}
					player.keys.keyPressed.put(DROP, false);
				}
			}
		}

		// LEVEL DESIGNER EXCLUSIVE

		if (gameState == GameState.LEVEL_DESIGNER) {

			// Designer speed manipulation

			if (player.keys.keyPressed.get(DEBUG1)) {
				if (!player.keys.keyPressed.get(SHIFT)) {
					baseDesignerSpeed = Math.min(3, baseDesignerSpeed + 0.4);
					updateDesignerSpeed();
				} else {
					baseDesignerSpeed = Math.max(0.4, baseDesignerSpeed - 0.4);
					updateDesignerSpeed();
				}
				player.keys.keyPressed.put(DEBUG1, false);
			}

			// Movement

			double[] willMovePlayer = {0, 0};

			if (player.keys.keyPressed.get(UP)) {
				if (!player.keys.keyPressed.get(LEFT) || !player.keys.keyPressed.get(RIGHT)) {
					willMovePlayer[1] -= designerSpeed;
				} else {
					willMovePlayer[1] -= (designerSpeed * Math.sqrt(2) / 3);
				}
			}
			if (player.keys.keyPressed.get(DOWN)) {
				if (!player.keys.keyPressed.get(LEFT) || !player.keys.keyPressed.get(RIGHT)) {
					willMovePlayer[1] += designerSpeed;
				} else {
					willMovePlayer[1] += designerSpeed * Math.sqrt(2) / 3;
				}
			}
			if (player.keys.keyPressed.get(LEFT)) {
				if (!player.keys.keyPressed.get(UP) || !player.keys.keyPressed.get(DOWN)) {
					willMovePlayer[0] -= designerSpeed;
				} else {
					willMovePlayer[0] -= designerSpeed * Math.sqrt(2) / 3;
				}
			}
			if (player.keys.keyPressed.get(RIGHT)) {
				willMovePlayer[0] += designerSpeed;
			}

			if (willMovePlayer[0] != 0 || willMovePlayer[1] != 0) {
				Physics.movePlayer(willMovePlayer[0], willMovePlayer[1]);
			}

			// Pause

			if (player.keys.keyPressed.get(PAUSE)) {
				pauseGame();
				player.keys.keyPressed.put(PAUSE, false);
			}

			// Zooming

			if (player.keys.keyPressed.get(EQUAL)) {
				gameScale = Math.min(8, gameScale + 0.25);
				updateTileSize();
				player.keys.keyPressed.put(EQUAL, false);
			}
			if (player.keys.keyPressed.get(MINUS)) {
				gameScale = Math.max(0.75, gameScale - 0.25);
				updateTileSize();
				player.keys.keyPressed.put(MINUS, false);
			}
			if (player.keys.keyPressed.get(NUM_0) && player.keys.keyPressed.get(CTRL)) {
				gameScale = scale;
			}
			if (player.keys.keyPressed.get(GRID)) {
				levelDesignerGrid = !levelDesignerGrid;
				player.keys.keyPressed.put(GRID, false);
			}
		}

		// DEBUG KEYS '[' AND ']'

		if (gameState != GameState.LEVEL_DESIGNER) {
			if (player.keys.keyPressed.get(DEBUG1)) {

				if (!player.keys.keyPressed.get(SHIFT)) {

					player.inv.addItem(createItem(3, 20));

				} else {

					player.attr.setMaxStackSizeMulti(player.attr.getMaxStackSizeMulti() + 0.125f);
					System.out.println("Updated max stack size to " + player.attr.getMaxStackSizeMulti());

				}

				player.keys.keyPressed.put(DEBUG1, false);
			}
			if (player.keys.keyPressed.get(DEBUG2)) {

				if (!player.keys.keyPressed.get(SHIFT)) {

					player.inv.clearInventory();

				} else {

					player.attr.setMaxStackSizeMulti(player.attr.getMaxStackSizeMulti() - 0.125f);
					System.out.println("Updated max stack size to " + player.attr.getMaxStackSizeMulti());

				}


				player.keys.keyPressed.put(DEBUG2, false);
			}
		}
	}
}
