package com.ded.misle.renderer.particles;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ParticleRegistry {
	private ParticleRegistry() {}

	private static final List<Particle> particles = new CopyOnWriteArrayList<>();

	public static void updateThenDraw(Graphics2D g2d) {
		for (Particle particle : particles) {
			particle.update();
			particle.draw(g2d);
		}
	}

	public static void add(Particle particle) {
		particles.add(particle);
	}

	public static void remove(Particle particle) {
		particles.remove(particle);
	}

	public static boolean contains(Particle particle) {
		return particles.contains(particle);
	}
}
