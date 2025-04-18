package com.ded.misle.world.enemies;

import com.ded.misle.world.player.PlayerAttributes;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.tileSize;
import static com.ded.misle.core.PhysicsEngine.isSpaceOccupied;
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
        try {
            if (lastBreadcrumbUpdate + 1000 < System.currentTimeMillis()) {
                updateBreadcrumbs();
            }

            for (Enemy enemy : getEnemyBoxes()) {
                    switch (enemy.getEnemyType()) {
                        case GOBLIN -> goblinAI(enemy);
                    }
            }
        } catch (ConcurrentModificationException e) {
                    //
        }
    }

    public static void goblinAI(Enemy enemy) {
        enemy.checkIfBreadcrumbUpdate();

        double playerX;
        double playerY;
        try {
            playerX = enemy.getPersonalBreadcrumbs().getLast()[0];
            playerY = enemy.getPersonalBreadcrumbs().getLast()[1];
        } catch (IndexOutOfBoundsException e) {
            playerX = enemy.getPersonalBreadcrumbs().getFirst()[0];
            playerY = enemy.getPersonalBreadcrumbs().getFirst()[1];
        }
        double enemyX = enemy.getX();
        double enemyY = enemy.getY();
        double distanceX = (playerX - enemyX);
        double distanceY = (playerY - enemyY);
        int maxDistance = 140;
        boolean withinDistance = Math.abs(distanceX) < maxDistance && Math.abs(distanceY) < maxDistance;


        if (!(enemy.isMoving)) {
            switch (enemy.AIState) {
                case STILL -> {
                    if (enemy.lastMoved + enemy.moveInterval < System.currentTimeMillis()) {
                        enemy.AIState = AIState.WANDERING;
                    }
                }
                case WANDERING -> {
                    enemy.lastMoved = System.currentTimeMillis();
                    enemy.AIState = AIState.STILL;
                    if (withinDistance) {
                        enemy.AIState = AIState.PURSUING;
                    } else {
                        enemy.moveInterval = ThreadLocalRandom.current().nextInt(500, 2500 + 1);
                        double rand = (Math.random() * (7 - 1) + 1);
                        double randomPos = ThreadLocalRandom.current().nextInt(-1, 1 + 1);
                        int moveX = (int) (Math.clamp(randomPos, -1, 1) * rand);
                        int moveY = (int) (Math.clamp(randomPos, -1, 1) * rand);

                        if (!isSpaceOccupied(enemy.getX() + moveX, enemy.getY(), enemy)) {
                            moveCollisionBox(enemy, moveX, 0, rand * 4);
                        }
                        if (!isSpaceOccupied(enemy.getX(), enemy.getY() + moveY, enemy)) {
                            moveCollisionBox(enemy, 0, moveY, rand * 4);
                        }
                    }
                }
                case PURSUING -> {
                    if (!withinDistance) {
                        enemy.lastMoved = System.currentTimeMillis();
                        enemy.AIState = AIState.WANDERING;
                        break;
                    }

                    double rand = (Math.random() * (7 - 1) + 1);
                    int moveX = (int) (Math.clamp(distanceX, -1, 1) * rand);
                    int moveY = (int) (Math.clamp(distanceY, -1, 1) * rand);

                    if (!isSpaceOccupied(enemy.getX() + moveX, enemy.getY(), enemy)) {
                        moveCollisionBox(enemy, moveX, 0, rand * 4);
                    }
                    if (!isSpaceOccupied(enemy.getX(), enemy.getY() + moveY, enemy)) {
                        moveCollisionBox(enemy, 0, moveY, rand * 4);
                    }
                }
            }
        }
    }

    public enum AIState {
        STILL,
        WANDERING,
        PURSUING
    }
}
