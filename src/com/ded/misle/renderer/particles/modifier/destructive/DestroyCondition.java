package com.ded.misle.renderer.particles.modifier.destructive;

import com.ded.misle.renderer.particles.core.*;

import java.util.function.Predicate;

@ModifierType(ModifierType.Type.DESTRUCTIVE)
public class DestroyCondition implements ParticleModifier {

	private final Predicate<Particle> condition;

	public DestroyCondition(Predicate<Particle> condition) {
		this.condition = condition;
	}

	@Override
	public void modify(Particle particle) {
		if (condition.test(particle))
			particle.destroy();
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.FRAME;
	}
}
