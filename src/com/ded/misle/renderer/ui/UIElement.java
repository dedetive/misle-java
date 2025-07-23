package com.ded.misle.renderer.ui;

import java.awt.*;

public interface UIElement {
	void setActive(boolean active);
	boolean isActive();
	void drawIfPossible(Graphics2D g2d);
}
