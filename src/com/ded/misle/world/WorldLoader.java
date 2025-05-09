package com.ded.misle.world;

import com.ded.misle.core.TurnTimer;
import com.ded.misle.items.DropTable;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.effects.Chest;
import com.ded.misle.world.effects.Spawnpoint;
import com.ded.misle.world.effects.Travel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.SettingsManager.getPath;
import static com.ded.misle.world.RoomManager.*;
import static com.ded.misle.world.World.Background.GRASS;
import static com.ded.misle.world.WorldLoader.SideGridDirection.*;
import static com.ded.misle.world.boxes.BoxHandling.*;

public abstract class WorldLoader {
	public static void loadBoxes() {
		TurnTimer.clearRoomScopedTimers();
        Room room;
		room = roomIDToName(player.pos.getRoomID());
//		room = findRoom("TUANI_CITY"); // TEMPORARILY FORCING TUANI_CITY
		assert room != null;

		Path basePath = getPath().resolve("resources/worlds/");
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
        new World(worldWidth, worldHeight, GRASS);

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
					travel,
					chest,
					spawnpoint
				}
				try {
					Function<String, Integer> getInt = (String val) -> Integer.parseInt(val.split(":")[1].replace(",", ""));
					Function<String, Long> getLong = (String val) -> Long.parseLong(val.split(":")[1].replace(",", ""));
					Function<String, String> getString = (String val) -> val.split(":")[1].replace(",", "");
					switch (CustomColorCodeOption.valueOf(parts[0])) {
						case travel -> {
							int id = getInt.apply(parts[1]);
							Point coordinates = new Point(getInt.apply(parts[2]), getInt.apply(parts[3]));
							box = addBox(BoxPreset.TRAVEL);
							box.effect = new Travel(id, coordinates);
						}
						case spawnpoint -> {
							int id = getInt.apply(parts[1]);
							box = addBox(BoxPreset.SPAWNPOINT);
							box.effect = new Spawnpoint(id);
						}
						case chest -> {
							DropTable dropTable = DropTable.getDropTableByName(getString.apply(parts[1]));
							box = addBox(BoxPreset.CHEST);
							long openRate = getLong.apply(parts[2]);
							box.effect = new Chest(openRate, dropTable);
						}
					}
				} catch (IllegalArgumentException e) { box = addBox(BoxPreset.WALL_DEFAULT); }
			} else {
				box = RGBToBox.get(rgb).call();
			}
			int x = point[0];
			int y = point[1];
			int z = point[2];
			box.setPos(x, y, z);
		} catch (Exception ignored) {}

	}

	private static final Map<Integer, Callable<Box>> RGBToBox = Map.of(
		0xC4C4C4, () -> addBox(BoxPreset.WALL_DEFAULT),
		0xDFDFDF, () -> addBox(BoxPreset.FLOOR_DEFAULT)
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
					int dotIndex = textureName.indexOf(".");
					if (dotIndex == -1) continue; // Is not a box that has sides
					String normalizedName = textureName.substring(0, dotIndex);

					boolean hasSides = checkIfPresetHasSides(BoxPreset.valueOf(normalizedName.toUpperCase()));
					if (hasSides) {
						b = new Box[3][3][world.layers];
						b = world.getNeighborhood(currentBox.getX(), currentBox.getY(), 3);

						String corners = ".WASD";
						String sides = ".WASD";

						sides = checkSide(NORTH, sides, "A", layer);
						sides = checkSide(WEST, sides, "W", layer);
						sides = checkSide(EAST, sides, "S", layer);
						sides = checkSide(SOUTH, sides, "D", layer);

						corners = checkCorner(NORTHWEST, corners, "W", layer);
						corners = checkCorner(NORTHEAST, corners, "A", layer);
						corners = checkCorner(SOUTHWEST, corners, "D", layer);
						corners = checkCorner(SOUTHEAST, corners, "S", layer);

						currentBox.setTexture(normalizedName + sides + corners);
					}
				}
			}
		}
	}

	private static String checkSide(SideGridDirection direction, String side, String toReplace, int layer) {
		if (isSameTexture(direction, layer)) return side.replaceFirst(toReplace, "");
		return side;
	}

	private static String checkCorner(SideGridDirection cornerDirection, String corner, String toReplace, int layer) {
		if (isSameTexture(cornerDirection, layer)) return corner.replaceFirst(toReplace, "");
		for (SideGridDirection direction : cornerDirection.breakdown) {
			if (!isSameTexture(direction, layer)) return corner.replaceFirst(toReplace, "");
		}
		return corner;
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