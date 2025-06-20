package com.ded.misle.renderer.smoother.modifiers;

import com.ded.misle.renderer.smoother.ValueModifier;

import java.util.Random;

public class ShakeModifier implements ValueModifier {
    private final float strength;
    private final float duration;
    private float time;
    private final Random random = new Random();
    private float currentOffset;

    public ShakeModifier(float strength, float duration) {
        this.strength = strength;
        this.duration = duration;
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        if (!isFinished()) {
            currentOffset = (random.nextFloat() * 2f - 1f) * strength;
        }
    }

    @Override
    public float getOffset() {
        return isFinished() ? 0f : currentOffset;
    }

    @Override
    public boolean isFinished() {
        return time >= duration;
    }

    @Override
    public ShakeModifier clone() throws CloneNotSupportedException {
        return (ShakeModifier) super.clone();
    }
}

