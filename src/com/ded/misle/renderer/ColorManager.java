package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;

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
    public final static Color healthBarShadow = defaultShadowColor;
    public final static Color healthBarBackground = new Color(0xA0A0A0);
    public final static Color healthBarCurrent = new Color(0x50EE50);
    public final static Color healthBarLockedHP = Color.DARK_GRAY;
    public final static Color healthBarTextShadow = defaultShadowColor;
    public final static Color healthBarText = new Color(225, 210, 170);
    public final static Color healthBarTextCritical = new Color(255, 100, 80);
        // Entropy UI
    public final static Color entropyBarShadow = defaultShadowColor;
    public final static Color entropyBarBackground = new Color(0xA0A0A0);
    public final static Color entropyBarCurrent = new Color(0xA0A0FF);
        // Coins
    public final static Color coinTextUI = new Color(225, 210, 170);
    public final static Color coinTextShadowColor = defaultShadowColor;

    // Menus
        // Buttons
    public final static Color buttonDefaultColor = new Color(0x5a4020);
    public final static Color buttonHoveredColor = new Color(0x422807);
    public final static Color buttonCurrentMenu = new Color(40, 25, 1);
    public final static Color buttonTextColor = new Color(0xe3c2a1);
    public final static Color buttonTextShadowColor = defaultShadowColor;
        // Menus themselves
    public final static Color menuBackgroundColor = new Color(0x9d7a53);
    public final static Color menuTitleColor = new Color(0xd1d1ab);
    public final static Color menuTitleShadowColor = defaultShadowColor;
    public final static Color menuVersionColor = new Color(217, 217, 217);
    public final static Color menuVersionShadowColor = defaultShadowColor;
    public final static Color progressBarShadowColor = defaultShadowColor;
    public final static Color progressBarColor = new Color(100, 200, 100);
    public final static Color progressBarPercentage = new Color(191, 191, 191);
    public final static Color progressBarPercentageShadow = defaultShadowColor;
    public final static Color settingsSeparatingBar = new Color(40, 25, 1);
    public final static Color FPSColor = new Color(50, 255, 50);
    public final static Color FPSShadowColor = defaultShadowColor;

    // Fading

    public final static Color fadingColor = new Color(0x000000);

    // Game panel

    public final static Color windowBackground = new Color(0, 0, 0);

    // Dialog

    public final static Color dialogWindowBackground = new Color(140, 110, 70, 245);
    public final static Color dialogTextColor = new Color(255, 255, 255);

    // Level Designer

    public final static Color gridColor = Color.BLACK;

    // Attribute colors

    public final static Color strengthColor = new Color(0xDB963D);
    public final static Color vitalityColor = new Color(0xDB423D);
    public final static Color entropyColor = new Color(0xA13DDB);
    public final static Color defenseColor = new Color(0x3D5ADB);
    public final static Color regenerationColor = new Color(0x52DB3D);
    public final static Color speedColor = new Color(0x3DD8DB);
    public final static Color slingshotDamageColor = new Color(0xDB963D);

    public enum StringToColorCode {
        SLINGSHOT_DAMAGE(LanguageManager.getText("slingshot_damage"), slingshotDamageColor),
        HP(LanguageManager.getText("hp"), vitalityColor),

        STRENGTH(LanguageManager.getText("inventory_strength"), strengthColor),
        ENTROPY(LanguageManager.getText("inventory_entropy"), entropyColor),
        VITALITY(LanguageManager.getText("inventory_vitality"), vitalityColor),
        DEFENSE(LanguageManager.getText("inventory_defense"), defenseColor),
        REGENERATION(LanguageManager.getText("inventory_regeneration"), regenerationColor),
        SPEED(LanguageManager.getText("inventory_speed"), speedColor),

        STR(LanguageManager.getText("short_strength"), strengthColor),
        ENT(LanguageManager.getText("short_entropy"), entropyColor),
        VIT(LanguageManager.getText("short_vitality"), vitalityColor),
        DEF(LanguageManager.getText("short_defense"), defenseColor),
        REG(LanguageManager.getText("short_regeneration"), regenerationColor),
        SPD(LanguageManager.getText("short_speed"), speedColor);

        public final String text;
        public final Color color;

        StringToColorCode(String text, Color color) {
            this.text = text;
            this.color = color;
        }
    }

    public static String replaceColorIndicators(String text) {
        text = text.replaceAll(" ", "_");
        return normalizeColorIndicator(StringToColorCode.valueOf(text).text, StringToColorCode.valueOf(text).color);
    }

    private static String normalizeColorIndicator(String text, Color color) {
        return "c{#" + Integer.toHexString(color.getRGB()).substring(2) + "," + text + "}";
    }

    public static void drawColoredText(Graphics2D g2d, String text, int x, int y, Font font, Color baseColor, boolean forceBaseColor) {
        g2d.setFont(font);

        if (text.contains("r{")) {
            String[] separatedText = text.split("r\\{");
            text = "";
            for (int i = 0; i < separatedText.length; i++) {
                String part = separatedText[i];
                if (i == 0) {
                    text = part;
                } else {
                    if (part.contains("}")) {
                        String[] insideColorIndicator = part.split("}");
                        String colorText = insideColorIndicator[0];
                        colorText = replaceColorIndicators(colorText);
                        text = text.concat(colorText);
                        if (insideColorIndicator.length > 1) {
                            text = text.concat(insideColorIndicator[1]);
                        }
                    } else {
                        text = text.concat(part);
                    }
                }
            }
        }

        if (!text.contains("c{") || forceBaseColor) {
            text = removeColorIndicators(text);
            g2d.setColor(baseColor);
            g2d.drawString(text, x, y);
        } else {
            Pattern pattern = Pattern.compile("c\\{(.*?),\\s*(.*?)}");
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
                Color color = new Color(0xFFFFFF);
                try {
                    color = "BASE".equals(part[1]) ? baseColor : Color.decode(part[1]);
                } catch (NumberFormatException e) {
                    color = "BASE".equals(part[1]) ? baseColor : ColorConstant.valueOf(part[1]).color;
                }
                g2d.setColor(color);
                g2d.drawString(snippet, x, y);
                x += g2d.getFontMetrics().stringWidth(snippet); // Move x forward
            }
        }
    }

    private enum ColorConstant {
        HEAL(healColor),
        ENTROPY(entropyColor),
        LIGHT_ENTROPY(entropyBarCurrent),
        DEFENSE(defenseColor),;

        public final Color color;

        ColorConstant(Color color) {
            this.color = color;
        }
    }

    public static String removeColorIndicators(String text) {
        return text.replaceAll("c\\{.*?,\\s*(.*?)}", "$1").replaceAll("r\\{}", "$1");
    }
}
