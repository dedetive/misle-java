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
import static com.ded.misle.world.npcs.NPCDialog.getCurrentTalkingTo;
import static com.ded.misle.world.player.PlayerStats.PlaytimeMode.*;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.FontManager.*;
import static com.ded.misle.renderer.InventoryRenderer.wrapText;

public class DialogRenderer {

    private static long lastTimeMillis;
    private static int currentLetter;
    private final static int CHARACTER_INTERVAL_MS = 10;

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
        g2d.setColor(npc.nameColor);
        g2d.setFont(dialogNPCName);
        String npcName = npc.name;
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
            placeholders.put("npcName", npc.name);

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

        // Display text character by character
        int length = text.length();
        long currentTime = System.currentTimeMillis();
        boolean canIncreaseLetter = currentTime > lastTimeMillis + CHARACTER_INTERVAL_MS;
        if (canIncreaseLetter && currentLetter < length) {
            currentLetter++;
            lastTimeMillis = currentTime;
        }
//        text = text.substring(0, currentLetter);

        String[] wrappedText = wrapText(text, (int) ((512 - 64) * scale), fm);

        int height = (int) (192 * scale);
        int pastLength = 0;
        for (String line : wrappedText) {
            boolean hasColorIndicators = line.length() != removeColorIndicators(line).length();
            int currentLineLetter = currentLetter - pastLength;
            String extra = "";

            if (hasColorIndicators) {
                boolean sectionContainsFormatKey = line.substring(currentLineLetter, Math.min(currentLineLetter + 1, line.length())).contains("{");

                if (sectionContainsFormatKey) {
                    String subLineExtra = line.substring(currentLineLetter, line.indexOf('}', currentLineLetter));
                    String subLineText = subLineExtra.replaceAll(".*?,\\s*(.*?)", "$1");

                    currentLetter += Math.max(subLineExtra.length() - subLineText.length(), 0);
                    currentLineLetter = currentLetter - pastLength;
                }

                if (currentLineLetter - 1 < line.lastIndexOf('}') &&
                    line.indexOf('}', currentLineLetter) <= currentLineLetter + 2) {
                    extra = "}";
                }

            }

            line = line.substring(0, currentLineLetter).concat(extra);

            drawColoredText(g2d, line, (int) (40 * scale), height, dialogNPCText, dialogTextColor, false);
            height += fm.getHeight();
            pastLength += line.length();
        }
    }

    public static void resetLetterDisplay() {
        currentLetter = 0;
        lastTimeMillis = 0;
    }
}