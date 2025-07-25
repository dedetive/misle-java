package com.ded.misle.renderer.menu;

import com.ded.misle.renderer.ui.*;

import java.awt.*;

public class MainMenu implements Menu {
	private final UIRegistry registry = new UIRegistry();

	@Override
	public void draw(Graphics2D g2d) {
		registry.drawActive(g2d);
	}

	@Override
	public void init() {

	}
}
