package com.ded.misle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
	static int tileSize = (int) (originalTileSize * scale)/3;
	final double maxScreenCol = 24; // Horizontal
	final double maxScreenRow = 13.5; // Vertical
	double width = maxScreenCol * tileSize;
	double height = maxScreenRow * tileSize;

	// PLAYER'S DEFAULT POSITION

	double playerX = (0.1 * width);
	double playerY = (0.1 * height);
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

				tileSize = (int) (originalTileSize * scale) / 3;
				playerSpeed = (scale * 2 + 0.166) / 3;

				playerX = Math.min(playerX, width - playerWidth);
				playerY = Math.min(playerY, height - playerHeight);

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

			detectedWidth = Math.min(detectedWidth, screenWidth);
			detectedHeight = Math.min(detectedHeight, screenHeight);

			double scaleX = (double) detectedWidth / previousWidth;
			double scaleY = (double) detectedHeight / previousHeight;

			playerX *= scaleX;
			playerY *= scaleY;

			previousWidth = detectedWidth;
			previousHeight = detectedHeight;

			scale = (double) detectedWidth / 512;
			width = Math.min(width, screenWidth);
			height = Math.min(height, screenHeight);

			tileSize = (int) (originalTileSize * scale) / 3;
			playerSpeed = (scale * 2 + 0.166) / 3;

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

			try {
				Thread.sleep(333);
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


	private void updateGame() {
		// Calculate the camera offset to keep the player centered
		cameraOffsetX = playerX - width / 2 + (double) playerWidth / 2;
		cameraOffsetY = playerY - height / 2 + (double) playerHeight / 2;

		// Calculate world borders
		double worldWidth = 1000 * scale;
		double worldHeight = 1000 * scale;

		// Ensure camera doesn't show out-of-bounds areas (world boundaries)
		cameraOffsetX = Math.max(0, Math.min(cameraOffsetX, worldWidth - width));
		cameraOffsetY = Math.max(0, Math.min(cameraOffsetY, worldHeight - height));

		updateKeys();
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
		if (willMovePlayer[0] != 0 || willMovePlayer[1] != 0) {
			if (!isCoordinateOccupied(pixelToCoordinate(playerX + willMovePlayer[0]), pixelToCoordinate(playerY + willMovePlayer[1]))) {
				movePlayer(willMovePlayer[0], willMovePlayer[1]);
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
}

private boolean isCoordinateOccupied(double coordinateX, double coordinateY) {
	return false;
}

@Override
protected void paintComponent(Graphics g) {
	super.paintComponent(g);

	Graphics2D g2d = (Graphics2D) g;

	// Adjust the player's position based on the camera offsets
	int playerScreenX = (int) (playerX - cameraOffsetX);
	int playerScreenY = (int) (playerY - cameraOffsetY);

	// Draw the player
	g2d.setColor(Color.WHITE); // For now, a rectangle
	g2d.fillRect(playerScreenX, playerScreenY, tileSize, tileSize);

	// Draw other game elements, using the camera offset as well
	int maxX = pixelToCoordinate(1000 * scale) / 3;
	int x = 0;
	int maxY = pixelToCoordinate(1000 * scale) / 3;
	int y = 0;
	while (x < maxX) {
		while (y < maxY) {
			g2d.setColor(new Color(210, 165, 40));
			final double boxXCoordinate = x * 3;
			final double boxYCoordinate = y * 3;
			int boxX = (int) (coordinateToPixel((int) boxXCoordinate) - cameraOffsetX);
			int boxY = (int) (coordinateToPixel((int) boxYCoordinate) - cameraOffsetY);
			g2d.fillRect(boxX, boxY, tileSize, tileSize);

			y++;
		}
		y = 0;
		x++;
	}

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