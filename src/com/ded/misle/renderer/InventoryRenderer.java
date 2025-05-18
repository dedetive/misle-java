package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.input.MouseHandler;
import com.ded.misle.items.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.ImageManager.cachedImages;

public abstract class InventoryRenderer {
    // [0] = Inventory and inventory bar
    // [1] = Rings and trophies
    public final static int[] slotSize = new int[]{32, 24};
    public final static int[] slotSpacing = new int[]{0, 8};
    public final static int[][] gridOffset = new int[][]{{225, 148}, {132, 32}};
    // For gridOffset, first value is either INVENTORY or EXTRA and second value is either X or Y

    public static void drawSelectedSlotOverlay(Graphics2D g2d, int slotX, int slotY, int slotSize) {
        g2d.setColor(selectedSlotOverlay); // Semi-transparent overlay
        g2d.fillRect(slotX, slotY, slotSize, slotSize);
    }

    public static void renderInventoryMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Semi-transparent background overlay
        g2d.setColor(backgroundOverlay);
        g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

        // Start the grid

        g2d.drawImage(cachedImages.get(ImageManager.ImageName.INVENTORY_MENU), 0, 0, (int) screenWidth, (int) screenHeight, null);

        // Draw slots and item icons in the row order {1, 2, 3, 0}
        int[] rowOrder = {1, 2, 3, 0};

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 7; i++) {
                int slotX = gridOffset[0][0] + i * (slotSize[0] + slotSpacing[0]);
                int slotY = gridOffset[0][1] + j * (slotSize[0] + slotSpacing[0]);

                // Draw the slot as a gray rectangle (DISABLED, ENABLE FOR TESTING)
//				g2d.setColor(new Color(0x44, 0x44, 0x44, 120));
//				g2d.fillRect(slotX, slotY, slotSize, slotSize);

                // Draw item icon if there is one in this slot
                Item item = player.inv.getItem(rowOrder[j], i);
                if (item != null) {
                    g2d.drawImage(item.getIcon(), slotX, slotY, slotSize[0], slotSize[0], null);
                    int itemCount = item.getCount();
                    if (itemCount > 1) {
                        g2d.setFont(FontManager.itemCountFont);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(Integer.toString(itemCount));
                        int textX = slotX - textWidth + slotSize[0];
                        int countY = slotY + slotSize[0];
                        g2d.setColor(itemCountShadowColor);
                        drawColoredText(g2d, Integer.toString(itemCount), (int) (textX + MainRenderer.textShadow), (int) (countY + MainRenderer.textShadow));
                        g2d.setColor(itemCountColor);
                        drawColoredText(g2d, Integer.toString(itemCount), textX, countY);
                    }
                }
            }
        }

        // Draw extra slots (rings and trophy)
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 2; i++) {
                int slotX = gridOffset[1][0] + i * (slotSize[1] + slotSpacing[1]);
                int slotY = gridOffset[1][1] + j * (slotSize[1] + slotSpacing[1]);

                Item item = player.inv.getItem(i * 2 + j);
                if (item != null) {
                    g2d.drawImage(cachedImages.get(ImageManager.ImageName.INVENTORY_RINGLESS_EXTRA_SLOT), slotX, slotY, slotSize[1], slotSize[1], null);
                    g2d.drawImage(item.getIcon(), slotX, slotY, slotSize[1], slotSize[1], null);
                    int itemCount = item.getCount();
                    if (itemCount > 1) {
                        g2d.setFont(FontManager.itemCountFont);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(Integer.toString(itemCount));
                        int textX = slotX - textWidth + slotSize[1];
                        int countY = slotY + slotSize[1];
                        g2d.setColor(itemCountShadowColor);
                        drawColoredText(g2d, Integer.toString(itemCount), (int) (textX + MainRenderer.textShadow), (int) (countY + MainRenderer.textShadow));
                        g2d.setColor(itemCountColor);
                        drawColoredText(g2d, Integer.toString(itemCount), textX, countY);
                    }
                }
            }
        }

        // Draw stats name
            // VIT
        drawStat(g2d, LanguageManager.getText("inventory_vitality"), 288, 33);
        String formattedMaxHP = Long.toString(Math.round(player.getMaxHP()));
        formattedMaxHP = formattedMaxHP + LanguageManager.getText("inventory_vitality_measure_word");
        drawStat(g2d, formattedMaxHP, 288, 47);
            // DEF
        drawStat(g2d, LanguageManager.getText("inventory_defense"), 288, 65);
        String formattedDefense = Long.toString(Math.round(player.getDefense()));
        formattedDefense = formattedDefense + LanguageManager.getText("inventory_defense_measure_word");
        drawStat(g2d, formattedDefense, 288, 79);
            // REG
        drawStat(g2d, LanguageManager.getText("inventory_regeneration"), 288, 97);
        String formattedRegeneration = Long.toString(Math.round(player.getRegenerationQuality()));
        formattedRegeneration = formattedRegeneration + LanguageManager.getText("inventory_regeneration_measure_word");
        drawStat(g2d, formattedRegeneration, 288, 111);
            // ENT
        drawStat(g2d, LanguageManager.getText("inventory_entropy"), 384, 33);
        String formattedEntropy = Long.toString(Math.round(player.attr.getMaxEntropy()));
        formattedEntropy = formattedEntropy + LanguageManager.getText("inventory_entropy_measure_word");
        drawStat(g2d, formattedEntropy, 384, 47);
            // STR
        drawStat(g2d, LanguageManager.getText("inventory_strength"), 384, 65);
		String formattedStrength = Long.toString(Math.round(player.attr.getStrength()));
		formattedStrength = formattedStrength + LanguageManager.getText("inventory_strength_measure_word");
        drawStat(g2d, formattedStrength, 384, 79);
            // SPD
        drawStat(g2d, LanguageManager.getText("inventory_speed"), 384, 97);
        String formattedSpeed = Long.toString(1);
        formattedSpeed = formattedSpeed + LanguageManager.getText("inventory_speed_measure_word");
        drawStat(g2d, formattedSpeed, 384, 111);
            // LEVEL
        String levelText = LanguageManager.getText("inventory_level");
        String formattedLevel = Long.toString(player.attr.getLevel());
        formattedLevel = formattedLevel + LanguageManager.getText("inventory_level_measure_word");
        drawStat(g2d, levelText + " " + formattedLevel, 336, 129);
            // XP
        int xpBarWidth = 7;
        int xpBarHeight = 70;
        int xpBarX = 430;
        int xpBarY = 40;
        final int shadowExtra = 3;
        final int shadowWidth = xpBarWidth + shadowExtra;
        final int shadowHeight = xpBarHeight + shadowExtra;
        final int shadowX = xpBarX - shadowExtra / 2;
        final int shadowY = xpBarY - shadowExtra / 2;
        double xpPercentage = Math.clamp(player.attr.getXP() / player.attr.getXPtoLevelUp(), 0, 1);

        // Shadow
        g2d.setColor(xpBarShadow);
        g2d.fillRect(shadowX, shadowY, shadowWidth, shadowHeight);

        // Draw the background of the xp bar
        g2d.setColor(xpBarBackground);
        g2d.fillRect(xpBarX, xpBarY, xpBarWidth, xpBarHeight);

        // Draw the current xp bar
        g2d.setColor(xpBarCurrent);
        g2d.fillRect(xpBarX, (int) Math.ceil((xpBarY + xpBarHeight - xpBarHeight * xpPercentage)), xpBarWidth, (int) (xpBarHeight * xpPercentage));
    }

    private static void drawStat(Graphics2D g2d, String statText, int centerX, int y, Color textColor, Color shadowColor) {
        // Calculate center
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(statText);
        int startX = centerX - (textWidth / 2);

        // Draw text shadow
        g2d.setColor(shadowColor);
        drawColoredText(g2d, statText, (int) (startX + MainRenderer.textShadow), (int) (y + MainRenderer.textShadow));

        // Draw text
        g2d.setColor(textColor);
        drawColoredText(g2d, statText, startX, y);
    }

    private static void drawStat(Graphics2D g2d, String statText, int centerX, int y) {
        g2d.setFont(FontManager.buttonFont);

        drawStat(g2d, statText, centerX, y, statColor, statShadowColor);
    }

    public static void drawHoveredItemTooltip(Graphics g, int[] hoveredSlot, boolean isExtra, MouseHandler mouseHandler) {
        Graphics2D g2d = (Graphics2D) g;

        int slotX;
        int slotY;
        Item hoveredItem = null;
        if (!isExtra) {
            if (hoveredSlot[0] == -1) {
                // If playing
                int inventoryBarWidth = 120;
                int inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
                int inventoryBarY = (int) (screenHeight - 100);

                int slotStartX = inventoryBarX + (inventoryBarWidth - (7 * slotSize[0] + 6 * slotSpacing[0])) / 2;
                slotX = slotStartX + hoveredSlot[1] * (slotSize[0] + slotSpacing[0]);
                slotY = inventoryBarY + slotSize[0] / 2;
                hoveredSlot[0] = 0;
            } else {
                // If in inventory menu

                int[] rowOrder = {3, 0, 1, 2};

                slotX = gridOffset[0][0] + hoveredSlot[1] * (slotSize[0] + slotSpacing[0]);
                slotY = gridOffset[0][1] + rowOrder[hoveredSlot[0]] * (slotSize[0] + slotSpacing[0]);
            }

            hoveredItem = player.inv.getItem(hoveredSlot[0], hoveredSlot[1]);

            if (gameState == GameState.INVENTORY || (hoveredSlot[1] != player.inv.getSelectedSlot() && gameState == GameState.PLAYING)) {
                drawSelectedSlotOverlay(g2d, slotX, slotY, slotSize[0]);
            }
        } else {
            slotX = gridOffset[1][0] + hoveredSlot[0] * (slotSize[1] + slotSpacing[1]);
            slotY = gridOffset[1][1] + hoveredSlot[1] * (slotSize[1] + slotSpacing[1]);

            hoveredItem = player.inv.getItem(hoveredSlot[0] * 2 + hoveredSlot[1]);

            drawSelectedSlotOverlay(g2d, slotX, slotY, slotSize[1]);
        }

        drawHoveredItemTooltip(g2d, slotSize[0], hoveredItem, isExtra, mouseHandler);
    }

    public static void drawHoveredItemTooltip(Graphics2D g2d, int slotSize, Item hoveredItem, boolean isInverted, MouseHandler mouseHandler) {
        int invertedMultiplier = isInverted ? 1 : -1; // WIP

        int slotX = mouseHandler.getMouseX() - slotSize / 2;
        int slotY = mouseHandler.getMouseY() - slotSize / 4;

        // Get item details
        String itemName;
        try {
            itemName = hoveredItem.getDisplayName();
        } catch (NullPointerException e) {
            return;
        }
        String itemCount = "";
        if (hoveredItem.getCount() > 1) {
            itemCount = " (" + hoveredItem.getCount() + "x)";
        }
        String itemType = hoveredItem.getDisplayType();
        String itemEffect = hoveredItem.getDisplayEffect();
        String itemDescription = "\"" + hoveredItem.getDescription() + "\"";

        // Font and dimensions
        g2d.setFont(FontManager.itemInfoFont);
        FontMetrics fm = g2d.getFontMetrics();

        // Calculate width based on text
        int tooltipWidth = Math.max(slotSize * 4, fm.stringWidth(removeColorIndicators(itemName)) + 20);
        int tooltipX = slotX - (tooltipWidth / 2) + slotSize / 2;
        int tooltipY;

        // Calculate dynamic height
        int lineHeight = fm.getHeight();
        int tooltipHeight = (int) (lineHeight * 4.35); // minimum height for basic text

        // Adjust for multi-line description
        String[] wrappedDescription = wrapText(removeColorIndicators(itemDescription), tooltipWidth - 20, fm);
        tooltipHeight += lineHeight * wrappedDescription.length;

        java.util.List<String> differentEffects = new ArrayList<>();
        List<String[]> wrappedEffect = new ArrayList<>();
        int maxTooltipWidth = slotSize * 8; // Set maximum tooltip width

        // Split itemEffect into individual lines
        String[] effects = itemEffect.split("\\\\n");
        Collections.addAll(differentEffects, effects);
        tooltipWidth = Math.min(maxTooltipWidth, Math.max(tooltipWidth, fm.stringWidth(itemEffect) + 20));

        // Wrap each line of text
        for (String effect : differentEffects) {
            wrappedEffect.add(wrapText(effect, tooltipWidth - 20, fm));
        }
        tooltipHeight += lineHeight * (wrappedEffect.size() - 1); // Adjust height based on wrapped lines

        // Shift tooltip upwards if text exceeds height
        int triangleHeight = slotSize / 4;
        tooltipY = slotY - tooltipHeight - (triangleHeight);

        // Draw rounded tooltip box
        g2d.setColor(hoveredTooltipBackground);
        g2d.fillRoundRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, 45, 45);

        // Draw triangle
        int triangleBase = slotSize;
        int[] xPoints = { slotX + slotSize / 2 - triangleBase / 2, slotX + slotSize / 2 + triangleBase / 2, slotX + slotSize / 2 };
        int[] yPoints = { tooltipY + tooltipHeight, tooltipY + tooltipHeight, tooltipY + tooltipHeight + triangleHeight };
        g2d.fillPolygon(xPoints, yPoints, 3);

        // Draw text within tooltip

            // Item name
        int textX = tooltipX + 10;
        int textY = tooltipY + lineHeight;
        drawColoredText(g2d, itemName, (int) (textX + MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow),
            g2d.getFont(), tooltipTextShadowColor, true);
        drawColoredText(g2d, itemName, textX, textY,
            g2d.getFont(), hoveredItem.getNameColor(), false);

            // Item count
        int itemNameWidth = fm.stringWidth(itemName);
        g2d.setColor(tooltipTextShadowColor);
        drawColoredText(g2d, itemCount, (int) (textX + itemNameWidth + MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow));
        g2d.setColor(itemCountTooltip);
        drawColoredText(g2d, itemCount, textX + itemNameWidth, textY);

        textY += lineHeight;

            // Item type
        drawColoredText(g2d, itemType, (int) (textX + MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow),
            g2d.getFont(), tooltipTextShadowColor, true);
        drawColoredText(g2d, itemType, textX, textY,
            g2d.getFont(), itemTypeTooltip, false);
        textY += lineHeight;


            // Item effect
        for (String[] effectWrappedLines : wrappedEffect) {
            for (String line : effectWrappedLines) {
                drawColoredText(g2d, line, (int) (textX + MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow),
                    g2d.getFont(), tooltipTextShadowColor, true);
                drawColoredText(g2d, line, textX, textY,
                    g2d.getFont(), itemEffectTooltip, false);
                textY += lineHeight;
            }
        }

            // Item description
        for (String line : wrappedDescription) {
            drawColoredText(g2d, line, (int) (textX + MainRenderer.textShadow), (int) (textY + MainRenderer.textShadow),
                g2d.getFont(), tooltipTextShadowColor, true);
            drawColoredText(g2d, line, textX, textY,
                g2d.getFont(), itemDescriptionTooltip, false);

            textY += lineHeight;
        }
    }

    // Helper method to wrap text
    public static String[] wrapText(String text, int maxWidth, FontMetrics fm) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (String word : text.split(" ")) {
            boolean containsEscape = word.contains("\\n");
            if (fm.stringWidth(removeColorIndicators(line + word)) > maxWidth || containsEscape) {
                lines.add(line.toString());
                line = new StringBuilder();
            }
            if (!containsEscape) {
                line.append(word).append(" ");
            }
        }
        lines.add(line.toString().trim());

        return lines.toArray(new String[0]);
    }

    private static final SmoothPosition smoothPos = new SmoothPosition(0, 0);

    public static void updateMousePos(MouseHandler mouseHandler) {
        int originX = mouseHandler.getMouseX() - slotSize[0] / 2;
        int originY = mouseHandler.getMouseY() - slotSize[0] / 2;
        smoothPos.setTarget(originX, originY);
        smoothPos.update(75f, 1);
    }

    public static void drawDraggedItem(Graphics2D g2d) {
        Item draggedItem = player.inv.getDraggedItem();

        int posX = smoothPos.getRenderX();
        int posY = smoothPos.getRenderY();

        g2d.drawImage(draggedItem.getIcon(), posX, posY, slotSize[0], slotSize[0], null);
    }
}
