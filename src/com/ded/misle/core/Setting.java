package com.ded.misle.core;

public class Setting<T> {

    public Class<T> valueType;
    public T value;
    public T defaultValue;

    public Setting(Class<T> valueType, T defaultValue) {
        this.valueType = valueType;
        this.defaultValue = defaultValue;
    }

    public static Setting screenSize = new Setting(String.class, "medium");

//    screenSize = getSetting("screenSize");
//    isFullscreen = Boolean.parseBoolean(getSetting("isFullscreen"));
//    fullscreenMode = getSetting("fullscreenMode");
//    displayFPS = Boolean.parseBoolean(getSetting("displayFPS"));
//    antiAliasing = Boolean.parseBoolean(getSetting("antiAliasing"));
//    frameRateCap = Integer.parseInt(getSetting("frameRateCap"));
//    languageCode = getSetting("language");
//    languageManager = new LanguageManager(languageCode);
//    levelDesigner = Boolean.parseBoolean(getSetting("levelDesigner"));
//    heldItemFollowsMouse = Boolean.parseBoolean(getSetting("heldItemFollowsMouse"));
//    displayMoreInfo = getSetting("displayMoreInfo");

}
