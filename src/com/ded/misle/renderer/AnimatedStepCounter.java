package com.ded.misle.renderer;

import java.awt.*;

public class AnimatedStepCounter {
    private int currentStep = 0;
    private float scale = 1f;
    private float yOffset = 0;
    private long lastUpdateTime = 0;

    private float minScale = 1f;
    private float maxScale = 2f;

    private static final float MAX_ALLOWED_SCALE = 3f;
    private static final float SCALE_INCREMENT = 0.05f;
    private static final float JUMP_HEIGHT = 16f;
    private static final long ANIMATION_DURATION = 300;

    public void updateStep(int step) {
        currentStep = step;

        minScale = Math.min(minScale + SCALE_INCREMENT, MAX_ALLOWED_SCALE);
        maxScale = Math.min(maxScale + SCALE_INCREMENT, MAX_ALLOWED_SCALE);

        scale = maxScale;
        yOffset = -JUMP_HEIGHT;
        lastUpdateTime = System.currentTimeMillis();
    }

    public void update() {
        long delta = System.currentTimeMillis() - lastUpdateTime;
        if (delta > ANIMATION_DURATION) {
            scale = minScale;
            yOffset = 0;
        } else {
            float progress = delta / (float) ANIMATION_DURATION;
            scale = maxScale - (maxScale - minScale) * progress;
            yOffset = -JUMP_HEIGHT * (1 - progress);
        }
    }

    public void draw(Graphics2D g2d, Font baseFont, int x, int y) {
        update();
        Font scaled = FontManager.getResizedFont(baseFont, baseFont.getSize() * scale);
        g2d.setFont(scaled);
        g2d.setColor(new Color(255, 210, 50, 255));
        String text = String.valueOf(currentStep);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        g2d.drawString(text, x - width / 2, y + (int) yOffset);
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void reset() {
        currentStep = 0;
        yOffset = 0;
        scale = 1f;
        maxScale = 2f;
        minScale = 1f;
    }
}