package com.ded.misle.world.logic;

import com.ded.misle.world.entities.Entity;

import java.util.ArrayList;
import java.util.List;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.entities.Entity.getEntities;

public abstract class TurnManager {

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

        List<Entity<?>> entitiesCopy = new ArrayList<>(getEntities());
        for (Entity<?> e : entitiesCopy) {
            e.updateRegenerationHP();
            e.getController().run();
        }
    }
}
