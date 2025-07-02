package com.ded.misle.renderer.image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static com.ded.misle.game.GamePanel.getWindow;
import static com.ded.misle.core.Path.getPath;
import static com.ded.misle.renderer.ColorManager.getRandomColor;
import static java.nio.file.Files.createDirectories;

public abstract class ImageManager {
    public static final Map<ImageName, BufferedImage> cachedImages = new HashMap<>();
    public static final ArrayList<ImageName> playerImages = new ArrayList<>();

    private static final Map<EditKey, BufferedImage> colorEditCache = new WeakHashMap<>();
    private static final Map<MergeKey, BufferedImage> mergeCache = new WeakHashMap<>();

    static {
        Collections.addAll(playerImages,
            ImageName.PLAYER_FRONT0_EDIT, ImageName.PLAYER_FRONT1_EDIT,
            ImageName.PLAYER_WALK0_EDIT, ImageName.PLAYER_WALK1_EDIT, ImageName.PLAYER_WALK2_EDIT);
    }

    public enum ImageName {
        // UI
            INVENTORY_BAR("ui", "inventory_bar.png"),
            INVENTORY_MENU("ui", "inventory_background.png"),
            INVENTORY_RINGLESS_EXTRA_SLOT("ui", "inventory_ringless_extra_slot.png"),
            COIN("ui", "coin.png"),
            MAIN_MENU_BACKGROUND("ui", "main_menu_background.png"),
            ENEMY_HEALTH_BAR("ui", "enemy_health_bar.png"),
            ENEMY_HEALTH_BAR_INSIDE("ui", "enemy_health_bar_inside.png"),

        // CHARACTER
            // PLAYER
                PLAYER_FRONT0("characters/player", "player_front0.png"),
                PLAYER_FRONT1("characters/player", "player_front1.png"),
                PLAYER_WALK0("characters/player", "player_walk0.png"),
                PLAYER_WALK1("characters/player", "player_walk1.png"),
                PLAYER_WALK2("characters/player", "player_walk2.png"),
                PLAYER_FRONT0_EDIT("characters/player", "player_front0.png"),
                PLAYER_FRONT1_EDIT("characters/player", "player_front1.png"),
                PLAYER_WALK0_EDIT("characters/player", "player_walk0.png"),
                PLAYER_WALK1_EDIT("characters/player", "player_walk1.png"),
                PLAYER_WALK2_EDIT("characters/player", "player_walk2.png"),

        ;

        final Palette palette;

        ImageName(String category, String fileName) {
            Path basePath = getPath(com.ded.misle.core.Path.PathTag.RESOURCES).resolve("images");
            Path fullPath = basePath.resolve(category + "/" + fileName);
            try {
                cachedImages.put(this, ImageIO.read(fullPath.toFile()));
                this.palette = new Palette(cachedImages.get(this));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static BufferedImage requestImage() {
        File file = null;
        BufferedImage img;

        FileDialog fd = new FileDialog(getWindow(), "Choose an image", FileDialog.LOAD);
        fd.setFile("*.png");
        fd.setVisible(true);

        String result = fd.getFile();
        boolean exists = result != null;
        if (exists) {
            file = fd.getFiles()[0];
        }

        try {
            assert file != null;
            img = ImageIO.read(file);
        } catch (IOException | IllegalArgumentException e) {
            //
            return null;
        }

        return img;
    }

    public static BufferedImage editImageColor(BufferedImage img, Color color) {
        EditKey key = new EditKey(img, color);
        if (colorEditCache.containsKey(key)) return colorEditCache.get(key);

        BufferedImage copy = deepCopy(img);

        for (int i = 0; i < copy.getWidth(); i++) {
            for (int j = 0; j < copy.getHeight(); j++) {
                if (copy.getRGB(i, j) != 16777215) {
                    copy.setRGB(i, j, color.getRGB());
                }
            }
        }

        colorEditCache.put(key, copy);
        return copy;
    }

    public static BufferedImage randomizeImageColors(BufferedImage img) {
        return editImageColor(img, getRandomColor());
    }

    public static BufferedImage mergeImages(BufferedImage img, BufferedImage target) {
        MergeKey key = new MergeKey(img, target);
        if (mergeCache.containsKey(key)) return mergeCache.get(key);

        BufferedImage copy = deepCopy(img);

        Image targetImage = target.getScaledInstance(copy.getWidth(), copy.getHeight(), Image.SCALE_DEFAULT);
        int width = targetImage.getWidth(null);
        int height = targetImage.getHeight(null);
        target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        target.getGraphics().drawImage(targetImage, 0, 0, null);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (copy.getRGB(i, j) != 16777215 &&
                    target.getRGB(i, j) != 0) {
                    copy.setRGB(i, j, target.getRGB(i, j));
                }
            }
        }

        mergeCache.put(key, copy);
        return copy;
    }

    static BufferedImage deepCopy(BufferedImage img) {
        ColorModel cm = img.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = img.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static BufferedImage getCurrentScreen() {
        // Image getter
        JFrame frame = getWindow();
        BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        frame.printAll(img.getGraphics());

        return img;
    }

    private final static Path screenshotDirectory = getPath(com.ded.misle.core.Path.PathTag.RESOURCES).resolve("screenshots");

    public static void saveScreenshot(BufferedImage img) {
        try {
            // File creator
            String t = LocalDateTime.now().toString();
            t = t.substring(0, t.indexOf("."));
            t = t.replace("T", ".");

            createDirectories(screenshotDirectory);
            ImageIO.write(img, "png", (screenshotDirectory.resolve(t + ".png")).toFile());

        } catch (IOException e) {
            System.out.println("Failed to take a screenshot");
        }
    }

    private record EditKey(BufferedImage img, Color color) {}
    private record MergeKey(BufferedImage img, BufferedImage overlay) {}
}
