package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.core.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.util.ConcurrentModificationException;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.core.GamePanel.screenHeight;
import static com.ded.misle.core.GamePanel.screenWidth;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.dialogNPCText;
import static com.ded.misle.renderer.MainRenderer.textShadow;
import static com.ded.misle.renderer.MenuButton.createButton;
import static com.ded.misle.renderer.MenuButton.drawButtons;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;
import static com.ded.misle.renderer.SettingsMenuRenderer.SettingPos.*;

public class SettingsMenuRenderer {
    public enum SettingState {
        GENERAL,
        GRAPHICS,
        AUDIO,
        GAMEPLAY,
    }
    public static SettingState settingState = SettingState.GENERAL;

    public static void renderOptionsMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {
            // ANTI-ALIASING
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            double scaleByScreenSize = scale / 3.75;

            // BACKGROUND
            drawMenuBackground(g2d);

            // SEPARATING BAR
            g2d.setColor(settingsSeparatingBar);
            int separatingBarY = (int) (210 * Math.pow(scale, 1.04));
            int separatingBarHeight = (int) (2 * scale);
            g2d.fillRect(0, separatingBarY, (int) screenWidth, separatingBarHeight);
            g2d.setColor(settingsSeparatingBarBottom);
            g2d.fillRect(0, separatingBarY + separatingBarHeight, (int) screenWidth, (int) (screenHeight - (separatingBarY + separatingBarHeight)));

            // MENU ITSELF
            MenuRenderer.createTitle("settings_menu_options", g2d, scaleByScreenSize);

            int buttonX;
            int buttonY = (int) (220 * Math.pow(scale, 1.04));
            int buttonWidth = (int) (50 * scale);
            int buttonHeight = (int) (31 * scale);
            Rectangle button;

            // Other menus buttons
            String[] menus = new String[]{
                LanguageManager.getText("settings_menu_general"),
                LanguageManager.getText("settings_menu_graphics"),
                LanguageManager.getText("settings_menu_audio"),
                LanguageManager.getText("settings_menu_gameplay")
            };
            Runnable[] actions = new Runnable[]{
                SettingsMenuRenderer::switchToGeneral,
                SettingsMenuRenderer::switchToGraphics,
                SettingsMenuRenderer::switchToAudio,
                SettingsMenuRenderer::switchToGameplay
            };
            for (int i = 0; i < 4; i++) {
                buttonX = (int) ((42 + 65 * i) * scale);
                button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
                createButton(button, menus[i], actions[i], panel, 38 * i + 100);
            }

            // Go back button
            buttonX = (int) (356 * scale);
            buttonWidth = (int) (109 * scale);
            button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            createButton(button, LanguageManager.getText("settings_menu_go_back"), MenuRenderer::goToPreviousMenu, panel, 40);

            // Text with setting state below title
            g2d.setFont(dialogNPCText);
            FontMetrics fm = g2d.getFontMetrics();
            String text = LanguageManager.getText("settings_menu_" + String.valueOf(settingState).toLowerCase());
            int textWidth = fm.stringWidth(text);
            int centerX = (int) ((screenWidth - textWidth) / 2);
            int textY = (int) (66 * scale);

            g2d.setColor(buttonTextShadowColor);
            g2d.drawString(text, (int) (centerX + textShadow), (int) (textY + textShadow));
            g2d.setColor(buttonTextColor);
            g2d.drawString(text, centerX, textY);

            switch (settingState) {
                case GENERAL -> renderGeneralMenu(panel);
                case GRAPHICS -> renderGraphicsMenu(panel);
                case AUDIO -> renderAudioMenu(panel);
                case GAMEPLAY -> renderGameplayMenu(panel);
            }

            try {
                drawButtons(g2d, scaleByScreenSize);
            } catch (ConcurrentModificationException e) {
                //
            }
        }
    }

    private static final int LEFTX = 42;
    private static final int RIGHTX = 282;
    private static final int TOPY = 82;
    private static final int MIDY = 112;
    private static final int BOTTOMY = 142;

    enum SettingPos {
        TOP_LEFT(new int[]{LEFTX, TOPY}),
        TOP_RIGHT(new int[]{RIGHTX, TOPY}),
        MID_LEFT(new int[]{LEFTX, MIDY}),
        MID_RIGHT(new int[]{RIGHTX, MIDY}),
        BOTTOM_LEFT(new int[]{LEFTX, BOTTOMY}),
        BOTTOM_RIGHT(new int[]{RIGHTX, BOTTOMY}),

        ;

        final int[] pos;

        SettingPos (int[] pos) {
            this.pos = pos;
        }
    }

    public static void renderGeneralMenu(JPanel panel) {

        // language
        int[] pos = TOP_LEFT.pos;
        createSetting("settings_general_language", String.valueOf(languageCode),
            pos[0], pos[1], SettingsManager::cycleLanguage, panel, 42);
    }

    public static void renderGraphicsMenu(JPanel panel) {
        // isFullscreen
        int[] pos = TOP_LEFT.pos;
        createSetting("settings_graphics_isFullscreen", String.valueOf(isFullscreen),
            pos[0], pos[1], SettingsManager::cycleIsFullscreen, panel, 46);

        // fullscreenMode
        pos = TOP_RIGHT.pos;
        createSetting("settings_graphics_fullscreenMode", fullscreenMode,
            pos[0], pos[1], SettingsManager::cycleFullscreenMode, panel, 48);

        // frameRateCap
        pos = MID_LEFT.pos;
        createSetting("settings_graphics_frameRateCap", String.valueOf(frameRateCap),
            pos[0], pos[1], SettingsManager::cycleFrameRateCap, panel, 50);

        // displayFPS
        pos = MID_RIGHT.pos;
        createSetting("settings_graphics_displayFPS", String.valueOf(displayFPS),
            pos[0], pos[1], SettingsManager::cycleDisplayFPS, panel, 54);

        // screenSize
        pos = BOTTOM_LEFT.pos;
        createSetting("settings_graphics_screenSize", String.valueOf(screenSize),
            pos[0], pos[1], SettingsManager::cycleScreenSize, panel, 44);

        // antiAliasing
        pos = BOTTOM_RIGHT.pos;
        createSetting("settings_graphics_antiAliasing", String.valueOf(antiAliasing),
            pos[0], pos[1], SettingsManager::cycleAntiAliasing, panel, 56);
    }

    public static void renderAudioMenu(JPanel panel) {

    }

    public static void renderGameplayMenu(JPanel panel) {
        // heldItemFollowsMouse
        int[] pos = TOP_LEFT.pos;
        createSetting("settings_gameplay_heldItemFollowsMouse", String.valueOf(heldItemFollowsMouse),
            pos[0], pos[1], SettingsManager::cycleHeldItemFollowsMouse, panel, 52);

        // DisplayMoreInfo
        pos = TOP_RIGHT.pos;
        createSetting("settings_gameplay_displayMoreInfo", String.valueOf(displayMoreInfo),
            pos[0], pos[1], SettingsManager::cycleDisplayMoreInfo, panel, 58);
    }

    public static void createSetting(String text, String value, int unscaledX, int unscaledY, Runnable action, JPanel panel, int id) {
        int buttonX = (int) (unscaledX * scale);
        int buttonY = (int) (unscaledY * Math.pow(scale, 1.04));
        int buttonWidth = (int) (88 * scale);
        int buttonHeight = (int) (28 * scale);
        Rectangle button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        createButton(button, LanguageManager.getText(text), action, panel, id);

        buttonX = (int) ((unscaledX + 100) * scale);
        buttonWidth = (int) (39 * scale);
        button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        createButton(button, LanguageManager.getText(value), action, panel, id + 1);
    }

    public static void switchToGeneral() { settingState = SettingState.GENERAL; }

    public static void switchToGraphics() { settingState = SettingState.GRAPHICS; }

    public static void switchToAudio() { settingState = SettingState.AUDIO; }

    public static void switchToGameplay() { settingState = SettingState.GAMEPLAY; }
}
