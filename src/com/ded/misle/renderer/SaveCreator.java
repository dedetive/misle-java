package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.Setting.antiAliasing;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.dialogNPCText;
import static com.ded.misle.renderer.MainRenderer.gameStart;
import static com.ded.misle.renderer.MainRenderer.textShadow;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.MenuRenderer.createTitle;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;

public abstract class SaveCreator {
    public static int creatingSave = -1;
    public static StringBuilder playerName = new StringBuilder();
    public static String saveCreationWarning = "";

    public static void renderSaveCreator(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

            // ANTI-ALIASING
            if (antiAliasing.bool()) {
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
            int width = (int) (220 * scale);
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

                // Transparent 'Player name'

            if (playerName.isEmpty()) {
                text = LanguageManager.getText("save_creator_player_name");
                textWidth = fm.stringWidth(text);
                textX = (int) (((double) 512 / 2) * scale - (double) textWidth / 2);
                g2d.setColor(saveCreatorPlayerName);
                g2d.drawString(text, textX, textY);
            } else {

                // Caret

                if (System.currentTimeMillis() % 1600 <= 800) {

                    int x = (int) (((double) 512 / 2) * scale + (double) fm.stringWidth(playerName.toString()) / 2 + 2 * scale);
                    int y = textY - 2 * fm.getHeight() / 3;
                    g2d.setColor(saveCreatorCaret);
                    g2d.fillRect(x, y, (int) (1 * scale), 4 * fm.getHeight() / 5);

                }

            }

                // Confirm name button
            buttonWidth = (int) (width * 1.05);
            buttonHeight = fm.getHeight();
            buttonX = (int) ((double) 512 / 2 * scale) - buttonWidth / 2;
            buttonY = (int) (140 * scale);
            Rectangle buttonRect = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            Runnable runnable = SaveCreator::confirmName;

            createButton(buttonRect, LanguageManager.getText("save_creator_confirm_button"), runnable, panel, 127);

                // Add image
            buttonWidth = (int) (40 * scale);
            buttonHeight = buttonWidth;
            buttonY = (int) (184 * scale);
            buttonRect = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            runnable = SaveCreator::handleIcon;
            createButton(buttonRect, "", runnable, panel, 120);

                // Go back button
            createGoBackButton(panel, 400);

            if (!isIconActive) {
                drawButtons(g2d);

                    // Add image plus
                g2d.setColor(saveSelectorTextBackground);
                g2d.fillRoundRect((int) (buttonX + (double) buttonWidth / 2 - 1 * scale),
                    (int) (buttonY + (double) buttonHeight / 5 + 1 * scale),
                    (int) (4 * scale), buttonHeight / 2, (int) (3 * scale), (int) (3 * scale));

                g2d.fillRoundRect((int) (buttonX + (double) buttonWidth / 2 - 9 * scale),
                    (int) (buttonY + (double) buttonHeight / 4 + 8 * scale),
                    buttonHeight / 2, (int) (4 * scale), (int) (3 * scale), (int) (3 * scale));
            } else {
                    // Clear icon
                buttonRect = new Rectangle(buttonX, buttonY + 9 * buttonHeight / 8, buttonWidth, buttonHeight / 4);
                runnable = SaveCreator::clearIcon;

                createButton(buttonRect, LanguageManager.getText("save_creator_clear_icon"), runnable, panel, 129);

                drawButtons(g2d);

                RoundRectangle2D clip = new RoundRectangle2D.Double(buttonX, buttonY, buttonWidth, buttonHeight, 17 * scale, 17 * scale);
                g2d.setClip(clip);
                g2d.drawImage(icon, buttonX, buttonY, buttonWidth, buttonHeight, null);
                g2d.setClip(null);
            }
        }
    }

    public static void confirmName() {
        boolean canConfirm = isNameValid();

        if (canConfirm) {
            clearButtons();
            gameStart(creatingSave);
            player.name = playerName.toString();
            if (isIconActive) {
                player.icon = icon;
                player.isIconActive = true;
            }
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

    private static BufferedImage icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    private static boolean isIconActive = false;

    private static void handleIcon() {
            // Get image from user input
        icon = ImageManager.requestImage();
            // Modify to be 16x16
        try {
            Image targetImage = icon.getScaledInstance(16, 16, Image.SCALE_DEFAULT);
            int width = targetImage.getWidth(null);
            int height = targetImage.getHeight(null);
            icon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            icon.getGraphics().drawImage(targetImage, 0, 0, null);
            // Activate icon image
            isIconActive = true;
        } catch (NullPointerException e) {
            // This means image request was cancelled
        }
    }

    public static void clearIcon() {
        isIconActive = false;
    }
}
