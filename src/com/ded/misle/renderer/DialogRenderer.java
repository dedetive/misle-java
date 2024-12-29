package com.ded.misle.renderer;

import com.ded.misle.LanguageManager;
import com.ded.misle.npcs.NPC;

import java.awt.*;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.npcs.NPCDialog.getCurrentTalkingTo;
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
        g2d.setColor(dialogTextColor);
        g2d.setFont(dialogNPCText);
        FontMetrics fm = g2d.getFontMetrics();
        String[] wrappedText = wrapText(text, (int) ((512 - 64) * scale), fm);
        int height = (int) (192 * scale);
        for (String line : wrappedText) {
            g2d.drawString(line, (int) (40 * scale), height);
            height += fm.getHeight();
        }
    }
}
