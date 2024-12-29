package com.ded.misle.npcs;

import com.ded.misle.GamePanel;

import static com.ded.misle.GamePanel.gameState;
import static com.ded.misle.npcs.NPC.getDialogNPCs;

public class NPCDialog {
    public static void startDialog(NPC npc) {
        assert getDialogNPCs().contains(npc);
        if (npc.getDialogID() != 0) {
            gameState = GamePanel.GameState.DIALOG;

            System.out.println(npc.getDialogID());
        }
    }
}
