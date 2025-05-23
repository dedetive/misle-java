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
import static com.ded.misle.world.entities.enemies.EnemyAI.clearBreadcrumbs;
import static com.ded.misle.world.entities.player.Planner.resumeExecution;
import static java.lang.System.currentTimeMillis;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;


public abstract class MainRenderer {
	public static GameState previousMenu;
	public static GameState currentMenu;

	public static long startTime;
	public static final int LOADING_DURATION = 500;

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
		loadBoxes();

		player.attr.updateStat(PlayerAttributes.Stat.ALL);
		player.setHP(player.getMaxHP());
		player.attr.fillEntropy();
		player.currentSaveSlot = saveSlot;
		clearBreadcrumbs();

		Timer fadeTimer = new Timer(LOADING_DURATION, e -> { fader.fadeIn(); });
		fadeTimer.setRepeats(false);
		fadeTimer.start();

		Timer timer = new Timer(75, e -> {
			if (fader.isState(Fader.FadingState.FADED)) {
				fader.slowlyFadeOut();
				for (int i = 15; i > 0; i--) {
					storeCachedBoxes(i);
				}
				player.stats.resetStartTimestamp();
				player.pos.reloadSpawnpoint();
				gameState = PLAYING;

				((Timer) e.getSource()).stop();
			}
		});

		timer.setRepeats(true);
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

    public static void drawRotatedImage(Graphics2D g2d, Image image, double x, double y, int width, int height, double angle) {
        // Calculate the rotation center based on the desired width and height
        double centerX = x + width / 2.0;
        double centerY = y + height / 2.0;

        // Save the original transform
        AffineTransform originalTransform = g2d.getTransform();

        // Apply rotation around the calculated center
        g2d.rotate(Math.toRadians(angle), centerX, centerY);

        // Draw the scaled and rotated image at the specified position
        g2d.drawImage(image, (int) x, (int) y, width, height, null);

        // Restore the original transform to avoid affecting other drawings
        g2d.setTransform(originalTransform);
    }

    public static void drawRotatedImage(Graphics2D g2d, Image image, double x, double y, int width, int height, double angle, boolean mirror) {
        // Calculate the rotation center based on the desired width and height
        double centerX = x + width / 2.0;
        double centerY = y + height / 2.0;

        // Save the original transform
        AffineTransform originalTransform = g2d.getTransform();

        // Apply rotation around the calculated center
        g2d.rotate(Math.toRadians(angle), centerX, centerY);

        // Apply mirroring if needed
        if (mirror) {
            // Translate to the center of the image, apply scaling to flip horizontally, then translate back
            g2d.translate(x + width, y); // Move to the right edge of the image
            g2d.scale(-1, 1);           // Flip horizontally
            g2d.translate(-x, -y);       // Move back to the original position
        }

        // Draw the scaled and rotated (and possibly mirrored) image at the specified position
        g2d.drawImage(image, (int) x, (int) y, width, height, null);

        // Restore the original transform to avoid affecting other drawings
        g2d.setTransform(originalTransform);
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
