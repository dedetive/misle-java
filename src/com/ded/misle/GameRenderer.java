package com.ded.misle;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.SaveFile.loadSaveFile;
import static com.ded.misle.SaveFile.saveEverything;

import java.awt.Rectangle;
import javax.swing.JPanel;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GameRenderer {
	private static String previousMenu;
	private static String currentMenu;

	public static void renderMainMenu(Graphics g, double width, double height, JPanel panel) {
		if (g instanceof Graphics2D g2d) {
			currentMenu = "MAIN_MENU";

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) width, (int) height);

			// MENU ITSELF

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(new Font("Dialog", Font.BOLD, (int) (96 * scaleByScreenSize)));
			g2d.drawString("Misle", (int) (820 * scaleByScreenSize), (int) (182 * scaleByScreenSize));

			// Play button

			g2d.setColor(new Color(191, 191, 191));
			int playButtonX = (int) (736 * scaleByScreenSize);
			int playButtonY = (int) (462 * scaleByScreenSize);
			int playButtonWidth = (int) (448 * scaleByScreenSize);
			int playButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

			createButton(playButton, "Play", GameRenderer::gameStart, panel, g2d, scaleByScreenSize);

			// Quit button

			g2d.setColor(new Color(191, 191, 191));
			int quitButtonX = (int) (992 * scaleByScreenSize);
			int quitButtonY = (int) (660 * scaleByScreenSize);
			int quitButtonWidth = (int) (192 * scaleByScreenSize);
			int quitButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

			createButton(quitButton, "Quit", GameRenderer::quitGame, panel, g2d, scaleByScreenSize);

			// Options menu

			g2d.setColor(new Color(191, 191, 191));
			int optionsButtonX = (int) (736 * scaleByScreenSize);
			int optionsButtonY = (int) (660 * scaleByScreenSize);
			int optionsButtonWidth = (int) (192 * scaleByScreenSize);
			int optionsButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

			createButton(optionsButton, "Options", GameRenderer::optionsMenu, panel, g2d, scaleByScreenSize);

			// Version

			g2d.setColor(new Color(217, 217, 217));
			g2d.setFont(new Font("Dialog", Font.BOLD, (int) (40 * scaleByScreenSize)));
			g2d.drawString("v0.1.3-alpha", (int) (1640 * scaleByScreenSize), (int) (1010* Math.pow(scaleByScreenSize, 1.04)));
		}
	}

	private static void createButton(Rectangle button, String text, Runnable action, JPanel panel, Graphics2D g2d, double scaleByScreenSize) {
		g2d.fillRoundRect(button.x, button.y, button.width, button.height, (int) (69 * scaleByScreenSize), (int) (69 * scaleByScreenSize));
		g2d.setColor(new Color(0, 0, 0));
		g2d.setFont(new Font("Helvetica", Font.BOLD, (int) (40 * scaleByScreenSize)));
		FontMetrics fm = g2d.getFontMetrics();
		String buttonText = text;
		int textWidth = fm.stringWidth(buttonText);
		int textHeight = fm.getAscent();
		int textX = button.x + (button.width - textWidth) / 2;
		int textY = button.y + (button.height + textHeight) / 2 - fm.getDescent();
		g2d.drawString(buttonText, textX, textY);

		addClickable(button, action, panel);
	}

	private static List<Object[]> clickables = new ArrayList<>();
	private static boolean mouseListenerAdded = false;

	private static void addClickable(Rectangle button, Runnable action, JPanel panel) {
		// Add button and action as an object array to the list
		clickables.add(new Object[]{button, action});

		// Add the mouse listener only once
		if (!mouseListenerAdded) {
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					Point clickPoint = e.getPoint();
					for (Object[] clickable : clickables) {
						Rectangle rect = (Rectangle) clickable[0]; // Get the rectangle
						Runnable clickableAction = (Runnable) clickable[1]; // Get the action

						if (rect.contains(clickPoint)) {
							clickableAction.run(); // Run action

							// Clearing clickables afterwards
							ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
							scheduler.schedule(GameRenderer::clearClickables, 8, TimeUnit.MILLISECONDS);
							break;
						}
					}
				}
			});
			mouseListenerAdded = true; // Prevent adding the listener again
		}
	}



	public static void clearClickables() {
		clickables.clear();
	}

	public static void gameStart() {
		previousMenu = currentMenu;
		currentMenu = "PLAYING";
		loadSaveFile();
		GamePanel.gameState = GamePanel.GameState.PLAYING;
	}

	public static void softGameStart() {
		previousMenu = currentMenu;
		currentMenu = "PLAYING";
		GamePanel.gameState = GamePanel.GameState.PLAYING;
	}

	public static void quitGame() {
		GamePanel.quitGame();
	}

	public static void optionsMenu() {
		previousMenu = currentMenu;
		currentMenu = "OPTIONS_MENU";
		GamePanel.gameState = GamePanel.GameState.OPTIONS_MENU;
	}

	public static void goToPreviousMenu() {
		System.out.println("Previous menu: " + previousMenu);
		System.out.println("Current menu: " + currentMenu);
		switch (previousMenu) {
			case "MAIN_MENU":
				GamePanel.gameState = GamePanel.GameState.MAIN_MENU;
				break;
			case "OPTIONS_MENU":
				GamePanel.gameState = GamePanel.GameState.OPTIONS_MENU;
				break;
			case "PLAYING":
				GamePanel.gameState = GamePanel.GameState.PLAYING;
				break;
			case "PAUSE_MENU":
				GamePanel.gameState = GamePanel.GameState.PAUSE_MENU;
		}
		previousMenu = currentMenu;
	}

	public static void goToMainMenu() {
		saveEverything();
		previousMenu = currentMenu;
		GamePanel.gameState = GamePanel.GameState.MAIN_MENU;
	}

	public static void renderOptionsMenu(Graphics g, double width, double height, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) width, (int) height);

			// MENU ITSELF

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(new Font("Dialog", Font.BOLD, (int) (96 * scaleByScreenSize)));
			g2d.drawString("Options", (int) (800 * scaleByScreenSize), (int) (182 * scaleByScreenSize));

			// Go back button

			g2d.setColor(new Color(191, 191, 191));
			int playButtonX = (int) (1338 * scaleByScreenSize);
			int playButtonY = (int) (883 * Math.pow(scaleByScreenSize, 1.04));
			int playButtonWidth = (int) (407 * scaleByScreenSize);
			int playButtonHeight = (int) (116 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

			createButton(playButton, "Go back", GameRenderer::goToPreviousMenu, panel, g2d, scaleByScreenSize);
		}
	}

	public static void pauseGame() {
		previousMenu = currentMenu;
		currentMenu = "PAUSE_MENU";
		GamePanel.gameState = GamePanel.GameState.PAUSE_MENU;
	}

	public static void renderPauseMenu(Graphics g, double width, double height, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) width, (int) height);

			// MENU ITSELF

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(new Font("Dialog", Font.BOLD, (int) (96 * scaleByScreenSize)));
			g2d.drawString("Paused", (int) (780 * scaleByScreenSize), (int) (182 * scaleByScreenSize));

			// Resume button

			g2d.setColor(new Color(191, 191, 191));
			int playButtonX = (int) (736 * scaleByScreenSize);
			int playButtonY = (int) (462 * scaleByScreenSize);
			int playButtonWidth = (int) (448 * scaleByScreenSize);
			int playButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

			createButton(playButton, "Resume", GameRenderer::softGameStart, panel, g2d, scaleByScreenSize);

			// Quit button

			g2d.setColor(new Color(191, 191, 191));
			int quitButtonX = (int) (992 * scaleByScreenSize);
			int quitButtonY = (int) (660 * scaleByScreenSize);
			int quitButtonWidth = (int) (192 * scaleByScreenSize);
			int quitButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

			createButton(quitButton, "Quit", GameRenderer::goToMainMenu, panel, g2d, scaleByScreenSize);

			// Options menu

			g2d.setColor(new Color(191, 191, 191));
			int optionsButtonX = (int) (736 * scaleByScreenSize);
			int optionsButtonY = (int) (660 * scaleByScreenSize);
			int optionsButtonWidth = (int) (192 * scaleByScreenSize);
			int optionsButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

			createButton(optionsButton, "Options", GameRenderer::optionsMenu, panel, g2d, scaleByScreenSize);
		}
	}
}
