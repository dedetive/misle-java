package com.ded.misle.renderer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class Palette {

    private final List<Color> palette;

    public Palette(BufferedImage img) {
        Map<Color, Integer> colorCountMap = new HashMap<>();

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color color = new Color(img.getRGB(x, y), true);
                colorCountMap.put(color, colorCountMap.getOrDefault(color, 0) + 1);
            }
        }

        this.palette = colorCountMap.entrySet()
            .stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .map(Map.Entry::getKey)
            .toList();
    }

    public Color get(int index) {
        return palette.get(index);
    }

    public int size() {
        return palette.size();
    }

    public List<Color> asList() {
        return Collections.unmodifiableList(palette);
    }

    @Override
    public String toString() {
        return "Palette{" +
            "size=" + palette.size() +
            ", colors=" + palette +
            '}';
    }
}
