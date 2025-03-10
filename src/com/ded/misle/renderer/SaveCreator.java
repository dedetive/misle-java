package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static com.ded.misle.Launcher.antiAliasing;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.dialogNPCText;
import static com.ded.misle.renderer.MainRenderer.gameStart;
import static com.ded.misle.renderer.MainRenderer.textShadow;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.MenuRenderer.createTitle;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;

public class SaveCreator {
    public static int creatingSave = -1;
    public static StringBuilder playerName = new StringBuilder();
    public static String saveCreationWarning = "";

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
            createTitle(LanguageManager.getText("save_creator_title"), g2d);

            int buttonX;
            int buttonY;
            int buttonWidth;
            int buttonHeight;

            // Text background
            g2d.setFont(dialogNPCText);
            FontMetrics fm = g2d.getFontMetrics();
            String text = playerName.toString();
            int textWidth = fm.stringWidth(text);
            int width = fm.stringWidth("WWWWWWWWWWWWWWWW");
            int textX = (int) (((double) 512 / 2) * scale);
            int textY = (int) (100 * scale);

            g2d.setColor(saveCreatorTextBackground);
            g2d.fillRoundRect(textX - width / 2, textY - 7 * fm.getHeight() / 9,
                (int) (width * 1.05), fm.getHeight(), (int) (16 * scale), (int) (16 * scale));

                // Text
            g2d.setColor(saveCreatorTextShadow);
            g2d.drawString(text, (int) (textX + textShadow- (double) textWidth / 2), (int) (textY + textShadow));
            g2d.setColor(saveCreatorText);
            g2d.drawString(text, (int) (textX - (double) textWidth / 2), textY);

                // Warning
            g2d.setColor(saveCreatorWarning);
            textWidth = fm.stringWidth(saveCreationWarning);
            g2d.drawString(saveCreationWarning, (int) (textX - (double) textWidth / 2), textY + fm.getHeight());

                // Insert name text
            text = LanguageManager.getText("save_creator_instruction");
            textWidth = fm.stringWidth(text);
            textX = (int) (((double) 512 / 2) * scale - (double) textWidth / 2);
            g2d.setColor(saveCreatorInsertNameShadow);
            g2d.drawString(text, (int) (textX + textShadow), (int) (textY - 20 * scale + textShadow));
            g2d.setColor(saveCreatorInsertName);
            g2d.drawString(text, textX, (int) (textY - 20 * scale));

                // Confirm name button
            buttonWidth = (int) (width * 1.05);
            buttonHeight = fm.getHeight();
            buttonX = (int) ((double) 512 / 2 * scale) - buttonWidth / 2;
            buttonY = (int) (140 * scale);
            Rectangle buttonRect = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            Runnable runnable = SaveCreator::confirmName;

            createButton(buttonRect, LanguageManager.getText("save_creator_confirm_button"), runnable, panel, 120);

            // Go back button
            createGoBackButton(panel, 400);

            drawButtons(g2d);
        }
    }

    public static void confirmName() {
        boolean canConfirm = isNameValid();

        if (canConfirm) {
            clearButtons();
            gameStart(creatingSave);
            player.name = playerName.toString();
        } else {
            saveCreationWarning = LanguageManager.getText("save_creator_warning1");
        }
    }

    private static boolean isNameValid() {
        boolean isValid = true;

        if (playerName.toString().trim().isEmpty()) isValid = false;
        if (Objects.equals(playerName.toString().trim(), LanguageManager.getText("save_creator_cannot_be"))) isValid = false;
        System.out.println(playerName.toString().trim() + ", " + LanguageManager.getText("save_creator_cannot_be") + " = " + isValid);

        return isValid;
    }
}
