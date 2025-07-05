package com.ded.misle.renderer.smoother;

public class SyncedValue {

    private float real;
    private final SmoothValue visual;

    public SyncedValue(float initial) {
        this.real = initial;
        this.visual = new SmoothValue(initial);
    }

    public void set(float value) {
        this.real = value;
        this.visual.setTarget(value);
    }

    public float getReal() {
        return real;
    }

    public float getVisual() {
        return visual.getCurrentFloat();
    }

    public int getVisualInt() {
        return visual.getCurrentInt();
    }

    public void update(float speed) {
        visual.update(speed);
    }

    public void addModifier(ValueModifier mod) {
        visual.addModifier(mod);
    }

    public void addModifier(ValueModifier... mod) {
        visual.addModifiers(mod);
    }

    public void invalidateVisual() {
        visual.invalidate();
    }
}