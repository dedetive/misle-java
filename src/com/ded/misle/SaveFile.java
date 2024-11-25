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

	public enum PixelColor { RED, GREEN, BLUE }

	public enum PixelData {
		MAX_HP_HIGHEST(PixelColor.BLUE, 82, 10),
		MAX_HP_HIGH(PixelColor.GREEN, 30, 127),
		MAX_HP_LOW(PixelColor.RED, 99, 1),
		SPAWNPOINT_HIGH(PixelColor.RED, 69, 42),
		SPAWNPOINT_LOW(PixelColor.BLUE, 42, 69);

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
					player.attr.setMaxHP(playerMaxHP);

					// Load spawnpoint

					int spawnpointHigh = loadThis(PixelData.SPAWNPOINT_HIGH);
					int spawnpointLow = loadThis(PixelData.SPAWNPOINT_LOW);
					int spawnpoint = spawnpointHigh * 255 + spawnpointLow;
					player.pos.setSpawnpoint(Math.max(spawnpoint, 1));
					player.pos.reloadSpawnpoint();

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

		switch (color) {
			case PixelColor.RED:
				return pixel.getRed();
			case PixelColor.GREEN:
				return pixel.getGreen();
			case PixelColor.BLUE:
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
			brandIntoSaveFile(maxHPHighest, PixelData.MAX_HP_HIGHEST);
			brandIntoSaveFile(maxHPHigh, PixelData.MAX_HP_HIGH);
			brandIntoSaveFile(maxHPLow, PixelData.MAX_HP_LOW);

			// Spawnpoint

			int spawnpointHigh = (player.pos.getSpawnpoint() / 255) % 255;
			int spawnpointLow = player.pos.getSpawnpoint() % 255;
			brandIntoSaveFile(spawnpointHigh, PixelData.SPAWNPOINT_HIGH);
			brandIntoSaveFile(spawnpointLow, PixelData.SPAWNPOINT_LOW);

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
			case PixelColor.RED:
				image.setRGB(x, y, new Color(value, green, blue).getRGB());
				break;
			case PixelColor.GREEN:
				image.setRGB(x, y, new Color(red, value, blue).getRGB());
				break;
			case PixelColor.BLUE:
				image.setRGB(x, y, new Color(red, green, value).getRGB());
				break;
			default:
				System.out.println("Invalid color parameter for branding to the save file: " + color);
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
}
