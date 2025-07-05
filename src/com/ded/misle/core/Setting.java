package com.ded.misle.core;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.ded.misle.Launcher.languageManager;
import static com.ded.misle.core.LanguageManager.Language;
import static com.ded.misle.game.GamePanel.nsPerFrame;
import static com.ded.misle.renderer.FontManager.updateFontScript;

/*
    TODO:
        - Add missing onCycles values
        - Remove SettingManager setting-specific cycles methods
        - Update SettingsMenuRenderer to use setting::cycle
        instead of calling SettingsManager setting-specific cycle method
*/
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
        new Integer[]{30, 60, 90, 120, 160},
        (val) -> nsPerFrame = 1000000000.0 / Math.clamp((int) val, 30, 144)
    ),

    languageCode(Language.en_US,
        Arrays.stream(Language.values())
            .filter(e -> e != Language.mi_PM)
            .toArray(Language[]::new),
        (val) -> {
            languageManager = new LanguageManager(val.toString());
            updateFontScript();
        }
    ),

    screenShake(
        true,
        new Boolean[]{true, false}
    ),

    pixelation(
        "normal",
        new String[]{"normal", "low", "none"}
    )

    ;

    public final Object defaultValue;
    private Object value;
    private final Object[] cycleOptions;
    private final Consumer<Object> onCycle;

    Setting(Object defaultValue, Object[] cycleOptions) {
        this(defaultValue, cycleOptions, null);
    }

    Setting(Object defaultValue, Object[] cycleOptions, Consumer<Object> onCycle) {
        this.defaultValue = defaultValue;
        this.cycleOptions = cycleOptions;
        this.onCycle = onCycle;
    }

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

    public void cycle() {
        if (cycleOptions == null || cycleOptions.length == 0)
            return;

        Object current = get();
        for (int i = 0; i < cycleOptions.length; i++) {
            if (cycleOptions[i].equals(current)) {
                Object nextValue = cycleOptions[(i + 1) % cycleOptions.length];
                set(nextValue);
                SettingsManager.changeSetting(this.name(), this.str());
                if (onCycle != null) onCycle.accept(nextValue);
                return;
            }
        }

        Object fallback = cycleOptions[0];
        set(fallback);
        if (onCycle != null) onCycle.accept(fallback);
    }
}