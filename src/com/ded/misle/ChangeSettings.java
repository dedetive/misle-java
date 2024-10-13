package com.ded.misle;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * This is for changing settings (use changeThis()) and for getting the path of the game (use getPath())
 */

public class ChangeSettings {

	/**
	 * Receives a setting and a value and changes it in the location you ask to. The file has to already exist. Every writing has to be exact.
	 *
	 * @param setting the setting you'd like to alter their value. E.g.: isFullscreen
	 * @param changeTo the value of the setting you'd like to change. E.g.: true
	 * @param location the position of the file. E.g.: For settings, go with settings.config
	 */

	public static void changeThis(String setting, String changeTo, String location) {
		File file = getPath();
		File settingsFile = new File(file, ""+ location);
		File tempFile = new File(file, "resources/temp.config");
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(String.valueOf(settingsFile))));
		     BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
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
		if (!settingsFile.delete()) {
			throw new RuntimeException("Could not delete the original settings file");
		}
		if (!tempFile.renameTo(new File(String.valueOf(settingsFile)))) {
			throw new RuntimeException("Could not rename the temp file to settings.config");
		}
	}

	/**
	 * Use this to get the path of the game, usually to equal to a variable. Has to be in a File format.
	 *
	 * @return the path of the game (com/ded/misle/) in a File format
	 */

	public static File getPath() {
		String workingDir = System.getProperty("user.dir"); // gets the current working directory
		if (!workingDir.contains("src/com/ded/misle")) {
			return new File(workingDir + "/src/com/ded/misle");
		} else {
			return new File(workingDir);
		}
	}


	/**
	 * Receives a setting you want to know the value of and returns it's value in a String format. The parameter writing has to be exact.
	 *
	 * @param args the setting you want to know the value of
	 * @return the value of a specific setting from settings.config in a String format
	 */

	static String getSetting(String args) {

		// GET INFO FROM SETTINGS.CONFIG

		File file = new File(ChangeSettings.getPath() + "/resources/settings.config");
		String result = "";
		try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(file)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(args)) {
					result = line.split("= ")[1];
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (Objects.equals(result, "")) {
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
			case "isFullscreen":
				defaultSetting = "false";
				break;
			case "fullscreenMode":
				defaultSetting = "windowed";
				break;
			case "frameRateCap":
				defaultSetting = "60";
				break;
			case "displayFPS":
				defaultSetting = "false";
				break;
		}
		return defaultSetting;
	}
}
