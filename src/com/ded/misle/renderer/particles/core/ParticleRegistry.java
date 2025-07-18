package com.ded.misle.renderer.particles.core;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Global registry and manager for all active and inactive particles in the game.
 * <p>
 * This class handles the update and rendering of all active particles,
 * and also stores particles that were created but not yet started.
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
	 * Thread-safe list of inactive particles (created but not yet started).
	 * Useful for managing preloaded or delayed particles, and for modifiers manipulation.
	 */
	private static final List<Particle> inactiveParticles = new CopyOnWriteArrayList<>();

	/**
	 * Updates and draws all currently active particles.
	 * <p>
	 * This method should be called once per frame, during the main
	 * rendering cycle of the game.
	 *
	 * @param g2d the graphics context to draw all particles with
	 */
	public static void updateThenDraw(Graphics2D g2d) {
		updateInactive();
		for (Particle particle : particles) {
			particle.update();
			particle.draw(g2d);
		}
	}

	/**
	 * Updates all registered inactive particles.
	 * <p>
	 * This method is called before rendering active particles, allowing
	 * inactive particles to process modifiers or prepare internal state
	 * (e.g., delay-based activation, passive animations).
	 * <p>
	 * Inactive particles are those added via {@link #addInactive(Particle)}
	 * but not yet started via {@link Particle#start()}.
	 */
	private static void updateInactive() {
		for (Particle particle : inactiveParticles) {
			particle.updateInactive();
		}
	}

	/**
	 * Registers a particle as inactive, to be updated but not rendered.
	 * <p>
	 * Inactive particles do not get drawn or counted as running, but still
	 * receive {@link ParticleModifier.ActivationTime#INACTIVE_FRAME} updates
	 * each frame, allowing them to prepare behavior or react passively.
	 * <p>
	 * Particles in this state can be promoted to active via {@link Particle#start()}.
	 *
	 * @param particle the particle to register as inactive
	 */
	public static void addInactive(Particle particle) {
		inactiveParticles.add(particle);
	}

	/**
	 * Activates a particle, registering it for updates and rendering.
	 * <p>
	 * If the particle is currently in the inactive list, it is removed from there
	 * and added to the active particle list. Otherwise, it is added directly to the active list.
	 * <p>
	 * This method is typically called from {@link Particle#start()}.
	 *
	 * @param particle the particle to activate and render
	 */
	public static void add(Particle particle) {
		inactiveParticles.remove(particle);
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