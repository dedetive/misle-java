package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.core.SettingsManager;

import javax.swing.*;
import java.awt.*;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.core.GamePanel.screenHeight;
import static com.ded.misle.core.GamePanel.screenWidth;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.dialogNPCText;
import static com.ded.misle.renderer.MainRenderer.textShadow;
import static com.ded.misle.renderer.MenuButton.createButton;
import static com.ded.misle.renderer.MenuButton.drawButtons;

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
            g2d.setColor(menuBackgroundColor);
            g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

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
                createButton(button, menus[i], actions[i], panel);
            }

            // Go back button
            buttonX = (int) (356 * scale);
            buttonWidth = (int) (109 * scale);
            button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            createButton(button, LanguageManager.getText("settings_menu_go_back"), MenuRenderer::goToPreviousMenu, panel);

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

            // Separating bar
            g2d.setColor(settingsSeparatingBar);
            g2d.fillRect(0, (int) (210 * Math.pow(scale, 1.04)), (int) screenWidth, (int) (2 * scale));

            switch (settingState) {
                case GENERAL -> renderGeneralMenu(g2d, panel);
                case GRAPHICS -> renderGraphicsMenu(g2d, panel);
                case AUDIO -> renderAudioMenu(g2d, panel);
                case GAMEPLAY -> renderGameplayMenu(g2d, panel);
            }

            drawButtons(g2d, scaleByScreenSize);
        }
    }

    public static void renderGeneralMenu(Graphics2D g2d, JPanel panel) {
        // language
        int buttonX = (int) (42 * scale);
        int buttonY = (int) (82 * Math.pow(scale, 1.04));
        int buttonWidth = (int) (88 * scale);
        int buttonHeight = (int) (28 * scale);
        Rectangle button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        createButton(button, LanguageManager.getText("settings_general_language"), SettingsManager::cycleLanguage, panel);

        buttonX = (int) (142 * scale);
        buttonWidth = (int) (39 * scale);
        button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        createButton(button, LanguageManager.getText(languageCode), SettingsManager::cycleLanguage, panel);
    }

    public static void renderGraphicsMenu(Graphics2D g2d, JPanel panel) {
        // screenSize
        int buttonX = (int) (42 * scale);
        int buttonY = (int) (82 * Math.pow(scale, 1.04));
        int buttonWidth = (int) (88 * scale);
        int buttonHeight = (int) (28 * scale);
        Rectangle button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        createButton(button, LanguageManager.getText("settings_graphics_screenSize"), SettingsManager::cycleScreenSize, panel);

        buttonX = (int) (142 * scale);
        buttonWidth = (int) (39 * scale);
        button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        createButton(button, LanguageManager.getText(screenSize), SettingsManager::cycleScreenSize, panel);

        // isFullscreen


        // fullscreenMode


        // displayFPS


        // frameRateCap
    }

    public static void renderAudioMenu(Graphics2D g2d, JPanel panel) {

    }

    public static void renderGameplayMenu(Graphics2D g2d, JPanel panel) {
        // heldItemFollowsMouse
    }

    public static void switchToGeneral() { settingState = SettingState.GENERAL; }

    public static void switchToGraphics() { settingState = SettingState.GRAPHICS; }

    public static void switchToAudio() { settingState = SettingState.AUDIO; }

    public static void switchToGameplay() { settingState = SettingState.GAMEPLAY; }
}