package com.ded.misle.core;

import java.util.Arrays;

import static com.ded.misle.Launcher.languageManager;
import static com.ded.misle.core.LanguageManager.Language;
import static com.ded.misle.renderer.FontManager.updateFontScript;

public enum Setting {

    screenSize("medium",
        new String[]{"small", "medium", "big", "huge"}
    ),

    isFullscreen(true,
        new Boolean[]{true, false}
    ),

    fullscreenMode("windowed",
        new String[]{"windowed", "exclusive"}
    ),

    displayFPS(true,
        new Boolean[]{true, false}
    ),

    antiAliasing(true,
        new Boolean[]{true, false}
    ),

    frameRateCap(60,
        new Integer[]{30, 60, 90, 120, 160}
    ),

    languageCode(Language.en_US,
        Arrays.stream(Language.values()).filter(e -> e != Language.mi_PM).toArray(Language[]::new),
        () -> {
            languageManager = new LanguageManager();
            updateFontScript();
        }
    )

    ;

    public final Object defaultValue;
    private Object value;
    private final Object[] cycleOptions;
    private final Runnable onCycle;

    Setting(Object defaultValue, Object[] cycleOptions) {
        this(defaultValue, cycleOptions, null);
    }

    Setting(Object defaultValue, Object[] cycleOptions, Runnable onCycle) {
        this.defaultValue = defaultValue;
        this.cycleOptions = cycleOptions;
        this.onCycle = onCycle;
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

    public void cycle() {
        if (cycleOptions == null || cycleOptions.length == 0)
            return;

        Object current = get();
        for (int i = 0; i < cycleOptions.length; i++) {
            if (cycleOptions[i].equals(current)) {
                int nextIndex = (i + 1) % cycleOptions.length;
                set(cycleOptions[nextIndex]);
                SettingsManager.changeSetting(this.name(), this.str());
                if (onCycle != null) onCycle.run();
                return;
            }
        }

        // fallback if not found
        set(cycleOptions[0]);
        if (onCycle != null) onCycle.run();
    }
}