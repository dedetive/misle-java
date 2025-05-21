package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.core.SaveFile;
import com.ded.misle.items.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;

import static com.ded.misle.game.GamePanel.GameState.*;
import static com.ded.misle.game.GamePanel.gameState;
import static com.ded.misle.core.SaveFile.*;
import static com.ded.misle.core.Setting.antiAliasing;
import static com.ded.misle.game.GamePanel.originalTileSize;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.*;
import static com.ded.misle.renderer.ImageManager.*;
import static com.ded.misle.renderer.ImageManager.ImageName.PLAYER_FRONT0;
import static com.ded.misle.renderer.ImageManager.ImageName.PLAYER_FRONT0_EDIT;
import static com.ded.misle.renderer.InventoryRenderer.wrapText;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.MenuRenderer.createTitle;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;
import static com.ded.misle.renderer.SaveCreator.*;

public abstract class SaveSelector {

    public static void saveSelectorMenu() {
        MainRenderer.previousMenu = MainRenderer.currentMenu;
        MainRenderer.currentMenu = SAVE_SELECTOR;
        gameState = SAVE_SELECTOR;
        clearButtonFading();
    }

    public static void renderSaveSelector(Graphics g, JPanel panel) {
        if (g instanceof Graphics2D g2d) {

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
                int buttonX = 64;
                int buttonY = 66;
                int buttonWidth = 120;
                int buttonHeight = 120;
                int buttonSpacing = 12;
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
                            clearIcon();
                        };
                    } else {
                        runnable = () -> gameStart(finalI);
                    }


                    createButton(buttonRect, "", runnable, panel, id);

                    if (saveExists) {
                        buttonRect = new Rectangle(buttonX, buttonY + buttonHeight + 4, buttonWidth, 15);
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

                    buttonX = 64;
                    buttonY += 17;
                    buttonWidth = 60;
                    buttonHeight = 120;
                    int backgroundSize = 14;

                    for (int saveSlot = 0; saveSlot < 3; saveSlot++) {

                        // Background

                        g2d.setColor(saveSelectorTextBackground);
                        g2d.fillRoundRect((int) (buttonX + buttonWidth * 0.975 - backgroundSize * 0.25), (int) (buttonY - backgroundSize * 0.8),
                            backgroundSize, backgroundSize, 4, 4);

                        // Shadow

                        g2d.setColor(saveSelectorTextShadow);
                        drawColoredText(g2d, String.valueOf(saveSlot + 1),
                            (int) (buttonX + buttonWidth * 0.975 + textShadow), buttonY + textShadow);

                        // Number

                        g2d.setColor(saveSelectorNumber);
                        drawColoredText(g2d, String.valueOf(saveSlot + 1),
                            (int) (buttonX + buttonWidth * 0.975), buttonY);

                        if (existingSaves[saveSlot]) {

                            // Player

                            boolean isPlayerTextureIcon = (boolean) loadSaveScreenInformation(SaveScreenOption.IS_PLAYER_TEXTURE_ICON, saveSlot);

                            BufferedImage icon = (BufferedImage) loadSaveScreenInformation(SaveScreenOption.ICON, saveSlot);

                            BufferedImage img = isPlayerTextureIcon ?
                                mergeImages(cachedImages.get(PLAYER_FRONT0_EDIT), icon) :
                                cachedImages.get(PLAYER_FRONT0);

                            g2d.drawImage(img,
                                (int) (buttonX + buttonWidth * 0.75), (int) (buttonY - 40 + (double) buttonHeight / 2),
                                originalTileSize, originalTileSize, null);

                            // Level

                            int level = (int) loadSaveScreenInformation(SaveFile.SaveScreenOption.LEVEL, saveSlot);

                            String text = LanguageManager.getText("save_selector_level") + " " + level;
                            FontMetrics fm = g2d.getFontMetrics();
                            int textWidth = fm.stringWidth(text);

                            int x = buttonX + buttonWidth - textWidth / 2;
                            int y = (int) (buttonY + 20 + (double) buttonHeight / 2);
                            drawColoredText(g2d, text, x, y);

                            // Playtime

                            text = String.valueOf(loadSaveScreenInformation(SaveFile.SaveScreenOption.PLAYTIME, saveSlot));

                            textWidth = fm.stringWidth(text);

                            x = buttonX + buttonWidth - textWidth / 2;
                            y = (int) (buttonY + 20 + (double) buttonHeight / 2 + fm.getHeight());
                            drawColoredText(g2d, text, x, y);

                            // Name

                            text = String.valueOf(loadSaveScreenInformation(SaveFile.SaveScreenOption.NAME, saveSlot));

                            textWidth = fm.stringWidth(text);

                            x = buttonX + buttonWidth - textWidth / 2;
                            y = buttonY + 14;
                            drawColoredText(g2d, text, x, y);

                            // Draw hand item

                            Object itemResult = loadSaveScreenInformation(SaveScreenOption.FIRST_ITEM, saveSlot);
                            if (itemResult instanceof Item item) {

                                if (item.getId() != 0) {
                                    drawRotatedImage(g2d, item.getIcon(), buttonX + buttonWidth * 1.1, buttonY - 37 + (double) buttonHeight / 2,
                                        (int) (100 / 3.75), (int) (100 / 3.75), 0, false);
                                }
                            }

                            // Draw icon

                            g2d.drawImage((BufferedImage) loadSaveScreenInformation(SaveScreenOption.ICON, saveSlot),
                                x + textWidth + 4, buttonY + fm.getHeight() / 8,
                                16, 16, null);

                        } else {

                            // Plus sign

                            g2d.setColor(saveSelectorTextBackground);
                            g2d.fillRoundRect(buttonX + buttonWidth + 2, buttonY + buttonHeight / 5,
                                4, buttonHeight / 4, 3, 3);
                            g2d.fillRoundRect((int) (buttonX + buttonWidth * 0.8), (int) (buttonY + (double) buttonHeight / 4 + 7),
                                buttonHeight / 4, 4, 3, 3);
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

        int previewX = 112;
        int previewY = 86;
        int previewWidth = 120;
        int previewHeight = 120;
        int buttonBorderSize = (int) (69 / 3.75);


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

        int backgroundSize = 14;


        g2d.setColor(buttonTextColor);
        g2d.setFont(buttonFont);

            // Background

        g2d.setColor(saveSelectorTextBackground);
        g2d.fillRoundRect((int) (previewX + (double) (previewWidth) / 2 - backgroundSize * 0.25), (int) (previewY - backgroundSize * 0.8 + 18),
            backgroundSize, backgroundSize, 4, 4);

            // Shadow

        g2d.setColor(saveSelectorTextShadow);
        drawColoredText(g2d, String.valueOf(saveSlot + 1), (int) (previewX + (double) (previewWidth / 2) + textShadow), (int) (previewY + textShadow + 18));

            // Number

        g2d.setColor(saveSelectorNumber);
        drawColoredText(g2d, String.valueOf(saveSlot + 1), previewX + previewWidth / 2, previewY + 18);

            // Player

        boolean isPlayerTextureIcon = (boolean) loadSaveScreenInformation(SaveScreenOption.IS_PLAYER_TEXTURE_ICON, saveSlot);

        BufferedImage icon = (BufferedImage) loadSaveScreenInformation(SaveScreenOption.ICON, saveSlot);

        BufferedImage img = isPlayerTextureIcon ?
            mergeImages(cachedImages.get(PLAYER_FRONT0_EDIT), icon) :
            cachedImages.get(PLAYER_FRONT0);

        g2d.drawImage(img, previewX + 2 * previewWidth / 5,
            (int) (previewY - 25 + (double) previewHeight / 2), 135, 135, null);

            // Level

        int level = (int) loadSaveScreenInformation(SaveFile.SaveScreenOption.LEVEL, saveSlot);

        String text = LanguageManager.getText("save_selector_level") + " " + level;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);

        int x = previewX + previewWidth / 2 - 2 * textWidth / 5;
        int y = (int) (previewY + 40 + (double) previewHeight / 2);
        drawColoredText(g2d, text, x, y);

            // Name

        text = String.valueOf(loadSaveScreenInformation(SaveFile.SaveScreenOption.NAME, saveSlot));

        textWidth = fm.stringWidth(text);

        x = (int) (previewX + (double) previewWidth / 2 - (double) textWidth / 2 + 3);
        y = previewY + 32;
        drawColoredText(g2d, text, x, y);

            // Playtime

        text = String.valueOf(loadSaveScreenInformation(SaveFile.SaveScreenOption.PLAYTIME, saveSlot));

        textWidth = fm.stringWidth(text);

        x = previewX + previewWidth / 2 - 2 * textWidth / 5;
        y = (int) (previewY + 40 + (double) previewHeight / 2 + fm.getHeight());
        drawColoredText(g2d, text, x, y);

            // Draw hand item

        Item item = (Item) loadSaveScreenInformation(SaveFile.SaveScreenOption.FIRST_ITEM, saveSlot);

        if (item.getId() != 0) {
            drawRotatedImage(g2d, item.getIcon(), previewX + (double) previewWidth / 2 + 6,
                    previewY - (double) previewHeight / 2 + 37.5 + (double) previewHeight / 2,
                       (int) (100 / 3.75), (int) (100 / 3.75), 0, false);
        }

        g2d.drawImage(icon, x + textWidth,
            (int) (y - 100 + (double) fm.getHeight() / 3),
            16, 16, null);

        int buttonX = 261;
        int buttonY = 140;
        int buttonWidth = 100;
        int buttonHeight = 25;
        Rectangle button;
        int id = 310;

        // CONFIRMATION TEXT
        fm = g2d.getFontMetrics(buttonFont);
        g2d.setColor(buttonBorderColor);
        g2d.fillRoundRect(buttonX - buttonBorderOffsetPos, buttonY - buttonHeight - 4 - buttonBorderOffsetPos - fm.getHeight(),
            buttonWidth + buttonBorderOffsetSize, buttonHeight + buttonBorderOffsetSize,
            buttonBorderSize + buttonBorderOffsetPos / 2, buttonBorderSize + buttonBorderOffsetPos / 2);

        g2d.setColor(buttonDefaultColor);
        g2d.fillRoundRect(buttonX, buttonY - buttonHeight - 4 - fm.getHeight(), buttonWidth, buttonHeight,
            buttonBorderSize, buttonBorderSize);

        text = LanguageManager.getText("save_selector_deletion_confirmation");
        fm = g2d.getFontMetrics(buttonFont);

        drawColoredText(g2d, text, buttonX - fm.stringWidth(text) / 2 + buttonWidth / 2, buttonY - buttonHeight - 1, buttonFont, buttonTextColor, true);

        // CANCEL
        button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        Runnable runnable = () -> {
            askingToDelete = -1;
            clearButtonFading();
        };
        createButton(button, LanguageManager.getText("save_selector_deletion_cancel"), runnable, panel, id);

        // DELETE
        button = new Rectangle(buttonX, buttonY + buttonHeight + 4, buttonWidth, buttonHeight);
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
        double extraY = 15;
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
