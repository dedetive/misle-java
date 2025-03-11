package com.ded.misle.core;

public class Setting<T> {

    public Class<T> valueType;
    public Object value;
    public Object defaultValue;

    public Setting(Class<T> valueType, T defaultValue) {
        this.valueType = valueType;
        this.defaultValue = defaultValue;
    }

    public static Setting<String> screenSize = new Setting<>(String.class, "medium");
    public static Setting<Boolean> isFullscreen = new Setting<>(boolean.class, true);
    public static Setting<String> fullscreenMode = new Setting<>(String.class, "windowed");
    public static Setting<Boolean> displayFPS = new Setting<>(boolean.class, true);

//    antiAliasing = Boolean.parseBoolean(getSetting("antiAliasing"));
//    frameRateCap = Integer.parseInt(getSetting("frameRateCap"));
//    languageCode = getSetting("language");
//    languageManager = new LanguageManager(languageCode);
//    levelDesigner = Boolean.parseBoolean(getSetting("levelDesigner"));
//    heldItemFollowsMouse = Boolean.parseBoolean(getSetting("heldItemFollowsMouse"));
//    displayMoreInfo = getSetting("displayMoreInfo");

}
