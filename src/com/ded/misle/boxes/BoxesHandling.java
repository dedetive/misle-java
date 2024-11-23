package com.ded.misle.boxes;

import javax.sound.sampled.Line;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.Launcher.levelDesigner;
import static com.ded.misle.Launcher.scale;

public class BoxesHandling {

	private static final List<Box> boxes = new ArrayList<>();
	private static final List<String> presetsWithSides = List.of(new String[]{"wallDefault"});
	private static final double boxBaseSize = 20.9;
	public static int maxLevel = 19;
	private static List<Box>[] cachedBoxes = new ArrayList[maxLevel + 1];
	public static enum LineAddBoxMode {
		HOLLOW,
		FILL
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
	public static void addBox(double x, double y, Color color, String texture, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical, String[] effect, double rotation) {
		boxes.add(new Box(x, y, color, texture, hasCollision, boxScaleHorizontal, boxScaleVertical, effect, rotation));
		addBoxToCache(boxes.getLast());
	}

	public static void addBox(double x, double y) {
		boxes.add(new Box(x, y));
		addBoxToCache(boxes.getLast());
	}

	/**
	 *
	 * Valid presets are:<br>
	 * - spawnpoint<br>
	 * - mountainChest<br>
	 * - wallDefault<br>
	 * - wallDefault@Deco<br>
	 * - grass
	 *
	 */
	public static void addBox(double x, double y, String preset) {
		boxes.add(new Box(x, y));
		loadPreset(boxes.getLast(), preset);
		if (checkIfPresetHasSides(preset)) {
			editLastBox("texture", preset + "0");
		}
		addBoxToCache(boxes.getLast());
	}

	public static Box addBoxItem(double x, double y, int id, int count) {
		boxes.add(new Box(x, y));
		editLastBox("effect", "{item, " + id + ", " + count + ", true}");
		editLastBox("texture", (".." + File.separator + "items" + File.separator + id));
		addBoxToCache(boxes.getLast());
		return boxes.getLast();
	}

	public static int lineCoordinatedAddBox(double startX, double startY, int boxesX, int boxesY, String preset, LineAddBoxMode mode) {
		return lineAddBox(startX * boxBaseSize, startY * boxBaseSize, boxesX, boxesY, preset, mode);
	}

	public static int lineCoordinatedAddScaledBox(double startX, double startY, int boxesX, int boxesY, String preset, String mode, double scale) {
		return lineAddScaledBox(startX * boxBaseSize, startY * boxBaseSize, boxesX, boxesY, mode, scale);
	}

	public static int lineAddBox(double startX, double startY, int boxesX, int boxesY, String preset, LineAddBoxMode mode) {
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

								editLastBox("texture", preset + openSides);
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

							editLastBox("texture", preset + openSides);
						}

						Counter++;
						break;
				}
			}
		}
		return Counter;
	}

	public static int lineAddScaledBox(double startX, double startY, int boxesX, int boxesY, String mode, double boxScale) {
		int Counter = 0;
		for (int i = 0; i < boxesX; i++) {
			for (int j = 0; j < boxesY; j++) {
				switch (mode) {
					case "hollow":
						if ((i == 0 || i == boxesX - 1) || (j == 0 || j == boxesY - 1)) {
							boxes.add(new Box(startX + i * boxBaseSize * boxScale, startY + j * boxBaseSize * boxScale));
							addBoxToCache(boxes.getLast());
							editLastBox("boxScaleHorizontal", String.valueOf(boxScale));
							editLastBox("boxScaleVertical", String.valueOf(boxScale));
							Counter++;
						}
						break;
					case "fill":
					default:
						boxes.add(new Box(startX + i * boxBaseSize * boxScale, startY + j * boxBaseSize * boxScale));
						addBoxToCache(boxes.getLast());
						editLastBox("boxScaleHorizontal", String.valueOf(boxScale));
						editLastBox("boxScaleVertical", String.valueOf(boxScale));
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
				editBox(box,"effect", "{spawnpoint, -1}");
				editBox(box,"color", "0xF0F05A");
				editBox(box,"texture", "spawnpoint");
				break;
			case "mountainChest":
				editBox(box,"effect", "{chest, 3, mountain}");
				editBox(box,"hasCollision", "true");
				editBox(box,"texture", "chest");
				break;
			case "wallDefault":
				editBox(box,"hasCollision", "true");
				editBox(box,"color", "0x606060");
				editBox(box,"texture", "wallDefault");
				break;
			case "grass":
				editBox(box,"hasCollision", "false");
				editBox(box,"color", "0x1EA81E");
				break;
		}
	}

	public static void editBox(Box box, String key, String value) {
		switch (key) {
			case "x":
				box.setCurrentX(Double.parseDouble(value));
				break;
			case "y":
				box.setCurrentY(Double.parseDouble(value));
				break;
			case "color":
				box.setColor(Color.decode(value));
				break;
			case "texture":
				box.setTexture(value);
				break;
			case "hasCollision":
				box.setHasCollision(Boolean.parseBoolean(value));
				break;
			case "boxScaleHorizontal":
				box.setBoxScaleHorizontal(Double.parseDouble(value));
				break;
			case "boxScaleVertical":
				box.setBoxScaleVertical(Double.parseDouble(value));
				break;
			case "effect":
				value = value.replaceAll("[{}]", "");
				String[] effectArray = value.split(",\\s*");
				box.setEffect(effectArray);
				break;
			case "collectible":
				box.setEffectReason(value);
				break;
			case "rotation":
				box.setRotation(Double.parseDouble(value));
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
	public static void editLastBox(String key, String value) {
		editBox(boxes.getLast(), key, value);
	}

	public static void editLastBox(String key, String value, int boxCount) {
		for (int i = 1; i < boxCount + 1; i++) {
			editBox(boxes.get(boxes.size() - i), key, value);
		}
	}

	/**
	 *
	 * Edits the last X box, with X being the input negativeIndex. E.g.: If 1, edit the last box added.
	 *
	 */
	public static void editBoxNegativeIndex(String key, String value, int negativeIndex) {
		editBox(boxes.get(boxes.size() - negativeIndex), key, value);
	}

	// Render boxes with camera offset, scale, and tileSize
	public static void renderBoxes(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY, double gameScale, int tileSize) {
		List<Box> nearbyBoxes;
		if (!levelDesigner) {
			nearbyBoxes = getCachedBoxesNearPlayer(11);
		} else {
			nearbyBoxes = getCachedBoxesNearPlayer(12);
		}
		for (Box box : nearbyBoxes) {
			box.draw(g2d, cameraOffsetX, cameraOffsetY, gameScale, tileSize, box.getBoxScaleHorizontal(), box.getBoxScaleVertical());
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
		boxes.clear();
	}

	public static int getBoxesCount() {
		return boxes.size();
	}

	/**
	 *
	 * DEPRECATED. Use getCachedBoxes() instead.
	 *
	 */
	public static List<Box> getBoxesInRange(double playerX, double playerY, double range, double scale, int tileSize) {
    	List<Box> boxesInRange = new ArrayList<>();
    	for (Box box : boxes) {
        	double scaledX = box.getCurrentX() * scale;
        	double scaledY = box.getCurrentY() * scale;

        	// Calculate bounding box range based on the player's position
		    if (scaledX + tileSize * box.getBoxScaleHorizontal() / 1.5 >= playerX - range && scaledX <= playerX + range
				    && scaledY + tileSize * box.getBoxScaleVertical() / 1.5 >= playerY - range && scaledY <= playerY + range) {
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

	public static void storeCachedBoxes(int level) {
		if (level >= maxLevel) {
			cachedBoxes[maxLevel] = getBoxesInRange(player.pos.getX(), player.pos.getY(), Math.pow(2, level), scale, tileSize);
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
		double playerX = player.pos.getX();
		double playerY = player.pos.getY();
		double range = Math.pow(2, level);
		for (Box box : cachedBoxes[level + 1]) {
			double scaledX = box.getCurrentX() * scale;
			double scaledY = box.getCurrentY() * scale;

			if (scaledX + tileSize * box.getBoxScaleHorizontal() / 1.5 >= playerX - range && scaledX <= playerX + range
					&& scaledY + tileSize * box.getBoxScaleVertical() / 1.5 >= playerY - range && scaledY <= playerY + range) {
				boxesInRange.add(box);
			}
		}
		return boxesInRange;
	}

	public static List<Box> getCachedBoxesInRange(int x, int y, int level) {
		List<Box> boxesInRange = new ArrayList<>();
		double range = Math.pow(2, level);
		for (Box box : cachedBoxes[level + 1]) {
			double scaledX = box.getCurrentX() * scale;
			double scaledY = box.getCurrentY() * scale;

			if (scaledX + tileSize * box.getBoxScaleHorizontal() / 1.5 >= x - range && scaledX <= x + range
					&& scaledY + tileSize * box.getBoxScaleVertical() / 1.5 >= y - range && scaledY <= y + range) {
				boxesInRange.add(box);
			}
		}
		return boxesInRange;
	}

		// END BOX CACHING

	public static List<Box> getCollisionBoxesInRange(double playerX, double playerY, double range, double scale, int tileSize, int level) {
    	List<Box> boxesInRange = new ArrayList<>();
    	for (Box box : getCachedBoxes(level)) {
    			if (!box.getHasCollision()) {
    				continue;
    			}
        	double scaledX = box.getCurrentX() * scale;
        	double scaledY = box.getCurrentY() * scale;

        	// Calculate bounding box range based on the player's position
		    if (scaledX + tileSize * box.getBoxScaleHorizontal() / 1.5 >= playerX - range && scaledX <= playerX + range
				    && scaledY + tileSize * box.getBoxScaleVertical() / 1.5 >= playerY - range && scaledY <= playerY + range) {
			    boxesInRange.add(box);
		    }
    	}
    	return boxesInRange;
	}

	public static List<Box> getNonCollisionBoxesInRange(double playerX, double playerY, double range, double scale, int tileSize) {
		List<Box> boxesInRange = new ArrayList<>();
		for (Box box : getCachedBoxes(9)) {
			if (box.getHasCollision()) {
				continue;
			}
			double scaledX = box.getCurrentX() * scale;
			double scaledY = box.getCurrentY() * scale;
			// Calculate bounding box range based on the player's position
			if (scaledX + tileSize * box.getBoxScaleHorizontal() / 1.5 >= playerX - range && scaledX <= playerX + range
					&& scaledY + tileSize * box.getBoxScaleVertical() / 1.5 >= playerY - range && scaledY <= playerY + range) {
				boxesInRange.add(box);
			}
		}
		return boxesInRange;
	}
}
