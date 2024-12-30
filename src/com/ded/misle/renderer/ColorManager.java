package com.ded.misle.renderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorManager {

    // Shadows

    public final static Color defaultShadowColor = Color.BLACK;

    // Floating text

    public final static Color damageColor = new Color(0xDE4040);
    public final static Color healColor = new Color(0x50EE50);
    public final static Color entropyGainColor = new Color(0xA0A0FF);
    public final static Color floatingTextShadow = defaultShadowColor;

    // Boxes

    public final static Color defaultBoxColor = new Color(0xFFFFFF);

    // Inventory UI

    public final static Color selectedSlotOverlay = new Color(255, 255, 255, 100);
    public final static Color backgroundOverlay = new Color(15, 15, 15, 130);
    public final static Color statColor = new Color(230, 230, 180);
    public final static Color statShadowColor = defaultShadowColor;
    public final static Color itemCountShadowColor = defaultShadowColor;
    public final static Color itemCountColor = Color.WHITE;
    public final static Color selectedItemNameShadowColor = defaultShadowColor;
        // Tooltip
    public final static Color hoveredTooltipBackground = new Color(84, 84, 84, 190);
    public final static Color itemTypeTooltip = new Color(0xE0DE9B);
    public final static Color itemCountTooltip = new Color(0xFFFFFF);
    public final static Color itemEffectTooltip = new Color(0xE0DE9B);
    public final static Color itemDescriptionTooltip = new Color(0xA0A0A0);
    public final static Color tooltipTextShadowColor = defaultShadowColor;

    // Game UI
        // Health UI
    public final static Color healthBarBackground = new Color(0xA0A0A0);
    public final static Color healthBarCurrent = new Color(0x50EE50);
    public final static Color healthBarLockedHP = Color.DARK_GRAY;
        // Entropy UI
    public final static Color entropyBarBackground = new Color(0xA0A0A0);
    public final static Color entropyBarCurrent = new Color(0xA0A0FF);

    // Menus
        // Buttons
    public final static Color buttonDefaultColor = new Color(70, 51, 5);
    public final static Color buttonHoveredColor = new Color(40, 25, 1);
    public final static Color buttonTextColor = new Color(225, 210, 170);
    public final static Color buttonTextShadowColor = defaultShadowColor;
        // Menus themselves
    public final static Color menuBackgroundColor = new Color(140, 110, 70);
    public final static Color menuTitleColor = new Color(200, 160, 105);
    public final static Color menuTitleShadowColor = defaultShadowColor;
    public final static Color menuVersionColor = new Color(217, 217, 217);
    public final static Color menuVersionShadowColor = defaultShadowColor;
    public final static Color progressBarColor = new Color(100, 200, 100);
    public final static Color progressBarPercentage = new Color(191, 191, 191);
    public final static Color progressBarPercentageShadow = defaultShadowColor;

    // Fading

    public final static Color fadingColor = new Color(0x000000);

    // Game panel

    public final static Color windowBackground = new Color(0, 0, 0);

    // Dialog

    public final static Color dialogWindowBackground = new Color(140, 110, 70, 245);
    public final static Color dialogTextColor = new Color(255, 255, 255);

    // Level Designer

    public final static Color gridColor = Color.BLACK;

    // Stats colors

    public final static Color strengthColor = new Color(0xDB963D);

    public static String replaceColorIndicators(String text) {
        return switch (text) {
            case "STRENGTH" -> "c{#" + Integer.toHexString(strengthColor.getRGB()).substring(2) + ",Strength}";
            default -> text;
        };
    }

    public static void drawColoredText(Graphics2D g2d, String text, int x, int y, Font font, Color baseColor, boolean forceBaseColor) {
        g2d.setFont(font);
        if (!text.contains("c{") || forceBaseColor) {
            text = removeColorIndicators(text);
            g2d.setColor(baseColor);
            g2d.drawString(text, x, y);
        } else {
            Pattern pattern = Pattern.compile("c\\{#([A-Fa-f0-9]{6}),\\s*(.*?)}");
            Matcher matcher = pattern.matcher(text);
            ArrayList<String[]> parts = new ArrayList<>();
            int lastEnd = 0;

            while (matcher.find()) {
                // Add the preceding text (with baseColor)
                if (matcher.start() > lastEnd) {
                    parts.add(new String[]{text.substring(lastEnd, matcher.start()), "BASE"});
                }

                // Extract color and content inside the block
                String colorCode = matcher.group(1);
                String coloredText = matcher.group(2);
                parts.add(new String[]{coloredText, colorCode});

                lastEnd = matcher.end();
            }

            // Add the remaining text after the last match (with baseColor)
            if (lastEnd < text.length()) {
                parts.add(new String[]{text.substring(lastEnd), "BASE"});
            }

            // Draw stuff
            g2d.setFont(font);
            for (String[] part : parts) {
                String snippet = part[0];
                Color color = "BASE".equals(part[1]) ? baseColor : Color.decode("#" + part[1]);
                g2d.setColor(color);
                g2d.drawString(snippet, x, y);
                x += g2d.getFontMetrics().stringWidth(snippet); // Move x forward
            }
        }
    }

    public static String removeColorIndicators(String text) {
        return text.replaceAll("c\\{#[A-Fa-f0-9]{6},\\s*(.*?)}", "$1");
    }
}
