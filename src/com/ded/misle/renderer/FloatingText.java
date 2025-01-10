package com.ded.misle.renderer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.renderer.ColorManager.floatingTextShadow;

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

            g2d.setFont(FontManager.itemInfoFont);
            g2d.setColor(floatingTextShadow);
            g2d.drawString(floatingText.text, (int) ((floatingText.position.x) * scale + MainRenderer.textShadow), (int) ((floatingText.position.y) * scale + MainRenderer.textShadow));
            g2d.setColor(floatingText.color);
            g2d.drawString(floatingText.text, (int) (floatingText.position.x * scale), (int) (floatingText.position.y * scale));
        }
    }

    public void moveUp() {
        this.position = new Point(this.position.x, this.position.y - 1);
    }
}
