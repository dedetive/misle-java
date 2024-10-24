package com.ded.misle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import javax.imageio.ImageIO;

import static com.ded.misle.ChangeSettings.getPath;
import static com.ded.misle.GamePanel.player;

public class SaveFile {

	// FILE PATH FOR .PNG
	private static final String filePath = getPath() + File.separator + "savefile.png";
	private static final File save = new File(filePath);

	// IMAGE

	static BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

	public static void loadSaveFile() {
		boolean fileExists = checkIfSaveFileExists();

		if (fileExists) {
			try {
				image = ImageIO.read(save);

				// Load HP
				int hpHigh = new Color(image.getRGB(30, 127)).getBlue(); // High byte
				int hpLow = new Color(image.getRGB(99, 0)).getBlue();    // Low byte
				double playerHP = 255 * hpHigh + hpLow;
				player.attr.setPlayerHP(playerHP);

				// Load maxHP
				int maxHPHigh = new Color(image.getRGB(30, 127)).getGreen(); // High byte
				int maxHPLow = new Color(image.getRGB(99, 1)).getRed();      // Low byte
				double playerMaxHP = 255 * maxHPHigh + maxHPLow;
				player.attr.setPlayerMaxHP(playerMaxHP);

				// Load spawnpoint

				int spawnpointXHighest = new Color(image.getRGB(42, 69)).getRed();
				int spawnpointXHigh = new Color(image.getRGB(69, 42)).getRed();
				int spawnpointXLow = new Color(image.getRGB(42, 69)).getBlue();

				int spawnpointYHighest = new Color(image.getRGB(69, 42)).getBlue();
				int spawnpointYHigh = new Color(image.getRGB(69, 42)).getGreen();
				int spawnpointYLow = new Color(image.getRGB(42, 69)).getGreen();

				double spawnpointX = spawnpointXHighest * 255 * 255 + spawnpointXHigh * 255 + spawnpointXLow;
				double spawnpointY = spawnpointYHighest * 255 * 255 + spawnpointYHigh * 255 + spawnpointYLow;
				player.pos.setSpawnpoint(spawnpointX, spawnpointY);
				player.pos.reloadSpawnpoint();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Save file does not exist. Creating a blank one.");
		}
	}


	public static void saveEverything() {
		brandIntoSaveFile("hp", Double.toString(player.attr.getPlayerHP()));
		brandIntoSaveFile("maxHP", Double.toString(player.attr.getPlayerMaxHP()));
		brandIntoSaveFile("spawnpoint", Arrays.toString(player.pos.getSpawnpoint()));
	}

	private static void brandIntoSaveFile(String key, String value) {
		try {
			for (int i = 0; i < image.getWidth(); i++) {
				image.setRGB(i, i, new Color(254, 197, 229).getRGB()); // For guaranteeing the image has not been altered
			}
			int[] pos = {0, 0};

			if (Objects.equals(key, "hp")) {
				value = value.split("\\.")[0];
				int hp = Integer.parseInt(value);
				pos[0] = 30;
				pos[1] = 127;
				image.setRGB(pos[0], pos[1], new Color(new Color(image.getRGB(pos[0], pos[1])).getRed(), new Color(image.getRGB(pos[0], pos[1])).getGreen(), hp / 255).getRGB());

				pos[0] = 99;
				pos[1] = 0;
				image.setRGB(pos[0], pos[1], new Color(new Color(image.getRGB(pos[0], pos[1])).getRed(), new Color(image.getRGB(pos[0], pos[1])).getGreen(), hp % 255).getRGB());
			} else if (Objects.equals(key, "maxHP")) {
				value = value.split("\\.")[0];
				int maxHP = Integer.parseInt(value);
				pos[0] = 30;
				pos[1] = 127;
				image.setRGB(pos[0], pos[1], new Color(new Color(image.getRGB(pos[0], pos[1])).getRed(), maxHP / 255, new Color(image.getRGB(pos[0], pos[1])).getBlue()).getRGB());

				pos[0] = 99;
				pos[1] = 1;
				image.setRGB(pos[0], pos[1], new Color(maxHP % 255, new Color(image.getRGB(pos[0], pos[1])).getGreen(), new Color(image.getRGB(pos[0], pos[1])).getBlue()).getRGB());
			}  else if (Objects.equals(key, "spawnpoint")) {
				pos[0] = 42;
				pos[1] = 69;
				image.setRGB(pos[0], pos[1], new Color((int) (player.pos.getSpawnpoint()[0] / (255 * 255)), new Color(image.getRGB(pos[0], pos[1])).getGreen(), new Color(image.getRGB(pos[0], pos[1])).getBlue()).getRGB());

				pos[0] = 69;
				pos[1] = 42;
				image.setRGB(pos[0], pos[1], new Color((int) (player.pos.getSpawnpoint()[0] / 255), new Color(image.getRGB(pos[0], pos[1])).getGreen(), new Color(image.getRGB(pos[0], pos[1])).getBlue()).getRGB());

				pos[0] = 42;
				pos[1] = 69;
				image.setRGB(pos[0], pos[1], new Color(new Color(image.getRGB(pos[0], pos[1])).getRed(), new Color(image.getRGB(pos[0], pos[1])).getGreen(), (int) (player.pos.getSpawnpoint()[0] % 255)).getRGB());

				pos[0] = 69;
				pos[1] = 42;
				image.setRGB(pos[0], pos[1], new Color(new Color(image.getRGB(pos[0], pos[1])).getRed(), new Color(image.getRGB(pos[0], pos[1])).getGreen(), (int) (player.pos.getSpawnpoint()[1] / (255 * 255))).getRGB());

				pos[0] = 69;
				pos[1] = 42;
				image.setRGB(pos[0], pos[1], new Color(new Color(image.getRGB(pos[0], pos[1])).getRed(), (int) (player.pos.getSpawnpoint()[1] / 255), new Color(image.getRGB(pos[0], pos[1])).getBlue()).getRGB());

				pos[0] = 42;
				pos[1] = 69;
				image.setRGB(pos[0], pos[1], new Color(new Color(image.getRGB(pos[0], pos[1])).getRed(), (int) (player.pos.getSpawnpoint()[1] % 255), new Color(image.getRGB(pos[0], pos[1])).getBlue()).getRGB());
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid key or value: " + key + ", " + value);
		}
		try {
			ImageIO.write(image, "png", SaveFile.save);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean checkIfSaveFileExists() {
		if (!SaveFile.save.exists()) {
			System.out.println("Save file not found. Creating a new, base PNG...");

			try {
				for (int i = 0; i < image.getWidth(); i++) {
					image.setRGB(i, i, new Color(254, 197, 229).getRGB()); // For guaranteeing the image has not been altered
				}                                   // 0xFEC5E5

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
