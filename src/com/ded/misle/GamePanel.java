package com.ded.misle;

import com.ded.misle.boxes.Box;
import com.ded.misle.boxes.BoxesHandling;
import com.ded.misle.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static com.ded.misle.GameRenderer.*;
import static com.ded.misle.Launcher.*;
import static com.ded.misle.SaveFile.saveEverything;

/**
 * This is for loading and altering how the window behaves. Only do this once, otherwise new screens are created.
 */
public class GamePanel extends JPanel implements Runnable {

	private final JFrame window;
	private final int targetFPS = frameRateCap;
	private static volatile boolean running = true;
	private JLabel fpsLabel;
	KeyHandler keyH = new KeyHandler();
	Thread gameThread;

	// TILES SIZE

	static final int originalTileSize = 64; // 64x64 tiles
	public static int tileSize = (int) (originalTileSize * scale) / 3;
	final double maxScreenCol = 24; // Horizontal
	final double maxScreenRow = 13.5; // Vertical
	double width = maxScreenCol * tileSize;
	double height = maxScreenRow * tileSize;

	// INITIALIZING PLAYER

	public static Player player;
	static {
		player = new Player();
	}

	// GAMESTATE

	public enum GameState {
		PLAYING,
		DIALOG,
		INVENTORY,
		MAIN_MENU,
		OPTIONS_MENU,
		PAUSE_MENU,
		LOADING_MENU,
	}

	public static GameState gameState = GameState.MAIN_MENU; // Start in PLAYING by default


	// CAMERA WORLD BOUNDARIES

	double originalWorldWidth = 1000;
	double originalWorldHeight = 1000;
	double worldWidth = originalWorldWidth * scale;
	double worldHeight = originalWorldHeight * scale;

	public GamePanel() {
		// Setting up the JFrame
		window = new JFrame();
		window.setTitle(windowTitle);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize((int) width, (int) height);
		window.setLocationRelativeTo(null);

		window.add(this);
		this.setLayout(null);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);

		this.setBackground(Color.BLACK);

		KeyHandler.initializeKeyHandler();

		Thread windowSizeThread = new Thread(this::changeAndDetectWindowSize);
		windowSizeThread.start();

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
					width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
					height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
				} else {
					width = window.getWidth();
					height = window.getHeight();
				}
				scale = width / 512.0;

			
				repaint();
			}
		});

		// Fullscreen Mode Logic
		if (isFullscreen) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			window.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
			scale = (screenSize.getWidth() * 3) / (originalTileSize * maxScreenCol);
			width = (int) screenSize.getWidth();
			height = (int) screenSize.getHeight();
			tileSize = (int) (originalTileSize * scale)/3;

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
		System.exit(0);
	}

	private void changeAndDetectWindowSize() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int screenWidth = (int) toolkit.getScreenSize().getWidth();
		int screenHeight = (int) toolkit.getScreenSize().getHeight();

		double previousWidth = window.getWidth();
		double previousHeight = window.getHeight();
		while (running) {

			int detectedWidth = window.getWidth();
			int detectedHeight = window.getHeight();

			if (detectedWidth != previousWidth || detectedHeight != previousHeight) {
				detectedWidth = Math.min(detectedWidth, screenWidth);
				detectedHeight = Math.min(detectedHeight, screenHeight);

				// Calculate the new dimensions to maintain the 16:9 aspect ratio
				if (detectedWidth > detectedHeight * 16 / 9) {
					detectedWidth = detectedHeight * 16 / 9;
				} else {
					detectedHeight = detectedWidth * 9 / 16;
				}

				// Update the window size
				window.setSize(detectedWidth, detectedHeight);

				// Scale based on the new dimensions
				scale = (double) detectedWidth / 512;
				width = Math.min(detectedWidth, screenWidth);
				height = Math.min(detectedHeight, screenHeight);

				tileSize = (int) (originalTileSize * scale) / 3;
				player.attr.setPlayerSpeedModifier(player.attr.getPlayerSpeedModifier());
				player.attr.setPlayerWidth(tileSize);
				player.attr.setPlayerHeight(tileSize);
				worldWidth = originalWorldWidth * scale;
				worldHeight = originalWorldHeight * scale;

				player.pos.setX(player.pos.getOriginalPlayerX() * scale);
				player.pos.setY(player.pos.getOriginalPlayerY() * scale);

				GameRenderer.clearClickables();

				previousWidth = detectedWidth;
				previousHeight = detectedHeight;
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
				if (gameState == GameState.PLAYING || gameState == GameState.INVENTORY) {
					updateGame(); // Only update if in the playing state
				} else if (gameState == GameState.PAUSE_MENU) {
					if (player.keys.keyPressed.get("pause")) {
						softGameStart();
						player.keys.keyPressed.put("pause", false);
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
		player.pos.setCameraOffsetX(player.pos.getX() - width / 2 + player.attr.getPlayerWidth() / 2);
		player.pos.setCameraOffsetY(player.pos.getY() - height / 2 + player.attr.getPlayerHeight() / 2);

		player.pos.setCameraOffsetX(Math.max(0, Math.min(player.pos.getCameraOffsetX(), worldWidth - width)));
		player.pos.setCameraOffsetY(Math.max(0, Math.min(player.pos.getCameraOffsetY(), worldHeight - height)));

		player.attr.checkIfLevelUp();

		player.attr.updateRegenerationHP(currentTime);
		keyH.updateKeys();  // Check for player input and update position accordingly
		}





	/**
	 * This moves the player by x, oftentimes being the playerSpeed, or by y.
	 * Set the other as 0, unless you intend to move the player diagonally.
	 * <p></p>
	 * Example use:
	 * movePlayer(playerSpeed, 0);
	 *
	 * @param x double - How many pixels in x direction (this is not based on scale).
	 * @param y double - How many pixels in y direction (this is not based on scale).
	 */
	public static void movePlayer(double x, double y) {
		player.pos.setX(player.pos.getX() + x);
		player.pos.setY(player.pos.getY() + y);
		player.stats.increaseDistance(x, y);

		if (player.attr.getLastVelocityBox() != null) {
			player.attr.setPlayerEnvironmentSpeedModifier(1.0); // Reset to default speed
			player.attr.setLastVelocityBox(null); // Clear the last velocity box
		}

		List<Box> nearbyNonCollisionBoxes = ((BoxesHandling.getNonCollisionBoxesInRange(player.pos.getX(), player.pos.getY(), tileSize, scale, tileSize)));
		for (Box box: nearbyNonCollisionBoxes) {
			if (!box.getEffect().isEmpty()) {
				Box.handleEffect(box);
			}
		}
	}
	
	/**
	 * This takes the top-left corner of an object as pixels and the object width and height and returns
	 * either true or false based if there's a box with collision on in the pixel detected.
	 * <p></p>
	 * Example use:
	 * (!isPixelOccupied((playerX + 45), playerY, playerWidth, playerHeight) will check if there's
	 * something blocking the player at 45 pixels in the X axis from where the player is, based on
	 * the player entire hitbox, not just from the top-left corner.
	 *
	 * @param pixelX double - The X location in pixels of the object.
	 * @param pixelY double - The Y location in pixels of the object.
	 * @param objectWidth double - The width of the object, in pixels.
	 * @param objectHeight double - The height of the object, in pixels.
 	 */
	public static boolean isPixelOccupied(double pixelX, double pixelY, double objectWidth, double objectHeight, double range) {
		List<Box> nearbyCollisionBoxes = BoxesHandling.getCollisionBoxesInRange(player.pos.getX(), player.pos.getY(), range, scale, tileSize);
		for (Box box : nearbyCollisionBoxes) {
			if (box.getBoxScaleHorizontal() >= 1 && box.getBoxScaleVertical() >= 1) {
				if (box.isPointColliding(pixelX, pixelY, scale, objectWidth, objectHeight) || // Up-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY, scale, objectWidth, objectHeight)) || // Up-right corner
					(box.isPointColliding(pixelX, pixelY + objectHeight, scale, objectWidth, objectHeight)) || // Bottom-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY + objectHeight, scale, objectWidth, objectHeight)) // Bottom-right corner
				) {
					if (!box.getEffect().isEmpty()) {
						Box.handleEffect(box);
					}
					return true;
				}
			} else {
				int inverseBoxScale = (int) (1 / Math.min(box.getBoxScaleHorizontal(), box.getBoxScaleVertical())) + 1;
				for (int i = 0; i <= inverseBoxScale; i++) {
					if ((box.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY, scale, objectWidth, objectHeight)) || // Top edge
						(box.isPointColliding(pixelX, pixelY + i * objectHeight / inverseBoxScale, scale, objectWidth, objectHeight)) || // Left edge
						(box.isPointColliding(pixelX + objectWidth, pixelY + i * objectHeight / inverseBoxScale, scale, objectWidth, objectHeight)) || // Right edge
						(box.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY + objectHeight, scale, objectWidth, objectHeight)) // Bottom edge
					) {
						if (!box.getEffect().isEmpty()) {
							Box.handleEffect(box);
						}
						return true;
					}
				}
			}
        }
    return false;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		switch (gameState) {
			case GameState.INVENTORY:
			case GameState.PLAYING:
				renderPlayingGame(g, getWidth(), getHeight(), this);
				break;
			case GameState.MAIN_MENU:
				renderMainMenu(g, getWidth(), getHeight(), this);
				break;
			case GameState.OPTIONS_MENU:
				renderOptionsMenu(g, getWidth(), getHeight(), this);
				break;
			case GameState.PAUSE_MENU:
				renderPauseMenu(g, getWidth(), getHeight(), this);
				break;
			case GameState.LOADING_MENU:
				renderLoadingMenu(g, getWidth(), getHeight(), this);
				break;
		}
	}

	public static double coordinateToPixel(int coordinate) {
		return coordinate * tileSize;
	}

	public static int pixelToCoordinate(double pixel) {
		return (int) (pixel / tileSize);
	}

	public void renderFrame() {
		repaint();
	}

}
