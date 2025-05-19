package com.ded.misle.renderer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import static com.ded.misle.renderer.ColorManager.drawColoredText;
import static com.ded.misle.renderer.ColorManager.floatingTextShadow;
import static com.ded.misle.renderer.FontManager.itemInfoFont;

public class FloatingText {
    private static final ArrayList<FloatingText> floatingTexts = new ArrayList<>();
    private final String text;
    private Point position;
    private final Color color;
    private final long birthTimestamp;

    // General useful methods
    public void deleteFloatingText() {
        floatingTexts.remove(this);
    }
    public void clearFloatingTexts() {
        floatingTexts.clear();
    }
    public static ArrayList<FloatingText> getFloatingTexts() {
        return floatingTexts;
    }

    // Constructor
    public FloatingText(String text, Color color, int x, int y, boolean movesUp) {
        this.text = text;
        this.position = new Point(x, y);
        this.color = color;
        this.birthTimestamp = System.currentTimeMillis();

        floatingTexts.add(this);

        if (movesUp) {
            Timer moveUpTimer = new Timer(200, e -> this.moveUp());
            moveUpTimer.setRepeats(true);
            moveUpTimer.start();
        }
    }

    public static void drawFloatingTexts(Graphics2D g2d) {
        Iterator<FloatingText> iterator = floatingTexts.iterator();
        while (iterator.hasNext()) {
            FloatingText floatingText = iterator.next();

            if (floatingText.birthTimestamp + 2500 < System.currentTimeMillis()) {
                iterator.remove();
                continue;
            }

            Font font = itemInfoFont;
            int x = floatingText.position.x;
            int y = floatingText.position.y;

            // SHADOW
            drawColoredText(g2d, floatingText.text, (int) (x + MainRenderer.textShadow),
                (int) (y + MainRenderer.textShadow), font, floatingTextShadow, false);

            // REGULAR
            drawColoredText(g2d, floatingText.text, x, y, font, floatingText.color, false);
        }
    }

    public void moveUp() {
        this.position = new Point(this.position.x, this.position.y - 1);
    }
}
