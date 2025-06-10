package com.ded.misle.world.logic;

import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.enemies.Enemy;
import com.ded.misle.world.entities.enemies.EnemyRegistry;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.entities.Entity.getEntities;

public abstract class LogicManager {

    private static boolean pendingTurn = false;

    public static void requestNewTurn() {
        pendingTurn = true;
    }

    public static void updateIfNeeded() {
        if (pendingTurn && !player.isWaiting()) {
            updateTurn();
            pendingTurn = false;

            TurnTimer.increaseTurn();
        }
    }

    private static void updateTurn() {
        player.attr.checkIfLevelUp();

        for (Entity box : getEntities()) {
            box.updateRegenerationHP();
        }

        for (Enemy enemy : EnemyRegistry.all()) {
            enemy.getController().run();
        }
    }
}
