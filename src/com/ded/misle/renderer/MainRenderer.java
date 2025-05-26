package com.ded.misle.renderer;

import com.ded.misle.input.KeyHandler;
import com.ded.misle.world.entities.player.Player;
import com.ded.misle.world.entities.player.PlayerAttributes;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.game.GamePanel.GameState.LEVEL_DESIGNER;
import static com.ded.misle.game.GamePanel.GameState.PLAYING;
import static com.ded.misle.core.SaveFile.loadSaveFile;
import static com.ded.misle.world.boxes.BoxHandling.storeCachedBoxes;
import static com.ded.misle.world.data.WorldLoader.loadBoxes;
import static com.ded.misle.world.entities.player.Planner.resumeExecution;
import static java.lang.System.currentTimeMillis;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public abstract class MainRenderer {
	public static GameState previousMenu;
	public static GameState currentMenu;

	public static long startTime;
	public static final int LOADING_DURATION = 1200;

	public static final int textShadow = 1;

	public static Fader fader = new Fader();

	public static void gameStart(int saveSlot) {
		player = new Player();
		player.currentSaveSlot = saveSlot;

		keyH = new KeyHandler();

		previousMenu = currentMenu;
		currentMenu = PLAYING;
		startTime = currentTimeMillis();
		gameState = GameState.LOADING_MENU;


		loadSaveFile();

		player.attr.updateStat(PlayerAttributes.Stat.ALL);
		player.setHP(player.getMaxHP());
		player.attr.fillEntropy();
		player.currentSaveSlot = saveSlot;

		Timer fadeTimer = new Timer(LOADING_DURATION, e -> pixelate(1500, 32));
		fadeTimer.setRepeats(false);
		fadeTimer.start();

		Timer timer = new Timer(4000, e -> {
			unpixelate(1200);

			player.stats.resetStartTimestamp();
			player.pos.reloadSpawnpoint();
			gameState = PLAYING;
		});

		timer.setRepeats(false);
		timer.start();
	}

	public static void softGameStart() {
		previousMenu = currentMenu;
		currentMenu = PLAYING;
		gameState = PLAYING;
		resumeExecution();
	}

	public static void enterLevelDesigner() {
		previousMenu = currentMenu;
		currentMenu = LEVEL_DESIGNER;
		startTime = currentTimeMillis();
		gameState = GameState.LOADING_MENU;

		loadBoxes();

		Timer timer = new Timer(LOADING_DURATION, e -> {
			for (int i = 15; i > 0; i--) {
				storeCachedBoxes(i);
			}
			gameState = LEVEL_DESIGNER;
		});

		timer.setRepeats(false); // Ensure the timer only runs once
		timer.start();
	}

	public static void softEnterLevelDesigner() {
		previousMenu = currentMenu;
		currentMenu = LEVEL_DESIGNER;
		gameState = LEVEL_DESIGNER;
	}

    public static void drawRotatedImage(Graphics2D g2d, BufferedImage image, double x, double y, int width, int height, double angle) {
		drawRotatedImage(g2d, image, x, y, width, height, angle, false);
    }

	private static final Map<String, Image> imageMap = new HashMap<>();
    public static void drawRotatedImage(Graphics2D g2d, BufferedImage image, double x, double y, int width, int height, double angle, boolean mirror) {
		if (angle == 0 && !mirror) {
			Image transformed = imageMap.computeIfAbsent((Arrays.toString(new Object[]{image.getSource(), width, height})),
				(ignored) ->
					image.getScaledInstance(width, height, Image.SCALE_DEFAULT));
			g2d.drawImage(transformed, (int) x, (int) y, null);

			if (imageMap.size() > 100) imageMap.clear();
			return;
		}

		AffineTransform transform = new AffineTransform();

		double centerX = x + width / 2.0;
		double centerY = y + height / 2.0;

		transform.translate(centerX, centerY);
		transform.rotate(Math.toRadians(angle));

		if (mirror) {
			transform.scale(-1, 1);
		}

		transform.translate(-width / 2.0, -height / 2.0);
		transform.scale((double) width / image.getWidth(), (double) height / image.getHeight());

		g2d.drawImage(image, transform, null);
    }

    public static void drawRotatedRect(Graphics2D g2d, Rectangle rectangle, double angle) {
        double centerX = rectangle.x + rectangle.width / 2.0;
        double centerY = rectangle.y + rectangle.height / 2.0;

        // Save the original transform
        AffineTransform originalTransform = g2d.getTransform();

        // Apply rotation around the calculated center
        g2d.rotate(Math.toRadians(angle), centerX, centerY);

        // Draw the scaled and rotated image at the specified position
        g2d.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

        // Restore the original transform to avoid affecting other drawings
        g2d.setTransform(originalTransform);
    }
}
