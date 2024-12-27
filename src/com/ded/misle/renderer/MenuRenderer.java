package com.ded.misle.renderer;

import com.ded.misle.GamePanel;
import com.ded.misle.LanguageManager;

import javax.swing.*;
import java.awt.*;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.levelDesigner;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.GameRenderer.*;
import static com.ded.misle.renderer.MenuButton.createButton;
import static com.ded.misle.renderer.MenuButton.drawButtons;
import static com.ded.misle.SaveFile.saveEverything;
import static com.ded.misle.boxes.WorldLoader.unloadBoxes;
import static java.lang.System.currentTimeMillis;
import static com.ded.misle.GamePanel.screenWidth;
import static com.ded.misle.GamePanel.screenHeight;

public class MenuRenderer {
    private static final String gameVersion = "v0.1.5-alpha";

    private static void createTitle(String text, Graphics2D g2d, double scaleByScreenSize) {
        g2d.setFont(FontManager.titleFont);
        FontMetrics fm = g2d.getFontMetrics();
        String titleText = LanguageManager.getText(text);
        int textWidth = fm.stringWidth(titleText);
        int centerX = (int) ((screenWidth - textWidth) / 2);
        int textY = (int) (182 * scaleByScreenSize);
        g2d.setColor(menuTitleShadowColor);
        g2d.drawString(titleText, (int) (centerX - GameRenderer.textShadow), textY); // Left
        g2d.drawString(titleText, (int) (centerX + GameRenderer.textShadow), textY); // Right
        g2d.drawString(titleText, centerX, (int) (textY - GameRenderer.textShadow)); // Up
        g2d.drawString(titleText, centerX, (int) (textY + GameRenderer.textShadow)); // Down
        g2d.drawString(titleText, (int) (centerX - GameRenderer.textShadow), (int) (textY - GameRenderer.textShadow)); // Left-up corner
        g2d.drawString(titleText, (int) (centerX + GameRenderer.textShadow), (int) (textY - GameRenderer.textShadow)); // Right-up corner
        g2d.drawString(titleText, (int) (centerX - GameRenderer.textShadow), (int) (textY + GameRenderer.textShadow)); // Left-down corner
        g2d.drawString(titleText, (int) (centerX - GameRenderer.textShadow), (int) (textY - GameRenderer.textShadow)); // Right-down corner
        g2d.setColor(menuTitleColor);
        g2d.drawString(titleText, centerX, textY);
    }

    public static void quitGame() {
        GamePanel.quitGame();
    }

    public static void optionsMenu() {
        GameRenderer.previousMenu = GameRenderer.currentMenu;
        GameRenderer.currentMenu = "OPTIONS_MENU";
        gameState = GamePanel.GameState.OPTIONS_MENU;
    }

    public static void goToPreviousMenu() {
        switch (GameRenderer.previousMenu) {
            case "MAIN_MENU":
                gameState = GamePanel.GameState.MAIN_MENU;
                break;
            case "OPTIONS_MENU":
                gameState = GamePanel.GameState.OPTIONS_MENU;
                break;
            case "PLAYING":
                gameState = GamePanel.GameState.PLAYING;
                break;
            case "PAUSE_MENU":
                gameState = GamePanel.GameState.PAUSE_MENU;
        }
        GameRenderer.previousMenu = GameRenderer.currentMenu;
    }

    public static void goToMainMenu() {
        saveEverything();
        unloadBoxes();
        player.unloadPlayer();
        GameRenderer.previousMenu = GameRenderer.currentMenu;
        gameState = GameState.MAIN_MENU;
        fadingProgress = 0F;
        isFading = FadingState.UNFADED;
    }

    public static void pauseGame() {
        GameRenderer.previousMenu = GameRenderer.currentMenu;
        GameRenderer.currentMenu = "PAUSE_MENU";
        gameState = GameState.PAUSE_MENU;
    }

    public static void renderMainMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            GameRenderer.currentMenu = "MAIN_MENU";

            double scaleByScreenSize = scale / 3.75;

            // BACKGROUND

            g2d.setColor(menuBackgroundColor);
            g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

            // MENU ITSELF

            // Centering the title

            createTitle("misle", g2d, scaleByScreenSize);

            // Play button

            int playButtonX = (int) (736 * scaleByScreenSize);
            int playButtonY = (int) (462 * scaleByScreenSize);
            int playButtonWidth = (int) (448 * scaleByScreenSize);
            int playButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

            if (!levelDesigner) {
                createButton(playButton, LanguageManager.getText("main_menu_play"), GameRenderer::gameStart, panel);
            } else {
                createButton(playButton, LanguageManager.getText("main_menu_level_designer"), GameRenderer::enterLevelDesigner, panel);
            }

            // Quit button

            int quitButtonX = (int) (992 * scaleByScreenSize);
            int quitButtonY = (int) (660 * scaleByScreenSize);
            int quitButtonWidth = (int) (192 * scaleByScreenSize);
            int quitButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

            createButton(quitButton, LanguageManager.getText("main_menu_quit"), MenuRenderer::quitGame, panel);


            // Options menu

            int optionsButtonX = (int) (736 * scaleByScreenSize);
            int optionsButtonY = (int) (660 * scaleByScreenSize);
            int optionsButtonWidth = (int) (192 * scaleByScreenSize);
            int optionsButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

            createButton(optionsButton, LanguageManager.getText("main_menu_options"), MenuRenderer::optionsMenu, panel);

            drawButtons(g2d, scaleByScreenSize);

            // Version

            g2d.setFont(FontManager.itemInfoFont);
            g2d.setColor(menuVersionShadowColor);
            g2d.drawString(gameVersion, (int) (1640 * scaleByScreenSize + GameRenderer.textShadow), (int) (1010* Math.pow(scaleByScreenSize, 1.04) + GameRenderer.textShadow));
            g2d.setColor(menuVersionColor);
            g2d.drawString(gameVersion, (int) (1640 * scaleByScreenSize), (int) (1010* Math.pow(scaleByScreenSize, 1.04)));
        }
    }

    public static void renderPauseMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            double scaleByScreenSize = scale / 3.75;

            // BACKGROUND

            g2d.setColor(menuBackgroundColor);
            g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

            // MENU ITSELF

            createTitle("pause_menu_paused", g2d, scaleByScreenSize);

            // Resume button

            int playButtonX = (int) (736 * scaleByScreenSize);
            int playButtonY = (int) (462 * scaleByScreenSize);
            int playButtonWidth = (int) (448 * scaleByScreenSize);
            int playButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

            createButton(playButton, LanguageManager.getText("pause_menu_resume"), GameRenderer::softGameStart, panel);

            // Quit button

            int quitButtonX = (int) (992 * scaleByScreenSize);
            int quitButtonY = (int) (660 * scaleByScreenSize);
            int quitButtonWidth = (int) (192 * scaleByScreenSize);
            int quitButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle quitButton = new Rectangle(quitButtonX, quitButtonY, quitButtonWidth, quitButtonHeight);

            createButton(quitButton, LanguageManager.getText("pause_menu_quit"), MenuRenderer::goToMainMenu, panel);

            // Options menu

            int optionsButtonX = (int) (736 * scaleByScreenSize);
            int optionsButtonY = (int) (660 * scaleByScreenSize);
            int optionsButtonWidth = (int) (192 * scaleByScreenSize);
            int optionsButtonHeight = (int) (155 * scaleByScreenSize);
            Rectangle optionsButton = new Rectangle(optionsButtonX, optionsButtonY, optionsButtonWidth, optionsButtonHeight);

            createButton(optionsButton, LanguageManager.getText("pause_menu_options"), MenuRenderer::optionsMenu, panel);

            drawButtons(g2d, scaleByScreenSize);
        }
    }

    public static void renderOptionsMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            double scaleByScreenSize = scale / 3.75;

            // BACKGROUND

            g2d.setColor(menuBackgroundColor);
            g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

            // MENU ITSELF

            createTitle("options_menu_options", g2d, scaleByScreenSize);

            // Go back button

            int playButtonX = (int) (1338 * scaleByScreenSize);
            int playButtonY = (int) (883 * Math.pow(scaleByScreenSize, 1.04));
            int playButtonWidth = (int) (407 * scaleByScreenSize);
            int playButtonHeight = (int) (116 * scaleByScreenSize);
            Rectangle playButton = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

            createButton(playButton, LanguageManager.getText("options_menu_go_back"), MenuRenderer::goToPreviousMenu, panel);

            drawButtons(g2d, scaleByScreenSize);
        }
    }

    public static void renderLoadingMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            double scaleByScreenSize = scale / 3.75;

            // BACKGROUND
            g2d.setColor(menuBackgroundColor);
            g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

            // MENU ITSELF
            createTitle("loading_menu_loading", g2d, scaleByScreenSize);
            g2d.setFont(FontManager.titleFont);
            FontMetrics fm = g2d.getFontMetrics();
            String titleText = LanguageManager.getText("loading_menu_loading");
            fm.stringWidth(titleText);
            int textY = (int) (182 * scaleByScreenSize);

            // Progress bar
            long elapsedTime = currentTimeMillis() - GameRenderer.startTime;
            double progress = Math.min((double) elapsedTime / GameRenderer.LOADING_DURATION, 1.0); // Calculate progress (0.0 to 1.0)
            String percentage = (int) (progress * 100) + "%";

            int progressBarWidth = (int) (640 * progress * scaleByScreenSize);
            int progressBarHeight = (int) (25 * scaleByScreenSize);
            int progressBarY = (int) ((textY + 560) * scaleByScreenSize);

            g2d.setColor(progressBarColor);
            g2d.fillRect((int) (660 * scaleByScreenSize), progressBarY, progressBarWidth, progressBarHeight);

            g2d.setFont(FontManager.selectedItemNameFont);
            FontMetrics percentageFm = g2d.getFontMetrics();
            int textWidth = percentageFm.stringWidth(percentage);
            int centerX = (int) ((screenWidth - textWidth) / 2);
            textY = (int) ((progressBarY) - 20 * scaleByScreenSize);
            g2d.setColor(progressBarPercentageShadow);
            g2d.drawString(percentage, (int) (centerX + GameRenderer.textShadow), (int) (textY + GameRenderer.textShadow));
            g2d.setColor(progressBarPercentage);
            g2d.drawString(percentage, centerX, textY);

            if (isFading != GameRenderer.FadingState.UNFADED) drawFading(g2d);
        }
    }
}
