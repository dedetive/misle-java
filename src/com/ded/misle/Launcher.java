package com.ded.misle;

import com.ded.misle.audio.AudioFile;
import com.ded.misle.game.GamePanel;
import com.ded.misle.core.LanguageManager;

import javax.swing.*;

import static com.ded.misle.audio.AudioFile.consume_small_pot;
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
	public static boolean levelDesigner;
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
		displayMoreInfo = getSetting("displayMoreInfo");

		// WINDOW CONFIGS

		windowTitle = "Misle";
	}

	public static void main(String[] args) {

		loadMainMenu();
		AudioFile loadDummy = consume_small_pot; // Preloading AudioFile enum so it doesn't lag when first using it

		SwingUtilities.invokeLater(() -> {
			GamePanel panel = new GamePanel();
			GamePanel.forceResize(screenSize.str());
			panel.showScreen();
			panel.startGameThread();

			GamePanel.setDebug(true);
		});
	}
}
