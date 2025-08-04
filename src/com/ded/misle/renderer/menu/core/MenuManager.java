package com.ded.misle.renderer.menu.core;

import com.ded.misle.renderer.menu.menus.MainMenu;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.ded.misle.game.GamePanel.originalScreenHeight;
import static com.ded.misle.game.GamePanel.originalScreenWidth;

public final class MenuManager {
	private MenuManager() {}

	private static Menu activeMenu;
	private static boolean needsUpdate = true;

	private static final BufferedImage lastImage = new BufferedImage(originalScreenWidth, originalScreenHeight, BufferedImage.TYPE_INT_ARGB);
	private static final Graphics2D lastImageGraphics = lastImage.createGraphics();

	public static void init() {
		setCurrent(new MainMenu());
	}

	public static void setCurrent(Menu main) {
		activeMenu = main;
		activeMenu.init();
	}

	public static Menu getCurrent() {
		return activeMenu;
	}

	public static void requestUpdate() {
		needsUpdate = true;
	}

	public static void draw(Graphics2D g2d) {
		if (activeMenu != null) activeMenu.update();
		if (needsUpdate) {
			needsUpdate = false;
			if (activeMenu != null) activeMenu.draw(lastImageGraphics);
			else init();
		}
		g2d.drawImage(lastImage, 0, 0, null);
	}
}
