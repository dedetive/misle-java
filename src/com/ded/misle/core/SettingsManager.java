package com.ded.misle.core;

import com.ded.misle.world.player.PlayerAttributes;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.core.GamePanel.*;
import static com.ded.misle.core.Setting.screenSize;
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
	 */

	public static void changeSetting(String setting, String changeTo) {
		Path file = getPath();
		Path settingsFile = file.resolve("resources/settings.config");
		Path tempFile = file.resolve("resources/temp.config");

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

	public static void updateSetting(Setting setting) {
		Path file = getPath().resolve("resources/settings.config");
		String result = "";

		try (BufferedReader reader = Files.newBufferedReader(file)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(setting.toString())) {
					try {
						result = line.split("= ")[1];
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Setting " + setting.toString() + " not found in settings.config file.");
					}
				}
			}
		} catch (IOException e) {
			result = "";
		}

		if (result.isEmpty()) {
			result = setting.defaultValue.toString();
		}
		setting.value = result;
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
		languageCode = cycleThroughSetting(languageCodes, languageCode);

		changeSetting("language", languageCode);
		languageManager = new LanguageManager(languageCode);
		updateFontSizes();
	}

	// Graphics
	public static void cycleScreenSize() {
		String[] screenSizes = new String[]{"small", "medium", "big", "huge"};
		screenSize.value = cycleThroughSetting(screenSizes, String.valueOf(screenSize.value));

		changeSetting("screenSize", (String) screenSize.value);
		forceResize((String) screenSize.value);
	}

	public static void cycleIsFullscreen() {
		isFullscreen = cycleBoolean("isFullscreen", isFullscreen);
		forceResize((String) screenSize.value);
	}

	public static void cycleFullscreenMode() {
		String[] modes = new String[]{"windowed", "exclusive"};
		fullscreenMode = cycleThroughSetting(modes, fullscreenMode);

		changeSetting("fullscreenMode", fullscreenMode);
		forceResize((String) screenSize.value);
	}

	public static void cycleDisplayFPS() {
		displayFPS = cycleBoolean("displayFPS", displayFPS);
	}

	public static void cycleFrameRateCap() {
		String[] modes = new String[]{"30", "60", "90", "120", "160"};
		frameRateCap = Integer.parseInt(cycleThroughSetting(modes, String.valueOf(frameRateCap)));

		changeSetting("frameRateCap", String.valueOf(frameRateCap));
		nsPerFrame = 1000000000.0 / Math.clamp((double) frameRateCap, 30, 144);
		Timer wait = new Timer(500, e -> {
			player.attr.updateStat(PlayerAttributes.Stat.SPEED);
		});
		wait.setRepeats(false);
		wait.start();
	}

	public static void cycleAntiAliasing() {
		antiAliasing = cycleBoolean("antiAliasing", antiAliasing);
	}

	// Gameplay
	public static void cycleHeldItemFollowsMouse() {
		heldItemFollowsMouse = cycleBoolean("heldItemFollowsMouse", heldItemFollowsMouse);
	}

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
