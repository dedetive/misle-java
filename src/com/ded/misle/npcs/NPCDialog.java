package com.ded.misle.npcs;

import com.ded.misle.GamePanel;

import static com.ded.misle.GamePanel.gameState;
import static com.ded.misle.npcs.NPC.getDialogNPCs;

public class NPCDialog {
    private static NPC currentTalkingTo;

    public static void startDialog(NPC npc) {
        assert getDialogNPCs().contains(npc);
        if (npc.getDialogID() != 0) {
            gameState = GamePanel.GameState.DIALOG;
            currentTalkingTo = npc;
        }
    }

    public static void endDialog() {
        gameState = GamePanel.GameState.PLAYING;
    }

    public static NPC getCurrentTalkingTo() {
        return currentTalkingTo;
    }
}
