package com.ded.misle.renderer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Represents a color palette extracted from a {@link BufferedImage}, ordered by color frequency.
 * <p>
 * Each color in the palette corresponds to a unique pixel value found in the image,
 * with entries ordered from the most to the least frequent.
 * This allows deterministic mapping of colors by usage, which is useful for tasks such as
 * palette swapping, visual clustering, recoloring, or compression.
 * <p>
 * The palette is immutable once created and supports indexed access, equality comparison,
 * and conversion to an unmodifiable list.
 *
 * @see Painter
 */
public class Palette {

    /**
     * The list of unique colors, ordered by their frequency (most common first).
     */
    private final List<Color> palette;

    /**
     * Constructs a {@code Palette} by analyzing the frequency of each pixel color in the provided image.
     * <p>
     * All colors are included regardless of alpha value and transparency.
     * The resulting palette is ordered by descending pixel count.
     * <p>
     * When multiple color values have the same frequency, the order of appearance is given priority instead.
     *
     * @param img the image to extract unique colors and build the palette from
     */
    public Palette(BufferedImage img) {
        this.palette = PaletteMemorial.paletteMap.computeIfAbsent(
            img,
            (img_) -> {
                Map<Color, Integer> colorCountMap = new LinkedHashMap<>();

                for (int x = 0; x < img.getWidth(); x++) {
                    for (int y = 0; y < img.getHeight(); y++) {
                        Color color = new Color(img.getRGB(x, y), true);
                        colorCountMap.put(color, colorCountMap.getOrDefault(color, 0) + 1);
                    }
                }

                List<Map.Entry<Color, Integer>> entries = new ArrayList<>(colorCountMap.entrySet());
                entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

                return new Palette(entries.stream()
                    .map(Map.Entry::getKey)
                    .toList());
            }
        ).asList();
    }

    public Palette(List<Color> palette) {
        this.palette = List.copyOf(palette);
    }

    /**
     * Returns the color at the specified index in the palette.
     * <p>
     * Index {@code 0} corresponds to the most frequent color in the source image.
     * If the index is out of bounds, {@code null} is returned and an error is logged.
     *
     * @param index the position of the color in the palette
     * @return the color at that index, or {@code null} if out of range
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
     * @return the size of the palette (i.e., number of distinct colors)
     */
    public int size() {
        return palette.size();
    }

    /**
     * Returns an unmodifiable view of this palette's color list.
     * <p>
     * The list is ordered by descending frequency and reflects the palette's internal state.
     *
     * @return an unmodifiable list of the palette's colors
     */
    public List<Color> asList() {
        return palette;
    }

    /**
     * Returns a string representation of this palette, including its size and contents.
     *
     * @return a string describing the palette
     */
    @Override
    public String toString() {
        return "Palette{" +
            "size=" + palette.size() +
            ", colors=" + palette +
            '}';
    }

    /**
     * Compares this palette to another for equality.
     * <p>
     * Two palettes are considered equal if they contain the exact same colors
     * in the exact same order.
     *
     * @param obj the object to compare with
     * @return {@code true} if both palettes contain the same colors in order; {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Palette other)) return false;

        return this.palette.equals(other.palette);
    }

    /**
     * Returns a hash code for this palette, based on its internal color list.
     *
     * @return a hash code representing the palette
     */
    @Override
    public int hashCode() {
        return palette.hashCode();
    }
}
