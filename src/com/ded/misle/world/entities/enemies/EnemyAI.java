package com.ded.misle.world.entities.enemies;

import static com.ded.misle.world.entities.enemies.Enemy.getEnemyBoxes;

public class EnemyAI {
    public static void updateEnemyAI() {
        for (Enemy enemy : getEnemyBoxes()) {
            switch (enemy.getEnemyType()) {
                case GOBLIN -> goblinAI(enemy);
            }
        }
    }

    private static void goblinAI(Enemy enemy) {



        switch (enemy.AIState) {

        }
    }

    public enum AIState {
        STILL,
        WANDERING,
        PURSUING
    }
}