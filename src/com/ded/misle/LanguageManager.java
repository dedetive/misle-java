package com.ded.misle;

import java.util.ResourceBundle;
import java.util.Locale;

public class LanguageManager {
	private static ResourceBundle messages;
	private static ResourceBundle fallbackMessages;

	public LanguageManager(String languageCode) {
		try {
			String[] parts = languageCode.split("_");
			Locale locale = new Locale(parts[0], parts.length > 1 ? parts[1] : "");

			// Load the target locale's messages
			messages = ResourceBundle.getBundle("com.ded.misle.resources.lang.messages", locale, new ResourceBundle.Control() {
				@Override
				public Locale getFallbackLocale(String baseName, Locale locale) {
					// Fall back to English if no match is found
					return Locale.ENGLISH;
				}
			});

			// Load the English fallback messages
			fallbackMessages = ResourceBundle.getBundle("com.ded.misle.resources.lang.messages", Locale.ENGLISH);
		} catch (Exception e) {
			// If any error occurs, fallback to English for both
			messages = ResourceBundle.getBundle("com.ded.misle.resources.lang.messages", Locale.ENGLISH);
			fallbackMessages = messages;
		}
	}

	public static String getText(String key) {
		try {
			return messages.getString(key);
		} catch (Exception e) {
			// If the key is missing, fallback to English
			try {
				return fallbackMessages.getString(key);
			} catch (Exception ex) {
				// If the key is missing in English too, return the key name as a placeholder
				return key;
			}
		}
	}
}