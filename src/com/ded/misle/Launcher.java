package com.ded.misle;

import com.ded.misle.core.GamePanel;
import com.ded.misle.core.LanguageManager;
import com.ded.misle.core.Setting;

import javax.swing.*;

import static com.ded.misle.core.Setting.isFullscreen;
import static com.ded.misle.core.Setting.screenSize;
import static com.ded.misle.core.SettingsManager.getSetting;
import static com.ded.misle.core.SettingsManager.updateSetting;

/**
 * This launches the game. Only ever run the game from this class.
 *
 * @author ded
 */
public class Launcher {

	// VARIABLE INITIALIZATIONS

	public static String windowTitle;
	public static String fullscreenMode;
	public static boolean displayFPS;
	public static int frameRateCap;
	public static double scale;
	public static boolean levelDesigner;
	public static String languageCode;
	public static boolean heldItemFollowsMouse;
	public static LanguageManager languageManager;
	public static boolean antiAliasing;
	public static String displayMoreInfo;

	/**
	 * Loads main menu.
	 * <p>
	 * Never use this after the game is loaded.
	 * <p>
	 * I do not know what would happen if I did and I'm scared to try it out.
	 */

	public static void loadMainMenu() {

		// SETTINGS GETTERS

		updateSetting(screenSize);
		updateSetting(isFullscreen);
		fullscreenMode = getSetting("fullscreenMode");
		displayFPS = Boolean.parseBoolean(getSetting("displayFPS"));
		antiAliasing = Boolean.parseBoolean(getSetting("antiAliasing"));
		frameRateCap = Integer.parseInt(getSetting("frameRateCap"));
		languageCode = getSetting("language");
        languageManager = new LanguageManager(languageCode);
        levelDesigner = Boolean.parseBoolean(getSetting("levelDesigner"));
		heldItemFollowsMouse = Boolean.parseBoolean(getSetting("heldItemFollowsMouse"));
		displayMoreInfo = getSetting("displayMoreInfo");

		// WINDOW CONFIGS

		windowTitle = "Misle";

		scale = 2;
	}

	public static void main(String[] args) {

		loadMainMenu();

		SwingUtilities.invokeLater(() -> {
			GamePanel panel = new GamePanel();
			panel.showScreen();
			panel.startGameThread();
		});
	}
}
