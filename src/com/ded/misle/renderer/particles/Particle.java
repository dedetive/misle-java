package com.ded.misle.renderer.particles;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.ded.misle.renderer.particles.ParticleModifier.ActivationTime.*;

public abstract class Particle implements ParticleInterface {
	BufferedImage image;
	Point origin;
	float sizeMulti;
	ParticleModifier[] modifiers;

	public Particle(BufferedImage image, Point origin, float sizeMulti, ParticleModifier... modifiers) {
		this.image = image;
		this.origin = origin;
		this.sizeMulti = sizeMulti;
		this.modifiers = modifiers;
		updateModifiers(INIT);
	}

	public Particle(BufferedImage image, Point origin, ParticleModifier... modifiers) {
		this(image, origin, 1f, modifiers);
	}

	@Override
	public void update() {
		updateModifiers(FRAME);
	}

	@Override
	public void destroy() {
		updateModifiers(DESTROY);
		ParticleRegistry.remove(this);
	}

	@Override
	public void start() {
		updateModifiers(START);
		ParticleRegistry.add(this);
	}

	@Override
	public boolean isRunning() {
		return ParticleRegistry.contains(this);
	}

	public Point getOrigin() { return origin; }
	public void setOrigin(Point origin) { this.origin = origin; }
	public float getSizeMulti() { return sizeMulti; }
	public void setSizeMulti(float sizeMulti) { this.sizeMulti = sizeMulti; }
	public BufferedImage getImage() { return image; }
	public void setImage(BufferedImage image) { this.image = image; }

	protected void updateModifiers(ParticleModifier.ActivationTime time) {
		for (ParticleModifier modifier : modifiers) {
			modifier.updateIfNeeded(time, this);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.scale(sizeMulti, sizeMulti);
		Point drawPos = ParticleInterface.getDrawPos(origin);
		g.drawImage(image,
				drawPos.x,
				drawPos.y,
				null);
		g.scale(1 / sizeMulti, 1 / sizeMulti);
	}
}
