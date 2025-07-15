package com.ded.misle.renderer.particles.modifier.positional;

import com.ded.misle.renderer.particles.core.*;
import com.ded.misle.renderer.smoother.*;

import java.awt.geom.Point2D;

import static com.ded.misle.game.GamePanel.FIXED_DELTA;

@ModifierType(ModifierType.Type.POSITIONAL)
public class Acceleration implements ParticleModifier {

	private final SyncedValue velocity;
	private final float acceleration;

	private final float dx;
	private final float dy;

	/**
	 * @param directionDegrees The direction of motion in degrees (0° = right, 90° = down).
	 */
	public Acceleration(float acceleration, float initialVelocity, float directionDegrees, ValueModifier... modifiers) {
		this.acceleration = acceleration;
		this.velocity = new SyncedValue(initialVelocity);
		this.velocity.addModifier(modifiers);

		double radians = Math.toRadians(directionDegrees);
		this.dx = (float) Math.cos(radians);
		this.dy = (float) Math.sin(radians);
	}

	public Acceleration(float acceleration, float directionDegrees, ValueModifier... modifiers) {
		this(acceleration, 0f, directionDegrees, modifiers);
	}

	public static Acceleration of(float acceleration, float initialVelocity, float directionDegrees, ValueModifier... modifiers) {
		return new Acceleration(acceleration, initialVelocity, directionDegrees, modifiers);
	}

	public static Acceleration of(float acceleration, float directionDegrees, ValueModifier... modifiers) {
		return new Acceleration(acceleration, 0f, directionDegrees, modifiers);
	}

	@Override
	public void modify(Particle particle) {
		velocity.set(velocity.getReal() + acceleration);
		velocity.update((float) FIXED_DELTA);

		float v = velocity.getVisual();

		Point2D.Float pos = particle.getWorldPosition();
		pos.x += dx * v;
		pos.y += dy * v;

		particle.setWorldPosition(pos);
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.FRAME;
	}
}