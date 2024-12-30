package com.ded.misle.renderer;

import com.ded.misle.LanguageManager;
import com.ded.misle.npcs.NPC;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.npcs.NPCDialog.getCurrentTalkingTo;
import static com.ded.misle.player.PlayerStats.PlaytimeMode.*;
import static com.ded.misle.renderer.ColorManager.dialogTextColor;
import static com.ded.misle.renderer.ColorManager.dialogWindowBackground;
import static com.ded.misle.renderer.FontManager.*;
import static com.ded.misle.renderer.InventoryRenderer.wrapText;

public class DialogRenderer {

    public static void renderDialog(Graphics2D g2d) {
        // Background
        g2d.setColor(dialogWindowBackground);
        g2d.fillRect((int) (24 * scale), (int) (148 * scale), (int) ((512 - 48) * scale), (int) (114 * scale));

        // NPC name
        NPC npc = getCurrentTalkingTo();
        g2d.setColor(npc.getNameColor());
        g2d.setFont(dialogNPCName);
        String npcName = npc.getName();
        g2d.drawString(npcName, (int) (32 * scale), (int) (174 * scale));

        // Dialog itself
        String text = LanguageManager.getText("DIALOG_" + npc.getDialogID() + "." + npc.getDialogIndex());

        if (text.contains("f{")) {
            // Placeholder to method mapping
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("totalPlaytimeHours", Long.toString(player.stats.getCurrentTotalPlaytime(HOURS)));
            placeholders.put("totalPlaytimeMinutes", Long.toString(player.stats.getCurrentTotalPlaytime(MINUTES)));
            placeholders.put("totalPlaytimeSeconds", Long.toString(player.stats.getCurrentTotalPlaytime(SECONDS)));

            // Regex to match placeholders in the format f{...}
            Pattern pattern = Pattern.compile("f\\{(.*?)}");
            Matcher matcher = pattern.matcher(text);

            StringBuilder result = new StringBuilder();
            while (matcher.find()) {
                String placeholder = matcher.group(1); // Extract the placeholder inside f{}
                String replacement = placeholders.getOrDefault(placeholder, "UNKNOWN"); // Get replacement or fallback
                matcher.appendReplacement(result, replacement); // Replace in text
            }
            matcher.appendTail(result);
            text = result.toString();
        }

        FontMetrics fm = g2d.getFontMetrics(dialogNPCText);
        String[] wrappedText = wrapText(text, (int) ((512 - 64) * scale), fm);
        int height = (int) (192 * scale);
        for (String line : wrappedText) {
            drawColoredText(g2d, line, (int) (40 * scale), height, dialogNPCText, dialogTextColor);
            height += fm.getHeight();
        }
    }

    public static void drawColoredText(Graphics2D g2d, String text, int x, int y, Font font, Color baseColor) {
        g2d.setFont(font);
        if (!text.contains("c{")) {
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
}