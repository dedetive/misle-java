package com.ded.misle.world.boxes;

import com.ded.misle.world.entities.HPBox;
import com.ded.misle.world.logic.PhysicsEngine;
import com.ded.misle.world.logic.World;
import com.ded.misle.world.logic.effects.Chest;
import com.ded.misle.world.logic.effects.Collectible;
import com.ded.misle.world.logic.effects.Effect;
import com.ded.misle.world.logic.effects.Spawnpoint;
import com.ded.misle.world.entities.enemies.Enemy;
import com.ded.misle.world.entities.npcs.NPC;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.game.GamePanel.tileSize;
import static com.ded.misle.world.entities.HPBox.clearHPBoxes;
import static com.ded.misle.world.entities.HPBox.getHPBoxes;
import static com.ded.misle.world.entities.enemies.Enemy.clearEnemyBoxes;
import static com.ded.misle.world.entities.enemies.Enemy.getEnemyBoxes;
import static com.ded.misle.world.entities.npcs.NPC.clearNPCs;
import static com.ded.misle.world.entities.npcs.NPC.getInteractableNPCs;

public class BoxHandling {

	private static final List<Box> boxes = new ArrayList<>();
	public static int maxLevel = 19;
	private static final List<Box>[] cachedBoxes = new ArrayList[maxLevel + 1];
	public enum LineAddBoxModes {
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
	public static void addBox(int x, int y, Color color, String texture, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical, Effect effect, double rotation, PhysicsEngine.ObjectType objectType, boolean interactsWithPlayer) {
		boxes.add(new Box(x, y, color, texture, hasCollision, boxScaleHorizontal, boxScaleVertical, effect, rotation, objectType, interactsWithPlayer));
		addBoxToCache(boxes.getLast());
	}

	public static Box addBox(int x, int y) {
		boxes.add(new Box(x, y));
		addBoxToCache(boxes.getLast());
		return boxes.getLast();
	}

	public static Box addBox(BoxPreset preset) {
		Box box = new Box();

		box.setVisualScaleHorizontal(1);
		box.setVisualScaleVertical(1);
		boolean loaded = loadPreset(box, preset);
		if (checkIfPresetHasSides(preset)) {
			box.setTexture(box.textureName + ".");
		}

		if (hasExtra(preset)) {
			String s = preset.toString();
			int index = (s.lastIndexOf("_"));
			char[] p = s.toCharArray();
			p[index] = '@';
			s = String.copyValueOf(p);
			s = s.substring(0, index) + s.substring(index, index + 2).toUpperCase() + s.substring(index + 2).toLowerCase();
			box.setTexture(s);
		}

		if (loaded) {
			boxes.add(box);
			addBoxToCache(box);

			return box;
		} else {
			return null;
		}
	}

	public static HPBox addHPBox(int x, int y) {
		boxes.add(new HPBox(x, y));
		addBoxToCache(boxes.getLast());
		return getHPBoxes().getLast();
	}

	public static Enemy addEnemyBox(int x, int y, Enemy.EnemyType enemyType, double magnification) {
		boxes.add(new Enemy(x, y, enemyType, magnification));
		addBoxToCache(boxes.getLast());
		return getEnemyBoxes().getLast();
	}

	public enum BoxPreset {
		SPAWNPOINT,
		CHEST,
		WALL_DEFAULT,
		WALL_DEFAULT_DECO,
		FLOOR_DEFAULT,
		GRASS_LIGHT,
		GRASS_DARK,
		TRAVEL,

		;
	}

	private static final List<BoxPreset> presetsWithSides = List.of(new BoxPreset[] {
		BoxPreset.WALL_DEFAULT,
		BoxPreset.FLOOR_DEFAULT,
	});

	public static boolean checkIfPresetHasSides(BoxPreset preset) {
		String presetName = preset.toString();
		if (hasExtra(preset)) {
			return presetsWithSides.contains(BoxPreset.valueOf(presetName.substring(0, presetName.indexOf("_DECO"))));
		}
		return presetsWithSides.contains(preset);
	}

	public static boolean hasExtra(BoxPreset preset) {
		BoxPreset[] presetsWithExtra = new BoxPreset[]{
			BoxPreset.WALL_DEFAULT_DECO
		};
		return Arrays.stream(presetsWithExtra).anyMatch(boxPreset -> boxPreset == preset);
	}

	public static Box addBox(int x, int y, BoxPreset preset) {
		boxes.add(new Box(x, y));
		Box box = boxes.getLast();
		loadPreset(box, preset);
		if (hasExtra(preset)) {
			String s = preset.toString();
			int index = (s.lastIndexOf("_"));
			char[] p = s.toCharArray();
			p[index] = '@';
			s = String.copyValueOf(p);
			s = s.substring(0, index) + s.substring(index, index + 2).toUpperCase() + s.substring(index + 2).toLowerCase();
			box.setTexture(s);
		}
		addBoxToCache(box);

		return box;
	}

	public static Box createDummyBox() {
		return new Box();
	}

	public static Box addBoxItem(int x, int y, int id, int count) {
		if (id > 0) {
			Box box = new Box(x, y);
			boxes.add(box);
			box.effect = new Collectible(id, count, true);
			box.setTexture(".." + File.separator + "items" + File.separator + id);
			addBoxToCache(box);
			return box;
		} else {
			Box box = new Box(x, y);
			boxes.add(box);
			box.effect = new Collectible(1, 0, true);
			box.setTexture("invisible");
			return box;
		}
	}

	public static boolean loadPreset(Box box, BoxPreset preset) {
		boolean loaded = true;

		switch (preset) {
			case SPAWNPOINT:
				box.effect = new Spawnpoint(-1);
				box.setTexture("spawnpoint");
				break;
			case BoxPreset.CHEST:
				box.effect = new Chest(0, null);
				box.setCollision(true);
				box.setTexture("chest");
				break;
			case WALL_DEFAULT:
				box.setCollision(true);
				box.setTexture("wall_default");
				break;
			case FLOOR_DEFAULT:
				box.setCollision(false);
				box.setTexture("wall_default");
				break;
			case GRASS_LIGHT:
				box.setCollision(false);
				box.setTexture("grass_light");
				break;
			case GRASS_DARK:
				box.setCollision(false);
				box.setTexture("grass_dark");
				break;
			case TRAVEL:
				box.setCollision(true);
				box.setTexture("invisible");
				break;
			default:
				loaded = false;
		}

		return loaded;
	}

	// Render boxes with camera offset, scale, and tileSize
	public static void renderBoxes(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY) {
//		List<Box> nearbyBoxes;
//        nearbyBoxes = getCachedBoxesNearPlayer(11);

		List<Box> nearbyBoxes = new ArrayList<>();
		World world = player.pos.world;
		for (int k = 0; k < world.layers; k++) {
			for (int i = 0; i < world.width; i++) {
				for (int j = 0; j < world.height; j++) {
					Box box = world.grid[i][j][k];
					if (box != null && !Objects.equals(box.textureName, "invisible")) {
						nearbyBoxes.add(world.grid[i][j][k]);
					}
				}
			}
		}

        for (Box box : nearbyBoxes) {
			box.draw(g2d, cameraOffsetX, cameraOffsetY);
		}
	}

	public static List<Box> getAllBoxes() {
      return new ArrayList<>(boxes);
    }

	public static boolean deleteBox(Box box) {
		World world = player.pos.world;
		world.grid[box.getX()][box.getY()][box.worldLayer] = null;
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
		World world = player.pos.world;
		world.grid = new Box[][][]{};
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
		double scaledX = box.getX();
		double scaledY = box.getY();

		// Calculate bounding box range based on the player's position
		return scaledX + tileSize * box.getVisualScaleHorizontal() / 1.5 >= x - range && scaledX <= x + range
				&& scaledY + tileSize * box.getVisualScaleVertical() / 1.5 >= y - range && scaledY <= y + range;
	}
}
