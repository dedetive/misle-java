package com.ded.misle.renderer.particles.core;

import java.awt.*;
import java.awt.geom.Point2D;

import static com.ded.misle.game.GamePanel.player;

/**
 * Defines the base behavior expected from any renderable and updatable particle in the game.
 * <p>
 * A particle is a temporary visual element—such as an effect, spark, or ambient detail—
 * that can be updated, rendered, started, or destroyed.
 * Implementations of this interface are usually small, self-contained effects
 * that may be animated, modified by external behaviors (modifiers),
 * or simply appear for a duration.
 * <p>
 * This interface is intentionally minimal, to allow both simple and advanced particles
 * to be created and integrated with the global rendering loop.
 */
public interface ParticleInterface {

	/**
	 * Called every frame to update the state of the particle.
	 * This may include position changes, lifespan handling, or other behavior.
	 */
	void update();

	/**
	 * Called every frame to render the particle on the screen.
	 * Should respect world coordinates and internal state (size, image, etc.).
	 *
	 * @param g the graphics context to draw onto
	 */
	void draw(Graphics2D g);

	/**
	 * Immediately removes the particle from the active registry.
	 * Once destroyed, the particle is no longer updated or rendered.
	 */
	void destroy();

	/**
	 * Starts the particle and registers it for updates and rendering.
	 * Should be called once after creation.
	 */
	void start();

	/**
	 * Checks if the particle is currently active in the system.
	 *
	 * @return {@code true} if the particle is running, {@code false} otherwise
	 */
	boolean isRunning();

	/**
	 * Calculates the screen-space position where a particle should be drawn,
	 * by applying the camera offset to its world position and by
	 * reverting size scale multiplier.
	 * <p>
	 * This utility ensures that particles are always drawn relative to the camera,
	 * regardless of where the player is in the world.
	 *
	 * @param worldPosition the raw position of the particle in world coordinates
	 * @param sizeMulti the particle's size multiplier for normalizing scale
	 * @return a 2D point representing the on-screen drawing position
	 *
	 */
	static Point2D.Float getDrawPos(Point2D.Float worldPosition, float sizeMulti) {
		return new Point2D.Float(
				(worldPosition.x - player.pos.getCameraOffsetX()) / sizeMulti,
				(worldPosition.y - player.pos.getCameraOffsetY()) / sizeMulti);
	}
}