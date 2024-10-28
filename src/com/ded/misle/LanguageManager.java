package com.ded.misle;

import java.util.ResourceBundle;
import java.util.Locale;

public class LanguageManager {
	private static ResourceBundle messages;

	public LanguageManager(String languageCode) {
		Locale locale = new Locale(languageCode);
		messages = ResourceBundle.getBundle("lang.messages", locale);
	}

	public static String getText(String key) {
		return messages.getString(key);
	}
}
