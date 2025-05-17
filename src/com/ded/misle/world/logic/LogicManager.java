package com.ded.misle.world.logic;

import com.ded.misle.core.TurnTimer;
import com.ded.misle.world.entities.HPBox;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.world.entities.HPBox.getHPBoxes;
import static com.ded.misle.world.entities.enemies.EnemyAI.updateEnemyAI;

public abstract class LogicManager {

    private static boolean pendingTurn = false;

    public static void requestNewTurn() {
        pendingTurn = true;
    }

    public static void updateIfNeeded() {
        if (pendingTurn) {
            updateTurn();
            pendingTurn = false;

            TurnTimer.increaseTurn();
        }
    }

    private static void updateTurn() {
        player.attr.checkIfLevelUp();

        updateEnemyAI();

        for (HPBox box : getHPBoxes()) {
            box.updateRegenerationHP();
        }
    }
}
