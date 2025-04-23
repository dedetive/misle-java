package com.ded.misle.world.boxes;

import com.ded.misle.core.PhysicsEngine;
import com.ded.misle.world.World;
import com.ded.misle.items.DropTable;
import com.ded.misle.world.enemies.Enemy;
import com.ded.misle.world.npcs.NPC;

import java.awt.*;
import java.io.File;
import java.util.*;
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

		box.setBoxScaleHorizontal(1);
		box.setBoxScaleVertical(1);
		boolean loaded = loadPreset(box, preset);
		if (checkIfPresetHasSides(preset)) {
			editBox(box, EditBoxKeys.TEXTURE, box.textureName + ".");
		}

		if (hasExtra(preset)) {
			String s = preset.toString();
			int index = (s.lastIndexOf("_"));
			char[] p = s.toCharArray();
			p[index] = '@';
			s = String.copyValueOf(p);
			s = s.substring(0, index) + s.substring(index, index + 2).toUpperCase() + s.substring(index + 2).toLowerCase();
			editLastBox(EditBoxKeys.TEXTURE, s);
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
		GRASS,
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
			editLastBox(EditBoxKeys.TEXTURE, s);
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
			box.effect = new Effect.Collectible(id, count, true);
			editLastBox(EditBoxKeys.TEXTURE, (".." + File.separator + "items" + File.separator + id));
			addBoxToCache(boxes.getLast());
			return boxes.getLast();
		} else {
			Box box = new Box(x, y);
			boxes.add(box);
			box.effect = new Effect.Collectible(1, 0, true);
			editLastBox(EditBoxKeys.TEXTURE, ("invisible"));
			addBoxToCache(boxes.getLast());
			return boxes.getLast();
		}
	}

	public static boolean loadPreset(Box box, BoxPreset preset) {
		boolean loaded = true;

		switch (preset) {
			case SPAWNPOINT:
				box.effect = new Effect.Spawnpoint(-1);
				editBox(box, EditBoxKeys.TEXTURE, "spawnpoint");
				break;
			case BoxPreset.CHEST:
				box.effect = new Effect.Chest(0, null);
				editBox(box, EditBoxKeys.HAS_COLLISION, "true");
				editBox(box, EditBoxKeys.TEXTURE, "chest");
				break;
			case WALL_DEFAULT:
				editBox(box, EditBoxKeys.HAS_COLLISION, "true");
				editBox(box, EditBoxKeys.TEXTURE, "wall_default");
				break;
			case FLOOR_DEFAULT:
				editBox(box, EditBoxKeys.HAS_COLLISION, "false");
				editBox(box, EditBoxKeys.TEXTURE, "wall_default");
				break;
			case GRASS:
				editBox(box, EditBoxKeys.HAS_COLLISION, "false");
				editBox(box, EditBoxKeys.TEXTURE, "grass");
				break;
			case TRAVEL:
				editBox(box, EditBoxKeys.HAS_COLLISION, "true");
				editBox(box, EditBoxKeys.TEXTURE, "invisible");
				break;
			default:
				loaded = false;
		}

		return loaded;
	}

	// TODO: might remove this later idk
	@Deprecated(forRemoval = true)
	public static void editBox(Box box, EditBoxKeys key, String value) {
		switch (key) {
			case X:
				box.setX(Integer.parseInt(value));
				break;
			case Y:
				box.setY(Integer.parseInt(value));
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
			case ROTATION:
				box.setVisualRotation(Double.parseDouble(value));
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
//		List<Box> nearbyBoxes;
//        nearbyBoxes = getCachedBoxesNearPlayer(11);

		List<Box> nearbyBoxes = new ArrayList<>();
		World world = player.pos.world;
		for (int i = 0; i < world.width; i++) {
			for (int j = 0; j < world.height; j++) {
				for (int k = 0; k < world.layers; k++) {
					Box box = world.grid[i][j][k];
					if (box != null && !Objects.equals(box.textureName, "invisible")) {
						nearbyBoxes.add(world.grid[i][j][k]);
					}
				}
			}
		}

        for (Box box : nearbyBoxes) {
			box.draw(g2d, cameraOffsetX, cameraOffsetY, box.getBoxScaleHorizontal(), box.getBoxScaleVertical());
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
		double scaledX = box.getX() * scale;
		double scaledY = box.getY() * scale;

		// Calculate bounding box range based on the player's position
		return scaledX + tileSize * box.getBoxScaleHorizontal() / 1.5 >= x - range && scaledX <= x + range
				&& scaledY + tileSize * box.getBoxScaleVertical() / 1.5 >= y - range && scaledY <= y + range;
	}
}
