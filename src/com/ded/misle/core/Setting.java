package com.ded.misle.core;

import static com.ded.misle.core.LanguageManager.Language.en_US;

public class Setting<T> {

    public Class<T> valueType;
    public Object value;
    public final Object defaultValue;
    public final String name;

    public Setting(Class<T> valueType, T defaultValue, String name) {
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.name = name;
    }

    public boolean bool() {
        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public boolean boolDefault() {
        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public String str() {
        return String.valueOf(this.value);
    }

    public String strDefault() {
        return String.valueOf(this.value);
    }

    public int integer() {
        return Integer.parseInt(this.value.toString());
    }

    public static Setting<String> screenSize = new Setting<>(String.class, "medium", "screenSize");
    public static Setting<Boolean> isFullscreen = new Setting<>(boolean.class, true, "isFullscreen");
    public static Setting<String> fullscreenMode = new Setting<>(String.class, "windowed", "fullscreenMode");
    public static Setting<Boolean> displayFPS = new Setting<>(boolean.class, true, "displayFPS");
    public static Setting<Boolean> antiAliasing = new Setting<>(boolean.class, true, "antiAliasing");
    public static Setting<Integer> frameRateCap = new Setting<>(int.class, 60, "frameRateCap");
    public static Setting<LanguageManager.Language> languageCode = new Setting<>(LanguageManager.Language.class, en_US, "languageCode");

//    languageCode = getSetting("language");
//    languageManager = new LanguageManager(languageCode);
//    levelDesigner = Boolean.parseBoolean(getSetting("levelDesigner"));
//    heldItemFollowsMouse = Boolean.parseBoolean(getSetting("heldItemFollowsMouse"));
//    displayMoreInfo = getSetting("displayMoreInfo");

}
