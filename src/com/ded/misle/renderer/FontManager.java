package com.ded.misle.renderer;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import static com.ded.misle.core.LanguageManager.getCurrentScript;
import static com.ded.misle.Launcher.scale;

public class FontManager {
    /**
     *  Comfortaa, Ubuntu -> Supports Latin, Cyrillic and Greek
     *  Basic -> Supports only Latin
     *  NotoSansSC -> Supports Simplified Han, Hiragana, Katakana, Latin, Cyrillic, Greek and Hangul
     *
     */

    public static Font titleFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (96 * scale / 3.75));
    public static Font selectedItemNameFont = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (35 * scale / 3.75));
    public static Font itemCountFont = loadFont("/fonts/Ubuntu-Regular.ttf", (float) (40 * scale / 3.75));
    public static Font buttonFont = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (44 * scale / 3.75));
    public static Font itemInfoFont = loadFont("/fonts/Basic-Regular.ttf", (float) (40 * scale / 3.75));
    public static Font dialogNPCName = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (85 * scale / 3.75));
    public static Font dialogNPCText = loadFont("/fonts/Ubuntu-Regular.ttf", (float) (52 * scale / 3.75));
    public static Font coinTextFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (72 * scale / 3.75));
    public static Font backupAdvisorFont = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (24 * scale / 3.75));
    static {
        updateFontSizes();
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

    public static void updateFontSizes() {
        coinTextFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (62 * scale / 3.75));
        itemCountFont = loadFont("/fonts/Ubuntu-Regular.ttf", (float) (50 * scale / 3.75));
        switch (getCurrentScript()) {
            case LATIN -> {
                titleFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (96 * scale / 3.75));
                selectedItemNameFont = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (40 * scale / 3.75));
                itemInfoFont = loadFont("/fonts/Basic-Regular.ttf", (float) (40 * scale / 3.75));
                buttonFont = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (44 * scale / 3.75));
                dialogNPCName = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (85 * scale / 3.75));
                dialogNPCText = loadFont("/fonts/Ubuntu-Regular.ttf", (float) (52 * scale / 3.75));
                backupAdvisorFont = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (24 * scale / 3.75));
            }
            case GREEK, CYRILLIC -> {
                titleFont = loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (96 * scale / 3.75));
                selectedItemNameFont = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (40 * scale / 3.75));
                buttonFont = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (44 * scale / 3.75));
                itemInfoFont = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (40 * scale / 3.75));
                dialogNPCName = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (85 * scale / 3.75));
                dialogNPCText = loadFont("/fonts/Ubuntu-Regular.ttf", (float) (52 * scale / 3.75));
                backupAdvisorFont = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (24 * scale / 3.75));
            }
            case SIMPLIFIED_HAN -> {
                titleFont = loadFont("/fonts/NotoSansSC-SemiBold.ttf", (float) (96 * scale / 3.75));
                selectedItemNameFont = loadFont("/fonts/NotoSansSC-Regular.ttf", (float) (35 * scale / 3.75));
                itemInfoFont = loadFont("/fonts/NotoSansSC-Regular.ttf", (float) (40 * scale / 3.75));
                buttonFont = loadFont("/fonts/NotoSansSC-SemiBold.ttf", (float) (44 * scale / 3.75));
                dialogNPCName = loadFont("/fonts/NotoSansSC-SemiBold.ttf", (float) (85 * scale / 3.75));
                dialogNPCText = loadFont("/fonts/NotoSansSC-Regular.ttf", (float) (52 * scale / 3.75));
                backupAdvisorFont = loadFont("/fonts/NotoSansSC-Regular.ttf", (float) (24 * scale / 3.75));
            }
            case PRASPOMIC -> {
                titleFont = loadFont("/fonts/Praspomia-Regular.otf", (float) (96 * scale / 3.75));
                selectedItemNameFont = loadFont("/fonts/Praspomia-Regular.otf", (float) (35 * scale / 3.75));
                itemInfoFont = loadFont("/fonts/Praspomia-Regular.otf", (float) (40 * scale / 3.75));
                buttonFont = loadFont("/fonts/Praspomia-Regular.otf", (float) (44 * scale / 3.75));
                dialogNPCName = loadFont("/fonts/Praspomia-Regular.otf", (float) (85 * scale / 3.75));
                dialogNPCText = loadFont("/fonts/Praspomia-Regular.otf", (float) (52 * scale / 3.75));
                backupAdvisorFont = loadFont("/fonts/Praspomia-Regular.otf", (float) (24 * scale / 3.75));
            }
        }

        MainRenderer.textShadow = 1 * scale;
    }
}
