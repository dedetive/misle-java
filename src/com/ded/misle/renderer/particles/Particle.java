package com.ded.misle.renderer.particles;

import java.awt.*;

public interface Particle {
	void update();
	void draw(Graphics2D g);

	default void destroy() {
		ParticleRegistry.remove(this);
	}

	default void start() {
		ParticleRegistry.add(this);
	}

	default boolean isRunning() {
		return ParticleRegistry.contains(this);
	}
}