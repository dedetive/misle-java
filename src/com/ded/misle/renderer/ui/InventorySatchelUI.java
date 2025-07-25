package com.ded.misle.renderer.ui;

import com.ded.misle.renderer.image.ImageManager;

import java.awt.*;

import static com.ded.misle.game.GamePanel.originalScreenHeight;
import static com.ded.misle.game.GamePanel.originalScreenWidth;
import static com.ded.misle.renderer.image.ImageManager.cachedImages;

public final class InventorySatchelUI extends AbstractUIElement.SingletonUIElement {
	@Override
	public void drawIfPossible(Graphics2D g2d) {
		if (!isActive) return;
		g2d.drawImage(cachedImages.get(ImageManager.ImageName.INVENTORY_SATCHEL),
				originalScreenWidth - 64,
				originalScreenHeight - 64,
				null);
	}
}
