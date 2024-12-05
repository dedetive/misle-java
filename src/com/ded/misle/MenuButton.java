package com.ded.misle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import static com.ded.misle.GameRenderer.textShadow;
import static com.ded.misle.GameRenderer.ubuntuFont44;
import static com.ded.misle.Launcher.scale;

public class MenuButton {
    static Color defaultColor = new Color(70, 51, 5);
    static Color hoverColor = new Color(40, 25, 1);
    static Color shadowColor = new Color(40, 25, 1);

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

    public static void createButton(Rectangle bounds, String text, Runnable action, JPanel panel, Graphics2D g2d, double scaleByScreenSize) {
        for (MenuButton button : buttons) {
            if (button.text.equals(text)) {
                return;
            }
        }
        MenuButton button = new MenuButton(bounds, defaultColor, action, text);
        buttons.add(button);

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point mousePoint = e.getPoint();
                boolean repaintNeeded = false;

                for (MenuButton button : buttons) {
                    if (button.bounds.contains(mousePoint)) {
                        if (!button.isHovered) {
                            button.isHovered = true;
                            button.color = hoverColor;
                            repaintNeeded = true;
                            panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        }
                    } else if (button.isHovered) {
                        button.isHovered = false;
                        button.color = defaultColor;
                        panel.setCursor(Cursor.getDefaultCursor());
                        repaintNeeded = true;
                    }
                }

                if (repaintNeeded) {
                    panel.repaint();
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point clickPoint = e.getPoint();
                for (MenuButton button : buttons) {
                    if (button.bounds.contains(clickPoint)) {
                        button.action.run();
                        clearButtons();
                        break;
                    }
                }
            }
        });
    }

    public static void drawButtons(Graphics2D g2d, double scaleByScreenSize) {
        for (MenuButton button : buttons) {
            int buttonBorderSize = (int) (69 * scaleByScreenSize);

            g2d.setColor(button.color);
            g2d.fillRoundRect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height,
                buttonBorderSize, buttonBorderSize);

            // TEXT SHADOW
            g2d.setFont(ubuntuFont44);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(button.text);
            int textHeight = fm.getAscent();
            int textX = button.bounds.x + (button.bounds.width - textWidth) / 2;
            int textY = button.bounds.y + (button.bounds.height + textHeight) / 2 - fm.getDescent() + (int) (2 * scale);
            g2d.setColor(Color.black);
            g2d.drawString(button.text, (int) (textX - textShadow), textY); // Left
            g2d.drawString(button.text, (int) (textX + textShadow), textY); // Right
            g2d.drawString(button.text, textX, (int) (textY - textShadow)); // Up
            g2d.drawString(button.text, textX, (int) (textY + textShadow)); // Down

            // ACTUAL TEXT
            g2d.setColor(new Color(225, 225, 225));
            g2d.drawString(button.text, textX, textY);
        }
    }


    public static void clearButtons() {
        buttons.clear();
    }
}

