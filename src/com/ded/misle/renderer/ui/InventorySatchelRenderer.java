package com.ded.misle.renderer.ui;

import com.ded.misle.renderer.image.ImageManager;

import java.awt.*;

import static com.ded.misle.game.GamePanel.originalScreenHeight;
import static com.ded.misle.game.GamePanel.originalScreenWidth;
import static com.ded.misle.renderer.image.ImageManager.cachedImages;

public class InventorySatchelRenderer {
	private static boolean isActive = true;

	public static void setActive(boolean isActive) {
		InventorySatchelRenderer.isActive = isActive;
	}

	public static void drawIfPossible(Graphics2D g2d) {
		if (!isActive) return;
		g2d.drawImage(cachedImages.get(ImageManager.ImageName.INVENTORY_SATCHEL),
				originalScreenWidth - 64,
				originalScreenHeight - 64,
				null);
	}
}
