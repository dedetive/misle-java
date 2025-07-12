package com.ded.misle.renderer.particles.modifier;

import com.ded.misle.renderer.particles.core.*;

import java.awt.geom.Point2D;

@ModifierType(ModifierType.Type.POSITIONAL)
public class Offset implements ParticleModifier {
	private final Point2D.Float offset;

	public Offset(Point2D.Float offset) {
		this.offset = offset;
	}

	public static Offset of(Point2D.Float offset) {
		return new Offset(offset);
	}

	public Offset(float x, float y) {
		this.offset = new Point2D.Float(x, y);
	}

	public static Offset of(float x, float y) {
		return new Offset(x, y);
	}

	@Override
	public void modify(Particle particle) {
		Point2D.Float p = particle.getWorldPosition();
		p.x += offset.x;
		p.y += offset.y;
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.INIT;
	}
}
