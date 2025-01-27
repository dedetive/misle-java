package com.ded.misle.world.enemies;

import com.ded.misle.world.player.PlayerAttributes;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.player;
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
        double playerX = player.getX() / scale;
        double playerY = player.getY() / scale;
        double enemyX = enemy.getX();
        double enemyY = enemy.getY();
        double distanceX = (playerX - enemyX);
        double distanceY = (playerY - enemyY);
        double rand = (Math.random() * (10 - 1) + 1);
        double moveX = Math.clamp(distanceX, -1, 1) * rand;
        double moveY = Math.clamp(distanceY, -1, 1) * rand;
        if (!isPixelOccupied(enemy, enemy.getX() + moveX, enemy.getY() + moveY,
            tileSize, 7, PlayerAttributes.KnockbackDirection.NONE, Enemy.EnemyType.GOBLIN)) {
            if (!(enemy.isMoving) &&
                Math.abs(distanceX) < 140 &&
                Math.abs(distanceY) < 140) {
                System.out.println("distanceX: " + distanceX + " distanceY: " + distanceY);
                moveCollisionBox(enemy, moveX, moveY, rand * 4);
                isPixelOccupied(player, tileSize, 8, PlayerAttributes.KnockbackDirection.NONE);
            }
        }
    }
}
