package com.ded.misle.renderer.smoother.modifiers;

import com.ded.misle.renderer.smoother.ValueModifier;

/**
 * A value modifier that applies a continuous sine wave-based offset to a value over time.
 * <p>
 * This class is typically used for creating oscillating effects, such as smooth bobbing,
 * idle breathing animations, or hovering motions. The wave is determined by its amplitude
 * and frequency and continues indefinitely.
 */
public class SineWaveModifier implements ValueModifier, Cloneable {

	/**
	 * Elapsed time since the modifier started.
	 * <p>
	 * Used as the time input for the sine wave. Increases continuously with each update call.
	 */
	private float time;

	/**
	 * The maximum height of the sine wave from its center.
	 * <p>
	 * Controls how strong the offset effect is.
	 */
	private final float amplitude;

	/**
	 * The frequency of the sine wave, in cycles per second.
	 * <p>
	 * Controls how fast the oscillation occurs. Higher values result in faster oscillations.
	 */
	private final float frequency;

	/**
	 * Constructs a new {@code SineWaveModifier} with the given amplitude and frequency.
	 *
	 * @param amplitude the maximum vertical displacement from the center of the wave
	 * @param frequency the number of oscillations per second
	 */
	public SineWaveModifier(float amplitude, float frequency) {
		this.amplitude = amplitude;
		this.frequency = frequency;
	}

	/**
	 * Advances the internal time state used by the sine wave.
	 * <p>
	 * This should be called once per frame or tick with the frame delta.
	 *
	 * @param deltaTime time in seconds since the last update
	 */
	@Override
	public void update(float deltaTime) {
		time += deltaTime;
	}

	/**
	 * Computes the current offset value based on the sine wave.
	 *
	 * @return the current sine wave offset
	 */
	@Override
	public float getOffset() {
		return (float) (amplitude * Math.sin(frequency * Math.PI * time));
	}

	/**
	 * Indicates whether the modifier has finished.
	 * <p>
	 * For sine waves, this is always {@code false}, since the wave is infinite.
	 *
	 * @return {@code false} always
	 */
	@Override
	public boolean isFinished() {
		return false;
	}

	/**
	 * Creates a clone of this modifier with a reset time.
	 * <p>
	 * The cloned instance will have the same amplitude and frequency, but will
	 * start from time zero.
	 *
	 * @return a new {@code SineWaveModifier} with the same configuration
	 * @throws CloneNotSupportedException if cloning fails (should not happen)
	 */
	@Override
	public SineWaveModifier clone() throws CloneNotSupportedException {
		return (SineWaveModifier) super.clone();
	}
}