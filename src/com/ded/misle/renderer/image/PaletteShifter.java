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

    //endregion
}