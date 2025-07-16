package com.ded.misle.renderer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * A pixel-wise image recoloring tool that remaps the colors of a {@link BufferedImage}
 * to a new target palette, optionally preserving alpha transparency.
 * <p>
 * The {@code Painter} class is designed to perform color substitutions based on palette index positions.
 * It compares the palette of the input image with a target {@link Palette}, and replaces each pixel's color
 * accordingly. The input palette is generated at runtime from the unique colors found in the image,
 * and each of those is mapped to the color of the same index in the target palette.
 *
 * <p>
 * This is commonly used for tasks such as:
 * <ul>
 *     <li>Swapping character outfits, team colors, or variants</li>
 *     <li>Recoloring tilesets and environment elements based on biome or time of day</li>
 *     <li>Dynamic palette swapping in low-memory systems or stylized graphics</li>
 * </ul>
 *
 * <p>
 * If the input and target palettes are considered equal (via {@link Palette#equals(Object)}),
 * then no recoloring is applied and the original image is returned unchanged.
 *
 * <p>
 * Color mapping respects index order. If the target palette is smaller than the input's palette,
 * only up to {@code min(input.size, target.size)} colors are remapped. Any remaining input colors
 * without a mapped target are retained unmodified in the final image.
 * <p>
 * Transparency (alpha) is handled by the {@link #preserveAlpha} flag:
 * <ul>
 *     <li>If {@code true}, the alpha channel from the input pixel is retained, and only RGB values are changed</li>
 *     <li>If {@code false}, the full RGBA value of the mapped palette color is applied</li>
 * </ul>
 *
 * @see Palette
 */
public class Painter {

    /**
     * The palette to which all input image colors will be mapped.
     * This target palette defines the final color outputs after recoloring.
     * Can be changed at runtime using {@link #setPalette(Palette)}.
     */
    private Palette palette;

    /**
     * Defines whether the alpha (transparency) of each pixel in the input image
     * should be preserved during recoloring.
     * <p>
     * If {@code true}, the original alpha channel is retained and only RGB values are modified.
     * If {@code false}, the resulting pixel will inherit both color and alpha from the target palette.
     * <p>
     * Default value is {@code true}.
     */
    private boolean preserveAlpha = true;

    /**
     * Constructs a new {@code Painter} instance that uses the given {@link Palette}
     * as the target for recoloring operations.
     *
     * @param palette the palette to use for remapping colors
     */
    public Painter(Palette palette) {
        this.palette = palette;
    }

    /**
     * Sets whether alpha (transparency) should be preserved during the recoloring process.
     *
     * @param preserveAlpha {@code true} to retain the original alpha of each pixel,
     *                      {@code false} to use the alpha of the mapped palette color
     */
    public void setPreserveAlpha(boolean preserveAlpha) {
        this.preserveAlpha = preserveAlpha;
    }


    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    /**
     * Recolors the given {@link BufferedImage} by mapping each unique input color
     * to a corresponding color in the target palette.
     * <p>
     * If the target and input palettes are identical, the original image is returned without changes.
     * <p>
     * The remapping is performed based on palette index position:
     * color at index {@code i} in the input is mapped to color at index {@code i} in the target.
     * <p>
     * If the input palette has more colors than the target, only the colors within the shared range
     * are recolored; unmatched colors are preserved.
     * <p>
     * Alpha channel handling is determined by the {@link #preserveAlpha} flag.
     *
     * @param input the image to recolor
     * @return a new {@link BufferedImage} with its colors remapped to the target palette,
     *         or the original image if no changes are necessary
     */
    public BufferedImage paint(BufferedImage input) {

        Palette inputPalette = new Palette(input);

        if (this.palette.equals(inputPalette)) return input;

        List<Color> inputColors = inputPalette.asList();
        List<Color> targetColors = palette.asList();
        if (targetColors.isEmpty()) return input;

        Map<Integer, Integer> colorMap = new HashMap<>();
        for (int i = 0; i < inputColors.size(); i++) {
            if (targetColors.size() <= i) colorMap.put(inputColors.get(i).getRGB(), targetColors.get(i % targetColors.size()).getRGB());
            else colorMap.put(inputColors.get(i).getRGB(), targetColors.get(i).getRGB());
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