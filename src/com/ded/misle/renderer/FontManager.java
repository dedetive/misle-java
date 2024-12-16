package com.ded.misle.renderer;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import static com.ded.misle.Launcher.scale;

public class FontManager {
    public static Font comfortaaFont96 = loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (96 * scale / 3.75));
    public static Font ubuntuFont35 = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (35 * scale / 3.75));
    public static Font basicFont40 = loadFont("/fonts/Basic-Regular.ttf", (float) (40 * scale / 3.75));
    public static Font itemCountFont = loadFont("/fonts/Ubuntu-Regular.ttf", (float) (40 * scale / 3.75));
    public static Font ubuntuFont44 = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (44 * scale / 3.75));

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
        comfortaaFont96 = loadFont("/fonts/Comfortaa-SemiBold.ttf", (float) (96 * scale / 3.75));
        ubuntuFont35 = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (40 * scale / 3.75));
        basicFont40 = loadFont("/fonts/Basic-Regular.ttf", (float) (40 * scale / 3.75));
        itemCountFont = loadFont("/fonts/Ubuntu-Regular.ttf", (float) (50 * scale / 3.75));
        ubuntuFont44 = loadFont("/fonts/Ubuntu-Medium.ttf", (float) (44 * scale / 3.75));

        GameRenderer.textShadow = 1 * scale;
    }
}
