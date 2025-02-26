package com.ded.misle.renderer;

import javax.swing.*;
import java.awt.*;

import static com.ded.misle.Launcher.antiAliasing;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.dialogNPCText;
import static com.ded.misle.renderer.MainRenderer.textShadow;
import static com.ded.misle.renderer.MenuButton.createGoBackButton;
import static com.ded.misle.renderer.MenuButton.drawButtons;
import static com.ded.misle.renderer.MenuRenderer.createTitle;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;

public class SaveCreator {
    public static int creatingSave = -1;
    public static StringBuilder playerName = new StringBuilder("PlayerName");

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

                // Text background

            g2d.setFont(dialogNPCText);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(playerName.toString());
            int textX = (int) (((double) 512 / 2) * scale - (double) textWidth / 2);
            int textY = (int) (100 * scale);

            g2d.setColor(saveCreatorTextBackground);
            g2d.fillRoundRect(textX - textWidth / 2, textY - 7 * fm.getHeight() / 9,
                textWidth * 2, fm.getHeight(), (int) (16 * scale), (int) (16 * scale));

                // Text
            g2d.setColor(saveCreatorTextShadow);
            g2d.drawString(playerName.toString(), (int) (textX + textShadow), (int) (textY + textShadow));
            g2d.setColor(saveCreatorText);
            g2d.drawString(playerName.toString(), textX, textY);

            // Go back button
            createGoBackButton(panel, 400);

            drawButtons(g2d);
        }
    }
}
