package com.ded.misle.core;

import com.ded.misle.world.boxes.HPBox;
import com.ded.misle.input.KeyHandler;
import com.ded.misle.input.MouseHandler;
import com.ded.misle.world.player.Player;
import com.ded.misle.world.player.PlayerAttributes;
import com.ded.misle.renderer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.ded.misle.core.Setting.*;
import static com.ded.misle.renderer.FontManager.buttonFont;
import static com.ded.misle.world.boxes.HPBox.getHPBoxes;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.input.KeyHandler.updateDesignerSpeed;
import static com.ded.misle.Launcher.*;
import static com.ded.misle.renderer.InventoryRenderer.updateRendererVariableScales;
import static com.ded.misle.renderer.MenuButton.clearButtons;
import static com.ded.misle.input.MouseHandler.updateMouseVariableScales;
import static com.ded.misle.core.SaveFile.saveEverything;
import static com.ded.misle.renderer.PlayingRenderer.updatePlayingVariableScales;
import static com.ded.misle.world.enemies.EnemyAI.updateEnemyAI;

/**
 * This is for loading and altering how the window behaves. Only do this once, otherwise new screens are created.
 */
public class GamePanel extends JPanel implements Runnable {

	private final JFrame window;
	private static volatile boolean running = true;
	public static KeyHandler keyH;
	public MouseHandler mouseHandler;
	Thread gameThread;

	// TILES SIZE

	static final int originalTileSize = 64; // 64x64 tiles
	public static double gameScale = scale;
	public static int tileSize = updateTileSize();
	static final double maxScreenCol = 24; // Horizontal
	static final double maxScreenRow = 13.5; // Vertical
	public static double screenWidth = maxScreenCol * tileSize;
	public static double screenHeight = maxScreenRow * tileSize;

	public static int updateTileSize() {
		tileSize = (int) (originalTileSize * gameScale) / 2;
		return tileSize;
	}

	// INITIALIZING PLAYER

	public static Player player;
	static {
		player = new Player();
		keyH = new KeyHandler();
	}

	// GAMESTATE

	public enum GameState {
		DIALOG,
		PLAYING,
		INVENTORY,
		MAIN_MENU,
		PAUSE_MENU,
		OPTIONS_MENU,
		LOADING_MENU,
		SAVE_CREATOR,
		SAVE_SELECTOR,
		LEVEL_DESIGNER,
		FROZEN_PLAYING,
	}

	public enum PlayingGameState {
		DIALOG,
		PLAYING,
		INVENTORY,
		PAUSE_MENU,
		LEVEL_DESIGNER,
		FROZEN_PLAYING,
	}

	public static GameState gameState = GameState.MAIN_MENU; // Start in MAIN_MENU by default

	// CAMERA WORLD BOUNDARIES

	static double originalWorldWidth = 1;
	static double originalWorldHeight = 1;
	static double worldWidth = originalWorldWidth * scale;
	static double worldHeight = originalWorldHeight * scale;

	public static void setWorldBorders(int width, int height) {
		originalWorldWidth = width * 20;
		originalWorldHeight = height * 20;
		worldWidth = originalWorldWidth * scale;
		worldHeight = originalWorldHeight * scale;
	}

	public GamePanel() {
		// Setting up the JFrame
		window = new JFrame();
		window.setTitle(windowTitle);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize((int) screenWidth, (int) screenHeight);
		setWindow(window);
		try {
			forceResize(screenSize.str());
		} catch (IllegalArgumentException e) {
			forceResize(screenSize.strDefault());
		}

		window.add(this);
		this.setLayout(null);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);

		this.setBackground(windowBackground);

		mouseHandler = new MouseHandler();
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);

		updateMouseVariableScales();

		forceResize(screenSize.str());

		// Handle window close event
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quitGame();
			}
		});
	}

	public static void quitGame() {
		for (PlayingGameState playingGameState : PlayingGameState.values()) {
			if (gameState == GameState.valueOf(String.valueOf(playingGameState))) {
				saveEverything();
			}
		}
		running = false;
		Timer timer = new Timer(10, e -> {
			System.exit(0);
		});
		timer.setRepeats(false);
		timer.start();
	}

	private enum ScreenSizeDimensions {
		small(1.5),
		medium(2),
		big(3.125),
		huge(3.75),
		tv_sized(5),
		comical(15),
		;

		final int x;
		final int y;
		final double scale;

		ScreenSizeDimensions(double scale) {
			int x = (int) (scale * 512);
			int y = (int) (scale * 288);
			this.x = x;
			this.y = y;
			this.scale = scale;
		}
	}

	private static JFrame jframe;
	private static void setWindow(JFrame frame) {
		jframe = frame;
	}
	public static JFrame getWindow() {
		return jframe;
	}

	public static void forceResize(String screenSize) {
		double previousScale = scale;
		ScreenSizeDimensions screen = ScreenSizeDimensions.valueOf(screenSize);
		int preferredX = screen.x;
		int preferredY = screen.y;
		scale = screen.scale;
		gameScale = scale;
		GamePanel.screenWidth = preferredX;
		GamePanel.screenHeight = preferredY;

		JFrame window = getWindow();

		// Handle fullscreen logic
		if (isFullscreen.bool()) {
			if (fullscreenMode.value.equals("windowed")) {
				// Set window to borderless and maximize
				window.dispose();
				window.setUndecorated(true);
				window.setResizable(false);
				window.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
				window.setVisible(true);
				scale = (double) window.getWidth() / 512;
				gameScale = scale;
				GamePanel.screenWidth = window.getWidth();
				GamePanel.screenHeight = window.getHeight();
			} else if (fullscreenMode.value.equals("exclusive")) {
				// Switch to exclusive fullscreen mode
				GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				if (device.isFullScreenSupported()) {
					window.dispose();
					window.setUndecorated(true);
					device.setFullScreenWindow(window);
					scale = (double) window.getWidth() / 512;
					gameScale = scale;
					GamePanel.screenWidth = window.getWidth();
					GamePanel.screenHeight = window.getHeight();
				} else {
					System.out.println("Exclusive fullscreen not supported on this device.");
				}
			}
		} else {
			// Restore windowed mode
			window.dispose();
			window.setUndecorated(false);
			window.setPreferredSize(new Dimension(preferredX, preferredY));
			window.pack();
			window.setResizable(false);
			window.setLocationRelativeTo(null);
			window.setVisible(true);
		}

		// Update game variables for resizing
		updateTileSize();
		player.attr.updateStat(PlayerAttributes.Stat.SPEED);
		player.setBoxScaleHorizontal(0.91);
		player.setBoxScaleVertical(0.91);
		worldWidth = originalWorldWidth * scale;
		worldHeight = originalWorldHeight * scale;

		clearButtons();
		FontManager.updateFontSizes();
		updateRendererVariableScales();
		updateMouseVariableScales();
		updatePlayingVariableScales();
		if (levelDesigner) {
			updateDesignerSpeed();
		}

		PlayingRenderer.updateSelectedItemNamePosition();

		window.repaint();
	}


	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	public void showScreen() {
		window.setVisible(true);
	}

	public static double nsPerFrame;
	public static double deltaTime;

	public static AtomicLong frameCount = new AtomicLong();
	@Override
	public void run() {
		long lastTime = System.nanoTime(); // Using nanoTime for precision with delta time
		double delta = 0;
		nsPerFrame = 1000000000.0 / Math.clamp(frameRateCap.integer(), 30, 144);

		// GAME LOOP

		while (gameThread != null && running) {
			long currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / nsPerFrame;
			deltaTime = (currentTime - lastTime) / 1e9;
			lastTime = currentTime;

			// Process updates and rendering while delta is >= 1
			while (delta >= 1) {
				switch (gameState) {
					case PLAYING, INVENTORY -> updateGame(); // Only update if in the playing state
					case PAUSE_MENU -> {
						if (player.keys.keyPressed.get(KeyHandler.Key.PAUSE)) {
							if (!levelDesigner) {
								softGameStart();
							} else {
								softEnterLevelDesigner();
							}
							clearButtons();
							this.setCursor(Cursor.getDefaultCursor());
							player.keys.keyPressed.put(KeyHandler.Key.PAUSE, false);
						}
					}
					case LEVEL_DESIGNER -> {
						player.pos.setCameraOffsetX(player.getX() - screenWidth / 2 + player.getBoxScaleHorizontal() / 2 * tileSize);
						player.pos.setCameraOffsetY(player.getY() - screenHeight / 2 + player.getBoxScaleVertical() / 2 * tileSize);

						player.pos.setCameraOffsetX(Math.max(0, Math.min(player.pos.getCameraOffsetX(), worldWidth - screenWidth)));
						player.pos.setCameraOffsetY(Math.max(0, Math.min(player.pos.getCameraOffsetY(), worldHeight - screenHeight)));

						keyH.updateKeys(mouseHandler);
						mouseHandler.updateMouse();
					}
					case DIALOG -> {
						keyH.updateKeys(mouseHandler);
						mouseHandler.updateMouse();
					}
					case null, default -> {
						keyH.updateKeys(mouseHandler);
					}
				}
				delta--;
			}

			renderFrame(); // Render the appropriate frame based on gameState
			frameCount.getAndIncrement();
			Timer framePastSecond = new Timer(1000, evt -> {
				frameCount.getAndDecrement();
			});
			framePastSecond.setRepeats(false);
			framePastSecond.start();

			// Sleep dynamically to maintain target FPS
			try {
				Thread.sleep(Math.max(0, (long) ((nsPerFrame - (System.nanoTime() - lastTime)) / 1000000))); // Sleep in milliseconds
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}


	public void updateGame() {
		long currentTime = System.currentTimeMillis();

		// Update the camera offset to center the player in the view
		player.pos.setCameraOffsetX(player.getX() - screenWidth / 2 + player.getBoxScaleHorizontal() / 2);
		player.pos.setCameraOffsetY(player.getY() - screenHeight / 2 + player.getBoxScaleVertical() / 2);

		player.pos.setCameraOffsetX(Math.max(0, Math.min(player.pos.getCameraOffsetX(), worldWidth - screenWidth)));
		player.pos.setCameraOffsetY(Math.max(0, Math.min(player.pos.getCameraOffsetY(), worldHeight - screenHeight)));

		player.attr.checkIfLevelUp();

		updateEnemyAI();

		for (HPBox box : getHPBoxes()) {
			box.updateRegenerationHP(currentTime);
		}
		keyH.updateKeys(mouseHandler);
		mouseHandler.updateMouse();
		}


	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		switch (gameState) {
			case GameState.INVENTORY:
			case GameState.PLAYING:
			case GameState.FROZEN_PLAYING:
			case GameState.DIALOG:
				PlayingRenderer.renderPlayingGame(g, mouseHandler);
				break;
			case GameState.MAIN_MENU:
				MenuRenderer.renderMainMenu(g, this);
				break;
			case GameState.OPTIONS_MENU:
				SettingsMenuRenderer.renderOptionsMenu(g, this);
				break;
			case GameState.PAUSE_MENU:
				MenuRenderer.renderPauseMenu(g, this);
				break;
			case GameState.LOADING_MENU:
				MenuRenderer.renderLoadingMenu(g, this);
				break;
			case GameState.LEVEL_DESIGNER:
				LevelDesignerRenderer.renderLevelDesigner(g, this, mouseHandler);
				break;
			case GameState.SAVE_SELECTOR:
				SaveSelector.renderSaveSelector(g, this);
				break;
			case SAVE_CREATOR:
				SaveCreator.renderSaveCreator(g, this);
				break;
		}

		if (displayFPS.bool()) {
			g2d.setFont(buttonFont);
			String text = "FPS: " + frameCount;
			FontMetrics fm = g2d.getFontMetrics(buttonFont);
			int textWidth = fm.stringWidth(text);
			int textX = (int) (screenWidth - textWidth) - 8;
			int textY = fm.getHeight() - 8;
			g2d.setColor(FPSShadowColor);
			drawColoredText(g2d, text, (int) (textX + textShadow), (int) (textY + textShadow));
			g2d.setColor(FPSColor);
			drawColoredText(g2d, text, textX, textY);
		}
	}

	public void renderFrame() {
		repaint();
	}
}
