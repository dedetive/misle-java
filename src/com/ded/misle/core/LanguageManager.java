package com.ded.misle.core;

import java.util.ResourceBundle;
import java.util.Locale;

import static com.ded.misle.core.LanguageManager.Script.*;

public class LanguageManager {
	private static ResourceBundle messages;
	private static ResourceBundle fallbackMessages;

	public enum Script {
		LATIN,
		GREEK,
		CYRILLIC,
		SIMPLIFIED_HAN,
		PRASPOMIC,
	}

	public enum Language {
		es_ES(LATIN),
		en_US(LATIN),
		pt_BR(LATIN),
		pt_BR2(LATIN),
		de_DE(LATIN),
		ru_RU(CYRILLIC),
		el_GR(GREEK),
		mi_PM(PRASPOMIC),
		zh_CN(SIMPLIFIED_HAN);

		private final Script script;

		Language(Script script) {
			this.script = script;
		}

		public Script getScript() {
			return script;
		}
	}

	private static Script currentScript;

	public LanguageManager() {
		this(Setting.languageCode.str());
	}

	public LanguageManager(String languageCode) {
		try {
			for (Language language : Language.values()) {
				if (language == Language.valueOf(languageCode)) {
					currentScript = language.getScript();
					break;
				}
			}

			String[] parts = languageCode.split("_");

			Locale locale = new Locale(parts[0], parts.length > 1 ? parts[1] : "");

			// Load the target locale's messages
			messages = ResourceBundle.getBundle("resources.lang.messages", locale, new ResourceBundle.Control() {
				@Override
				public Locale getFallbackLocale(String baseName, Locale locale) {
					// Fall back to English if no match is found
					return Locale.ENGLISH;
				}
			});

			// Load the English fallback messages
			fallbackMessages = ResourceBundle.getBundle("resources.lang.messages", Locale.ENGLISH);
		} catch (Exception e) {
			// If any error occurs, fallback to English for both
			messages = ResourceBundle.getBundle("resources.lang.messages", Locale.ENGLISH);
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

	public static Script getCurrentScript() {
		return currentScript;
	}
}