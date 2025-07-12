package com.ded.misle.renderer.particles;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static com.ded.misle.game.GamePanel.originalTileSize;
import static com.ded.misle.renderer.particles.ParticleModifier.ActivationTime.*;

public abstract class Particle implements ParticleInterface {
	BufferedImage image;
	Point2D.Float worldPosition;
	float sizeMulti = 1f;
	ParticleModifier[] modifiers;

	protected Particle(BufferedImage image, Point2D.Float worldPosition, ParticleModifier... modifiers) {
		this.image = image;
		this.worldPosition =
				new Point2D.Float(worldPosition.x * originalTileSize, worldPosition.y * originalTileSize);
		this.modifiers = modifiers;
		updateModifiers(INIT);
	}
	protected Particle(BufferedImage image, Point worldPosition, ParticleModifier... modifiers) {
		this(image, new Point2D.Float(worldPosition.x, worldPosition.y), modifiers);
	}

	public final Point2D.Float getWorldPosition() { return worldPosition; }
	public final void setWorldPosition(Point2D.Float worldPosition) { this.worldPosition = worldPosition; }
	public final float getSizeMulti() { return sizeMulti; }
	public final void setSizeMulti(float sizeMulti) { this.sizeMulti = sizeMulti; }
	public final BufferedImage getImage() { return image; }
	public final void setImage(BufferedImage image) { this.image = image; }

	@Override
	public final void draw(Graphics2D g) {
		g.scale(sizeMulti, sizeMulti);
		Point2D.Float drawPos = ParticleInterface.getDrawPos(worldPosition);
		g.drawImage(image,
				Math.round(drawPos.x),
				Math.round(drawPos.y),
				null);
		g.scale(1 / sizeMulti, 1 / sizeMulti);
	}

	@Override
	public final void update() {
		updateModifiers(FRAME);
	}

	@Override
	public final void destroy() {
		updateModifiers(DESTROY);
		ParticleRegistry.remove(this);
	}

	@Override
	public final void start() {
		updateModifiers(START);
		ParticleRegistry.add(this);
	}

	@Override
	public final boolean isRunning() {
		return ParticleRegistry.contains(this);
	}

	protected final void updateModifiers(ParticleModifier.ActivationTime time) {
		for (ParticleModifier modifier : modifiers) {
			modifier.updateIfNeeded(time, this);
		}
	}
}
