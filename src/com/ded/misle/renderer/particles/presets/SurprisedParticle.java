package com.ded.misle.renderer.particles.presets;

import com.ded.misle.renderer.particles.Particle;
import com.ded.misle.renderer.particles.ParticleModifier;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.ded.misle.renderer.image.ImageManager.ImageName.SURPRISE_PARTICLE;
import static com.ded.misle.renderer.image.ImageManager.cachedImages;

public class SurprisedParticle extends Particle {
	private static final BufferedImage SURPRISE_EXCLAMATION_IMAGE = cachedImages.get(SURPRISE_PARTICLE);

	public SurprisedParticle(Point origin, float sizeMulti, ParticleModifier... modifiers) {
		super(SURPRISE_EXCLAMATION_IMAGE, origin, sizeMulti, modifiers);
	}

	public SurprisedParticle(Point origin, ParticleModifier... modifiers) {
		this(origin, 1f, modifiers);
	}
}