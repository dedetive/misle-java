package com.ded.misle.renderer;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static com.ded.misle.core.LanguageManager.getCurrentScript;

public abstract class FontManager {
    /**
     *  Comfortaa, Ubuntu -> Supports Latin, Cyrillic and Greek
     *  Basic -> Supports only Latin
     *  NotoSansSC -> Supports Simplified Han, Hiragana, Katakana, Latin, Cyrillic, Greek and Hangul
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
    }};
    private static int getSize(Font font) {
        return fontToSize.getOrDefault(font, 0);
    }

    static {
        updateFontScript();
    }

    public static Font loadFont(String fontPath, float size) {
		try (InputStream is = FontManager.class.getResourceAsStream(fontPath)) {
			if (is == null) {
//				System.err.println("Warning: Font resource not found: " + fontPath);
				return new Font("Dialog", Font.PLAIN, (int) size); // Fallback font
			}
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			return font.deriveFont(size);
		} catch (FontFormatException e) {
			System.err.println("Font format error: " + e.getMessage());
			return new Font("Dialog", Font.PLAIN, (int) size); // Fallback font
		} catch (IOException e) {
			System.err.println("Error loading font: " + e.getMessage());
			return new Font("Dialog", Font.PLAIN, (int) size); // Fallback font
		}
	}

    public static void updateFontScript() {
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
            }
            case GREEK, CYRILLIC -> {
                titleFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", getSize(titleFont));
                selectedItemNameFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(selectedItemNameFont));
                buttonFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(buttonFont));
                itemInfoFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(itemInfoFont));
                dialogNPCName = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(dialogNPCName));
                dialogNPCText = loadFont("/fonts/Ubuntu-Regular.ttf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("/fonts/Ubuntu-Medium.ttf", getSize(backupAdvisorFont));
            }
            case SIMPLIFIED_HAN -> {
                titleFont = loadFont("/fonts/NotoSansSC-SemiBold.ttf", getSize(titleFont));
                selectedItemNameFont = loadFont("/fonts/NotoSansSC-Regular.ttf", getSize(selectedItemNameFont));
                itemInfoFont = loadFont("/fonts/NotoSansSC-Regular.ttf", getSize(itemInfoFont));
                buttonFont = loadFont("/fonts/NotoSansSC-SemiBold.ttf", getSize(buttonFont));
                dialogNPCName = loadFont("/fonts/NotoSansSC-SemiBold.ttf", getSize(dialogNPCName));
                dialogNPCText = loadFont("/fonts/NotoSansSC-Regular.ttf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("/fonts/NotoSansSC-Regular.ttf", getSize(backupAdvisorFont));
            }
            case PRASPOMIC -> {
                titleFont = loadFont("/fonts/Praspomia-Regular.otf", getSize(titleFont));
                selectedItemNameFont = loadFont("/fonts/Praspomia-Regular.otf", getSize(selectedItemNameFont));
                itemInfoFont = loadFont("/fonts/Praspomia-Regular.otf", getSize(itemInfoFont));
                buttonFont = loadFont("/fonts/Praspomia-Regular.otf", getSize(buttonFont));
                dialogNPCName = loadFont("/fonts/Praspomia-Regular.otf", getSize(dialogNPCName));
                dialogNPCText = loadFont("/fonts/Praspomia-Regular.otf", getSize(dialogNPCText));
                backupAdvisorFont = loadFont("/fonts/Praspomia-Regular.otf", getSize(backupAdvisorFont));
                coinTextFont = loadFont("/fonts/Praspomia-Regular.otf", 62);
                itemCountFont = loadFont("/fonts/Praspomia-Regular.otf", 50);
            }
        }

        MainRenderer.textShadow = 1;
    }
}
