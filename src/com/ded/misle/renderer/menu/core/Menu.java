package com.ded.misle.renderer.menu.core;

import java.awt.*;

public interface Menu {
	void draw(Graphics2D g2d);
	default void init() {};
}
