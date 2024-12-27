package com.ded.misle.renderer;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.SaveFile.loadSaveFile;
import static com.ded.misle.boxes.BoxHandling.storeCachedBoxes;
import static com.ded.misle.boxes.WorldLoader.loadBoxes;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.ColorManager.fadingColorB;
import static java.lang.System.currentTimeMillis;

import javax.swing.*;
import java.awt.*;


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

		player.attr.setHP(player.attr.getMaxHP());
		player.attr.fillEntropy();

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

	public static FadingState isFading = FadingState.UNFADED;

	public static float fadingProgress;
	public enum FadingState {
		FADING_IN,
		FADING_OUT,
		FADED,
		UNFADED
	}

	public static void fadeIn() {
		if (isFading == FadingState.UNFADED || isFading == FadingState.FADING_OUT) {
			isFading = FadingState.FADING_IN;
		}
	}

	public static void fadeOut() {
		if (isFading == FadingState.FADED || isFading == FadingState.FADING_IN) {
			isFading = FadingState.FADING_OUT;
		}
	}

	public static void fadeInThenOut(int ms) {
		fadeIn();

		Timer timer = new Timer(ms, e -> {
			fadeOut();
		});
		timer.setRepeats(false);
		timer.start();
	}

	public static void drawFading(Graphics2D g2d) {
		if (isFading == GameRenderer.FadingState.FADING_IN || isFading == GameRenderer.FadingState.FADED) {
			fadingProgress = Math.min(fadingProgress + 0.019F, 1F);
			g2d.setColor(new Color((float) fadingColorR / 256, (float) fadingColorG / 256, (float) fadingColorB / 256, fadingProgress));
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);
			if (fadingProgress == 1F) {
				isFading = GameRenderer.FadingState.FADED;
			}
		} else if (isFading == GameRenderer.FadingState.FADING_OUT
		) {
			fadingProgress = Math.max(fadingProgress - 0.02125F, 0F);
			g2d.setColor(new Color((float) fadingColorR / 256, (float) fadingColorG / 256, (float) fadingColorB / 256, fadingProgress));
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);
			if (fadingProgress == 0F) {
				isFading = GameRenderer.FadingState.UNFADED;
			}
		}
	}
}
