package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.net.NetClient;
import com.ded.misle.renderer.image.ImageManager;
import com.ded.misle.renderer.particles.core.ParticleRegistry;
import com.ded.misle.renderer.smoother.SmoothValue;
import com.ded.misle.world.data.Direction;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.player.Planner;
import com.ded.misle.world.entities.player.Player;
import com.ded.misle.world.logic.World;
import com.ded.misle.world.entities.npcs.NPC;
import com.ded.misle.input.MouseHandler;
import com.ded.misle.world.boxes.BoxHandling;
import com.ded.misle.items.Item;
import com.ded.misle.world.logic.attacks.Range;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Objects;

import static com.ded.misle.Launcher.*;
import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.renderer.FontManager.*;
import static com.ded.misle.renderer.image.ImageManager.mergeImages;
import static com.ded.misle.world.boxes.Box.getTexture;
import static com.ded.misle.world.boxes.Box.isInvalid;
import static com.ded.misle.world.entities.Entity.getEntities;
import static com.ded.misle.world.entities.npcs.NPC.getSelectedNPCs;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.DialogRenderer.renderDialog;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.renderer.image.ImageManager.cachedImages;
import static com.ded.misle.renderer.InventoryRenderer.*;
import static com.ded.misle.world.data.Direction.*;
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

        for (NetClient.Player netPlayer : player.getOnlinePlayerList()) {
            if (Objects.equals(player.getUUIDString(), netPlayer.uuid)) continue;
            int netPlayerScreenX = (int) (netPlayer.x * originalTileSize + player.getVisualOffsetX() * originalTileSize - player.pos.getCameraOffsetX());
            int netPlayerScreenY = (int) (netPlayer.y * originalTileSize + player.getVisualOffsetY() * originalTileSize - player.pos.getCameraOffsetY());

            FontMetrics fontMetrics = g2d.getFontMetrics();
            drawColoredText(g2d, removeColorIndicators(netPlayer.name),
                (int) (netPlayerScreenX - (double) fontMetrics.stringWidth(netPlayer.name) / 2 + 0.4 * originalTileSize + 1),
                netPlayerScreenY - fontMetrics.getAscent() + fontMetrics.getDescent() + 1,
                g2d.getFont(), new Color(0x33000000, true), true);

            Composite originalComposite = g2d.getComposite();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f);
            g2d.setComposite(ac);

            drawColoredText(g2d, removeColorIndicators(netPlayer.name),
                (int) (netPlayerScreenX - (double) fontMetrics.stringWidth(netPlayer.name) / 2 + 0.4 * originalTileSize),
                netPlayerScreenY - fontMetrics.getAscent() + fontMetrics.getDescent(),
                g2d.getFont(), new Color(0xEFEFEF), false);

            BufferedImage playerSprite = cachedImages.get(ImageManager.ImageName.PLAYER_FRONT0_EDIT);
            playerSprite = mergeImages(playerSprite, netPlayer.icon);
            drawRotatedImage(g2d, playerSprite,
                netPlayerScreenX - 0.2275 * originalTileSize,
                netPlayerScreenY - 0.2275 * originalTileSize,
                (int) (1.365 * originalTileSize),
                (int) (1.365 * originalTileSize), 0, false);

            if (netPlayer.heldItemID != 0) {
                try {
                    double posX = netPlayerScreenX + (double) (originalTileSize - 4) / 2;

                    drawRotatedImage(g2d, Objects.requireNonNull(Item.createItem(netPlayer.heldItemID)).getIcon(),
                        posX,
                        netPlayerScreenY,
                        originalTileSize, originalTileSize,
                        0, false);
                } catch (Exception ignored) {}
            }

            g2d.setComposite(originalComposite);
        }

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

        // Player planning
        Planner planner = player.getPlanner();
        if (planner.isPlanning()) drawPlanning(g2d);
        else {
            long lastTime = player.getPlanner().getLastTimeExecuted();
            long extraTime = 1000 + Math.min(30L * player.stepCounter.getCurrentStep(), 1500);
            long timeSinceEnd = currentTimeMillis() - lastTime;

            if (timeSinceEnd < extraTime) {
                float alpha = 1.0f - (float) timeSinceEnd / extraTime;
                alpha = Math.max(0, Math.min(1, alpha));

                Composite original = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                Font baseFont = plannerCounter;
                int centerX = originalScreenWidth / 2;
                int y = originalScreenHeight / 2 - 100;
                player.stepCounter.draw(g2d, baseFont, centerX, y);

                g2d.setComposite(original);
            }

        }

        // Player position adjustments
        player.updateVisualPosition(50f);
        player.updateVisualOffset(10f);
        player.pos.updateCameraOffset(200f);
        int playerScreenX = (int) (player.getRenderX() + player.getVisualOffsetX() * originalTileSize - player.pos.getCameraOffsetX());
        int playerScreenY = (int) (player.getRenderY() + player.getVisualOffsetY() * originalTileSize - player.pos.getCameraOffsetY());

        // Draw the player above every box
//        g2d.setColor(player.getColor());
//        Rectangle playerRect = new Rectangle(playerScreenX, playerScreenY, (int) player.getBoxScaleHorizontal(), (int) player.getBoxScaleVertical());
//        drawRotatedRect(g2d, playerRect, player.pos.getVisualRotation()); // CUBE PLAYER

        long precision = 200;
        Direction horizontalDirection = player.getRecentHorizontalDirection(precision);
        Direction verticalDirection = player.getRecentVerticalDirection(precision);
        Direction totalDirection = player.getRecentDirection(precision);
        BufferedImage playerSprite = null;
        boolean playerMirror = player.getHorizontalDirection() == LEFT;

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

        if (planner.isPlanning()) {
            planner.updateSmoothPos();

            int plannerScreenX = (int) (planner.getSmoothPos().x + player.getVisualOffsetX() * originalTileSize - player.pos.getCameraOffsetX() + originalTileSize / 10);
            int plannerScreenY = (int) (planner.getSmoothPos().y + player.getVisualOffsetY() * originalTileSize - player.pos.getCameraOffsetY() + originalTileSize / 10);

            Composite originalComposite = g2d.getComposite();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f);
            g2d.setComposite(ac);
            drawRotatedImage(g2d, playerSprite,
                plannerScreenX - player.getVisualScaleHorizontal() * 0.25 * originalTileSize,
                plannerScreenY - player.getVisualScaleVertical() * 0.25 * originalTileSize,
                (int) (player.getVisualScaleHorizontal() * 1.5 * originalTileSize),
                (int) (player.getVisualScaleVertical() * 1.5 * originalTileSize), player.pos.getRotation(), playerMirror);
            g2d.setComposite(originalComposite);
        }

        drawRotatedImage(g2d, playerSprite,
            playerScreenX - player.getVisualScaleHorizontal() * 0.25 * originalTileSize,
            playerScreenY - player.getVisualScaleVertical() * 0.25 * originalTileSize,
            (int) (player.getVisualScaleHorizontal() * 1.5 * originalTileSize),
            (int) (player.getVisualScaleVertical() * 1.5 * originalTileSize), player.pos.getRotation(), playerMirror);

        drawHandItem(g2d, playerScreenX, playerScreenY, mouseHandler);

        drawUIElements(g2d);

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

            if (player.getHorizontalDirection() == RIGHT) {
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


            FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
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


            FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
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
                    FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
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
                    FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
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

    static int maxDigitWidth = 12;
    private static void drawCoins(Graphics2D g2d) {
        final int coinRightX = 468;
        final int coinTextY = 32;
        int spacing = 4;

        g2d.setFont(coinTextFont);
        player.attr.updateBalance();
        String balanceText = String.valueOf(player.attr.getVisualBalance());

        int x = coinRightX - maxDigitWidth * balanceText.length();

        for (int i = 0; i < balanceText.length(); i++) {
            char c = balanceText.charAt(i);
            int charX = x + i * maxDigitWidth;

            g2d.setColor(coinTextShadowColor);
            drawColoredText(g2d, String.valueOf(c), charX + textShadow, coinTextY + textShadow);

            g2d.setColor(coinTextUI);
            drawColoredText(g2d, String.valueOf(c), charX, coinTextY);
        }

        FontMetrics fm = FontManager.getCachedMetrics(g2d, g2d.getFont());
        int coinIconX = coinRightX + spacing;
        int coinIconSize = fm.getHeight();
        int coinIconY = coinTextY - coinIconSize + 4;

        g2d.drawImage(
            cachedImages.get(ImageManager.ImageName.COIN),
            coinIconX, coinIconY,
            coinIconSize, coinIconSize,
            null
        );
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

    private void drawPlanning(Graphics2D g2d) {
        int i = 0;
        int max = player.getPlannerState().length;
        int scaledTile = (int) (originalTileSize * 1.2);
        int scaledTileDifference = originalTileSize / 5;
        int cameraOffsetX = (int) player.pos.getCameraOffsetX();
        int cameraOffsetY = (int) player.pos.getCameraOffsetY();

        Point finalPoint = player.getPlanner().hasEnemyPoint()
            ? player.getPlannerState()[player.getPlannerState().length - 2]
            : player.getPlanner().getEnd();

        Point previousPoint = new Point(player.getX(), player.getY());

        for (Point point : player.getPlannerState()) {
            Point difference = new Point(point.x - previousPoint.x, point.y - previousPoint.y);
            previousPoint = point;

            if (player.getPlannerState()[0].equals(point)) continue;

            i++;

            int screenX = point.x * originalTileSize - cameraOffsetX;
            int screenY = point.y * originalTileSize - cameraOffsetY;
            if (isInvalid(screenX, screenY)) continue;

            float progress = (float) i / max;
            int alpha = (int) (255 * (0.05f + progress * 0.65f));
            alpha = Math.max(alpha, 100);

            int r = (int) (planningColor.getRed() + (255 - planningColor.getRed()) * (1 - progress));
            int g = (int) (planningColor.getGreen() + (255 - planningColor.getGreen()) * (1 - progress));
            int b = (int) (planningColor.getBlue() + (255 - planningColor.getBlue()) * (1 - progress));
            Color color = new Color(r, g, b, alpha);

            float scale = (float) (0.7 + 0.3f * progress);
            int width = 2 * originalTileSize / 3;
            int height = 2 * originalTileSize / 3;
            width = (int) (width * scale);
            height = (int) (height * scale);
            int x = screenX + originalTileSize / 2 - width / 2;
            int y = screenY + originalTileSize / 2 - height / 2;
            int arcW = 5 * width / 16;
            int arcH = 5 * height / 16;

            boolean willDrawPreview = point.equals(player.getPlannerState()[player.getPlannerState().length - 1]);

            if (point.equals(finalPoint)) {
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.fillRect(screenX - (scaledTileDifference), screenY - (scaledTileDifference),
                    scaledTile + scaledTileDifference, scaledTile + scaledTileDifference);

                g2d.setColor(color);
                g2d.fillRect(screenX - (scaledTileDifference / 2), screenY - (scaledTileDifference / 2), scaledTile, scaledTile);

                if (willDrawPreview)
                    calculateAndDrawPreview(g2d, cameraOffsetX, cameraOffsetY, finalPoint, difference, width, height, arcW, arcH);
            }
            else {
                if (willDrawPreview) {
                    calculateAndDrawPreview(g2d, cameraOffsetX, cameraOffsetY, finalPoint, difference, width, height, arcW, arcH);
                } else {
                    g2d.setColor(color);

                    g2d.fillRoundRect(x, y,
                        width, height,
                        arcW, arcH);

                    g2d.setColor(new Color(255, 255, 255, 110));
                    g2d.drawRoundRect(x, y,
                        width, height,
                        arcW, arcH
                    );
                }
            }
        }

        if (player.getPlanner().isExecuting()) {
            long executionTime = currentTimeMillis() - player.getPlanner().getLastTimeStarted();
            float fadeDuration = 750f;
            float a = Math.min(1.0f, executionTime / fadeDuration);

            Composite original = g2d.getComposite();
            if (a != 1) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
            }

            Font baseFont = plannerCounter;
            int centerX = originalScreenWidth / 2;
            int y = originalScreenHeight / 2 - 100;
            player.stepCounter.draw(g2d, baseFont, centerX, y);

            g2d.setComposite(original);
        }
    }

    private void calculateAndDrawPreview(Graphics2D g2d, int cameraOffsetX, int cameraOffsetY, Point finalPoint, Point difference, int width, int height, int arcW, int arcH) {
        int screenX;
        int screenY;
        screenX = finalPoint.x * originalTileSize - cameraOffsetX;
        screenY = finalPoint.y * originalTileSize - cameraOffsetY;

        Rectangle rect = new Rectangle();
        rect.x = screenX + originalTileSize / 2 - width / 2;
        rect.y = screenY + originalTileSize / 2 - height / 2;
        rect.width = width;
        rect.height = height;
        Point arc = new Point(arcW, arcH);

        drawAttackPreview(g2d, rect, arc, difference);
    }

    private void drawAttackPreview(Graphics2D g2d, Rectangle rect, Point arc, Point directionVec) {
        Direction direction = interpretDirection(directionVec.x, directionVec.y);
        Range attackRange = player.animator.getRange(direction);

        int x = rect.x;
        int y = rect.y;

        for (Point attackPoint : attackRange.getPoints()) {
            g2d.setColor(new Color(0xA6DE4040, true));
            g2d.fillRoundRect(x + attackPoint.x * originalTileSize, y + attackPoint.y * originalTileSize,
                rect.width, rect.height,
                arc.x, arc.y);

            g2d.setColor(new Color(255, 255, 255, 110));
            g2d.drawRoundRect(x + attackPoint.x * originalTileSize, y + attackPoint.y * originalTileSize,
                rect.width, rect.height,
                arc.x, arc.y
            );
        }
    }

    private static void drawUIElements(Graphics2D g2d) {
        drawEntityHealthBars(g2d);
        ParticleRegistry.updateThenDraw(g2d);
        drawHealthBar(g2d);
        drawEntropyBar(g2d);
        drawXPBar(g2d);
        drawLevel(g2d);
        drawCoins(g2d);
        drawInventoryBar(g2d);
        drawSelectedItemName(g2d);
    }

    private static void drawEntityHealthBars(Graphics2D g2d) {
        Entity[] entities = getEntities().toArray(new Entity[0]);

        for (Entity e : entities) {
            if (e instanceof Player || !e.displayHP())
                continue;

            BufferedImage bar = ImageManager.cachedImages.get(ImageManager.ImageName.ENEMY_HEALTH_BAR);
            BufferedImage compatible = new BufferedImage(bar.getWidth(), bar.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = compatible.createGraphics();
            g.drawImage(bar, 0, 0, null);
            g.dispose();

            bar = compatible;

            int drawX = (int) (e.getRenderX() - player.pos.getCameraOffsetX());
            int drawY = (int) (e.getRenderY()
                - (originalTileSize * e.getVisualScaleVertical())
                + originalTileSize
                - player.pos.getCameraOffsetY())
                - 5;

            g2d.drawImage(bar, drawX, drawY, null);

            bar = ImageManager.cachedImages.get(ImageManager.ImageName.ENEMY_HEALTH_BAR_INSIDE);
            float w = bar.getWidth();

            SmoothValue hp = e.getHPSmoother();
            hp.update(12, Math.min(deltaTime, 0.01));

            w *= (float) (hp.getCurrentFloat() / e.getMaxHP());
            w = Math.max(w, 1);

            compatible = new BufferedImage((int) w, bar.getHeight(), BufferedImage.TYPE_INT_ARGB);
            g = compatible.createGraphics();
            g.drawImage(bar, 0, 0, null);
            g.dispose();

            bar = compatible;

            g2d.drawImage(bar, drawX, drawY, null);
        }
    }
}

