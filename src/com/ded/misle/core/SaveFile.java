package com.ded.misle.core;

import com.ded.misle.items.Item;
import com.ded.misle.renderer.ImageManager;
import com.ded.misle.world.player.PlayerAttributes;
import com.ded.misle.world.player.PlayerStats;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import static com.ded.misle.core.SaveFile.SaveScreenOption.ICON;
import static com.ded.misle.core.SaveFile.SaveScreenOption.IS_PLAYER_TEXTURE_ICON;
import static com.ded.misle.core.SettingsManager.getPath;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.SaveFile.PixelColor.*;
import static com.ded.misle.renderer.ImageManager.*;

public class SaveFile {

	private static final Object fileLock = new Object();

	// FILE PATH FOR .PNG
	private static final Path filePath = getPath().resolve("savefile");
	private static final File[] save = new File[3];
	static {
		for (int i = 0; i < 3; i++) {
			save[i] = (Path.of(filePath + String.valueOf(i) + ".png")).toFile();
		}
	}

	// IMAGE

	static BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

	// COLORS

	public enum PixelColor { RED, GREEN, BLUE }

	public enum PixelData {

		/**
		  PIXELS IN USE:
		  X   Y
		  0, 0 RGB
		  0, 1 G
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
		MAX_STACK_SIZE_MULTIPLIER_L	(GREEN,27,27),
		BALANCE_E					(RED, 0, 0),
		BALANCE_H					(GREEN, 0, 0),
		BALANCE_M					(BLUE, 0, 0),
		BALANCE_L					(GREEN, 0, 1),
		ICON_ACTIVE_L				(BLUE, 0, 110),
		IS_PLAYER_TEXTURE_ICON_L	(BLUE, 0, 110),

		;

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
		int saveSlot = player.currentSaveSlot;

		synchronized (fileLock) {
			boolean fileExists = checkIfSaveFileExists(saveSlot);

			if (fileExists) {
				try {
					image = ImageIO.read(save[saveSlot]);

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

					// Coins

					player.attr.setBalance(loadAttribute(PixelData.BALANCE_E, PixelData.BALANCE_H, PixelData.BALANCE_M, PixelData.BALANCE_L));

					// Name

                    byte[] nameBytes = new byte[16];
					PixelColor pixelColor;

					for (int i = 0; i < 16; i++) {
						if (i % 3 == 0) pixelColor = RED;
						else if (i % 3 == 1) pixelColor = GREEN;
						else pixelColor = BLUE;

						nameBytes[i] = (byte) loadThis(pixelColor, i / 3, 127);
					}

					String playerName = new String(nameBytes, StandardCharsets.UTF_8);
					player.name = playerName.trim();

					// Load inventory

					int[][][] tempInventory = new int[4][7][4];
					for (int i = 0; i < 4; i++) {
						for (int j = 0; j < 7; j++) {
							tempInventory[i][j][0] = loadThis(RED, i, j + 15);
							tempInventory[i][j][1] = loadThis(GREEN, i, j + 15);
							// [i][j][0] = ID
							int itemID = tempInventory[i][j][0] * 255 + tempInventory[i][j][1];

							tempInventory[i][j][2] = loadThis(BLUE, i, j + 15);
							tempInventory[i][j][3] = loadThis(RED, j + 15, i);
							// [i][j][1] = Count
							int itemCount = tempInventory[i][j][2] * 255 + tempInventory[i][j][3];

							if (itemID == 0 || itemCount == 0) continue;

							player.inv.bruteSetItem(Item.createItem(itemID, itemCount), i, j);
						}
					}

					tempInventory = new int[2][2][4];
					for (int i = 0; i < 2; i++) {
						for (int j = 0; j < 2; j++) {
							tempInventory[i][j][0] = loadThis(RED, i, j + 25);
							tempInventory[i][j][1] = loadThis(GREEN, i, j + 25);
							// [i][j][0] = ID
							int itemID = tempInventory[i][j][0] * 255 + tempInventory[i][j][1];

							tempInventory[i][j][2] = loadThis(BLUE, i, j + 25);
							tempInventory[i][j][3] = loadThis(RED, j + 25, i);
							// [i][j][1] = Count
							int itemCount = tempInventory[i][j][2] * 255 + tempInventory[i][j][3];

							if (itemID == 0 || itemCount == 0) continue;

							player.inv.bruteSetItem(Item.createItem(itemID, itemCount), i * 2 + j);
						}
					}

					player.isIconActive = (getLow(loadThis(PixelData.ICON_ACTIVE_L)) % 2) == 1;
					player.icon = (BufferedImage) loadSaveScreenInformation(ICON, saveSlot);
					player.isIconTexture = (boolean) loadSaveScreenInformation(SaveScreenOption.IS_PLAYER_TEXTURE_ICON, saveSlot);

					if (player.isIconTexture) {
						for (ImageManager.ImageName img : playerImages) {
							mergeImages(cachedImages.get(img), player.icon);
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
		int saveSlot = player.currentSaveSlot;

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

			long playtime = player.stats.getCurrentTotalPlaytime(PlayerStats.PlaytimeMode.SECONDS);
			brandValue(playtime, PixelData.TOTAL_PLAYTIME_E, PixelData.TOTAL_PLAYTIME_H, PixelData.TOTAL_PLAYTIME_M, PixelData.TOTAL_PLAYTIME_L);

			int maxStackSizeMultiplier = (int) (1000 * player.attr.getMaxStackSizeMulti());
			brandValue(maxStackSizeMultiplier, PixelData.MAX_STACK_SIZE_MULTIPLIER_M, PixelData.MAX_STACK_SIZE_MULTIPLIER_L);

			int balance = player.attr.getBalance();
			brandValue(balance, PixelData.BALANCE_E, PixelData.BALANCE_H, PixelData.BALANCE_M, PixelData.BALANCE_L);

			// Name

			int maxLength = 17;
			String name = player.name.substring(0, Math.min(maxLength - 1, player.name.length()));

			int charPos = 0;
			PixelColor pixelColor;
			for (byte s : name.getBytes()) {
				if (charPos % 3 == 0) pixelColor = RED;
				else if (charPos % 3 == 1) pixelColor = GREEN;
				else pixelColor = BLUE;
				brandIntoSaveFile(s, pixelColor, charPos / 3, 127);
				charPos++;
			}

			// Remove trailing
			if (player.name.length() < maxLength) {
				for (int i = player.name.length(); i < maxLength - player.name.length(); i++) {
					if (i % 3 == 0) pixelColor = RED;
					else if (i % 3 == 1) pixelColor = GREEN;
					else pixelColor = BLUE;
					brandIntoSaveFile(0, pixelColor, i / 3, 127);
				}
			}

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
						try {
							Item item = player.inv.getItem(i * 2 + j);

							tempInventory[i][j][0] = getMedium(item.getId());       // HIGH ID
							tempInventory[i][j][1] = getLow(item.getId());          // LOW ID
							tempInventory[i][j][2] = getMedium(item.getCount());    // HIGH COUNT
							tempInventory[i][j][3] = getLow(item.getCount());       // LOW COUNT

							brandIntoSaveFile(tempInventory[i][j][0], RED, i, j + 25);
							brandIntoSaveFile(tempInventory[i][j][1], GREEN, i, j + 25);
							brandIntoSaveFile(tempInventory[i][j][2], BLUE, i, j + 25);
							brandIntoSaveFile(tempInventory[i][j][3], RED, j + 25, i);
						} catch (NullPointerException ignored) {
							brandIntoSaveFile(0, RED, i, j + 25);
							brandIntoSaveFile(0, GREEN, i, j + 25);
							brandIntoSaveFile(0, BLUE, i, j + 25);
							brandIntoSaveFile(0, RED, j + 25, i);
						}
					}
				}
			} catch (NullPointerException e) {
				// If the game hadn't been started before quitting, this just means the inventory was not loaded yet.
			}

			Color px = new Color(image.getRGB(0, 110));
			int value = px.getBlue();
			value = value - value % 2;
			value = value - value % 4;

			if (player.isIconActive) value++;
			if (player.isIconTexture) value += 2;

			image.setRGB(0, 110,
				new Color(px.getRed(),
					px.getGreen(),
					value).getRGB());

			if (player.isIconActive) {
				for (int i = 0; i < player.icon.getWidth(); i++) {
					for (int j = 0; j < player.icon.getHeight(); j++) {
						image.setRGB(i, j + 111, player.icon.getRGB(i, j));
					}
				}
			}

			ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(1.0f); // Maximum quality (lossless)
			try (ImageOutputStream ios = ImageIO.createImageOutputStream(save[saveSlot])) {
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

	private static boolean checkIfSaveFileExists(int saveSlot) {
		if (!SaveFile.save[saveSlot].exists()) {
			System.out.println("Save file not found. Creating a new empty file...");

			try {
				// Write the image to a file
				ImageIO.write(image, "png", SaveFile.save[saveSlot]);

				System.out.println("PNG save file created: " + save[saveSlot].getPath());
				return false;
			} catch (IOException e) {
				System.err.println("Failed to create the base PNG save file.");
				e.printStackTrace();
				return false;
			}
		} else {
			System.out.println("Save file found: " + save[saveSlot].getPath());
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

	public enum SaveScreenOption {
		EXISTS,
		LEVEL,
		PLAYTIME,
		FIRST_ITEM,
		NAME,
		ICON,
		IS_PLAYER_TEXTURE_ICON,
	}

	public static Object loadSaveScreenInformation(SaveScreenOption option, int saveSlot) {
		try {
			return switch (option) {
				case EXISTS -> {
					yield SaveFile.save[saveSlot].exists();
				}
				case LEVEL -> {
					image = ImageIO.read(SaveFile.save[saveSlot]);

                    yield loadAttribute(PixelData.LEVEL_M, PixelData.LEVEL_L);
				}
				case PLAYTIME -> {
					image = ImageIO.read(SaveFile.save[saveSlot]);
					long seconds = (loadAttribute(PixelData.TOTAL_PLAYTIME_E, PixelData.TOTAL_PLAYTIME_H, PixelData.TOTAL_PLAYTIME_M, PixelData.TOTAL_PLAYTIME_L));
					long minutes = seconds / 60;
					long hours = minutes / 60;
					String time = hours + "h:" + minutes % 60 + "m:" + seconds % 60 + "s";

					yield time;
				}
				case FIRST_ITEM -> {
					image = ImageIO.read(SaveFile.save[saveSlot]);
					int idHigh = loadThis(RED, 0, 15);
					int idLow = loadThis(GREEN, 0, 15);
					int itemID = idHigh * 255 + idLow;

					yield new Item(itemID);
				}
				case NAME -> {
					image = ImageIO.read(SaveFile.save[saveSlot]);
					byte[] nameBytes = new byte[16];
					PixelColor pixelColor;

					for (int i = 0; i < 16; i++) {
						if (i % 3 == 0) pixelColor = RED;
						else if (i % 3 == 1) pixelColor = GREEN;
						else pixelColor = BLUE;

						nameBytes[i] = (byte) loadThis(pixelColor, i / 3, 127);
					}

					String playerName = new String(nameBytes, StandardCharsets.UTF_8);

					yield playerName.trim();
				}
				case ICON -> {
					image = ImageIO.read(SaveFile.save[saveSlot]);
					BufferedImage output = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
					boolean isIconActive = new Color(image.getRGB(0, 110)).getBlue() % 2 == 1;

					if (isIconActive) {

						for (int i = 0; i < 16; i++) {
							for (int j = 0; j < 16; j++) {
								int color = image.getRGB(i, j + 111);
								if (color == -16777216) continue;
								output.setRGB(i, j, color);
							}
						}
					}

					yield output;
				}
				case IS_PLAYER_TEXTURE_ICON -> {
					image = ImageIO.read(SaveFile.save[saveSlot]);

					yield Objects.equals(((loadAttribute(PixelData.IS_PLAYER_TEXTURE_ICON_L) / 2) % 2), 1);
				}
            };
		} catch (IOException e) {
			System.out.println("Could not load the save screen information!");
		} catch (Exception e) {
			System.out.println(option.toString() + " in save selection screen failed to load!");
        }
        return 0;
	}

	public static void deleteSaveFile(int saveSlot) {
		try {
			String path = String.valueOf(SaveFile.save[saveSlot]);
			path = path.substring(0, path.lastIndexOf("."));
			SaveFile.save[saveSlot].renameTo(new File(path + "B.png"));
		} catch (SecurityException e) {
			System.out.println("Could not delete " + SaveFile.save[saveSlot].getPath());
		}
	}

	public static boolean backupExists(int saveSlot) {
		return new File(String.valueOf(SaveFile.save[saveSlot]).substring(0, String.valueOf(SaveFile.save[saveSlot]).lastIndexOf(".")) + "B.png").exists();
	}
}
