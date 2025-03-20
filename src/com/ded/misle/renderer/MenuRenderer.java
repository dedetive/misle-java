package com.ded.misle.renderer;

import com.ded.misle.core.GamePanel;
import com.ded.misle.core.LanguageManager;

import javax.swing.*;
import java.awt.*;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.core.GamePanel.*;
import static com.ded.misle.core.GamePanel.GameState.*;
import static com.ded.misle.core.Setting.antiAliasing;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.itemInfoFont;
import static com.ded.misle.renderer.ImageManager.ImageName.MAIN_MENU_BACKGROUND;
import static com.ded.misle.renderer.ImageManager.cachedImages;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.core.SaveFile.saveEverything;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.SettingsMenuRenderer.switchToEmpty;
import static com.ded.misle.world.WorldLoader.unloadBoxes;
import static java.lang.System.currentTimeMillis;
import static com.ded.misle.core.GamePanel.screenWidth;
import static com.ded.misle.core.GamePanel.screenHeight;

public class MenuRenderer {
    static void createTitle(String text, Graphics2D g2d) {
        g2d.setFont(FontManager.titleFont);
        FontMetrics fm = g2d.getFontMetrics();
        String titleText = LanguageManager.getText(text);
        int textWidth = fm.stringWidth(titleText);
        int centerX = (int) ((screenWidth - textWidth) / 2);
        int textY = (int) (48 * scale);
        g2d.setColor(menuTitleShadowColor);
        g2d.drawString(titleText, (int) (centerX - MainRenderer.textShadow), textY); // Left
        g2d.drawString(titleText, (int) (centerX + MainRenderer.textShadow), textY); // Right
        g2d.drawString(titleText, centerX, (int) (textY - MainRenderer.textShadow)); // Up
        g2d.drawString(titleText, centerX, (int) (textY + MainRenderer.textShadow)); // Down
        g2d.drawString(titleText, (int) (centerX - MainRenderer.textShadow), (int) (textY - MainRenderer.textShadow)); // Left-up corner
        g2d.drawString(titleText, (int) (centerX + MainRenderer.textShadow), (int) (textY - MainRenderer.textShadow)); // Right-up corner
        g2d.drawString(titleText, (int) (centerX - MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow)); // Left-down corner
        g2d.drawString(titleText, (int) (centerX - MainRenderer.textShadow), (int) (textY - MainRenderer.textShadow)); // Right-down corner
        g2d.setColor(menuTitleColor);
        g2d.drawString(titleText, centerX, textY);
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
    }

    public static void goToMainMenu() {
        saveEverything();
        unloadBoxes();
        player.unloadPlayer();
        MainRenderer.previousMenu = MainRenderer.currentMenu;
        gameState = MAIN_MENU;
        MainRenderer.fadingProgress = 0F;
        isFading = FadingState.UNFADED;
        clearButtonFading();
    }

    public static void pauseGame() {
        MainRenderer.previousMenu = MainRenderer.currentMenu;
        MainRenderer.currentMenu = PAUSE_MENU;
        gameState = PAUSE_MENU;
        clearButtonFading();
    }

    public static void renderMainMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            if (antiAliasing.bool()) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }

            MainRenderer.currentMenu = MAIN_MENU;

            double scaleByScreenSize = scale / 3.75;

            // BACKGROUND
            drawMenuBackground(g2d);

            // MENU ITSELF

            // Centering the title

            createTitle("misle", g2d);

            // Play button

            int playButtonX = (int) (736 * scaleByScreenSize);
            int playButtonY = (int) (462 * scaleByScreenSize);
            int playButtonWidth = (int) (448 * scaleByScreenSize);
            int playButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

            if (!levelDesigner) {
//                createButton(playButton, LanguageManager.getText("main_menu_play"), MainRenderer::gameStart, panel, 12838);
                createButton(playButton, LanguageManager.getText("main_menu_play"), SaveSelector::saveSelectorMenu, panel, 12838);
            } else {
                createButton(playButton, LanguageManager.getText("main_menu_level_designer"), MainRenderer::enterLevelDesigner, panel, 12839);
            }

            // Quit button

            int quitButtonX = (int) (992 * scaleByScreenSize);
            int quitButtonY = (int) (660 * scaleByScreenSize);
            int quitButtonWidth = (int) (192 * scaleByScreenSize);
            int quitButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

            createButton(quitButton, LanguageManager.getText("main_menu_quit"), MenuRenderer::quitGame, panel, 12732);


            // Options menu

            int optionsButtonX = (int) (736 * scaleByScreenSize);
            int optionsButtonY = (int) (660 * scaleByScreenSize);
            int optionsButtonWidth = (int) (192 * scaleByScreenSize);
            int optionsButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

            createButton(optionsButton, LanguageManager.getText("main_menu_options"), MenuRenderer::optionsMenu, panel, 12783);

            drawButtons(g2d);

            // Version

            String gameVersion = LanguageManager.getText("version");
            drawColoredText(g2d, gameVersion, (int) (1640 * scaleByScreenSize + textShadow), (int) (1010* Math.pow(scaleByScreenSize, 1.04) + textShadow), itemInfoFont, menuVersionShadowColor, true);
            drawColoredText(g2d, gameVersion, (int) (1640 * scaleByScreenSize), (int) (1010* Math.pow(scaleByScreenSize, 1.04)), itemInfoFont, menuVersionColor, false);
        }
    }

    public static void renderPauseMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            if (antiAliasing.bool()) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }

            double scaleByScreenSize = scale / 3.75;

            // BACKGROUND
            drawMenuBackground(g2d);

            // MENU ITSELF

            createTitle("pause_menu_paused", g2d);

            // Resume button

            int playButtonX = (int) (736 * scaleByScreenSize);
            int playButtonY = (int) (462 * scaleByScreenSize);
            int playButtonWidth = (int) (448 * scaleByScreenSize);
            int playButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

            createButton(playButton, LanguageManager.getText("pause_menu_resume"), MainRenderer::softGameStart, panel, 2738);

            // Quit button

            int quitButtonX = (int) (992 * scaleByScreenSize);
            int quitButtonY = (int) (660 * scaleByScreenSize);
            int quitButtonWidth = (int) (192 * scaleByScreenSize);
            int quitButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

            createButton(quitButton, LanguageManager.getText("pause_menu_quit"), MenuRenderer::goToMainMenu, panel, 7384);

            // Options menu

            int optionsButtonX = (int) (736 * scaleByScreenSize);
            int optionsButtonY = (int) (660 * scaleByScreenSize);
            int optionsButtonWidth = (int) (192 * scaleByScreenSize);
            int optionsButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

            createButton(optionsButton, LanguageManager.getText("pause_menu_settings"), MenuRenderer::optionsMenu, panel, 8734);

            drawButtons(g2d);
        }
    }

    public static void renderLoadingMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            if (antiAliasing.bool()) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }

            double scaleByScreenSize = scale / 3.75;

            // BACKGROUND
            drawMenuBackground(g2d);

            // MENU ITSELF
            createTitle("loading_menu_loading", g2d);
            g2d.setFont(FontManager.titleFont);
            FontMetrics fm = g2d.getFontMetrics();
            String titleText = LanguageManager.getText("loading_menu_loading");
            fm.stringWidth(titleText);
            int textY = (int) (182 * scaleByScreenSize);

            // Progress bar
            long elapsedTime = currentTimeMillis() - MainRenderer.startTime;
            double progress = Math.min((double) elapsedTime / MainRenderer.LOADING_DURATION, 1.0); // Calculate progress (0.0 to 1.0)
            String percentage = (int) (progress * 100) + "%";

            int fullProgressBarWidth = (int) (640 * scaleByScreenSize);
            int progressBarWidth = (int) (fullProgressBarWidth * progress);
            int progressBarHeight = (int) (25 * scaleByScreenSize);
            int progressBarX = (int) (660 * scaleByScreenSize);
            int progressBarY = (int) ((textY + 560) * scaleByScreenSize);

            final int shadowExtra = (int) (2 * scale);
            final int shadowWidth = fullProgressBarWidth + shadowExtra;
            final int shadowHeight = progressBarHeight + shadowExtra;
            final int shadowX = progressBarX - shadowExtra / 2;
            final int shadowY = progressBarY - shadowExtra / 2;

            g2d.setColor(progressBarShadowColor);
            g2d.fillRect(shadowX, shadowY, shadowWidth, shadowHeight);

            g2d.setColor(progressBarColor);
            g2d.fillRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);

            g2d.setFont(FontManager.selectedItemNameFont);
            FontMetrics percentageFm = g2d.getFontMetrics();
            int textWidth = percentageFm.stringWidth(percentage);
            int centerX = (int) ((screenWidth - textWidth) / 2);
            textY = (int) ((progressBarY) - 20 * scaleByScreenSize);
            g2d.setColor(progressBarPercentageShadow);
            g2d.drawString(percentage, (int) (centerX + MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow));
            g2d.setColor(progressBarPercentage);
            g2d.drawString(percentage, centerX, textY);

            if (isFading != MainRenderer.FadingState.UNFADED) drawFading(g2d);
        }
    }

    public static void drawMenuBackground(Graphics2D g2d) {
//        g2d.setColor(menuBackgroundColor);
//        g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

        g2d.drawImage(cachedImages.get(MAIN_MENU_BACKGROUND), 0, 0, (int) screenWidth, (int) screenHeight, null);
    }
}
