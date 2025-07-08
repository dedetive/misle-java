package com.ded.misle.world.boxes;

import com.ded.misle.world.entities.enemies.EnemyRegistry;
import com.ded.misle.world.entities.enemies.EnemyType;
import com.ded.misle.world.logic.World;
import com.ded.misle.world.logic.effects.Chest;
import com.ded.misle.world.logic.effects.Collectible;
import com.ded.misle.world.logic.effects.Spawnpoint;
import com.ded.misle.world.entities.enemies.Enemy;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.entities.Entity.clearEntities;
import static com.ded.misle.world.entities.npcs.NPC.clearNPCs;

public class BoxHandling {

	private static final List<Box> boxes = new ArrayList<>();

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

			return box;
		} else {
			return null;
		}
	}

	public static Enemy addEnemyBox(Point pos, EnemyType enemyType, double magnification) {
		boxes.add(new Enemy(pos, enemyType, magnification));
		return EnemyRegistry.all().getLast();
	}

	public enum BoxPreset {
		SPAWNPOINT,
		CHEST,
		STONE_BRICK_WALL,
		WALL_DEFAULT_DECO,
		FLOOR_DEFAULT,
		GRASS_LIGHT,
		GRASS_DARK,
		TRAVEL,

		;
	}

	private static final List<BoxPreset> presetsWithSides = List.of(new BoxPreset[] {
		BoxPreset.STONE_BRICK_WALL,
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

	public static Box createDummyBox() {
		return new Box();
	}

	public static Box addBoxItem(int x, int y, int id, int count) {
		if (id > 0) {
			Box box = new Box(x, y);
			boxes.add(box);
			box.effect = new Collectible(id, count, true);
			box.setTexture(".." + File.separator + "items" + File.separator + id);
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
			case STONE_BRICK_WALL:
				box.setCollision(true);
				box.setTexture("stone_brick_wall");
				break;
			case FLOOR_DEFAULT:
				box.setCollision(false);
				box.setTexture("stone_brick_wall");
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

						box.updateVisualOffset(10f);

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
		clearEntities();
		EnemyRegistry.clear();
		clearNPCs();
		World world = player.pos.world;
		world.grid = new Box[][][]{};
	}

	public static int getBoxesCount() {
		return boxes.size();
	}
}
