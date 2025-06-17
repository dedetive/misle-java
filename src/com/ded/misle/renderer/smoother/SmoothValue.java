package com.ded.misle.renderer.smoother;

import static com.ded.misle.game.GamePanel.deltaTime;

public class SmoothValue {
    private float current;
    private float target;

    public SmoothValue(float initial) {
        this.target = initial;
        this.current = -1;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public void update(float speed) {
        if (current == -1) {
            current = target;
        } else if (Math.abs(current - target) >= 0.1f) {
            current += (float)((target - current) * deltaTime * speed);
        }
    }

    public float getCurrentFloat() {
        return current;
    }

    public int getCurrentInt() {
        return Math.round(current);
    }
}
