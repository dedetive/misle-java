package com.ded.misle.world.boxes;

import com.ded.misle.core.PhysicsEngine;
import com.ded.misle.world.enemies.Enemy;
import com.ded.misle.world.npcs.NPC;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.tileSize;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.world.boxes.HPBox.clearHPBoxes;
import static com.ded.misle.world.boxes.HPBox.getHPBoxes;
import static com.ded.misle.world.enemies.Enemy.clearEnemyBoxes;
import static com.ded.misle.world.enemies.Enemy.getEnemyBoxes;
import static com.ded.misle.world.npcs.NPC.clearNPCs;
import static com.ded.misle.world.npcs.NPC.getInteractableNPCs;

public class BoxHandling {

	private static final List<Box> boxes = new ArrayList<>();
	private static final List<String> presetsWithSides = List.of(new String[]{"wall_default"});
	private static final double boxBaseSize = 20.9;
	public static int maxLevel = 19;
	private static final List<Box>[] cachedBoxes = new ArrayList[maxLevel + 1];
	public enum LineAddBoxModes {
		HOLLOW,
		FILL
	}

	public enum EditBoxKeys {
		X,
		Y,
		COLOR,
		TEXTURE,
		HAS_COLLISION,
		BOX_SCALE_HORIZONTAL,
		BOX_SCALE_VERTICAL,
		EFFECT,
		COLLECTIBLE,
		ROTATION,
		INTERACTS_WITH_PLAYER
	}

	static {
		for (int i = 0; i < cachedBoxes.length; i++) {
			cachedBoxes[i] = new ArrayList<>();
		}
	}

	/**
	 *
	 *  ...
	 *
	 */
	public static void addBox(double x, double y, Color color, String texture, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical, String[] effect, double rotation, PhysicsEngine.ObjectType objectType, boolean interactsWithPlayer) {
		boxes.add(new Box(x, y, color, texture, hasCollision, boxScaleHorizontal, boxScaleVertical, effect, rotation, objectType, interactsWithPlayer));
		addBoxToCache(boxes.getLast());
	}

	public static Box addBox(double x, double y) {
		boxes.add(new Box(x, y));
		addBoxToCache(boxes.getLast());
		return boxes.getLast();
	}

	public static HPBox addHPBox(double x, double y) {
		boxes.add(new HPBox(x, y));
		addBoxToCache(boxes.getLast());
		return getHPBoxes().getLast();
	}

	public static Enemy addEnemyBox(double x, double y, Enemy.EnemyType enemyType, double magnification) {
		boxes.add(new Enemy(x, y, enemyType, magnification));
		addBoxToCache(boxes.getLast());
		return getEnemyBoxes().getLast();
	}

	/**
	 *
	 * Valid presets are:<br>
	 * - spawnpoint<br>
	 * - mountain_chest<br>
	 * - wall_default<br>
	 * - wall_default@Deco<br>
	 * - grass
	 * - travel
	 *
	 */
	public static void addBox(double x, double y, String preset) {
		boxes.add(new Box(x, y));
		loadPreset(boxes.getLast(), preset);
		if (checkIfPresetHasSides(preset)) {
			editLastBox(EditBoxKeys.TEXTURE, preset + "0");
		}
		addBoxToCache(boxes.getLast());
	}

	public static Box addBoxItem(double x, double y, int id, int count) {
		if (id > 0) {
			boxes.add(new Box(x, y));
			editLastBox(EditBoxKeys.EFFECT, "{item, " + id + ", " + count + ", true}");
			editLastBox(EditBoxKeys.TEXTURE, (".." + File.separator + "items" + File.separator + id));
			addBoxToCache(boxes.getLast());
			return boxes.getLast();
		} else {
			boxes.add(new Box(x, y));
			editLastBox(EditBoxKeys.EFFECT, "{item, " + 1 + ", " + 0 + ", false}");
			editLastBox(EditBoxKeys.TEXTURE, ("invisible"));
			addBoxToCache(boxes.getLast());
			return boxes.getLast();
		}
	}

	public static int lineCoordinatedAddBox(double startX, double startY, int boxesX, int boxesY, String preset, LineAddBoxModes mode) {
		return lineAddBox(startX * boxBaseSize, startY * boxBaseSize, boxesX, boxesY, preset, mode);
	}

	public static int lineCoordinatedAddScaledBox(double startX, double startY, int boxesX, int boxesY, String mode, double scale, String preset) {
		return lineAddScaledBox(startX * boxBaseSize, startY * boxBaseSize, boxesX, boxesY, mode, scale, preset);
	}

	public static int lineAddBox(double startX, double startY, int boxesX, int boxesY, String preset, LineAddBoxModes mode) {
		int Counter = 0;
		for (int i = 0; i < boxesX; i++) {
			for (int j = 0; j < boxesY; j++) {
				switch (mode) {
					case HOLLOW:
						if ((i == 0 || i == boxesX - 1) || (j == 0 || j == boxesY - 1)) {
							boxes.add(new Box(startX + i * boxBaseSize, startY + j * boxBaseSize));
							addBoxToCache(boxes.getLast());
							if (preset.contains("@")) { loadPreset(boxes.getLast(), preset.substring(0, preset.indexOf("@"))); }
							else { loadPreset(boxes.getLast(), preset); }

							// For wall corner detection

							if (checkIfPresetHasSides(preset)) {
								String openSides;
								if (boxesX == 1 && boxesY == 1) {
									openSides = ".WASD..@";
								} else if (boxesX == 1 && j == 0) {
									openSides = ".WAD..@";
								} else if (boxesX == 1 && j == boxesY - 1) {
									openSides = ".ASD..@";
								} else if (boxesY == 1 && i == 0) {
									openSides = ".WAS..@";
								} else if (boxesY == 1 && i == boxesX - 1) {
									openSides = ".WSD..@";
								} else if (boxesX == 1) {
									openSides = ".AD";
								} else if (boxesY == 1) {
									openSides = ".WS";
								} else if (i == 0 && j == 0) {
									openSides = ".AW.S.@"; // Left-up corner
								} else if (i == 0 && j == boxesY - 1) {
									openSides = ".AS.D.@"; // Left-down corner
								} else if (i == boxesX - 1 && j == 0) {
									openSides = ".WD.A.@"; // Right-up corner
								} else if (i == boxesX - 1 && j == boxesY - 1) {
									openSides = ".SD.W.@"; // Right-down corner
								} else if (i == 0 || i == boxesX - 1) {
									openSides = ".AD";
								} else if (j == 0 || j == boxesY - 1) {
									openSides = ".WS";
								} else {
									openSides = "";
								}

								editLastBox(EditBoxKeys.TEXTURE, preset + openSides);
							}

							Counter++;
						}
						break;
					case FILL:
					default:
						boxes.add(new Box(startX + i * boxBaseSize, startY + j * boxBaseSize));
						addBoxToCache(boxes.getLast());
						if (preset.contains("@")) { loadPreset(boxes.getLast(), preset.substring(0, preset.indexOf("@"))); }
						else { loadPreset(boxes.getLast(), preset); }

						if (checkIfPresetHasSides(preset)) {
							String openSides;
							if (boxesX == 1 && boxesY == 1) {
								openSides = ".WASD..@";
							} else if (boxesX == 1 && j == 0) {
								openSides = ".WAD..@";
							} else if (boxesX == 1 && j == boxesY - 1) {
								openSides = ".ASD..@";
							} else if (boxesY == 1 && i == 0) {
								openSides = ".WAS..@";
							} else if (boxesY == 1 && i == boxesX - 1) {
								openSides = ".WSD..@";
							} else if (boxesX == 1) {
								openSides = ".AD";
							} else if (boxesY == 1) {
								openSides = ".WS";
							} else if (i == 0 && j == 0) {
								openSides = ".AW..@"; // Left-up corner
							} else if (i == 0 && j == boxesY - 1) {
								openSides = ".AS..@"; // Left-down corner
							} else if (i == boxesX - 1 && j == 0) {
								openSides = ".WD..@"; // Right-up corner
							} else if (i == boxesX - 1 && j == boxesY - 1) {
								openSides = ".SD..@"; // Right-down corner
							} else if (i == 0) {
								openSides = ".A";
							}  else if (i == boxesX - 1) {
								openSides = ".D";
							} else if (j == 0) {
								openSides = ".W";
							} else if (j == boxesY - 1) {
								openSides = ".S";
							} else {
								openSides = "";
							}

							editLastBox(EditBoxKeys.TEXTURE, preset + openSides);
						}

						Counter++;
						break;
				}
			}
		}
		return Counter;
	}

	public static int lineAddScaledBox(double startX, double startY, int boxesX, int boxesY, String mode, double boxScale, String preset) {
		int Counter = 0;
		for (int i = 0; i < boxesX; i++) {
			for (int j = 0; j < boxesY; j++) {
				switch (mode) {
					case "hollow":
						if ((i == 0 || i == boxesX - 1) || (j == 0 || j == boxesY - 1)) {
							boxes.add(new Box(startX + i * boxBaseSize * boxScale, startY + j * boxBaseSize * boxScale));
							addBoxToCache(boxes.getLast());
							editLastBox(EditBoxKeys.BOX_SCALE_HORIZONTAL, String.valueOf(boxScale));
							editLastBox(EditBoxKeys.BOX_SCALE_VERTICAL, String.valueOf(boxScale));
							loadPreset(boxes.getLast(), preset);
							Counter++;
						}
						break;
					case "fill":
					default:
						boxes.add(new Box(startX + i * boxBaseSize * (boxScale - 0.05), startY + j * boxBaseSize * (boxScale - 0.05)));
						addBoxToCache(boxes.getLast());
						editLastBox(EditBoxKeys.BOX_SCALE_HORIZONTAL, String.valueOf(boxScale));
						editLastBox(EditBoxKeys.BOX_SCALE_VERTICAL, String.valueOf(boxScale));
						loadPreset(boxes.getLast(), preset);
						Counter++;
						break;
				}
			}
		}
		return Counter;
	}

	public static boolean checkIfPresetHasSides(String preset) {
		if (preset.contains("@")) {
			return presetsWithSides.contains(preset.substring(0, preset.indexOf("@")));
		}
		return presetsWithSides.contains(preset);
	}

	private static void loadPreset(Box box, String preset) {
		switch (preset) {
			case "spawnpoint":
				editBox(box, EditBoxKeys.EFFECT, "{spawnpoint, -1}");
				editBox(box, EditBoxKeys.TEXTURE, "spawnpoint");
				break;
			case "mountain_chest":
				editBox(box, EditBoxKeys.EFFECT, "{chest, 3, mountain}");
				editBox(box, EditBoxKeys.HAS_COLLISION, "true");
				editBox(box, EditBoxKeys.TEXTURE, "chest");
				break;
			case "wall_default":
				editBox(box, EditBoxKeys.HAS_COLLISION, "true");
				editBox(box, EditBoxKeys.TEXTURE, "wall_default");
				break;
			case "grass":
				editBox(box, EditBoxKeys.HAS_COLLISION, "false");
				editBox(box, EditBoxKeys.TEXTURE, "grass");
				break;
			case "travel":
				editBox(box, EditBoxKeys.HAS_COLLISION, "true");
				editBox(box, EditBoxKeys.TEXTURE, "invisible");
				// Should also manually add room ID, X and Y positions (in this order)
				// Effect should look like: {travel, 1, 500, 20}
				break;
		}
	}

	public static void editBox(Box box, EditBoxKeys key, String value) {
		switch (key) {
			case X:
				box.setX(Double.parseDouble(value));
				break;
			case Y:
				box.setY(Double.parseDouble(value));
				break;
			case COLOR:
				box.setColor(Color.decode(value));
				break;
			case TEXTURE:
				box.setTexture(value);
				break;
			case HAS_COLLISION:
				box.setHasCollision(Boolean.parseBoolean(value));
				break;
			case BOX_SCALE_HORIZONTAL:
				box.setBoxScaleHorizontal(Double.parseDouble(value));
				break;
			case BOX_SCALE_VERTICAL:
				box.setBoxScaleVertical(Double.parseDouble(value));
				break;
			case EFFECT:
				value = value.replaceAll("[{}]", "");
				String[] effectArray = value.split(",\\s*");
				box.setEffect(effectArray);
				break;
			case COLLECTIBLE:
				box.setEffectReason(value);
				break;
			case ROTATION:
				box.setRotation(Double.parseDouble(value));
				break;
			case INTERACTS_WITH_PLAYER:
				box.setInteractsWithPlayer(Boolean.parseBoolean(value));
				break;
		}
	}

	/**
	 *
	 * Effect needs to be in the expected format of effects. For example: <br>
	 * <br>
	 * editLastBox("effect","{damage, 10, 350, normal, {''}}");
	 *
	 * @param key name of the parameter to be edited. The key can be either x, y, color, hasCollision, boxScaleHorizontal, boxScaleVertical and effect
	 * @param value value to be changed to
	 */
	public static void editLastBox(EditBoxKeys key, String value) {
		editBox(boxes.getLast(), key, value);
	}

	public static void editLastBox(EditBoxKeys key, String value, int boxCount) {
		for (int i = 1; i < boxCount + 1; i++) {
			editBox(boxes.get(boxes.size() - i), key, value);
		}
	}

	/**
	 *
	 * Edits the last X box, with X being the input negativeIndex. E.g.: If 1, edit the last box added.
	 *
	 */
	public static void editBoxNegativeIndex(EditBoxKeys key, String value, int negativeIndex) {
		editBox(boxes.get(boxes.size() - negativeIndex), key, value);
	}

	// Render boxes with camera offset, scale, and tileSize
	public static void renderBoxes(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY) {
		List<Box> nearbyBoxes;
        nearbyBoxes = getCachedBoxesNearPlayer(11);

        for (Box box : nearbyBoxes) {
			box.draw(g2d, cameraOffsetX, cameraOffsetY, box.getBoxScaleHorizontal(), box.getBoxScaleVertical());
		}
	}

	public static List<Box> getAllBoxes() {
      return new ArrayList<>(boxes);
    }

	public static boolean deleteBox(Box box) {
		removeBoxFromCache(box);
		return boxes.remove(box);
	}

	/**
	 *
	 * Removes the last X box, with X being the input negativeIndex. E.g.: If 1, remove the last box added. If 7, removes the 7th most recent box added.
	 *
	 */
	public static void deleteBox(int negativeIndex) {
		deleteBox(boxes.get(boxes.size() - negativeIndex));
	}

	/**
	 *
	 * Removes the last X box, with X being the input boxNegativeIndex, Count times. E.g.: If 1 and 2, remove the last and the second to last boxes added.
	 *
	 */
	public static void deleteBox(int boxNegativeIndex, int Count) {
		for (int i = 0; i < Count; i++) {
			deleteBox(boxNegativeIndex);
		}
	}

	public static Box getLastBox() {
		return boxes.getLast();
	}

	public static void clearAllBoxes() {
		clearEntireCache();
		boxes.clear();
		clearHPBoxes();
		clearEnemyBoxes();
		clearNPCs();
	}

	public static int getBoxesCount() {
		return boxes.size();
	}

	/**
	 *
	 * DEPRECATED. Use getCachedBoxes() instead.
	 *
	 */
	public static List<Box> getBoxesInRange(double x, double y, double range) {
    	List<Box> boxesInRange = new ArrayList<>();
    	for (Box box : boxes) {
		    if (checkIfBoxInRange(box, x, y, range)) {
			    boxesInRange.add(box);
		    }
    	}
    	return boxesInRange;
	}

		// BOX CACHING

	// There are 16 levels. Each of them is a power of 2, starting at 2^0 (1) and ending at 2^15 (32768).
	// They represent the steps the player may take, and will only update if the player takes steps.
	// Each level has 2 times the radius of the previous level, thus always storing equal or more boxes than the previous levels.
	// Only the highest level, 15, uses getBoxesInRange() method, which checks for every loaded box instead of only the nearby ones.

	public static List<Box> getCachedBoxes(int level) {
		return cachedBoxes[level];
	}

	public static void clearEntireCache() {
		for (int level = maxLevel; level > 0; level--) {
			cachedBoxes[level].clear();
		}
	}

	public static void storeCachedBoxes(int level) {
		if (level >= maxLevel) {
			cachedBoxes[maxLevel] = getBoxesInRange(player.getX(), player.getY(), Math.pow(2, level));
		} else {
			cachedBoxes[level] = getCachedBoxesNearPlayer(level);
		}
	}

	public static void addBoxToCache(Box box) {
		for (int level = maxLevel; level > 0; level--) {
			cachedBoxes[level].add(box);
		}
	}

	public static void removeBoxFromCache(Box box) {
		for (int level = maxLevel; level > 0; level--) {
			cachedBoxes[level].remove(box);
		}
	}

	public static List<Box> getCachedBoxesNearPlayer(int level) {
		List<Box> boxesInRange = new ArrayList<>();
		double playerX = player.getX();
		double playerY = player.getY();
		double range = Math.pow(2, level);
		for (Box box : cachedBoxes[level + 1]) {
			if (checkIfBoxInRange(box, playerX, playerY, range)) {
				boxesInRange.add(box);
			}
		}
		return boxesInRange;
	}

	public static List<Box> getCachedBoxesInRange(int x, int y, int level) {
		List<Box> boxesInRange = new ArrayList<>();
		double range = Math.pow(2, level);
		for (Box box : cachedBoxes[level + 1]) {
			if (checkIfBoxInRange(box, x, y, range)) {
				boxesInRange.add(box);
			}
		}
		return boxesInRange;
	}

		// END BOX CACHING

	public static List<Box> getCollisionBoxesInRange(double x, double y, double range, int level) {
    	List<Box> boxesInRange = new ArrayList<>();
    	for (Box box : getCachedBoxes(level)) {
            if (!box.getHasCollision()) continue;

		    if (checkIfBoxInRange(box, x, y, range)) {
			    boxesInRange.add(box);
		    }
    	}
    	return boxesInRange;
	}

	public static List<Box> getNonCollisionBoxesInRange(double x, double y, double range) {
		List<Box> boxesInRange = new ArrayList<>();
		for (Box box : getCachedBoxes(9)) {
			if (box.getHasCollision()) continue;

			if (checkIfBoxInRange(box, x, y, range)) {
				boxesInRange.add(box);
			}
		}
		return boxesInRange;
	}

	public static List<NPC> getNPCsInRange(double x, double y, double range) {
		try {
			List<NPC> npcsInRange = new ArrayList<>();
			for (Box box : getCachedBoxes(9)) {
				if (box.getObjectType() != PhysicsEngine.ObjectType.NPC) continue;

				if (checkIfBoxInRange(box, x, y, range)) {
					npcsInRange.add((NPC) box);
				}
			}
			return npcsInRange;
		} catch (ConcurrentModificationException e) {
			return new ArrayList<>();
		}
	}

	public static List<NPC> getInteractableNPCsInRange(double x, double y, double range) {
		List<NPC> npcsInRange = getNPCsInRange(x, y, range);

		// Intersection of npcsInRange and interactableNPCs
        return npcsInRange.stream().filter(getInteractableNPCs()::contains).toList();
	}

	public static List<HPBox> getHPBoxesInRange(double x, double y, double range) {
		List<HPBox> boxesInRange = new ArrayList<>();
		for (Box box : getCachedBoxes(8)) {
			if (!(box instanceof HPBox)) continue;

			if (checkIfBoxInRange(box, x, y, range)) {
				boxesInRange.add((HPBox) box);
			}
		}
		return boxesInRange;
	}

	public static boolean checkIfBoxInRange(Box box, double x, double y, double range) {
		double scaledX = box.getX() * scale;
		double scaledY = box.getY() * scale;

		// Calculate bounding box range based on the player's position
		return scaledX + tileSize * box.getBoxScaleHorizontal() / 1.5 >= x - range && scaledX <= x + range
				&& scaledY + tileSize * box.getBoxScaleVertical() / 1.5 >= y - range && scaledY <= y + range;
	}
}
