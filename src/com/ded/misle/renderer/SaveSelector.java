package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.core.SaveFile;
import com.ded.misle.world.data.items.Item;
import com.ded.misle.renderer.utils.Plus;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.game.GamePanel.GameState.*;
import static com.ded.misle.core.SaveFile.*;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.*;
import static com.ded.misle.renderer.image.ImageManager.*;
import static com.ded.misle.renderer.image.ImageManager.ImageName.PLAYER_FRONT0;
import static com.ded.misle.renderer.image.ImageManager.ImageName.PLAYER_FRONT0_EDIT;
import static com.ded.misle.renderer.InventoryRenderer.wrapText;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.renderer.MenuButton.*;
import static com.ded.misle.renderer.MenuRenderer.createTitle;
import static com.ded.misle.renderer.MenuRenderer.drawMenuBackground;
import static com.ded.misle.renderer.SaveCreator.*;

public abstract class SaveSelector {

    private static final Rectangle BASE = new Rectangle(64, 66, 120, 120);
    private static final RoundRectangle2D NUMBER_RECT = new RoundRectangle2D.Double(BASE.x + BASE.width / 2d - 7, BASE.y + 2, 14, 14, 6, 6);
    private static final Point NUMBER_POS = new Point((int) (NUMBER_RECT.getX() + NUMBER_RECT.getWidth() / 2), 79);
    private static final Rectangle DELETE_BUTTON = new Rectangle(BASE.x, BASE.y + BASE.height + 4, BASE.width, 15);
    private static final Rectangle PLAYER = new Rectangle(BASE.x + BASE.width / 2 - originalTileSize / 2, BASE.y + BASE.height / 2 - originalTileSize / 2, originalTileSize, originalTileSize);
    private static final Rectangle HAND_ITEM = new Rectangle(PLAYER.x + 20, PLAYER.y + 4, 26, 26);
    private static final Point LEVEL = new Point(BASE.x + BASE.width / 2, BASE.y + BASE.height - 20);
    private static final Point PLAYTIME = new Point(BASE.x + BASE.width / 2, BASE.y + BASE.height - 20);
    private static final Point NAME = new Point(BASE.x + BASE.width / 2, BASE.y + 28);
    private static final Rectangle ICON = new Rectangle(NAME.x, NAME.y - 12, 16, 16);
    private static final int SPACING = (originalScreenWidth - BASE.width) / 3;

    private static final int DELETE_OFFSET = 24;
    private static final int DELETE_SPACING = 200;
    private static final Rectangle CONFIRMATION = new Rectangle(BASE.x + DELETE_OFFSET + DELETE_SPACING, BASE.y + 2, 128, 25);
    private static final Rectangle CANCEL = new Rectangle(CONFIRMATION.x, CONFIRMATION.y + CONFIRMATION.height * 2, CONFIRMATION.width, CONFIRMATION.height);
    private static final Rectangle DELETE = new Rectangle(CANCEL.x, CANCEL.y + CANCEL.height + 4, CANCEL.width, CANCEL.height);
    private static final Point BACKUP_WARNING = new Point(DELETE.x + DELETE.width / 2, DELETE.y + DELETE.height + 12);
    private static final Point BACKUP_OVERRIDE_WARNING = new Point(BACKUP_WARNING.x, BACKUP_WARNING.y);

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
            } else {
                // Save buttons
                boolean[] existingSaves = new boolean[3];
                for (int i = 0; i < 3; i++) {
                    Object saveExists = loadSaveScreenInformation(SaveFile.SaveScreenOption.EXISTS, i);
                    if (saveExists == null) continue;
                    boolean exists = (boolean) saveExists;
                    existingSaves[i] = exists;

                    int finalI = i;

                    Runnable runnable;

                    if (!exists) {
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

                    drawSaveBase(panel, runnable, i, i * SPACING);

                    int offsetX = SPACING * i;
                    if (exists) drawDeleteButton(panel, offsetX, i);
                }

                // Go back buttonRect
                createGoBackButton(panel, MenuButtonID.SAVE_SELECTOR_MENU_GO_BACK);

                try {
                    drawButtons(g2d);
                    for (int i = 0; i < 3; i++)
                        drawSaveInfo(g2d, SPACING * i, i, existingSaves[i]);
                } catch (ConcurrentModificationException ignored) {}
            }
        }
    }

    public static int askingToDelete = -1;
    public static void askToDeleteSave(int saveSlot, JPanel panel, Graphics2D g2d) {

        drawSaveBase(panel, null, saveSlot, DELETE_OFFSET);
        drawButtons(g2d);

        drawSaveInfo(g2d, DELETE_OFFSET, saveSlot, true);

        drawConfirmation(panel);
        drawCancel(panel);
        drawDelete(panel, saveSlot);
        drawWarning(g2d, saveSlot);
        drawBackupOverrideWarning(saveSlot, g2d);
    }

    private static void drawSaveInfo(Graphics2D g2d, int offsetX, int saveSlot, boolean saveExists) {
        if (saveExists) {
            g2d.setFont(buttonFont);

            drawSaveSlotNumber(g2d, offsetX, saveSlot);
            drawPlayerIcon(g2d, offsetX, saveSlot);
            drawLevel(g2d, offsetX, saveSlot);
            drawPlaytime(g2d, offsetX, saveSlot);
            drawName(g2d, offsetX, saveSlot);
            drawHandItem(g2d, offsetX, saveSlot);
            drawIcon(g2d, offsetX, saveSlot);
        } else {
            drawPlusSign(g2d, offsetX);
        }
    }

    private static void drawBackupOverrideWarning(int saveSlot, Graphics2D g2d) {
        if (backupExists(saveSlot)) {
            g2d.setFont(backupAdvisorFont);
            FontMetrics fm = getCachedMetrics(g2d, backupAdvisorFont);
            String[] texts = wrapText(LanguageManager.getText("save_selector_deletion_warning3"), DELETE.width, fm);
            int extraY = 0;
            int fontHeight = fm.getHeight();
            for (String s : texts) {
                drawColoredText(g2d, s, BACKUP_OVERRIDE_WARNING.x - fm.stringWidth(removeColorIndicators(s)) / 2 + textShadow, BACKUP_OVERRIDE_WARNING.y + extraY + textShadow,
                        backupAdvisorFont, backupWarningShadow, true);
                drawColoredText(g2d, s, BACKUP_OVERRIDE_WARNING.x - fm.stringWidth(removeColorIndicators(s)) / 2, BACKUP_OVERRIDE_WARNING.y + extraY,
                        backupAdvisorFont, backupWarning, false);
                extraY += fontHeight;
            }
        }
    }
    private static void drawWarning(Graphics2D g2d, int saveSlot) {
        g2d.setFont(backupAdvisorFont);
        FontMetrics fm = getCachedMetrics(g2d, backupAdvisorFont);
        String[] texts = wrapText(LanguageManager.getText("save_selector_deletion_warning1") + saveSlot + LanguageManager.getText("save_selector_deletion_warning2"), DELETE.width - 4, fm);
        int extraY = 0;
        int fontHeight = fm.getHeight();
        for (String s : texts) {
            drawColoredText(g2d, s, BACKUP_WARNING.x - fm.stringWidth(removeColorIndicators(s)) / 2, BACKUP_WARNING.y + extraY,
                    backupAdvisorFont, backupAdvisor, false);
            extraY += fontHeight;
        }
        BACKUP_OVERRIDE_WARNING.y = BACKUP_WARNING.y + extraY;
    }
    private static void drawDelete(JPanel panel, int saveSlot) {
        Runnable runnable = () -> {
            deleteSaveFile(saveSlot);
            askingToDelete = -1;
            clearButtons();
        };
        createButton(DELETE, LanguageManager.getText("save_selector_deletion_delete"), runnable, panel, MenuButtonID.SAVE_SELECTOR_CONFIRM_DELETE_SAVE);
    }
    private static void drawCancel(JPanel panel) {
        Runnable runnable = () -> {
            askingToDelete = -1;
            clearButtons();
        };
        createButton(CANCEL, LanguageManager.getText("save_selector_deletion_cancel"), runnable, panel, MenuButtonID.SAVE_SELECTOR_CANCEL_DELETE_SAVE);
    }
    private static void drawConfirmation(JPanel panel) {
	    createButton(CONFIRMATION,
                LanguageManager.getText("save_selector_deletion_confirmation"),
                null,
                panel,
                MenuButtonID.SAVE_SELECTOR_DELETE_SAVE_QUESTION);
    }
    private static void drawDeleteButton(JPanel panel, int offsetX, int saveSlot) {
        Rectangle buttonRect = new Rectangle(DELETE_BUTTON.x + offsetX, DELETE_BUTTON.y, DELETE_BUTTON.width, DELETE_BUTTON.height);
        Runnable runnable = () -> askingToDelete = saveSlot;
        createButton(buttonRect, LanguageManager.getText("save_selector_delete"), runnable, panel,
                MenuButtonID.valueOf("SAVE_SELECTOR_DELETE_SAVE" + (saveSlot + 1)));
    }
    private static void drawSaveBase(JPanel panel, Runnable runnable, int saveSlot, int offsetX) {
        Rectangle newButtonRect = new Rectangle(BASE.x + offsetX, BASE.y, BASE.width, BASE.height);
        createButton(newButtonRect, "", runnable, panel,
                MenuButtonID.valueOf("SAVE_SELECTOR_SELECT_SAVE" + (saveSlot + 1)));
    }
    private static void drawIcon(Graphics2D g2d, int offsetX, int saveSlot) {
        g2d.drawImage((BufferedImage) loadSaveScreenInformation(SaveScreenOption.ICON, saveSlot),
                ICON.x + offsetX + nameWidth(g2d, saveSlot) / 2 + 4, ICON.y,
                ICON.width, ICON.height, null);
    }
    private static void drawHandItem(Graphics2D g2d, int offsetX, int saveSlot) {
        Object itemResult = loadSaveScreenInformation(SaveScreenOption.FIRST_ITEM, saveSlot);
        if (itemResult instanceof Item item) {

            if (item.getId() != 0) {
                drawRotatedImage(g2d, item.getIcon(), HAND_ITEM.x + offsetX, HAND_ITEM.y,
                        HAND_ITEM.width, HAND_ITEM.height, 0, false);
            }
        }
    }
    private static void drawName(Graphics2D g2d, int offsetX, int saveSlot) {
        String text = String.valueOf(loadSaveScreenInformation(SaveScreenOption.NAME, saveSlot));

        drawColoredText(g2d, text, NAME.x - nameWidth(g2d, saveSlot) / 2 + offsetX, NAME.y);
    }
    private static void drawPlaytime(Graphics2D g2d, int offsetX, int saveSlot) {
        String text = String.valueOf(loadSaveScreenInformation(SaveScreenOption.PLAYTIME, saveSlot));
        String[] playtimeText = text.split(":");
        text = "";
        for (String s : playtimeText) {
            if (s.length() <= 2) {
                s = "0" + s;
            }
            s = ":" + s;
            text = text.concat(s);
        }
        if (text.charAt(0) == ':') text = text.replaceFirst(":", "");

        FontMetrics fm = getCachedMetrics(g2d, g2d.getFont());
        int textWidth = fm.stringWidth(text);

        drawColoredText(g2d, text, PLAYTIME.x - textWidth / 2 + offsetX, PLAYTIME.y + fm.getHeight());
    }
    private static void drawLevel(Graphics2D g2d, int offsetX, int saveSlot) {
        Object loadedLevel = loadSaveScreenInformation(SaveScreenOption.LEVEL, saveSlot);
        if (loadedLevel == null) return;
        int level = (int) loadedLevel;

        String text = LanguageManager.getText("save_selector_level") + " " + level;
        FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
        int textWidth = fm.stringWidth(text);

        drawColoredText(g2d, text, LEVEL.x - textWidth / 2 + offsetX, LEVEL.y);
    }
    private static void drawPlayerIcon(Graphics2D g2d, int offsetX, int saveSlot) {
        Object isPlayerTextureIcon = loadSaveScreenInformation(SaveScreenOption.IS_PLAYER_TEXTURE_ICON, saveSlot);
        if (isPlayerTextureIcon == null) return;

        BufferedImage icon = (BufferedImage) loadSaveScreenInformation(SaveScreenOption.ICON, saveSlot);

        BufferedImage img = (boolean) isPlayerTextureIcon ?
                mergeImages(cachedImages.get(PLAYER_FRONT0_EDIT), icon) :
                cachedImages.get(PLAYER_FRONT0);

        g2d.drawImage(img,PLAYER.x + offsetX, PLAYER.y, PLAYER.width, PLAYER.height, null);
    }
    private static void drawSaveSlotNumber(Graphics2D g2d, int offsetX, int saveSlot) {
        // Number background
        g2d.setColor(saveSelectorTextBackground);
        g2d.fillRoundRect((int) NUMBER_RECT.getX() + offsetX, (int) NUMBER_RECT.getY(),
                (int) NUMBER_RECT.getWidth(), (int) NUMBER_RECT.getHeight(),
                (int) NUMBER_RECT.getArcWidth(), (int) NUMBER_RECT.getArcHeight());

        // Shadow
        g2d.setColor(saveSelectorTextShadow);
        drawColoredText(g2d, String.valueOf(saveSlot + 1),
                NUMBER_POS.x + textShadow + offsetX - getCachedMetrics(g2d, buttonFont).stringWidth(String.valueOf(saveSlot + 1)) / 2, NUMBER_POS.y + textShadow);

        // Number
        g2d.setColor(saveSelectorNumber);
        drawColoredText(g2d, String.valueOf(saveSlot + 1),
                NUMBER_POS.x + offsetX - getCachedMetrics(g2d, buttonFont).stringWidth(String.valueOf(saveSlot + 1)) / 2, NUMBER_POS.y);
    }
    private static int nameWidth(Graphics2D g2d, int saveSlot) {
        String text = String.valueOf(loadSaveScreenInformation(SaveScreenOption.NAME, saveSlot));

        FontMetrics fm = getCachedMetrics(g2d, g2d.getFont());
        return fm.stringWidth(text);
    }
    private static void drawPlusSign(Graphics2D g2d, int offsetX) {
        g2d.setColor(saveSelectorTextBackground);

        new Plus(
            new Rectangle(BASE.x + offsetX, BASE.y, BASE.width, BASE.height),
            saveSelectorTextBackground,.5f, 7, 6)
            .draw(g2d);
    }
}
