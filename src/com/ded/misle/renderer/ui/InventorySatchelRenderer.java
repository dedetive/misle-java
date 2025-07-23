package com.ded.misle.renderer.ui;

import com.ded.misle.renderer.image.ImageManager;

import java.awt.*;

import static com.ded.misle.game.GamePanel.originalScreenHeight;
import static com.ded.misle.game.GamePanel.originalScreenWidth;
import static com.ded.misle.renderer.image.ImageManager.cachedImages;

public class InventorySatchelRenderer {
	private static boolean shouldRender = true;

	public static void setRender(boolean shouldRender) {
		InventorySatchelRenderer.shouldRender = shouldRender;
	}

	public static void drawIfPossible(Graphics2D g2d) {
		if (!shouldRender) return;
		g2d.drawImage(cachedImages.get(ImageManager.ImageName.INVENTORY_SATCHEL),
				originalScreenWidth - 64,
				originalScreenHeight - 64,
				null);
	}
}
