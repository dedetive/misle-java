package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.core.SaveFile;
import com.ded.misle.items.Item;

import javax.swing.*;
import java.awt.*;
import java.util.ConcurrentModificationException;

import static com.ded.misle.Launcher.antiAliasing;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.GameState.*;
import static com.ded.misle.core.GamePanel.gameState;
import static com.ded.misle.core.SaveFile.*;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.*;
import static com.ded.misle.renderer.ImageRenderer.cachedImages;
import static com.ded.misle.renderer.InventoryRenderer.wrapText;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.MenuRenderer.createTitle;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;
import static com.ded.misle.renderer.PlayingRenderer.scaleByScreenSize;
import static com.ded.misle.renderer.SaveCreator.saveCreationWarning;
import static com.ded.misle.renderer.SaveCreator.playerName;

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
            createTitle(LanguageManager.getText("save_selector_title"), g2d);

            if (askingToDelete != -1) {
                askToDeleteSave(askingToDelete, panel, g2d);
                drawButtons(g2d);

            } else {
                // Save buttons
                int buttonX = (int) (64 * scale);
                int buttonY = (int) (86 * scale);
                int buttonWidth = (int) (120 * scale);
                int buttonHeight = (int) (120 * scale);
                int buttonSpacing = (int) (12 * scale);
                Rectangle buttonRect;
                int id = 300;
                boolean[] existingSaves = new boolean[3];
                for (int i = 0; i < 3; i++) {
                    boolean saveExists = (boolean) loadSaveScreenInformation(SaveFile.SaveScreenOption.EXISTS, i);
                    existingSaves[i] = saveExists;

                    buttonRect = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
                    int finalI = i;

                    Runnable runnable;

                    if (!saveExists) {
                        runnable = () -> {
                            SaveCreator.creatingSave = finalI;
                            MainRenderer.previousMenu = MainRenderer.currentMenu;
                            MainRenderer.currentMenu = MAIN_MENU;
                            gameState = SAVE_CREATOR;
                            saveCreationWarning = "";
                            playerName = new StringBuilder();
                            clearButtonFading();
                        };
                    } else {
                        runnable = () -> gameStart(finalI);
                    }


                    createButton(buttonRect, "", runnable, panel, id);

                    if (saveExists) {
                        buttonRect = new Rectangle(buttonX, (int) (buttonY + buttonHeight + 4 * scale), buttonWidth, (int) (15 * scale));
                        runnable = () -> askingToDelete = finalI;
                        createButton(buttonRect, LanguageManager.getText("save_selector_delete"), runnable, panel, id + 3);
                    }

                    buttonX += buttonWidth + buttonSpacing;
                    id++;
                }

                // Go back buttonRect
                createGoBackButton(panel, 400);

                try {
                    drawButtons(g2d);

                    buttonX = (int) (64 * scale);
                    buttonY = (int) (106 * scale);
                    buttonWidth = (int) (120 * scale / 2);
                    buttonHeight = (int) (120 * scale);
                    int backgroundSize = (int) (14 * scale);

                    for (int i = 0; i < 3; i++) {

                        // Background

                        g2d.setColor(saveSelectorTextBackground);
                        g2d.fillRoundRect((int) (buttonX + buttonWidth * 0.975 - backgroundSize * 0.25), (int) (buttonY - backgroundSize * 0.8),
                            backgroundSize, backgroundSize, (int) (4 * scale), (int) (4 * scale));

                        // Shadow

                        g2d.setColor(saveSelectorTextShadow);
                        g2d.drawString(String.valueOf(i + 1), (int) (buttonX + (double) buttonWidth * 0.975 + textShadow), (int) (buttonY + textShadow));

                        // Number

                        g2d.setColor(saveSelectorNumber);
                        g2d.drawString(String.valueOf(i + 1), (int) (buttonX + buttonWidth * 0.975), buttonY);

                        if (existingSaves[i]) {

                            // Player

                            g2d.drawImage(cachedImages.get(ImageRenderer.ImageName.PLAYER_FRONT0), (int) (buttonX + buttonWidth * 0.75),
                                (int) (buttonY - 40 * scale + (double) buttonHeight / 2), 135, 135, null);

                            // Level

                            int level = (int) loadSaveScreenInformation(SaveFile.SaveScreenOption.LEVEL, i);

                            String text = LanguageManager.getText("save_selector_level") + " " + level;
                            FontMetrics fm = g2d.getFontMetrics();
                            int textWidth = fm.stringWidth(text);

                            int x = buttonX + buttonWidth - textWidth / 2;
                            int y = (int) (buttonY + 20 * scale + (double) buttonHeight / 2);
                            g2d.drawString(text, x, y);

                            // Playtime

                            text = String.valueOf(loadSaveScreenInformation(SaveFile.SaveScreenOption.PLAYTIME, i));

                            textWidth = fm.stringWidth(text);

                            x = buttonX + buttonWidth - textWidth / 2;
                            y = (int) (buttonY + 20 * scale + (double) buttonHeight / 2 + fm.getHeight());
                            g2d.drawString(text, x, y);

                            // Name

                            text = String.valueOf(loadSaveScreenInformation(SaveFile.SaveScreenOption.NAME, i));

                            textWidth = fm.stringWidth(text);

                            x = buttonX + buttonWidth - textWidth / 2;
                            y = (int) (buttonY + 14 * scale);
                            g2d.drawString(text, x, y);

                            // Draw hand item

                            Item item = (Item) loadSaveScreenInformation(SaveFile.SaveScreenOption.FIRST_ITEM, i);

                            if (item.getId() != 0) {
                                drawRotatedImage(g2d, item.getIcon(), buttonX + buttonWidth * 1.1, buttonY - 37 * scale + (double) buttonHeight / 2,
                                    (int) (100 * scale / 3.75), (int) (100 * scale / 3.75), 0, false);
                            }

                        } else {

                            // Plus sign

                            g2d.setColor(saveSelectorTextBackground);
                            g2d.fillRoundRect((int) (buttonX + buttonWidth + 2 * scale), buttonY + buttonHeight / 5,
                                (int) (4 * scale), buttonHeight / 4, (int) (3 * scale), (int) (3 * scale));
                            g2d.fillRoundRect((int) (buttonX + buttonWidth * 0.8), (int) (buttonY + (double) buttonHeight / 4 + 7 * scale),
                                buttonHeight / 4, (int) (4 * scale), (int) (3 * scale), (int) (3 * scale));
                        }

                        buttonX += buttonWidth * 2 + buttonSpacing;
                    }

                } catch (ConcurrentModificationException e) {
                    //
                }
            }
        }
    }

    public static int askingToDelete = -1;
    public static void askToDeleteSave(int saveSlot, JPanel panel, Graphics2D g2d) {

        int previewX = (int) (112 * scale);
        int previewY = (int) (86 * scale);
        int previewWidth = (int) (120 * scale);
        int previewHeight = (int) (120 * scale);
        int buttonBorderSize = (int) (69 * scaleByScreenSize);


        // BORDER
        int buttonBorderOffsetPos = 4;
        int buttonBorderOffsetSize = buttonBorderOffsetPos * 2;
        g2d.setColor(buttonBorderColor);
        g2d.fillRoundRect(previewX - buttonBorderOffsetPos, previewY - buttonBorderOffsetPos,
            previewWidth + buttonBorderOffsetSize, previewHeight + buttonBorderOffsetSize,
            buttonBorderSize + buttonBorderOffsetPos / 2, buttonBorderSize + buttonBorderOffsetPos / 2);

        // BUTTON
        g2d.setColor(buttonDefaultColor);
        g2d.fillRoundRect(previewX, previewY, previewWidth, previewHeight,
            buttonBorderSize, buttonBorderSize);

        int backgroundSize = (int) (14 * scale);


        g2d.setColor(buttonTextColor);
        g2d.setFont(buttonFont);

            // Background

        g2d.setColor(saveSelectorTextBackground);
        g2d.fillRoundRect((int) (previewX + (double) (previewWidth) / 2 - backgroundSize * 0.25), (int) (previewY - backgroundSize * 0.8 + 18 * scale),
            backgroundSize, backgroundSize, (int) (4 * scale), (int) (4 * scale));

            // Shadow

        g2d.setColor(saveSelectorTextShadow);
        g2d.drawString(String.valueOf(saveSlot + 1), (int) (previewX + (double) (previewWidth / 2) + textShadow), (int) (previewY + textShadow + 18 * scale));

            // Number

        g2d.setColor(saveSelectorNumber);
        g2d.drawString(String.valueOf(saveSlot + 1), previewX + previewWidth / 2, (int) (previewY + 18 * scale));

            // Player

        g2d.drawImage(cachedImages.get(ImageRenderer.ImageName.PLAYER_FRONT0), previewX + 2 * previewWidth / 5,
            (int) (previewY - 25 * scale + (double) previewHeight / 2), 135, 135, null);

            // Level

        int level = (int) loadSaveScreenInformation(SaveFile.SaveScreenOption.LEVEL, saveSlot);

        String text = LanguageManager.getText("save_selector_level") + " " + level;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);

        int x = previewX + previewWidth / 2 - 2 * textWidth / 5;
        int y = (int) (previewY + 40 * scale + (double) previewHeight / 2);
        g2d.drawString(text, x, y);

            // Name

        text = String.valueOf(loadSaveScreenInformation(SaveFile.SaveScreenOption.NAME, saveSlot));

        textWidth = fm.stringWidth(text);

        x = (int) (previewX + (double) previewWidth / 2 - (double) textWidth / 2 + 3 * scale);
        y = (int) (previewY + 32 * scale);
        g2d.drawString(text, x, y);

            // Playtime

        text = String.valueOf(loadSaveScreenInformation(SaveFile.SaveScreenOption.PLAYTIME, saveSlot));

        textWidth = fm.stringWidth(text);

        x = previewX + previewWidth / 2 - 2 * textWidth / 5;
        y = (int) (previewY + 40 * scale + (double) previewHeight / 2 + fm.getHeight());
        g2d.drawString(text, x, y);

            // Draw hand item

        Item item = (Item) loadSaveScreenInformation(SaveFile.SaveScreenOption.FIRST_ITEM, saveSlot);

        if (item.getId() != 0) {
            drawRotatedImage(g2d, item.getIcon(), previewX + (double) previewWidth / 2 + 6 * scale, previewY - (double) previewHeight / 2 + 37.5 * scale + (double) previewHeight / 2,
                (int) (100 * scale / 3.75), (int) (100 * scale / 3.75), 0, false);
        }



        int buttonX = (int) (261 * scale);
        int buttonY = (int) (140 * scale);
        int buttonWidth = (int) (100 * scale);
        int buttonHeight = (int) (25 * scale);
        Rectangle button;
        int id = 310;

        // CONFIRMATION TEXT
        fm = g2d.getFontMetrics(buttonFont);
        g2d.setColor(buttonBorderColor);
        g2d.fillRoundRect(buttonX - buttonBorderOffsetPos, (int) (buttonY - buttonHeight - 4 * scale) - buttonBorderOffsetPos - fm.getHeight(),
            buttonWidth + buttonBorderOffsetSize, buttonHeight + buttonBorderOffsetSize,
            buttonBorderSize + buttonBorderOffsetPos / 2, buttonBorderSize + buttonBorderOffsetPos / 2);

        g2d.setColor(buttonDefaultColor);
        g2d.fillRoundRect(buttonX, (int) (buttonY - buttonHeight - 4 * scale) - fm.getHeight(), buttonWidth, buttonHeight,
            buttonBorderSize, buttonBorderSize);

        text = LanguageManager.getText("save_selector_deletion_confirmation");
        fm = g2d.getFontMetrics(buttonFont);

        drawColoredText(g2d, text, buttonX - fm.stringWidth(text) / 2 + buttonWidth / 2, (int) (buttonY - buttonHeight - 1 * scale), buttonFont, buttonTextColor, true);

        // CANCEL
        button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        Runnable runnable = () -> {
            askingToDelete = -1;
            clearButtonFading();
        };
        createButton(button, LanguageManager.getText("save_selector_deletion_cancel"), runnable, panel, id);

        // DELETE
        button = new Rectangle(buttonX, (int) (buttonY + buttonHeight + 4 * scale), buttonWidth, buttonHeight);
        runnable = () -> {
            deleteSaveFile(saveSlot);
            askingToDelete = -1;
            clearButtonFading();
        };
        createButton(button, LanguageManager.getText("save_selector_deletion_delete"), runnable, panel, id + 1);

        // WARNING
        g2d.setFont(backupAdvisorFont);
        fm = g2d.getFontMetrics(backupAdvisorFont);
        String[] texts = wrapText(LanguageManager.getText("save_selector_deletion_warning1") + saveSlot + LanguageManager.getText("save_selector_deletion_warning2"), buttonWidth * 3 / 2, fm);
        double extraY = 15 * scale;
        int fontHeight = fm.getHeight();
        for (String s : texts) {
            drawColoredText(g2d, s, buttonX + buttonWidth * 3 / 8 - fm.stringWidth(s) / 3, (int) (buttonY + buttonHeight * 2 + extraY),
                backupAdvisorFont, backupAdvisor, false);
            extraY += fontHeight;
        }

        // Backup deletion saveCreationWarning
        if (backupExists(saveSlot)) {
            g2d.setFont(backupAdvisorFont);
            fm = g2d.getFontMetrics(backupAdvisorFont);
            texts = wrapText(LanguageManager.getText("save_selector_deletion_warning3"), buttonWidth * 3 / 2, fm);
            fontHeight = fm.getHeight();
            for (String s : texts) {
                drawColoredText(g2d, s, (int) (buttonX + (double) buttonWidth / 2 - (double) fm.stringWidth(s) / 2 + textShadow), (int) ((int) (buttonY + buttonHeight * 2 + extraY) + textShadow),
                    backupAdvisorFont, backupWarningShadow, true);
                drawColoredText(g2d, s, buttonX + buttonWidth / 2 - fm.stringWidth(s) / 2, (int) (buttonY + buttonHeight * 2 + extraY),
                    backupAdvisorFont, backupWarning, false);
                extraY += fontHeight;
            }
        }
    }
}
