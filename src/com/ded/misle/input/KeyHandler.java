package com.ded.misle.input;

import com.ded.misle.world.entities.player.Planner;
import com.ded.misle.world.logic.LogicManager;
import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.entities.npcs.NPC;
import com.ded.misle.world.entities.player.PlayerAttributes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.world.logic.PhysicsEngine.isSpaceOccupied;
import static com.ded.misle.renderer.DialogRenderer.fillLetterDisplay;
import static com.ded.misle.renderer.DialogRenderer.isLetterDisplayFull;
import static com.ded.misle.renderer.FontManager.dialogNPCText;
import static com.ded.misle.renderer.ImageManager.*;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.MenuRenderer.goToPreviousMenu;
import static com.ded.misle.renderer.SaveCreator.confirmName;
import static com.ded.misle.renderer.SaveCreator.playerName;
import static com.ded.misle.renderer.SaveSelector.askingToDelete;
import static com.ded.misle.renderer.SettingsMenuRenderer.*;
import static com.ded.misle.world.entities.npcs.NPC.getDialogNPCs;
import static com.ded.misle.world.entities.npcs.NPC.getSelectedNPCs;
import static com.ded.misle.world.entities.npcs.NPCDialog.getCurrentTalkingTo;
import static com.ded.misle.world.entities.npcs.NPCDialog.startDialog;
import static com.ded.misle.renderer.LevelDesignerRenderer.levelDesignerGrid;
import static com.ded.misle.renderer.MenuRenderer.pauseGame;
import static com.ded.misle.input.KeyHandler.Key.*;
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
		LEFT_MENU(VK_A),
		RIGHT_MENU(VK_D),
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
		PLANNING_TOGGLE(VK_SPACE),
		PLANNING_CONFIRM(VK_ENTER), /*
			TODO: unused while this key handler system is trash. please refactor this whole thing
				this absolute buffoon cannot even handle two keys with the same key code
		 */

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
		CTRL,
		SHIFT,
	};

	Key[] outputOnRelease = new Key[] {
		UP,
		DOWN,
		LEFT,
		RIGHT,
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
		PLANNING_TOGGLE,
		PLANNING_CONFIRM,
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
		USE,
	};

	Key[] triggerLogic = new Key[] {
		DODGE,
		DROP,
		USE,
		LEFT,
		RIGHT,
		DOWN,
		UP,
	};

	private void triggerLogicIfNeeded() {
		for (Key key : triggerLogic) {
			if (isPressed(key)) {
				LogicManager.requestNewTurn();
			}
		}
	}

	static HashMap<Key, Integer> keyCodes = new HashMap<>();

	public KeyHandler() {
		for (Key key : values()) {
			player.keys.keyPressed.put(key, false);
		}
	}

	static double baseDesignerSpeed = 1.67;
	static int designerSpeed = (int) (baseDesignerSpeed);
	static {
		updateDesignerSpeed();
	}

	public static void updateDesignerSpeed() {
		designerSpeed = (int) (baseDesignerSpeed);
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

		if (isPressed(CTRL) && isPressed(SHIFT) && isPressed(MINUS)) {
			// Ctrl + Shift + - results in crashing without saving

			System.exit(0);
		}

		boolean isPlanning = false;

		// PLAYING EXCLUSIVE

		if (gameState == GameState.PLAYING) {
			Planner planner = player.getPlanner();

			if (!planner.isExecuting() && isPressed(PLANNING_TOGGLE)) {
				if (planner.isPlanning()) {
					planner.setPlanning(false);
				} else {
					player.getNewPlanner().setPlanning(true);
				}
			}
			isPlanning = planner.isPlanning();

			/* should be PLANNING_CONFIRM, not ENTER. but this bad bad system
				cannot even comprehend two identical key codes so im using this for now */
			if (isPlanning && !planner.isExecuting() && isPressed(ENTER)) {
				planner.executePlan();
			} else if (isPlanning && isPressed(ENTER)) {
				planner.skipStep();
			}

			for (NumberKey numberKey : NumberKey.values()) {
				if (!isPlanning && isPressed(valueOf(String.valueOf(numberKey)))) {
					player.inv.setSelectedSlot(numberKey.slot);
				}
			}
			if (isPressed(PAUSE)) {
				pauseGame();
			}
			int[] willMovePlayer = {0, 0};
			int playerTilesPerStep = 1;
			if (isPressed(UP)) {
				if (!isPressed(LEFT) || !isPressed(RIGHT)) {
					willMovePlayer[1] -= playerTilesPerStep;
				}
			}
			if (isPressed(DOWN)) {
				if (!isPressed(LEFT) || !isPressed(RIGHT)) {
					willMovePlayer[1] += playerTilesPerStep;
				}
			}
			if (isPressed(LEFT)) {
				if (!isPressed(UP) || !isPressed(DOWN)) {
					willMovePlayer[0] -= playerTilesPerStep;
				}
			}
			if (isPressed(RIGHT)) {
				if (!isPressed(UP) || !isPressed(DOWN)) {
					willMovePlayer[0] += playerTilesPerStep;
				}
			}

			if (!isPlanning && isPressed(DROP)) {
				if (player.inv.hasHeldItem()) {
					if (isPressed(CTRL)) {
						player.inv.dropItem(0, player.inv.getSelectedSlot(), player.inv.getSelectedItem().getCount());
					} else {
						player.inv.dropItem(0, player.inv.getSelectedSlot(), 1);
					}
				}
			}
			if (!isPlanning && isPressed(DODGE)) {
				int delay = 100;
				player.pos.delayedRotate(-360, delay * 5);
				player.setIsInvulnerable(true);
				Timer timer = new Timer(delay, e -> {
					player.setIsInvulnerable(false);
				});
				timer.setRepeats(false); // Ensure the timer only runs once
				timer.start();

			}
			if (isPressed(USE)) {
				pressUseButton(mouseHandler);
				player.keys.keyPressed.put(USE, false);
			}

			// MOVING

			if (!player.attr.isDead() && !planner.isExecuting()) {
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

                    if (isPlanning) {
						Point lastPoint = planner.getEnd();
						int currentX = lastPoint != null ? lastPoint.x : player.getX();
						int currentY = lastPoint != null ? lastPoint.y : player.getY();
                        int targetX = currentX + willMovePlayer[0];
						int targetY = currentY + willMovePlayer[1];
                        if (!isSpaceOccupied(targetX, targetY)) {
                            planner.attemptToMove(new Point(targetX, targetY));
                        }
                    } else {
						int targetX = player.getX() + willMovePlayer[0];
						int targetY = player.getY() + willMovePlayer[1];
                        if (!isSpaceOccupied(targetX, targetY, player)) {
                            BoxManipulation.movePlayer(willMovePlayer[0], willMovePlayer[1]);
                        }
                    }
                }
			}
		}

		// EITHER PLAYING OR INVENTORY

		if (!isPlanning && isPressed(INVENTORY)) {
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
					if (isPressed(valueOf(String.valueOf(numberKey)))) {
						player.inv.setTempItem(player.inv.getItem(hoveredRow, hoveredCol));
						player.inv.bruteSetItem(player.inv.getItem(0, numberKey.slot), hoveredRow, hoveredCol);
						player.inv.bruteSetItem(player.inv.getTempItem(), 0, numberKey.slot);
						player.inv.destroyTempItem();

					}
				}
			}

			if (isPressed(PAUSE)) {
				gameState = GameState.PLAYING;
			}

			if (isPressed(DROP)) {
				if (mouseHandler.getHoveredSlot()[0] >= 0 && mouseHandler.getHoveredSlot()[1] >= 0 && player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]) != null) {
					if (isPressed(CTRL)) {
						player.inv.dropItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1], player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]).getCount());
					} else {
						player.inv.dropItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1], 1);
					}
				} else if (mouseHandler.getExtraHoveredSlot()[0] >= 0 && mouseHandler.getExtraHoveredSlot()[1] >= 0 && player.inv.getItem(mouseHandler.getExtraHoveredSlot()[0] * 2 + mouseHandler.getExtraHoveredSlot()[1]) != null) {
					if (isPressed(CTRL)) {
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

			if (isPressed(DEBUG1)) {
				if (!isPressed(SHIFT)) {
					baseDesignerSpeed = Math.min(3, baseDesignerSpeed + 0.4);
					updateDesignerSpeed();
				} else {
					baseDesignerSpeed = Math.max(0.4, baseDesignerSpeed - 0.4);
					updateDesignerSpeed();
				}
			}

			// Movement

			int[] willMovePlayer = {0, 0};

			if (isPressed(UP)) {
				if (!isPressed(LEFT) || !isPressed(RIGHT)) {
					willMovePlayer[1] -= designerSpeed;
				}
			}
			if (isPressed(DOWN)) {
				if (!isPressed(LEFT) || !isPressed(RIGHT)) {
					willMovePlayer[1] += designerSpeed;
				}
			}
			if (isPressed(LEFT)) {
				if (!isPressed(UP) || !isPressed(DOWN)) {
					willMovePlayer[0] -= designerSpeed;
				}
			}
			if (isPressed(RIGHT)) {
				willMovePlayer[0] += designerSpeed;
			}

			if (willMovePlayer[0] != 0 || willMovePlayer[1] != 0) {
				BoxManipulation.movePlayer(willMovePlayer[0], willMovePlayer[1]);
			}

			// Pause

			if (isPressed(PAUSE)) {
				pauseGame();
			}

			// Zooming

			if (isPressed(EQUAL)) {
				gameScale = Math.min(8, gameScale + 0.25);
				updateTileSize();
			}
			if (isPressed(MINUS)) {
				gameScale = Math.max(0.75, gameScale - 0.25);
				updateTileSize();
			}
			if (isPressed(NUM_0) && isPressed(CTRL)) {
				gameScale = getWindowScale();
			}
			if (isPressed(GRID)) {
				levelDesignerGrid = !levelDesignerGrid;
			}
		}

		// SAVE CREATOR EXCLUSIVE

		if (gameState == GameState.SAVE_CREATOR) {
			if (isPressed(BACKSPACE)) {
				playerName.setLength(Math.max(playerName.length() - 1, 0));
			}
			if (isPressed(ENTER)) {
				confirmName();
			}
		}

		// DIALOG EXCLUSIVE

		if (gameState == GameState.DIALOG) {
			if (isPressed(USE)) {
				if (isLetterDisplayFull()) {
					getCurrentTalkingTo().incrementDialogIndex();
				} else {
					fillLetterDisplay();
				}
			}
		}

		// OTHER MENUS
			// Options, save selector and save creator
		if (gameState == GameState.OPTIONS_MENU || gameState == GameState.SAVE_SELECTOR || gameState == GameState.SAVE_CREATOR) {
			if (isPressed(PAUSE)) {
				if (gameState == GameState.SAVE_SELECTOR && askingToDelete > -1) {
					askingToDelete = -1;
					clearButtons();
				} else {
					goToPreviousMenu();
				}
			}

			if (gameState == GameState.OPTIONS_MENU) {
				if (isPressed(LEFT_MENU) || isPressed(RIGHT_MENU)) {

					if (isPressed(LEFT_MENU)) {
						moveSettingMenu(-1);
						leftKeyIndicatorWidth = 19;
					}
					if (isPressed(RIGHT_MENU)) {
						moveSettingMenu(1);
						rightKeyIndicatorWidth = 19;
					}

				}
			}
		}

		if (isPressed(SCREENSHOT)) {
			BufferedImage ss = getCurrentScreen();
			saveScreenshot(ss);
		}

		// DEBUG KEYS '[' AND ']'

		if (gameState != GameState.LEVEL_DESIGNER) {
			if (isPressed(DEBUG1)) {
//				if (!isPressed(SHIFT)) {
					for (int i = 1; i <= 27; i++) {
						if (i != 5) {
							player.inv.addItem(createItem(i, 1));
						}
					}
//				} else {
//					if (isPressed(CTRL)) {
//						player.attr.setLevel(1);
//					} else {
//						player.attr.addXP(player.attr.getXPtoLevelUp() * 9 / 10);
//					}
//				}


			}
			if (isPressed(DEBUG2)) {

				player.inv.clearInventory();

//				player.setColor(getRandomColor());

//				for (ImageManager.ImageName img : playerImages) {
//					try {
//						Path path = getPath();
//						path = path.resolve(path + "/resources/images/ui/img.png");
//						mergeImages(cachedImages.get(img), ImageIO.read(path.toFile()));
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}

			}
		}

		if (!isPlanning) {
			triggerLogicIfNeeded();
		}
		setKeysToFalse();
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
			player.inv.useItem();
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

	public static boolean isPressed(Key key) {
		try {
			return player.keys.keyPressed.get(key);
		} catch (NullPointerException e) {
			return false;
		}
	}
}
