package com.ded.misle.renderer;

import java.awt.*;

public class ColorManager {

    // Floating text

    public final static Color damageColor = new Color(0xDE4040);
    public final static Color healColor = new Color(0x50EE50);
    public final static Color entropyGainColor = new Color(0xA0A0FF);
    public final static Color floatingTextShadow = Color.BLACK;

    // Boxes

    public final static Color defaultBoxColor = new Color(0xFFFFFF);

    // Inventory UI

    public final static Color selectedSlotOverlay = new Color(255, 255, 255, 100);
    public final static Color backgroundOverlay = new Color(15, 15, 15, 130);
    public final static Color statColor = new Color(230, 230, 180);
    public final static Color statShadowColor = Color.BLACK;
    public final static Color itemCountShadowColor = Color.BLACK;
    public final static Color itemCountColor = Color.WHITE;
    public final static Color selectedItemNameShadowColor = Color.BLACK;
        // Tooltip
    public final static Color hoveredTooltipBackground = new Color(84, 84, 84, 190);
    public final static Color itemTypeTooltip = new Color(0xE0DE9B);
    public final static Color itemCountTooltip = new Color(0xFFFFFF);
    public final static Color itemEffectTooltip = new Color(0x00A2FF);
    public final static Color itemDescriptionTooltip = new Color(0xA0A0A0);
    public final static Color tooltipTextShadowColor = Color.BLACK;

    // Game UI
        // Health UI
    public final static Color healthBarBackground = Color.GRAY;
    public final static Color healthBarCurrentHP = Color.GREEN;
    public final static Color healthBarLockedHP = Color.DARK_GRAY;

    // Menus
        // Buttons
    public final static Color buttonDefaultColor = new Color(70, 51, 5);
    public final static Color buttonHoveredColor = new Color(40, 25, 1);
    public final static Color buttonTextColor = new Color(225, 225, 225);
    public final static Color buttonTextShadowColor = Color.BLACK;
        // Menus themselves
    public final static Color menuBackgroundColor = new Color(140, 110, 70);
    public final static Color menuTitleColor = new Color(233, 233, 233);
    public final static Color menuTitleShadowColor = Color.BLACK;
    public final static Color menuVersionColor = new Color(217, 217, 217);
    public final static Color menuVersionShadowColor = Color.BLACK;
    public final static Color progressBarColor = new Color(100, 200, 100);
    public final static Color progressBarPercentage = new Color(191, 191, 191);
    public final static Color progressBarPercentageShadow = Color.BLACK;

    // Fading

    public final static int fadingColorR = 0;
    public final static int fadingColorG = 0;
    public final static int fadingColorB = 0;

    // Game panel

    public final static Color windowBackground = new Color(0, 0, 0);

    // Level Designer

    public final static Color gridColor = Color.BLACK;
}
