package com.ded.misle;

import com.ded.misle.items.Item;

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

	public enum pixelColor { RED, GREEN, BLUE }

	public static void loadSaveFile() {

		synchronized (fileLock) {
			boolean fileExists = checkIfSaveFileExists();

			if (fileExists) {
				try {
					image = ImageIO.read(save);

					// Load maxHP
					int maxHPHighest = loadThis(pixelColor.BLUE, 82, 10);
					int maxHPHigh = loadThis(pixelColor.GREEN, 30, 127);
					int maxHPLow = loadThis(pixelColor.RED, 99, 1);
					double playerMaxHP = 255 * 255 * maxHPHighest + 255 * maxHPHigh + maxHPLow;
					player.attr.setMaxHP(playerMaxHP);

					// Load spawnpoint

					int spawnpointHigh = loadThis(pixelColor.RED, 69, 42);
					int spawnpointLow = loadThis(pixelColor.BLUE, 42, 69);
					int spawnpoint = spawnpointHigh * 255 + spawnpointLow;
					player.pos.setSpawnpoint(Math.max(spawnpoint, 1));
					player.pos.reloadSpawnpoint();

					// Load inventory

					int[][][] tempInventory = new int[4][7][4];
					for (int i = 0; i < 4; i++) {
						for (int j = 0; j < 7; j++) {
							tempInventory[i][j][0] = loadThis(pixelColor.RED, i, j + 15);
							tempInventory[i][j][1] = loadThis(pixelColor.GREEN, i, j + 15);
							// [i][j][0] = ID
							tempInventory[i][j][0] = tempInventory[i][j][0] * 255 + tempInventory[i][j][1];

							tempInventory[i][j][2] = loadThis(pixelColor.BLUE, i, j + 15);
							tempInventory[i][j][3] = loadThis(pixelColor.RED, j + 15, i);
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

	/**
	 *
	 * @param color either red, green or blue of the pixel
	 * @param x x position of the pixel
	 * @param y y position of the pixel
	 * @return
	 */
	private static int loadThis(pixelColor color, int x, int y) {
		if (x < 0 || x > 128 || y < 0 || y > 128) {
			System.out.println("Invalid x or y position for loading from the save file: " + x + ", " + y);
		}

		Color pixel = new Color(image.getRGB(x, y));

		switch (color) {
			case pixelColor.RED:
				return pixel.getRed();
			case pixelColor.GREEN:
				return pixel.getGreen();
			case pixelColor.BLUE:
				return pixel.getBlue();
			default:
				System.out.println("Invalid color parameter for loading from the save file: " + color);
				return 0;
		}

	}


	public static void saveEverything() {

		synchronized (fileLock) {

			// Max HP

			int maxHPHighest = ((int) player.attr.getMaxHP() / (255 * 255)) % 255;
			int maxHPHigh = ((int) player.attr.getMaxHP() / 255) % 255;
			int maxHPLow = (int) player.attr.getMaxHP() % 255;
			brandIntoSaveFile(maxHPHighest, pixelColor.BLUE, 82, 10);
			brandIntoSaveFile(maxHPHigh, pixelColor.GREEN, 30, 127);
			brandIntoSaveFile(maxHPLow, pixelColor.RED, 99, 1);

			// Spawnpoint

			int spawnpointHigh = (player.pos.getSpawnpoint() / 255) % 255;
			int spawnpointLow = player.pos.getSpawnpoint() % 255;
			brandIntoSaveFile(spawnpointHigh, pixelColor.RED, 69, 42);
			brandIntoSaveFile(spawnpointLow, pixelColor.BLUE, 42, 69);

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
							tempInventory[i][j][0] = (player.inv.getItem(i, j).getId() / 255) % 255;
							tempInventory[i][j][1] = player.inv.getItem(i, j).getId() % 255;
							tempInventory[i][j][2] = (player.inv.getItem(i, j).getCount() / 255) % 255;
							tempInventory[i][j][3] = player.inv.getItem(i, j).getCount() % 255;
						}
						brandIntoSaveFile(tempInventory[i][j][0], pixelColor.RED, i, j + 15);
						brandIntoSaveFile(tempInventory[i][j][1], pixelColor.GREEN, i, j + 15);
						brandIntoSaveFile(tempInventory[i][j][2], pixelColor.BLUE, i, j + 15);
						brandIntoSaveFile(tempInventory[i][j][3], pixelColor.RED, j + 15, i);
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

	private static void brandIntoSaveFile(int value, pixelColor color, int x, int y) {

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
			case pixelColor.RED:
				image.setRGB(x, y, new Color(value, green, blue).getRGB());
				break;
			case pixelColor.GREEN:
				image.setRGB(x, y, new Color(red, value, blue).getRGB());
				break;
			case pixelColor.BLUE:
				image.setRGB(x, y, new Color(red, green, value).getRGB());
				break;
			default:
				System.out.println("Invalid color parameter for branding to the save file: " + color);
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
