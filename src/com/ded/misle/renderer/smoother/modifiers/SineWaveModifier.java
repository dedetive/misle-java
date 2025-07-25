package com.ded.misle.renderer.smoother.modifiers;

import com.ded.misle.renderer.smoother.ValueModifier;

public class SineWaveModifier implements ValueModifier, Cloneable {
	private float time;

	private final float amplitude;

	private final float frequency;

	public SineWaveModifier(float amplitude, float frequency) {
		this.amplitude = amplitude;
		this.frequency = frequency;
	}

	@Override
	public void update(float deltaTime) {
		time += deltaTime;
	}

	@Override
	public float getOffset() {
		return (float) (amplitude * Math.sin(frequency * Math.PI * time));
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public SineWaveModifier clone() throws CloneNotSupportedException {
		return (SineWaveModifier) super.clone();
	}
}
