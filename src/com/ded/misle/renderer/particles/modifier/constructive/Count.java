package com.ded.misle.renderer.particles.modifier.constructive;

import com.ded.misle.renderer.particles.core.Particle;
import com.ded.misle.renderer.particles.core.ParticleModifier;

import com.ded.misle.renderer.particles.core.*;

import java.util.function.Supplier;

@ModifierType(ModifierType.Type.CONSTRUCTIVE)
public class Count implements ParticleModifier {

	private final int count;
	private final Supplier<Particle> particleFactory;

	public Count(int count, Supplier<Particle> particleFactory) {
		this.count = count;
		this.particleFactory = particleFactory;
	}

	public static Count of(int count, Supplier<Particle> particleFactory) {
		return new Count(count, particleFactory);
	}

	@Override
	public void modify(Particle particle) {
		particle.destroy();
		for (int i = 0; i < count; i++) {
			Particle p = particleFactory.get();
			p.start();
		}
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.INIT;
	}
}