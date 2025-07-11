package com.ded.misle.renderer.particles;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class ParticleRegistry {
	private ParticleRegistry() {}

	private static final List<Particle> particles = new ArrayList<>();

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
