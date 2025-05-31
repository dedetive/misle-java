package com.ded.misle.world.logic;

import com.ded.misle.world.entities.Entity;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.entities.Entity.getEntities;
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

        for (Entity box : getEntities()) {
            box.updateRegenerationHP();
        }
    }
}
