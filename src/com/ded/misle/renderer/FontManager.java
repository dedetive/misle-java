package com.ded.misle.renderer;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import static com.ded.misle.core.LanguageManager.getCurrentScript;

public abstract class FontManager {
    /**
     *  Comfortaa, Ubuntu -> Supports Latin, Cyrillic and Greek
     *  Basic -> Supports only Latin
     *  NotoSansSC -> Supports Simplified Han, Hiragana, Katakana, Latin, Cyrillic, Greek and Hangul
     *  Praspomia -> Supports only Praspomia (uses Greek characters), converts Latin into Praspomia equivalents (though limited)
     *
     */

    public static Font titleFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", 96);
    public static Font selectedItemNameFont = loadFont("/fonts/Ubuntu-Medium.ttf", 35);
    public static Font itemCountFont = loadFont("/fonts/Ubuntu-Regular.ttf", 40);
    public static Font buttonFont = loadFont("/fonts/Ubuntu-Medium.ttf", 44);
    public static Font itemInfoFont = loadFont("/fonts/Basic-Regular.ttf", 40);
    public static Font dialogNPCName = loadFont("/fonts/Ubuntu-Medium.ttf", 85);
    public static Font dialogNPCText = loadFont("/fonts/Ubuntu-Regular.ttf", 52);
    public static Font coinTextFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", 72);
    public static Font backupAdvisorFont = loadFont("/fonts/Ubuntu-Medium.ttf", 24);
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
        coinTextFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", 72);
        itemCountFont = loadFont("/fonts/Ubuntu-Regular.ttf", 50);
        switch (getCurrentScript()) {
            case LATIN -> {
                titleFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", 96);
                selectedItemNameFont = loadFont("/fonts/Ubuntu-Medium.ttf", 40);
                itemInfoFont = loadFont("/fonts/Basic-Regular.ttf", 40);
                buttonFont = loadFont("/fonts/Ubuntu-Medium.ttf", 44);
                dialogNPCName = loadFont("/fonts/Ubuntu-Medium.ttf", 85);
                dialogNPCText = loadFont("/fonts/Ubuntu-Regular.ttf", 52);
                backupAdvisorFont = loadFont("/fonts/Ubuntu-Medium.ttf", 24);
            }
            case GREEK, CYRILLIC -> {
                titleFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", 96);
                selectedItemNameFont = loadFont("/fonts/Ubuntu-Medium.ttf", 40);
                buttonFont = loadFont("/fonts/Ubuntu-Medium.ttf", 44);
                itemInfoFont = loadFont("/fonts/Ubuntu-Medium.ttf", 40);
                dialogNPCName = loadFont("/fonts/Ubuntu-Medium.ttf", 85);
                dialogNPCText = loadFont("/fonts/Ubuntu-Regular.ttf", 52);
                backupAdvisorFont = loadFont("/fonts/Ubuntu-Medium.ttf", 24);
            }
            case SIMPLIFIED_HAN -> {
                titleFont = loadFont("/fonts/NotoSansSC-SemiBold.ttf", 96);
                selectedItemNameFont = loadFont("/fonts/NotoSansSC-Regular.ttf", 35);
                itemInfoFont = loadFont("/fonts/NotoSansSC-Regular.ttf", 40);
                buttonFont = loadFont("/fonts/NotoSansSC-SemiBold.ttf", 44);
                dialogNPCName = loadFont("/fonts/NotoSansSC-SemiBold.ttf", 85);
                dialogNPCText = loadFont("/fonts/NotoSansSC-Regular.ttf", 52);
                backupAdvisorFont = loadFont("/fonts/NotoSansSC-Regular.ttf", 24);
            }
            case PRASPOMIC -> {
                titleFont = loadFont("/fonts/Praspomia-Regular.otf", 96);
                selectedItemNameFont = loadFont("/fonts/Praspomia-Regular.otf", 35);
                itemInfoFont = loadFont("/fonts/Praspomia-Regular.otf", 40);
                buttonFont = loadFont("/fonts/Praspomia-Regular.otf", 44);
                dialogNPCName = loadFont("/fonts/Praspomia-Regular.otf", 85);
                dialogNPCText = loadFont("/fonts/Praspomia-Regular.otf", 52);
                backupAdvisorFont = loadFont("/fonts/Praspomia-Regular.otf", 24);
                coinTextFont = loadFont("/fonts/Praspomia-Regular.otf", 62);
                itemCountFont = loadFont("/fonts/Praspomia-Regular.otf", 50);
            }
        }

        MainRenderer.textShadow = 1;
    }
}
