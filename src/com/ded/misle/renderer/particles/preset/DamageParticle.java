package com.ded.misle.renderer.particles.preset;

import com.ded.misle.renderer.particles.core.Particle;
import com.ded.misle.renderer.particles.core.ParticleModifier;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import static com.ded.misle.renderer.image.ImageManager.ImageName.*;
import static com.ded.misle.renderer.image.ImageManager.cachedImages;

public class DamageParticle extends Particle {
	private static final BufferedImage[] DAMAGE_PARTICLE_IMAGE = new BufferedImage[] {
			cachedImages.get(DAMAGE_PARTICLE0),
			cachedImages.get(DAMAGE_PARTICLE1),
			cachedImages.get(DAMAGE_PARTICLE2),
			cachedImages.get(DAMAGE_PARTICLE3),
			cachedImages.get(DAMAGE_PARTICLE4),
	};

	public DamageParticle(Point2D.Float worldPosition, ParticleModifier... modifiers) {
		super(getRandomDamageParticle(), worldPosition, modifiers);
	}

	public DamageParticle(Point worldPosition, ParticleModifier... modifiers) {
		super(getRandomDamageParticle(), worldPosition, modifiers);
	}

	private static BufferedImage getRandomDamageParticle() {
		return DAMAGE_PARTICLE_IMAGE[new Random().nextInt(DAMAGE_PARTICLE_IMAGE.length)];
	}
}
