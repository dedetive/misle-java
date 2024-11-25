package com.ded.misle;

import com.ded.misle.items.Item;
import com.ded.misle.player.PlayerAttributes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import static com.ded.misle.ChangeSettings.getPath;
import static com.ded.misle.GamePanel.player;

public class SaveFile {

	private static final Object fileLock = new Object();

	// FILE PATH FOR .PNG
	private static final Path filePath = getPath().resolve("savefile.png");
	private static final File save = filePath.toFile();

	// IMAGE

	static BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

	// COLORS

	public enum PixelColor { RED, GREEN, BLUE }

	public enum PixelData {

		/**
		  PIXELS IN USE:
		  X   Y
		  4, 15 RGB
		  5, 15 RGB
		  30, 127 RG
		  42, 69 B
		  69, 42 R
		  82, 10 B
		  99, 1 R
		 */

		 /**
		 * Additional pixels based on inventory (X, Y coordinates for each pixel set in inventory):
	     * X  Y
		 * 0, 15 R
		 * 1, 15 G
		 * 2, 15 B
		 * 3, 15 R
		 * 0, 16 G
		 * 1, 16 B
		 * 2, 16 R
		 * 3, 16 G
		 * 0, 17 B
		 * 1, 17 R
		 * 2, 17 G
		 * 3, 17 B
		 * 0, 18 R
		 * 1, 18 G
		 * 2, 18 B
		 * 3, 18 R
		 * 0, 19 G
		 * 1, 19 B
		 * 2, 19 R
		 * 3, 19 G
		 * 0, 20 B
		 * 1, 20 R
		 * 2, 20 G
		 * 3, 20 B
		 * 0, 21 R
		 * 1, 21 G
		 * 2, 21 B
		 * 3, 21 R
		 * 0, 22 G
		 * 1, 22 B
		 * 2, 22 R
		 * 3, 22 G
		 * 0, 23 B
		 * 1, 23 R
		 * 2, 23 G
		 * 3, 23 B
		 * 0, 24 R
		 * 1, 24 G
		 * 2, 24 B
		 * 3, 24 R
		 *
		 */

		MAX_HP_HIGHEST(PixelColor.BLUE, 82, 10),
		MAX_HP_HIGH(PixelColor.GREEN, 30, 127),
		MAX_HP_LOW(PixelColor.RED, 99, 1),
		SPAWNPOINT_HIGH(PixelColor.RED, 69, 42),
		SPAWNPOINT_LOW(PixelColor.BLUE, 42, 69),
		LEVEL_HIGH(PixelColor.RED, 4, 15),
		LEVEL_LOW(PixelColor.BLUE, 4, 15),
		LEVEL_POINTS_HIGH(PixelColor.GREEN, 4, 15),
		LEVEL_POINTS_LOW(PixelColor.RED, 5, 15),
		XP_HIGHEST(PixelColor.GREEN, 5, 15),
		XP_HIGH(PixelColor.BLUE, 5, 15),
		XP_LOW(PixelColor.RED, 30, 127);

		private final PixelColor color;
		private final int x;
		private final int y;

		PixelData(PixelColor color, int x, int y) {
			this.color = color;
			this.x = x;
			this.y = y;
		}
	}

	public static void loadSaveFile() {

		synchronized (fileLock) {
			boolean fileExists = checkIfSaveFileExists();

			if (fileExists) {
				try {
					image = ImageIO.read(save);

					// Load maxHP

					int maxHPHighest = loadThis(PixelData.MAX_HP_HIGHEST);
					int maxHPHigh = loadThis(PixelData.MAX_HP_HIGH);
					int maxHPLow = loadThis(PixelData.MAX_HP_LOW);
					double playerMaxHP = 255 * 255 * maxHPHighest + 255 * maxHPHigh + maxHPLow;
					player.attr.setLevelStat(PlayerAttributes.LevelStat.MAX_HP, playerMaxHP);

					// Load spawnpoint

					int spawnpointHigh = loadThis(PixelData.SPAWNPOINT_HIGH);
					int spawnpointLow = loadThis(PixelData.SPAWNPOINT_LOW);
					int spawnpoint = spawnpointHigh * 255 + spawnpointLow;
					player.pos.setSpawnpoint(Math.max(spawnpoint, 1));
					player.pos.reloadSpawnpoint();

					// Load Level and XP related

					int levelHigh = loadThis(PixelData.LEVEL_HIGH);
					int levelLow = loadThis(PixelData.LEVEL_LOW);
					int level = levelHigh * 255 + levelLow;
					player.attr.setLevel(level);

					int levelPointsHigh = loadThis(PixelData.LEVEL_POINTS_HIGH);
					int levelPointsLow = loadThis(PixelData.LEVEL_POINTS_LOW);
					int levelPoints = levelPointsHigh * 255 + levelPointsLow;
					player.attr.addLevelUpPoints(levelPoints);

					int XPHighest = loadThis(PixelData.XP_HIGHEST);
					int XPHigh = loadThis(PixelData.XP_HIGH);
					int XPLow = loadThis(PixelData.XP_LOW);
					int XP = XPHighest * 255 * 255 + XPHigh * 255 + XPLow;
					player.attr.setXP(XP);

					// Load inventory

					int[][][] tempInventory = new int[4][7][4];
					for (int i = 0; i < 4; i++) {
						for (int j = 0; j < 7; j++) {
							tempInventory[i][j][0] = loadThis(PixelColor.RED, i, j + 15);
							tempInventory[i][j][1] = loadThis(PixelColor.GREEN, i, j + 15);
							// [i][j][0] = ID
							tempInventory[i][j][0] = tempInventory[i][j][0] * 255 + tempInventory[i][j][1];

							tempInventory[i][j][2] = loadThis(PixelColor.BLUE, i, j + 15);
							tempInventory[i][j][3] = loadThis(PixelColor.RED, j + 15, i);
							// [i][j][1] = Count
							tempInventory[i][j][1] = tempInventory[i][j][2] * 255 + tempInventory[i][j][3];

							player.inv.bruteSetItem(Item.createItem(tempInventory[i][j][0], tempInventory[i][j][1]), i, j);
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static int loadThis(PixelData pixelData) {
		return loadThis(pixelData.color, pixelData.x, pixelData.y);
	}

	private static int loadThis(PixelColor color, int x, int y) {
		if (x < 0 || x > 128 || y < 0 || y > 128) {
			System.out.println("Invalid x or y position for loading from the save file: " + x + ", " + y);
		}

		Color pixel = new Color(image.getRGB(x, y));

		return switch (color) {
			case PixelColor.RED -> pixel.getRed();
			case PixelColor.GREEN -> pixel.getGreen();
			case PixelColor.BLUE -> pixel.getBlue();
			default -> {
				System.out.println("Invalid color parameter for loading from the save file: " + color);
				yield 0;
			}
		};
	}

	public static void saveEverything() {

		synchronized (fileLock) {

			// Max HP

			int playerMaxHP = (int) player.attr.getLevelStat(PlayerAttributes.LevelStat.MAX_HP);
			brandIntoSaveFile(getHighest(playerMaxHP), PixelData.MAX_HP_HIGHEST);
			brandIntoSaveFile(getHigh(playerMaxHP), PixelData.MAX_HP_HIGH);
			brandIntoSaveFile(getLow(playerMaxHP), PixelData.MAX_HP_LOW);

			// Spawnpoint

			int playerSpawnpoint = player.pos.getSpawnpoint();
			brandIntoSaveFile(getHigh(playerSpawnpoint), PixelData.SPAWNPOINT_HIGH);
			brandIntoSaveFile(getLow(playerSpawnpoint), PixelData.SPAWNPOINT_LOW);

			int playerLevel = player.attr.getLevel();
			brandIntoSaveFile(getHigh(playerLevel), PixelData.LEVEL_HIGH);
			brandIntoSaveFile(getLow(playerLevel), PixelData.LEVEL_LOW);

			int playerLevelUpPoints = player.attr.getLevelUpPoints();
			brandIntoSaveFile(getHigh(playerLevelUpPoints), PixelData.LEVEL_POINTS_HIGH);
			brandIntoSaveFile(getLow(playerLevelUpPoints), PixelData.LEVEL_POINTS_LOW);

			int playerXP = (int) player.attr.getXP();
			brandIntoSaveFile(getHighest(playerXP), PixelData.XP_HIGHEST);
			brandIntoSaveFile(getHigh(playerXP), PixelData.XP_HIGH);
			brandIntoSaveFile(getLow(playerXP), PixelData.XP_LOW);

			// Inventory

			try {
				int[][][] tempInventory = new int[4][7][4];
				for (int i = 0; i < 4; i++) {
					for (int j = 0; j < 7; j++) {
						if (player.inv.getItem(i, j) == null) {
							tempInventory[i][j][0] = 0;
							tempInventory[i][j][1] = 0;
							tempInventory[i][j][2] = 0;
							tempInventory[i][j][3] = 0;
						} else {
							tempInventory[i][j][0] = getHigh((player.inv.getItem(i, j).getId()));       // HIGH ID
							tempInventory[i][j][1] = getLow(player.inv.getItem(i, j).getId());          // LOW ID
							tempInventory[i][j][2] = getHigh((player.inv.getItem(i, j).getCount()));    // HIGH COUNT
							tempInventory[i][j][3] = getLow(player.inv.getItem(i, j).getCount());       // LOW COUNT
						}
						brandIntoSaveFile(tempInventory[i][j][0], PixelColor.RED, i, j + 15);
						brandIntoSaveFile(tempInventory[i][j][1], PixelColor.GREEN, i, j + 15);
						brandIntoSaveFile(tempInventory[i][j][2], PixelColor.BLUE, i, j + 15);
						brandIntoSaveFile(tempInventory[i][j][3], PixelColor.RED, j + 15, i);
					}
				}
			} catch (NullPointerException e) {
				// If the game hadn't been started before quitting, this just means the inventory was not loaded yet.
			}

			ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(1.0f); // Maximum quality (lossless)
			try (ImageOutputStream ios = ImageIO.createImageOutputStream(save)) {
				writer.setOutput(ios);
				writer.write(null, new IIOImage(image, null, null), param);
				writer.dispose();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void brandIntoSaveFile(int value, PixelColor color, int x, int y) {

		if (value > 255 || value < 0) {
			throw new IllegalArgumentException("Save value must be between 0 and 255. Value inserted: " + value);
		}

		if (x < 0 || x >= 128 || y < 0 || y >= 128) {
			throw new IllegalArgumentException("Invalid x or y position for save file: " + x + ", " + y + ", value: " + value);
		}

		Color previousValue = new Color(image.getRGB(x, y));
		int red = previousValue.getRed();
		int green = previousValue.getGreen();
		int blue = previousValue.getBlue();

		switch (color) {
			case PixelColor.RED -> image.setRGB(x, y, new Color(value, green, blue).getRGB());
			case PixelColor.GREEN -> image.setRGB(x, y, new Color(red, value, blue).getRGB());
			case PixelColor.BLUE -> image.setRGB(x, y, new Color(red, green, value).getRGB());
			default -> System.out.println("Invalid color parameter for branding to the save file: " + color);
		}
	}

	private static void brandIntoSaveFile(int value, PixelData pixelData) {
		brandIntoSaveFile(value, pixelData.color, pixelData.x, pixelData.y);
	}

	private static boolean checkIfSaveFileExists() {
		if (!SaveFile.save.exists()) {
			System.out.println("Save file not found. Creating a new empty file...");

			try {
				// Write the image to a file
				ImageIO.write(image, "png", SaveFile.save);

				System.out.println("PNG save file created: " + SaveFile.filePath);
				return false;
			} catch (IOException e) {
				System.err.println("Failed to create the base PNG save file.");
				e.printStackTrace();
				return false;
			}
		} else {
			System.out.println("Save file found: " + SaveFile.filePath);
			return true;
		}
	}

	private static int getHighest(int value) {
		return (value / (255 * 255)) % 255;
	}

	private static int getHigh(int value) {
		return (value / 255) % 255;
	}

	private static int getLow(int value) {
		return value % 255;
	}
}
