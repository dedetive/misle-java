package com.ded.misle.renderer;

import com.ded.misle.core.GamePanel;
import com.ded.misle.core.LanguageManager;
import com.ded.misle.world.npcs.NPC;
import com.ded.misle.input.MouseHandler;
import com.ded.misle.world.boxes.BoxHandling;
import com.ded.misle.items.Item;
import com.ded.misle.world.player.PlayerStats;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Objects;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.core.GamePanel.*;
import static com.ded.misle.renderer.FloatingText.drawFloatingTexts;
import static com.ded.misle.renderer.FontManager.*;
import static com.ded.misle.renderer.FontManager.buttonFont;
import static com.ded.misle.world.boxes.Box.getTexture;
import static com.ded.misle.world.npcs.NPC.getSelectedNPCs;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.DialogRenderer.renderDialog;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.renderer.ImageRenderer.cachedImages;
import static com.ded.misle.renderer.InventoryRenderer.*;
import static com.ded.misle.world.player.PlayerStats.Direction.*;
import static java.lang.System.currentTimeMillis;

public class PlayingRenderer {
    public static double isFacingRight;
    public static boolean mirror;

    public static String selectedItemName;
    public static Point selectedItemNamePosition;
    public static long itemNameDisplayStartTime;

    public static double scaleByScreenSize = scale / 3.75;

    public static int inventoryBarWidth = (int) (120 * scale);
    public static int inventoryBarHeight = (int) (20 * scale);
    public static int inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
    public static int inventoryBarY = (int) (screenHeight - inventoryBarHeight - 60);

    public static int totalSlotsWidth = 7 * slotSize[0] + (6 * slotSpacing[0]);
    public static int slotStartX = inventoryBarX + (inventoryBarWidth - totalSlotsWidth) / 2;

    public static void updatePlayingVariableScales() {
        scaleByScreenSize = scale / 3.75;

        inventoryBarWidth = (int) (120 * scale);
        inventoryBarHeight = (int) (20 * scale);
        inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
        inventoryBarY = (int) (screenHeight - inventoryBarHeight - 60);

        totalSlotsWidth = 7 * slotSize[0] + (6 * slotSpacing[0]);
        slotStartX = inventoryBarX + (inventoryBarWidth - totalSlotsWidth) / 2;
    }

    public static void renderPlayingGame(Graphics g, MouseHandler mouseHandler) {
        Graphics2D g2d = (Graphics2D) g;

        // ANTI-ALIASING
        if (antiAliasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // Draw game components
        BoxHandling.renderBoxes(g2d, player.pos.getCameraOffsetX(), player.pos.getCameraOffsetY(), scale, tileSize);

        // Draw selected NPC indicator
        ArrayList<NPC> selectedNPCs = getSelectedNPCs();
        try {
            for (NPC npc : selectedNPCs) {
                for (int i = 0; i <= 270; i += 90) {
                    drawRotatedImage(g2d, getTexture("wall_default_overlayW"), npc.getX() * scale - player.pos.getCameraOffsetX(), npc.getY() * scale - player.pos.getCameraOffsetY(),
                        (int) (tileSize * npc.getBoxScaleHorizontal()), (int) (tileSize * npc.getBoxScaleVertical()), i + npc.getRotation());
                }
            }
        } catch (ConcurrentModificationException e) {
            //
        }

        // Player position adjustments
        int playerScreenX = (int) (player.getX() - player.pos.getCameraOffsetX());
        int playerScreenY = (int) (player.getY() - player.pos.getCameraOffsetY());

        // Draw the player above every box
        g2d.setColor(player.getColor());
        Rectangle playerRect = new Rectangle(playerScreenX, playerScreenY, (int) player.getBoxScaleHorizontal(), (int) player.getBoxScaleVertical());
//        drawRotatedRect(g2d, playerRect, player.pos.getRotation()); // CUBE PLAYER

        long precision = 50;
        PlayerStats.Direction horizontalDirection = player.stats.getCurrentHorizontalDirection(precision);
        PlayerStats.Direction verticalDirection = player.stats.getCurrentVerticalDirection(precision);
        PlayerStats.Direction totalDirection = player.stats.getCurrentWalkingDirection(precision);
        BufferedImage playerSprite = null;
        boolean playerMirror = player.stats.getHorizontalDirection() == LEFT;

        // Draw player sprite
        if (totalDirection == NONE) {
            playerSprite = cachedImages.get(ImageRenderer.ImageName.PLAYER_FRONT0);
        } else if (horizontalDirection != NONE) {
            int animationFrame = (int) ((System.currentTimeMillis() / 150) % 3);

            playerSprite = cachedImages.get(ImageRenderer.ImageName.valueOf("PLAYER_WALK" + animationFrame));
        } else if (verticalDirection != NONE) {
            int animationFrame = (int) ((System.currentTimeMillis() / 100) % 2);

            playerSprite = cachedImages.get(ImageRenderer.ImageName.valueOf("PLAYER_FRONT" + animationFrame));
        }

        drawRotatedImage(g2d, playerSprite,
            playerScreenX - player.getBoxScaleHorizontal() * 0.25, playerScreenY - player.getBoxScaleVertical() * 0.25,
            (int) (player.getBoxScaleHorizontal() * 1.5), (int) (player.getBoxScaleVertical() * 1.5), player.pos.getRotation(), playerMirror);

        drawHandItem(g2d, playerScreenX, playerScreenY, scaleByScreenSize, mouseHandler);

        drawUIElements(g2d);

        drawFloatingTexts(g2d);

        if (gameState == GameState.INVENTORY) {
            renderInventoryMenu(g);
            if (mouseHandler.getHoveredSlot()[0] > -1 && mouseHandler.getHoveredSlot()[1] > -1 && player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]) != null) {
                drawHoveredItemTooltip(g, new int[]{mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]}, false);
            } else if (mouseHandler.getExtraHoveredSlot()[0] > -1 && mouseHandler.getExtraHoveredSlot()[1] > -1 &&
                player.inv.getItem(mouseHandler.getExtraHoveredSlot()[0] + mouseHandler.getExtraHoveredSlot()[1] * 2) != null) {
                drawHoveredItemTooltip(g, new int[]{mouseHandler.getExtraHoveredSlot()[1], mouseHandler.getExtraHoveredSlot()[0]}, true);
            }
            if (player.inv.getDraggedItem() != null) {
                drawDraggedItem(g2d, mouseHandler);
            }
        } else {
            if (mouseHandler.getHoveredBarSlot() > -1 && player.inv.getItem(0, mouseHandler.getHoveredBarSlot()) != null) {
                drawHoveredItemTooltip(g, new int[]{-1, mouseHandler.getHoveredBarSlot()}, false);
            }
        }

        if (gameState == GameState.DIALOG) {
            renderDialog(g2d);
        }

        if (isFading != FadingState.UNFADED) drawFading(g2d);

        if (displayFPS) {
            g2d.setFont(buttonFont);
            String text = "FPS: " + frameCount;
            FontMetrics fm = g2d.getFontMetrics(buttonFont);
            int textWidth = fm.stringWidth(text);
            int textX = (int) (screenWidth - textWidth) - 8;
            int textY = fm.getHeight() - 8;
            g2d.setColor(FPSShadowColor);
            g2d.drawString(text, (int) (textX + textShadow), (int) (textY + textShadow));
            g2d.setColor(FPSColor);
            g2d.drawString(text, textX, textY);
        }

        g2d.dispose();
    }

    private static void drawHandItem(Graphics2D g2d, double playerScreenX, double playerScreenY, double scaleByScreenSize, MouseHandler mouseHandler) {
        if (player.inv.hasHeldItem()) {

            if ((heldItemFollowsMouse && mouseHandler.getMouseHorizontalDirection() == RIGHT) || (!heldItemFollowsMouse && player.stats.getHorizontalDirection() == RIGHT)) {
                isFacingRight = 0.5;
                mirror = false;
            } else {
                isFacingRight = -1;
                mirror = true;
            }

            double distance = playerScreenX + (player.getBoxScaleHorizontal() / 2) * 2 * isFacingRight * scaleByScreenSize;

            Item selectedItem = player.inv.getSelectedItem();

            if (selectedItem.getCountLimit() >= 16 && selectedItem.getCount() > selectedItem.getCountLimit() / 3) {
                double pos = 12 * isFacingRight * scaleByScreenSize;
                drawRotatedImage(g2d, selectedItem.getIcon(), distance + pos + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + 15 * scaleByScreenSize + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (35 + selectedItem.getAnimationRotation()) * Math.ceil(isFacingRight), mirror);
            }

            if (selectedItem.getCountLimit() >= 100 && selectedItem.getCount() > 2 * selectedItem.getCountLimit() / 3) {
                double pos = -12 * isFacingRight * scaleByScreenSize;
                drawRotatedImage(g2d, selectedItem.getIcon(), distance + pos + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + 15 * scaleByScreenSize + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (-35 + selectedItem.getAnimationRotation()) * Math.ceil(isFacingRight), mirror);
            }

            if (selectedItem.getCountLimit() == 1 && heldItemFollowsMouse) {
                drawRotatedImage(g2d, selectedItem.getIcon(), (int) distance + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), selectedItem.getAnimationRotation() * Math.ceil(isFacingRight) + mouseHandler.getMouseHorizontalRotation(), mirror);
            } else {
                drawRotatedImage(g2d, selectedItem.getIcon(), (int) distance + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), selectedItem.getAnimationRotation() * Math.ceil(isFacingRight), mirror);
            }
        }
    }

    private static void drawHealthBar(Graphics2D g2d) {
        int healthBarWidth = (int) (15 * scale);
        int healthBarHeight = (int) (50 * scale);
        int healthBarX = (int) (30 * scale);
        int healthBarY = (int) (212 * scale);
        final int shadowExtra = (int) (3 * scale);
        final int shadowWidth = healthBarWidth + shadowExtra;
        final int shadowHeight = healthBarHeight + shadowExtra;
        final int shadowX = healthBarX - shadowExtra / 2;
        final int shadowY = healthBarY - shadowExtra / 2;

        // Calculate the percentage of health remaining
        double healthPercentage = Math.min(player.getHP() / player.getMaxHP(), 1);

        // Shadow
        g2d.setColor(healthBarShadow);
        g2d.fillRect(shadowX, shadowY, shadowWidth, shadowHeight);

        // Draw the background of the health bar
        g2d.setColor(healthBarBackground);
        g2d.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

        // Draw the current health bar
        g2d.setColor(healthBarCurrent);
        g2d.fillRect(healthBarX, (int) (healthBarY + healthBarHeight - healthBarHeight * healthPercentage), healthBarWidth, (int) (healthBarHeight * healthPercentage));

        // Draw locked HP, if any
        double lockedHPPercentage = Math.min(player.getLockedHP() / player.getMaxHP(), 1);

        g2d.setColor(healthBarLockedHP);
        g2d.fillRect(healthBarX, healthBarY, healthBarWidth, (int) (healthBarHeight * lockedHPPercentage));

        // More info (Current HP / Max HP)
        if (!Objects.equals(displayMoreInfo, "false")) {
            g2d.setFont(itemInfoFont);
            String str;

            if (Objects.equals(displayMoreInfo, "exact")) {
                str = (int) player.getHP() + "/" + (int) player.getMaxHP();
            } else {
                str = (int) (100 * player.getHP() / player.getMaxHP()) + "%";
            }


            FontMetrics fm = g2d.getFontMetrics();
            int strWidth = fm.stringWidth(str);
            int x = healthBarX + healthBarWidth / 2 - strWidth / 2;
            int y = (int) (healthBarY + healthBarHeight * 1.2);

            g2d.setColor(healthBarTextShadow);
            g2d.drawString(str, (int) (x + textShadow), (int) (y + textShadow));

            if (healthPercentage <= 0.25)
                g2d.setColor(healthBarTextCritical);
            else g2d.setColor(healthBarText);

            g2d.drawString(str, x, y);
        }
    }

    private static void drawEntropyBar(Graphics2D g2d) {
        int entropyBarWidth = (int) (15 * scale);
        int entropyBarHeight = (int) (50 * scale);
        int entropyBarX = (int) (65 * scale);
        int entropyBarY = (int) (212 * scale);
        final int shadowExtra = (int) (3 * scale);
        final int shadowWidth = entropyBarWidth + shadowExtra;
        final int shadowHeight = entropyBarHeight + shadowExtra;
        final int shadowX = entropyBarX - shadowExtra / 2;
        final int shadowY = entropyBarY - shadowExtra / 2;

        // Calculate the percentage of entropy remaining
        double entropyPercentage = Math.min(player.attr.getEntropy() / player.attr.getMaxEntropy(), 1);

        // Shadow
        g2d.setColor(entropyBarShadow);
        g2d.fillRect(shadowX, shadowY, shadowWidth, shadowHeight);

        // Draw the background of the entropy bar
        g2d.setColor(entropyBarBackground);
        g2d.fillRect(entropyBarX, entropyBarY, entropyBarWidth, entropyBarHeight);

        // Draw the current entropy bar
        g2d.setColor(entropyBarCurrent);
        g2d.fillRect(entropyBarX, (int) (entropyBarY + entropyBarHeight - entropyBarHeight * entropyPercentage), entropyBarWidth, (int) (entropyBarHeight * entropyPercentage));

        // More info (Current Entropy / Max Entropy)
        if (!Objects.equals(displayMoreInfo, "false")) {
            g2d.setFont(itemInfoFont);

            String str;
            if (Objects.equals(displayMoreInfo, "exact")) {
                str = (int) player.attr.getEntropy() + "/" + (int) player.attr.getMaxEntropy();
            } else {
                str = (int) (100 * player.attr.getEntropy() / player.attr.getMaxEntropy()) + "%";
            }


            FontMetrics fm = g2d.getFontMetrics();
            int strWidth = fm.stringWidth(str);
            int x = entropyBarX + entropyBarWidth / 2 - strWidth / 2;
            int y = (int) (entropyBarY + entropyBarHeight * 1.2);

            g2d.setColor(entropyBarTextShadow);
            g2d.drawString(str, (int) (x + textShadow), (int) (y + textShadow));

            if (entropyPercentage <= 0.25)
                g2d.setColor(entropyBarTextCritical);
            else g2d.setColor(entropyBarText);

            g2d.drawString(str, x, y);
        }
    }

    private static void drawXPBar(Graphics2D g2d) {
        int xpBarWidth = (slotSize[0] + slotSpacing[0]) * 7;
        int xpBarHeight = (int) (4 * scale);
        int xpBarX = slotStartX - 4;
        int xpBarY = inventoryBarImageY - 2 - xpBarHeight;
        final int shadowOffset = (int) (1 * scale);
        final int shadowWidth = xpBarWidth + shadowOffset * 2;
        final int shadowHeight = xpBarHeight + shadowOffset * 2;
        final int shadowX = xpBarX - shadowOffset;
        final int shadowY = xpBarY - shadowOffset;
        final int arcWidth = (int) (2 * scale);
        final int arcHeight = (int) (14 * scale);

        // Calculate the percentage of XP remaining
        double xpPercentage = Math.min(player.attr.getXP() / player.attr.getXPtoLevelUp(), 1);

        // Shadow
        g2d.setColor(xpBarShadow);
        g2d.fillRoundRect(shadowX, shadowY, shadowWidth, shadowHeight, arcWidth, arcHeight);

        // Draw the background of the XP bar
        g2d.setColor(xpBarBackground);
        g2d.fillRoundRect(xpBarX, xpBarY, xpBarWidth, xpBarHeight, arcWidth, arcHeight);

        // Draw the current XP bar
        g2d.setColor(xpBarCurrent);
        g2d.fillRoundRect(xpBarX, xpBarY,
            (int) (xpBarWidth * xpPercentage), xpBarHeight, arcWidth, arcHeight);

        // More info
        if (!Objects.equals(displayMoreInfo, "false")) {
            g2d.setFont(itemInfoFont);

            String str;
            if (Objects.equals(displayMoreInfo, "exact")) {
                str = (int) player.attr.getXP() + "/" + (int) player.attr.getXPtoLevelUp();
            } else {
                str = (int) (100 * player.attr.getXP() / player.attr.getXPtoLevelUp()) + "%";
            }
            str = str + LanguageManager.getText("inventory_xp_measure_word");

            int x = xpBarX + xpBarWidth + 6;
            int y = (int) (xpBarY + xpBarHeight * 1.2);

            g2d.setColor(xpBarTextShadow);
            g2d.drawString(str, (int) (x + textShadow), (int) (y + textShadow));

            g2d.setColor(xpBarText);
            g2d.drawString(str, x, y);
        }
    }

    static int inventoryBarImageY =(int) (screenHeight - 82 * Math.pow(scale, (double) 1 /2));
    private static void drawInventoryBar(Graphics2D g2d) {

        g2d.drawImage(cachedImages.get(ImageRenderer.ImageName.INVENTORY_BAR), 0, inventoryBarImageY,
            (int) (512 * scale), (int) (35 * scale), null);

        // Slots info

        int selectedSlot = player.inv.getSelectedSlot();

        for (int i = 0; i < 7; i++) {
            int slotX = slotStartX + i * (slotSize[0] + slotSpacing[0]);
            int slotY = inventoryBarY + (inventoryBarHeight - slotSize[0]) / 2;

            // Draw the slot (DISABLED, ENABLE FOR TESTING)
//			g2d.setColor(Color.GRAY);
//			g2d.fillRect(slotX, slotY, slotSize, slotSize);
            if (!Objects.equals(displayMoreInfo, "false")) {
                g2d.setFont(FontManager.coinTextFont);
                g2d.setColor(slotIndicator);
                g2d.drawString(String.valueOf(i + 1), slotX + slotSize[0] / 3, slotY + slotSize[1]);
            }

            Item item = player.inv.getItem(0, i);
            if (item != null) {
                g2d.drawImage(item.getIcon(), slotX, slotY, slotSize[0], slotSize[0], null);
                int itemCount = item.getCount();
                if (itemCount > 1) {
                    // Draw item count
                    g2d.setFont(FontManager.itemCountFont);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(Integer.toString(itemCount));
                    int textX = slotX - textWidth + slotSize[0];
                    int textY = slotY + 8 * slotSize[0] / 9;
                    g2d.setColor(itemCountShadowColor);
                    g2d.drawString(Integer.toString(itemCount), (int) (textX + textShadow), (int) (textY + textShadow));
                    g2d.setColor(itemCountColor);
                    g2d.drawString(Integer.toString(itemCount), textX, textY);
                }
            }

            if (i == selectedSlot) {
                drawSelectedSlotOverlay(g2d, slotX, slotY, slotSize[0]);
            }
        }
    }

    public static void updateSelectedItemNamePosition() {
        Item selectedItem = player.inv.getSelectedItem();
        if (selectedItem != null) {
            selectedItemName = selectedItem.getDisplayName();

            int slotX = slotStartX + player.inv.getSelectedSlot() * (slotSize[0] + slotSpacing[0]);
            int slotY = inventoryBarY + 50;

            // Position the name above the selected slot
            selectedItemNamePosition = new Point((int) (slotX + slotSize[0] / scale * scaleByScreenSize / 2), slotY - 70);
            itemNameDisplayStartTime = currentTimeMillis();
        } else {
            selectedItemName = null;
            selectedItemNamePosition = null;
        }
    }

    public static void drawSelectedItemName(Graphics2D g2d) {
        if (selectedItemName != null && selectedItemNamePosition != null) {
            // Check if the current time is within 5 seconds of the start time
            try {
                long currentTime = currentTimeMillis();
                if (currentTime - itemNameDisplayStartTime < 5000) {
                    g2d.setFont(FontManager.selectedItemNameFont);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(removeColorIndicators(selectedItemName));

                    int textX = selectedItemNamePosition.x - textWidth / 2;
                    int textY = selectedItemNamePosition.y;

                    drawColoredText(g2d, selectedItemName, (int) (textX + textShadow), (int) (textY + textShadow),
                        g2d.getFont(), selectedItemNameShadowColor, true);

                    drawColoredText(g2d, selectedItemName, textX, textY,
                        g2d.getFont(), player.inv.getSelectedItem().getNameColor(), false);
                } else {
                    // Clear the selected item name after 5 seconds
                    selectedItemName = null;
                    selectedItemNamePosition = null;
                }
            } catch (NullPointerException e) {
                System.out.println("Selected item not found!");
                selectedItemName = null;
                selectedItemNamePosition = null;
            }
        }
    }

    private static void drawCoins(Graphics2D g2d) {
        int coinTextX = (int) (468 * scale);
        int coinTextY = (int) (222 * scale);
        g2d.setColor(coinTextShadowColor);
        g2d.setFont(coinTextFont);
        g2d.drawString(String.valueOf(player.attr.getBalance()), (int) (coinTextX + textShadow), (int)(coinTextY + textShadow));

        g2d.setColor(coinTextUI);
        g2d.drawString(String.valueOf(player.attr.getBalance()), coinTextX, coinTextY);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(String.valueOf(player.attr.getBalance()));
        int coinPosX = coinTextX + textWidth;
        int coinPosY = (int) (coinTextY - g2d.getFontMetrics().getHeight() + 4 * scale);

        g2d.drawImage(cachedImages.get(ImageRenderer.ImageName.COIN), coinPosX, coinPosY,
            g2d.getFontMetrics().getHeight(), g2d.getFontMetrics().getHeight(), null);
    }

    private static void drawLevel(Graphics2D g2d) {
        int level = player.attr.getLevel();
        int x = (int) (screenWidth / 2);
        int xpBarHeight = (int) (4 * scale);
        int xpBarY = inventoryBarImageY - 2 - xpBarHeight;
        FontMetrics fm = g2d.getFontMetrics(coinTextFont);
        x -= fm.stringWidth(String.valueOf(level)) / 2;

        int y = (int) (xpBarY + 3 * scale);

        g2d.setColor(levelTextShadowColor);
        g2d.setFont(coinTextFont);
        g2d.drawString(String.valueOf(level), (int) (x + textShadow), (int)(y + textShadow));

        g2d.setColor(levelTextUI);
        g2d.drawString(String.valueOf(level), x, y);

    }

    private static void drawUIElements(Graphics2D g2d) {
        drawHealthBar(g2d);
        drawEntropyBar(g2d);
        drawXPBar(g2d);
        drawLevel(g2d);
//        drawCoins(g2d);
        drawInventoryBar(g2d);
        drawSelectedItemName(g2d);
    }
}

