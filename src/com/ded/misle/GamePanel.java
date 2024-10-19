package com.ded.misle;

import com.ded.misle.boxes.Box;
import com.ded.misle.boxes.BoxesHandling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

import static com.ded.misle.Launcher.*;

/**
 * This is for loading and altering how the window behaves. Only do this once, otherwise new screens are created.
 */
public class GamePanel extends JPanel implements Runnable {

	private final JFrame window;
	private static final int FPS_UPDATE_INTERVAL = 1000;
	private final int targetFPS = frameRateCap;
	private volatile boolean running = true;
	private JLabel fpsLabel;
	KeyHandler keyH = new KeyHandler();
	Thread gameThread;

	// TILES SIZE

	static final int originalTileSize = 64; // 64x64 tiles
	static int tileSize = (int) (originalTileSize * scale) / 3;
	final double maxScreenCol = 24; // Horizontal
	final double maxScreenRow = 13.5; // Vertical
	double width = maxScreenCol * tileSize;
	double height = maxScreenRow * tileSize;

	// PLAYER'S DEFAULT POSITION

	double playerX = (1.5 * width);
	double playerY = (1.5 * height);
	double originalPlayerX = playerX / scale;
	double originalPlayerY = playerY / scale;
	double playerSpeed = (scale * 2 + 0.166) / 3;
	int playerWidth = tileSize;
	int playerHeight = tileSize;

	// PLAYER CAMERA

	double cameraOffsetX = 0;
	double cameraOffsetY = 0;

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

		int maxX = pixelToCoordinate(1000 * scale) / 3;
		int maxY = pixelToCoordinate(1000 * scale) / 3;
		int x = 0;
		int y = 0;
		while (x < maxX) {
			while (y < maxY) {
				final double boxXCoordinate = (x * 4) + 2;
				final double boxYCoordinate = (y * 4) + 2;
				int boxX = (int) (coordinateToPixel((int) boxXCoordinate) - cameraOffsetX);
				int boxY = (int) (coordinateToPixel((int) boxYCoordinate) - cameraOffsetY);
				int colorRed = Math.min((60), 255);
				int colorGreen = Math.min((170), 255); // GREEN SQUARES, COLLISION DISABLED
				int colorBlue = Math.min((60), 255);
				BoxesHandling.addBox(boxX, boxY, new Color(colorRed, colorGreen, colorBlue), false, 0.5, 0.5);
				y++;
			}
			y = 0;
			x++;
		}
		x = 0;
		y = 0;
		while (x < maxX) {
			while (y < maxY) {
				final double boxXCoordinate = x * 4;
				final double boxYCoordinate = y * 4;
				int boxX = (int) (coordinateToPixel((int) boxXCoordinate) - cameraOffsetX);
				int boxY = (int) (coordinateToPixel((int) boxYCoordinate) - cameraOffsetY);
				int colorRed = Math.min((190), 255); // RED SQUARES, COLLISION ENABLED
				int colorGreen = Math.min((60), 255);
				int colorBlue = Math.min((60), 255);
				BoxesHandling.addBox(boxX, boxY, new Color(colorRed, colorGreen, colorBlue), true, 0.25, 2);
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
				playerSpeed = (scale * 2 + 0.166) / 3;
				playerWidth = tileSize;
				playerHeight = tileSize;

				playerX = originalPlayerX * scale;
				playerY = originalPlayerY * scale;

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
		long lastTime = System.currentTimeMillis();
		long frameCount = 0;
		long lastFPSUpdate = lastTime;

		while (gameThread != null && running) {
			long currentTime = System.currentTimeMillis();
			long elapsedTime = currentTime - lastTime;

			updateGame();
			repaint();
			renderFrame();

			frameCount++;

			// Update FPS label every second
			if (currentTime - lastFPSUpdate >= FPS_UPDATE_INTERVAL) {
				if (displayFPS) {
					fpsLabel.setText("FPS: " + frameCount);
					System.out.println(frameCount);
				}
				frameCount = 0;
				lastFPSUpdate = currentTime;
			}

			// Sleep to maintain the target FPS
			long sleepTime = 1000 / Math.max(1, Math.min(targetFPS, 144));
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public void updateGame() {
		// Update the camera offset to center the player in the view
		cameraOffsetX = playerX - width / 2 + (double) playerWidth / 2;
		cameraOffsetY = playerY - height / 2 + (double) playerHeight / 2;

		// Ensure camera doesn't show out-of-bounds areas (world boundaries)
		double worldWidth = 1000 * scale;
		double worldHeight = 1000 * scale;

		cameraOffsetX = Math.max(0, Math.min(cameraOffsetX, worldWidth - width));
		cameraOffsetY = Math.max(0, Math.min(cameraOffsetY, worldHeight - height));

		updateKeys();  // Check for player input and update position accordingly
	}



	private void updateKeys() {
		double[] willMovePlayer = {0, 0};
		if (keyH.upPressed) {
			if (!keyH.leftPressed || !keyH.rightPressed) {
				willMovePlayer[1] -= playerSpeed;
			} else {
				willMovePlayer[1] -= (playerSpeed * Math.sqrt(2) / 3);
			}
		}
		if (keyH.downPressed) {
			if (!keyH.leftPressed || !keyH.rightPressed) {
				willMovePlayer[1] += playerSpeed;
			} else {
				willMovePlayer[1] += playerSpeed * Math.sqrt(2) / 3;
			}
		}
		if (keyH.leftPressed) {
			if (!keyH.upPressed || !keyH.downPressed) {
				willMovePlayer[0] -= playerSpeed;
			} else {
				willMovePlayer[0] -= playerSpeed * Math.sqrt(2) / 3;
			}
		}
		if (keyH.rightPressed) {
			willMovePlayer[0] += playerSpeed;
		}
		double range = 200 * scale;
		if (willMovePlayer[0] != 0 || willMovePlayer[1] != 0) {
			if (!isPixelOccupied((playerX + willMovePlayer[0]), playerY, playerWidth, playerHeight, range)) {
				movePlayer(willMovePlayer[0], 0);
			}
			if (!isPixelOccupied(playerX, (playerY + willMovePlayer[1]), playerWidth, playerHeight, range)) {
				movePlayer(0, willMovePlayer[1]);
			}
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
		playerX += x;
		playerY += y;
		originalPlayerX = playerX / scale;
		originalPlayerY = playerY / scale;
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
		List<Box> nearbyCollisionBoxes = BoxesHandling.getCollisionBoxesInRange(playerX, playerY, range, scale, tileSize);
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
		int playerScreenX = (int) (playerX - cameraOffsetX);
		int playerScreenY = (int) (playerY - cameraOffsetY);

		// Draw the player
		g2d.setColor(Color.WHITE); // For now, a rectangle
		g2d.fillRect(playerScreenX, playerScreenY, playerWidth, playerHeight);

		// Draw other game elements, using the camera offset as well
		BoxesHandling.renderBoxes(g2d, cameraOffsetX, cameraOffsetY, scale, tileSize);
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
