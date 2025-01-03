package com.ded.misle.renderer;

import com.ded.misle.npcs.NPC;
import com.ded.misle.input_handler.MouseHandler;
import com.ded.misle.boxes.BoxHandling;
import com.ded.misle.items.Item;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.boxes.Box.getTexture;
import static com.ded.misle.npcs.NPC.getSelectedNPCs;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.DialogRenderer.renderDialog;
import static com.ded.misle.renderer.FontManager.coinTextFont;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.renderer.ImageRenderer.cachedImages;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.player.PlayerStats.Direction.LEFT;
import static com.ded.misle.player.PlayerStats.Direction.RIGHT;
import static com.ded.misle.renderer.InventoryRenderer.*;
import static java.lang.System.currentTimeMillis;

public class PlayingRenderer {
    private static final java.util.List<String> floatingText = new ArrayList<>();
    private static final java.util.List<Point> floatingTextPosition = new ArrayList<>();
    private static final java.util.List<Color> floatingTextColor = new ArrayList<>();

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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw game components
        BoxHandling.renderBoxes(g2d, player.pos.getCameraOffsetX(), player.pos.getCameraOffsetY(), scale, tileSize);

        // Draw selected NPC indicator
        ArrayList<NPC> selectedNPCs = getSelectedNPCs();
        for (NPC npc : selectedNPCs) {
            for (int i = 0; i <= 270; i += 90) {
                drawRotatedImage(g2d, getTexture("wall_default_overlayW"), npc.getX() * scale - player.pos.getCameraOffsetX(), npc.getY() * scale - player.pos.getCameraOffsetY(),
                    (int) (tileSize * npc.getBoxScaleHorizontal()), (int) (tileSize * npc.getBoxScaleVertical()), i + npc.getRotation());
            }
        }

        // Player position adjustments
        int playerScreenX = (int) (player.getX() - player.pos.getCameraOffsetX());
        int playerScreenY = (int) (player.getY() - player.pos.getCameraOffsetY());

        // Draw the player above every box
        g2d.setColor(player.getColor());
        Rectangle playerRect = new Rectangle(playerScreenX, playerScreenY, (int) player.getBoxScaleHorizontal(), (int) player.getBoxScaleVertical());
        drawRotatedRect(g2d, playerRect, player.pos.getRotation());

        drawHandItem(g2d, playerScreenX, playerScreenY, scaleByScreenSize, mouseHandler);

        drawUIElements(g2d);

        if (floatingText != null) {
            drawFloatingText(g2d);
        }

        if (gameState == GameState.INVENTORY) {
            InventoryRenderer.renderInventoryMenu(g);
            if (mouseHandler.getHoveredSlot()[0] > -1 && mouseHandler.getHoveredSlot()[1] > -1 && player.inv.getItem(mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]) != null) {
                InventoryRenderer.drawHoveredItemTooltip(g, new int[]{mouseHandler.getHoveredSlot()[0], mouseHandler.getHoveredSlot()[1]}, false);
            } else if (mouseHandler.getExtraHoveredSlot()[0] > -1 && mouseHandler.getExtraHoveredSlot()[1] > -1 && player.inv.getItem(mouseHandler.getExtraHoveredSlot()[0] * 2 + mouseHandler.getExtraHoveredSlot()[1]) != null) {
                InventoryRenderer.drawHoveredItemTooltip(g, new int[]{mouseHandler.getExtraHoveredSlot()[1], mouseHandler.getExtraHoveredSlot()[0]}, true);
            }
            if (player.inv.getDraggedItem() != null) {
                InventoryRenderer.drawDraggedItem(g2d, mouseHandler);
            }
        } else {
            if (mouseHandler.getHoveredBarSlot() > -1 && player.inv.getItem(0, mouseHandler.getHoveredBarSlot()) != null) {
                InventoryRenderer.drawHoveredItemTooltip(g, new int[]{-1, mouseHandler.getHoveredBarSlot()}, false);
            }
        }

        if (gameState == GameState.DIALOG) {
            renderDialog(g2d);
        }

        if (isFading != MainRenderer.FadingState.UNFADED) drawFading(g2d);

        g2d.dispose();
    }

    private static void drawHandItem(Graphics2D g2d, double playerScreenX, double playerScreenY, double scaleByScreenSize, MouseHandler mouseHandler) {
        if (player.inv.hasHeldItem()) {

            if (mouseHandler.getMouseHorizontalDirection() == RIGHT) {
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

            drawRotatedImage(g2d, selectedItem.getIcon(), (int) distance + selectedItem.getAnimationX() * isFacingRight * scale / 3.75, playerScreenY + selectedItem.getAnimationY() * scale / 3.75, (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), (int) (100 * scaleByScreenSize * selectedItem.getAnimationBulk()), selectedItem.getAnimationRotation() * Math.ceil(isFacingRight) + mouseHandler.getMouseHorizontalRotation(), mirror);
        }
    }

    public static void createFloatingText(String textToDisplay, Color color, int x, int y, boolean movesUp) {
        floatingText.add(textToDisplay);
        Point point = new Point(x, y);
        floatingTextPosition.add(point);
        floatingTextColor.add(color);

        try {

            AtomicInteger index = new AtomicInteger(floatingTextPosition.size() - 1);
            Timer movingUp = new Timer(200, e -> {
                if (index.get() != -1) {
                    Point newPoint = new Point(floatingTextPosition.get(index.get()).x, (floatingTextPosition.get(index.get()).y - 1));
                    floatingTextPosition.set(index.get(), newPoint);
                }
            });
            if (movesUp) {
                movingUp.setRepeats(true);
                movingUp.start();
            }

            Timer timer = new Timer(2500, l -> {
                index.addAndGet(-1);
                movingUp.stop();
                floatingText.removeFirst();
                floatingTextPosition.removeFirst();
                floatingTextColor.removeFirst();
            });
            timer.setRepeats(false);
            timer.start();
        } catch (IndexOutOfBoundsException e) {
            // This would mean floatingText was removed, so stop
        }
    }

    private static void drawFloatingText(Graphics2D g2d) {
        for (int i = 0; i < floatingText.size() - 1; i++) {
            g2d.setFont(FontManager.itemInfoFont);
            g2d.setColor(floatingTextShadow);
            g2d.drawString(floatingText.get(i), (int) ((floatingTextPosition.get(i).x) * scale + MainRenderer.textShadow), (int) ((floatingTextPosition.get(i).y) * scale + MainRenderer.textShadow));
            g2d.setColor(floatingTextColor.get(i));
            g2d.drawString(floatingText.get(i), (int) (floatingTextPosition.get(i).x * scale), (int) (floatingTextPosition.get(i).y * scale));
        }
    }

    private static void drawHealthBar(Graphics2D g2d) {
        int healthBarWidth = (int) (15 * scale);
        int healthBarHeight = (int) (50 * scale);
        int healthBarX = (int) (30 * scale);
        int healthBarY = (int) (212 * scale);

        // Calculate the percentage of health remaining
        double healthPercentage = Math.min(player.getHP() / player.getMaxHP(), 1);

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
    }

    private static void drawEntropyBar(Graphics2D g2d) {
        int entropyBarWidth = (int) (15 * scale);
        int entropyBarHeight = (int) (50 * scale);
        int entropyBarX = (int) (65 * scale);
        int entropyBarY = (int) (212 * scale);

        // Calculate the percentage of entropy remaining
        double entropyPercentage = Math.min(player.attr.getEntropy() / player.attr.getMaxEntropy(), 1);

        // Draw the background of the entropy bar
        g2d.setColor(entropyBarBackground);
        g2d.fillRect(entropyBarX, entropyBarY, entropyBarWidth, entropyBarHeight);

        // Draw the current entropy bar
        g2d.setColor(entropyBarCurrent);
        g2d.fillRect(entropyBarX, (int) (entropyBarY + entropyBarHeight - entropyBarHeight * entropyPercentage), entropyBarWidth, (int) (entropyBarHeight * entropyPercentage));
    }

    private static void drawInventoryBar(Graphics2D g2d) {

        g2d.drawImage(cachedImages.get(ImageRenderer.ImageName.INVENTORY_BAR), 0, (int) (screenHeight - 82 * Math.pow(scale, (double) 1 /2)), (int) (512 * scale), (int) (35 * scale), null);

        // Slots info

        int selectedSlot = player.inv.getSelectedSlot();

        for (int i = 0; i < 7; i++) {
            int slotX = slotStartX + i * (slotSize[0] + slotSpacing[0]);
            int slotY = inventoryBarY + (inventoryBarHeight - slotSize[0]) / 2;

            // Draw the slot (DISABLED, ENABLE FOR TESTING)
//			g2d.setColor(Color.GRAY);
//			g2d.fillRect(slotX, slotY, slotSize, slotSize);

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
                    g2d.drawString(Integer.toString(itemCount), (int) (textX + MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow));
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

                    drawColoredText(g2d, selectedItemName, (int) (textX + MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow),
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

    public static void drawCoins(Graphics2D g2d) {
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

    private static void drawUIElements(Graphics2D g2d) {
        drawHealthBar(g2d);
        drawEntropyBar(g2d);
        drawCoins(g2d);
        drawInventoryBar(g2d);
        drawSelectedItemName(g2d);
    }
}
