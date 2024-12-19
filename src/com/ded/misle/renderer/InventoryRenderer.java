package com.ded.misle.renderer;

import com.ded.misle.LanguageManager;
import com.ded.misle.input_handler.MouseHandler;
import com.ded.misle.items.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.renderer.ImageRenderer.cachedImages;

public class InventoryRenderer {
    // [0] = Inventory and inventory bar
    // [1] = Rings and trophies
    public final static int[] slotSize = new int[]{(int) (32 * scale), (int) (24 * scale)};
    public final static int[] slotSpacing = new int[]{0, (int) (8 * scale)};
    public final static int[][] gridOffset = new int[][]{{(int) (225 * scale), (int) (148 * scale)}, {(int) (132 * scale), (int) (32 * scale)}};
    // For gridOffset, first value is either INVENTORY or EXTRA and second value is either X or Y

    public static void updateRendererVariableScales() {
        slotSize[0] = (int) (32 * scale);
        slotSpacing[0] = (int) (0 * scale);
        gridOffset[0][0] = (int) (225 * scale);
        gridOffset[0][1] = (int) (148 * scale);
        slotSize[1] = (int) (24 * scale);
        slotSpacing[1] = (int) (8 * scale);
        gridOffset[1][0] = (int) (132 * scale);
        gridOffset[1][1] = (int) (32 * scale);
    }

    public static void drawSelectedSlotOverlay(Graphics2D g2d, int slotX, int slotY, int slotSize) {
        g2d.setColor(new Color(255, 255, 255, 100)); // Semi-transparent overlay
        g2d.fillRect(slotX, slotY, slotSize, slotSize);
    }

    public static void renderInventoryMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Semi-transparent background overlay
        g2d.setColor(new Color(15, 15, 15, 130));
        g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

        // Start the grid

        g2d.drawImage(cachedImages.get(ImageRenderer.ImageName.INVENTORY_MENU), 0, 0, (int) screenWidth, (int) screenHeight, null);

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
                        g2d.setColor(Color.black);
                        g2d.drawString(Integer.toString(itemCount), (int) (textX + GameRenderer.textShadow), (int) (countY + GameRenderer.textShadow));
                        g2d.setColor(Color.white);
                        g2d.drawString(Integer.toString(itemCount), textX, countY);
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
                    g2d.drawImage(cachedImages.get(ImageRenderer.ImageName.INVENTORY_RINGLESS_EXTRA_SLOT), slotX, slotY, slotSize[1], slotSize[1], null);
                    g2d.drawImage(item.getIcon(), slotX, slotY, slotSize[1], slotSize[1], null);
                    int itemCount = item.getCount();
                    if (itemCount > 1) {
                        g2d.setFont(FontManager.itemCountFont);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(Integer.toString(itemCount));
                        int textX = slotX - textWidth + slotSize[1];
                        int countY = slotY + slotSize[1];
                        g2d.setColor(Color.black);
                        g2d.drawString(Integer.toString(itemCount), (int) (textX + GameRenderer.textShadow), (int) (countY + GameRenderer.textShadow));
                        g2d.setColor(Color.white);
                        g2d.drawString(Integer.toString(itemCount), textX, countY);
                    }
                }
            }
        }

        // Draw stats name
            // VIT
        drawStat(g2d, LanguageManager.getText("inventory_vitality"), 288, 33);
        String formattedMaxHP = Long.toString(Math.round(player.attr.getMaxHP()));
        formattedMaxHP = formattedMaxHP + LanguageManager.getText("inventory_vitality_measure_word");
        drawStat(g2d, formattedMaxHP, 288, 47);
            // DEF
        drawStat(g2d, LanguageManager.getText("inventory_defense"), 288, 65);
        String formattedDefense = Long.toString(Math.round(player.attr.getDefense()));
        formattedDefense = formattedDefense + LanguageManager.getText("inventory_defense_measure_word");
        drawStat(g2d, formattedDefense, 288, 79);
            // REG
        drawStat(g2d, LanguageManager.getText("inventory_regeneration"), 288, 97);
        String formattedRegeneration = Long.toString(Math.round(player.attr.getRegenerationQuality()));
        formattedRegeneration = formattedRegeneration + LanguageManager.getText("inventory_regeneration_measure_word");
        drawStat(g2d, formattedRegeneration, 288, 111);
            // ENT
        drawStat(g2d, LanguageManager.getText("inventory_entropy"), 384, 33);
        String formattedEntropy = Long.toString(Math.round(player.attr.getMaxEntropy()));
        formattedEntropy = formattedEntropy + LanguageManager.getText("inventory_entropy_measure_word");
        drawStat(g2d, formattedEntropy, 384, 47);
            // STR
        drawStat(g2d, LanguageManager.getText("inventory_strength"), 384, 65);
//		String formattedStrength = Long.toString(Math.round(player.attr.getStrength()));
//		formattedStrength = formattedStrength + LanguageManager.getText("inventory_strength_measure_word");
        String formattedStrength = "WIP";
        drawStat(g2d, formattedStrength, 384, 79);
            // SPD
        drawStat(g2d, LanguageManager.getText("inventory_speed"), 384, 97);
        String formattedSpeed = Long.toString(Math.round(player.attr.getSpeed()));
        formattedSpeed = formattedSpeed + LanguageManager.getText("inventory_speed_measure_word");
        drawStat(g2d, formattedSpeed, 384, 111);
    }

    private static void drawStat(Graphics2D g2d, String statText, int centerX, int y, Color textColor, Color shadowColor) {
        // Calculate center
        centerX = (int) (centerX * scale);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(statText);
        int startX = centerX - (textWidth / 2);
        y = (int) (y * scale);

        // Draw text shadow
        g2d.setColor(shadowColor);
        g2d.drawString(statText, (int) (startX + GameRenderer.textShadow), (int) (y + GameRenderer.textShadow));

        // Draw text
        g2d.setColor(textColor);
        g2d.drawString(statText, startX, y);
    }

    private static void drawStat(Graphics2D g2d, String statText, int centerX, int y) {
        g2d.setFont(FontManager.ubuntuFont44);
        Color textColor = new Color(230, 230, 180);
        Color shadowColor = Color.black;

        drawStat(g2d, statText, centerX, y, textColor, shadowColor);
    }

    public static void drawHoveredItemTooltip(Graphics g, int[] hoveredSlot, boolean isExtra) {
        Graphics2D g2d = (Graphics2D) g;

        int slotX;
        int slotY;
        Item hoveredItem = null;
        if (!isExtra) {
            if (hoveredSlot[0] == -1) {
                // If playing
                int inventoryBarWidth = (int) (120 * scale);
                int inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
                int inventoryBarY = (int) (screenHeight - 20 * scale - 60);

                int slotStartX = inventoryBarX + (inventoryBarWidth - (7 * slotSize[0] + 6 * slotSpacing[0])) / 2;
                slotX = slotStartX + hoveredSlot[1] * (slotSize[0] + slotSpacing[0]);
                slotY = (int) (inventoryBarY + (20 * scale - slotSize[0]) / 2);
                hoveredSlot[0] = 0;
            } else {
                // If in inventory menu

                int gridX = (int) (225 * scale);
                int gridY = (int) (148 * scale);

                int[] rowOrder = {3, 0, 1, 2};

                slotX = gridX + hoveredSlot[1] * (slotSize[0] + slotSpacing[0]);
                slotY = gridY + rowOrder[hoveredSlot[0]] * (slotSize[0] + slotSpacing[0]);
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

        drawHoveredItemTooltip(g2d, slotX, slotY, slotSize[1], hoveredItem, isExtra);
    }

    public static void drawHoveredItemTooltip(Graphics2D g2d, int slotX, int slotY, int slotSize, Item hoveredItem, boolean isInverted) {
        int invertedMultiplier = isInverted ? 1 : -1; // WIP

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
        g2d.setFont(FontManager.basicFont40);
        FontMetrics fm = g2d.getFontMetrics();

        // Calculate width based on text
        int tooltipWidth = Math.max(slotSize * 4, fm.stringWidth(itemName) + (int) (20 * scale / 3.75));
        int tooltipX = slotX - (tooltipWidth / 2) + slotSize / 2;
        int tooltipY;

        // Calculate dynamic height
        int lineHeight = fm.getHeight();
        int tooltipHeight = lineHeight * 4; // minimum height for basic text

        // Adjust for multi-line description
        String[] wrappedDescription = wrapText(itemDescription, tooltipWidth - 20, fm);
        tooltipHeight += lineHeight * wrappedDescription.length;

        java.util.List<String> differentEffects = new ArrayList<>();
        List<String[]> wrappedEffect = new ArrayList<>();
        int maxTooltipWidth = slotSize * 6; // Set maximum tooltip width

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
        int triangleHeight = slotSize / 2;
        tooltipY = slotY - tooltipHeight - (triangleHeight);

        // Draw rounded tooltip box
        g2d.setColor(new Color(84, 84, 84, 190));
        g2d.fillRoundRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, (int) (45 * scale / 3.75), (int) (45 * scale / 3.75));

        // Draw triangle
        int triangleBase = slotSize;
        int[] xPoints = { slotX + slotSize / 2 - triangleBase / 2, slotX + slotSize / 2 + triangleBase / 2, slotX + slotSize / 2 };
        int[] yPoints = { tooltipY + tooltipHeight, tooltipY + tooltipHeight, tooltipY + tooltipHeight + triangleHeight };
        g2d.fillPolygon(xPoints, yPoints, 3);

        // Draw text within tooltip

        // Item name

        int textX = tooltipX + 10;
        int textY = tooltipY + lineHeight;
        g2d.setColor(Color.black);
        g2d.drawString(itemName, (int) (textX + GameRenderer.textShadow), (int) (textY + GameRenderer.textShadow));
        g2d.setColor(hoveredItem.getNameColor());
        g2d.drawString(itemName, textX, textY);

        // Item count
        int itemNameWidth = fm.stringWidth(itemName);
        g2d.setColor(Color.black);
        g2d.drawString(itemCount, (int) (textX + itemNameWidth + GameRenderer.textShadow), (int) (textY + GameRenderer.textShadow));
        g2d.setColor(Color.decode("#FFFFFF"));
        g2d.drawString(itemCount, textX + itemNameWidth, textY);

        textY += lineHeight;

        // Item type
        g2d.setColor(Color.black);
        g2d.drawString(itemType, (int) (textX + GameRenderer.textShadow), (int) (textY + GameRenderer.textShadow));
        g2d.setColor(Color.decode("#E0DE9B"));
        g2d.drawString(itemType, textX, textY);
        textY += lineHeight;

        // Item effect
        for (String[] effectWrappedLines : wrappedEffect) {
            for (String line : effectWrappedLines) {
                g2d.setColor(Color.black);
                g2d.drawString(line, (int) (textX + GameRenderer.textShadow), (int) (textY + GameRenderer.textShadow));
                g2d.setColor(Color.decode("#00A2FF"));
                g2d.drawString(line, textX, textY);
                textY += lineHeight;
            }
        }

        // Item description
        for (String line : wrappedDescription) {
            g2d.setColor(Color.black);
            g2d.drawString(line, (int) (textX + GameRenderer.textShadow), (int) (textY + GameRenderer.textShadow));
            g2d.setColor(Color.decode("#A0A0A0"));
            g2d.drawString(line, textX, textY);
            textY += lineHeight;
        }
    }

    // Helper method to wrap text
    private static String[] wrapText(String text, int maxWidth, FontMetrics fm) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (String word : text.split(" ")) {
            if (fm.stringWidth(line + word) > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        lines.add(line.toString().trim());

        return lines.toArray(new String[0]);
    }

    public static void drawDraggedItem(Graphics2D g2d, MouseHandler mouseHandler) {
        Item draggedItem = player.inv.getDraggedItem();

        g2d.drawImage(draggedItem.getIcon(), mouseHandler.getMouseX(), mouseHandler.getMouseY(), slotSize[0], slotSize[0], null);
    }
}
