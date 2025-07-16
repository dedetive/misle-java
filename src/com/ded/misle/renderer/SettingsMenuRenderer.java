package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.core.SettingsManager;
import com.ded.misle.input.KeyHandlerDep;

import javax.swing.*;
import java.awt.*;
import java.util.ConcurrentModificationException;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.core.Setting.*;
import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.input.KeyDep.LEFT_MENU;
import static com.ded.misle.input.KeyDep.RIGHT_MENU;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.dialogNPCText;
import static com.ded.misle.renderer.MainRenderer.textShadow;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;
import static com.ded.misle.renderer.SettingsMenuRenderer.SettingPos.*;

public abstract class SettingsMenuRenderer {
    public enum SettingState {
        EMPTY(-1, MenuButtonID.SETTINGS_TAB_EMPTY),
        GENERAL(0, MenuButtonID.SETTINGS_TAB_GENERAL),
        GRAPHICS(1, MenuButtonID.SETTINGS_TAB_GRAPHICS),
        AUDIO(2, MenuButtonID.SETTINGS_TAB_AUDIO),
        GAMEPLAY(3, MenuButtonID.SETTINGS_TAB_GAMEPLAY),

        ;

        public final int order;
        public final MenuButtonID buttonId;

        SettingState(int order, MenuButtonID buttonId) {
            this.order = order;
            this.buttonId = buttonId;
        }

        public static SettingState getStateByOrder(int order) {
            return switch (order) {
                case -1: yield GAMEPLAY;
                case 0: yield GENERAL;
                case 1: yield GRAPHICS;
                case 2: yield AUDIO;
                case 3: yield GAMEPLAY;
                case 4: yield GENERAL;
                default: yield GENERAL;
            };
        }
    }
    public static SettingState settingState = SettingState.GENERAL;

    public static void renderOptionsMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {
            // ANTI-ALIASING
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // BACKGROUND
            drawMenuBackground(g2d);

            // SEPARATING BAR
            g2d.setColor(settingsSeparatingBar);
            int separatingBarY = 210;
            int separatingBarHeight = 2;
            g2d.fillRect(0, separatingBarY, originalScreenWidth, separatingBarHeight);
            g2d.setColor(settingsSeparatingBarBottom);
            g2d.fillRect(0, separatingBarY + separatingBarHeight, originalScreenWidth, originalScreenHeight - (separatingBarY + separatingBarHeight));

            // MENU ITSELF
            MenuRenderer.createTitle("settings_menu_options", g2d);

            int buttonX;
            int buttonY = 220;
            int buttonWidth = 50;
            int buttonHeight = 31;
            Rectangle button;

            // Other menus buttons
            SettingState[] states = new SettingState[]{SettingState.GENERAL, SettingState.GRAPHICS, SettingState.AUDIO, SettingState.GAMEPLAY};
            for (int i = 0; i < states.length; i++) {
                SettingState state = states[i];
                buttonX = 42 + 65 * i;
                button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
                createButton(
                    button,
                    LanguageManager.getText("settings_menu_" + state.name().toLowerCase()),
                    () -> settingState = state,
                    panel,
                    MenuButtonID.valueOf("SETTINGS_TAB_" + state.name())
                );
            }

            // Go back button
            createGoBackButton(panel, MenuButtonID.SETTINGS_MENU_GO_BACK);

            // Text with setting state below title
            g2d.setFont(dialogNPCText);
            FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
            String text = LanguageManager.getText("settings_menu_" + String.valueOf(settingState).toLowerCase());
            int textWidth = fm.stringWidth(text);
            int centerX = (originalScreenWidth - textWidth) / 2;
            int textY = 66;

            g2d.setColor(buttonTextShadowColor);
            g2d.drawString(text, centerX + textShadow, textY + textShadow);
            g2d.setColor(buttonTextColor);
            g2d.drawString(text, centerX, textY);

            switch (settingState) {
                case GENERAL -> renderGeneralMenu(panel);
                case GRAPHICS -> renderGraphicsMenu(panel);
                case AUDIO -> renderAudioMenu(panel);
                case GAMEPLAY -> renderGameplayMenu(panel);
            }

            for (int i = 0; i < 2; i++) {
                int width;
                if (i == 0) {
                    width = (int) (leftKeyIndicatorWidth);
                } else {
                    width = (int) (rightKeyIndicatorWidth);
                }
                int height = width;
                int x = (30 + i * 269) - width / 2;
                int y = 220 + buttonHeight / 2 - height / 2;
                int fixedX = 22 + i * 269;
                int fixedY = (int) (212 + (double) buttonHeight / 2);
                int arcW = width / 4;
                g2d.setColor(settingsMoveKeyHint);
                g2d.fillRoundRect(x, y, width, height, arcW, arcW);

                if (i == 0) {
                    text = KeyHandlerDep.getChar(LEFT_MENU);
                }
                else {
                    text = KeyHandlerDep.getChar(RIGHT_MENU);
                }
                textWidth = fm.stringWidth(text);

                g2d.setColor(settingsMoveKeyHintText);
                g2d.drawString(text, (int) (fixedX + 8 - (double) textWidth / 2), (int) (fixedY + 16 - (double) fm.getHeight() / 5));
            }

            if (leftKeyIndicatorWidth > 16.05) leftKeyIndicatorWidth = Math.max(leftKeyIndicatorWidth - 0.05, 16);
            if (rightKeyIndicatorWidth > 16.05) rightKeyIndicatorWidth = Math.max(rightKeyIndicatorWidth - 0.05, 16);

            try {
                drawButtons(g2d);
            } catch (ConcurrentModificationException e) {
                //
            }
        }
    }

    public static double leftKeyIndicatorWidth = 16;
    public static double rightKeyIndicatorWidth = 16;

    private static final int LEFT_X = 42;
    private static final int RIGHT_X = 282;
    private static final int TOP_Y = 82;
    private static final int MID_Y = 112;
    private static final int BOTTOM_Y = 142;

    enum SettingPos {
        TOP_LEFT(new int[]{LEFT_X, TOP_Y}),
        TOP_RIGHT(new int[]{RIGHT_X, TOP_Y}),
        MID_LEFT(new int[]{LEFT_X, MID_Y}),
        MID_RIGHT(new int[]{RIGHT_X, MID_Y}),
        BOTTOM_LEFT(new int[]{LEFT_X, BOTTOM_Y}),
        BOTTOM_RIGHT(new int[]{RIGHT_X, BOTTOM_Y}),

        ;

        final int[] pos;

        SettingPos (int[] pos) {
            this.pos = pos;
        }
    }

    public static void renderGeneralMenu(JPanel panel) {

        // language
        int[] pos = TOP_LEFT.pos;
        createSetting("settings_general_language", String.valueOf(languageCode.str()),
            pos[0], pos[1], languageCode::cycle, panel, MenuButtonID.SETTING_LANGUAGE);

        pos = TOP_RIGHT.pos;
        createSetting("settings_general_screenShake", String.valueOf(screenShake.str()),
            pos[0], pos[1], screenShake::cycle, panel, MenuButtonID.SETTING_SCREEN_SHAKE);

        pos = MID_RIGHT.pos;
        createSetting("settings_general_pixelation", String.valueOf(pixelation.str()),
            pos[0], pos[1], pixelation::cycle, panel, MenuButtonID.SETTING_PIXELATION);
    }

    public static void renderGraphicsMenu(JPanel panel) {
        // isFullscreen
        int[] pos = TOP_LEFT.pos;
        createSetting("settings_graphics_isFullscreen", isFullscreen.str(),
            pos[0], pos[1], SettingsManager::cycleIsFullscreen, panel, MenuButtonID.SETTING_FULLSCREEN);

        // fullscreenMode
        pos = TOP_RIGHT.pos;
        createSetting("settings_graphics_fullscreenMode", fullscreenMode.str(),
            pos[0], pos[1], SettingsManager::cycleFullscreenMode, panel, MenuButtonID.SETTING_FULLSCREEN_MODE);

        // frameRateCap
        pos = MID_LEFT.pos;
        createSetting("settings_graphics_frameRateCap", frameRateCap.str(),
            pos[0], pos[1], frameRateCap::cycle, panel, MenuButtonID.SETTING_FRAME_RATE_CAP);

        // displayFPS
        pos = MID_RIGHT.pos;
        createSetting("settings_graphics_displayFPS", displayFPS.str(),
            pos[0], pos[1], SettingsManager::cycleDisplayFPS, panel, MenuButtonID.SETTING_DISPLAY_FPS);

        // screenSize
        pos = BOTTOM_LEFT.pos;
        createSetting("settings_graphics_screenSize", screenSize.str(),
            pos[0], pos[1], SettingsManager::cycleScreenSize, panel, MenuButtonID.SETTING_SCREEN_SIZE);

        // antiAliasing
        pos = BOTTOM_RIGHT.pos;
        createSetting("settings_graphics_antiAliasing", antiAliasing.str(),
            pos[0], pos[1], SettingsManager::cycleAntiAliasing, panel, MenuButtonID.SETTING_ANTIALIASING);
    }

    public static void renderAudioMenu(JPanel panel) {

    }

    public static void renderGameplayMenu(JPanel panel) {
        // DisplayMoreInfo
        int[] pos = TOP_LEFT.pos;
        createSetting("settings_gameplay_displayMoreInfo", String.valueOf(displayMoreInfo),
            pos[0], pos[1], SettingsManager::cycleDisplayMoreInfo, panel, MenuButtonID.SETTING_DISPLAY_MORE_INFO);
    }

    public static void createSetting(String text, String value, int unscaledX, int unscaledY, Runnable action, JPanel panel, MenuButtonID id) {
        int buttonX = unscaledX;
        int buttonY = unscaledY;
        int buttonWidth = 88;
        int buttonHeight = 28;
        Rectangle button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        createButton(button, LanguageManager.getText(text), action, panel, id);

        buttonX = unscaledX + 100;
        buttonWidth = 39;
        button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        createButton(button, LanguageManager.getText(value), action, panel, MenuButtonID.valueOf(id + "_VALUE"));
    }

    public static void switchToEmpty() { settingState = SettingState.EMPTY; }

    public static void moveSettingMenu(int offset) {
        int nextOrder = settingState.order + offset;
        if (nextOrder < 0) nextOrder = 3;
        if (nextOrder > 3) nextOrder = 0;
        settingState = SettingState.getStateByOrder(nextOrder);

        MenuButtonID buttonId = settingState.buttonId;

        fadingState.put(buttonId, Fader.FadingState.FADING_OUT);
        fadingProgress.put(buttonId, 0.75F);
        clearButtons();
    }
}
