package com.ded.misle.renderer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;

import static com.ded.misle.core.LanguageManager.getCurrentScript;

public abstract class FontManager {
    private static final HashMap<String, Font> fontCache = new HashMap<>();
    private static final HashMap<Font, FontMetrics> metricsCache = new HashMap<>();

    public static FontMetrics getCachedMetrics(Graphics g, Font font) {
        return metricsCache.computeIfAbsent(font, g::getFontMetrics);
    }


    /**
     *  Comfortaa, Ubuntu -> Supports Latin, Cyrillic and Greek<p>
     *  Basic -> Supports only Latin<p>
     *  NotoSansSC -> Supports Simplified Han, Hiragana, Katakana, Latin, Cyrillic, Greek and Hangul<p>
     *  Praspomia -> Supports only Praspomia (uses Greek characters), converts Latin into Praspomia equivalents (though limited)
     *
     */

    public static Font titleFont = loadFont("Comfortaa-SemiBold.ttf", 26);
    public static Font selectedItemNameFont = loadFont("Ubuntu-Medium.ttf", 9);
    public static Font itemCountFont = loadFont("Ubuntu-Regular.ttf", 13);
    public static Font buttonFont = loadFont("Ubuntu-Medium.ttf", 12);
    public static Font itemInfoFont = loadFont("Basic-Regular.ttf", 10);
    public static Font dialogNPCName = loadFont("Ubuntu-Medium.ttf", 23);
    public static Font dialogNPCText = loadFont("Ubuntu-Regular.ttf", 14);
    public static Font coinTextFont = loadFont("Comfortaa-SemiBold.ttf", 19);
    public static Font backupAdvisorFont = loadFont("Ubuntu-Medium.ttf", 6);
    public static Font plannerCounter = loadFont("Ubuntu-Medium.ttf", 30);

    private static final HashMap<Font, Integer> fontToSize = new HashMap<>(){{
        put(titleFont, 26);
        put(selectedItemNameFont, 9);
        put(itemCountFont, 13);
        put(buttonFont, 12);
        put(itemInfoFont, 10);
        put(dialogNPCName, 23);
        put(dialogNPCText, 14);
        put(coinTextFont, 19);
        put(backupAdvisorFont, 6);
        put(plannerCounter, 30);
    }};

    private static int getSize(Font font) {
        if (!fontToSize.containsKey(font)) {
            fontToSize.put(font, font.getSize());
        }
        return fontToSize.getOrDefault(font, 0);
    }

    public static Font getResizedFont(Font font, float size) {
        return fontCache.computeIfAbsent(String.valueOf(font) + Math.floor(size),
            (e) -> font.deriveFont((float) (int) size));
    }

    static {
        updateFontScript();
    }

    public static Font loadFont(String fontPath, int size) {
        String key = fontPath + ":" + size;
        if (fontCache.containsKey(key)) {
            return fontCache.get(key);
        }

        String fullFontPath = "/resources/fonts/" + fontPath;

        try (InputStream is = FontManager.class.getResourceAsStream(fullFontPath)) {
            if (is == null) {
                System.err.println("Font not found: " + fullFontPath);
                Font fallback = new Font("Dialog", Font.PLAIN, size);
                fontCache.put(key, fallback);
                return fallback;
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont((float) size);
            fontCache.put(key, font);
            return font;
        } catch (FontFormatException | IOException e) {
            Font fallback = new Font("Dialog", Font.PLAIN, size);
            fontCache.put(key, fallback);
            return fallback;
        }
    }


    public static void updateFontScript() {
        fontCache.clear();
        metricsCache.clear();

        coinTextFont = loadFont("Comfortaa-SemiBold.ttf", getSize(coinTextFont));
        itemCountFont = loadFont("Ubuntu-Regular.ttf", getSize(itemCountFont));
        switch (getCurrentScript()) {
            case LATIN -> {
                titleFont = loadFont("Comfortaa-SemiBold.ttf", getSize(titleFont));
                selectedItemNameFont = loadFont("Ubuntu-Medium.ttf", getSize(selectedItemNameFont));
                itemInfoFont = loadFont("Basic-Regular.ttf", getSize(itemCountFont));
                buttonFont = loadFont("Ubuntu-Medium.ttf", getSize(buttonFont));
                dialogNPCName = loadFont("Ubuntu-Medium.ttf", getSize(dialogNPCName));
                dialogNPCText = loadFont("Ubuntu-Regular.ttf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("Ubuntu-Medium.ttf", getSize(backupAdvisorFont));
                plannerCounter = loadFont("Ubuntu-Medium.ttf", getSize(plannerCounter));
            }
            case GREEK, CYRILLIC -> {
                titleFont = loadFont("Comfortaa-SemiBold.ttf", getSize(titleFont));
                selectedItemNameFont = loadFont("Ubuntu-Medium.ttf", getSize(selectedItemNameFont));
                buttonFont = loadFont("Ubuntu-Medium.ttf", getSize(buttonFont));
                itemInfoFont = loadFont("Ubuntu-Medium.ttf", getSize(itemInfoFont));
                dialogNPCName = loadFont("Ubuntu-Medium.ttf", getSize(dialogNPCName));
                dialogNPCText = loadFont("Ubuntu-Regular.ttf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("Ubuntu-Medium.ttf", getSize(backupAdvisorFont));
                plannerCounter = loadFont("Ubuntu-Medium.ttf", getSize(plannerCounter));
            }
            case SIMPLIFIED_HAN -> {
                titleFont = loadFont("NotoSansSC-SemiBold.ttf", getSize(titleFont));
                selectedItemNameFont = loadFont("NotoSansSC-Regular.ttf", getSize(selectedItemNameFont));
                itemInfoFont = loadFont("NotoSansSC-Regular.ttf", getSize(itemInfoFont));
                buttonFont = loadFont("NotoSansSC-SemiBold.ttf", getSize(buttonFont));
                dialogNPCName = loadFont("NotoSansSC-SemiBold.ttf", getSize(dialogNPCName));
                dialogNPCText = loadFont("NotoSansSC-Regular.ttf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("NotoSansSC-Regular.ttf", getSize(backupAdvisorFont));
                plannerCounter = loadFont("NotoSansSC-Regular.ttf", getSize(plannerCounter));
            }
            case PRASPOMIC -> {
                titleFont = loadFont("Praspomia-Regular.otf", getSize(titleFont));
                selectedItemNameFont = loadFont("Praspomia-Regular.otf", getSize(selectedItemNameFont));
                itemInfoFont = loadFont("Praspomia-Regular.otf", getSize(itemInfoFont));
                buttonFont = loadFont("Praspomia-Regular.otf", getSize(buttonFont));
                dialogNPCName = loadFont("Praspomia-Regular.otf", getSize(dialogNPCName));
                dialogNPCText = loadFont("Praspomia-Regular.otf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("Praspomia-Regular.otf", getSize(backupAdvisorFont));
                plannerCounter = loadFont("Praspomia-Regular.otf", getSize(plannerCounter));
                coinTextFont = loadFont("Praspomia-Regular.otf", 17);
                itemCountFont = loadFont("Praspomia-Regular.otf", 13);
            }
        }
    }
}
