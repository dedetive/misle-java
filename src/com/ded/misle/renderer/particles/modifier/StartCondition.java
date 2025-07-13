package com.ded.misle.renderer.particles.modifier;

import com.ded.misle.renderer.particles.core.*;

import java.util.function.Predicate;

@ModifierType(ModifierType.Type.CONSTRUCTIVE)
public class StartCondition implements ParticleModifier {

	private final Predicate<Particle> condition;

	public StartCondition(Predicate<Particle> condition) {
		this.condition = condition;
	}

	@Override
	public void modify(Particle particle) {
		if (condition.test(particle))
			particle.start();
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.INACTIVE_FRAME;
	}
}