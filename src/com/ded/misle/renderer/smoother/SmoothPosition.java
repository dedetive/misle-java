package com.ded.misle.renderer.smoother;

import static com.ded.misle.game.GamePanel.*;

public class SmoothPosition {
    private final SmoothValue x;
    private final SmoothValue y;

    public SmoothPosition(int x, int y) {
        this.x = new SmoothValue(x);
        this.y = new SmoothValue(y);
    }

    public void setTarget(int newX, int newY) {
        x.setTarget(newX * originalTileSize);
        y.setTarget(newY * originalTileSize);
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
