package com.ded.misle.renderer.particles.core;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static com.ded.misle.game.GamePanel.originalTileSize;
import static com.ded.misle.renderer.particles.core.ParticleModifier.ActivationTime.*;

/**
 * Base abstract class representing a visual particle in the game world.
 * <p>
 * A particle encapsulates an image, a world position (scaled to tile size),
 * a size multiplier for rendering scale, and a set of {@link ParticleModifier}s
 * which define its behavior over its lifecycle.
 * <p>
 * This class manages the lifecycle of the particle through {@link #start()}, {@link #update()},
 * and {@link #destroy()} methods, delegating to modifiers at specific lifecycle phases.
 * Particles must be registered with {@link ParticleRegistry} to be active and rendered.
 * <p>
 * Subclasses are responsible for defining specific particle visuals and behavior,
 * while this class provides the core update and draw logic common to all particles.
 */
public abstract class Particle implements ParticleInterface {

	/**
	 * The image representing the particle's visual appearance.
	 * This image may be modified dynamically by {@link ParticleModifier}s.
	 */
	BufferedImage image;

	/**
	 * The particle's position in world coordinates, scaled by the game's original tile size.
	 * Used as the base position for drawing and modifications such as offsets.
	 */
	Point2D.Float worldPosition;

	/**
	 * A multiplier applied to the rendering scale of the particle.
	 * Useful for dynamically resizing particles without modifying the base image.
	 */
	float sizeMulti = 1f;

	/**
	 * The array of modifiers that influence this particle's behavior at different lifecycle phases.
	 */
	ParticleModifier[] modifiers;

	/**
	 * Constructs a particle with the specified image, world position, and modifiers.
	 * <p>
	 * The provided world position coordinates are multiplied by {@code originalTileSize} internally,
	 * converting from tile units to pixel coordinates.
	 * <p>
	 * Modifiers are immediately invoked with the {@link ParticleModifier.ActivationTime#INIT} phase.
	 *
	 * @param image the base image for this particle
	 * @param worldPosition the initial world position in tile coordinates
	 * @param modifiers optional modifiers affecting the particle's behavior
	 */
	protected Particle(BufferedImage image, Point2D.Float worldPosition, ParticleModifier... modifiers) {
		this.image = image;
		this.worldPosition =
				new Point2D.Float(worldPosition.x * originalTileSize, worldPosition.y * originalTileSize);
		this.modifiers = modifiers;
		updateModifiers(INIT);
	}

	/**
	 * Convenience constructor accepting a {@link Point} instead of {@link Point2D.Float}
	 * for the world position, forwarding to the main constructor.
	 *
	 * @param image the base image for this particle
	 * @param worldPosition the initial world position in tile coordinates as an integer point
	 * @param modifiers optional modifiers affecting the particle's behavior
	 */
	protected Particle(BufferedImage image, Point worldPosition, ParticleModifier... modifiers) {
		this(image, new Point2D.Float(worldPosition.x, worldPosition.y), modifiers);
	}

	/**
	 * Returns the current world position of the particle.
	 * <p>
	 * This position is in pixel coordinates, scaled by the original tile size.
	 *
	 * @return the world position as a floating-point 2D coordinate
	 */
	public final Point2D.Float getWorldPosition() { return worldPosition; }

	/**
	 * Updates the particle's world position.
	 *
	 * @param worldPosition the new world position in pixel coordinates
	 */
	public final void setWorldPosition(Point2D.Float worldPosition) { this.worldPosition = worldPosition; }

	/**
	 * Returns the current rendering size multiplier of the particle.
	 *
	 * @return the scale multiplier applied during rendering
	 */
	public final float getSizeMulti() { return sizeMulti; }

	/**
	 * Sets the rendering size multiplier for the particle.
	 * <p>
	 * This does not modify the particle's image but affects the scale used when drawing.
	 *
	 * @param sizeMulti the scale multiplier to apply during rendering
	 */
	public final void setSizeMulti(float sizeMulti) { this.sizeMulti = sizeMulti; }

	/**
	 * Returns the current image used to visually represent the particle.
	 *
	 * @return the particle's image
	 */
	public final BufferedImage getImage() { return image; }

	/**
	 * Sets the image used for this particle's visual representation.
	 *
	 * @param image the new image to display for this particle
	 */
	public final void setImage(BufferedImage image) { this.image = image; }

	/**
	 * Draws the particle on the provided graphics context.
	 * <p>
	 * The particle's position is converted to screen coordinates via {@link ParticleInterface#getDrawPos(Point2D.Float)},
	 * and the image is rendered scaled by {@link #sizeMulti}.
	 *
	 * @param g the graphics context on which to draw the particle
	 */
	@Override
	public final void draw(Graphics2D g) {
		g.scale(sizeMulti, sizeMulti);
		Point2D.Float drawPos = ParticleInterface.getDrawPos(worldPosition);
		g.drawImage(image,
				Math.round(drawPos.x),
				Math.round(drawPos.y),
				null);
		g.scale(1 / sizeMulti, 1 / sizeMulti);
	}

	/**
	 * Updates the particle by invoking its modifiers for the {@link ParticleModifier.ActivationTime#FRAME} phase.
	 * <p>
	 * Called once per frame, this triggers per-frame behavior such as movement or animation.
	 * <p>
	 * Modifiers are not updated before this particle starts.
	 */
	@Override
	public final void update() {
		updateModifiers(FRAME);
	}

	/**
	 * Starts the particle by invoking modifiers for the {@link ParticleModifier.ActivationTime#START} phase,
	 * then registers the particle with the global {@link ParticleRegistry} for updating and rendering.
	 * <p>
	 * Modifiers are not updated before this particle starts.
	 */
	@Override
	public final void start() {
		updateModifiers(START);
		ParticleRegistry.add(this);
	}

	/**
	 * Destroys the particle by invoking modifiers for the {@link ParticleModifier.ActivationTime#DESTROY} phase,
	 * then removing it from the {@link ParticleRegistry} to cease updates and rendering.
	 */
	@Override
	public final void destroy() {
		updateModifiers(DESTROY);
		ParticleRegistry.remove(this);
	}

	/**
	 * Returns whether this particle is currently registered and active.
	 *
	 * @return {@code true} if the particle is present in the {@link ParticleRegistry}; {@code false} otherwise
	 */
	@Override
	public final boolean isRunning() {
		return ParticleRegistry.contains(this);
	}

	/**
	 * Helper method to update all modifiers for a given lifecycle activation phase.
	 * <p>
	 * Iterates through all modifiers and invokes {@link ParticleModifier#updateIfNeeded(ParticleModifier.ActivationTime, Particle)}
	 * with the provided activation time and this particle instance.
	 *
	 * @param time the lifecycle phase during which modifiers should be updated
	 */
	protected final void updateModifiers(ParticleModifier.ActivationTime time) {
		for (ParticleModifier modifier : modifiers) {
			modifier.updateIfNeeded(time, this);
		}
	}
}