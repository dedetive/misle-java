package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.util.ConcurrentModificationException;

import static com.ded.misle.Launcher.antiAliasing;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.GameState.SAVE_SELECTOR;
import static com.ded.misle.core.GamePanel.gameState;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.MainRenderer.gameStart;
import static com.ded.misle.renderer.MainRenderer.textShadow;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.MenuRenderer.createTitle;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;

public class SaveSelector {

    public static void saveSelectorMenu() {
        MainRenderer.previousMenu = MainRenderer.currentMenu;
        MainRenderer.currentMenu = SAVE_SELECTOR;
        gameState = SAVE_SELECTOR;
        clearButtonFading();
    }

    public static void renderSaveSelector(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            if (antiAliasing) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }

            // BACKGROUND
            drawMenuBackground(g2d);

            // MENU ITSELF
                // Title
            createTitle("Saves", g2d, scale / 3.75);

                // Save buttons
            int buttonX = (int) (64 * scale);
            int buttonY = (int) (86 * scale);
            int buttonWidth = (int) (120 * scale);
            int buttonHeight = (int) (120 * scale);
            int buttonSpacing = (int) (12 * scale);
            Rectangle button;
            int id = 300;
            for (int i = 0; i < 3; i++) {
                button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
                int finalI = i;
                Runnable runnable = () -> gameStart(finalI);
                createButton(button, "", runnable, panel, id);
                buttonX += buttonWidth + buttonSpacing;
                id++;
            }


                // Go back button
            buttonX = (int) (356 * scale);
            buttonY = (int) (220 * Math.pow(scale, 1.04));
            buttonWidth = (int) (109 * scale);
            buttonHeight = (int) (31 * scale);
            button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            createButton(button, LanguageManager.getText("settings_menu_go_back"), MenuRenderer::goToPreviousMenu, panel, 400);

            try {
                drawButtons(g2d, scale / 3.75);

                buttonX = (int) (64 * scale);
                buttonY = (int) (106 * scale);
                buttonWidth = (int) (120 * scale / 2);
                int backgroundSize = (int) (14 * scale);

                for (int i = 0; i < 3; i++) {

                    // Background
                    g2d.setColor(saveSelectorTextBackground);
                    g2d.fillRoundRect((int) (buttonX + buttonWidth - backgroundSize * 0.25), (int) (buttonY - backgroundSize * 0.8),
                        backgroundSize, backgroundSize, (int) (4 * scale), (int) (4 * scale));

                    // Shadow
                    g2d.setColor(saveSelectorTextShadow);
                    g2d.drawString(String.valueOf(i), (int) (buttonX + (double) buttonWidth + textShadow), (int) (buttonY + textShadow));
                    // Number
                    g2d.setColor(saveSelectorNumber);
                    g2d.drawString(String.valueOf(i), buttonX + buttonWidth, buttonY);

                    buttonX += buttonWidth * 2 + buttonSpacing;
                }
            } catch (ConcurrentModificationException e) {
                //
            }
        }
    }
}
