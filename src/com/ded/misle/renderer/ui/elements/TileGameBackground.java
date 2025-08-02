package com.ded.misle.renderer.ui.elements;

import com.ded.misle.renderer.menu.core.MenuManager;
import com.ded.misle.renderer.ui.core.AbstractUIElement;
import com.ded.misle.world.logic.World;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.game.GamePanel.originalTileSize;

public class TileGameBackground extends AbstractUIElement.SingletonUIElement {

	private static BufferedImage tileBackgroundTexture;

	private static void recalculate() {
		World world = player.pos.world;

		tileBackgroundTexture = new BufferedImage(originalScreenWidth, originalScreenHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = tileBackgroundTexture.createGraphics();

		for (int i = 0; i < originalScreenWidth / originalTileSize + 2; i++) {
			for (int j = 0; j < originalScreenHeight / originalTileSize + 2; j++) {

				int worldX = (int) (Math.max((player.pos.getCameraOffsetX() / (double) originalTileSize), 0) + i);
				int worldY = (int) (Math.max((player.pos.getCameraOffsetY() / (double) originalTileSize), 0) + j);

				BufferedImage texture = ((worldX + worldY) % 2 == 0) ?
						world.background.box[0].getTexture() :
						world.background.box[1].getTexture();

				int drawX = (int) (i * originalTileSize - (player.pos.getCameraOffsetX() % originalTileSize));
				int drawY = (int) (j * originalTileSize - (player.pos.getCameraOffsetY() % originalTileSize));

				g2d.drawImage(texture, drawX, drawY, originalTileSize, originalTileSize, null);
			}
		}
	}

	public static void triggerUpdate() {
		recalculate();
		MenuManager.requestUpdate();
	}

	@Override
	public void drawIfPossible(Graphics2D g2d) {
		g2d.drawImage(tileBackgroundTexture, 0, 0, null);
	}
}
