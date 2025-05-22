package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.game.GamePanel;
import com.ded.misle.world.logic.World;
import com.ded.misle.world.entities.npcs.NPC;
import com.ded.misle.input.MouseHandler;
import com.ded.misle.world.boxes.BoxHandling;
import com.ded.misle.items.Item;
import com.ded.misle.world.entities.player.PlayerStats;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Objects;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.core.Setting.antiAliasing;
import static com.ded.misle.renderer.FloatingText.drawFloatingTexts;
import static com.ded.misle.renderer.FontManager.*;
import static com.ded.misle.renderer.ImageManager.mergeImages;
import static com.ded.misle.world.boxes.Box.getTexture;
import static com.ded.misle.world.entities.npcs.NPC.getSelectedNPCs;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.DialogRenderer.renderDialog;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.renderer.ImageManager.cachedImages;
import static com.ded.misle.renderer.InventoryRenderer.*;
import static com.ded.misle.world.entities.player.PlayerStats.Direction.*;
import static java.lang.System.currentTimeMillis;

public class PlayingRenderer extends AbstractRenderer {
    public static double facingMultiplicator;
    public static boolean mirror;

    public static String selectedItemName;
    public static Point selectedItemNamePosition;
    public static long itemNameDisplayStartTime;

    public static final int inventoryBarWidth = 120;
    public static final int inventoryBarHeight = 20;
    public static final int inventoryBarX = (originalScreenWidth - inventoryBarWidth) / 2;
    public static final int inventoryBarY = originalScreenHeight - inventoryBarHeight - 10;

    public static final int totalSlotsWidth = 7 * slotSize[0] + (6 * slotSpacing[0]);
    public static final int slotStartX = inventoryBarX + (inventoryBarWidth - totalSlotsWidth) / 2;

    @Override
    public void render(Graphics g, MouseHandler mouseHandler) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw background
        World world = player.pos.world;

        for (int i = 0; i < originalScreenWidth / originalTileSize + 2; i++) {
            for (int j = 0; j < originalScreenHeight / originalTileSize + 2; j++) {

                int worldX = (int) (Math.max((player.pos.getCameraOffsetX() / (double) originalTileSize), 0) + i);
                int worldY = (int) (Math.max((player.pos.getCameraOffsetY() / (double) originalTileSize), 0) + j);

                BufferedImage texture = ((worldX + worldY) % 2 == 0) ?
                    world.background.box[0].getTexture() :
                    world.background.box[1].getTexture();

                int drawX = (int) (i * originalTileSize - (player.pos.getCameraOffsetX() % originalTileSize));
                int drawY = (int) (j * originalTileSize - (player.pos.getCameraOffsetY() % originalTileSize));

                g2d.drawImage(texture, drawX, drawY, originalTileSize, originalTileSize, null);
            }
        }

        // Draw boxes
        BoxHandling.renderBoxes(g2d, player.pos.getCameraOffsetX(), player.pos.getCameraOffsetY());

        // Draw selected NPC indicator
        ArrayList<NPC> selectedNPCs = getSelectedNPCs();
        try {
            for (NPC npc : selectedNPCs) {
                for (int i = 0; i <= 270; i += 90) {
                    drawRotatedImage(g2d, getTexture("wall_default_overlayW"), npc.getX() - player.pos.getCameraOffsetX(), npc.getY() - player.pos.getCameraOffsetY(),
                        (int) (originalTileSize * npc.getVisualScaleHorizontal()), (int) (originalTileSize * npc.getVisualScaleVertical()), i + npc.getVisualRotation());
                }
            }
        } catch (ConcurrentModificationException e) {
            //
        }

        // Player position adjustments
        player.updateVisualPosition(50f);
        int playerScreenX = (int) (player.getRenderX() + player.visualOffsetX * originalTileSize - player.pos.getCameraOffsetX());
        int playerScreenY = (int) (player.getRenderY() + player.visualOffsetY * originalTileSize - player.pos.getCameraOffsetY());

        // Draw the player above every box
//        g2d.setColor(player.getColor());
//        Rectangle playerRect = new Rectangle(playerScreenX, playerScreenY, (int) player.getBoxScaleHorizontal(), (int) player.getBoxScaleVertical());
//        drawRotatedRect(g2d, playerRect, player.pos.getVisualRotation()); // CUBE PLAYER

        long precision = 200;
        PlayerStats.Direction horizontalDirection = getRecentHorizontalDirection(precision);
        PlayerStats.Direction verticalDirection = getRecentVerticalDirection(precision);
        PlayerStats.Direction totalDirection = getRecentDirection(precision);
        BufferedImage playerSprite = null;
        boolean playerMirror = player.stats.getHorizontalDirection() == LEFT;

        // Draw player sprite
        if (totalDirection == NONE) {
            playerSprite = cachedImages.get(ImageManager.ImageName.PLAYER_FRONT0_EDIT);
        } else if (horizontalDirection != NONE) {
            int animationFrame = (int) ((System.currentTimeMillis() / 150) % 3);

            playerSprite = cachedImages.get(ImageManager.ImageName.valueOf("PLAYER_WALK" + animationFrame + "_EDIT"));
        } else if (verticalDirection != NONE) {
            int animationFrame = (int) ((System.currentTimeMillis() / 100) % 2);

            playerSprite = cachedImages.get(ImageManager.ImageName.valueOf("PLAYER_FRONT" + animationFrame + "_EDIT"));
        }

        playerSprite = player.isIconTexture ?
            mergeImages(playerSprite, player.icon) :
            playerSprite;

        drawRotatedImage(g2d, playerSprite,
            playerScreenX - player.getVisualScaleHorizontal() * 0.25 * originalTileSize,
            playerScreenY - player.getVisualScaleVertical() * 0.25 * originalTileSize,
            (int) (player.getVisualScaleHorizontal() * 1.5 * originalTileSize),
            (int) (player.getVisualScaleVertical() * 1.5 * originalTileSize), player.pos.getRotation(), playerMirror);

        drawHandItem(g2d, playerScreenX, playerScreenY, mouseHandler);

        drawUIElements(g2d);

        drawFloatingTexts(g2d);

        if (gameState == GameState.INVENTORY) {
            renderInventoryMenu(g);
            if (mouseHandler.getHoveredSlot()[0] > -1 && mouseHandler.getHoveredSlot()[1] > -1 && player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]) != null) {
                drawHoveredItemTooltip(g, new int[]{mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]}, false, mouseHandler);
            } else if (mouseHandler.getExtraHoveredSlot()[0] > -1 && mouseHandler.getExtraHoveredSlot()[1] > -1 &&
                player.inv.getItem(mouseHandler.getExtraHoveredSlot()[0] + mouseHandler.getExtraHoveredSlot()[1] * 2) != null) {
                drawHoveredItemTooltip(g, new int[]{mouseHandler.getExtraHoveredSlot()[1], mouseHandler.getExtraHoveredSlot()[0]}, true, mouseHandler);
            }
            InventoryRenderer.updateMousePos(mouseHandler);
            if (player.inv.getDraggedItem() != null) {
                drawDraggedItem(g2d);
            }
        } else {
            if (mouseHandler.getHoveredBarSlot() > -1 && player.inv.getItem(0, mouseHandler.getHoveredBarSlot()) != null) {
                drawHoveredItemTooltip(g, new int[]{-1, mouseHandler.getHoveredBarSlot()}, false, mouseHandler);
            }
        }

        if (gameState == GameState.DIALOG) {
            renderDialog(g2d);
        }

        fader.drawFading(g2d);
    }

    private static void drawHandItem(Graphics2D g2d, double playerScreenX, double playerScreenY, MouseHandler mouseHandler) {
        if (player.inv.hasHeldItem()) {

            if (player.stats.getHorizontalDirection() == RIGHT) {
                facingMultiplicator = 0.5;
                mirror = false;
            } else {
                facingMultiplicator = -0.5;
                mirror = true;
            }

            double distance = playerScreenX + player.getVisualScaleHorizontal() * originalTileSize
                * facingMultiplicator * (double) 1;

            Item selectedItem = player.inv.getSelectedItem();
            int itemSize = (int) (originalTileSize * selectedItem.getAnimationBulk());

            if (selectedItem.getCountLimit() >= 16 && selectedItem.getCount() > selectedItem.getCountLimit() / 3) {
                double pos = 12 * facingMultiplicator * (double) 1;
                drawRotatedImage(g2d, selectedItem.getIcon(),
                    distance + pos + selectedItem.getAnimationX() * facingMultiplicator / 3.75,
                    playerScreenY + 15 * (double) 1 + selectedItem.getAnimationY() / 3.75,
                    itemSize, itemSize,
                    (35 + selectedItem.getAnimationRotation()) * Math.ceil(facingMultiplicator), mirror);
            }

            if (selectedItem.getCountLimit() >= 100 && selectedItem.getCount() > 2 * selectedItem.getCountLimit() / 3) {
                double pos = -12 * facingMultiplicator * (double) 1;
                drawRotatedImage(g2d, selectedItem.getIcon(),
                    distance + pos + selectedItem.getAnimationX() * facingMultiplicator / 3.75,
                    playerScreenY + 15 * (double) 1 + selectedItem.getAnimationY() / 3.75,
                    itemSize, itemSize,
                    (-35 + selectedItem.getAnimationRotation()) * Math.ceil(facingMultiplicator), mirror);
            }

            drawRotatedImage(g2d, selectedItem.getIcon(),
                (int) distance + selectedItem.getAnimationX() * facingMultiplicator / 3.75,
                playerScreenY + selectedItem.getAnimationY() / 3.75,
                itemSize, itemSize,
                selectedItem.getAnimationRotation() * Math.ceil(facingMultiplicator), mirror);
        }
    }

    private static void drawHealthBar(Graphics2D g2d) {
        int healthBarWidth = 15;
        int healthBarHeight = 50;
        int healthBarX = 30;
        int healthBarY = 212;
        final int shadowExtra = 3;
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
            drawColoredText(g2d, str, (int) (x + textShadow), (int) (y + textShadow));

            if (healthPercentage <= 0.25)
                g2d.setColor(healthBarTextCritical);
            else g2d.setColor(healthBarText);

            drawColoredText(g2d, str, x, y);
        }
    }

    private static void drawEntropyBar(Graphics2D g2d) {
        int entropyBarWidth = 15;
        int entropyBarHeight = 50;
        int entropyBarX = 65;
        int entropyBarY = 212;
        final int shadowExtra = 3;
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
            drawColoredText(g2d, str, (int) (x + textShadow), (int) (y + textShadow));

            if (entropyPercentage <= 0.25)
                g2d.setColor(entropyBarTextCritical);
            else g2d.setColor(entropyBarText);

            drawColoredText(g2d, str, x, y);
        }
    }

    private static void drawXPBar(Graphics2D g2d) {
        int xpBarWidth = (slotSize[0] + slotSpacing[0]) * 7;
        int xpBarHeight = 4;
        int xpBarX = slotStartX - 4;
        int xpBarY = inventoryBarImageY - 2 - xpBarHeight;
        final int shadowOffset = 1;
        final int shadowWidth = xpBarWidth + shadowOffset * 2;
        final int shadowHeight = xpBarHeight + shadowOffset * 2;
        final int shadowX = xpBarX - shadowOffset;
        final int shadowY = xpBarY - shadowOffset;
        final int arcWidth = 2;
        final int arcHeight = 14;

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
            drawColoredText(g2d, str, (int) (x + textShadow), (int) (y + textShadow));

            g2d.setColor(xpBarText);
            drawColoredText(g2d, str, x, y);
        }
    }

    static int inventoryBarImageY = originalScreenHeight - 36;
    private static void drawInventoryBar(Graphics2D g2d) {

        g2d.drawImage(cachedImages.get(ImageManager.ImageName.INVENTORY_BAR), 0, inventoryBarImageY,
            originalScreenWidth, 35, null);

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
                drawColoredText(g2d, String.valueOf(i + 1), slotX + slotSize[0] / 3, slotY + slotSize[1]);
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
                    int textY = slotY + slotSize[0];
                    g2d.setColor(itemCountShadowColor);
                    drawColoredText(g2d, Integer.toString(itemCount), (int) (textX + textShadow), (int) (textY + textShadow));
                    g2d.setColor(itemCountColor);
                    drawColoredText(g2d, Integer.toString(itemCount), textX, textY);
                }
            }

            if (i == selectedSlot) {
                int width = slotSize[0];
                int height = slotSize[0] - 1;
                if (selectedSlot == 0) {
                    slotX++;
                    width--;
                } else if (selectedSlot == 6) {
                    width--;
                }
                slotY = inventoryBarImageY + 2;
                drawSelectedSlotOverlay(g2d, slotX - 1, slotY, width, height);
            }
        }
    }

    public static void updateSelectedItemNamePosition() {
        Item selectedItem = player.inv.getSelectedItem();
        if (selectedItem != null) {
            selectedItemName = selectedItem.getDisplayName();

            int slotX = slotStartX + player.inv.getSelectedSlot() * (slotSize[0] + slotSpacing[0]);
            int slotY = inventoryBarY - 12;

            // Position the name above the selected slot
            selectedItemNamePosition = new Point(slotX + slotSize[0] / 2, slotY);
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
        int coinTextX = 468;
        int coinTextY = 222;
        g2d.setColor(coinTextShadowColor);
        g2d.setFont(coinTextFont);
        drawColoredText(g2d, String.valueOf(player.attr.getBalance()), (int) (coinTextX + textShadow), (int)(coinTextY + textShadow));

        g2d.setColor(coinTextUI);
        drawColoredText(g2d, String.valueOf(player.attr.getBalance()), coinTextX, coinTextY);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(String.valueOf(player.attr.getBalance()));
        int coinPosX = coinTextX + textWidth;
        int coinPosY = coinTextY - g2d.getFontMetrics().getHeight() + 4;

        g2d.drawImage(cachedImages.get(ImageManager.ImageName.COIN), coinPosX, coinPosY,
            g2d.getFontMetrics().getHeight(), g2d.getFontMetrics().getHeight(), null);
    }

    private static void drawLevel(Graphics2D g2d) {
        int level = player.attr.getLevel();
        int x = originalScreenWidth / 2;
        int xpBarHeight = 4;
        int xpBarY = inventoryBarImageY - 2 - xpBarHeight;
        FontMetrics fm = g2d.getFontMetrics(coinTextFont);
        x -= fm.stringWidth(String.valueOf(level)) / 2;

        int y = xpBarY + 3;

        g2d.setColor(levelTextShadowColor);
        g2d.setFont(coinTextFont);
        drawColoredText(g2d, String.valueOf(level), (int) (x + textShadow), (int)(y + textShadow));

        g2d.setColor(levelTextUI);
        drawColoredText(g2d, String.valueOf(level), x, y);

    }

    private static void drawUIElements(Graphics2D g2d) {
        drawHealthBar(g2d);
        drawEntropyBar(g2d);
        drawXPBar(g2d);
        drawLevel(g2d);
        drawCoins(g2d);
        drawInventoryBar(g2d);
        drawSelectedItemName(g2d);
    }
}

