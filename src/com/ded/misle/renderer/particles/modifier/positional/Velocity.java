package com.ded.misle.renderer.particles.modifier.positional;

import com.ded.misle.renderer.particles.core.*;

import java.awt.geom.Point2D;

@ModifierType(ModifierType.Type.POSITIONAL)
public class Velocity implements ParticleModifier {
	private final float vx, vy;

	public Velocity(float vx, float vy) {
		this.vx = vx;
		this.vy = vy;
	}

	@Override
	public void modify(Particle particle) {
		Point2D.Float p = particle.getWorldPosition();
		p.x += vx;
		p.y += vy;
		particle.setWorldPosition(p);
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.FRAME;
	}
}