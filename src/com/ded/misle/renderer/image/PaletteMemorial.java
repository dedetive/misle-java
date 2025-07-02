package com.ded.misle.renderer.image;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.WeakHashMap;

public class PaletteMemorial {

    private PaletteMemorial() {}

    public static final Map<BufferedImage, Palette> paletteMap = new WeakHashMap<>();


}
