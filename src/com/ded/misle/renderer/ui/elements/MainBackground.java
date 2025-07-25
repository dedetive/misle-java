package com.ded.misle.renderer.ui.elements;

import com.ded.misle.renderer.ui.core.AbstractUIElement;

import java.awt.*;

import static com.ded.misle.game.GamePanel.originalScreenHeight;
import static com.ded.misle.game.GamePanel.originalScreenWidth;
import static com.ded.misle.renderer.image.ImageManager.ImageName.MAIN_MENU_BACKGROUND;
import static com.ded.misle.renderer.image.ImageManager.cachedImages;

public final class MainBackground extends AbstractUIElement.SingletonUIElement {
	@Override
	public void drawIfPossible(Graphics2D g2d) {
		g2d.drawImage(cachedImages.get(MAIN_MENU_BACKGROUND),
				0,
				0,
				originalScreenWidth,
				originalScreenHeight,
				null);
	}
}