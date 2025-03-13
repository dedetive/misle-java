package com.ded.misle.input;

import com.ded.misle.core.PhysicsEngine;
import com.ded.misle.world.npcs.NPC;
import com.ded.misle.world.player.PlayerAttributes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ded.misle.core.GamePanel.*;
import static com.ded.misle.core.SettingsManager.getPath;
import static com.ded.misle.renderer.FontManager.dialogNPCText;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.MenuRenderer.goToPreviousMenu;
import static com.ded.misle.renderer.SaveCreator.confirmName;
import static com.ded.misle.renderer.SaveCreator.playerName;
import static com.ded.misle.renderer.SaveSelector.askingToDelete;
import static com.ded.misle.renderer.SettingsMenuRenderer.*;
import static com.ded.misle.world.npcs.NPC.getDialogNPCs;
import static com.ded.misle.world.npcs.NPC.getSelectedNPCs;
import static com.ded.misle.world.npcs.NPCDialog.getCurrentTalkingTo;
import static com.ded.misle.world.npcs.NPCDialog.startDialog;
import static com.ded.misle.renderer.LevelDesignerRenderer.levelDesignerGrid;
import static com.ded.misle.renderer.MenuRenderer.pauseGame;
import static com.ded.misle.input.KeyHandler.Key.*;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.items.Item.createItem;
import static java.awt.event.KeyEvent.*;
import static java.nio.file.Files.createDirectories;

public class KeyHandler implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
		// THIS BAD
	}

	public enum Key {
		PAUSE(VK_ESCAPE),
		UP(VK_UP),
		DOWN(VK_DOWN),
		LEFT_MENU(VK_A),
		LEFT(VK_LEFT),
		RIGHT_MENU(VK_D),
		RIGHT(VK_RIGHT),
		DEBUG1(VK_OPEN_BRACKET),
		DEBUG2(VK_CLOSE_BRACKET),
		SCREENSHOT(VK_F2),
		INVENTORY(VK_E),
		DROP(VK_Q),
		CTRL(VK_CONTROL),
		SHIFT(VK_SHIFT),
		DODGE(VK_C),
		USE(VK_Z),
		EQUAL(VK_EQUALS),
		MINUS(VK_MINUS),
		GRID(VK_G),
		BACKSPACE(VK_BACK_SPACE),
		ENTER(VK_ENTER),
		NUM_0(VK_0),
		NUM_1(VK_1),
		NUM_2(VK_2),
		NUM_3(VK_3),
		NUM_4(VK_4),
		NUM_5(VK_5),
		NUM_6(VK_6),
		NUM_7(VK_7),

		;

		public final int keyCode;

		Key(int keyEvent) {
			keyCodes.put(this, keyEvent);
			keyCode = keyEvent;
		}
	}

	public enum NumberKey {
		NUM_1(0),
		NUM_2(1),
		NUM_3(2),
		NUM_4(3),
		NUM_5(4),
		NUM_6(5),
		NUM_7(6);

		public final int slot;

		NumberKey(int slot) {
			this.slot = slot;
		}
	}

	Key[] continuousInput = new Key[]{
		UP,
		DOWN,
		LEFT,
		RIGHT,
		CTRL,
		SHIFT,
	};

	Key[] outputOnRelease = new Key[] {
		NUM_0,
		NUM_1,
		NUM_2,
		NUM_3,
		NUM_4,
		NUM_5,
		NUM_6,
		NUM_7,
		PAUSE,
		INVENTORY,
		DROP,
		EQUAL,
		MINUS,
		GRID,
		ENTER,
		SCREENSHOT,
	};

	Key[] cooldownOnPress = new Key[]{
		DEBUG1,
		DEBUG2,
		BACKSPACE,
		LEFT_MENU,
		RIGHT_MENU,
	};

	Key[] cooldownOnRelease = new Key[] {
		DODGE,
		USE
	};

	static HashMap<Key, Integer> keyCodes = new HashMap<>();

	public KeyHandler() {
		for (Key key : values()) {
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
				if (!(gameState == GameState.SAVE_CREATOR && e.getKeyCode() != VK_BACK_SPACE)) {
					return;
				}
			}
		}

		for (Key key : cooldownOnPress) {
			if (code == keyCodes.get(key)) {
				handleCooldownPress(key);
				if (!(gameState == GameState.SAVE_CREATOR && e.getKeyCode() != VK_BACK_SPACE)) {
					return;
				}
			}
		}

		// SAVE CREATOR EXCLUSIVE

		if (gameState == GameState.SAVE_CREATOR) {
			if (playerName.length() < 16) {
				playerName.append(removeExtraChars(e.getKeyChar()));
			}
		}
	}

	public static char removeExtraChars(char s) {
		if (dialogNPCText.canDisplay(s)) return s;
		else return '\0';

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
			for (NumberKey numberKey : NumberKey.values()) {
				if (player.keys.keyPressed.get(valueOf(String.valueOf(numberKey)))) {
					player.inv.setSelectedSlot(numberKey.slot);
				}
			}
			if (player.keys.keyPressed.get(PAUSE)) {
				pauseGame();
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
				}
			}
			if (player.keys.keyPressed.get(DODGE)) {
				int delay = 100;
				player.pos.delayedRotate(-360, delay * 5);
				player.setIsInvulnerable(true);
				Timer timer = new Timer(delay, e -> {
					player.setIsInvulnerable(false);
				});
				timer.setRepeats(false); // Ensure the timer only runs once
				timer.start();

			}
			if (player.keys.keyPressed.get(USE)) {
				pressUseButton(mouseHandler);
				player.keys.keyPressed.put(USE, false);
			}

			// MOVING

			if (!player.attr.isDead()) {
				double range = (tileSize + 1) * Math.max(1, player.attr.getSpeed());
				if (willMovePlayer[0] != 0 || willMovePlayer[1] != 0) {
					PlayerAttributes.KnockbackDirection horizontalDirection = PlayerAttributes.KnockbackDirection.NONE;
					PlayerAttributes.KnockbackDirection verticalDirection = PlayerAttributes.KnockbackDirection.NONE;
					if (willMovePlayer[0] > 0) {
						horizontalDirection = PlayerAttributes.KnockbackDirection.RIGHT;
					} else {
						horizontalDirection = PlayerAttributes.KnockbackDirection.LEFT;
					}
					if (willMovePlayer[1] > 0) {
						verticalDirection = PlayerAttributes.KnockbackDirection.DOWN;
					} else {
						verticalDirection = PlayerAttributes.KnockbackDirection.UP;
					}
					if (!PhysicsEngine.isPixelOccupied(player, (player.getX() + willMovePlayer[0] * 2), player.getY(), range, 12, horizontalDirection)) {
						PhysicsEngine.movePlayer(willMovePlayer[0], 0);
					}
					if (!PhysicsEngine.isPixelOccupied(player, player.getX(), (player.getY() + willMovePlayer[1] * 2), range, 12, verticalDirection)) {
						PhysicsEngine.movePlayer(0, willMovePlayer[1]);
					}
				}
			}
		}

		// EITHER PLAYING OR INVENTORY

		if (player.keys.keyPressed.get(INVENTORY)) {
			if (gameState == GameState.PLAYING) {
				gameState = GameState.INVENTORY;
			} else if (gameState == GameState.INVENTORY) {
				gameState = GameState.PLAYING;
			}
		}

		// INVENTORY EXCLUSIVE

		if (gameState == GameState.INVENTORY) {
			int hoveredRow = mouseHandler.getHoveredSlot()[0];
			int hoveredCol = mouseHandler.getHoveredSlot()[1];

			if (hoveredRow >= 0 && hoveredCol >= 0) {
				for (NumberKey numberKey : NumberKey.values()) {
					if (player.keys.keyPressed.get(valueOf(String.valueOf(numberKey)))) {
						player.inv.setTempItem(player.inv.getItem(hoveredRow, hoveredCol));
						player.inv.bruteSetItem(player.inv.getItem(0, numberKey.slot), hoveredRow, hoveredCol);
						player.inv.bruteSetItem(player.inv.getTempItem(), 0, numberKey.slot);
						player.inv.destroyTempItem();

					}
				}
			}

			if (player.keys.keyPressed.get(PAUSE)) {
				gameState = GameState.PLAYING;
			}

			if (player.keys.keyPressed.get(DROP)) {
				if (mouseHandler.getHoveredSlot()[0] >= 0 && mouseHandler.getHoveredSlot()[1] >= 0 && player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]) != null) {
					if (player.keys.keyPressed.get(CTRL)) {
						player.inv.dropItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1], player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]).getCount());
					} else {
						player.inv.dropItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1], 1);
					}
				} else if (mouseHandler.getExtraHoveredSlot()[0] >= 0 && mouseHandler.getExtraHoveredSlot()[1] >= 0 && player.inv.getItem(mouseHandler.getExtraHoveredSlot()[0] * 2 + mouseHandler.getExtraHoveredSlot()[1]) != null) {
					if (player.keys.keyPressed.get(CTRL)) {
						player.inv.dropItem(mouseHandler.getExtraHoveredSlot()[1] * 2 + mouseHandler.getExtraHoveredSlot()[0], player.inv.getItem(mouseHandler.getExtraHoveredSlot()[0] * 2 + mouseHandler.getExtraHoveredSlot()[1]).getCount());
					} else {
						player.inv.dropItem(mouseHandler.getExtraHoveredSlot()[1] * 2 + mouseHandler.getExtraHoveredSlot()[0], 1);
					}
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
				PhysicsEngine.movePlayer(willMovePlayer[0], willMovePlayer[1]);
			}

			// Pause

			if (player.keys.keyPressed.get(PAUSE)) {
				pauseGame();
			}

			// Zooming

			if (player.keys.keyPressed.get(EQUAL)) {
				gameScale = Math.min(8, gameScale + 0.25);
				updateTileSize();
			}
			if (player.keys.keyPressed.get(MINUS)) {
				gameScale = Math.max(0.75, gameScale - 0.25);
				updateTileSize();
			}
			if (player.keys.keyPressed.get(NUM_0) && player.keys.keyPressed.get(CTRL)) {
				gameScale = scale;
			}
			if (player.keys.keyPressed.get(GRID)) {
				levelDesignerGrid = !levelDesignerGrid;
			}
		}

		// SAVE CREATOR EXCLUSIVE

		if (gameState == GameState.SAVE_CREATOR) {
			if (player.keys.keyPressed.get(BACKSPACE)) {
				playerName.setLength(Math.max(playerName.length() - 1, 0));
			}
			if (player.keys.keyPressed.get(ENTER)) {
				confirmName();
			}
		}

		// DIALOG EXCLUSIVE

		if (gameState == GameState.DIALOG) {
			if (player.keys.keyPressed.get(USE)) {
				getCurrentTalkingTo().incrementDialogIndex();
			}
		}

		// OTHER MENUS
			// Options, save selector and save creator
		if (gameState == GameState.OPTIONS_MENU || gameState == GameState.SAVE_SELECTOR || gameState == GameState.SAVE_CREATOR) {
			if (player.keys.keyPressed.get(PAUSE)) {
				if (gameState == GameState.SAVE_SELECTOR && askingToDelete > -1) {
					askingToDelete = -1;
					clearButtonFading();
					clearButtons();
				} else {
					goToPreviousMenu();
				}
			}

			if (gameState == GameState.OPTIONS_MENU) {
				if (player.keys.keyPressed.get(LEFT_MENU) || player.keys.keyPressed.get(RIGHT_MENU)) {

					if (player.keys.keyPressed.get(LEFT_MENU)) {
						moveSettingMenu(-1);
						leftKeyIndicatorWidth = 19;
					}
					if (player.keys.keyPressed.get(RIGHT_MENU)) {
						moveSettingMenu(1);
						rightKeyIndicatorWidth = 19;
					}

				}
			}
		}

		if (player.keys.keyPressed.get(SCREENSHOT)) {
			takeScreenshot();
		}

		// DEBUG KEYS '[' AND ']'

		if (gameState != GameState.LEVEL_DESIGNER) {
			if (player.keys.keyPressed.get(DEBUG1)) {
				if (!player.keys.keyPressed.get(SHIFT)) {
					for (int i = 1; i <= 22; i++) {
						if (i != 5) {
							player.inv.addItem(createItem(i, 1));
						}
					}
				} else {
					if (player.keys.keyPressed.get(CTRL)) {
						player.attr.setLevel(1);
					} else {
						player.attr.addXP(player.attr.getXPtoLevelUp() * 9 / 10);
					}
				}


			}
			if (player.keys.keyPressed.get(DEBUG2)) {

				player.inv.clearInventory();

			}
		}

		setKeysToFalse();
	}

	public static void takeScreenshot() {
		try {
			// Image getter
			JFrame frame = getWindow();
			BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
			frame.printAll(img.getGraphics());

			// File creator
			String t = LocalDateTime.now().toString();
			t = t.substring(0, t.indexOf("."));
			t = t.replace("T", ".");

			createDirectories(Path.of(getPath() + "/resources/screenshots"));
			ImageIO.write(img, "png", (getPath().resolve("resources/screenshots/" + t + ".png")).toFile());
//					System.out.println("Screenshot saved at " + getPath().resolve("resources/screenshots/" + t + ".png"));
		} catch (IOException e) {
			System.out.println("Failed to take a screenshot");
		}
	}

	public void setKeysToFalse() {
		List<Key> keysToSetFalse = new ArrayList<>();
		keysToSetFalse.addAll(List.of(outputOnRelease));
		keysToSetFalse.addAll(List.of(cooldownOnRelease));
		keysToSetFalse.addAll(List.of(cooldownOnPress));

		for (Key key : keysToSetFalse) {
			player.keys.keyPressed.put(key, false);
		}
	}

	public static void pressUseButton(MouseHandler mouseHandler) {
		ArrayList<NPC> nearbyNPCs = getSelectedNPCs();

		if (nearbyNPCs.isEmpty()) {
			player.inv.useItem(mouseHandler);
		} else {
			int size = nearbyNPCs.size();
			int rand = ThreadLocalRandom.current().nextInt(0, size);
			NPC npc = nearbyNPCs.get(rand);

			if (getDialogNPCs().contains(npc)) {
				startDialog(npc);
			}
		}
	}

	public static String getChar(Key key) {
		return getKeyText(key.keyCode);
	}
}
