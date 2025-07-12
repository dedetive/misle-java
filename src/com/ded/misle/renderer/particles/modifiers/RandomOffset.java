package com.ded.misle.renderer.particles.modifiers;

import com.ded.misle.renderer.particles.*;

import java.awt.geom.Point2D;
import java.util.Random;

@ModifierType(ModifierType.Type.POSITIONAL)
public class RandomOffset implements ParticleModifier {
	private static final Random random = new Random();

	private float minX;
	private float minY;
	private float maxX;
	private float maxY;

	private RandomOffset(float min, float max) {
		this.minX = min;
		this.minY = min;
		this.maxX = max;
		this.maxY = max;
	}

	private RandomOffset(float minX, float maxX, float minY, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	private RandomOffset(float min, float max, Axis axis) {
		switch (axis) {
			case X -> {
				this.minX = min;
				this.maxX = max;
				this.minY = 0;
				this.maxY = 0;
			}
			case Y -> {
				this.minY = min;
				this.maxY = max;
				this.minX = 0;
				this.maxX = 0;
			}
		}
	}

	public static RandomOffset of(float min, float max) {
		return new RandomOffset(min, max);
	}

	public static RandomOffset of(float minX, float maxX, float minY, float maxY) {
		return new RandomOffset(minX, maxX, minY, maxY);
	}

	public static RandomOffset of(float min, float max, Axis axis) {
		return new RandomOffset(min, max, axis);
	}

	@Override
	public void modify(Particle particle) {
		Point2D.Float p = particle.getWorldPosition();
		float x = getRandomValue(minX, maxX);
		float y = getRandomValue(minY, maxY);
		p.x += x;
		p.y += y;
	}

	private static float getRandomValue(float min, float max) {
		if (min == max) return min;
		return min + (max - min) * random.nextFloat();
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.INIT;
	}

	public enum Axis {
		X,
		Y
	}
}
