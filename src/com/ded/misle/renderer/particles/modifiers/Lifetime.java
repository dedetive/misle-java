package com.ded.misle.renderer.particles.modifiers;

import com.ded.misle.renderer.particles.Particle;
import com.ded.misle.renderer.particles.ParticleModifier;

public class Lifetime implements ParticleModifier {

	private final long lifetime;
	private long initialTime;

	public Lifetime(long ms) {
		this.lifetime = ms;
	}

	public Lifetime(float seconds) {
		this((long) (seconds * 1000));
	}

	public static Lifetime ofSeconds(float seconds) {
		return new Lifetime(seconds);
	}

	public static Lifetime ofMillis(long ms) {
		return new Lifetime(ms);
	}

	public void start() {
		this.initialTime = System.currentTimeMillis();
	}

	@Override
	public void modify(Particle particle) {
		if (particle.isRunning() && initialTime == 0)
			start();
		if (System.currentTimeMillis() - initialTime > lifetime)
			particle.destroy();
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.FRAME;
	}
}
