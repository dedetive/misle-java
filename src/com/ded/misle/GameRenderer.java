package com.ded.misle;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.SaveFile.loadSaveFile;
import static com.ded.misle.SaveFile.saveEverything;
import static com.ded.misle.boxes.BoxesLoad.loadBoxes;

import java.awt.Rectangle;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GameRenderer {
	private static String previousMenu;
	private static String currentMenu;

	private static final String gameVersion = "v0.1.4-alpha";

	private static long startTime;
	private static final int LOADING_DURATION = 500;

	private static void createButton(Rectangle button, String text, Runnable action, JPanel panel, Graphics2D g2d, double scaleByScreenSize) {
		Font alefFont = FontManager.loadFont("/fonts/Alef-Regular.ttf", (float) (44 * scale / 3.75));
		Font alefBoldFont = FontManager.loadFont("/fonts/Alef-Bold.ttf", (float) (44 * scale / 3.75));
		g2d.fillRoundRect(button.x, button.y, button.width, button.height, (int) (69 * scaleByScreenSize), (int) (69 * scaleByScreenSize));
		g2d.setColor(new Color(0, 0, 0));
		g2d.setFont(alefFont);
		FontMetrics fm = g2d.getFontMetrics();
		String buttonText = text;
		int textWidth = fm.stringWidth(buttonText);
		int textHeight = fm.getAscent();
		int textX = button.x + (button.width - textWidth) / 2;
		int textY = button.y + (button.height + textHeight) / 2 - fm.getDescent() + (int) (2 * scale);
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
		startTime = System.currentTimeMillis();
		GamePanel.gameState = GamePanel.GameState.LOADING_MENU;
		loadSaveFile();
		loadBoxes();

		Timer timer = new Timer(LOADING_DURATION, e -> {
			player.pos.reloadSpawnpoint();
			GamePanel.gameState = GamePanel.GameState.PLAYING;
		});

		timer.setRepeats(false); // Ensure the timer only runs once
		timer.start();
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
		player.unloadPlayer();
		previousMenu = currentMenu;
		GamePanel.gameState = GamePanel.GameState.MAIN_MENU;
	}

	public static void pauseGame() {
		previousMenu = currentMenu;
		currentMenu = "PAUSE_MENU";
		GamePanel.gameState = GamePanel.GameState.PAUSE_MENU;
	}

	public static void renderMainMenu(Graphics g, double width, double height, JPanel panel) {
		if (g instanceof Graphics2D g2d) {
			Font acmeFont = FontManager.loadFont("/fonts/Acme-Regular.ttf", (float) (96 * scale / 3.75));
			Font basicFont = FontManager.loadFont("/fonts/Basic-Regular.ttf", (float) (40 * scale / 3.75));

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			currentMenu = "MAIN_MENU";

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) width, (int) height);

			// MENU ITSELF

			// Centering the title

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(acmeFont);
			FontMetrics fm = g2d.getFontMetrics();
			String titleText = "Misle";
			int textWidth = fm.stringWidth(titleText);
			int centerX = (int) ((width - textWidth) / 2);
			int textY = (int) (182 * scaleByScreenSize);
			g2d.drawString(titleText, centerX, textY);

			// Play button

			g2d.setColor(new Color(191, 191, 191));
			int playButtonX = (int) (736 * scaleByScreenSize);
			int playButtonY = (int) (462 * scaleByScreenSize);
			int playButtonWidth = (int) (448 * scaleByScreenSize);
			int playButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

			createButton(playButton, LanguageManager.getText("main_menu_play"), GameRenderer::gameStart, panel, g2d, scaleByScreenSize);

			// Quit button

			g2d.setColor(new Color(191, 191, 191));
			int quitButtonX = (int) (992 * scaleByScreenSize);
			int quitButtonY = (int) (660 * scaleByScreenSize);
			int quitButtonWidth = (int) (192 * scaleByScreenSize);
			int quitButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

			createButton(quitButton, LanguageManager.getText("main_menu_quit"), GameRenderer::quitGame, panel, g2d, scaleByScreenSize);

			// Options menu

			g2d.setColor(new Color(191, 191, 191));
			int optionsButtonX = (int) (736 * scaleByScreenSize);
			int optionsButtonY = (int) (660 * scaleByScreenSize);
			int optionsButtonWidth = (int) (192 * scaleByScreenSize);
			int optionsButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

			createButton(optionsButton, LanguageManager.getText("main_menu_options"), GameRenderer::optionsMenu, panel, g2d, scaleByScreenSize);

			// Version

			g2d.setColor(new Color(217, 217, 217));
			g2d.setFont(basicFont);
			g2d.drawString(gameVersion, (int) (1640 * scaleByScreenSize), (int) (1010* Math.pow(scaleByScreenSize, 1.04)));
		}
	}

	public static void renderPauseMenu(Graphics g, double width, double height, JPanel panel) {
		if (g instanceof Graphics2D g2d) {
			Font acmeFont = FontManager.loadFont("/fonts/Acme-Regular.ttf", (float) (96 * scale / 3.75));

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) width, (int) height);

			// MENU ITSELF

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(acmeFont);
			FontMetrics fm = g2d.getFontMetrics();
			String titleText = LanguageManager.getText("pause_menu_paused");
			int textWidth = fm.stringWidth(titleText);
			int centerX = (int) ((width - textWidth) / 2);
			int textY = (int) (182 * scaleByScreenSize);
			g2d.drawString(titleText, centerX, textY);

			// Resume button

			g2d.setColor(new Color(191, 191, 191));
			int playButtonX = (int) (736 * scaleByScreenSize);
			int playButtonY = (int) (462 * scaleByScreenSize);
			int playButtonWidth = (int) (448 * scaleByScreenSize);
			int playButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

			createButton(playButton, LanguageManager.getText("pause_menu_resume"), GameRenderer::softGameStart, panel, g2d, scaleByScreenSize);

			// Quit button

			g2d.setColor(new Color(191, 191, 191));
			int quitButtonX = (int) (992 * scaleByScreenSize);
			int quitButtonY = (int) (660 * scaleByScreenSize);
			int quitButtonWidth = (int) (192 * scaleByScreenSize);
			int quitButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

			createButton(quitButton, LanguageManager.getText("pause_menu_quit"), GameRenderer::goToMainMenu, panel, g2d, scaleByScreenSize);

			// Options menu

			g2d.setColor(new Color(191, 191, 191));
			int optionsButtonX = (int) (736 * scaleByScreenSize);
			int optionsButtonY = (int) (660 * scaleByScreenSize);
			int optionsButtonWidth = (int) (192 * scaleByScreenSize);
			int optionsButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

			createButton(optionsButton, LanguageManager.getText("pause_menu_options"), GameRenderer::optionsMenu, panel, g2d, scaleByScreenSize);
		}
	}

	public static void renderOptionsMenu(Graphics g, double width, double height, JPanel panel) {
		if (g instanceof Graphics2D g2d) {
			Font acmeFont = FontManager.loadFont("/fonts/Acme-Regular.ttf", (float) (96 * scale / 3.75));

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) width, (int) height);

			// MENU ITSELF

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(acmeFont);
			FontMetrics fm = g2d.getFontMetrics();
			String titleText = LanguageManager.getText("options_menu_options");
			int textWidth = fm.stringWidth(titleText);
			int centerX = (int) ((width - textWidth) / 2);
			int textY = (int) (182 * scaleByScreenSize);
			g2d.drawString(titleText, centerX, textY);

			// Go back button

			g2d.setColor(new Color(191, 191, 191));
			int playButtonX = (int) (1338 * scaleByScreenSize);
			int playButtonY = (int) (883 * Math.pow(scaleByScreenSize, 1.04));
			int playButtonWidth = (int) (407 * scaleByScreenSize);
			int playButtonHeight = (int) (116 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

			createButton(playButton, LanguageManager.getText("options_menu_go_back"), GameRenderer::goToPreviousMenu, panel, g2d, scaleByScreenSize);
		}
	}

	public static void renderLoadingMenu(Graphics g, double width, double height, JPanel panel) {
		if (g instanceof Graphics2D g2d) {
			Font acmeFont = FontManager.loadFont("/fonts/Acme-Regular.ttf", (float) (96 * scale / 3.75));
			Font alefFont = FontManager.loadFont("/fonts/Alef-Regular.ttf", (float) (40 * scale / 3.75));

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) width, (int) height);

			// MENU ITSELF

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(acmeFont);
			FontMetrics fm = g2d.getFontMetrics();
			String titleText = LanguageManager.getText("loading_menu_loading");
			int textWidth = fm.stringWidth(titleText);
			int centerX = (int) ((width - textWidth) / 2);
			int textY = (int) (182 * scaleByScreenSize);
			g2d.drawString(titleText, centerX, textY);

			// Progress bar

			long elapsedTime = System.currentTimeMillis() - startTime;
			double progress = Math.min((double) elapsedTime / LOADING_DURATION, 1.0); // Calculate progress (0.0 to 1.0)
			String percentage = (int) (progress * 100) + "%";

			int progressBarWidth = (int) (640 * progress * scaleByScreenSize);
			int progressBarHeight = (int) (25 * scaleByScreenSize);
			int progressBarY = (int) ((textY + 560) * scaleByScreenSize);

			g2d.setColor(new Color(100, 200, 100));
			g2d.fillRect((int) (660 * scaleByScreenSize), progressBarY, progressBarWidth, progressBarHeight);

			g2d.setColor(new Color(191, 191, 191));
			g2d.setFont(alefFont);
			FontMetrics percentageFm = g2d.getFontMetrics();
			textWidth = percentageFm.stringWidth(percentage); // Use the new font metrics for percentage
			centerX = (int) ((width - textWidth) / 2);
			textY = (int) ((progressBarY) - 20 * scaleByScreenSize);
			g2d.drawString(percentage, centerX, textY);
		}
	}
}