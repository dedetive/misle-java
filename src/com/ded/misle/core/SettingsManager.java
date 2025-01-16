package com.ded.misle.core;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.core.GamePanel.forceResize;
import static com.ded.misle.renderer.FontManager.updateFontSizes;

/**
 * This is for changing settings (use changeThis()) and for getting the path of the game (use getPath())
 */

public class SettingsManager {

	/**
	 * Receives a setting and a value and changes it in the location you ask to. The file has to already exist. Every writing has to be exact.
	 *
	 * @param setting the setting you'd like to alter their value. E.g.: isFullscreen
	 * @param changeTo the value of the setting you'd like to change. E.g.: true
	 * @param location the position of the file. E.g.: For settings, go with settings.config
	 */

	public static void changeSetting(String setting, String changeTo, Path location) {
		Path file = getPath();
		Path settingsFile = file.resolve(location);
		Path tempFile = file.resolve("resources/temp.config");

		try (BufferedReader reader = Files.newBufferedReader(settingsFile);
		     BufferedWriter writer = Files.newBufferedWriter(tempFile)) {

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(setting)) {
					setting = line.split("= ")[0];
					line = setting + "= " + changeTo;
				}
				writer.write(line);
				writer.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			Files.delete(settingsFile);
			Files.move(tempFile, settingsFile);
		} catch (IOException e) {
			throw new RuntimeException("Could not replace the original settings file", e);
		}
	}


	/**
	 * Use this to get the path of the game, usually to equal to a variable. Has to be in a File format.
	 *
	 * @return the path of the game (com/ded/misle/) in a File format
	 */
	public static Path getPath() {
		Path workingDir = Paths.get(System.getProperty("user.dir"));
		Path srcPath = workingDir.resolve("src/com/ded/misle");
		Path outPath = workingDir.resolve("com/ded/misle");

		if (Files.exists(srcPath)) {
			return srcPath;
		} else if (Files.exists(outPath)) {
			return outPath;
		} else {
			throw new RuntimeException("settings.config file not found");
		}
	}


	/**
	 * Receives a setting you want to know the value of and returns it's value in a String format. The parameter writing has to be exact.
	 *
	 * @param args the setting you want to know the value of
	 * @return the value of a specific setting from settings.config in a String format
	 */
	public static String getSetting(String args) {
		Path file = getPath().resolve("resources/settings.config");
		String result = "";

		try (BufferedReader reader = Files.newBufferedReader(file)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(args)) {
					try {
						result = line.split("= ")[1];
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Setting " + args + " not found in settings.config file.");
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (result.isEmpty()) {
			result = getDefault(args);
		}
		return result;
	}


	private static String getDefault(String args) {
		String defaultSetting = "";
		switch (args) {
			case "screenSize":
				defaultSetting = "medium";
				break;
			case "fullscreenMode":
				defaultSetting = "windowed";
				break;
			case "frameRateCap":
				defaultSetting = "60";
				break;
			case "isFullscreen":
			case "levelDesigner":
			case "displayFPS":
				defaultSetting = "false";
				break;
			case "antiAliasing":
				defaultSetting = "true";
				break;
			case "language":
				defaultSetting = "en_US";
				break;
		}
		return defaultSetting;
	}

	public static void cycleLanguage() {
		String[] languageCodes = new String[]{"de_DE", "el_GR", "en_US", "es_ES", "pt_BR", "ru_RU", "zh_CN"};
		languageCode = cycleThroughSetting(languageCodes, languageCode);

		changeSetting("language", languageCode, getPath().resolve("resources/settings.config"));
		languageManager = new LanguageManager(languageCode);
		updateFontSizes();
	}

	public static void cycleScreenSize() {
		String[] screenSizes = new String[]{"small", "medium", "big", "huge"};
		screenSize = cycleThroughSetting(screenSizes, screenSize);

		changeSetting("screenSize", screenSize, getPath().resolve("resources/settings.config"));
		forceResize(screenSize);
	}

	public static void cycleHeldItemFollowsMouse() {
		String[] options = new String[]{"true", "false"};
		heldItemFollowsMouse = Boolean.parseBoolean(cycleThroughSetting(options, String.valueOf(heldItemFollowsMouse)));

		changeSetting("heldItemFollowsMouse", String.valueOf(heldItemFollowsMouse), getPath().resolve("resources/settings.config"));
	}

	private static String cycleThroughSetting(String[] possibleValues, String currentValue) {
		String value;
		for (int i = 0; i < possibleValues.length; i++) {
			if (possibleValues[i].equals(currentValue)) {
				try {
					value = possibleValues[i + 1];
				} catch (ArrayIndexOutOfBoundsException e) {
					value = possibleValues[0];
				}
				return value;
			}
		}
		return possibleValues[0];
	}
}
