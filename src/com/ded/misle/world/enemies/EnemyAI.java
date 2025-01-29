package com.ded.misle.world.enemies;

import com.ded.misle.world.player.PlayerAttributes;

import java.util.ArrayList;
import java.util.List;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.tileSize;
import static com.ded.misle.core.PhysicsEngine.isPixelOccupied;
import static com.ded.misle.world.boxes.BoxManipulation.moveCollisionBox;
import static com.ded.misle.world.enemies.Enemy.getEnemyBoxes;

public class EnemyAI  {
    // First breadcrumb = oldest breadcrumb
    // Last breadcrumb = newest breadcrumb
    private static final List<int[]> breadcrumbs = new ArrayList<>();
    private static long lastBreadcrumbUpdate = System.currentTimeMillis();
    private static final int maxBreadcrumbs = 5;

    public static void updateBreadcrumbs() {
        breadcrumbs.add(new int[]{(int) (player.getX() / scale), (int) (player.getY() / scale)});
        lastBreadcrumbUpdate = System.currentTimeMillis();
        if (breadcrumbs.size() > maxBreadcrumbs) {
            breadcrumbs.removeFirst();
        }
//        System.out.println("=============");
//        System.out.println("Breadcrumbs: " + breadcrumbs.size());
//        for (int[] b : breadcrumbs) {
//            System.out.println("X: " + b[0] + ", Y: " + b[1]);
//        }
//        System.out.println("=============");
    }

    public static void clearBreadcrumbs() {
        breadcrumbs.clear();
    }

    public static void updateEnemyAI() {
        if (lastBreadcrumbUpdate + 1000 < System.currentTimeMillis()) {
            updateBreadcrumbs();
        }

        for (Enemy enemy : getEnemyBoxes()) {
            switch (enemy.getEnemyType()) {
                case GOBLIN -> goblinAI(enemy);
            }
        }
    }

    public static void goblinAI(Enemy enemy) {
        enemy.checkIfBreadcrumbUpdate();

        double playerX;
        double playerY;
        try {
            playerX = enemy.getPersonalBreadcrumbs().get(2)[0];
            playerY = enemy.getPersonalBreadcrumbs().get(2)[1];
        } catch (IndexOutOfBoundsException e) {
            playerX = enemy.getPersonalBreadcrumbs().getFirst()[0];
            playerY = enemy.getPersonalBreadcrumbs().getFirst()[1];
        }
        double enemyX = enemy.getX();
        double enemyY = enemy.getY();
        double distanceX = (playerX - enemyX);
        double distanceY = (playerY - enemyY);
        double rand = (Math.random() * (7 - 1) + 1);
        double moveX = Math.clamp(distanceX, -1, 1) * rand;
        double moveY = Math.clamp(distanceY, -1, 1) * rand;
        if (!isPixelOccupied(enemy, enemy.getX() + moveX, enemy.getY() + moveY,
            tileSize, 7, PlayerAttributes.KnockbackDirection.NONE, Enemy.EnemyType.GOBLIN)) {
            if (!(enemy.isMoving) &&
                Math.abs(distanceX) < 140 &&
                Math.abs(distanceY) < 140) {
                moveCollisionBox(enemy, moveX, moveY, rand * 4);
                isPixelOccupied(player, tileSize, 8, PlayerAttributes.KnockbackDirection.NONE);
            }
        }
    }
}
