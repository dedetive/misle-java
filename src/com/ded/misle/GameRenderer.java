package com.ded.misle;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.SaveFile.loadSaveFile;
import static com.ded.misle.boxes.BoxesHandling.storeCachedBoxes;
import static com.ded.misle.boxes.BoxesLoad.loadBoxes;
import static java.lang.System.currentTimeMillis;

import javax.swing.*;


public class GameRenderer {
	public static String previousMenu;
	public static String currentMenu;

	public static long startTime;
	public static final int LOADING_DURATION = 500;

	public static double textShadow = 1 * scale;

	public static void gameStart() {
		previousMenu = currentMenu;
		currentMenu = "PLAYING";
		startTime = currentTimeMillis();
		gameState = GameState.LOADING_MENU;

		loadSaveFile();
		loadBoxes();

		Timer timer = new Timer(LOADING_DURATION, e -> {
			for (int i = 15; i > 0; i--) {
				storeCachedBoxes(i);
			}
			player.stats.resetStartTimestamp();
			player.pos.reloadSpawnpoint();
			gameState = GameState.PLAYING;
		});

		timer.setRepeats(false); // Ensure the timer only runs once
		timer.start();
	}

	public static void softGameStart() {
		previousMenu = currentMenu;
		currentMenu = "PLAYING";
		gameState = GameState.PLAYING;
	}

	public static void enterLevelDesigner() {
		previousMenu = currentMenu;
		currentMenu = "LEVEL_DESIGNER";
		startTime = currentTimeMillis();
		gameState = GameState.LOADING_MENU;

		loadBoxes();

		Timer timer = new Timer(LOADING_DURATION, e -> {
			for (int i = 15; i > 0; i--) {
				storeCachedBoxes(i);
			}
			gameState = GameState.LEVEL_DESIGNER;
		});

		timer.setRepeats(false); // Ensure the timer only runs once
		timer.start();
	}

	public static void softEnterLevelDesigner() {
		previousMenu = currentMenu;
		currentMenu = "LEVEL_DESIGNER";
		gameState = GameState.LEVEL_DESIGNER;
	}
}
