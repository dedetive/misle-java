package com.ded.misle;

import com.ded.misle.boxes.Box;
import com.ded.misle.boxes.BoxesHandling;
import com.ded.misle.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static com.ded.misle.Launcher.*;

/**
 * This is for loading and altering how the window behaves. Only do this once, otherwise new screens are created.
 */
public class GamePanel extends JPanel implements Runnable {

	private final JFrame window;
	private final int targetFPS = frameRateCap;
	private volatile boolean running = true;
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

		int interval = 4;
		int maxX = (int) (originalWorldWidth / (31.25 * interval));
		int maxY = (int) (originalWorldHeight / (31.25 * interval));
		int x = 0;
		int y = 0;
		while (x < maxX) {
			while (y < maxY) {
				final double boxXCoordinate = (x * interval) + (double) interval / 2;
				final double boxYCoordinate = (y * interval) + (double) interval / 2;
				int boxX = (int) (coordinateToPixel((int) boxXCoordinate) - player.getCameraOffsetX());
				int boxY = (int) (coordinateToPixel((int) boxYCoordinate) - player.getCameraOffsetY());
				int colorRed = Math.min((60), 255);
				int colorGreen = Math.min((170), 255); // GREEN SQUARES, COLLISION DISABLED
				int colorBlue = Math.min((60), 255);
				BoxesHandling.addBox(boxX, boxY, new Color(colorRed, colorGreen, colorBlue), false, 1, 1);
				y++;
			}
			y = 0;
			x++;
		}
		x = 0;
		y = 0;
		while (x < maxX) {
			while (y < maxY) {
				final double boxXCoordinate = x * interval;
				final double boxYCoordinate = y * interval;
				int boxX = (int) (coordinateToPixel((int) boxXCoordinate) - player.getCameraOffsetX());
				int boxY = (int) (coordinateToPixel((int) boxYCoordinate) - player.getCameraOffsetY());
				int colorRed = Math.min((190), 255); // RED SQUARES, COLLISION ENABLED
				int colorGreen = Math.min((60), 255);
				int colorBlue = Math.min((60), 255);
				BoxesHandling.addBox(boxX, boxY, new Color(colorRed, colorGreen, colorBlue), true, 1, 1);
				y++;
			}
			y = 0;
			x++;
		}

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
				running = false;
				System.exit(0);
			}
		});
	}

	public void changeAndDetectWindowSize() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int screenWidth = (int) toolkit.getScreenSize().getWidth();
		int screenHeight = (int) toolkit.getScreenSize().getHeight();

		double previousWidth = window.getWidth();
		double previousHeight = window.getHeight();

		while (running) {
			int detectedWidth = window.getWidth();
			int detectedHeight = window.getHeight();

			if (detectedWidth != previousWidth) {
				detectedWidth = Math.min(detectedWidth, screenWidth);
				detectedHeight = Math.min(detectedHeight, screenHeight);
				if (Math.abs(detectedWidth - (detectedHeight * 16) / 9) > 9) {
					window.setSize((int) width, (int) height);
					if (detectedWidth > detectedHeight) {
						width = detectedWidth;
						height = (double) (detectedWidth * 9) / 16;
					} else {
						height = detectedHeight;
						width = (double) (detectedHeight * 16) / 9;
					}
				}

				scale = (double) detectedWidth / 512;
				width = Math.min(width, screenWidth);
				height = Math.min(height, screenHeight);

				tileSize = (int) (originalTileSize * scale) / 3;
				player.setPlayerSpeedModifier(player.getPlayerSpeedModifier());
				player.setPlayerWidth(tileSize);
				player.setPlayerHeight(tileSize);
				worldWidth = originalWorldWidth * scale;
				worldHeight = originalWorldHeight * scale;

				player.setX(player.getOriginalPlayerX() * scale);
				player.setY(player.getOriginalPlayerY() * scale);

				previousWidth = detectedWidth;
				previousHeight = detectedHeight;

				// System.out.println("x: " + playerX + ", y: " + playerY + ", s: " + scale);
				// System.out.println("x: " + playerX / scale + ", y: " + playerY / scale);
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

		while (gameThread != null && running) {
			long currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / nsPerFrame;
			lastTime = currentTime;

			// Process updates and rendering while delta is >= 1
			while (delta >= 1) {
				updateGame();
				delta--;
			}

			repaint(); // Render the game (renderFrame if necessary)
			renderFrame();

			frameCount++;

			// Update FPS label every second
			long currentMillis = System.currentTimeMillis();
			if (currentMillis - lastFPSUpdate >= 1000) {
				if (displayFPS) {
					fpsLabel.setText("FPS: " + frameCount);
					System.out.println(frameCount);
				}
				frameCount = 0;
				lastFPSUpdate = currentMillis;
			}

			// Sleep dynamically to maintain target FPS
			try {
				Thread.sleep(Math.max(0, (long) ((nsPerFrame - (System.nanoTime() - lastTime)) / 1000000))); // Sleep in milliseconds
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}


	public void updateGame() {
		// Update the camera offset to center the player in the view
		player.setCameraOffsetX(player.getX() - width / 2 + player.getPlayerWidth() / 2);
		player.setCameraOffsetY(player.getY() - height / 2 + player.getPlayerHeight() / 2);

		player.setCameraOffsetX(Math.max(0, Math.min(player.getCameraOffsetX(), worldWidth - width)));
		player.setCameraOffsetY(Math.max(0, Math.min(player.getCameraOffsetY(), worldHeight - height)));

		updateKeys();  // Check for player input and update position accordingly
		}



	private void updateKeys() {
		double[] willMovePlayer = {0, 0};
		if (player.keyPressed.get("up")) {
			if (!player.keyPressed.get("left") || !player.keyPressed.get("right")) {
				willMovePlayer[1] -= player.getPlayerSpeed();
			} else {
				willMovePlayer[1] -= (player.getPlayerSpeed() * Math.sqrt(2) / 3);
			}
		}
		if (player.keyPressed.get("down")) {
			if (!player.keyPressed.get("left") || !player.keyPressed.get("right")) {
				willMovePlayer[1] += player.getPlayerSpeed();
			} else {
				willMovePlayer[1] += player.getPlayerSpeed() * Math.sqrt(2) / 3;
			}
		}
		if (player.keyPressed.get("left")) {
			if (!player.keyPressed.get("up") || !player.keyPressed.get("down")) {
				willMovePlayer[0] -= player.getPlayerSpeed();
			} else {
				willMovePlayer[0] -= player.getPlayerSpeed() * Math.sqrt(2) / 3;
			}
		}
		if (player.keyPressed.get("right")) {
			willMovePlayer[0] += player.getPlayerSpeed();
		}
		double range = (player.getPlayerSpeed() * 64) * scale;
		if (willMovePlayer[0] != 0 || willMovePlayer[1] != 0) {
			if (!isPixelOccupied((player.getX() + willMovePlayer[0]), player.getY(), player.getPlayerWidth(), player.getPlayerHeight(), range)) {
				movePlayer(willMovePlayer[0], 0);
			}
			if (!isPixelOccupied(player.getX(), (player.getY() + willMovePlayer[1]), player.getPlayerWidth(), player.getPlayerHeight(), range)) {
				movePlayer(0, willMovePlayer[1]);
			}
		}
		if (player.keyPressed.get("debug1")) {
			player.setPlayerHP(Math.max((player.getPlayerHP() - 1), 0));
			System.out.println("Reduced 1 HP, currently at: " + player.getPlayerHP());
			player.keyPressed.put("debug1", false);
		}
		if (player.keyPressed.get("debug2")) {
			player.setPlayerHP(Math.min((player.getPlayerHP() + 1), player.getPlayerMaxHP()));
			System.out.println("Regenerated 1 HP, currently at: " + player.getPlayerHP());
			player.keyPressed.put("debug2", false);
		}
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
	private void movePlayer(double x, double y) {
		player.setX(player.getX() + x);
		player.setY(player.getY() + y);
		player.setOriginalPlayerX(player.getX() / scale);
		player.setOriginalPlayerY(player.getY() / scale);
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
	private boolean isPixelOccupied(double pixelX, double pixelY, double objectWidth, double objectHeight, double range) {
		List<Box> nearbyCollisionBoxes = BoxesHandling.getCollisionBoxesInRange(player.getX(), player.getY(), range, scale, tileSize);
		for (Box box : nearbyCollisionBoxes) {
			if (box.getBoxScaleHorizontal() >= 1 && box.getBoxScaleVertical() >= 1) {
				if (box.isPointColliding(pixelX, pixelY, scale, objectWidth, objectHeight) || // Up-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY, scale, objectWidth, objectHeight)) || // Up-right corner
					(box.isPointColliding(pixelX, pixelY + objectHeight, scale, objectWidth, objectHeight)) || // Bottom-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY + objectHeight, scale, objectWidth, objectHeight)) // Bottom-right corner
				) {
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

		Graphics2D g2d = (Graphics2D) g;

		// Adjust the player's position based on the camera offsets
		int playerScreenX = (int) (player.getX() - player.getCameraOffsetX());
		int playerScreenY = (int) (player.getY() - player.getCameraOffsetY());

		// Draw the player
		g2d.setColor(Color.WHITE); // For now, a rectangle
		g2d.fillRect(playerScreenX, playerScreenY, (int) player.getPlayerWidth(), (int) player.getPlayerHeight());

		// Draw other game elements, using the camera offset as well
		BoxesHandling.renderBoxes(g2d, player.getCameraOffsetX(), player.getCameraOffsetY(), player.getX(), player.getY(), width, scale, tileSize);
		g2d.dispose();
	}

	public static double coordinateToPixel(int coordinate) {
		return coordinate * tileSize;
	}

	public static int pixelToCoordinate(double pixel) {
		return (int) (pixel / tileSize);
	}

	private void renderFrame() {
		// Insert render logic here
	}
}
