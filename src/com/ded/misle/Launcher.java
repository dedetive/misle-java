package com.ded.misle;

import javax.swing.*;

import static com.ded.misle.ChangeSettings.getSetting;
import static com.ded.misle.SaveFile.loadSaveFile;

/**
 * This launches the game. Only ever run the game from this class.
 *
 * @author ded
 */
public class Launcher {

	// VARIABLE INITIALIZATIONS

	static String windowTitle;
	static boolean isFullscreen;
	static String fullscreenMode;
	static boolean displayFPS;
	static int frameRateCap;
	public static double scale;
	public static boolean levelDesigner;

	private static String previousScreenSize = "";

	/**
	 * Loads main menu.
	 * <p>
	 * Never use this after the game is loaded.
	 * <p>
	 * I do not know what would happen if I did and I'm scared to try it out.
	 */

	public static void loadMainMenu() {

		// VARIABLE DECLARATIONS

		boolean screenSizeChanged = false;

		String screenSize = getSetting("screenSize");
		isFullscreen = Boolean.parseBoolean(getSetting("isFullscreen"));
		fullscreenMode = getSetting("fullscreenMode");
//		displayFPS = Boolean.parseBoolean(getSetting("displayFPS"));
		frameRateCap = Integer.parseInt(getSetting("frameRateCap"));
		String languageCode = getSetting("language");
		LanguageManager languageManager = new LanguageManager(languageCode);
		levelDesigner = Boolean.parseBoolean(getSetting("levelDesigner"));

		if (!previousScreenSize.equals(screenSize)) {
			screenSizeChanged = true;
		}

		previousScreenSize = screenSize;

		// WINDOW CONFIGS

		windowTitle = "Misle";

		// SCREEN SIZE

		if (screenSizeChanged) {
			switch (screenSize) {
				case "small":
					scale = 1.5; // 768x576
					break;
				case "big":
					scale = 3.125; // 1600x1200
					break;
				case "huge":
					scale = 3.75; // 1920x1440
					break;
				case "tv-sized":
					scale = 5; // 2560x1920
					break;
				case "comical":
					scale = 15; // 7680x5760
					break;
				case "medium":
				case null, default:
					scale = 2; // 1024x768
					break;
			}
		}
	}

	public static void main(String[] args) {

		loadMainMenu();

		SwingUtilities.invokeLater(() -> {
			GamePanel mainGamePanel = new GamePanel();
			mainGamePanel.showScreen();
			mainGamePanel.startGameThread();
		});
	}
}
