package com.ded.misle.renderer.particles.modifier.transformer;

import com.ded.misle.renderer.particles.core.*;

@ModifierType(ModifierType.Type.TRANSFORMER)
public class Enlarge implements ParticleModifier {
	private final float max;
	private final float step;
	private float current = Float.MIN_VALUE;

	public Enlarge(float step, float max) {
		this.max = max;
		this.step = step;
	}

	public Enlarge(float step) {
		this(step, Float.MAX_VALUE);
	}

	public static Enlarge of(float step) {
		return new Enlarge(step);
	}

	@Override
	public void modify(Particle particle) {
		if (current == Float.MIN_VALUE) current = particle.getSizeMulti();
		current = Math.min(current + step, max);
		particle.setSizeMulti(current);
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.FRAME;
	}
}
