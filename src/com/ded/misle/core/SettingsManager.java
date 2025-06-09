package com.ded.misle.core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.core.Setting.*;
import static com.ded.misle.renderer.FontManager.updateFontScript;

/**
 * This is for changing settings (use changeThis()) and for getting the path of the game (use getPath())
 */

public class SettingsManager {

	/**
	 * Receives a setting and a value and changes it in the location you ask to. The file has to already exist. Every writing has to be exact.
	 *
	 * @param setting the setting you'd like to alter their value. E.g.: isFullscreen
	 * @param changeTo the value of the setting you'd like to change. E.g.: true
	 */

	public static void changeSetting(String setting, String changeTo) {
		Path file = com.ded.misle.core.Path.getPath(com.ded.misle.core.Path.PathTag.RESOURCES);
		Path settingsFile = com.ded.misle.core.Path.getPath(com.ded.misle.core.Path.PathTag.CONFIG);
		Path tempFile = file.resolve("temp.config");

		try (BufferedReader reader = Files.newBufferedReader(settingsFile);
		     BufferedWriter writer = Files.newBufferedWriter(tempFile)) {

			boolean foundLine = false;
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(setting)) {
					setting = line.split(" = ")[0];
					line = setting + " = " + changeTo;
					foundLine = true;
				}
				writer.write(line);
				writer.newLine();
			}
			if (!foundLine) {
				line = setting + " = " + changeTo;
				writer.write(line);
			}
		} catch (IOException e) {
			try {
				Files.createFile(settingsFile);
				changeSetting(setting, changeTo);
				return;
			} catch (IOException exc) {
				throw new RuntimeException("Could not create settings file", exc);
			}
		}

		try {
			Files.delete(settingsFile);
			Files.move(tempFile, settingsFile);
		} catch (IOException e) {
			throw new RuntimeException("Could not replace the original settings file", e);
		}
	}

	public static void updateSetting(Setting<?> setting) {
		Path file = com.ded.misle.core.Path.getPath(com.ded.misle.core.Path.PathTag.CONFIG);
		String result = "";

		try (BufferedReader reader = Files.newBufferedReader(file)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(setting.name)) {
					try {
						result = line.split("= ")[1];
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Setting " + setting.name + " not found in settings.config file.");
					}
				}
			}
		} catch (IOException e) {
			result = "";
		}

		if (result.isEmpty()) {
			result = setting.defaultValue.toString();
		}

		try {
			setting.value = result;
		} catch (ClassCastException e) {
			setting.value = setting.defaultValue;
		}
	}

	/**
	 * Receives a setting you want to know the value of and returns it's value in a String format. The parameter writing has to be exact.
	 *
	 * @param args the setting you want to know the value of
	 * @return the value of a specific setting from settings.config in a String format
	 */
	public static String getSetting(String args) {
		Path file = com.ded.misle.core.Path.getPath(com.ded.misle.core.Path.PathTag.CONFIG);
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
			result = "";
		}

		if (result.isEmpty()) {
			result = getDefault(args);
		}
		return result;
	}

	@Deprecated
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
			case "displayMoreInfo":
				defaultSetting = "false";
				break;
			case "antiAliasing":
			case "heldItemFollowsMouse":
				defaultSetting = "true";
				break;
			case "language":
				defaultSetting = "en_US";
				break;
		}
		return defaultSetting;
	}

	// General
	public static void cycleLanguage() {
		String[] languageCodes = new String[]{"de_DE", "el_GR", "en_US", "es_ES", "pt_BR", "ru_RU", "zh_CN"};
		languageCode.value =
			LanguageManager.Language.valueOf(cycleThroughSetting(languageCodes, languageCode.str()));

		changeSetting("language", languageCode.str());
		languageManager = new LanguageManager(languageCode.str());
		updateFontScript();
	}

	// Graphics
	public static void cycleScreenSize() {
		String[] screenSizes = new String[]{"small", "medium", "big", "huge"};
		screenSize.value = cycleThroughSetting(screenSizes, screenSize.str());

		changeSetting("screenSize", screenSize.str());
		forceResize(screenSize.str());
	}

	public static void cycleIsFullscreen() {
		isFullscreen.value = cycleBoolean("isFullscreen", isFullscreen.bool());
		forceResize(screenSize.str());
	}

	public static void cycleFullscreenMode() {
		String[] modes = new String[]{"windowed", "exclusive"};
		fullscreenMode.value = cycleThroughSetting(modes, fullscreenMode.str());

		changeSetting("fullscreenMode", fullscreenMode.str());
		forceResize(screenSize.str());
	}

	public static void cycleDisplayFPS() {
		displayFPS.value = cycleBoolean("displayFPS", displayFPS.bool());
	}

	public static void cycleFrameRateCap() {
		String[] modes = new String[]{"30", "60", "90", "120", "160"};
		frameRateCap.value = Integer.parseInt(cycleThroughSetting(modes, frameRateCap.str()));

		changeSetting("frameRateCap", frameRateCap.str());
		nsPerFrame = 1000000000.0 / Math.clamp(frameRateCap.integer(), 30, 144);
	}

	public static void cycleAntiAliasing() {
		antiAliasing.value = cycleBoolean("antiAliasing", antiAliasing.bool());
	}

	// Gameplay

	// General-use methods
	public static boolean cycleBoolean(String setting, boolean currentValue) {
		boolean cycledResult = !currentValue;

		changeSetting(setting, String.valueOf(cycledResult));

		return cycledResult;
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

	public static void cycleDisplayMoreInfo() {
		String[] modes = new String[]{"false", "exact", "percentage"};
		displayMoreInfo = cycleThroughSetting(modes, displayMoreInfo);

		changeSetting("displayMoreInfo", displayMoreInfo);
	}
}
