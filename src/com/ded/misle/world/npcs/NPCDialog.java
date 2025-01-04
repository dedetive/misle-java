package com.ded.misle.world.npcs;

import com.ded.misle.core.GamePanel;

import static com.ded.misle.core.GamePanel.gameState;
import static com.ded.misle.world.npcs.NPC.getDialogNPCs;

public class NPCDialog {
    private static NPC currentTalkingTo;

    public static void startDialog(NPC npc) {
        assert getDialogNPCs().contains(npc);
        if (npc.getDialogID() != 0) {
            currentTalkingTo = npc;
            gameState = GamePanel.GameState.DIALOG;
        }
    }

    public static void endDialog() {
        gameState = GamePanel.GameState.PLAYING;
        currentTalkingTo = null;
    }

    public static NPC getCurrentTalkingTo() {
        return currentTalkingTo;
    }
}
