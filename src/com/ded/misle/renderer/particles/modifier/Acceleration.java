package com.ded.misle.renderer.particles.modifier;

import com.ded.misle.renderer.particles.core.*;
import com.ded.misle.renderer.smoother.*;

import java.awt.geom.Point2D;

import static com.ded.misle.game.GamePanel.FIXED_DELTA;

@ModifierType(ModifierType.Type.POSITIONAL)
public class Acceleration implements ParticleModifier {

	private final SyncedValue velocity;

	private final float acceleration;

	public Acceleration(float acceleration, ValueModifier... modifiers) {
		this.acceleration = acceleration;
		this.velocity = new SyncedValue(0f);
		this.velocity.addModifier(modifiers);
	}

	public static Acceleration of(float acceleration, ValueModifier... modifiers) {
		return new Acceleration(acceleration, modifiers);
	}

	@Override
	public void modify(Particle particle) {
		velocity.set(velocity.getReal() + acceleration);
		velocity.update((float) FIXED_DELTA);

		Point2D.Float p = particle.getWorldPosition();
		p.y += velocity.getVisual();
		particle.setWorldPosition(p);
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.FRAME;
	}
}