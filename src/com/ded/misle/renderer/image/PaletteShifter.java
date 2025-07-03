package com.ded.misle.renderer.image;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaletteShifter {

    private Palette palette;

    public PaletteShifter(Palette palette) {
        this.palette = palette;
    }

    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    //region ordering

    public Palette reversed() {
        List<Color> reversed = new ArrayList<>(palette.asList());
        Collections.reverse(reversed);
        this.palette = new Palette(reversed);
        return this.palette;
    }

    public Palette rotated(int offset) {
        List<Color> colors = new ArrayList<>(palette.asList());
        Collections.rotate(colors, offset);
        this.palette = new Palette(colors);
        return this.palette;
    }

    public Palette limited(int maxSize) {
        List<Color> sub = palette.asList().subList(0, Math.min(maxSize, palette.size()));
        this.palette = new Palette(sub);
        return this.palette;
    }

    public Palette shuffled() {
        List<Color> shuffled = new ArrayList<>(palette.asList());
        Collections.shuffle(shuffled);
        this.palette = new Palette(shuffled);
        return this.palette;
    }

    //endregion

    //region coloring

    public Palette hueShift(float degrees) {
        List<Color> shifted = palette.asList().stream()
            .map(c -> shiftHue(c, degrees))
            .toList();
        this.palette = new Palette(shifted);
        return this.palette;
    }

    private Color shiftHue(Color c, float degrees) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float hue = (hsb[0] + (degrees / 360f)) % 1f;
        int rgb = Color.HSBtoRGB(hue, hsb[1], hsb[2]);
        return new Color(rgb, true);
    }

    public Palette grayscale() {
        List<Color> grays = palette.asList().stream()
            .map(c -> {
                int gray = (int) (c.getRed() * 0.3 + c.getGreen() * 0.59 + c.getBlue() * 0.11);
                return new Color(gray, gray, gray, c.getAlpha());
            }).toList();
        this.palette = new Palette(grays);
        return this.palette;
    }

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

    public Palette transform(java.util.function.Function<Color, Color> transform) {
        List<Color> mapped = palette.asList().stream().map(transform).toList();
        this.palette = new Palette(mapped);
        return this.palette;
    }

    //endregion

    //region helper

    private static float clampInt(float value) {
        return Math.min(255, Math.max(0, Math.round(value * 255)));
    }

    private static float clampFloat(float value) {
        return Math.min(1f, Math.max(0f, value));
    }

    public PaletteShifter copy() {
        return new PaletteShifter(new Palette(new ArrayList<>(palette.asList())));
    }

    //endregion
}