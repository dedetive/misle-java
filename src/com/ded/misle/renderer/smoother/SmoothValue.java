package com.ded.misle.renderer.smoother;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.ded.misle.game.GamePanel.deltaTime;

/**
 * A float-based value that smoothly interpolates toward a target over time, with optional modifiers applied.
 * <p>
 * This class is useful for UI animations, delayed updates, or any scenario where smooth transitions are preferred
 * over instant changes. Additionally, it supports temporary {@link ValueModifier}s that can add visual effects like
 * shaking or bouncing.
 */
public class SmoothValue {
    /**
     * The current internal value (before applying any modifier offset).
     */
    private float current;

    /**
     * The target value that the current value will interpolate toward.
     */
    private float target;

    /**
     * A list of active modifiers that apply temporary effects to the value.
     */
    private final List<ValueModifier> modifiers = new ArrayList<>();

    /**
     * Constructs a new SmoothValue starting at the given initial value.
     *
     * @param initial the initial and target value
     */
    public SmoothValue(float initial) {
        this.target = initial;
        this.current = 0;
        update(0);
    }

    /**
     * Sets the new target value to interpolate toward.
     *
     * @param target the target value
     */
    public void setTarget(float target) {
        this.target = target;
    }

    /**
     * Updates the current value toward the target and updates all active modifiers.
     * This should be called once per frame.
     *
     * @param speed the interpolation speed factor
     */
    public void update(float speed) {
        if (current == 0 && target != 0) {
            current = target;
        } else if (Math.abs(current - target) >= 0.1f) {
            current += (float)((target - current) * deltaTime * speed);
        }

        Iterator<ValueModifier> iter = modifiers.iterator();
        while (iter.hasNext()) {
            ValueModifier m = iter.next();
            m.update((float) deltaTime);
            if (m.isFinished()) iter.remove();
        }
    }

    /**
     * Adds a single modifier to this value. The modifier will be applied temporarily
     * and removed automatically when finished.
     *
     * @param modifier the modifier to apply
     */
    public void addModifier(ValueModifier modifier) {
        this.modifiers.add(modifier);
    }

    /**
     * Adds multiple modifiers to this value. Each will be managed independently.
     *
     * @param modifiers one or more modifiers to apply
     */
    public void addModifiers(ValueModifier... modifiers) {
        this.modifiers.addAll(Arrays.asList(modifiers));
    }

    /**
     * Returns the current interpolated float value, including any active modifier effects.
     *
     * @return the final float result
     */
    public float getCurrentFloat() {
        float sum = current;
        for (ValueModifier mod : modifiers) {
            sum += mod.getOffset();
        }
        return sum;
    }

    /**
     * Returns the current interpolated value as an integer, rounded from float.
     *
     * @return the final integer value with modifiers
     */
    public int getCurrentInt() {
        return Math.round(getCurrentFloat());
    }

    public void invalidate() {
        this.target = current;
    }
}
