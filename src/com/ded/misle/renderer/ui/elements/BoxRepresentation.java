package com.ded.misle.renderer.ui.elements;

import com.ded.misle.renderer.menu.core.MenuManager;
import com.ded.misle.renderer.ui.core.AbstractUIElement;
import com.ded.misle.world.boxes.Box;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static com.ded.misle.game.GamePanel.originalTileSize;
import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.boxes.Box.isInvalid;

public class BoxRepresentation extends AbstractUIElement {
	private BufferedImage texture;
	private Point position;
	private int priority = Integer.MIN_VALUE;

	private boolean needsRecalculation = true;

	public BoxRepresentation() {
		BoxScreen.addBox(this);
	}

	public BoxRepresentation updatePosition(Box box) {
		if (needsRecalculation) {
			this.priority = box.worldLayer;
			float renderX = box.getRenderX();
			float renderY = box.getRenderY();
			float cameraOffsetX = player.pos.getCameraOffsetX();
			float cameraOffsetY = player.pos.getCameraOffsetY();

			int screenX = (int) (renderX - cameraOffsetX - box.getVisualOffsetX() * originalTileSize);
			int screenY = (int) (renderY - cameraOffsetY - box.getVisualOffsetY() * originalTileSize);

			position = new Point(screenX, screenY);
			MenuManager.requestUpdate();
		}
		needsRecalculation = false;
		return this;
	}

	public BoxRepresentation setTexture(BufferedImage texture) {
		this.texture = texture;
		return this;
	}

	public BoxRepresentation triggerUpdate() {
		needsRecalculation = true;
		return this;
	}

	@Override
	public void drawIfPossible(Graphics2D g2d) {
		if (texture == null || position == null) return;
		if (isInvalid(position.x, position.y)) return;
		g2d.drawImage(texture, position.x, position.y, originalTileSize, originalTileSize, null);
	}

	public int getPriority() {
		return priority;
	}
}
