package com.ded.misle;

import com.ded.misle.boxes.BoxesHandling;
import com.ded.misle.items.Item;

import java.awt.*;

import static com.ded.misle.ChangeSettings.getPath;
import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.levelDesigner;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.MenuButton.createButton;
import static com.ded.misle.MenuButton.drawButtons;
import static com.ded.misle.SaveFile.loadSaveFile;
import static com.ded.misle.SaveFile.saveEverything;
import static com.ded.misle.boxes.BoxesHandling.storeCachedBoxes;
import static com.ded.misle.boxes.BoxesLoad.loadBoxes;
import static com.ded.misle.boxes.BoxesLoad.unloadBoxes;
import static com.ded.misle.player.PlayerStats.Direction.LEFT;
import static com.ded.misle.player.PlayerStats.Direction.RIGHT;
import static java.lang.System.currentTimeMillis;

import java.awt.Rectangle;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class GameRenderer {
	private static String previousMenu;
	private static String currentMenu;

	private static final String gameVersion = "v0.1.5-alpha";

	private static long startTime;
	private static final int LOADING_DURATION = 500;

	public static boolean levelDesignerGrid;

	private static String selectedItemName;
	private static Point selectedItemNamePosition;
	private static long itemNameDisplayStartTime;
	public static double textShadow = 1 * scale;

	public static int unscaledSlotSize = 32;
	public static int unscaledSlotSpacing = 0;

	public static boolean showHealthBar = false;

	private static final Color backgroundColor = new Color(140, 110, 70);

	private static final List<String> floatingText = new ArrayList<>();
	private static final List<Point> floatingTextPosition = new ArrayList<>();
	private static final List<Color> floatingTextColor = new ArrayList<>();

	private static double isFacingRight;
	private static boolean mirror;

	private static float fadingProgress;
	private enum FadingState {
		FADING_IN,
		FADING_OUT,
		FADED,
		UNFADED
	}

	private static FadingState isFading = FadingState.UNFADED;

	private static Font comfortaaFont96 = FontManager.loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (96 * scale / 3.75));
	private static Font ubuntuFont35 = FontManager.loadFont("/fonts/Ubuntu-Medium.ttf", (float) (35 * scale / 3.75));
	private static Font basicFont40 = FontManager.loadFont("/fonts/Basic-Regular.ttf", (float) (40 * scale / 3.75));
	private static Font itemCountFont = FontManager.loadFont("/fonts/Ubuntu-Regular.ttf", (float) (40 * scale / 3.75));
	public static Font ubuntuFont44 = FontManager.loadFont("/fonts/Ubuntu-Medium.ttf", (float) (44 * scale / 3.75));


	public static void updateFontSizes() {
		comfortaaFont96 = FontManager.loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (96 * scale / 3.75));
		ubuntuFont35 = FontManager.loadFont("/fonts/Ubuntu-Medium.ttf", (float) (40 * scale / 3.75));
		basicFont40 = FontManager.loadFont("/fonts/Basic-Regular.ttf", (float) (40 * scale / 3.75));
		itemCountFont = FontManager.loadFont("/fonts/Ubuntu-Regular.ttf", (float) (50 * scale / 3.75));
		ubuntuFont44 = FontManager.loadFont("/fonts/Ubuntu-Medium.ttf", (float) (44 * scale / 3.75));

		textShadow = 1 * scale;
	}

	private static void createTitle(String text, Graphics2D g2d, double scaleByScreenSize) {
		g2d.setFont(comfortaaFont96);
		FontMetrics fm = g2d.getFontMetrics();
		String titleText = LanguageManager.getText(text);
		int textWidth = fm.stringWidth(titleText);
		int centerX = (int) ((screenWidth - textWidth) / 2);
		int textY = (int) (182 * scaleByScreenSize);
		g2d.setColor(Color.black);
		g2d.drawString(titleText, (int) (centerX - textShadow), textY); // Left
		g2d.drawString(titleText, (int) (centerX + textShadow), textY); // Right
		g2d.drawString(titleText, centerX, (int) (textY - textShadow)); // Up
		g2d.drawString(titleText, centerX, (int) (textY + textShadow)); // Down
		g2d.drawString(titleText, (int) (centerX - textShadow), (int) (textY - textShadow)); // Left-up corner
		g2d.drawString(titleText, (int) (centerX + textShadow), (int) (textY - textShadow)); // Right-up corner
		g2d.drawString(titleText, (int) (centerX - textShadow), (int) (textY + textShadow)); // Left-down corner
		g2d.drawString(titleText, (int) (centerX - textShadow), (int) (textY - textShadow)); // Right-down corner
		g2d.setColor(new Color(233, 233, 233));
		g2d.drawString(titleText, centerX, textY);
	}

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

	private static void enterLevelDesigner() {
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

	public static void softGameStart() {
		previousMenu = currentMenu;
		currentMenu = "PLAYING";
		gameState = GameState.PLAYING;
	}

	public static void softEnterLevelDesigner() {
		previousMenu = currentMenu;
		currentMenu = "LEVEL_DESIGNER";
		gameState = GameState.LEVEL_DESIGNER;
	}

	public static void quitGame() {
		GamePanel.quitGame();
	}

	public static void optionsMenu() {
		previousMenu = currentMenu;
		currentMenu = "OPTIONS_MENU";
		gameState = GameState.OPTIONS_MENU;
	}

	public static void goToPreviousMenu() {
		switch (previousMenu) {
			case "MAIN_MENU":
				gameState = GameState.MAIN_MENU;
				break;
			case "OPTIONS_MENU":
				gameState = GameState.OPTIONS_MENU;
				break;
			case "PLAYING":
				gameState = GameState.PLAYING;
				break;
			case "PAUSE_MENU":
				gameState = GameState.PAUSE_MENU;
		}
		previousMenu = currentMenu;
	}

	public static void goToMainMenu() {
		saveEverything();
		unloadBoxes();
		player.unloadPlayer();
		previousMenu = currentMenu;
		gameState = GameState.MAIN_MENU;
	}

	public static void pauseGame() {
		previousMenu = currentMenu;
		currentMenu = "PAUSE_MENU";
		gameState = GameState.PAUSE_MENU;
	}

	public static void renderMainMenu(Graphics g, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			currentMenu = "MAIN_MENU";

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(backgroundColor);
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

			// MENU ITSELF

			// Centering the title

			createTitle("misle", g2d, scaleByScreenSize);

			// Play button

			int playButtonX = (int) (736 * scaleByScreenSize);
			int playButtonY = (int) (462 * scaleByScreenSize);
			int playButtonWidth = (int) (448 * scaleByScreenSize);
			int playButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

			if (!levelDesigner) {
				createButton(playButton, LanguageManager.getText("main_menu_play"), GameRenderer::gameStart, panel);
			} else {
				createButton(playButton, LanguageManager.getText("main_menu_level_designer"), GameRenderer::enterLevelDesigner, panel);
			}

			// Quit button

			int quitButtonX = (int) (992 * scaleByScreenSize);
			int quitButtonY = (int) (660 * scaleByScreenSize);
			int quitButtonWidth = (int) (192 * scaleByScreenSize);
			int quitButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

			createButton(quitButton, LanguageManager.getText("main_menu_quit"), GameRenderer::quitGame, panel);


			// Options menu

			int optionsButtonX = (int) (736 * scaleByScreenSize);
			int optionsButtonY = (int) (660 * scaleByScreenSize);
			int optionsButtonWidth = (int) (192 * scaleByScreenSize);
			int optionsButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

			createButton(optionsButton, LanguageManager.getText("main_menu_options"), GameRenderer::optionsMenu, panel);

			drawButtons(g2d, scaleByScreenSize);

			// Version

			g2d.setFont(basicFont40);
			g2d.setColor(Color.black);
			g2d.drawString(gameVersion, (int) (1640 * scaleByScreenSize + textShadow), (int) (1010* Math.pow(scaleByScreenSize, 1.04) + textShadow));
			g2d.setColor(new Color(217, 217, 217));
			g2d.drawString(gameVersion, (int) (1640 * scaleByScreenSize), (int) (1010* Math.pow(scaleByScreenSize, 1.04)));
		}
	}

	public static void renderPauseMenu(Graphics g, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(backgroundColor);
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

			// MENU ITSELF

			createTitle("pause_menu_paused", g2d, scaleByScreenSize);

			// Resume button

			int playButtonX = (int) (736 * scaleByScreenSize);
			int playButtonY = (int) (462 * scaleByScreenSize);
			int playButtonWidth = (int) (448 * scaleByScreenSize);
			int playButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

			createButton(playButton, LanguageManager.getText("pause_menu_resume"), GameRenderer::softGameStart, panel);

			// Quit button

			int quitButtonX = (int) (992 * scaleByScreenSize);
			int quitButtonY = (int) (660 * scaleByScreenSize);
			int quitButtonWidth = (int) (192 * scaleByScreenSize);
			int quitButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

			createButton(quitButton, LanguageManager.getText("pause_menu_quit"), GameRenderer::goToMainMenu, panel);

			// Options menu

			int optionsButtonX = (int) (736 * scaleByScreenSize);
			int optionsButtonY = (int) (660 * scaleByScreenSize);
			int optionsButtonWidth = (int) (192 * scaleByScreenSize);
			int optionsButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

			createButton(optionsButton, LanguageManager.getText("pause_menu_options"), GameRenderer::optionsMenu, panel);

			drawButtons(g2d, scaleByScreenSize);
		}
	}

	public static void renderOptionsMenu(Graphics g, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(backgroundColor);
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

			// MENU ITSELF

			createTitle("options_menu_options", g2d, scaleByScreenSize);

			// Go back button

			int playButtonX = (int) (1338 * scaleByScreenSize);
			int playButtonY = (int) (883 * Math.pow(scaleByScreenSize, 1.04));
			int playButtonWidth = (int) (407 * scaleByScreenSize);
			int playButtonHeight = (int) (116 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

			createButton(playButton, LanguageManager.getText("options_menu_go_back"), GameRenderer::goToPreviousMenu, panel);

			drawButtons(g2d, scaleByScreenSize);
		}
	}

	public static void renderLoadingMenu(Graphics g, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(backgroundColor);
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

			// MENU ITSELF

			createTitle("loading_menu_loading", g2d, scaleByScreenSize);
			g2d.setFont(comfortaaFont96);
			FontMetrics fm = g2d.getFontMetrics();
			String titleText = LanguageManager.getText("loading_menu_loading");
			fm.stringWidth(titleText);
			int textY = (int) (182 * scaleByScreenSize);

			// Progress bar

			long elapsedTime = currentTimeMillis() - startTime;
			double progress = Math.min((double) elapsedTime / LOADING_DURATION, 1.0); // Calculate progress (0.0 to 1.0)
			String percentage = (int) (progress * 100) + "%";

			int progressBarWidth = (int) (640 * progress * scaleByScreenSize);
			int progressBarHeight = (int) (25 * scaleByScreenSize);
			int progressBarY = (int) ((textY + 560) * scaleByScreenSize);

			g2d.setColor(new Color(100, 200, 100));
			g2d.fillRect((int) (660 * scaleByScreenSize), progressBarY, progressBarWidth, progressBarHeight);

			g2d.setFont(ubuntuFont35);
			FontMetrics percentageFm = g2d.getFontMetrics();
			int textWidth = percentageFm.stringWidth(percentage); // Use the new font metrics for percentage
			int centerX = (int) ((screenWidth - textWidth) / 2);
			textY = (int) ((progressBarY) - 20 * scaleByScreenSize);
			g2d.setColor(Color.black);
			g2d.drawString(percentage, (int) (centerX + textShadow), (int) (textY + textShadow));
			g2d.setColor(new Color(191, 191, 191));
			g2d.drawString(percentage, centerX, textY);
		}
	}

	public static void renderPlayingGame(Graphics g, MouseHandler mouseHandler) {
		Graphics2D g2d = (Graphics2D) g;
		double scaleByScreenSize = scale / 3.75;

		// ANTI-ALIASING
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw game components
		BoxesHandling.renderBoxes(g2d, player.pos.getCameraOffsetX(), player.pos.getCameraOffsetY(), scale, tileSize);

		// Player position adjustments
		int playerScreenX = (int) (player.getX() - player.pos.getCameraOffsetX());
		int playerScreenY = (int) (player.getY() - player.pos.getCameraOffsetY());

		// Draw the player above every box
		g2d.setColor(Color.WHITE);
		Rectangle playerRect = new Rectangle(playerScreenX, playerScreenY, (int) player.attr.getWidth(), (int) player.attr.getHeight());
		drawRotatedRect(g2d, playerRect, player.pos.getRotation());

		drawHandItem(g2d, playerScreenX, playerScreenY, scaleByScreenSize);

		drawUIElements(g2d, playerScreenX, playerScreenY);

		if (floatingText != null) {
			drawFloatingText(g2d);
		}

		if (gameState == GameState.INVENTORY) {
			renderInventoryMenu(g);
			if (mouseHandler.getHoveredSlot()[0] > -1 && mouseHandler.getHoveredSlot()[1] > -1 && player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]) != null) {
				drawHoveredItemTooltip(g, new int[]{mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]});
			}
			if (player.inv.getDraggedItem() != null) {
				drawDraggedItem(g2d, mouseHandler);
			}
		} else {
			if (mouseHandler.getHoveredBarSlot() > -1 && player.inv.getItem(0, mouseHandler.getHoveredBarSlot()) != null) {
				drawHoveredItemTooltip(g, new int[]{-1, mouseHandler.getHoveredBarSlot()});
			}
		}

		if (isFading == FadingState.FADING_IN || isFading == FadingState.FADED) {
			fadingProgress = Math.min(fadingProgress + 0.01F, 1F);
			g2d.setColor(new Color(0, 0, 0, fadingProgress));
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);
			if (fadingProgress == 1F) {
				isFading = FadingState.FADED;
			}
		} else if (isFading == FadingState.FADING_OUT
		) {
			fadingProgress = Math.max(fadingProgress - 0.02F, 0F);
			g2d.setColor(new Color(0, 0, 0, fadingProgress));
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);
			if (fadingProgress == 0F) {
				isFading = FadingState.UNFADED;
			}
		}

		g2d.dispose();
	}

	private static void drawHandItem(Graphics2D g2d, double playerScreenX, double playerScreenY, double scaleByScreenSize) {
		if (player.inv.hasHeldItem()) {

			if (player.stats.getHorizontalDirection() == RIGHT) {
				isFacingRight = 0.5;
				mirror = false;
			} else if (player.stats.getHorizontalDirection() == LEFT) {
				isFacingRight = -1;
				mirror = true;
			}

			double distance = playerScreenX + (player.attr.getWidth() / 2) * 2 * isFacingRight * scaleByScreenSize;

			Item selectedItem = player.inv.getSelectedItem();

			if (selectedItem.getCountLimit() >= 16 && selectedItem.getCount() > selectedItem.getCountLimit() / 3) {
				double pos = 12 * isFacingRight * scaleByScreenSize;
				drawRotatedImage(g2d, selectedItem.getIcon(), distance + pos + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + 15 * scaleByScreenSize + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (35 + selectedItem.getAnimationRotation()) * Math.ceil(isFacingRight), mirror);
			}

			if (selectedItem.getCountLimit() >= 100 && selectedItem.getCount() > 2 * selectedItem.getCountLimit() / 3) {
				double pos = -12 * isFacingRight * scaleByScreenSize;
				drawRotatedImage(g2d, selectedItem.getIcon(), distance + pos + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + 15 * scaleByScreenSize + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (-35 + selectedItem.getAnimationRotation()) * Math.ceil(isFacingRight), mirror);
			}

			drawRotatedImage(g2d, selectedItem.getIcon(), (int) distance + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), selectedItem.getAnimationRotation() * Math.ceil(isFacingRight), mirror);
		}
	}

	private static void drawUIElements(Graphics2D g2d, int playerScreenX, int playerScreenY) {
		if (showHealthBar) drawHealthBar(g2d, playerScreenX, playerScreenY);
		drawInventoryBar(g2d);
		drawSelectedItemName(g2d);
	}

	private static void drawHealthBar(Graphics2D g2d, int playerScreenX, int playerScreenY) {
		int healthBarWidth = (int) (50 * scale); // Width of the health bar
		int healthBarHeight = (int) (10 * scale); // Height of the health bar
		int healthBarX = (int) (playerScreenX - player.attr.getWidth() / 2 - 2 * scale); // Position it above the player
		int healthBarY = playerScreenY - healthBarHeight - 5; // Offset slightly above the player rectangle

		// Calculate the percentage of health remaining
		double healthPercentage = Math.min((double) player.attr.getHP() / player.attr.getMaxHP(), 1);

		// Draw the background of the health bar (gray)
		g2d.setColor(Color.GRAY);
		g2d.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

		// Draw the current health bar (green, for example)
		g2d.setColor(Color.GREEN);
		g2d.fillRect(healthBarX, healthBarY, (int) (healthBarWidth * healthPercentage), healthBarHeight);

		// Draw locked HP, if any
		double lockedHPPercentage = Math.min(player.attr.getLockedHP() / player.attr.getMaxHP(), 1);

		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(healthBarX, healthBarY, (int) (healthBarWidth * lockedHPPercentage), healthBarHeight);
	}

	private static void drawInventoryBar(Graphics2D g2d) {

		int inventoryBarWidth = (int) (120 * scale);
		int inventoryBarHeight = (int) (20 * scale);
		int inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
		int inventoryBarY = (int) (screenHeight - inventoryBarHeight - 60);

		// Slots info

		int slotSize = (int) (unscaledSlotSize * scale);
		int slotSpacing = (int) (unscaledSlotSpacing * scale);

		int totalSlotsWidth = 7 * slotSize + (6 * slotSpacing);
		int slotStartX = inventoryBarX + (inventoryBarWidth - totalSlotsWidth) / 2;

		int selectedSlot = player.inv.getSelectedSlot();

		for (int i = 0; i < 7; i++) {
			int slotX = slotStartX + i * (slotSize + slotSpacing);
			int slotY = inventoryBarY + (inventoryBarHeight - slotSize) / 2;

			// Draw the slot
			g2d.setColor(Color.GRAY);
			g2d.fillRect(slotX, slotY, slotSize, slotSize);

			Item item = player.inv.getItem(0, i);
			if (item != null) {
				g2d.drawImage(item.getIcon(), slotX, slotY, slotSize, slotSize, null);
				int itemCount = item.getCount();
				if (itemCount > 1) {
					g2d.setFont(itemCountFont);
					FontMetrics fm = g2d.getFontMetrics();
					int textWidth = fm.stringWidth(Integer.toString(itemCount));
					int textX = slotX - textWidth + slotSize;
					int textY = slotY + 8 * slotSize / 9;
					g2d.setColor(Color.black);
					g2d.drawString(Integer.toString(itemCount), (int) (textX + textShadow), (int) (textY + textShadow));
					g2d.setColor(Color.white);
					g2d.drawString(Integer.toString(itemCount), textX, textY);
				}
			}

			if (i == selectedSlot) {
				drawSelectedSlotOverlay(g2d, slotX, slotY, slotSize);
			}
		}
	}

	private static void drawSelectedSlotOverlay(Graphics2D g2d, int slotX, int slotY, int slotSize) {
		g2d.setColor(new Color(255, 255, 255, 100)); // Semi-transparent overlay
		g2d.fillRect(slotX, slotY, slotSize, slotSize);
	}

	public static void updateSelectedItemNamePosition() {
		Item selectedItem = player.inv.getSelectedItem();
		if (selectedItem != null) {
			selectedItemName = selectedItem.getDisplayName();

			double scaleByScreenSize = scale;
			int inventoryBarWidth = (int) (120 * scaleByScreenSize);
			int inventoryBarHeight = (int) (20 * scaleByScreenSize);
			int inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
			int inventoryBarY = (int) (screenHeight - inventoryBarHeight - 10);

			int slotWidth = (int) (unscaledSlotSize * scaleByScreenSize);
			int slotSpacing = (int) (unscaledSlotSpacing * scaleByScreenSize);
			int totalSlotsWidth = 7 * slotWidth + (6 * slotSpacing);
			int slotStartX = inventoryBarX + (inventoryBarWidth - totalSlotsWidth) / 2;

			int slotX = slotStartX + player.inv.getSelectedSlot() * (slotWidth + slotSpacing);
			int slotY = inventoryBarY;

			// Position the name above the selected slot
			selectedItemNamePosition = new Point(slotX + slotWidth / 2, slotY - 70);
			itemNameDisplayStartTime = currentTimeMillis();
		} else {
			selectedItemName = null;
			selectedItemNamePosition = null;
		}
	}

	public static void drawSelectedItemName(Graphics2D g2d) {
		if (selectedItemName != null && selectedItemNamePosition != null) {
			// Check if the current time is within 5 seconds of the start time
			try {
				long currentTime = currentTimeMillis();
				if (currentTime - itemNameDisplayStartTime < 5000) {
					g2d.setFont(ubuntuFont35);
					FontMetrics fm = g2d.getFontMetrics();
					int textWidth = fm.stringWidth(selectedItemName);

					int textX = selectedItemNamePosition.x - textWidth / 2;
					int textY = selectedItemNamePosition.y;

					g2d.setColor(Color.black);
					g2d.drawString(selectedItemName, (int) (textX + textShadow), (int) (textY + textShadow));
					g2d.setColor(player.inv.getSelectedItem().getNameColor());
					g2d.drawString(selectedItemName, textX, textY);
				} else {
					// Clear the selected item name after 5 seconds
					selectedItemName = null;
					selectedItemNamePosition = null;
				}
			} catch (NullPointerException e) {
				System.out.println("Selected item not found!");
				selectedItemName = null;
				selectedItemNamePosition = null;
			}
		}
	}


	public static void renderInventoryMenu(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// Semi-transparent background overlay
		g2d.setColor(new Color(15, 15, 15, 130));
		g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

		// Slot dimensions and spacing
		int slotSize = (int) (unscaledSlotSize * scale);
		int slotSpacing = (int) (unscaledSlotSpacing * scale);

		// Start the grid
		int gridX = (int) (225 * scale);
		int gridY = (int) (148 * scale);

		// Draw background
		Path basePath = getPath().resolve("resources/images/ui/");
		Path fullPath = basePath.resolve("inventoryBackground.png");

		try {
			g2d.drawImage(ImageIO.read(fullPath.toFile()), 0, 0, (int) screenWidth, (int) screenHeight, null);
		} catch (IOException e) {
			System.out.println("Can't find item texture " + fullPath + "!");
		}

		// Draw slots and item icons in the specified row order: row 1, row 2, row 3, row 0
		int[] rowOrder = {1, 2, 3, 0};

		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 7; i++) {
				int slotX = gridX + i * (slotSize + slotSpacing);
				int slotY = gridY + j * (slotSize + slotSpacing);

				// Draw the slot as a gray rectangle (DISABLED, ENABLE FOR TESTING)
//				g2d.setColor(new Color(0x44, 0x44, 0x44, 120));
//				g2d.fillRect(slotX, slotY, slotSize, slotSize);

				// Draw item icon if there is one in this slot
				Item item = player.inv.getItem(rowOrder[j], i);
				if (item != null) {
					g2d.drawImage(item.getIcon(), slotX, slotY, slotSize, slotSize, null);
					int itemCount = item.getCount();
					if (itemCount > 1) {
						g2d.setFont(itemCountFont);
						FontMetrics fm = g2d.getFontMetrics();
						int textWidth = fm.stringWidth(Integer.toString(itemCount));
						int textX = slotX - textWidth + slotSize;
						int countY = slotY + slotSize;
						g2d.setColor(Color.black);
						g2d.drawString(Integer.toString(itemCount), (int) (textX + textShadow), (int) (countY + textShadow));
						g2d.setColor(Color.white);
						g2d.drawString(Integer.toString(itemCount), textX, countY);
					}
				}
			}
		}

		// Draw stats name
			// VIT
		drawStat(g2d, LanguageManager.getText("inventory_vitality"), 288, 33);
		String formattedMaxHP = Long.toString(Math.round(player.attr.getMaxHP()));
		formattedMaxHP = formattedMaxHP + LanguageManager.getText("inventory_vitality_measure_word");
		drawStat(g2d, formattedMaxHP, 288, 47);
			// DEF
		drawStat(g2d, LanguageManager.getText("inventory_defense"), 288, 65);
		String formattedDefense = Long.toString(Math.round(player.attr.getDefense()));
		formattedDefense = formattedDefense + LanguageManager.getText("inventory_defense_measure_word");
		drawStat(g2d, formattedDefense, 288, 79);
			// REG
		drawStat(g2d, LanguageManager.getText("inventory_regeneration"), 288, 97);
		String formattedRegeneration = Long.toString(Math.round(player.attr.getRegenerationQuality()));
		formattedRegeneration = formattedRegeneration + LanguageManager.getText("inventory_regeneration_measure_word");
		drawStat(g2d, formattedRegeneration, 288, 111);
			// ENT
		drawStat(g2d, LanguageManager.getText("inventory_entropy"), 384, 33);
		String formattedEntropy = Long.toString(Math.round(player.attr.getMaxEntropy()));
		formattedEntropy = formattedEntropy + LanguageManager.getText("inventory_entropy_measure_word");
		drawStat(g2d, formattedEntropy, 384, 47);
			// STR
		drawStat(g2d, LanguageManager.getText("inventory_strength"), 384, 65);
//		String formattedStrength = Long.toString(Math.round(player.attr.getStrength()));
//		formattedStrength = formattedStrength + LanguageManager.getText("inventory_strength_measure_word");
		String formattedStrength = "WIP";
		drawStat(g2d, formattedStrength, 384, 79);
			// SPD
		drawStat(g2d, LanguageManager.getText("inventory_speed"), 384, 97);
		String formattedSpeed = Long.toString(Math.round(player.attr.getSpeed()));
		formattedSpeed = formattedSpeed + LanguageManager.getText("inventory_speed_measure_word");
		drawStat(g2d, formattedSpeed, 384, 111);
	}

	private static void drawStat(Graphics2D g2d, String statText, int centerX, int y, Color textColor, Color shadowColor) {
		// Calculate center
		centerX = (int) (centerX * scale);
		FontMetrics fm = g2d.getFontMetrics();
		int textWidth = fm.stringWidth(statText);
		int startX = centerX - (textWidth / 2);
		y = (int) (y * scale);

		// Draw text shadow
		g2d.setColor(shadowColor);
		g2d.drawString(statText, (int) (startX + textShadow), (int) (y + textShadow));

		// Draw text
		g2d.setColor(textColor);
		g2d.drawString(statText, startX, y);
	}

	private static void drawStat(Graphics2D g2d, String statText, int centerX, int y) {
		g2d.setFont(ubuntuFont44);
		Color textColor = new Color(230, 230, 180);
		Color shadowColor = Color.black;

		drawStat(g2d, statText, centerX, y, textColor, shadowColor);
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

	public static void drawHoveredItemTooltip(Graphics g, int[] hoveredSlot) {
		Graphics2D g2d = (Graphics2D) g;

		int slotX = 0;
		int slotY = 0;
		int slotSize = (int) (unscaledSlotSize * scale);
		int slotSpacing = (int) (unscaledSlotSpacing * scale);

		if (hoveredSlot[0] == -1) {
			// If playing
			int inventoryBarWidth = (int) (120 * scale);
			int inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
			int inventoryBarY = (int) (screenHeight - 20 * scale - 60);

			int slotStartX = inventoryBarX + (inventoryBarWidth - (7 * slotSize + 6 * slotSpacing)) / 2;
			slotX = slotStartX + hoveredSlot[1] * (slotSize + slotSpacing);
			slotY = (int) (inventoryBarY + (20 * scale - slotSize) / 2);
			hoveredSlot[0] = 0;
		} else {
			// If in inventory menu

			int gridX = (int) (225 * scale);
			int gridY = (int) (148 * scale);

			int[] rowOrder = {3, 0, 1, 2};

			for (int j = 0; j < 4; j++) {
				for (int i = 0; i < 7; i++) {
					slotX = gridX + hoveredSlot[1] * (slotSize + slotSpacing);
					slotY = gridY + rowOrder[hoveredSlot[0]] * (slotSize + slotSpacing);
				}
			}
		}

		if (gameState == GameState.INVENTORY || (hoveredSlot[1] != player.inv.getSelectedSlot() && gameState == GameState.PLAYING)) {
			drawSelectedSlotOverlay(g2d, slotX, slotY, slotSize);
		}

		// Get item details
		Item hoveredItem = player.inv.getItem(hoveredSlot[0], hoveredSlot[1]);
		String itemName = hoveredItem.getDisplayName();
		String itemCount = "";
		if (hoveredItem.getCount() > 1) {
			itemCount = " (" + hoveredItem.getCount() + "x)";
		}
		String itemType = hoveredItem.getDisplayType();
		String itemEffect = hoveredItem.getDisplayEffect();
		String itemDescription = "\"" + hoveredItem.getDescription() + "\"";

		// Font and dimensions
		g2d.setFont(basicFont40);
		FontMetrics fm = g2d.getFontMetrics();

		// Calculate width based on text
		int tooltipWidth = Math.max(slotSize * 4, fm.stringWidth(itemName) + (int) (20 * scale / 3.75));
		int tooltipX = slotX - (tooltipWidth / 2) + slotSize / 2;
		int tooltipY;

		// Calculate dynamic height
		int lineHeight = fm.getHeight();
		int tooltipHeight = lineHeight * 4; // minimum height for basic text

		// Adjust for multi-line description
		String[] wrappedDescription = wrapText(itemDescription, tooltipWidth - 20, fm);
		tooltipHeight += lineHeight * wrappedDescription.length;

		List<String> differentEffects = new ArrayList<>();
		List<String[]> wrappedEffect = new ArrayList<>();
		int maxTooltipWidth = slotSize * 6; // Set maximum tooltip width

		// Split itemEffect into individual lines
		String[] effects = itemEffect.split("\\\\n");
		Collections.addAll(differentEffects, effects);
		tooltipWidth = Math.min(maxTooltipWidth, Math.max(tooltipWidth, fm.stringWidth(itemEffect) + 20));

		// Wrap each line of text
		for (String effect : differentEffects) {
			wrappedEffect.add(wrapText(effect, tooltipWidth - 20, fm));
		}
		tooltipHeight += lineHeight * (wrappedEffect.size() - 1); // Adjust height based on wrapped lines

		// Shift tooltip upwards if text exceeds height
		int triangleHeight = slotSize / 2;
		tooltipY = slotY - tooltipHeight - (triangleHeight);

		// Draw rounded tooltip box
		g2d.setColor(new Color(84, 84, 84, 190));
		g2d.fillRoundRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, (int) (45 * scale / 3.75), (int) (45 * scale / 3.75));

		// Draw triangle
		int triangleBase = slotSize;
		int[] xPoints = { slotX + slotSize / 2 - triangleBase / 2, slotX + slotSize / 2 + triangleBase / 2, slotX + slotSize / 2 };
		int[] yPoints = { tooltipY + tooltipHeight, tooltipY + tooltipHeight, tooltipY + tooltipHeight + triangleHeight };
		g2d.fillPolygon(xPoints, yPoints, 3);

		// Draw text within tooltip

		// Item name

		int textX = tooltipX + 10;
		int textY = tooltipY + lineHeight;
		g2d.setColor(Color.black);
		g2d.drawString(itemName, (int) (textX + textShadow), (int) (textY + textShadow));
		g2d.setColor(hoveredItem.getNameColor());
		g2d.drawString(itemName, textX, textY);

		// Item count
		int itemNameWidth = fm.stringWidth(itemName);
		g2d.setColor(Color.black);
		g2d.drawString(itemCount, (int) (textX + itemNameWidth + textShadow), (int) (textY + textShadow));
		g2d.setColor(Color.decode("#FFFFFF"));
		g2d.drawString(itemCount, textX + itemNameWidth, textY);

		textY += lineHeight;

		// Item type
		g2d.setColor(Color.black);
		g2d.drawString(itemType, (int) (textX + textShadow), (int) (textY + textShadow));
		g2d.setColor(Color.decode("#E0DE9B"));
		g2d.drawString(itemType, textX, textY);
		textY += lineHeight;

		// Item effect
		for (String[] effectWrappedLines : wrappedEffect) {
			for (String line : effectWrappedLines) {
				g2d.setColor(Color.black);
				g2d.drawString(line, (int) (textX + textShadow), (int) (textY + textShadow));
				g2d.setColor(Color.decode("#00A2FF"));
				g2d.drawString(line, textX, textY);
				textY += lineHeight;
			}
		}

		// Item description
		for (String line : wrappedDescription) {
			g2d.setColor(Color.black);
			g2d.drawString(line, (int) (textX + textShadow), (int) (textY + textShadow));
			g2d.setColor(Color.decode("#A0A0A0"));
			g2d.drawString(line, textX, textY);
			textY += lineHeight;
		}
	}

	// Helper method to wrap text
	private static String[] wrapText(String text, int maxWidth, FontMetrics fm) {
		List<String> lines = new ArrayList<>();
		StringBuilder line = new StringBuilder();

		for (String word : text.split(" ")) {
			if (fm.stringWidth(line + word) > maxWidth) {
				lines.add(line.toString());
				line = new StringBuilder();
			}
			line.append(word).append(" ");
		}
		lines.add(line.toString().trim());

		return lines.toArray(new String[0]);
	}

	public static void createFloatingText(String textToDisplay, Color color, double x, double y, boolean movesUp) {
		floatingText.add(textToDisplay);
		Point point = new Point((int) x, (int) y);
		floatingTextPosition.add(point);
		floatingTextColor.add(color);

		try {

			AtomicInteger index = new AtomicInteger(floatingTextPosition.size() - 1);
			Timer movingUp = new Timer(200, e -> {
				if (index.get() != -1) {
					Point newPoint = new Point(floatingTextPosition.get(index.get()).x, (floatingTextPosition.get(index.get()).y - 1));
					floatingTextPosition.set(index.get(), newPoint);
				}
			});
			if (movesUp) {
				movingUp.setRepeats(true);
				movingUp.start();
			}

			Timer timer = new Timer(2500, l -> {
				index.addAndGet(-1);
				movingUp.stop();
				floatingText.removeFirst();
				floatingTextPosition.removeFirst();
				floatingTextColor.removeFirst();
			});
			timer.setRepeats(false);
			timer.start();
		} catch (IndexOutOfBoundsException e) {
			// This would mean floatingText was removed, so stop
		}
	}

	private static void drawFloatingText(Graphics2D g2d) {
		for (int i = 0; i < floatingText.size(); i++) {
			g2d.setFont(basicFont40);
			g2d.setColor(Color.black);
			g2d.drawString(floatingText.get(i), (int) ((floatingTextPosition.get(i).x) * scale + textShadow), (int) ((floatingTextPosition.get(i).y) * scale + textShadow));
			g2d.setColor(floatingTextColor.get(i));
			g2d.drawString(floatingText.get(i), (int) (floatingTextPosition.get(i).x * scale), (int) (floatingTextPosition.get(i).y * scale));
		}
	}

	public static void drawDraggedItem(Graphics2D g2d, MouseHandler mouseHandler) {
		Item draggedItem = player.inv.getDraggedItem();

		int slotSize = (int) (unscaledSlotSize * scale);

		g2d.drawImage(draggedItem.getIcon(), mouseHandler.getMouseX(), mouseHandler.getMouseY(), slotSize, slotSize, null);
	}

	public static void renderLevelDesigner(Graphics g, JPanel panel, MouseHandler mouseHandler) {
		Graphics2D g2d = (Graphics2D) g;
		double scaleByScreenSize = gameScale / 3.75;

		// ANTI-ALIASING
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw game components
		BoxesHandling.renderBoxes(g2d, player.pos.getCameraOffsetX(), player.pos.getCameraOffsetY(), gameScale, tileSize);

		if (levelDesignerGrid) {
			int timesToRepeatHorizontal = panel.getWidth() / tileSize;
			int timesToRepeatVertical = panel.getHeight() / tileSize;

			g2d.setColor(Color.black);
			for (int i = 1; i < timesToRepeatHorizontal; i++) {
				g2d.drawLine(tileSize * i, 0, tileSize * i, panel.getHeight());
			}
			for (int j = 1; j < timesToRepeatVertical; j++) {
				g2d.drawLine(0, tileSize * j, panel.getWidth(), tileSize * j);
			}
		}

		g2d.dispose();
	}

	public static void fadeIn() {
		if (isFading == FadingState.UNFADED || isFading == FadingState.FADING_OUT) {
			isFading = FadingState.FADING_IN;
		}
	}

	public static void fadeOut() {
		if (isFading== FadingState.FADED || isFading == FadingState.FADING_IN) {
			isFading = FadingState.FADING_OUT;
		}
	}
}
