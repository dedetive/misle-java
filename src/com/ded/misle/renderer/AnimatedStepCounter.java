package com.ded.misle.renderer;

import com.ded.misle.core.PraspomiaNumberConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

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

    private final Map<String, BufferedImage> renderCache = new HashMap<>();

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
        String text = PraspomiaNumberConverter.impureConvertNumberSystem(String.valueOf(currentStep), PraspomiaNumberConverter.ConvertMode.TO_PRASPOMIA);
        Font scaledFont = FontManager.getResizedFont(baseFont, baseFont.getSize() * scale);

        FontMetrics fm = FontManager.getCachedMetrics(g2d, scaledFont);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        String cacheKey = text + "@" + (int) scale;
        BufferedImage img = renderCache.get(cacheKey);

        if (img == null) {
            int scaleFactor = 3;
            int imgWidth = textWidth * scaleFactor + 8;
            int imgHeight = textHeight * scaleFactor + 8;

            img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gImg = img.createGraphics();

            gImg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gImg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gImg.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            gImg.scale(scaleFactor, scaleFactor);
            gImg.setFont(scaledFont);
            FontMetrics fmImg = gImg.getFontMetrics();

            int drawX = 2;
            int drawY = fmImg.getAscent() + 2;

            // Shadow
            gImg.setColor(new Color(0x002020));
            int shadowOffset = 1;
            for (int dx = -shadowOffset; dx <= shadowOffset; dx++) {
                for (int dy = -shadowOffset; dy <= shadowOffset; dy++) {
                    if (dx != 0 || dy != 0) {
                        gImg.drawString(text, drawX + dx, drawY + dy);
                    }
                }
            }

            // Text itself
            gImg.setColor(new Color(0x16FFEF));
            gImg.drawString(text, drawX, drawY);
            gImg.dispose();

            renderCache.put(cacheKey, img);
        }

        g2d.drawImage(
            img,
            x - textWidth / 2,
            y + (int) yOffset - textHeight / 2,
            textWidth,
            textHeight,
            null
        );
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