package com.ded.misle.world.entities.npcs;

import com.ded.misle.game.GamePanel;

import static com.ded.misle.game.GamePanel.gameState;
import static com.ded.misle.renderer.DialogRenderer.resetLetterDisplay;
import static com.ded.misle.world.entities.npcs.NPC.getDialogNPCs;

public class NPCDialog {
    private static NPC currentTalkingTo;

    public static void startDialog(NPC npc) {
        assert getDialogNPCs().contains(npc);
        if (npc.getDialogID() != 0) {
            currentTalkingTo = npc;
            gameState = GamePanel.GameState.DIALOG;
            resetLetterDisplay();
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
