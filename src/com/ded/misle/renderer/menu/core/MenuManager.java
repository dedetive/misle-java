package com.ded.misle.renderer.menu.core;

import com.ded.misle.renderer.menu.menus.MainMenu;

import java.awt.*;

public final class MenuManager {
	private MenuManager() {}

	private static com.ded.misle.renderer.menu.core.Menu mainActive;

	public static void init() {
		setCurrent(new MainMenu());
	}

	public static void setCurrent(com.ded.misle.renderer.menu.core.Menu main) {
		mainActive = main;
		mainActive.init();
	}

	public static Menu getCurrent() {
		return mainActive;
	}

	public static void draw(Graphics2D g2d) {
		if (mainActive != null) mainActive.draw(g2d);
		else init();
	}
}
