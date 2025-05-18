package com.ded.misle.renderer;

import com.ded.misle.game.GamePanel;
import com.ded.misle.core.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.*;
import java.util.List;

import static com.ded.misle.game.GamePanel.deltaTime;
import static com.ded.misle.game.GamePanel.gameState;
import static com.ded.misle.core.LanguageManager.getCurrentScript;
import static com.ded.misle.core.PraspomiaNumberConverter.ConvertMode.TO_PRASPOMIA;
import static com.ded.misle.core.PraspomiaNumberConverter.impureConvertNumberSystem;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.MainRenderer.textShadow;
import static com.ded.misle.renderer.FontManager.buttonFont;
import static com.ded.misle.renderer.SettingsMenuRenderer.settingState;

public class MenuButton {
    Rectangle bounds;
    Color color;
    Runnable action;
    boolean isHovered;
    String text;
    int id;
    boolean needsToUpdate;
    public static final HashMap<Integer, Fader.FadingState> fadingState = new HashMap<>();
    public static final HashMap<Integer, Float> fadingProgress = new HashMap<>();

    private volatile static List<MenuButton> buttons = new ArrayList<>();

    public MenuButton(Rectangle bounds, Color defaultColor, Runnable action, String text, int id) {
        this.bounds = bounds;
        this.color = defaultColor;
        this.action = action;
        this.isHovered = false;
        this.text = text;
        this.id = id;
        this.needsToUpdate = true;
    }

    public static MenuButton createButton(Rectangle bounds, String text, Runnable action, JPanel panel, int id) {
        for (MenuButton button : buttons) {
            if (button.id == id && !button.needsToUpdate) {
                return button;
            }
        }
        MenuButton button = new MenuButton(bounds, buttonDefaultColor, action, text, id);
        buttons.add(button);
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouseLocation, panel);
        detectIfButtonHovered(mouseLocation, panel);

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                detectIfButtonHovered(e.getPoint(), panel);
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            Point clickPoint = e.getPoint();
            for (MenuButton button : buttons) {
                if (button.bounds.contains(clickPoint)) {
                    fadingState.put(button.id, Fader.FadingState.FADING_OUT);
                    fadingProgress.put(button.id, 0.75F);
                    button.action.run();
                    panel.setCursor(Cursor.getDefaultCursor());
                    clearButtons();
                    break;
                }
            }
            }
        });
        button.needsToUpdate = false;
        return button;
    }

    private enum ButtonTextColorUpdater {
        main_menu_quit("#FF7070"),
        pause_menu_quit("#FF7070"),
        settings_menu_go_back("#FF7070"),

        ;

        public final String color;

        ButtonTextColorUpdater(String color) {
            this.color = color;
        }

        public String getColor() {
            return this.color;
        }
    }

    private static void detectIfButtonHovered(Point mousePoint, JPanel panel) {
        boolean repaintNeeded = false;

        for (MenuButton button : buttons) {
            if (gameState == GamePanel.GameState.OPTIONS_MENU) {
                if (Objects.equals(button.text, LanguageManager.getText("settings_menu_" + String.valueOf(settingState).toLowerCase()))) {
                    button.color = buttonCurrentMenu;
                    repaintNeeded = true;
                    button.needsToUpdate = true;
                }
            }

            if (button.bounds.contains(mousePoint)) {
                if (!button.isHovered) {
                    button.isHovered = true;
                    button.color = buttonHoveredColor;
                    for (ButtonTextColorUpdater updater : ButtonTextColorUpdater.values()) {
                        if (Objects.equals(button.text, LanguageManager.getText(String.valueOf(updater)))) {
                            button.text = "c{" + updater.getColor() + "," + LanguageManager.getText(String.valueOf(updater)) + "}";
                        }
                    }
                    repaintNeeded = true;
                    button.needsToUpdate = true;
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            } else if (button.isHovered) {
                button.isHovered = false;
                button.color = buttonDefaultColor;
                panel.setCursor(Cursor.getDefaultCursor());
                repaintNeeded = true;
                button.needsToUpdate = true;
            }
        }

        if (repaintNeeded) {
            panel.repaint();
        }
    }

    private final static int buttonBorderOffsetPos = 4;
    private final static int buttonBorderOffsetSize = buttonBorderOffsetPos * 2;

    public static void drawButtons(Graphics2D g2d) {
        try {
            for (MenuButton button : buttons) {
                int buttonBorderSize = 18;

                // BORDER
                g2d.setColor(buttonBorderColor);
                g2d.fillRoundRect(button.bounds.x - buttonBorderOffsetPos, button.bounds.y - buttonBorderOffsetPos,
                    button.bounds.width + buttonBorderOffsetSize, button.bounds.height + buttonBorderOffsetSize,
                    buttonBorderSize + buttonBorderOffsetPos / 2, buttonBorderSize + buttonBorderOffsetPos / 2);

                // BUTTON
                g2d.setColor(button.color);
                g2d.fillRoundRect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height,
                    buttonBorderSize, buttonBorderSize);

                // TEXT SHADOW
                g2d.setFont(buttonFont);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth;
                if (getCurrentScript() == LanguageManager.Script.PRASPOMIC) {
                    textWidth = fm.stringWidth(removeColorIndicators(impureConvertNumberSystem(button.text, TO_PRASPOMIA)));
                } else {
                    textWidth = fm.stringWidth(removeColorIndicators(button.text));
                }
                int textHeight = fm.getAscent();
                int textX = button.bounds.x + (button.bounds.width - textWidth) / 2;
                int textY = button.bounds.y + (button.bounds.height + textHeight) / 2 - fm.getDescent() + 2;
                g2d.setColor(buttonTextShadowColor);
                // Left, right, up, down
                drawColoredText(g2d, button.text, (int) (textX - textShadow), textY, g2d.getFont(), buttonTextShadowColor, true);
                drawColoredText(g2d, button.text, (int) (textX + textShadow), textY, g2d.getFont(), buttonTextShadowColor, true);
                drawColoredText(g2d, button.text, textX, (int) (textY - textShadow), g2d.getFont(), buttonTextShadowColor, true);
                drawColoredText(g2d, button.text, textX, (int) (textY + textShadow), g2d.getFont(), buttonTextShadowColor, true);

                // ACTUAL TEXT
                drawColoredText(g2d, button.text, textX, textY, g2d.getFont(), buttonTextColor, false);

                // FADING
                if (fadingState.containsKey(button.id)) {
                    float progress = fadingProgress.get(button.id);
                    Color fadingColor = new Color((float) buttonFadingColor.getRed() / 256, (float) buttonFadingColor.getGreen() / 256,
                        (float) buttonFadingColor.getBlue() / 256, progress);
                    g2d.setColor(fadingColor);
                    g2d.fillRoundRect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height,
                        buttonBorderSize, buttonBorderSize);

                    fadingProgress.put(button.id, Math.max((float) (progress - 0.015 * deltaTime * 80), 0));
                    if (progress <= 0) {
                        fadingState.put(button.id, Fader.FadingState.UNFADED);
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            clearButtons();
        }
    }

    public static void createGoBackButton(JPanel panel, int id) {
        int buttonX = 356;
        int buttonY = 220;
        int buttonWidth = 109;
        int buttonHeight = 31;
        Rectangle button = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        createButton(button, LanguageManager.getText("settings_menu_go_back"), MenuRenderer::goToPreviousMenu, panel, id);
    }


    public static void clearButtons() {
        buttons.clear();
    }

    public static void clearButtonFading() {
        fadingProgress.clear();
        fadingState.clear();
    }
}

