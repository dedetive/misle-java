package com.ded.misle.renderer.particles;

public interface ParticleModifier {
	void modify(Particle particle);

	ActivationTime getActivationTime();

	enum ActivationTime {
		INIT,
		START,
		FRAME,
		DESTROY
	}

	default boolean shouldUpdate(ActivationTime current) {
		return current == this.getActivationTime();
	}

	default void updateIfNeeded(ActivationTime current, Particle particle) {
		if (shouldUpdate(current)) this.modify(particle);
	}
}