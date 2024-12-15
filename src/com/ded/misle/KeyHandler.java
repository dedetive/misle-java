package com.ded.misle;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.renderer.LevelDesignerRenderer.levelDesignerGrid;
import static com.ded.misle.renderer.MenuRenderer.pauseGame;
import static com.ded.misle.KeyHandler.Key.*;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.items.Item.createItem;
import static java.awt.event.KeyEvent.*;

public class KeyHandler implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
		// THIS BAD
	}

	public enum Key {
		PAUSE(VK_ESCAPE),
		UP(VK_UP),
		DOWN(VK_DOWN),
		LEFT(VK_LEFT),
		RIGHT(VK_RIGHT),
		DEBUG1(VK_OPEN_BRACKET),
		DEBUG2(VK_CLOSE_BRACKET),
		INVENTORY(VK_E),
		DROP(VK_Q),
		CTRL(VK_CONTROL),
		SHIFT(VK_SHIFT),
		DODGE(VK_C),
		USE(VK_Z),
		EQUAL(VK_EQUALS),
		MINUS(VK_MINUS),
		GRID(VK_G),
		NUM_0(VK_0),
		NUM_1(VK_1),
		NUM_2(VK_2),
		NUM_3(VK_3),
		NUM_4(VK_4),
		NUM_5(VK_5),
		NUM_6(VK_6),
		NUM_7(VK_7);

		Key(int keyEvent) {
			keyCodes.put(this, keyEvent);
		}
	}

	Key[] continuousInput = new Key[]{
		NUM_0,
		NUM_1,
		NUM_2,
		NUM_3,
		NUM_4,
		NUM_5,
		NUM_6,
		NUM_7,
		UP,
		DOWN,
		LEFT,
		RIGHT,
		CTRL,
		SHIFT
	};

	Key[] outputOnRelease = new Key[] {
		PAUSE,
		INVENTORY,
		DROP,
		EQUAL,
		MINUS,
		GRID
	};

	Key[] cooldownOnPress = new Key[]{
		DEBUG1,
		DEBUG2
	};

	Key[] cooldownOnRelease = new Key[] {
		DODGE,
		USE
	};

	static HashMap<Key, Integer> keyCodes = new HashMap<>();

	public KeyHandler() {
		for (Key key : Key.values()) {
			player.keys.keyPressed.put(key, false);
		}
	}

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

		for (Key key : continuousInput) {
			if (code == keyCodes.get(key)) {
				player.keys.keyPressed.put(key, true);
				return;
			}
		}

		for (Key key : cooldownOnPress) {
			if (code == keyCodes.get(key)) {
				handleCooldownPress(key);
				return;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();

		for (Key key : continuousInput) {
			if (code == keyCodes.get(key)) {
				player.keys.keyPressed.put(key, false);
				return;
			}
		}

		for (Key key : outputOnRelease) {
			if (code == keyCodes.get(key)) {
				player.keys.keyPressed.put(key, true);
				return;
			}
		}

		for (Key key : cooldownOnRelease) {
			if (code == keyCodes.get(key)) {
				handleCooldownPress(key);
				return;
			}
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
					if (!Physics.isPixelOccupied((player.getX() + willMovePlayer[0]), player.getY(), player.getBoxScaleHorizontal(), player.getBoxScaleVertical(), range, 12, Physics.ObjectType.PLAYER)) {
						Physics.movePlayer(willMovePlayer[0], 0);
					}
					if (!Physics.isPixelOccupied(player.getX(), (player.getY() + willMovePlayer[1]), player.getBoxScaleHorizontal(), player.getBoxScaleVertical(), range, 12, Physics.ObjectType.PLAYER)) {
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

				for (int i = 1; i < 19; i++) {
					if (i != 5) {
						player.inv.addItem(createItem(i, 1));
					}
				}
				player.keys.keyPressed.put(DEBUG1, false);

			}
			if (player.keys.keyPressed.get(DEBUG2)) {

				player.inv.clearInventory();
				player.keys.keyPressed.put(DEBUG2, false);

			}
		}
	}
}
