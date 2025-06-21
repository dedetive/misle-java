package com.ded.misle.renderer.smoother;

public class SyncedValue {

    private float real;
    private final SmoothValue visual;

    public SyncedValue(float initial) {
        this.real = initial;
        this.visual = new SmoothValue(initial);
    }
}