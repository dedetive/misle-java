package com.ded.misle;

import com.ded.misle.boxes.BoxesHandling;
import com.ded.misle.items.Item;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.levelDesigner;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.SaveFile.loadSaveFile;
import static com.ded.misle.SaveFile.saveEverything;
import static com.ded.misle.boxes.BoxesHandling.storeCachedBoxes;
import static com.ded.misle.boxes.BoxesLoad.loadBoxes;
import static com.ded.misle.boxes.BoxesLoad.unloadBoxes;
import static java.lang.System.currentTimeMillis;

import java.awt.Rectangle;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

	private static final List<String> floatingText = new ArrayList<>();
	private static final List<Point> floatingTextPosition = new ArrayList<>();
	private static final List<Color> floatingTextColor = new ArrayList<>();

	private static double isFacingRight;
	private static boolean mirror;

	private static Font comfortaaFont96 = FontManager.loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (96 * scale / 3.75));
	private static Font ubuntuFont35 = FontManager.loadFont("/fonts/Ubuntu-Medium.ttf", (float) (35 * scale / 3.75));
	private static Font basicFont40 = FontManager.loadFont("/fonts/Basic-Regular.ttf", (float) (40 * scale / 3.75));
	private static Font itemCountFont = FontManager.loadFont("/fonts/Ubuntu-Regular.ttf", (float) (40 * scale / 3.75));
	private static Font ubuntuFont44 = FontManager.loadFont("/fonts/Ubuntu-Medium.ttf", (float) (44 * scale / 3.75));

	public static void updateFontSizes() {
		comfortaaFont96 = FontManager.loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (96 * scale / 3.75));
		ubuntuFont35 = FontManager.loadFont("/fonts/Ubuntu-Medium.ttf", (float) (40 * scale / 3.75));
		basicFont40 = FontManager.loadFont("/fonts/Basic-Regular.ttf", (float) (40 * scale / 3.75));
		itemCountFont = FontManager.loadFont("/fonts/Ubuntu-Regular.ttf", (float) (50 * scale / 3.75));
		ubuntuFont44 = FontManager.loadFont("/fonts/Ubuntu-Medium.ttf", (float) (44 * scale / 3.75));
	}

	private static void createButton(Rectangle button, String text, Runnable action, JPanel panel, Graphics2D g2d, double scaleByScreenSize) {
		g2d.fillRoundRect(button.x, button.y, button.width, button.height, (int) (69 * scaleByScreenSize), (int) (69 * scaleByScreenSize));
		g2d.setColor(new Color(0, 0, 0));
		g2d.setFont(ubuntuFont44);
		FontMetrics fm = g2d.getFontMetrics();
		String buttonText = text;
		int textWidth = fm.stringWidth(buttonText);
		int textHeight = fm.getAscent();
		int textX = button.x + (button.width - textWidth) / 2;
		int textY = button.y + (button.height + textHeight) / 2 - fm.getDescent() + (int) (2 * scale);
		g2d.drawString(buttonText, textX, textY);

		addClickable(button, action, panel);
	}

	private static final List<Object[]> clickables = new ArrayList<>();
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
		startTime = currentTimeMillis();
		gameState = GameState.LOADING_MENU;

		loadSaveFile();
		loadBoxes();

		Timer timer = new Timer(LOADING_DURATION, e -> {
			for (int i = 15; i > 0; i--) {
				storeCachedBoxes(i);
			}
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
		gameState = GamePanel.GameState.PLAYING;
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
		gameState = GamePanel.GameState.OPTIONS_MENU;
	}

	public static void goToPreviousMenu() {
		switch (previousMenu) {
			case "MAIN_MENU":
				gameState = GamePanel.GameState.MAIN_MENU;
				break;
			case "OPTIONS_MENU":
				gameState = GamePanel.GameState.OPTIONS_MENU;
				break;
			case "PLAYING":
				gameState = GamePanel.GameState.PLAYING;
				break;
			case "PAUSE_MENU":
				gameState = GamePanel.GameState.PAUSE_MENU;
		}
		previousMenu = currentMenu;
	}

	public static void goToMainMenu() {
		saveEverything();
		unloadBoxes();
		player.unloadPlayer();
		previousMenu = currentMenu;
		gameState = GamePanel.GameState.MAIN_MENU;
	}

	public static void pauseGame() {
		previousMenu = currentMenu;
		currentMenu = "PAUSE_MENU";
		gameState = GamePanel.GameState.PAUSE_MENU;
	}

	public static void renderMainMenu(Graphics g, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			currentMenu = "MAIN_MENU";

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

			// MENU ITSELF

			// Centering the title

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(comfortaaFont96);
			FontMetrics fm = g2d.getFontMetrics();
			String titleText = LanguageManager.getText("misle");
			int textWidth = fm.stringWidth(titleText);
			int centerX = (int) ((screenWidth - textWidth) / 2);
			int textY = (int) (182 * scaleByScreenSize);
			g2d.drawString(titleText, centerX, textY);

			// Play button

			g2d.setColor(new Color(191, 191, 191));
			int playButtonX = (int) (736 * scaleByScreenSize);
			int playButtonY = (int) (462 * scaleByScreenSize);
			int playButtonWidth = (int) (448 * scaleByScreenSize);
			int playButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

			if (!levelDesigner) {
				createButton(playButton, LanguageManager.getText("main_menu_play"), GameRenderer::gameStart, panel, g2d, scaleByScreenSize);
			} else {
				createButton(playButton, LanguageManager.getText("main_menu_level_designer"), GameRenderer::enterLevelDesigner, panel, g2d, scaleByScreenSize);
			}

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
			g2d.setFont(basicFont40);
			g2d.drawString(gameVersion, (int) (1640 * scaleByScreenSize), (int) (1010* Math.pow(scaleByScreenSize, 1.04)));
		}
	}

	public static void renderPauseMenu(Graphics g, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

			// MENU ITSELF

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(comfortaaFont96);
			FontMetrics fm = g2d.getFontMetrics();
			String titleText = LanguageManager.getText("pause_menu_paused");
			int textWidth = fm.stringWidth(titleText);
			int centerX = (int) ((screenWidth - textWidth) / 2);
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

	public static void renderOptionsMenu(Graphics g, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

			// MENU ITSELF

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(comfortaaFont96);
			FontMetrics fm = g2d.getFontMetrics();
			String titleText = LanguageManager.getText("options_menu_options");
			int textWidth = fm.stringWidth(titleText);
			int centerX = (int) ((screenWidth - textWidth) / 2);
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

	public static void renderLoadingMenu(Graphics g, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			// ANTI-ALIASING
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			double scaleByScreenSize = scale / 3.75;

			// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

			// MENU ITSELF

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(comfortaaFont96);
			FontMetrics fm = g2d.getFontMetrics();
			String titleText = LanguageManager.getText("loading_menu_loading");
			int textWidth = fm.stringWidth(titleText);
			int centerX = (int) ((screenWidth - textWidth) / 2);
			int textY = (int) (182 * scaleByScreenSize);
			g2d.drawString(titleText, centerX, textY);

			// Progress bar

			long elapsedTime = currentTimeMillis() - startTime;
			double progress = Math.min((double) elapsedTime / LOADING_DURATION, 1.0); // Calculate progress (0.0 to 1.0)
			String percentage = (int) (progress * 100) + "%";

			int progressBarWidth = (int) (640 * progress * scaleByScreenSize);
			int progressBarHeight = (int) (25 * scaleByScreenSize);
			int progressBarY = (int) ((textY + 560) * scaleByScreenSize);

			g2d.setColor(new Color(100, 200, 100));
			g2d.fillRect((int) (660 * scaleByScreenSize), progressBarY, progressBarWidth, progressBarHeight);

			g2d.setColor(new Color(191, 191, 191));
			g2d.setFont(ubuntuFont35);
			FontMetrics percentageFm = g2d.getFontMetrics();
			textWidth = percentageFm.stringWidth(percentage); // Use the new font metrics for percentage
			centerX = (int) ((screenWidth - textWidth) / 2);
			textY = (int) ((progressBarY) - 20 * scaleByScreenSize);
			g2d.drawString(percentage, centerX, textY);
		}
	}

	public static void renderPlayingGame(Graphics g, JPanel panel, MouseHandler mouseHandler) {
		Graphics2D g2d = (Graphics2D) g;
		double scaleByScreenSize = scale / 3.75;

		// ANTI-ALIASING
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw game components
		BoxesHandling.renderBoxes(g2d, player.pos.getCameraOffsetX(), player.pos.getCameraOffsetY(), scale, tileSize);

		// Player position adjustments
		int playerScreenX = (int) (player.pos.getX() - player.pos.getCameraOffsetX());
		int playerScreenY = (int) (player.pos.getY() - player.pos.getCameraOffsetY());

		// Draw the player
		g2d.setColor(Color.WHITE);
		Rectangle playerRect = new Rectangle(playerScreenX, playerScreenY, (int) player.attr.getWidth(), (int) player.attr.getHeight());
		drawRotatedRect(g2d, playerRect, player.pos.getRotation());

		drawHandItem(g2d, playerScreenX, playerScreenY, scaleByScreenSize);

		drawUIElements(g2d, playerScreenX, playerScreenY);

		if (floatingText != null) {
			drawFloatingText(g2d);
		}

		if (gameState == GamePanel.GameState.INVENTORY) {
			renderInventoryMenu(g, panel);
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

		g2d.dispose();
	}

	private static void drawHandItem(Graphics2D g2d, double playerScreenX, double playerScreenY, double scaleByScreenSize) {
		if (player.inv.hasHeldItem()) {

			if (player.stats.getWalkingDirection().equals("right")) {
				isFacingRight = 0.5;
				mirror = false;
			} else if (player.stats.getWalkingDirection().equals("left")) {
				isFacingRight = -1;
				mirror = true;
			}

			double distance = playerScreenX + (player.attr.getWidth() / 2) * 2 * isFacingRight * scaleByScreenSize;

			Item selectedItem = player.inv.getSelectedItem();

			if (selectedItem.getCountLimit() >= 16 && selectedItem.getCount() > selectedItem.getCountLimit() / 3) {
				double pos = 12 * isFacingRight * scaleByScreenSize;
				drawRotatedImage(g2d, selectedItem.getIcon(), distance + pos + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + 15 * scaleByScreenSize + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), 35 + selectedItem.getAnimationRotation() * Math.ceil(isFacingRight), mirror);
			}

			if (selectedItem.getCountLimit() >= 100 && selectedItem.getCount() > 2 * selectedItem.getCountLimit() / 3) {
				double pos = -12 * isFacingRight * scaleByScreenSize;
				drawRotatedImage(g2d, selectedItem.getIcon(), distance + pos + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + 15 * scaleByScreenSize + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), -35 + selectedItem.getAnimationRotation() * Math.ceil(isFacingRight), mirror);
			}

			drawRotatedImage(g2d, selectedItem.getIcon(), (int) distance + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), selectedItem.getAnimationRotation() * Math.ceil(isFacingRight), mirror);
		}
	}

	private static void drawUIElements(Graphics2D g2d, int playerScreenX, int playerScreenY) {
		drawHealthBar(g2d, playerScreenX, playerScreenY);
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

		// Background of the inventory
		g2d.setColor(new Color(30, 30, 30, 150)); // Semi-transparent black
		g2d.fillRect(inventoryBarX, inventoryBarY, inventoryBarWidth, inventoryBarHeight);

		// Slots info

		int slotWidth = (int) (30 * scale);
		int slotHeight = (int) (30 * scale);
		int slotSpacing = (int) (3 * scale);

		int totalSlotsWidth = 7 * slotWidth + (6 * slotSpacing);
		int slotStartX = inventoryBarX + (inventoryBarWidth - totalSlotsWidth) / 2;

		int selectedSlot = player.inv.getSelectedSlot();

		for (int i = 0; i < 7; i++) {
			int slotX = slotStartX + i * (slotWidth + slotSpacing);
			int slotY = inventoryBarY + (inventoryBarHeight - slotHeight) / 2;

			// Draw the slot (e.g., as a gray rectangle)
			g2d.setColor(Color.GRAY);
			g2d.fillRect(slotX, slotY, slotWidth, slotHeight);

		// Draw item if there is one in this slot (disabled as there's currently no getIcon())
			Item item = player.inv.getItem(0, i);
			if (item != null) {
				g2d.drawImage(item.getIcon(), slotX, slotY, slotWidth, slotHeight, null);
				int itemCount = item.getCount();
				if (itemCount > 1) {
					g2d.setFont(itemCountFont);
					g2d.setColor(Color.white);
					FontMetrics fm = g2d.getFontMetrics();
					int textWidth = fm.stringWidth(Integer.toString(itemCount));
					int textX = (int) (slotX - textWidth + slotWidth);
					int textY = (int) (slotY + 8 * slotHeight / 9);
					g2d.drawString(Integer.toString(itemCount), textX, textY);
				}
			}

			if (i == selectedSlot) {
				drawSelectedSlotOverlay(g2d, slotX, slotY, slotWidth, slotHeight);
			}
		}
	}

	private static void drawSelectedSlotOverlay(Graphics2D g2d, int slotX, int slotY, int slotWidth, int slotHeight) {
		g2d.setColor(new Color(255, 255, 255, 100)); // Semi-transparent overlay
		g2d.fillRect(slotX, slotY, slotWidth, slotHeight);
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

			int slotWidth = (int) (30 * scaleByScreenSize);
			int slotSpacing = (int) (3 * scaleByScreenSize);
			int totalSlotsWidth = 7 * slotWidth + (6 * slotSpacing);
			int slotStartX = inventoryBarX + (inventoryBarWidth - totalSlotsWidth) / 2;

			int slotX = slotStartX + player.inv.getSelectedSlot() * (slotWidth + slotSpacing);
			int slotY = inventoryBarY;

			// Position the name above the selected slot
			selectedItemNamePosition = new Point(slotX + slotWidth / 2, slotY - 70);
			itemNameDisplayStartTime = System.currentTimeMillis();
		} else {
			selectedItemName = null;
			selectedItemNamePosition = null;
		}
	}

	public static void drawSelectedItemName(Graphics2D g2d) {
		if (selectedItemName != null && selectedItemNamePosition != null) {
			// Check if the current time is within 5 seconds of the start time
			long currentTime = System.currentTimeMillis();
			if (currentTime - itemNameDisplayStartTime < 5000) {
				g2d.setFont(ubuntuFont35);
				g2d.setColor(player.inv.getSelectedItem().getNameColor());
				FontMetrics fm = g2d.getFontMetrics();
				int textWidth = fm.stringWidth(selectedItemName);

				int textX = selectedItemNamePosition.x - textWidth / 2;
				int textY = selectedItemNamePosition.y;

				g2d.drawString(selectedItemName, textX, textY);
			} else {
				// Clear the selected item name after 5 seconds
				selectedItemName = null;
				selectedItemNamePosition = null;
			}
		}
	}


	public static void renderInventoryMenu(Graphics g, JPanel panel) {
		double scaleByScreenSize = scale / 3.75;

		Graphics2D g2d = (Graphics2D) g;

		// Semi-transparent background overlay
		g2d.setColor(new Color(15, 15, 15, 130));
		g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

		// Inventory title
		g2d.setColor(new Color(233, 233, 233));
		g2d.setFont(comfortaaFont96);
		FontMetrics fm = g2d.getFontMetrics();
		String titleText = LanguageManager.getText("inventory_menu_inventory");
		int textWidth = fm.stringWidth(titleText);
		int centerX = (int) ((screenWidth - textWidth) / 2);
		int textY = (int) (182 * scaleByScreenSize);
		g2d.drawString(titleText, centerX, textY);

		// Slot dimensions and spacing
		int slotSize = (int) (30 * scale);
		int slotSpacing = (int) (3 * scale);

		// Calculate the total width and height of the 7x4 grid with spacing
		int gridWidth = 7 * slotSize + 6 * slotSpacing;
		int gridHeight = 4 * slotSize + 3 * slotSpacing;

		// Center the grid on the screen
		int gridX = (int) ((screenWidth - gridWidth) / 2);
		int gridY = (int) ((screenHeight - gridHeight) / 2);

		// Draw slots and item icons in the specified row order: row 1, row 2, row 3, row 0
		int[] rowOrder = {1, 2, 3, 0};

		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 7; i++) {
				int slotX = gridX + i * (slotSize + slotSpacing);
				int slotY = gridY + j * (slotSize + slotSpacing);

				// Draw the slot as a gray rectangle
				g2d.setColor(Color.GRAY);
				g2d.fillRect(slotX, slotY, slotSize, slotSize);

				// Draw item icon if there is one in this slot
				Item item = player.inv.getItem(rowOrder[j], i);
				if (item != null) {
					g2d.drawImage(item.getIcon(), slotX, slotY, slotSize, slotSize, null);
					int itemCount = item.getCount();
					if (itemCount > 1) {
						g2d.setFont(itemCountFont);
						g2d.setColor(Color.white);
						fm = g2d.getFontMetrics();
						textWidth = fm.stringWidth(Integer.toString(itemCount));
						int textX = slotX - textWidth + slotSize;
						int countY = slotY + slotSize;
						g2d.drawString(Integer.toString(itemCount), textX, countY);
					}
				}
			}
		}
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
		int slotWidth = (int) (30 * scale);
		int slotHeight = (int) (30 * scale);
		int slotSpacing = (int) (3 * scale);

		if (hoveredSlot[0] == -1) {
			// If playing
			int inventoryBarWidth = (int) (120 * scale);
			int inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
			int inventoryBarY = (int) (screenHeight - 20 * scale - 60);

			int slotStartX = inventoryBarX + (inventoryBarWidth - (7 * slotWidth + 6 * slotSpacing)) / 2;
			slotX = slotStartX + hoveredSlot[1] * (slotWidth + slotSpacing);
			slotY = (int) (inventoryBarY + (20 * scale - slotHeight) / 2);
			hoveredSlot[0] = 0;
		} else {
			// If in inventory menu

			int gridWidth = 7 * slotWidth + 6 * slotSpacing;
			int gridHeight = 4 * slotHeight + 3 * slotSpacing;

			int gridX = (int) ((screenWidth - gridWidth) / 2);
			int gridY = (int) ((screenHeight - gridHeight) / 2);

			int[] rowOrder = {3, 0, 1, 2};

			for (int j = 0; j < 4; j++) {
				for (int i = 0; i < 7; i++) {
					slotX = gridX + hoveredSlot[1] * (slotWidth + slotSpacing);
					slotY = gridY + rowOrder[hoveredSlot[0]] * (slotHeight + slotSpacing);
				}
			}
		}

		if (gameState == GameState.INVENTORY || (hoveredSlot[1] != player.inv.getSelectedSlot() && gameState == GameState.PLAYING)) {
			drawSelectedSlotOverlay(g2d, slotX, slotY, slotWidth, slotHeight);
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
		int tooltipWidth = Math.max(slotWidth * 4, fm.stringWidth(itemName) + (int) (20 * scale / 3.75));
		int tooltipX = slotX - (tooltipWidth / 2) + slotWidth / 2;
		int tooltipY;

		// Calculate dynamic height
		int lineHeight = fm.getHeight();
		int tooltipHeight = lineHeight * 4; // minimum height for basic text

		// Adjust for multi-line description
		String[] wrappedDescription = wrapText(itemDescription, tooltipWidth - 20, fm);
		tooltipHeight += lineHeight * wrappedDescription.length;

		int maxTooltipWidth = slotWidth * 6; // Set maximum tooltip width
		tooltipWidth = Math.min(maxTooltipWidth, Math.max(tooltipWidth, fm.stringWidth(itemEffect) + 20));
		String[] wrappedEffect = wrapText(itemEffect, tooltipWidth - 20, fm);
		tooltipHeight += lineHeight * (wrappedEffect.length - 1); // Adjust height based on wrapped lines

		// Shift tooltip upwards if text exceeds height
		int triangleHeight = slotHeight / 2;
		tooltipY = slotY - tooltipHeight - (triangleHeight);

		// Draw rounded tooltip box
		g2d.setColor(new Color(84, 84, 84, 190));
		g2d.fillRoundRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, (int) (45 * scale / 3.75), (int) (45 * scale / 3.75));

		// Draw triangle
		int triangleBase = slotWidth;
		int[] xPoints = { slotX + slotWidth / 2 - triangleBase / 2, slotX + slotWidth / 2 + triangleBase / 2, slotX + slotWidth / 2 };
		int[] yPoints = { tooltipY + tooltipHeight, tooltipY + tooltipHeight, tooltipY + tooltipHeight + triangleHeight };
		g2d.fillPolygon(xPoints, yPoints, 3);

		// Draw text within tooltip
		g2d.setColor(hoveredItem.getNameColor());
		int textX = tooltipX + 10;
		int textY = tooltipY + lineHeight;

		g2d.setColor(hoveredItem.getNameColor());
		g2d.drawString(itemName, textX, textY);
		int itemNameWidth = fm.stringWidth(itemName);
		g2d.setColor(Color.decode("#FFFFFF"));
		g2d.drawString(itemCount, textX + itemNameWidth, textY);

		textY += lineHeight;

		g2d.setColor(Color.decode("#E0DE9B"));
		g2d.drawString(itemType, textX, textY);
		textY += lineHeight;

		g2d.setColor(Color.decode("#00A2FF"));
		for (String line : wrappedEffect) {
			g2d.drawString(line, textX, textY);
			textY += lineHeight;
		}

		g2d.setColor(Color.decode("#A0A0A0"));
		for (String line : wrappedDescription) {
			g2d.drawString(line, textX, textY);
			textY += lineHeight;
		}
	}

	// Helper method to wrap text
	private static String[] wrapText(String text, int maxWidth, FontMetrics fm) {
		java.util.List<String> lines = new java.util.ArrayList<>();
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
	}

	private static void drawFloatingText(Graphics2D g2d) {
		for (int i = 0; i < floatingText.size(); i++) {
			g2d.setColor(floatingTextColor.get(i));
			g2d.setFont(basicFont40);
			g2d.drawString(floatingText.get(i), (int) (floatingTextPosition.get(i).x * scale), (int) (floatingTextPosition.get(i).y * scale));
		}
	}

	public static void drawDraggedItem(Graphics2D g2d, MouseHandler mouseHandler) {
		Item draggedItem = player.inv.getDraggedItem();

		int slotSize = (int) (30 * scale);

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
}
