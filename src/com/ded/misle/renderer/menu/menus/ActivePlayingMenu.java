package com.ded.misle.renderer.menu.menus;

import com.ded.misle.renderer.menu.core.Menu;
import com.ded.misle.renderer.ui.elements.BoxScreen;
import com.ded.misle.renderer.ui.elements.InventorySatchelUI;
import com.ded.misle.renderer.ui.core.UIRegistry;

import java.awt.*;

public class ActivePlayingMenu implements Menu {
	private final UIRegistry registry = new UIRegistry();

	public void draw(Graphics2D g2d) {
		registry.drawActive(g2d);
	}

	@Override
	public void init() {
		registry.add(InventorySatchelUI.class);
		registry.add(BoxScreen.class);
	}
}