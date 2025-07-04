package com.ded.misle.renderer;

import com.ded.misle.world.entities.player.Planner;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.ded.misle.core.PraspomiaNumberConverter.*;

public class AnimatedStepCounter {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    private int currentStep = 0;
    private float damageMultiplier = 1.0f;
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
        damageMultiplier = Planner.planningMultiplier(step, 0);

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
        String text =
            impureConvertNumberSystem(
                df.format(damageMultiplier),
                ConvertMode.TO_PRASPOMIA) + "x";
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
            float normalizedStep = Math.min(currentStep / 100f, 1f);
            gImg.setColor(interpolateGradient(normalizedStep));
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

    private Color interpolateGradient(float t) {
        t = Math.max(0f, Math.min(1f, t));

        Color[] colors = {
            new Color(0x16FFEF),
            new Color(0x4FF95B),
            new Color(0xFFD232),
            new Color(0xFF781E),
            new Color(0xFF0000),
        };

        float segment = 1f / (colors.length - 1);
        int index = (int)(t / segment);

        if (index >= colors.length - 1) return colors[colors.length - 1];

        float localT = (t - (index * segment)) / segment;

        Color c1 = colors[index];
        Color c2 = colors[index + 1];

        int r = (int)(c1.getRed() * (1 - localT) + c2.getRed() * localT);
        int g = (int)(c1.getGreen() * (1 - localT) + c2.getGreen() * localT);
        int b = (int)(c1.getBlue() * (1 - localT) + c2.getBlue() * localT);

        return new Color(r, g, b);
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void reset() {
        currentStep = 0;
        damageMultiplier = 1.0f;
        yOffset = 0;
        scale = 1f;
        maxScale = 2f;
        minScale = 1f;
    }
}