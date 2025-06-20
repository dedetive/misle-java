package com.ded.misle.renderer.smoother.modifiers;

import com.ded.misle.renderer.smoother.ValueModifier;

/**
 * A modifier that creates a bounce effect by applying a dampened oscillation over time.
 * <p>
 * The bounce begins with a specified amplitude and progressively decays
 * using an exponential falloff, producing a natural bouncing motion.
 * Useful for visual reactions like knockback, damage impact, or UI feedback.
 */
public class BounceModifier implements ValueModifier {
    /**
     * The current time elapsed since the modifier started.
     */
    private float time;

    /**
     * The total duration of the bounce effect.
     */
    private final float duration;

    /**
     * The maximum offset at the beginning of the bounce.
     */
    private final float amplitude;

    /**
     * The oscillation frequency in terms of π (e.g., 2 = 2π = one full sine wave per second).
     */
    private final float frequency;

    /**
     * Constructs a new bounce modifier with the specified parameters.
     *
     * @param amplitude the maximum offset at the beginning of the bounce
     * @param duration  how long the bounce lasts (in seconds)
     * @param frequency how many oscillations occur per unit of time (in multiples of π)
     */
    public BounceModifier(float amplitude, float duration, float frequency) {
        this.amplitude = amplitude;
        this.duration = duration;
        this.frequency = frequency;
    }

    /**
     * Advances the modifier's time state by the given delta.
     *
     * @param deltaTime time passed since the last update (in seconds)
     */
    @Override
    public void update(float deltaTime) {
        time += deltaTime;
    }

    /**
     * Returns the current offset contributed by the bounce.
     * <p>
     * This offset decreases over time due to exponential damping,
     * and follows a sine oscillation based on the specified frequency.
     *
     * @return the vertical offset applied by the bounce at the current time
     */
    @Override
    public float getOffset() {
        if (time >= duration) return 0f;

        float progress = time / duration;
        float dampened = (float) (amplitude * Math.exp(-3 * progress));
        return (float) (dampened * Math.sin(frequency * Math.PI * time));
    }

    /**
     * Returns whether the bounce animation has completed.
     *
     * @return true if the bounce has finished, false otherwise
     */
    @Override
    public boolean isFinished() {
        return time >= duration;
    }

    /**
     * Creates a copy of this bounce modifier.
     * Note: internal state (like elapsed time) is not copied.
     *
     * @return a new instance with the same bounce parameters
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    public BounceModifier clone() throws CloneNotSupportedException {
        return (BounceModifier) super.clone();
    }
}
