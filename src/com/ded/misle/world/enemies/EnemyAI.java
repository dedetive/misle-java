package com.ded.misle.world.enemies;

import com.ded.misle.world.player.PlayerAttributes;

import static com.ded.misle.core.GamePanel.tileSize;
import static com.ded.misle.core.PhysicsEngine.isPixelOccupied;
import static com.ded.misle.world.boxes.BoxManipulation.moveCollisionBox;
import static com.ded.misle.world.enemies.Enemy.getEnemyBoxes;

public class EnemyAI  {
    public static void updateEnemyAI() {
        for (Enemy enemy : getEnemyBoxes()) {
            switch (enemy.getEnemyType()) {
                case GOBLIN -> goblinAI(enemy);
            }
        }
    }

    public static void goblinAI(Enemy enemy) {
        double moveX = 0;
        double moveY = -30;
        if (!isPixelOccupied(enemy, enemy.getX() + moveX, enemy.getY() + moveY,
            tileSize, 10, PlayerAttributes.KnockbackDirection.NONE)) {
            if (!enemy.isMoving) {
                moveCollisionBox(enemy, moveX, moveY, 700);
            }
        }
    }
}
