package com.ded.misle.renderer.image;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides a fluent and immutable-like interface for applying transformations to a {@link Palette}.
 * <p>
 * The {@code PaletteShifter} allows you to re-order, recolor, or filter palettes using common
 * operations such as hue shifting, gamma correction, saturation adjustment, and more.
 * <p>
 * All methods return the updated {@link Palette} after applying the transformation, and internally
 * replace the current palette reference. You can access the modified palette using {@link #getPalette()}.
 *
 * @see Palette
 * @see Painter
 */
public class PaletteShifter {

    /**
     * The current working palette being transformed.
     * <p>
     * This palette is overwritten after each transformation method call. To preserve previous
     * states, use {@link #copy()} to clone the current state before making changes.
     */
    private Palette palette;

    /**
     * Initializes a new {@code PaletteShifter} using the provided {@link Palette} as the initial state.
     * <p>
     * This class acts as a mutable utility wrapper that lets you apply sequential color transformations
     * (such as hue shifting, brightness changes, or reordering) in a functional-like way.
     * <p>
     * The internal {@code Palette} is replaced after each transformation. If you want to retain
     * earlier versions for branching behavior, use {@link #copy()}.
     *
     * @param palette the initial palette to operate on
     */
    public PaletteShifter(Palette palette) {
        this.palette = palette;
    }

    /**
     * Returns the current {@link Palette} state after one or more transformations.
     * <p>
     * This is typically passed to a {@link Painter} for recoloring an image with the transformed palette.
     *
     * @return the current working palette
     */
    public Palette getPalette() {
        return palette;
    }

    /**
     * Replaces the internal palette with a new one, cloning its color list to avoid shared references.
     * <p>
     * This is useful if you want to reset or swap palettes dynamically without side effects.
     *
     * @param palette the new {@link Palette} to set
     */
    public void setPalette(Palette palette) {
        this.palette = new Palette(palette.asList());
    }

    /**
     * Merges the current palette with another palette, adding all unique colors from the given palette.
     * <p>
     * Colors from the {@code other} palette are appended only if they are not already present
     * in the current palette, preserving the order of both palettes.
     * <p>
     * This method is useful for combining color sets, ensuring no duplicates, and extending
     * palettes with additional colors from other sources (e.g., theme overlays or dynamic palettes).
     * <p>
     * The original palette remains unchanged; the merged palette is returned as a new {@link Palette}.
     * The internal state of this {@code PaletteShifter} is also left unmodified.
     *
     * @param other the palette whose unique colors will be merged into the current palette
     * @return a new {@link Palette} containing colors from both palettes, without duplicates
     */
    public Palette mergedWith(Palette other) {
        List<Color> merged = new ArrayList<>(palette.asList());
        for (Color c : other.asList()) {
            if (!merged.contains(c)) merged.add(c);
        }
        this.palette = new Palette(merged);
        return this.palette;
    }

    //region ordering

    /**
     * Reverses the order of the colors in the current palette.
     * <p>
     * This can be used to invert gradients, change ramp directions, or simply produce a mirrored effect
     * when recoloring an image via {@link Painter}.
     *
     * @return the updated {@link Palette} with reversed color order
     */
    public Palette reversed() {
        List<Color> reversed = new ArrayList<>(palette.asList());
        Collections.reverse(reversed);
        this.palette = new Palette(reversed);
        return this.palette;
    }

    /**
     * Cyclically rotates the color order in the palette by a given number of positions.
     * <p>
     * Colors pushed off the end wrap around to the beginning. This is useful for creating
     * animated effects or palette cycling, especially in retro-style or procedural art.
     *
     * @param offset the number of positions to rotate the palette by (positive or negative)
     * @return the updated {@link Palette} after rotation
     */
    public Palette rotated(int offset) {
        List<Color> colors = new ArrayList<>(palette.asList());
        Collections.rotate(colors, offset);
        this.palette = new Palette(colors);
        return this.palette;
    }

    /**
     * Alias for {@link #rotated(int)}. Provided for more intuitive naming in some contexts.
     *
     * @param offset the number of positions to rotate the palette by (positive or negative)
     * @return the updated {@link Palette} after rotation
     */
    public Palette offset(int offset) {
        return rotated(offset);
    }

    /**
     * Truncates the palette to a maximum number of colors, preserving only the first ones.
     * <p>
     * This can help enforce a color budget for compression, dithering, or stylized restrictions.
     *
     * @param maxSize the maximum number of colors to retain
     * @return the updated {@link Palette} with limited colors
     */
    public Palette limited(int maxSize) {
        List<Color> sub = palette.asList().subList(0, Math.min(maxSize, palette.size()));
        this.palette = new Palette(sub);
        return this.palette;
    }

    /**
     * Randomizes the order of colors in the palette.
     * <p>
     * This can produce glitch art effects or test how color order influences rendering with {@link Painter}.
     *
     * @return the updated {@link Palette} with shuffled colors
     */
    public Palette shuffled() {
        List<Color> shuffled = new ArrayList<>(palette.asList());
        Collections.shuffle(shuffled);
        this.palette = new Palette(shuffled);
        return this.palette;
    }

    //endregion

    //region coloring

    /**
     * Rotates the hue of each color in the palette by a fixed number of degrees around the color wheel.
     * <p>
     * This operation preserves saturation and brightness while shifting the perceived color tint.
     * It's useful for palette variations like theme swapping (e.g., "fire" to "ice").
     *
     * @param degrees how much to rotate the hue (positive or negative, wrapped mod 360)
     * @return the updated {@link Palette} with shifted hues
     */
    public Palette hueShift(float degrees) {
        List<Color> shifted = palette.asList().stream()
            .map(c -> shiftHue(c, degrees))
            .toList();
        this.palette = new Palette(shifted);
        return this.palette;
    }

    /**
     * Shifts the hue of a single {@link Color} by a given amount in degrees, wrapping within the hue circle.
     * <p>
     * This function preserves the saturation and brightness of the color while rotating its hue component
     * around the HSB (Hue–Saturation–Brightness) color space. The hue shift is modular, meaning it wraps
     * around after 360° (e.g., 370° → 10°).
     * <p>
     * Used internally by {@link #hueShift(float)} to recolor entire palettes.
     *
     * @param c the color to shift
     * @param degrees how much to shift hue, in degrees (positive or negative)
     * @return a new {@link Color} with adjusted hue and original alpha preserved
     */
    private Color shiftHue(Color c, float degrees) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float hue = (hsb[0] + (degrees / 360f)) % 1f;
        int rgb = Color.HSBtoRGB(hue, hsb[1], hsb[2]);
        return new Color(rgb, true);
    }

    /**
     * Converts all palette colors to grayscale using standard luminance weights:
     * {@code 0.3*R + 0.59*G + 0.11*B}.
     * <p>
     * This method is helpful for debugging image structure, achieving desaturated styles,
     * or preparing data for contrast testing.
     *
     * @return the updated grayscale {@link Palette}
     */
    public Palette grayscale() {
        List<Color> grays = palette.asList().stream()
            .map(c -> {
                int gray = (int) (c.getRed() * 0.3 + c.getGreen() * 0.59 + c.getBlue() * 0.11);
                return new Color(gray, gray, gray, c.getAlpha());
            }).toList();
        this.palette = new Palette(grays);
        return this.palette;
    }

    /**
     * Applies gamma correction to all colors in the current palette.
     * <p>
     * Gamma correction is a non-linear operation that adjusts color intensity based on human
     * visual perception. It darkens or brightens midtones while preserving black and white points.
     * <p>
     * Each RGB channel is transformed independently using:
     * <pre>{@code
     *   corrected = pow(original / 255.0, gamma)
     * }</pre>
     * The resulting values are clamped to [0.0, 1.0] and scaled back to 8-bit.
     * Alpha is normalized and preserved.
     * <p>
     * Examples:
     * <ul>
     *     <li>{@code gamma = 1.0} → no change</li>
     *     <li>{@code gamma < 1.0} → brighter midtones</li>
     *     <li>{@code gamma > 1.0} → darker midtones</li>
     * </ul>
     *
     * @param gamma the gamma exponent (e.g., 2.2 for correction, 0.5 for boosting brightness)
     * @return the updated {@link Palette} after gamma correction
     */
    public Palette gamma(float gamma) {
        List<Color> result = palette.asList().stream().map(c -> {
            float r = (float) Math.pow(c.getRed() / 255.0, gamma);
            float g = (float) Math.pow(c.getGreen() / 255.0, gamma);
            float b = (float) Math.pow(c.getBlue() / 255.0, gamma);
            return new Color(clampFloat(r), clampFloat(g), clampFloat(b), c.getAlpha() / 255f);
        }).toList();
        this.palette = new Palette(result);
        return this.palette;
    }

    /**
     * Adjusts the saturation (color intensity) of each color in the palette.
     * <p>
     * This transformation converts each color to the HSB color space, multiplies the saturation
     * component by the given factor, clamps it to the [0.0, 1.0] range, and reconstructs the color.
     * Hue and brightness remain unchanged.
     * <p>
     * Common use cases:
     * <ul>
     *   <li>{@code multiplier = 1.0} → no change</li>
     *   <li>{@code multiplier > 1.0} → increases vibrancy, making colors more vivid</li>
     *   <li>{@code multiplier < 1.0} → desaturates colors, shifting toward gray or pastel tones</li>
     *   <li>{@code multiplier = 0.0} → full desaturation (grayscale with original brightness)</li>
     * </ul>
     *
     * @param multiplier scale factor applied to the saturation channel
     * @return the updated {@link Palette} after saturation adjustment
     */
    public Palette saturate(float multiplier) {
        List<Color> saturated = palette.asList().stream()
            .map(c -> {
                float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                hsb[1] = clampFloat(hsb[1] * multiplier);
                int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                return new Color(rgb, true);
            }).toList();
        this.palette = new Palette(saturated);
        return this.palette;
    }

    /**
     * Adjusts the brightness (value) of all colors in the palette without modifying hue or saturation.
     * <p>
     * This operation transforms each color into HSB space, scales the brightness component
     * by the specified multiplier, clamps it to the [0.0, 1.0] range, and converts it back to RGB.
     * <p>
     * Brightness scaling is linear and affects how light or dark each color appears.
     * <ul>
     *   <li>{@code multiplier = 1.0} → no change</li>
     *   <li>{@code multiplier > 1.0} → brightens the colors</li>
     *   <li>{@code multiplier < 1.0} → darkens the colors</li>
     * </ul>
     * This is useful for simulating lighting changes, visual transitions, or generating UI themes.
     *
     * @param multiplier the scaling factor for brightness (value)
     * @return the updated {@link Palette} with brightness adjusted
     */
    public Palette brightness(float multiplier) {
        List<Color> brightened = palette.asList().stream()
            .map(c -> {
                float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                hsb[2] = clampFloat(hsb[2] * multiplier);
                int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                return new Color(rgb, true);
            }).toList();
        this.palette = new Palette(brightened);
        return this.palette;
    }

    /**
     * Scales the alpha channel (transparency) of every color in the palette.
     * <p>
     * This is especially useful when blending sprites, UI, or effects with varying opacity levels.
     * The alpha is multiplied and clamped to remain within [0, 255].
     *
     * @param multiplier a scaling factor for transparency (0 = fully transparent, 1 = original)
     * @return the updated {@link Palette} with faded alpha values
     */
    public Palette fadeAlpha(float multiplier) {
        List<Color> faded = palette.asList().stream().map(c -> {
            int alpha = Math.round(c.getAlpha() * multiplier);
            return new Color(
                clampFloat(c.getRed() / 255f),
                clampFloat(c.getGreen() / 255f),
                clampFloat(c.getBlue() / 255f),
                clampFloat(alpha / 255f));
        }).toList();
        this.palette = new Palette(faded);
        return this.palette;
    }

    /**
     * Applies a custom transformation function to each color in the palette.
     * <p>
     * This enables full control over color remapping for advanced effects like thermal mapping,
     * procedural color distortion, or algorithmic stylization.
     *
     * @param transform a {@link java.util.function.Function} that receives and returns {@link Color}
     * @return the updated {@link Palette} after applying the transformation
     */
    public Palette transform(java.util.function.Function<Color, Color> transform) {
        List<Color> mapped = palette.asList().stream().map(transform).toList();
        this.palette = new Palette(mapped);
        return this.palette;
    }

    //endregion

    //region helper

    /**
     * Clamps a float in [0,1] to an integer value in [0,255].
     * <p>
     * Used internally for accurate color channel scaling.
     *
     * @param value the normalized float value
     * @return the clamped int in [0,255]
     */
    private static float clampInt(float value) {
        return Math.min(255, Math.max(0, Math.round(value * 255)));
    }

    /**
     * Clamps a float value to the range [0.0, 1.0].
     *
     * @param value the value to clamp
     * @return the clamped value
     */
    private static float clampFloat(float value) {
        return Math.min(1f, Math.max(0f, value));
    }

    /**
     * Returns a deep copy of this {@code PaletteShifter}, preserving the current palette state.
     * <p>
     * Useful to branch palette transformations without mutating the original.
     *
     * @return a cloned {@code PaletteShifter} with the same internal palette
     */
    public PaletteShifter copy() {
        return new PaletteShifter(new Palette(new ArrayList<>(palette.asList())));
    }

    //endregion
}