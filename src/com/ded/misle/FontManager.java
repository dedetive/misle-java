package com.ded.misle;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FontManager {
	public static Font loadFont(String fontPath, float size) {
		try {
			InputStream is = FontManager.class.getResourceAsStream(fontPath);
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			return font.deriveFont(size);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			return new Font("Dialog", Font.PLAIN, (int) size); // Fallback
		}
	}
}
