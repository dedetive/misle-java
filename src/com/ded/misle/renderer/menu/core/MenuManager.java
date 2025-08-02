package com.ded.misle.renderer.menu.core;

import com.ded.misle.renderer.menu.menus.MainMenu;

import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.ded.misle.game.GamePanel.originalScreenHeight;
import static com.ded.misle.game.GamePanel.originalScreenWidth;

public final class MenuManager {
	private MenuManager() {}

	private static Menu mainActive;
	private static boolean needsUpdate = true;

	private static final BufferedImage lastImage = new BufferedImage(originalScreenWidth, originalScreenHeight, BufferedImage.TYPE_INT_ARGB);
	private static final Graphics2D lastImageGraphics = lastImage.createGraphics();

	public static void init() {
		setCurrent(new MainMenu());
	}

	public static void setCurrent(Menu main) {
		mainActive = main;
		mainActive.init();
	}

	public static Menu getCurrent() {
		return mainActive;
	}

	public static void requestUpdate() {
		needsUpdate = true;
	}

	public static void draw(Graphics2D g2d) {
		if (mainActive != null) mainActive.update();
		if (needsUpdate) {
			needsUpdate = false;
			if (mainActive != null) mainActive.draw(lastImageGraphics);
			else init();
		}
		g2d.drawImage(lastImage, 0, 0, null);
	}
}
