package com.ded.misle.renderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import static com.ded.misle.core.SettingsManager.getPath;

public class ImageRenderer {
    public static final java.util.Map<ImageName, BufferedImage> cachedImages = new HashMap<>();

    public enum ImageName {
        INVENTORY_BAR("ui", "inventory_bar.png"),
        INVENTORY_MENU("ui", "inventory_background.png"),
        INVENTORY_RINGLESS_EXTRA_SLOT("ui", "inventory_ringless_extra_slot.png"),
        COIN("ui", "coin.png"),

        ;

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
