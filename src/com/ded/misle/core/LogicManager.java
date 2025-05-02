package com.ded.misle.core;

import com.ded.misle.world.boxes.HPBox;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.world.boxes.HPBox.getHPBoxes;
import static com.ded.misle.world.enemies.EnemyAI.updateEnemyAI;

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
        long currentTime = System.currentTimeMillis();

        player.attr.checkIfLevelUp();

        updateEnemyAI();

        for (HPBox box : getHPBoxes()) {
            box.updateRegenerationHP(currentTime);
        }
    }
}
