package com.ded.misle.renderer;

import com.ded.misle.core.PraspomiaNumberConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class AnimatedStepCounter {
    private int currentStep = 0;
    private float scale = 1f;
    private float yOffset = 0;
    private long lastUpdateTime = 0;

    private float minScale = 1f;
    private float maxScale = 2f;

    private static final float MAX_ALLOWED_SCALE = 3f;
    private static final float SCALE_INCREMENT = 0.1f;
    private static final float JUMP_HEIGHT = 10f;
    private static final long ANIMATION_DURATION = 300;

    public void updateStep(int step) {
        currentStep = step;

        minScale = Math.min(minScale + SCALE_INCREMENT, MAX_ALLOWED_SCALE);
        maxScale = Math.min(maxScale + SCALE_INCREMENT, MAX_ALLOWED_SCALE);

        scale = maxScale;
        yOffset = -JUMP_HEIGHT;
        lastUpdateTime = System.currentTimeMillis();
    }

    private void update() {
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

        Point dimensions = new Point((int) (baseFont.getSize() * scale), (int) (baseFont.getSize() * scale));
        BufferedImage img = new BufferedImage(dimensions.x, dimensions.y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gImg = img.createGraphics();
        gImg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        gImg.setFont(scaled);
        FontMetrics fm = FontManager.getCachedMetrics(gImg, scaled);

        String text = PraspomiaNumberConverter.impureConvertNumberSystem(String.valueOf(currentStep), PraspomiaNumberConverter.ConvertMode.TO_PRASPOMIA);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();


        int drawX = 2;
        int drawY = fm.getAscent() + 2;

        // Shadow
        gImg.setColor(Color.BLACK);
        int shadowOffset = 1;
        for (int dx = -shadowOffset; dx <= shadowOffset; dx++) {
            for (int dy = -shadowOffset; dy <= shadowOffset; dy++) {
                if (dx != 0 || dy != 0) {
                    gImg.drawString(text, drawX + dx, drawY + dy);
                }
            }
        }

        // Text itself
        gImg.setColor(new Color(0x06FFCC));
        gImg.drawString(text, drawX, drawY);

        gImg.dispose();

        g2d.drawImage(img, x - textWidth / 2, y + (int) yOffset - textHeight / 2, null);
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