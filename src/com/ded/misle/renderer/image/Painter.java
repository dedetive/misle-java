package com.ded.misle.renderer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Painter {

    private final Palette palette;
    private boolean preserveAlpha = true;

    public Painter(Palette palette) {
        this.palette = palette;
    }

    public void setPreserveAlpha(boolean preserveAlpha) {
        this.preserveAlpha = preserveAlpha;
    }

    public BufferedImage paint(BufferedImage input) {

        Palette inputPalette = new Palette(input);

        if (this.palette.equals(inputPalette)) return input;

        List<Color> inputColors = inputPalette.asList();
        List<Color> targetColors = palette.asList();

        Map<Integer, Integer> colorMap = new HashMap<>();
        for (int i = 0; i < Math.min(inputColors.size(), targetColors.size()); i++) {
            colorMap.put(inputColors.get(i).getRGB(), targetColors.get(i).getRGB());
        }

        BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                int originalRGB = input.getRGB(x, y);
                Color targetColor = new Color(
                    colorMap.getOrDefault(
                        new Color(originalRGB, true)
                            .getRGB(), originalRGB),
                    true);

                int alpha = (originalRGB >> 24) & 0xFF;
                Color finalColor = preserveAlpha
                    ? new Color(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue(), alpha)
                    : targetColor;
                output.setRGB(x, y, finalColor.getRGB());
            }
        }

        return output;
    }
}