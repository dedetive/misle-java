package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.core.SaveFile;
import com.ded.misle.items.Item;

import javax.swing.*;
import java.awt.*;
import java.util.ConcurrentModificationException;

import static com.ded.misle.Launcher.antiAliasing;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.GameState.SAVE_SELECTOR;
import static com.ded.misle.core.GamePanel.gameState;
import static com.ded.misle.core.SaveFile.deleteSaveFile;
import static com.ded.misle.core.SaveFile.loadSaveScreenInformation;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.buttonFont;
import static com.ded.misle.renderer.ImageRenderer.cachedImages;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.MenuRenderer.createTitle;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;
import static com.ded.misle.renderer.PlayingRenderer.scaleByScreenSize;

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

            if (askingToDelete != -1) {
                askToDeleteSave(askingToDelete, panel, g2d);
                drawButtons(g2d, scale / 3.75);

            } else {
                // Save buttons
                int buttonX = (int) (64 * scale);
                int buttonY = (int) (86 * scale);
                int buttonWidth = (int) (120 * scale);
                int buttonHeight = (int) (120 * scale);
                int buttonSpacing = (int) (12 * scale);
                Rectangle button;
                int id = 300;
                boolean[] existingSaves = new boolean[3];
                for (int i = 0; i < 3; i++) {
                    boolean saveExists = (boolean) loadSaveScreenInformation(SaveFile.SaveScreenOption.EXISTS, i);
                    existingSaves[i] = saveExists;

                    button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
                    int finalI = i;
                    Runnable runnable = () -> gameStart(finalI);
                    createButton(button, "", runnable, panel, id);

                    if (saveExists) {
                        button = new Rectangle(buttonX, (int) (buttonY + buttonHeight + 4 * scale), buttonWidth, (int) (15 * scale));
                        runnable = () -> askingToDelete = finalI;
                        createButton(button, "c{#DE4040,Delete}", runnable, panel, id + 3);
                    }

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
                    buttonHeight = (int) (120 * scale);
                    int backgroundSize = (int) (14 * scale);

                    for (int i = 0; i < 3; i++) {
                        // Background
                        g2d.setColor(saveSelectorTextBackground);
                        g2d.fillRoundRect((int) (buttonX + buttonWidth - backgroundSize * 0.25), (int) (buttonY - backgroundSize * 0.8),
                            backgroundSize, backgroundSize, (int) (4 * scale), (int) (4 * scale));

                        // Shadow
                        g2d.setColor(saveSelectorTextShadow);
                        g2d.drawString(String.valueOf(i + 1), (int) (buttonX + (double) buttonWidth + textShadow), (int) (buttonY + textShadow));
                        // Number
                        g2d.setColor(saveSelectorNumber);
                        g2d.drawString(String.valueOf(i + 1), buttonX + buttonWidth, buttonY);

                        if (existingSaves[i]) {
                            // Player
                            g2d.drawImage(cachedImages.get(ImageRenderer.ImageName.PLAYER_FRONT0), (int) (buttonX + buttonWidth * 0.8),
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

    private static int askingToDelete = -1;

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



        int buttonX = (int) (261 * scale);
        int buttonY = (int) (152 * scale);
        int buttonWidth = (int) (100 * scale);
        int buttonHeight = (int) (25 * scale);
        Rectangle button;
        int id = 310;

        // TEXT
        FontMetrics fm = g2d.getFontMetrics(buttonFont);
        g2d.setColor(buttonBorderColor);
        g2d.fillRoundRect(buttonX - buttonBorderOffsetPos, (int) (buttonY - buttonHeight - 4 * scale) - buttonBorderOffsetPos - fm.getHeight(),
            buttonWidth + buttonBorderOffsetSize, buttonHeight + buttonBorderOffsetSize,
            buttonBorderSize + buttonBorderOffsetPos / 2, buttonBorderSize + buttonBorderOffsetPos / 2);

        g2d.setColor(buttonDefaultColor);
        g2d.fillRoundRect(buttonX, (int) (buttonY - buttonHeight - 4 * scale) - fm.getHeight(), buttonWidth, buttonHeight,
            buttonBorderSize, buttonBorderSize);

        drawColoredText(g2d, "Delete this save?", (int) (buttonX + 4 * scale), (int) (buttonY - buttonHeight - 1 * scale), buttonFont, buttonTextColor, true);

        button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        Runnable runnable = () -> askingToDelete = -1;
        createButton(button, "Cancel", runnable, panel, id);

        button = new Rectangle(buttonX, (int) (buttonY + buttonHeight + 4 * scale), buttonWidth, buttonHeight);
        runnable = () -> {
            deleteSaveFile(saveSlot);
            askingToDelete = -1;
        };
        createButton(button, "c{#DE4040,Delete}", runnable, panel, id + 1);
    }
}
