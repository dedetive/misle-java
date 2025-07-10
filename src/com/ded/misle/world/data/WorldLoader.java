package com.ded.misle.world.data;

import com.ded.misle.world.data.entity.configurations.EnemyType;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.enemies.Enemy;
import com.ded.misle.world.logic.TurnTimer;
import com.ded.misle.items.DropTable;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.logic.effects.Chest;
import com.ded.misle.world.logic.effects.Spawnpoint;
import com.ded.misle.world.logic.effects.Travel;
import com.ded.misle.world.logic.World;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.core.Path.getPath;
import static com.ded.misle.world.logic.RoomManager.*;
import static com.ded.misle.world.data.WorldLoader.SideGridDirection.*;
import static com.ded.misle.world.boxes.BoxHandling.*;

public abstract class WorldLoader {
	public static void loadBoxes() {
		TurnTimer.clearRoomScopedTimers();
        Room room;
		room = roomIDToName(player.pos.getRoomID());
		assert room != null;

		Path basePath = getPath(com.ded.misle.core.Path.PathTag.RESOURCES).resolve("rooms/");
		int fileCount = room.fileNames.length;
		Path[] fullPaths = new Path[fileCount];
		BufferedImage[] roomImages = new BufferedImage[fileCount];

		int i = 0;
		// That mess reads all files contained within room.fileNames json and put them as images in roomImages
		for (String fileName : room.fileNames) {
			fullPaths[i] = basePath.resolve(fileName + ".png");
			try {
				roomImages[i] = ImageIO.read(fullPaths[i].toFile());
			} catch (IOException e) {
				System.out.println(fullPaths[i] + " is missing");
				throw new RuntimeException(e);
			}
			i++;
		}

		// Set dimensions based on image dimensions
		int worldWidth = roomImages[0].getWidth();
		int worldHeight = roomImages[0].getHeight();
        new World(worldWidth, worldHeight, room.background);

        // Read values and set as boxes
		for (int x = 0; x < worldWidth; x++) {
			for (int y = 0; y < worldHeight; y++) {
				for (int z = 0; z < fileCount; z++) {
					Color color = new Color(roomImages[z].getRGB(x, y));
					int rgb = color.getRGB() & 0xFFFFFF;

					setRGBToBox(rgb, room, new int[]{x, y, z});
				}
			}
		}

		fixSides();
	}

	private static void setRGBToBox(int rgb, Room room, int[] point) {
		try {
			// Gets the box from pixel RGB and maps it to the image x and y
            Box box = null;
			boolean isSpecifiedByJsonEntry = room.colorCodeMap.containsKey(rgb);
			if (isSpecifiedByJsonEntry) {
				String v = room.colorCodeMap.get(rgb);
				String[] parts = v.split(", ");
				enum CustomColorCodeOption {
					TRAVEL,
					CHEST,
					SPAWNPOINT,
					ENEMY
				}
				try {
					Function<String, Integer> getInt = (String val) -> Integer.parseInt(val.split(":")[1].replace(",", ""));
					Function<String, Long> getLong = (String val) -> Long.parseLong(val.split(":")[1].replace(",", ""));
					Function<String, String> getString = (String val) -> val.split(":")[1].replace(",", "");
					switch (CustomColorCodeOption.valueOf(parts[0].toUpperCase())) {
						case TRAVEL -> {
							int id = getInt.apply(parts[1]);
							Point coordinates = new Point(getInt.apply(parts[2]), getInt.apply(parts[3]));
							box = addBox(BoxPreset.TRAVEL);
							box.effect = new Travel(id, coordinates);
						}
						case SPAWNPOINT -> {
							int id = getInt.apply(parts[1]);
							box = addBox(BoxPreset.SPAWNPOINT);
							box.effect = new Spawnpoint(id);
						}
						case CHEST -> {
							DropTable dropTable = DropTable.getDropTableByName(getString.apply(parts[1]));
							box = addBox(BoxPreset.CHEST);
							int openRate = getInt.apply(parts[2]);
							box.effect = new Chest(openRate, dropTable);
						}
						case ENEMY -> {
							box = addEnemyBox(new Point(0, 0),
								EnemyType.valueOf(parts[1].toUpperCase().split(":")[1]),
								Double.parseDouble(parts[2].split(":")[1]));
							int x = point[0];
							int y = point[1];
							box.setRoomId(room.id);
							box.setOrigin(new Point(x, y));
							if (!((Enemy) box).canRespawn()) {
								((Enemy) box).scheduleRespawn();
								deleteBox(box);
								return;
							}
						}
					}
				} catch (IllegalArgumentException e) { box = addBox(BoxPreset.STONE_BRICK_WALL); }
			} else {
				box = RGBToBox.get(rgb).call();
			}
			int x = point[0];
			int y = point[1];
			int z = point[2];
			box.setRoomId(room.id);
			box.setPos(x, y, z);
			box.setOrigin(new Point(x, y));
		} catch (Exception ignored) {}

	}

	private static final Map<Integer, Callable<Box>> RGBToBox = Map.of(
		0xC4C4C4, () -> addBox(BoxPreset.STONE_BRICK_WALL),
		0xDFDFDF, () -> {
			Entity e = new Entity();
			BoxPreset.CRACKED_STONE_BRICK_WALL.load(e);
			return e;
		},
		0xB38960, () -> addBox(BoxPreset.WOODEN_FLOOR)
	);

	private static void fixSides() {
		World world = player.pos.world;
		int worldWidth = world.width;
		int worldHeight = world.height;

		for (int x = 0; x < worldWidth; x++) {
			for (int y = 0; y < worldHeight; y++) {
				for (int layer = 0; layer < world.layers; layer++) {
					currentBox = world.grid[x][y][layer];
					if (currentBox == null) continue;
					String textureName = currentBox.textureName;
					BoxPreset preset;
					try {
						preset = BoxPreset.valueOf(textureName.toUpperCase());
						if (!preset.hasSides()) continue;
					} catch (IllegalArgumentException ignored) { continue; }

					List<Set<BoxPreset>> groups = BoxPreset.getSideGroups(preset);

					b = new Box[3][3][world.layers];
					b = world.getNeighborhood(currentBox.getX(), currentBox.getY(), 3);

					String corners = ".WASD";
					String sides = ".WASD";

					sides = checkSide(NORTH, sides, "A", layer, groups);
					sides = checkSide(WEST, sides, "W", layer, groups);
					sides = checkSide(EAST, sides, "S", layer, groups);
					sides = checkSide(SOUTH, sides, "D", layer, groups);

					corners = checkCorner(NORTHWEST, corners, "W", layer, groups);
					corners = checkCorner(NORTHEAST, corners, "A", layer, groups);
					corners = checkCorner(SOUTHWEST, corners, "D", layer, groups);
					corners = checkCorner(SOUTHEAST, corners, "S", layer, groups);

					currentBox.setTexture(textureName + sides + corners);
				}
			}
		}
	}

	private static String checkSide(SideGridDirection direction, String side, String toReplace, int layer, List<Set<BoxPreset>> groups) {
		if (isSameGroup(direction, layer, groups)) return side.replaceFirst(toReplace, "");
		return side;
	}

	private static String checkCorner(SideGridDirection cornerDirection, String corner, String toReplace, int layer, List<Set<BoxPreset>> groups) {
		if (isSameGroup(cornerDirection, layer, groups)) return corner.replaceFirst(toReplace, "");
		for (SideGridDirection direction : cornerDirection.breakdown) {
			if (!isSameGroup(direction, layer, groups)) return corner.replaceFirst(toReplace, "");
		}
		return corner;
	}

	private static boolean isSameGroup(SideGridDirection direction, int layer, List<Set<BoxPreset>> groups) {
		Box neighbor = b[direction.x][direction.y][layer];
		if (neighbor == null) return false;

		try {
			int index = neighbor.textureName.indexOf(".");
			if (index == -1) index = neighbor.textureName.length();
			BoxPreset neighborPreset = BoxPreset.valueOf(
				neighbor.textureName.substring(0, index)
					.toUpperCase());
			for (Set<BoxPreset> group : groups) {
				if (group.contains(neighborPreset)) return true;
			}
		} catch (IllegalArgumentException ignored) {}

		return false;
	}

	private static Box[][][] b = new Box[3][3][0];
	private static Box currentBox;

	enum SideGridDirection {
		NORTH(0, 1, new SideGridDirection[]{}),
		WEST(1, 0, new SideGridDirection[]{}),
		CENTER(1, 1, new SideGridDirection[]{}),
		EAST(1, 2, new SideGridDirection[]{}),
		SOUTH(2, 1, new SideGridDirection[]{}),
		NORTHWEST(0, 0, new SideGridDirection[]{NORTH, WEST}),
		NORTHEAST(0, 2, new SideGridDirection[]{NORTH, EAST}),
		SOUTHWEST(2, 0, new SideGridDirection[]{SOUTH, WEST}),
		SOUTHEAST(2, 2, new SideGridDirection[]{SOUTH, EAST}),

		;

		final int x;
		final int y;
		final SideGridDirection[] breakdown;

		SideGridDirection(int x, int y, SideGridDirection[] breakdown) {
			this.x = x;
			this.y = y;
			this.breakdown = breakdown;
		}
	}

	private static String normalizeTextureName(Box box) {
		int index = (box.textureName.indexOf("."));
		if (index == -1) index = box.textureName.length();
		return box.textureName.substring(0, index);
	}

	private static boolean isSameTexture(SideGridDirection direction, int layer) {
		Box target = b[direction.x][direction.y][layer];

		if (target == null) return false;

		String normalizedName = normalizeTextureName(currentBox);
		String normalizedTarget = normalizeTextureName(target);

		return normalizedTarget.equals(normalizedName);
	}

	public static void unloadBoxes() {
		clearAllBoxes();
	}
}