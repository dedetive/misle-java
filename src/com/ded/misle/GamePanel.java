package com.ded.misle;

import com.ded.misle.input_handler.KeyHandler;
import com.ded.misle.input_handler.MouseHandler;
import com.ded.misle.player.Player;
import com.ded.misle.player.PlayerAttributes;
import com.ded.misle.renderer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.GameRenderer.*;
import static com.ded.misle.input_handler.KeyHandler.updateDesignerSpeed;
import static com.ded.misle.Launcher.*;
import static com.ded.misle.renderer.InventoryRenderer.updateRendererVariableScales;
import static com.ded.misle.renderer.MenuButton.clearButtons;
import static com.ded.misle.input_handler.MouseHandler.updateMouseVariableScales;
import static com.ded.misle.SaveFile.saveEverything;
import static com.ded.misle.renderer.PlayingRenderer.updatePlayingVariableScales;

/**
 * This is for loading and altering how the window behaves. Only do this once, otherwise new screens are created.
 */
public class GamePanel extends JPanel implements Runnable {

	private final JFrame window;
	private final int targetFPS = frameRateCap;
	private static volatile boolean running = true;
	private JLabel fpsLabel;
	KeyHandler keyH = new KeyHandler();
	public MouseHandler mouseHandler;
	Thread gameThread;

	// TILES SIZE

	static final int originalTileSize = 64; // 64x64 tiles
	public static double gameScale = scale;
	public static int tileSize = (int) (originalTileSize * gameScale) / 3;
	static final double maxScreenCol = 24; // Horizontal
	static final double maxScreenRow = 13.5; // Vertical
	public static double screenWidth = maxScreenCol * tileSize;
	public static double screenHeight = maxScreenRow * tileSize;

	public static void updateTileSize() {
		tileSize = (int) (originalTileSize * gameScale) / 3;
	}

	// INITIALIZING PLAYER

	public static Player player;
	static {
		player = new Player();
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
		LEVEL_DESIGNER,
		FROZEN_PLAYING
	}

	public static GameState gameState = GameState.MAIN_MENU; // Start in MAIN_MENU by default

	// CAMERA WORLD BOUNDARIES

	static double originalWorldWidth = 1;
	static double originalWorldHeight = 1;
	static double worldWidth = originalWorldWidth * scale;
	static double worldHeight = originalWorldHeight * scale;

	public static void setWorldBorders(int width, int height) {
		originalWorldWidth = width;
		originalWorldHeight = height;
		worldWidth = originalWorldWidth * scale;
		worldHeight = originalWorldHeight * scale;
	}

	public GamePanel() {
		// Setting up the JFrame
		window = new JFrame();
		window.setTitle(windowTitle);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize((int) screenWidth, (int) screenHeight);
		window.setLocationRelativeTo(null);

		window.add(this);
		this.setLayout(null);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);

		this.setBackground(windowBackground);

		mouseHandler = new MouseHandler();
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);

		Thread windowSizeThread = new Thread(this::changeAndDetectWindowSize);
		windowSizeThread.start();

		updateMouseVariableScales();

		if (displayFPS) {
			fpsLabel = new JLabel("FPS: 0");
			fpsLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Set the font and size
			fpsLabel.setForeground(Color.WHITE); // Set text color
			this.add(fpsLabel);  // Add FPS label to the panel
			window.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					updateLabelPositionAndSize();
				}
			});
		}

		window.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if ((window.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
					screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
					screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
				} else {
					screenWidth = window.getWidth();
					screenHeight = window.getHeight();
				}
				scale = screenWidth / 512.0;

			
				repaint();
			}
		});

		// Fullscreen Mode Logic
		if (isFullscreen) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			window.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
			scale = (screenSize.getWidth() * 3) / (originalTileSize * maxScreenCol);
			screenWidth = (int) screenSize.getWidth();
			screenHeight = (int) screenSize.getHeight();
			updateTileSize();

			if (!fullscreenMode.equals("exclusive")) {
				window.setExtendedState(JFrame.MAXIMIZED_BOTH);
				window.setUndecorated(true);
			}
			else {
				GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				if (gd.isFullScreenSupported()) {
					window.setUndecorated(true);
					gd.setFullScreenWindow(window);
				} else {
					System.out.println("Exclusive fullscreen is not supported on this device.");
				}
			}
		}

		// Handle window close event
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quitGame();
			}
		});
	}

	public static void quitGame() {
		saveEverything();
		running = false;
		Timer timer = new Timer(10, e -> {
			System.exit(0);
		});
		timer.setRepeats(false);
		timer.start();
	}

	private void changeAndDetectWindowSize() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int screenWidth = (int) toolkit.getScreenSize().getWidth();
		int screenHeight = (int) toolkit.getScreenSize().getHeight();

		double previousWidth = window.getWidth();
		double previousHeight = window.getHeight();
		double resizeThreshold = 5.0; // Allowable pixel difference to ignore small changes

		while (running) {
			int detectedWidth = window.getWidth();
			int detectedHeight = window.getHeight();

			// Check if the size change is significant
			if (Math.abs(detectedWidth - previousWidth) > resizeThreshold ||
				Math.abs(detectedHeight - previousHeight) > resizeThreshold) {

				detectedWidth = Math.min(detectedWidth, screenWidth);
				detectedHeight = Math.min(detectedHeight, screenHeight);

				boolean widthAligned;
				// Calculate the new dimensions to maintain the 16:9 aspect ratio
				if (detectedWidth > ((double) (detectedHeight * 512) / 288) + resizeThreshold) {
					detectedWidth = (int) ((double) detectedHeight * 512 / 288);
					widthAligned = false;
				} else {
					detectedHeight = (int) ((double) detectedWidth * 288 / 512);
					widthAligned = true;
				}

				// Update the window size
				window.setSize(detectedWidth, detectedHeight);

				// Scale based on the new dimensions
				double previousScale = scale;
				if (widthAligned) {
					scale = (double) detectedWidth / 512;
				} else {
					scale = (double) detectedHeight / 288;
				}
				gameScale = scale;
				GamePanel.screenWidth = Math.min(detectedWidth, screenWidth);
				GamePanel.screenHeight = Math.min(detectedHeight, screenHeight);

				updateTileSize();
				player.attr.updateStat(PlayerAttributes.Stat.SPEED);
				player.setBoxScaleHorizontal(tileSize * 0.91);
				player.setBoxScaleVertical(tileSize * 0.91);
				worldWidth = originalWorldWidth * scale;
				worldHeight = originalWorldHeight * scale;

				player.setX(player.getX() * scale / previousScale);
				player.setY(player.getY() * scale / previousScale);

				clearButtons();
				FontManager.updateFontSizes();
				updateMouseVariableScales();
				updateRendererVariableScales();
				updatePlayingVariableScales();
				if (levelDesigner) {
					updateDesignerSpeed();
				}

				previousWidth = detectedWidth;
				previousHeight = detectedHeight;

				PlayingRenderer.updateSelectedItemNamePosition();
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}



	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	public void showScreen() {
		window.setVisible(true);
	}

	private void updateLabelPositionAndSize() {
//		int width = getToolkit().getScreenSize().width;
//		int height = getToolkit().getScreenSize().height;
//
//		int labelWidth = (int) (width / (10 - 2 * Math.log10(targetFPS)));
//		int labelHeight = (int) (height / (15 * scale));
//
//		int labelX = (int) (width - 58 - 18 * Math.floor(Math.log10(targetFPS)));
//		int labelY = 5;
//
//		fpsLabel.setBounds(labelX, labelY, labelWidth, labelHeight);
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime(); // Using nanoTime for precision with delta time
		double delta = 0;
		double nsPerFrame = 1000000000.0 / targetFPS;
		long frameCount = 0;
		long lastFPSUpdate = System.currentTimeMillis();

		// GAME LOOP

		while (gameThread != null && running) {
			long currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / nsPerFrame;
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
						player.pos.setCameraOffsetX(player.getX() - screenWidth / 2 + player.getBoxScaleHorizontal() / 2);
						player.pos.setCameraOffsetY(player.getY() - screenHeight / 2 + player.getBoxScaleVertical() / 2);

						player.pos.setCameraOffsetX(Math.max(0, Math.min(player.pos.getCameraOffsetX(), worldWidth - screenWidth)));
						player.pos.setCameraOffsetY(Math.max(0, Math.min(player.pos.getCameraOffsetY(), worldHeight - screenHeight)));

						keyH.updateKeys(mouseHandler);  // Check for player input and update position accordingly
						mouseHandler.updateMouse();
					}
					case null, default -> {
						keyH.updateKeys(mouseHandler);
					}
				}
				delta--;
			}

			renderFrame(); // Render the appropriate frame based on gameState
			frameCount++;

			// FPS LABEL DISABLED FOR NOW

			// Update FPS label every second
//			long currentMillis = System.currentTimeMillis();
//			if (currentMillis - lastFPSUpdate >= 1000) {
//				if (displayFPS) {
//					fpsLabel.setText("FPS: " + frameCount);
//					System.out.println(frameCount);
//				}
//				frameCount = 0;
//				lastFPSUpdate = currentMillis;
//			}

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

		player.attr.updateRegenerationHP(currentTime);
		keyH.updateKeys(mouseHandler);  // Check for player input and update position accordingly
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
				MenuRenderer.renderOptionsMenu(g, this);
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
		}
	}

	public void renderFrame() {
		repaint();
	}
}
