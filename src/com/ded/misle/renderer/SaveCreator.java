package com.ded.misle.renderer;

import javax.swing.*;
import java.awt.*;

import static com.ded.misle.Launcher.antiAliasing;
import static com.ded.misle.renderer.MenuButton.createGoBackButton;
import static com.ded.misle.renderer.MenuButton.drawButtons;
import static com.ded.misle.renderer.MenuRenderer.createTitle;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;

public class SaveCreator {
    public static int creatingSave = -1;

    public static void renderSaveCreator(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            if (antiAliasing) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }

            // BACKGROUND
            drawMenuBackground(g2d);

            // MENU ITSELF
            // Title
            createTitle("New Game", g2d);

            int buttonX;
            int buttonY;
            int buttonWidth;
            int buttonHeight;
            Rectangle button;



            // Go back button
            createGoBackButton(panel, 400);

            drawButtons(g2d);
        }
    }
}
