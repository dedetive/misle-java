package com.ded.misle.renderer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Represents a unique color palette extracted from a {@link BufferedImage}, ordered by frequency.
 * <p>
 * Colors are sorted from the most to the least frequent, based on how many times each appears in the image.
 */
public class Palette {

    /**
     * The list of unique colors, ordered by their frequency (most common first).
     */
    private final List<Color> palette;

    /**
     * Constructs a {@code Palette} from the given image by counting pixel occurrences of each color,
     * and sorting the result in descending order of frequency.
     *
     * @param img the image to extract the palette from
     */
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

    /**
     * Returns the color at the specified index in the palette.
     *
     * @param index the index of the color to retrieve
     * @return the {@link Color} at the given index, or null if given index is invalid
     */
    public Color get(int index) {
        if (index < 0 || index >= palette.size()) {
            System.err.println("Palette index out of range: " + index + ", " + palette.size());
            return null;
        }
        return palette.get(index);
    }

    /**
     * Returns the number of unique colors in this palette.
     *
     * @return the size of the palette
     */
    public int size() {
        return palette.size();
    }

    /**
     * Returns the entire palette as an unmodifiable list.
     *
     * @return an unmodifiable list of colors
     */
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Palette other)) return false;

        return this.palette.equals(other.palette);
    }

    @Override
    public int hashCode() {
        return palette.hashCode();
    }
}
