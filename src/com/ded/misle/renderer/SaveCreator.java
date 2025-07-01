package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static com.ded.misle.game.GamePanel.player;
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
            FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
            String text = playerName.toString();
            int textWidth = fm.stringWidth(text);
            int width = 220;
            int textX = 256;
            int textY = 100;

            g2d.setColor(saveCreatorTextBackground);
            g2d.fillRoundRect(textX - width / 2, textY - 7 * fm.getHeight() / 9,
                (int) (width * 1.05), fm.getHeight(), 16, 16);

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
            textX = (int) (256 - (double) textWidth / 2);
            g2d.setColor(saveCreatorInsertNameShadow);
            g2d.drawString(text, (int) (textX + textShadow), (int) (textY - 20 + textShadow));
            g2d.setColor(saveCreatorInsertName);
            g2d.drawString(text, textX, textY - 20);

                // Transparent 'Player name'

            if (playerName.isEmpty()) {
                text = LanguageManager.getText("save_creator_player_name");
                textWidth = fm.stringWidth(text);
                textX = (int) (256 - (double) textWidth / 2);
                g2d.setColor(saveCreatorPlayerName);
                g2d.drawString(text, textX, textY);
            } else {

                // Caret

                if (System.currentTimeMillis() % 1600 <= 800) {

                    int x = (int) (256 + (double) fm.stringWidth(playerName.toString()) / 2 + 2);
                    int y = textY - 2 * fm.getHeight() / 3;
                    g2d.setColor(saveCreatorCaret);
                    g2d.fillRect(x, y, 1, 4 * fm.getHeight() / 5);

                }

            }

                // Confirm name button
            buttonWidth = (int) (width * 1.05);
            buttonHeight = fm.getHeight();
            buttonX = 256 - buttonWidth / 2;
            buttonY = 140;
            Rectangle buttonRect = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            Runnable runnable = SaveCreator::confirmName;

            createButton(buttonRect, LanguageManager.getText("save_creator_confirm_button"), runnable, panel, MenuButtonID.SAVE_CREATOR_MENU_CONFIRM_NAME);

                // Add image
            buttonWidth = 40;
            buttonHeight = buttonWidth;
            buttonY = 184;
            buttonRect = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
            runnable = SaveCreator::handleIcon;
            createButton(buttonRect, "", runnable, panel, MenuButtonID.SAVE_CREATOR_MENU_ADD_ICON);

                // Go back button
            createGoBackButton(panel, MenuButtonID.SAVE_CREATOR_MENU_GO_BACK);

            if (!isIconActive) {
                drawButtons(g2d);

                    // Add image plus
                g2d.setColor(saveSelectorTextBackground);
                g2d.fillRoundRect((int) (buttonX + (double) buttonWidth / 2 - 1),
                    (int) (buttonY + (double) buttonHeight / 5 + 1),
                    4, buttonHeight / 2, 3, 3);

                g2d.fillRoundRect((int) (buttonX + (double) buttonWidth / 2 - 9),
                    (int) (buttonY + (double) buttonHeight / 4 + 8),
                    buttonHeight / 2, 4, 3, 3);
            } else {
                    // Clear icon
                buttonRect = new Rectangle(buttonX, buttonY + 9 * buttonHeight / 8, buttonWidth, buttonHeight / 4);
                runnable = SaveCreator::clearIcon;

                createButton(buttonRect, LanguageManager.getText("save_creator_clear_icon"), runnable, panel, MenuButtonID.SAVE_CREATOR_MENU_CLEAR_ICON);

                drawButtons(g2d);

                RoundRectangle2D clip = new RoundRectangle2D.Double(buttonX, buttonY, buttonWidth, buttonHeight, 17, 17);
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
