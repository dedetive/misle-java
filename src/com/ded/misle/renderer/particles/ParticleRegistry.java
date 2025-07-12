package com.ded.misle.renderer.particles;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Global registry and manager for all active particles in the game.
 * <p>
 * This class handles the update and rendering of all currently running particles.
 * Particles must be explicitly added or removed via this registry in order to be
 * processed each frame. Thread-safe for concurrent access.
 * <p>
 * It is not responsible for particle behavior; it simply ensures that all active
 * particles are properly ticked and drawn in the main rendering pipeline.
 */
public final class ParticleRegistry {

	/**
	 * Private constructor to prevent instantiation.
	 * <p>
	 * This class is intended to be used statically and should not be instantiated.
	 */
	private ParticleRegistry() {}

	/**
	 * Thread-safe list holding all currently active particles.
	 * <p>
	 * This list uses a {@link CopyOnWriteArrayList} to allow safe concurrent
	 * additions and removals during the update and render cycles, preventing
	 * {@link java.util.ConcurrentModificationException}s.
	 * <p>
	 * All particles registered here are automatically updated and drawn each frame.
	 */
	private static final List<Particle> particles = new CopyOnWriteArrayList<>();

	/**
	 * Updates and draws all currently registered particles.
	 * <p>
	 * This method should be called once per frame, during the main
	 * rendering cycle of the game.
	 *
	 * @param g2d the graphics context to draw all particles with
	 */
	public static void updateThenDraw(Graphics2D g2d) {
		for (Particle particle : particles) {
			particle.update();
			particle.draw(g2d);
		}
	}

	/**
	 * Adds a new particle to the registry, marking it as active.
	 *
	 * @param particle the particle to add
	 */
	public static void add(Particle particle) {
		particles.add(particle);
	}

	/**
	 * Removes a particle from the registry, stopping updates and rendering.
	 *
	 * @param particle the particle to remove
	 */
	public static void remove(Particle particle) {
		particles.remove(particle);
	}

	/**
	 * Checks whether a given particle is currently active.
	 *
	 * @param particle the particle to check
	 * @return {@code true} if the particle is present in the registry; {@code false} otherwise
	 */
	public static boolean contains(Particle particle) {
		return particles.contains(particle);
	}
}