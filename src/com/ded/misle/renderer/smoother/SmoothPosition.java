package com.ded.misle.renderer.smoother;

import static com.ded.misle.game.GamePanel.*;

public class SmoothPosition {
    private final SmoothValue x;
    private final SmoothValue y;

    public SmoothPosition(int x, int y) {
        this.x = new SmoothValue(x);
        this.y = new SmoothValue(y);
    }

    public SmoothPosition(int x, int y, int scale) {
        this.x = new SmoothValue(x * scale);
        this.y = new SmoothValue(y * scale);
    }

    public void setTarget(int newX, int newY, int scale) {
        x.setTarget(newX * scale);
        y.setTarget(newY * scale);
    }

    public void setTarget(int newX, int newY) {
        setTarget(newX, newY, 1);
    }

    public void update(float speed) {
        x.update(speed);
        y.update(speed);
    }

    public int getRenderX() {
        return x.getCurrentInt();
    }

    public int getRenderY() {
        return y.getCurrentInt();
    }
}
