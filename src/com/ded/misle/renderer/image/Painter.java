package com.ded.misle.renderer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Painter {

    private final Palette palette;

    public Painter(Palette palette) {
        this.palette = palette;
    }

    public BufferedImage paint(BufferedImage input) {

        Palette inputPalette = new Palette(input);

        if (this.palette.equals(inputPalette)) return input;
        if (inputPalette.size() > palette.size()) {
            System.err.println("Invalid palette size: " + inputPalette.size() + " > " + palette.size());
            return input;
        }

        List<Color> inputColors = inputPalette.asList();
        List<Color> targetColors = palette.asList();

        Map<Integer, Integer> colorMap = new HashMap<>();
        for (int i = 0; i < inputColors.size(); i++) {
            colorMap.put(inputColors.get(i).getRGB(), targetColors.get(i).getRGB());
        }

        BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                int rgb = input.getRGB(x, y);
                output.setRGB(x, y, colorMap.getOrDefault(rgb, rgb));
            }
        }

        return output;
    }

}