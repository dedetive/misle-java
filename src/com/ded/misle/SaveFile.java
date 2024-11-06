package com.ded.misle;

import com.ded.misle.items.Item;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import static com.ded.misle.ChangeSettings.getPath;
import static com.ded.misle.GamePanel.player;

public class SaveFile {

	// FILE PATH FOR .PNG
	private static final Path filePath = getPath().resolve("savefile.png");
	private static final File save = filePath.toFile();

	// IMAGE

	static BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

	public static void loadSaveFile() {
		boolean fileExists = checkIfSaveFileExists();

		if (fileExists) {
			try {
				image = ImageIO.read(save);

				// Load maxHP
				int maxHPHighest = loadThis("blue", 82, 10);
				int maxHPHigh = loadThis("green", 30, 127);
				int maxHPLow = loadThis("red", 99, 1);
				double playerMaxHP = 255 * 255 * maxHPHighest + 255 * maxHPHigh + maxHPLow;
				player.attr.setPlayerMaxHP(playerMaxHP);

				// Load spawnpoint

				int spawnpointXHighest = loadThis("red", 42, 69);
				int spawnpointXHigh = loadThis("red", 69, 42);
				int spawnpointXLow = loadThis("blue", 42, 69);
				int spawnpointYHighest = loadThis("blue", 69, 42);
				int spawnpointYHigh = loadThis("green", 69, 42);
				int spawnpointYLow = loadThis("green", 42,69);
				double spawnpointX = spawnpointXHighest * 255 * 255 + spawnpointXHigh * 255 + spawnpointXLow;
				double spawnpointY = spawnpointYHighest * 255 * 255 + spawnpointYHigh * 255 + spawnpointYLow;
				player.pos.setSpawnpoint(spawnpointX, spawnpointY);
				player.pos.reloadSpawnpoint();

				// Load inventory

				int[][][] tempInventory = new int[4][7][4];
				for (int i = 0 ; i < 4 ; i++) {
					for (int j = 0 ; j < 7 ; j++) {
						tempInventory[i][j][0] = loadThis("red", i, j + 15);
						tempInventory[i][j][1] = loadThis("green", i, j + 15);
							// [i][j][0] = ID
						tempInventory[i][j][0] = tempInventory[i][j][0] * 255 + tempInventory[i][j][1];

						tempInventory[i][j][2] = loadThis("blue", i, j + 15);
						tempInventory[i][j][3] = loadThis("red", j + 15, i);
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

	/**
	 *
	 * @param color either red, green or blue of the pixel
	 * @param x x position of the pixel
	 * @param y y position of the pixel
	 * @return
	 */
	private static int loadThis(String color, int x, int y) {
		try {
			image = ImageIO.read(save);

			if (x < 0 || x > 128 || y < 0 || y > 128) {
				System.out.println("Invalid x or y position for loading from the save file: " + x + ", " + y);
			}

			Color pixel = new Color(image.getRGB(x, y));

			switch (color.toLowerCase()) {
				case "red":
					return pixel.getRed();
				case "green":
					return pixel.getGreen();
				case "blue":
					return pixel.getBlue();
				default:
					System.out.println("Invalid color parameter for loading from the save file: " + color);
					return 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}


	public static void saveEverything() {

		// Max HP

		int maxHPHighest = ((int) player.attr.getPlayerMaxHP() / (255 * 255)) % 255;
		int maxHPHigh = ((int) player.attr.getPlayerMaxHP() / 255) % 255;
		int maxHPLow = (int) player.attr.getPlayerMaxHP() % 255;
		brandIntoSaveFile(maxHPHighest, "blue", 82, 10);
		brandIntoSaveFile(maxHPHigh, "green", 30, 127);
		brandIntoSaveFile(maxHPLow, "red", 99, 1);

		// Spawnpoint

		int spawnpointXHighest = ((int) player.pos.getSpawnpoint()[0] / (255 * 255)) % 255;
		int spawnpointXHigh = ((int) player.pos.getSpawnpoint()[0] / 255) % 255;
		int spawnpointXLow = (int) player.pos.getSpawnpoint()[0] % 255;
		int spawnpointYHighest = ((int) player.pos.getSpawnpoint()[1] / (255 * 255)) % 255;
		int spawnpointYHigh = ((int) player.pos.getSpawnpoint()[1] / 255) % 255;
		int spawnpointYLow = (int) player.pos.getSpawnpoint()[1] % 255;
		brandIntoSaveFile(spawnpointXHighest, "red", 42, 69);
		brandIntoSaveFile(spawnpointXHigh, "red", 69, 42);
		brandIntoSaveFile(spawnpointXLow, "blue", 42, 69);
		brandIntoSaveFile(spawnpointYHighest, "blue", 69, 42);
		brandIntoSaveFile(spawnpointYHigh, "green", 69, 42);
		brandIntoSaveFile(spawnpointYLow, "green", 42, 69);
		
		// Inventory

		try {
			int[][][] tempInventory = new int[4][7][4];
			for (int i = 0 ; i < 4 ; i++) {
				for (int j = 0 ; j < 7 ; j++) {
					if (player.inv.getItem(i, j) == null) {
						tempInventory[i][j][0] = 0;
						tempInventory[i][j][1] = 0;
						tempInventory[i][j][2] = 0;
						tempInventory[i][j][3] = 0;
					} else {
						tempInventory[i][j][0] = (player.inv.getItem(i, j).getId() / 255) % 255;
						tempInventory[i][j][1] = player.inv.getItem(i, j).getId() % 255;
						tempInventory[i][j][2] = (player.inv.getItem(i, j).getCount() / 255) % 255;
						tempInventory[i][j][3] = player.inv.getItem(i, j).getCount() % 255;
					}
					brandIntoSaveFile(tempInventory[i][j][0], "red", i, j + 15);
					brandIntoSaveFile(tempInventory[i][j][1], "green", i, j + 15);
					brandIntoSaveFile(tempInventory[i][j][2], "blue", i, j + 15);
					brandIntoSaveFile(tempInventory[i][j][3], "red", j + 15, i);
				}
			}
		} catch (NullPointerException e) {
			// If the game hadn't been started before quitting, this just means the inventory was not loaded yet.
		}
	}

	private static void brandIntoSaveFile(int value, String color, int x, int y) {

		if (value > 255 || value < 0) {
			throw new IllegalArgumentException("Value must be between 0 and 255. Value inserted: " + value);
		}

		Color previousValue = new Color(image.getRGB(x, y));
		int red = previousValue.getRed();
		int green = previousValue.getGreen();
		int blue = previousValue.getBlue();

		switch (color.toLowerCase()) {
			case "red":
				image.setRGB(x, y, new Color(value, green, blue).getRGB());
				break;
			case "green":
				image.setRGB(x, y, new Color(red, value, blue).getRGB());
				break;
			case "blue":
				image.setRGB(x, y, new Color(red, green, value).getRGB());
				break;
			default:
				System.out.println("Invalid color parameter for branding to the save file: " + color);
		}


		try {
			ImageIO.write(image, "png", SaveFile.save);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
