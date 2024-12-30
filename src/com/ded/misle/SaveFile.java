package com.ded.misle;

import com.ded.misle.items.Item;
import com.ded.misle.player.PlayerAttributes;
import com.ded.misle.player.PlayerStats;

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

import static com.ded.misle.SettingsManager.getPath;
import static com.ded.misle.GamePanel.player;
import static com.ded.misle.SaveFile.PixelColor.*;

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
		  9, 38 RGB
		  8, 38 G
		  27, 27 RB
		  30, 127 RG
		  42, 69 B
		  69, 42 R
		  70, 70 RGB
		  70, 71 RGB
		  71, 70 RGB
		  71, 71 RGB
		  82, 10 B
		  99, 1 R
		  117, 63 RGB
		  117, 64 RGB
		  117, 65 RGB
		  118, 63 RGB
		  118, 64 RGB
		  118, 65 RGB
		  118, 66 RGB
		  119, 63 RGB
		  119, 64 RGB
		  119, 65 RGB
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
		  *
		  *
		  *
		  */

		SPAWNPOINT_M				(RED,69,42),
		SPAWNPOINT_L				(BLUE,42,69),
		LEVEL_M						(RED,4,15),
		LEVEL_L						(BLUE,4,15),
		LEVEL_POINTS_M				(GREEN,4,15),
		LEVEL_POINTS_L				(RED,5,15),
		XP_H						(GREEN,5,15),
		XP_M						(BLUE,5,15),
		XP_L						(RED,30,127),
		MAX_HP_H					(BLUE,82,10),
		MAX_HP_M					(GREEN,30,127),
		MAX_HP_L					(RED,99,1),
		MAX_ENTROPY_H				(RED,70,70),
		MAX_ENTROPY_M				(GREEN,70,70),
		MAX_ENTROPY_L				(BLUE,70,70),
		DEFENSE_H					(RED,71,70),
		DEFENSE_M					(GREEN,71,70),
		DEFENSE_L					(BLUE,71,70),
		REGENERATION_QUALITY_H		(RED,71,71),
		REGENERATION_QUALITY_M		(GREEN,71,71),
		REGENERATION_QUALITY_L		(BLUE,71,71),
		SPEED_H						(RED,70,71),
		SPEED_M						(GREEN,70,71),
		SPEED_L						(BLUE,70,71),
		TOTAL_DISTANCE_H			(RED,117,63),
		TOTAL_DISTANCE_M			(RED,118,63),
		TOTAL_DISTANCE_L			(RED,119,63),
		DISTANCE_UP_H				(RED,117,64),
		DISTANCE_UP_M				(RED,118,64),
		DISTANCE_UP_L				(RED,119,64),
		DISTANCE_DOWN_H				(RED,117,65),
		DISTANCE_DOWN_M				(RED,118,65),
		DISTANCE_DOWN_L				(RED,119,65),
		DISTANCE_LEFT_H				(GREEN,117,63),
		DISTANCE_LEFT_M				(GREEN,118,63),
		DISTANCE_LEFT_L				(GREEN,119,63),
		DISTANCE_RIGHT_H			(GREEN,117,64),
		DISTANCE_RIGHT_M			(GREEN,118,64),
		DISTANCE_RIGHT_L			(GREEN,119,64),
		TOTAL_STEPS_H				(GREEN,117,65),
		TOTAL_STEPS_M				(GREEN,118,65),
		TOTAL_STEPS_L				(GREEN,119,65),
		STEPS_UP_H					(BLUE,117,63),
		STEPS_UP_M					(BLUE,118,63),
		STEPS_UP_L					(BLUE,119,63),
		STEPS_DOWN_H				(BLUE,117,64),
		STEPS_DOWN_M				(BLUE,118,64),
		STEPS_DOWN_L				(BLUE,119,64),
		STEPS_LEFT_H				(BLUE,117,65),
		STEPS_LEFT_M				(BLUE,118,65),
		STEPS_LEFT_L				(BLUE,119,65),
		STEPS_RIGHT_H				(BLUE,118,66),
		STEPS_RIGHT_M				(RED,118,66),
		STEPS_RIGHT_L				(GREEN,118,66),
		TOTAL_PLAYTIME_E			(RED,9,38),
		TOTAL_PLAYTIME_H			(GREEN,9,38),
		TOTAL_PLAYTIME_M			(GREEN,8,38),
		TOTAL_PLAYTIME_L			(BLUE,9,38),
		MAX_STACK_SIZE_MULTIPLIER_M	(RED,27,27),
		MAX_STACK_SIZE_MULTIPLIER_L	(GREEN,27,27);

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

					// Spawnpoint

					int spawnpoint = loadAttribute(PixelData.SPAWNPOINT_M, PixelData.SPAWNPOINT_L);
					player.pos.setSpawnpoint(Math.max(spawnpoint, 1));
					player.pos.reloadSpawnpoint();

					// Level and XP related

					int level = loadAttribute(PixelData.LEVEL_M, PixelData.LEVEL_L);
					player.attr.setLevel(level);

					int levelPoints = loadAttribute(PixelData.LEVEL_POINTS_M, PixelData.LEVEL_POINTS_L);
					player.attr.addLevelUpPoints(levelPoints);

					int XP = loadAttribute(PixelData.XP_H, PixelData.XP_M, PixelData.XP_L);
					player.attr.setXP(XP);

					// Level stats (Max HP, max entropy, defense, regeneration quality and speed)

					int playerMaxHP = loadAttribute(PixelData.MAX_HP_H, PixelData.MAX_HP_M, PixelData.MAX_HP_L);
					player.attr.setLevelStat(PlayerAttributes.LevelStat.MAX_HP, playerMaxHP);

					int playerMaxEntropy = loadAttribute(PixelData.MAX_ENTROPY_H, PixelData.MAX_ENTROPY_M, PixelData.MAX_ENTROPY_L);
					player.attr.setLevelStat(PlayerAttributes.LevelStat.MAX_ENTROPY, playerMaxEntropy);

					int playerDefense = loadAttribute(PixelData.DEFENSE_H, PixelData.DEFENSE_M, PixelData.DEFENSE_L);
					player.attr.setLevelStat(PlayerAttributes.LevelStat.DEFENSE, playerDefense);

					int playerRegenerationQuality = loadAttribute(PixelData.REGENERATION_QUALITY_H, PixelData.REGENERATION_QUALITY_M, PixelData.REGENERATION_QUALITY_L);
					player.attr.setLevelStat(PlayerAttributes.LevelStat.REGENERATION_QUALITY, playerRegenerationQuality);

					int playerSpeed = loadAttribute(PixelData.SPEED_M, PixelData.SPEED_M, PixelData.SPEED_L);
					player.attr.setLevelStat(PlayerAttributes.LevelStat.SPEED, playerSpeed);

					// Max stack size

					player.attr.setMaxStackSizeMulti((float) loadAttribute(PixelData.MAX_STACK_SIZE_MULTIPLIER_M, PixelData.MAX_STACK_SIZE_MULTIPLIER_L) / 1000);

					// Statistics

					player.stats.setDistance(PlayerStats.Direction.TOTAL, loadAttribute(PixelData.TOTAL_DISTANCE_H, PixelData.TOTAL_DISTANCE_M, PixelData.TOTAL_DISTANCE_L));
					player.stats.setDistance(PlayerStats.Direction.UP, loadAttribute(PixelData.DISTANCE_UP_H, PixelData.DISTANCE_UP_M, PixelData.DISTANCE_UP_L));
					player.stats.setDistance(PlayerStats.Direction.DOWN, loadAttribute(PixelData.DISTANCE_DOWN_H, PixelData.DISTANCE_DOWN_M, PixelData.DISTANCE_DOWN_L));
					player.stats.setDistance(PlayerStats.Direction.LEFT, loadAttribute(PixelData.DISTANCE_LEFT_H, PixelData.DISTANCE_LEFT_M, PixelData.DISTANCE_LEFT_L));
					player.stats.setDistance(PlayerStats.Direction.RIGHT, loadAttribute(PixelData.DISTANCE_RIGHT_H, PixelData.DISTANCE_RIGHT_M, PixelData.DISTANCE_RIGHT_L));

					player.stats.setSteps(PlayerStats.Direction.TOTAL, loadAttribute(PixelData.TOTAL_STEPS_H, PixelData.TOTAL_STEPS_M, PixelData.TOTAL_STEPS_L));
					player.stats.setSteps(PlayerStats.Direction.UP, loadAttribute(PixelData.STEPS_UP_H, PixelData.STEPS_UP_M, PixelData.STEPS_UP_L));
					player.stats.setSteps(PlayerStats.Direction.DOWN, loadAttribute(PixelData.STEPS_DOWN_H, PixelData.STEPS_DOWN_M, PixelData.STEPS_DOWN_L));
					player.stats.setSteps(PlayerStats.Direction.LEFT, loadAttribute(PixelData.STEPS_LEFT_H, PixelData.STEPS_LEFT_M, PixelData.STEPS_LEFT_L));
					player.stats.setSteps(PlayerStats.Direction.RIGHT, loadAttribute(PixelData.STEPS_RIGHT_H, PixelData.STEPS_RIGHT_M, PixelData.STEPS_RIGHT_L));

					player.stats.setTotalPlaytime(loadAttribute(PixelData.TOTAL_PLAYTIME_E, PixelData.TOTAL_PLAYTIME_H, PixelData.TOTAL_PLAYTIME_M, PixelData.TOTAL_PLAYTIME_L) * 1000L);

					// Load inventory

					int[][][] tempInventory = new int[4][7][4];
					for (int i = 0; i < 4; i++) {
						for (int j = 0; j < 7; j++) {
							tempInventory[i][j][0] = loadThis(RED, i, j + 15);
							tempInventory[i][j][1] = loadThis(GREEN, i, j + 15);
							// [i][j][0] = ID
							tempInventory[i][j][0] = tempInventory[i][j][0] * 255 + tempInventory[i][j][1];

							tempInventory[i][j][2] = loadThis(BLUE, i, j + 15);
							tempInventory[i][j][3] = loadThis(RED, j + 15, i);
							// [i][j][1] = Count
							tempInventory[i][j][1] = tempInventory[i][j][2] * 255 + tempInventory[i][j][3];

							player.inv.bruteSetItem(Item.createItem(tempInventory[i][j][0], tempInventory[i][j][1]), i, j);
						}
					}

					tempInventory = new int[2][2][4];
					for (int i = 0; i < 2; i++) {
						for (int j = 0; j < 2; j++) {
							tempInventory[i][j][0] = loadThis(RED, i, j + 25);
							tempInventory[i][j][1] = loadThis(GREEN, i, j + 25);
							// [i][j][0] = ID
							tempInventory[i][j][0] = tempInventory[i][j][0] * 255 + tempInventory[i][j][1];

							tempInventory[i][j][2] = loadThis(BLUE, i, j + 25);
							tempInventory[i][j][3] = loadThis(RED, j + 25, i);
							// [i][j][1] = Count
							tempInventory[i][j][1] = tempInventory[i][j][2] * 255 + tempInventory[i][j][3];

							player.inv.bruteSetItem(Item.createItem(tempInventory[i][j][0], tempInventory[i][j][1]), i * 2 + j);
						}
					}
				} catch (IOException e) {
					System.out.println("Failed to load save file!");
					player.unloadPlayer();
					e.printStackTrace();
				}
			} else {
				System.out.println("Could not find a save file!");
				player.unloadPlayer();
				player.pos.reloadSpawnpoint();
			}
		}
	}

	private static int loadAttribute(PixelData extreme, PixelData high, PixelData medium, PixelData low) {
		int extremeValue = loadThis(extreme);
		int highValue = loadThis(high);
		int mediumValue = loadThis(medium);
		int lowValue = loadThis(low);
		return extremeValue * 255 * 255 * 255 + highValue * 255 * 255 + mediumValue * 255 + lowValue;
	}

	private static int loadAttribute(PixelData high, PixelData medium, PixelData low) {
		int highValue = loadThis(high);
		int mediumValue = loadThis(medium);
		int lowValue = loadThis(low);
		return highValue * 255 * 255 + mediumValue * 255 + lowValue;
	}

	private static int loadAttribute(PixelData medium, PixelData low) {
		int mediumValue = loadThis(medium);
		int lowValue = loadThis(low);
		return mediumValue * 255 + lowValue;
	}

	private static int loadAttribute(PixelData low) {
		return loadThis(low);
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
			case RED -> pixel.getRed();
			case GREEN -> pixel.getGreen();
			case BLUE -> pixel.getBlue();
        };
	}

	public static void saveEverything() {

		synchronized (fileLock) {

			// Spawnpoint

			int playerSpawnpoint = player.pos.getSpawnpoint();
			brandValue(playerSpawnpoint, PixelData.SPAWNPOINT_M, PixelData.SPAWNPOINT_L);

			// Level related stuff

			int playerLevel = player.attr.getLevel();
			brandValue(playerLevel, PixelData.LEVEL_M, PixelData.LEVEL_L);

			int playerLevelUpPoints = player.attr.getLevelUpPoints();
			brandValue(playerLevelUpPoints, PixelData.LEVEL_POINTS_M, PixelData.LEVEL_POINTS_L);

			int playerXP = (int) player.attr.getXP();
			brandValue(playerXP, PixelData.XP_H, PixelData.XP_M, PixelData.XP_L);

			// Level Stats (Max HP, max entropy, defense, regeneration quality and speed)

			int playerLevelMaxHP = (int) player.attr.getLevelStat(PlayerAttributes.LevelStat.MAX_HP);
			brandValue(playerLevelMaxHP, PixelData.MAX_HP_H, PixelData.MAX_HP_M, PixelData.MAX_HP_L);

			int playerLevelMaxEntropy = (int) player.attr.getLevelStat(PlayerAttributes.LevelStat.MAX_ENTROPY);
			brandValue(playerLevelMaxEntropy, PixelData.MAX_ENTROPY_H, PixelData.MAX_ENTROPY_M, PixelData.MAX_ENTROPY_L);

			int playerLevelDefense = (int) player.attr.getLevelStat(PlayerAttributes.LevelStat.DEFENSE);
			brandValue(playerLevelDefense, PixelData.DEFENSE_H, PixelData.DEFENSE_M, PixelData.DEFENSE_L);

			int playerLevelRegenerationQuality = (int) player.attr.getLevelStat(PlayerAttributes.LevelStat.REGENERATION_QUALITY);
			brandValue(playerLevelRegenerationQuality, PixelData.REGENERATION_QUALITY_H, PixelData.REGENERATION_QUALITY_M, PixelData.REGENERATION_QUALITY_L);

			int playerLevelSpeed = (int) player.attr.getLevelStat(PlayerAttributes.LevelStat.SPEED);
			brandValue(playerLevelSpeed, PixelData.SPEED_H, PixelData.SPEED_M, PixelData.SPEED_L);

			// Statistics

			int totalSteps = player.stats.getSteps(PlayerStats.Direction.TOTAL);
			brandValue(totalSteps, PixelData.TOTAL_STEPS_H, PixelData.TOTAL_STEPS_M, PixelData.TOTAL_STEPS_L);
			int stepsUp = player.stats.getSteps(PlayerStats.Direction.UP);
			brandValue(stepsUp, PixelData.STEPS_UP_H, PixelData.STEPS_UP_M, PixelData.STEPS_UP_L);
			int stepsDown = player.stats.getSteps(PlayerStats.Direction.DOWN);
			brandValue(stepsDown, PixelData.STEPS_DOWN_H, PixelData.STEPS_DOWN_M, PixelData.STEPS_DOWN_L);
			int stepsLeft = player.stats.getSteps(PlayerStats.Direction.LEFT);
			brandValue(stepsLeft, PixelData.STEPS_LEFT_H, PixelData.STEPS_LEFT_M, PixelData.STEPS_LEFT_L);
			int stepsRight = player.stats.getSteps(PlayerStats.Direction.RIGHT);
			brandValue(stepsRight, PixelData.STEPS_RIGHT_H, PixelData.STEPS_RIGHT_M, PixelData.STEPS_RIGHT_L);

			int totalDistance = (int) player.stats.getDistance(PlayerStats.Direction.TOTAL);
			brandValue(totalDistance, PixelData.TOTAL_DISTANCE_H, PixelData.TOTAL_DISTANCE_M, PixelData.TOTAL_DISTANCE_L);
			int distanceUp = (int) player.stats.getDistance(PlayerStats.Direction.UP);
			brandValue(distanceUp, PixelData.DISTANCE_UP_H, PixelData.DISTANCE_UP_M, PixelData.DISTANCE_UP_L);
			int distanceDown = (int) player.stats.getDistance(PlayerStats.Direction.DOWN);
			brandValue(distanceDown, PixelData.DISTANCE_DOWN_H, PixelData.DISTANCE_DOWN_M, PixelData.DISTANCE_DOWN_L);
			int distanceLeft = (int) player.stats.getDistance(PlayerStats.Direction.LEFT);
			brandValue(distanceLeft, PixelData.DISTANCE_LEFT_H, PixelData.DISTANCE_LEFT_M, PixelData.DISTANCE_LEFT_L);
			int distanceRight = (int) player.stats.getDistance(PlayerStats.Direction.RIGHT);
			brandValue(distanceRight, PixelData.DISTANCE_RIGHT_H, PixelData.DISTANCE_RIGHT_M, PixelData.DISTANCE_RIGHT_L);

			long previousPlaytime = player.stats.getTotalPlaytime(PlayerStats.PlaytimeMode.SECONDS);
			long currentPlaytime = player.stats.getCurrentPlaytime(PlayerStats.PlaytimeMode.SECONDS);
			long totalPlaytime = previousPlaytime + currentPlaytime;
			brandValue(totalPlaytime, PixelData.TOTAL_PLAYTIME_E, PixelData.TOTAL_PLAYTIME_H, PixelData.TOTAL_PLAYTIME_M, PixelData.TOTAL_PLAYTIME_L);

			int maxStackSizeMultiplier = (int) (1000 * player.attr.getMaxStackSizeMulti());
			brandValue(maxStackSizeMultiplier, PixelData.MAX_STACK_SIZE_MULTIPLIER_M, PixelData.MAX_STACK_SIZE_MULTIPLIER_L);

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
							tempInventory[i][j][0] = getMedium((player.inv.getItem(i, j).getId()));       // HIGH ID
							tempInventory[i][j][1] = getLow(player.inv.getItem(i, j).getId());          // LOW ID
							tempInventory[i][j][2] = getMedium((player.inv.getItem(i, j).getCount()));    // HIGH COUNT
							tempInventory[i][j][3] = getLow(player.inv.getItem(i, j).getCount());       // LOW COUNT
						}
						brandIntoSaveFile(tempInventory[i][j][0], RED, i, j + 15);
						brandIntoSaveFile(tempInventory[i][j][1], GREEN, i, j + 15);
						brandIntoSaveFile(tempInventory[i][j][2], BLUE, i, j + 15);
						brandIntoSaveFile(tempInventory[i][j][3], RED, j + 15, i);
					}
				}
			} catch (NullPointerException e) {
				// If the game hadn't been started before quitting, this just means the inventory was not loaded yet.
			}

			try {
				int[][][] tempInventory = new int[2][2][4];
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 2; j++) {
						if (player.inv.getItem(i, j) == null) {
							tempInventory[i][j][0] = 0;
							tempInventory[i][j][1] = 0;
							tempInventory[i][j][2] = 0;
							tempInventory[i][j][3] = 0;
						} else {
							tempInventory[i][j][0] = getMedium((player.inv.getItem(i * 2 + j).getId()));       // HIGH ID
							tempInventory[i][j][1] = getLow(player.inv.getItem(i * 2 + j).getId());          // LOW ID
							tempInventory[i][j][2] = getMedium((player.inv.getItem(i * 2 + j).getCount()));    // HIGH COUNT
							tempInventory[i][j][3] = getLow(player.inv.getItem(i * 2 + j).getCount());       // LOW COUNT
						}
						brandIntoSaveFile(tempInventory[i][j][0], RED, i, j + 25);
						brandIntoSaveFile(tempInventory[i][j][1], GREEN, i, j + 25);
						brandIntoSaveFile(tempInventory[i][j][2], BLUE, i, j + 25);
						brandIntoSaveFile(tempInventory[i][j][3], RED, j + 25, i);
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
			System.out.println(("Save value must be between 0 and 255. Value inserted: " + value));
			value = 255;
		}

		if (x < 0 || x >= 128 || y < 0 || y >= 128) {
			System.out.println(("Invalid x or y position for save file: " + x + ", " + y + ", value: " + value));
			return;
		}

		Color previousValue = new Color(image.getRGB(x, y));
		int red = previousValue.getRed();
		int green = previousValue.getGreen();
		int blue = previousValue.getBlue();

		switch (color) {
			case RED -> image.setRGB(x, y, new Color(value, green, blue).getRGB());
			case GREEN -> image.setRGB(x, y, new Color(red, value, blue).getRGB());
			case BLUE -> image.setRGB(x, y, new Color(red, green, value).getRGB());
			default -> System.out.println("Invalid color parameter for branding to the save file: " + color);
		}
	}

	private static void brandIntoSaveFile(int value, PixelData pixelData) {
		brandIntoSaveFile(value, pixelData.color, pixelData.x, pixelData.y);
	}

	private static void brandValue(long value, PixelData extreme, PixelData high, PixelData medium, PixelData low) {
		brandIntoSaveFile(getExtreme(value), extreme);
		brandIntoSaveFile(getHigh(value), high);
		brandIntoSaveFile(getMedium(value), medium);
		brandIntoSaveFile(getLow(value), low);
	}

	private static void brandValue(long value, PixelData high, PixelData medium, PixelData low) {
		brandIntoSaveFile(getHigh(value), high);
		brandIntoSaveFile(getMedium(value), medium);
		brandIntoSaveFile(getLow(value), low);
	}

	private static void brandValue(long value, PixelData medium, PixelData low) {
		brandIntoSaveFile(getMedium(value), medium);
		brandIntoSaveFile(getLow(value), low);
	}

	private static void brandValue(int value, PixelData low) {
		brandIntoSaveFile(getLow(value), low);
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

	private static int getExtreme(long value) {
		return (int) ((value / (255 * 255 * 255)) % 255);
	}

	private static int getHigh(long value) {
		return (int) ((value / (255 * 255)) % 255);
	}

	private static int getMedium(long value) {
		return (int) ((value / 255) % 255);
	}

	private static int getLow(long value) {
		return (int) (value % 255);
	}
}
