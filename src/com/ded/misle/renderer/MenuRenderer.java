package com.ded.misle.renderer;

import com.ded.misle.game.GamePanel;
import com.ded.misle.core.LanguageManager;

import javax.swing.*;
import java.awt.*;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.game.GamePanel.GameState.*;
import static com.ded.misle.core.Setting.antiAliasing;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.itemInfoFont;
import static com.ded.misle.renderer.image.ImageManager.ImageName.MAIN_MENU_BACKGROUND;
import static com.ded.misle.renderer.image.ImageManager.cachedImages;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.core.SaveFile.saveEverything;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.SettingsMenuRenderer.switchToEmpty;
import static com.ded.misle.world.data.WorldLoader.unloadBoxes;
import static com.ded.misle.world.entities.player.Planner.resumeExecution;
import static java.lang.System.currentTimeMillis;

public abstract class MenuRenderer {
    static void createTitle(String text, Graphics2D g2d) {
        g2d.setFont(FontManager.titleFont);
        FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
        String titleText = LanguageManager.getText(text);
        int textWidth = fm.stringWidth(titleText);
        int centerX = (originalScreenWidth - textWidth) / 2;
        int textY = 48;
        g2d.setColor(menuTitleShadowColor);
        drawColoredText(g2d, titleText, (int) (centerX - MainRenderer.textShadow), textY); // Left
        drawColoredText(g2d, titleText, (int) (centerX + MainRenderer.textShadow), textY); // Right
        drawColoredText(g2d, titleText, centerX, (int) (textY - MainRenderer.textShadow)); // Up
        drawColoredText(g2d, titleText, centerX, (int) (textY + MainRenderer.textShadow)); // Down
        drawColoredText(g2d, titleText, (int) (centerX - MainRenderer.textShadow), (int) (textY - MainRenderer.textShadow)); // Left-up corner
        drawColoredText(g2d, titleText, (int) (centerX + MainRenderer.textShadow), (int) (textY - MainRenderer.textShadow)); // Right-up corner
        drawColoredText(g2d, titleText, (int) (centerX - MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow)); // Left-down corner
        drawColoredText(g2d, titleText, (int) (centerX - MainRenderer.textShadow), (int) (textY - MainRenderer.textShadow)); // Right-down corner
        g2d.setColor(menuTitleColor);
        drawColoredText(g2d, titleText, centerX, textY);
    }

    public static void quitGame() {
        GamePanel.quitGame();
    }

    public static void optionsMenu() {
        MainRenderer.previousMenu = MainRenderer.currentMenu;
        MainRenderer.currentMenu = OPTIONS_MENU;
        gameState = OPTIONS_MENU;
        clearButtonFading();
        switchToEmpty();
    }

    public static void goToPreviousMenu() {
        clearButtons();
        GameState newGameState = previousMenu;
        gameState = newGameState;
        MainRenderer.previousMenu = MainRenderer.currentMenu;
        currentMenu = newGameState;
        clearButtonFading();
        resumeExecution();
    }

    public static void goToMainMenu() {
        saveEverything();
        unloadBoxes();
        player.unloadPlayer();
        MainRenderer.previousMenu = MainRenderer.currentMenu;
        gameState = MAIN_MENU;
        fader.reset();
        clearButtonFading();
        resumeExecution();
    }

    public static void pauseGame() {
        MainRenderer.previousMenu = MainRenderer.currentMenu;
        MainRenderer.currentMenu = PAUSE_MENU;
        gameState = PAUSE_MENU;
        clearButtonFading();
    }

    public static void renderMainMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            MainRenderer.currentMenu = MAIN_MENU;

            // BACKGROUND
            drawMenuBackground(g2d);

            // MENU ITSELF

            // Centering the title

            createTitle("misle", g2d);

            // Play button

            int playButtonX = 196;
            int playButtonY = 123;
            int playButtonWidth = 119;
            int playButtonHeight = 41;
            Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

            if (!levelDesigner) {
                createButton(playButton, LanguageManager.getText("main_menu_play"), SaveSelector::saveSelectorMenu, panel, MenuButtonID.MAIN_MENU_PLAY);
            } else {
                createButton(playButton, LanguageManager.getText("main_menu_level_designer"), MainRenderer::enterLevelDesigner, panel, MenuButtonID.MAIN_MENU_LEVEL_DESIGNER);
            }

            // Quit button

            int quitButtonX = 265;
            int quitButtonY = 176;
            int quitButtonWidth = 51;
            int quitButtonHeight = 41;
            Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

            createButton(quitButton, LanguageManager.getText("main_menu_quit"), MenuRenderer::quitGame, panel, MenuButtonID.MAIN_MENU_QUIT);


            // Options menu

            int optionsButtonX = 196;
            int optionsButtonY = 176;
            int optionsButtonWidth = 51;
            int optionsButtonHeight = 41;
            Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

            createButton(optionsButton, LanguageManager.getText("main_menu_options"), MenuRenderer::optionsMenu, panel, MenuButtonID.MAIN_MENU_SETTINGS);

            drawButtons(g2d);

            // Version
            String gameVersion = LanguageManager.getText("version");
            String gameVersionShadow = LanguageManager.getText("version_shadow");
            FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
            int versionX = originalScreenWidth - fm.stringWidth(gameVersion) / 2;
            int versionY = originalScreenHeight - fm.getHeight() / 2;
            drawColoredText(g2d, gameVersionShadow, versionX + textShadow, versionY + textShadow, itemInfoFont, menuVersionShadowColor, false);
            drawColoredText(g2d, gameVersion, versionX, versionY, itemInfoFont, menuVersionColor, false);
        }
    }

    public static void renderPauseMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            if (antiAliasing.bool()) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }

            // BACKGROUND
            drawMenuBackground(g2d);

            // MENU ITSELF

            createTitle("pause_menu_paused", g2d);

            // Resume button

            int playButtonX = 196;
            int playButtonY = 123;
            int playButtonWidth = 119;
            int playButtonHeight = 41;
            Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

            createButton(playButton, LanguageManager.getText("pause_menu_resume"), MainRenderer::softGameStart, panel, MenuButtonID.PAUSE_MENU_RESUME);

            // Quit button

            int quitButtonX = 265;
            int quitButtonY = 176;
            int quitButtonWidth = 51;
            int quitButtonHeight = 41;
            Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

            createButton(quitButton, LanguageManager.getText("pause_menu_quit"), MenuRenderer::goToMainMenu, panel, MenuButtonID.PAUSE_MENU_QUIT);

            // Options menu

            int optionsButtonX = 196;
            int optionsButtonY = 176;
            int optionsButtonWidth = 51;
            int optionsButtonHeight = 41;
            Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

            createButton(optionsButton, LanguageManager.getText("pause_menu_settings"), MenuRenderer::optionsMenu, panel, MenuButtonID.PAUSE_MENU_SETTINGS);

            drawButtons(g2d);
        }
    }

    public static void renderLoadingMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            if (antiAliasing.bool()) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }

            // BACKGROUND
            drawMenuBackground(g2d);

            // MENU ITSELF
            createTitle("loading_menu_loading", g2d);
            g2d.setFont(FontManager.titleFont);
            FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
            String titleText = LanguageManager.getText("loading_menu_loading");
            fm.stringWidth(titleText);
            int textY = 49;

            // Progress bar
            long elapsedTime = currentTimeMillis() - MainRenderer.startTime;
            double progress = Math.min((double) elapsedTime / MainRenderer.LOADING_DURATION, 1.0); // Calculate progress (0.0 to 1.0)
            String percentage = (int) (progress * 100) + "%";

            int fullProgressBarWidth = 171;
            int progressBarWidth = (int) (fullProgressBarWidth * progress);
            int progressBarHeight = 7;
            int progressBarX = 176;
            int progressBarY = textY + 149;

            final int shadowExtra = 2;
            final int shadowWidth = fullProgressBarWidth + shadowExtra;
            final int shadowHeight = progressBarHeight + shadowExtra;
            final int shadowX = progressBarX - shadowExtra / 2;
            final int shadowY = progressBarY - shadowExtra / 2;

            g2d.setColor(progressBarShadowColor);
            g2d.fillRect(shadowX, shadowY, shadowWidth, shadowHeight);

            g2d.setColor(progressBarColor);
            g2d.fillRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);

            g2d.setFont(FontManager.selectedItemNameFont);
            FontMetrics percentageFm = FontManager.getCachedMetrics(g2d, g2d.getFont());
            int textWidth = percentageFm.stringWidth(percentage);
            int centerX = (originalScreenWidth - textWidth) / 2;
            textY = progressBarY - 5;
            g2d.setColor(progressBarPercentageShadow);
            drawColoredText(g2d, percentage, (int) (centerX + MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow));
            g2d.setColor(progressBarPercentage);
            drawColoredText(g2d, percentage, centerX, textY);

            fader.drawFading(g2d);
        }
    }

    public static void drawMenuBackground(Graphics2D g2d) {
        g2d.drawImage(cachedImages.get(MAIN_MENU_BACKGROUND), 0, 0, originalScreenWidth, originalScreenHeight, null);
    }
}
