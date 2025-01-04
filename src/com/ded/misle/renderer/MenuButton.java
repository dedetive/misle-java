package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.MainRenderer.textShadow;
import static com.ded.misle.renderer.FontManager.buttonFont;
import static com.ded.misle.Launcher.scale;

public class MenuButton {
    Rectangle bounds;
    Color color;
    Runnable action;
    boolean isHovered;
    String text;

    private static final List<MenuButton> buttons = new ArrayList<>();

    public MenuButton(Rectangle bounds, Color defaultColor, Runnable action, String text) {
        this.bounds = bounds;
        this.color = defaultColor;
        this.action = action;
        this.isHovered = false;
        this.text = text;
    }

    public static void createButton(Rectangle bounds, String text, Runnable action, JPanel panel) {
        for (MenuButton button : buttons) {
            if (button.text.equals(text)) {
                return;
            }
        }
        MenuButton button = new MenuButton(bounds, buttonDefaultColor, action, text);
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
                        button.action.run();
                        panel.setCursor(Cursor.getDefaultCursor());
                        clearButtons();
                        break;
                    }
                }
            }
        });
    }

    private enum ButtonTextColorUpdater {
        main_menu_quit("#FF7070"),
        pause_menu_quit("#FF7070"),
        options_menu_go_back("#FF7070"),

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
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            } else if (button.isHovered) {
                button.isHovered = false;
                button.color = buttonDefaultColor;
                panel.setCursor(Cursor.getDefaultCursor());
                repaintNeeded = true;
            }
        }

        if (repaintNeeded) {
            panel.repaint();
        }
    }

    public static void drawButtons(Graphics2D g2d, double scaleByScreenSize) {
        for (MenuButton button : buttons) {
            int buttonBorderSize = (int) (69 * scaleByScreenSize);

            g2d.setColor(button.color);
            g2d.fillRoundRect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height,
                buttonBorderSize, buttonBorderSize);

            // TEXT SHADOW
            g2d.setFont(buttonFont);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(removeColorIndicators(button.text));
            int textHeight = fm.getAscent();
            int textX = button.bounds.x + (button.bounds.width - textWidth) / 2;
            int textY = button.bounds.y + (button.bounds.height + textHeight) / 2 - fm.getDescent() + (int) (2 * scale);
            g2d.setColor(buttonTextShadowColor);
            // Left, right, up, down
            drawColoredText(g2d, button.text, (int) (textX - textShadow), textY, g2d.getFont(), buttonTextShadowColor, true);
            drawColoredText(g2d, button.text, (int) (textX + textShadow), textY, g2d.getFont(), buttonTextShadowColor, true);
            drawColoredText(g2d, button.text, textX, (int) (textY - textShadow), g2d.getFont(), buttonTextShadowColor, true);
            drawColoredText(g2d, button.text, textX, (int) (textY + textShadow), g2d.getFont(), buttonTextShadowColor, true);

            // ACTUAL TEXT
            drawColoredText(g2d, button.text, textX, textY, g2d.getFont(), buttonTextColor, false);
        }
    }


    public static void clearButtons() { buttons.clear(); }
}

