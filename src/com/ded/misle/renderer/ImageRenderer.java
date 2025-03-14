package com.ded.misle.renderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.ded.misle.core.SettingsManager.getPath;
import static com.ded.misle.renderer.ColorManager.getRandomColor;

public class ImageRenderer {
    public static final java.util.Map<ImageName, BufferedImage> cachedImages = new HashMap<>();
    public static final java.util.Map<ImageName, BufferedImage> editedImages = new HashMap<>();
    public static final java.util.ArrayList<ImageName> playerImages = new ArrayList<>();

    static {
        Collections.addAll(playerImages,
            ImageName.PLAYER_FRONT0, ImageName.PLAYER_FRONT1,
            ImageName.PLAYER_WALK0, ImageName.PLAYER_WALK1, ImageName.PLAYER_WALK2);
    }

    public enum ImageName {
        // UI
            INVENTORY_BAR("ui", "inventory_bar.png"),
            INVENTORY_MENU("ui", "inventory_background.png"),
            INVENTORY_RINGLESS_EXTRA_SLOT("ui", "inventory_ringless_extra_slot.png"),
            COIN("ui", "coin.png"),
            MAIN_MENU_BACKGROUND("ui", "main_menu_background.png"),

        // CHARACTER
            // PLAYER
                PLAYER_FRONT0("characters/player", "player_front0.png"),
                PLAYER_FRONT1("characters/player", "player_front1.png"),
                PLAYER_WALK0("characters/player", "player_walk0.png"),
                PLAYER_WALK1("characters/player", "player_walk1.png"),
                PLAYER_WALK2("characters/player", "player_walk2.png"),

        ;

        ImageName(String category, String fileName) {
            Path basePath = getPath().resolve("resources/images/");
            Path fullPath = basePath.resolve(category + "/" + fileName);
            try {
                cachedImages.put(this, ImageIO.read(fullPath.toFile()));
                editedImages.put(this, ImageIO.read(fullPath.toFile()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void editImageColor(Color color) {
            BufferedImage img = cachedImages.get(this);
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    if (img.getRGB(i, j) != 16777215) {
                        img.setRGB(i, j, color.getRGB());
                    }
                }
            }
            editedImages.put(this, img);
        }

        public void mergeImages(BufferedImage target) {
            BufferedImage img = cachedImages.get(this);
            Image targetImage = target.getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_DEFAULT);
            int width = targetImage.getWidth(null);
            int height = targetImage.getHeight(null);
            target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            target.getGraphics().drawImage(targetImage, 0, 0, null);

            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    if (img.getRGB(i, j) != 16777215 &&
                        target.getRGB(i, j) != 0) {
                        img.setRGB(i, j, target.getRGB(i, j));
                    }
                }
            }
            editedImages.put(this, img);
        }

        public void randomizeImageColors() {
            this.editImageColor(getRandomColor());
        }
    }
}
