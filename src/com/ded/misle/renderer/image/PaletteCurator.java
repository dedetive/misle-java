package com.ded.misle.renderer.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.awt.*;
import java.util.List;

import static com.ded.misle.core.Path.*;

public class PaletteCurator {

    private PaletteCurator() {}

    public static void generateFile(Palette palette, String fileName) {
        Path path = getPath(PathTag.PALETTES).resolve(fileName + ".png");

        BufferedImage image = new BufferedImage(palette.size(), 1, BufferedImage.TYPE_INT_ARGB);
        List<Color> asList = palette.asList();

        for (int i = 0; i < asList.size(); i++) {
            image.setRGB(i, 0, asList.get(i).getRGB());
        }

        try {
            Files.createDirectories(path.getParent());
            ImageIO.write(image, "png", path.toFile());
        } catch (IOException e) {
            System.err.println("Failed to save palette image: " + e.getMessage());
        }
    }

    public static Palette getPalette(String paletteName) {
        Path path = getPath(PathTag.PALETTES).resolve(paletteName + ".png");
        BufferedImage output = null;

        try {
            output = ImageIO.read(path.toFile());
        } catch (IOException e) {
            System.err.println("Failed to load palette image: " + e.getMessage());
        }

        return output != null
            ? new Palette(output)
            : null;
    }
}