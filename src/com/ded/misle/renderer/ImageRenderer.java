package com.ded.misle.renderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import static com.ded.misle.ChangeSettings.getPath;

public class ImageRenderer {
    public static final java.util.Map<ImageName, BufferedImage> cachedImages = new HashMap<>();

    public enum ImageName {
        INVENTORY_BAR("ui", "inventoryBar.png"),
        INVENTORY_MENU("ui", "inventoryBackground.png"),;

        ImageName(String category, String fileName) {
            Path basePath = getPath().resolve("resources/images/");
            Path fullPath = basePath.resolve(category + "/" + fileName);
            try {
                cachedImages.put(this, ImageIO.read(fullPath.toFile()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
