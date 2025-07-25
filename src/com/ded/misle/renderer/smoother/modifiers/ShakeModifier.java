package com.ded.misle.renderer.smoother.modifiers;

import com.ded.misle.renderer.smoother.ValueModifier;

import java.util.Random;

/**
 * A modifier that creates a random shaking effect over time.
 * <p>
 * Useful for visual feedback such as screen shake, damage impact, or dynamic offset effects.
 * The offset is randomly updated each frame within the given strength.
 */
public class ShakeModifier implements ValueModifier, Cloneable {
    /**
     * The maximum strength of the shake, determining the range of random offset values.
     */
    private final float strength;

    /**
     * The total duration of the shake effect in seconds.
     */
    private final float duration;

    /**
     * The current time elapsed since the shake started.
     */
    private float time;

    /**
     * A pseudorandom generator used to calculate the offset direction and magnitude.
     */
    private final Random random = new Random();

    /**
     * The current offset being applied, updated each frame.
     */
    private float currentOffset;

    /**
     * Constructs a new ShakeModifier with the specified strength and duration.
     *
     * @param strength the maximum offset range (positive and negative)
     * @param duration the total duration of the shake in seconds
     */
    public ShakeModifier(float strength, float duration) {
        this.strength = strength;
        this.duration = duration;
    }

    /**
     * Updates the modifier’s internal state and recalculates a new random offset if still active.
     *
     * @param deltaTime time passed since the last update (in seconds)
     */
    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        if (!isFinished()) {
            currentOffset = (random.nextFloat() * 2f - 1f) * strength;
        }
    }

    /**
     * Returns the current offset applied by the shake.
     *
     * @return the shake offset (randomized each frame), or 0 if finished
     */
    @Override
    public float getOffset() {
        return isFinished() ? 0f : currentOffset;
    }

    /**
     * Returns whether the shake effect has ended.
     *
     * @return true if the shake duration has passed, false otherwise
     */
    @Override
    public boolean isFinished() {
        return time >= duration;
    }

    /**
     * Creates a shallow copy of this ShakeModifier.
     * Note: the internal state (e.g., time) is not shared.
     *
     * @return a new instance with the same configuration
     * @throws CloneNotSupportedException if cloning is not supported
     */
    @Override
    public ShakeModifier clone() throws CloneNotSupportedException {
        return (ShakeModifier) super.clone();
    }
}
