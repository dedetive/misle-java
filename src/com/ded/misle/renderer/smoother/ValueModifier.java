package com.ded.misle.renderer.smoother;

/**
 * Represents a temporary effect that modifies a value over time.
 * <p>
 * This interface is intended to be used with value smoothing systems
 * such as {@link SmoothValue}, allowing dynamic visual behaviors
 * like shaking, bouncing, or other temporal distortions to come.
 * <p>
 * Modifiers are additive and optional, and are removed once finished.
 */
public interface ValueModifier extends Cloneable {

    /**
     * Updates the internal state of this modifier.
     * This is usually used to progress time-dependent effects.
     *
     * @param deltaTime the time elapsed since the last update, in seconds
     */
    void update(float deltaTime);

    /**
     * Returns the current offset this modifier contributes to the base value.
     * This offset is additive and can be positive or negative.
     *
     * @return the current offset value applied by this modifier
     */
    float getOffset();

    /**
     * Indicates whether this modifier has finished and should be removed.
     *
     * @return true if the modifier is finished, false otherwise
     */
    boolean isFinished();

    /**
     * Creates a deep copy of this modifier.
     * This is useful when assigning modifiers to independent value instances.
     *
     * @return a cloned copy of this modifier
     * @throws CloneNotSupportedException if cloning is not supported
     */
    ValueModifier clone() throws CloneNotSupportedException;
}
