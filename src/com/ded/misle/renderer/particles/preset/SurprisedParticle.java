package com.ded.misle.renderer.particles.preset;

import com.ded.misle.renderer.particles.core.Particle;
import com.ded.misle.renderer.particles.core.ParticleModifier;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static com.ded.misle.renderer.image.ImageManager.ImageName.SURPRISE_PARTICLE;
import static com.ded.misle.renderer.image.ImageManager.cachedImages;

public class SurprisedParticle extends Particle {
	private static final BufferedImage SURPRISE_EXCLAMATION_IMAGE = cachedImages.get(SURPRISE_PARTICLE);

	public SurprisedParticle(Point2D.Float worldPosition, ParticleModifier... modifiers) {
		super(SURPRISE_EXCLAMATION_IMAGE, worldPosition, modifiers);
	}

	public SurprisedParticle(Point worldPosition, ParticleModifier... modifiers) {
		super(SURPRISE_EXCLAMATION_IMAGE, new Point2D.Float(worldPosition.x, worldPosition.y), modifiers);
	}
}