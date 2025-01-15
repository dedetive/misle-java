package com.ded.misle.renderer;

import com.ded.misle.input.MouseHandler;
import com.ded.misle.world.boxes.BoxHandling;

import javax.swing.*;
import java.awt.*;

import static com.ded.misle.Launcher.antiAliasing;
import static com.ded.misle.core.GamePanel.*;
import static com.ded.misle.renderer.ColorManager.gridColor;

public class LevelDesignerRenderer {
    public static boolean levelDesignerGrid;

    public static void renderLevelDesigner(Graphics g, JPanel panel, MouseHandler mouseHandler) {
        Graphics2D g2d = (Graphics2D) g;
        double scaleByScreenSize = gameScale / 3.75;

        // ANTI-ALIASING
        if (antiAliasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // Draw game components
        BoxHandling.renderBoxes(g2d, player.pos.getCameraOffsetX(), player.pos.getCameraOffsetY(), gameScale, tileSize);

        if (levelDesignerGrid) {
            int timesToRepeatHorizontal = panel.getWidth() / tileSize;
            int timesToRepeatVertical = panel.getHeight() / tileSize;

            g2d.setColor(gridColor);
            for (int i = 1; i < timesToRepeatHorizontal; i++) {
                g2d.drawLine(tileSize * i, 0, tileSize * i, panel.getHeight());
            }
            for (int j = 1; j < timesToRepeatVertical; j++) {
                g2d.drawLine(0, tileSize * j, panel.getWidth(), tileSize * j);
            }
        }

        g2d.dispose();
    }
}
