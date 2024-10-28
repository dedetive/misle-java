package com.ded.misle;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static com.ded.misle.Launcher.scale;

import java.awt.Rectangle;
import javax.swing.JPanel;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GameRenderer {
	public static void renderMainMenu(Graphics g, double width, double height, JPanel panel) {
		if (g instanceof Graphics2D g2d) {

			double scaleByScreenSize = scale / 3.75;

					// BACKGROUND

			g2d.setColor(new Color(48, 48, 48));
			g2d.fillRect(0, 0, (int) width, (int) height);

					// MENU ITSELF

			g2d.setColor(new Color(233, 233, 233));
			g2d.setFont(new Font("Dialog", Font.BOLD, (int) (96 * scaleByScreenSize)));
			g2d.drawString("Misle", (int) (859 * scaleByScreenSize), (int) (182 * scaleByScreenSize));

			// Play button

			g2d.setColor(new Color(191, 191, 191));
			int playButtonX = (int) (736 * scaleByScreenSize);
			int playButtonY = (int) (462 * scaleByScreenSize);
			int playButtonWidth = (int) (448 * scaleByScreenSize);
			int playButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);
			g2d.fillRoundRect(playButtonX, playButtonY, playButtonWidth, playButtonHeight, 35, 35);
			g2d.setColor(new Color(0, 0, 0));
			g2d.setFont(new Font("Helvetica", Font.BOLD, (int) (40 * scaleByScreenSize)));
			FontMetrics playFm = g2d.getFontMetrics();
			String playButtonText = "Play";
			int playTextWidth = playFm.stringWidth(playButtonText);
			int playTextHeight = playFm.getAscent();
			int playTextX = playButtonX + (playButtonWidth - playTextWidth) / 2;
			int playTextY = playButtonY + (playButtonHeight + playTextHeight) / 2 - playFm.getDescent();
			g2d.drawString(playButtonText, playTextX, playTextY);

			// Quit button

			g2d.setColor(new Color(191, 191, 191));
			int quitButtonX = (int) (992 * scaleByScreenSize);
			int quitButtonY = (int) (660 * scaleByScreenSize);
			int quitButtonWidth = (int) (192 * scaleByScreenSize);
			int quitButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);
			g2d.fillRoundRect(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight, 35, 35);
			g2d.setColor(new Color(0, 0, 0));
			g2d.setFont(new Font("Helvetica", Font.BOLD, (int) (40 * scaleByScreenSize)));
			FontMetrics quitfm = g2d.getFontMetrics();
			String quitButtonText = "Quit";
			int quitTextWidth = quitfm.stringWidth(quitButtonText);
			int quitTextHeight = quitfm.getAscent();
			int quitTextX = quitButtonX + (quitButtonWidth - quitTextWidth) / 2;
			int quitTextY = quitButtonY + (quitButtonHeight + quitTextHeight) / 2 - quitfm.getDescent();
			g2d.drawString(quitButtonText, quitTextX, quitTextY);

			// Options menu

			g2d.setColor(new Color(191, 191, 191));
			int optionsButtonX = (int) (736 * scaleByScreenSize);
			int optionsButtonY = (int) (660 * scaleByScreenSize);
			int optionsButtonWidth = (int) (192 * scaleByScreenSize);
			int optionsButtonHeight = (int) (155 * scaleByScreenSize);
			Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);
			g2d.fillRoundRect(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight, 35, 35);
			g2d.setColor(new Color(0, 0, 0));
			g2d.setFont(new Font("Helvetica", Font.BOLD, (int) (40 * scaleByScreenSize)));
			FontMetrics optionsFm = g2d.getFontMetrics();
			String optionsButtonText = "Options";
			int optionsTextWidth = optionsFm.stringWidth(optionsButtonText);
			int optionsTextHeight = optionsFm.getAscent();
			int optionsTextX = optionsButtonX + (optionsButtonWidth - optionsTextWidth) / 2;
			int optionsTextY = optionsButtonY + (optionsButtonHeight + optionsTextHeight) / 2 - optionsFm.getDescent();
			g2d.drawString(optionsButtonText, optionsTextX, optionsTextY);

			addClickable(playButton, GameRenderer::gameStart, panel);
			addClickable(quitButton, GameRenderer::quitGame, panel);
			addClickable(optionsButton, GameRenderer::optionsMenu, panel);
		}
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
							System.out.println("Region clicked: " + clickPoint.x + ", " + clickPoint.y);
							System.out.println("Button clicked: " + rect);
							System.out.println("Button dimensions: Width = " + rect.width + ", Height = " + rect.height);
							System.out.println("Button position: X = " + rect.x + ", Y = " + rect.y);
							clickableAction.run(); // Run associated action

							// Optional: Clear the clickables if needed
							ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
							scheduler.schedule(GameRenderer::clearClickables, 16, TimeUnit.MILLISECONDS);
							break; // Exit after the first match
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
		System.out.println("Start game has been executed");
		GamePanel.gameState = GamePanel.GameState.PLAYING;
	}

	public static void quitGame() {
		System.out.println("Quit game has been executed");
		GamePanel.quitGame();
	}

	public static void optionsMenu() {
		System.out.println("Options menu has been executed");
		GamePanel.gameState = GamePanel.GameState.OPTIONS_MENU;
	}
}
