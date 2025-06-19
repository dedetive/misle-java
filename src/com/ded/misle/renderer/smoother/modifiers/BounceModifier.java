package com.ded.misle.renderer.smoother.modifiers;

import com.ded.misle.renderer.smoother.ValueModifier;

public class BounceModifier implements ValueModifier {
    private float time;
    private final float duration;
    private final float amplitude;
    private final float frequency;

    public BounceModifier(float amplitude, float duration, float frequency) {
        this.amplitude = amplitude;
        this.duration = duration;
        this.frequency = frequency;
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
    }

    @Override
    public float getOffset() {
        if (time >= duration) return 0f;

        float progress = time / duration;
        float dampened = (float) (amplitude * Math.exp(-3 * progress));
        return (float) (dampened * Math.sin(frequency * Math.PI * time));
    }

    @Override
    public boolean isFinished() {
        return time >= duration;
    }
}
