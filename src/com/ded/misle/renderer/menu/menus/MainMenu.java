package com.ded.misle.renderer.menu.menus;

import com.ded.misle.renderer.menu.core.Menu;
import com.ded.misle.renderer.ui.core.UIRegistry;
import com.ded.misle.renderer.ui.elements.MainBackground;

import java.awt.*;

public class MainMenu implements Menu {
	private final UIRegistry registry = new UIRegistry();

	@Override
	public void draw(Graphics2D g2d) {
		registry.drawActive(g2d);
	}

	@Override
	public void init() {
		registry.add(MainBackground.class);
	}
}
