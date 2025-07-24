package com.ded.misle.renderer.ui;

import com.ded.misle.renderer.image.ImageManager;

import java.awt.*;

import static com.ded.misle.game.GamePanel.originalScreenHeight;
import static com.ded.misle.game.GamePanel.originalScreenWidth;
import static com.ded.misle.renderer.image.ImageManager.cachedImages;

public final class InventorySatchelUI implements UIElement {
	private boolean isActive = true;

	@Override
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public void drawIfPossible(Graphics2D g2d) {
		if (!isActive) return;
		g2d.drawImage(cachedImages.get(ImageManager.ImageName.INVENTORY_SATCHEL),
				originalScreenWidth - 64,
				originalScreenHeight - 64,
				null);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;

		return this.getClass() == obj.getClass();
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
