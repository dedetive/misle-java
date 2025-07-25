package com.ded.misle.renderer.menu;

import java.awt.*;

public final class MenuManager {
	private MenuManager() {}

	private static Menu mainActive;

	public static void setCurrent(Menu main) {
		mainActive = main;
		mainActive.init();
	}

	public static Menu getCurrent() {
		return mainActive;
	}

	public static void draw(Graphics2D g2d) {
		mainActive.draw(g2d);
	}
}
