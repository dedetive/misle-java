package com.ded.misle.game;

import com.ded.misle.input.*;
import com.ded.misle.renderer.menu.core.MenuManager;
import com.ded.misle.world.data.items.ItemLoader;
import com.ded.misle.world.entities.player.Player;
import com.ded.misle.renderer.*;
import com.ded.misle.world.logic.TurnManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.ded.misle.core.Setting.*;
import static com.ded.misle.renderer.FontManager.buttonFont;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.Launcher.*;
import static com.ded.misle.renderer.MenuButton.clearButtons;
import static com.ded.misle.input.MouseHandler.updateMouseVariableScales;
import static com.ded.misle.core.SaveFile.saveEverything;

/**
 * This is for loading and altering how the window behaves. Only do this once, otherwise new screens are created.
 */
public class GamePanel extends JPanel implements Runnable {

	private final JFrame window;
	private static volatile boolean running = true;
	public static InputHandler inputHandler;
	public static MouseHandler mouseHandler;
	Thread gameThread;

	// TILES SIZE

	private static double windowScale;

	public static double getWindowScale() {
		return windowScale;
	}

	public static final int originalTileSize = 40; // 40x40 tiles
	public static double gameScale = getWindowScale();
	public static int tileSize = updateTileSize();
	static final double maxScreenCol = 24; // Horizontal
	static final double maxScreenRow = 13.5; // Vertical
	public static double screenWidth = maxScreenCol * tileSize;
	public static double screenHeight = maxScreenRow * tileSize;
	public static int originalScreenWidth = 512;
	public static int originalScreenHeight = 288;

	public static int updateTileSize() {
		tileSize = (int) (originalTileSize * gameScale) / 2;
		return tileSize;
	}

	// INITIALIZING PLAYER

	public static Player player;
	static {
		player = new Player();
		inputHandler = new InputHandler();
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
		FROZEN_PLAYING,
	}

	public enum PlayingGameState {
		DIALOG,
		PLAYING,
		INVENTORY,
		PAUSE_MENU,
		FROZEN_PLAYING,
	}

	public static GameState gameState = GameState.MAIN_MENU; // Start in MAIN_MENU by default

	// CAMERA WORLD BOUNDARIES

	public static double originalWorldWidth = 1;
	public static double originalWorldHeight = 1;
	public static double worldWidth = originalWorldWidth;
	public static double worldHeight = originalWorldHeight;

	public static void setWorldBorders(int width, int height) {
		originalWorldWidth = width * originalTileSize;
		originalWorldHeight = height * originalTileSize;
		worldWidth = width * tileSize;
		worldHeight = height * tileSize;
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
		this.addKeyListener(inputHandler);
		this.setFocusable(true);

		this.setBackground(windowBackground);

		mouseHandler = new MouseHandler();
		addMouseListener(inputHandler);
		addMouseMotionListener(inputHandler);

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
		double previousScale = getWindowScale();
		ScreenSizeDimensions screen = ScreenSizeDimensions.valueOf(screenSize);
		int preferredX = screen.x;
		int preferredY = screen.y;
		windowScale = screen.scale;
		gameScale = getWindowScale();
		GamePanel.screenWidth = preferredX;
		GamePanel.screenHeight = preferredY;

		JFrame window = getWindow();

		// Handle fullscreen logic
		if (isFullscreen.bool()) {
			if (fullscreenMode.str().equals("windowed")) {
				// Set window to borderless and maximize
				window.dispose();
				window.setUndecorated(true);
				window.setResizable(false);
				window.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
				window.setVisible(true);
				windowScale = (double) window.getWidth() / 512;
				gameScale = getWindowScale();
				GamePanel.screenWidth = window.getWidth();
				GamePanel.screenHeight = window.getHeight();
			} else if (fullscreenMode.str().equals("exclusive")) {
				// Switch to exclusive fullscreen mode
				GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				if (device.isFullScreenSupported()) {
					window.dispose();
					window.setUndecorated(true);
					device.setFullScreenWindow(window);
					windowScale = (double) window.getWidth() / 512;
					gameScale = getWindowScale();
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
		player.setVisualScaleHorizontal(0.91);
		player.setVisualScaleVertical(0.91);
		worldWidth = originalWorldWidth * getWindowScale();
		worldHeight = originalWorldHeight * getWindowScale();

		clearButtons();
		FontManager.updateFontScript();
		updateMouseVariableScales();

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

	public static boolean isRunning() {
		return running;
	}

	public static double nsPerFrame;
	public static double deltaTime;
	public static final double FIXED_DELTA = 0.0064;

	public static int frameCount = 0;
	@Override
	public void run() {

		InitKeys.init();
		MenuManager.init();

		try {
			ItemLoader.loadItems();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		long lastTimer = System.currentTimeMillis();
		long lastTime = System.nanoTime();
		double delta = 0;
		int frames = 0;
		nsPerFrame = 1000000000.0 / Math.clamp(frameRateCap.integer(), 30, 160);

		screenBuffer = new BufferedImage((int) screenWidth, (int) screenHeight, BufferedImage.TYPE_INT_RGB);
		g2dBuffer = screenBuffer.createGraphics();

		// GAME LOOP

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerFrame;
			deltaTime = (now - lastTime) / 1e9;
			lastTime = now;

			// Process updates and rendering while delta is >= 1
			while (delta >= 1) {
				InputHandler.triggerAllHeld();

				switch (gameState) {
					case PLAYING, INVENTORY ->  {
						mouseHandler.updateMouse();
						TurnManager.updateIfNeeded();
						updateCamera();
					} // Only update if in the playing state
					case DIALOG -> mouseHandler.updateMouse();
					case null, default -> {
					}
				}
				delta--;
			}

			renderFrame();
			frames++;

			// Sleep dynamically to maintain target FPS
			long timeTaken = System.nanoTime() - now;
			long sleepTime = (long)(nsPerFrame - timeTaken) / 1_000_000;

			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			if (System.currentTimeMillis() - lastTimer >= 1000) {
				frameCount = frames;
				frames = 0;
				lastTimer += 1000;
			}
		}
	}

	public void updateCamera() {
		// Camera dead zone
		double deadZoneWidth = originalTileSize;
		double deadZoneHeight = originalTileSize * 9 / 16d;

		double cameraX = player.pos.getCameraOffsetX();
		double targetCameraX = player.pos.calculateCameraOffsetX();
		double dx = targetCameraX - cameraX;

		double cameraY = player.pos.getCameraOffsetY();
		double targetCameraY = player.pos.calculateCameraOffsetY();
		double dy = targetCameraY - cameraY;

		// Damping: 0 < alpha < 1, lower values = smoother
		float alpha = (float) (1 - Math.pow(0.16, deltaTime));

		if (targetCameraX == Integer.MIN_VALUE) {
			player.pos.setCameraOffsetX((float) Math.clamp(
					(player.pos.calculateCameraOffsetX()),
				0,
				originalWorldWidth
			));
			player.pos.invalidateCameraOffsetX();

			player.pos.setCameraOffsetY((float) Math.clamp(
				(player.pos.calculateCameraOffsetY()) - 32,
				0,
				originalWorldHeight - originalTileSize
			));
			player.pos.invalidateCameraOffsetY();
			return;
		}
		if (Math.abs(dx) > deadZoneWidth) {
			cameraX += 1.5 * dx * alpha;
			player.pos.setCameraOffsetX((float) cameraX);
			mouseHandler.updateCurrentMouseRotation();
		}
		if (Math.abs(dy) > deadZoneHeight) {
			cameraY += 1.5 * dy * alpha;
			player.pos.setCameraOffsetY((float) cameraY);
			mouseHandler.updateCurrentMouseRotation();
		}
	}

	private BufferedImage screenBuffer;
	private Graphics2D g2dBuffer;
	private static final float DEFAULT_BUFFER_SCALE = 1.5f;
	private static float maximumBufferScale;
	private static float bufferScale = DEFAULT_BUFFER_SCALE;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		if (screenBuffer == null || g2dBuffer == null) {
			screenBuffer = new BufferedImage((int) screenWidth, (int) screenHeight, BufferedImage.TYPE_INT_RGB);
			g2dBuffer = screenBuffer.createGraphics();
		}

		if (bufferScale != DEFAULT_BUFFER_SCALE) {
			if (g2dBuffer != null) g2dBuffer.dispose();
			screenBuffer.flush();
			screenBuffer = new BufferedImage((int) screenWidth, (int) screenHeight, BufferedImage.TYPE_INT_RGB);
			g2dBuffer = screenBuffer.createGraphics();
		}

		float staticBufferScale = bufferScale;

		g2dBuffer.setTransform(new AffineTransform());
		g2dBuffer.scale(getWindowScale() / staticBufferScale, getWindowScale() / staticBufferScale);

		g2dBuffer.setComposite(AlphaComposite.SrcOver);
		g2dBuffer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		if (antiAliasing.bool() && staticBufferScale == DEFAULT_BUFFER_SCALE) {
			g2dBuffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2dBuffer.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}

		MenuManager.draw(g2dBuffer);

//		switch (gameState) {
//			case GameState.INVENTORY:
//			case GameState.PLAYING:
//			case GameState.FROZEN_PLAYING:
//			case GameState.DIALOG:
//				new PlayingRenderer().render(g2dBuffer, mouseHandler);
//				break;
//			case GameState.MAIN_MENU:
//				MenuRenderer.renderMainMenu(g2dBuffer, this);
//				break;
//			case GameState.OPTIONS_MENU:
//				SettingsMenuRenderer.renderOptionsMenu(g2dBuffer, this);
//				break;
//			case GameState.PAUSE_MENU:
//				MenuRenderer.renderPauseMenu(g2dBuffer, this);
//				break;
//			case GameState.LOADING_MENU:
//				MenuRenderer.renderLoadingMenu(g2dBuffer, this);
//				break;
//			case GameState.SAVE_SELECTOR:
//				SaveSelector.renderSaveSelector(g2dBuffer, this);
//				break;
//			case SAVE_CREATOR:
//				SaveCreator.renderSaveCreator(g2dBuffer, this);
//				break;
//		}

		if (displayFPS.bool()) {
			g2dBuffer.setFont(buttonFont);
			String text = "FPS: " + frameCount;
			FontMetrics fm = g2dBuffer.getFontMetrics(buttonFont);
			int textWidth = fm.stringWidth(text);
			int textX = originalScreenWidth - textWidth - 8;
			int textY = fm.getHeight();
			g2dBuffer.setColor(FPSShadowColor);
			drawColoredText(g2dBuffer, text, textX + textShadow, textY + textShadow);
			g2dBuffer.setColor(FPSColor);
			drawColoredText(g2dBuffer, text, textX, textY);
		}

		g2d.scale(staticBufferScale, staticBufferScale);
		g2d.drawImage(screenBuffer, 0, 0, null);

		Toolkit.getDefaultToolkit().sync();

		g2d.dispose();
	}

	public void renderFrame() {
		repaint();
	}

	static Thread pixelating;
	final static float PIXELATION_STEP = 1f;
	final static float UNPIXELATION_STEP = 2.2f;
	public static void pixelate(long delay, float maximumBufferScale) {
		if (Objects.equals(pixelation.str(), "none")) return;
		if (Objects.equals(pixelation.str(), "low")) maximumBufferScale /= 2.05f;

		GamePanel.maximumBufferScale = maximumBufferScale;
		try {
			unpixelating.interrupt();
		} catch (NullPointerException ignored) {}

		float finalMaximumBufferScale = maximumBufferScale;

		pixelating = new Thread(() -> {
			while (bufferScale < finalMaximumBufferScale) {
				bufferScale = Math.min(bufferScale + PIXELATION_STEP, finalMaximumBufferScale);
				try {
					Thread.sleep((long) (delay * PIXELATION_STEP / (finalMaximumBufferScale - DEFAULT_BUFFER_SCALE)));
				} catch (InterruptedException e) {
					break;
				}
			}
		});

		pixelating.start();
	}

	static Thread unpixelating;
	public static void unpixelate(long delay) {
		try {
			pixelating.interrupt();
		} catch (NullPointerException ignored) {}

		unpixelating = new Thread(() -> {
			while (bufferScale >= DEFAULT_BUFFER_SCALE) {
				bufferScale = Math.max(bufferScale - UNPIXELATION_STEP, DEFAULT_BUFFER_SCALE);
				try {
					Thread.sleep((long) (delay * UNPIXELATION_STEP / (maximumBufferScale - DEFAULT_BUFFER_SCALE)));
				} catch (InterruptedException e) {
					break;
				}
			}
		});

		unpixelating.start();
	}

	private static boolean debug = false;

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		GamePanel.debug = debug;
	}
}
