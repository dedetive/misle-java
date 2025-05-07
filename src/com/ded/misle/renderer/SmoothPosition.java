package com.ded.misle.renderer;

import static com.ded.misle.core.GamePanel.deltaTime;
import static com.ded.misle.core.GamePanel.tileSize;

public class SmoothPosition {
    private float renderX = -1;
    private float renderY = -1;
    private int x;
    private int y;

    public SmoothPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setTarget(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(float speed, int tileSize) {
        float targetX = x * tileSize;
        float targetY = y * tileSize;

        if (renderX == -1) renderX = targetX;
        else if (Math.abs(renderX - targetX) >= 0.1f)
            renderX += (float) ((targetX - renderX) * deltaTime * speed);

        if (renderY == -1) renderY = targetY;
        else if (Math.abs(renderY - targetY) >= 0.1f)
            renderY += (float) ((targetY - renderY) * deltaTime * speed);
    }

    public int getRenderX() {
        return Math.round(renderX);
    }

    public int getRenderY() {
        return Math.round(renderY);
    }
}
