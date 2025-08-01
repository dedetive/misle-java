package com.ded.misle.renderer.ui.elements;

import com.ded.misle.renderer.ui.core.AbstractUIElement;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.ded.misle.game.GamePanel.originalTileSize;

public class BoxRepresentation extends AbstractUIElement {
	private BufferedImage texture;
	private Point position;

	public BoxRepresentation() {
		BoxScreen.addBox(this);
	}

	public BoxRepresentation setPosition(Point position) {
		this.position = position;
		return this;
	}

	public BoxRepresentation setWorldPosition(Point position) {
		this.position = new Point(position.x * originalTileSize, position.y * originalTileSize);
		return this;
	}

	public BoxRepresentation setTexture(BufferedImage texture) {
		this.texture = texture;
		return this;
	}

	@Override
	public void drawIfPossible(Graphics2D g2d) {
		if (texture == null || position == null) return;

		g2d.drawImage(texture, position.x, position.y, originalTileSize, originalTileSize, null);
	}
}
