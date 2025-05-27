package com.ded.misle.renderer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
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

    public static Font titleFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", 26);
    public static Font selectedItemNameFont = loadFont("/fonts/Ubuntu-Medium.ttf", 9);
    public static Font itemCountFont = loadFont("/fonts/Ubuntu-Regular.ttf", 13);
    public static Font buttonFont = loadFont("/fonts/Ubuntu-Medium.ttf", 12);
    public static Font itemInfoFont = loadFont("/fonts/Basic-Regular.ttf", 10);
    public static Font dialogNPCName = loadFont("/fonts/Ubuntu-Medium.ttf", 23);
    public static Font dialogNPCText = loadFont("/fonts/Ubuntu-Regular.ttf", 14);
    public static Font coinTextFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", 19);
    public static Font backupAdvisorFont = loadFont("/fonts/Ubuntu-Medium.ttf", 6);
    public static Font plannerCounter = loadFont("/fonts/Ubuntu-Medium.ttf", 20);

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
        put(plannerCounter, 20);
    }};

    private static int getSize(Font font) {
        if (!fontToSize.containsKey(font)) {
            fontToSize.put(font, font.getSize());
        }
        return fontToSize.getOrDefault(font, 0);
    }

    public static Font getResizedFont(Font font, float size) {
        if (fontCache.containsKey(String.valueOf(font)) &&
        getSize(fontCache.get(String.valueOf(font))) == (int) size) {
            return font;
        } else {
            return fontCache.put(String.valueOf(font.deriveFont(size)), font);
        }
    }

    static {
        updateFontScript();
    }

    public static Font loadFont(String fontPath, int size) {
        String key = fontPath + ":" + size;
        if (fontCache.containsKey(key)) {
            return fontCache.get(key);
        }

        try (InputStream is = FontManager.class.getResourceAsStream(fontPath)) {
            if (is == null) {
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

        coinTextFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", getSize(coinTextFont));
        itemCountFont = loadFont("/fonts/Ubuntu-Regular.ttf", getSize(itemCountFont));
        switch (getCurrentScript()) {
            case LATIN -> {
                titleFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", getSize(titleFont));
                selectedItemNameFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(selectedItemNameFont));
                itemInfoFont = loadFont("/fonts/Basic-Regular.ttf", getSize(itemCountFont));
                buttonFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(buttonFont));
                dialogNPCName = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(dialogNPCName));
                dialogNPCText = loadFont("/fonts/Ubuntu-Regular.ttf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(backupAdvisorFont));
                plannerCounter = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(plannerCounter));
            }
            case GREEK, CYRILLIC -> {
                titleFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", getSize(titleFont));
                selectedItemNameFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(selectedItemNameFont));
                buttonFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(buttonFont));
                itemInfoFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(itemInfoFont));
                dialogNPCName = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(dialogNPCName));
                dialogNPCText = loadFont("/fonts/Ubuntu-Regular.ttf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(backupAdvisorFont));
                plannerCounter = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(plannerCounter));
            }
            case SIMPLIFIED_HAN -> {
                titleFont = loadFont("/fonts/NotoSansSC-SemiBold.ttf", getSize(titleFont));
                selectedItemNameFont = loadFont("/fonts/NotoSansSC-Regular.ttf", getSize(selectedItemNameFont));
                itemInfoFont = loadFont("/fonts/NotoSansSC-Regular.ttf", getSize(itemInfoFont));
                buttonFont = loadFont("/fonts/NotoSansSC-SemiBold.ttf", getSize(buttonFont));
                dialogNPCName = loadFont("/fonts/NotoSansSC-SemiBold.ttf", getSize(dialogNPCName));
                dialogNPCText = loadFont("/fonts/NotoSansSC-Regular.ttf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("/fonts/NotoSansSC-Regular.ttf", getSize(backupAdvisorFont));
                plannerCounter = loadFont("/fonts/NotoSansSC-Regular.ttf", getSize(plannerCounter));
            }
            case PRASPOMIC -> {
                titleFont = loadFont("/fonts/Praspomia-Regular.otf", getSize(titleFont));
                selectedItemNameFont = loadFont("/fonts/Praspomia-Regular.otf", getSize(selectedItemNameFont));
                itemInfoFont = loadFont("/fonts/Praspomia-Regular.otf", getSize(itemInfoFont));
                buttonFont = loadFont("/fonts/Praspomia-Regular.otf", getSize(buttonFont));
                dialogNPCName = loadFont("/fonts/Praspomia-Regular.otf", getSize(dialogNPCName));
                dialogNPCText = loadFont("/fonts/Praspomia-Regular.otf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("/fonts/Praspomia-Regular.otf", getSize(backupAdvisorFont));
                plannerCounter = loadFont("/fonts/Praspomia-Regular.otf", getSize(plannerCounter));
                coinTextFont = loadFont("/fonts/Praspomia-Regular.otf", 17);
                itemCountFont = loadFont("/fonts/Praspomia-Regular.otf", 13);
            }
        }
    }
}
