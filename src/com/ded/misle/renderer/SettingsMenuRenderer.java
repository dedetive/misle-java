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
    public static void renderOptionsMenu(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            double scaleByScreenSize = scale / 3.75;

            // BACKGROUND

            g2d.setColor(menuBackgroundColor);
            g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

            // MENU ITSELF

            MenuRenderer.createTitle("options_menu_options", g2d, scaleByScreenSize);

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
}
