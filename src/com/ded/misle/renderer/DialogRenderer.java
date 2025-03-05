package com.ded.misle.renderer;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.world.npcs.NPC;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.renderer.ImageRenderer.cachedImages;
import static com.ded.misle.world.npcs.NPCDialog.getCurrentTalkingTo;
import static com.ded.misle.world.player.PlayerStats.PlaytimeMode.*;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.*;
import static com.ded.misle.renderer.InventoryRenderer.wrapText;

public class DialogRenderer {

    public static void renderDialog(Graphics2D g2d) {
        // Background
        int dialogX = (int) (24 * scale);
        int dialogY = (int) (148 * scale);
        int dialogWidth = (int) ((512 - 48) * scale);
        int dialogHeight = (int) (114 * scale);
        g2d.setColor(dialogWindowBackground);
        g2d.fillRect(dialogX, dialogY, dialogWidth, dialogHeight);

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
            placeholders.put("totalPlaytimeMinutes", Long.toString(player.stats.getCurrentTotalPlaytime(MINUTES) % 60));
            placeholders.put("totalPlaytimeSeconds", Long.toString(player.stats.getCurrentTotalPlaytime(SECONDS) % 60));
            placeholders.put("name", player.name);
            placeholders.put("npcName", npc.getName());

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
            drawColoredText(g2d, line, (int) (40 * scale), height, dialogNPCText, dialogTextColor, false);
            height += fm.getHeight();
        }
    }
}