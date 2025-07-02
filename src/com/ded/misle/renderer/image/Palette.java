package com.ded.misle.renderer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;

public class Palette {

    private final List<Color> palette;

    public Palette(BufferedImage img) {
        Set<Color> colorSet = new LinkedHashSet<>();

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color color = new Color(img.getRGB(x, y));
                colorSet.add(color);
            }
        }

        this.palette = new ArrayList<>(colorSet);
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
