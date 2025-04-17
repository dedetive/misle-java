package com.ded.misle;

import com.ded.misle.audio.AudioPlayer;
import com.ded.misle.core.GamePanel;
import com.ded.misle.core.LanguageManager;

import javax.swing.*;

import static com.ded.misle.audio.AudioPlayer.AudioFile.consume_small_pot;
import static com.ded.misle.core.Setting.*;
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
	public static double scale;
	public static boolean levelDesigner;
	public static boolean heldItemFollowsMouse;
	public static LanguageManager languageManager;
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
		updateSetting(fullscreenMode);
		updateSetting(displayFPS);
		updateSetting(antiAliasing);
		updateSetting(frameRateCap);
		updateSetting(languageCode);
        languageManager = new LanguageManager(languageCode.str());
        levelDesigner = Boolean.parseBoolean(getSetting("levelDesigner"));
		heldItemFollowsMouse = Boolean.parseBoolean(getSetting("heldItemFollowsMouse"));
		displayMoreInfo = getSetting("displayMoreInfo");

		// WINDOW CONFIGS

		windowTitle = "Misle";

		scale = 2;
	}

	public static void main(String[] args) {

		loadMainMenu();
		AudioPlayer.AudioFile loadDummy = consume_small_pot; // Preloading AudioFile enum so it doesn't lag when first using it

		SwingUtilities.invokeLater(() -> {
			GamePanel panel = new GamePanel();
			panel.showScreen();
			panel.startGameThread();
		});
	}
}
