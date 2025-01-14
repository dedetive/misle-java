package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;

import javax.swing.*;
import java.awt.*;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.screenHeight;
import static com.ded.misle.core.GamePanel.screenWidth;
import static com.ded.misle.renderer.ColorManager.menuBackgroundColor;
import static com.ded.misle.renderer.MenuButton.createButton;
import static com.ded.misle.renderer.MenuButton.drawButtons;

public class SettingsMenuRenderer {
    public enum SettingState {
        GENERAL,
        GRAPHICS,
        AUDIO,
        GAMEPLAY,
    }
    private static SettingState settingState = SettingState.GENERAL;

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
                // General
            buttonX = (int) (42 * scale);
            button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            createButton(button, LanguageManager.getText("settings_menu_general"), SettingsMenuRenderer::switchToGeneral, panel);

                // Graphics
            // General
            buttonX = (int) (117 * scale);
            button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            createButton(button, LanguageManager.getText("settings_menu_graphics"), SettingsMenuRenderer::switchToGeneral, panel);

                // Audio
            buttonX = (int) (192 * scale);
            button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            createButton(button, LanguageManager.getText("settings_menu_audio"), SettingsMenuRenderer::switchToGeneral, panel);

                // Gameplay
            buttonX = (int) (267 * scale);
            button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            createButton(button, LanguageManager.getText("settings_menu_gameplay"), SettingsMenuRenderer::switchToGeneral, panel);

            // Go back button
            buttonX = (int) (356 * scale);
            buttonWidth = (int) (109 * scale);
            button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);

            createButton(button, LanguageManager.getText("settings_menu_go_back"), MenuRenderer::goToPreviousMenu, panel);

            switch (settingState) {
                case GENERAL -> renderGeneralMenu(g2d);
                case GRAPHICS -> renderGraphicsMenu(g2d);
                case AUDIO -> renderAudioMenu(g2d);
                case GAMEPLAY -> renderGameplayMenu(g2d);
            }

            drawButtons(g2d, scaleByScreenSize);
        }
    }

    public static void renderGeneralMenu(Graphics2D g2d) {

    }

    public static void renderGraphicsMenu(Graphics2D g2d) {

    }

    public static void renderAudioMenu(Graphics2D g2d) {

    }

    public static void renderGameplayMenu(Graphics2D g2d) {

    }

    public static void switchToGeneral() { settingState = SettingState.GENERAL; }

    public static void switchToGraphics() { settingState = SettingState.GRAPHICS; }

    public static void switchToAudio() { settingState = SettingState.AUDIO; }

    public static void switchToGameplay() { settingState = SettingState.GAMEPLAY; }
}
