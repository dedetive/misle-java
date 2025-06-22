package com.ded.misle.core;

import static com.ded.misle.core.LanguageManager.Language;

public enum Setting {

    screenSize("medium"),
    isFullscreen(true),
    fullscreenMode("windowed"),
    displayFPS(true),
    antiAliasing(true),
    frameRateCap(60),
    languageCode(Language.en_US);

    public final Object defaultValue;

    private Object value;

    Setting(Object defaultValue) {
        this.defaultValue = defaultValue;
        this.value = null;
    }

    // Getters

    public Object get() {
        return value != null ? value : defaultValue;
    }

    public void set(Object value) {
        this.value = value;
    }

    public boolean bool() {
        return Boolean.parseBoolean(String.valueOf(get()));
    }

    public int integer() {
        return Integer.parseInt(String.valueOf(get()));
    }

    public String str() {
        return String.valueOf(get());
    }

    public String strDefault() {
        return String.valueOf(defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T getTyped(Class<T> clazz) {
        Object val = get();
        return clazz.isInstance(val) ? (T) val : null;
    }

    //    levelDesigner = Boolean.parseBoolean(getSetting("levelDesigner"));
    //    displayMoreInfo = getSetting("displayMoreInfo");
}