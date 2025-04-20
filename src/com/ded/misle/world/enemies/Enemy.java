package com.ded.misle.world.enemies;

import com.ded.misle.world.boxes.Effect;
import com.ded.misle.world.boxes.HPBox;
import com.ded.misle.world.chests.DropTable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;
import static com.ded.misle.world.boxes.BoxHandling.editBox;
import static com.ded.misle.world.enemies.EnemyAI.AIState.STILL;
import static com.ded.misle.world.enemies.EnemyAI.AIState.WANDERING;

public class Enemy extends HPBox {

    private final EnemyType enemyType;
    private final double magnification;

    private static List<Enemy> enemyBoxes = new ArrayList<>();

    private double xpDrop = 0;
    private int[] coinDrop = new int[]{0, 0};
    public EnemyAI.AIState AIState;
    public long lastMoved = 0;
    public int moveInterval;

    // INITIALIZATION

    public Enemy(int x, int y, EnemyType enemyType, double magnification) {
        super(x, y);
        this.magnification = magnification;

        this.enemyType = enemyType;
        this.loadEnemy();

        enemyBoxes.add(this);
    }

    // ENEMY LOADER

    public enum EnemyType {
        RED_BLOCK,
        GOBLIN
    }

    public void loadEnemy() {
        double maxHP = 1;
        double damage = 1;
        double damageRate = 1;  // In ms
        boolean defaultDamageType = true;

        switch (enemyType) {
            case RED_BLOCK -> {
                // Attributes
                maxHP = 50;
                damage = 5;
                damageRate = 300;

                // Structural
                this.setTexture("solid");
                this.setColor(new Color(0xA02020));
                this.setDropTable(DropTable.POTION_CHEST);
                this.AIState = STILL;
                this.moveInterval = 0;

                // Drops
                this.xpDrop = 50;
                this.coinDrop = new int[]{0, 20};
            }
            case GOBLIN -> {
                // Attributes
                maxHP = 20;
                damage = 3;
                damageRate = 400;

                // Structural
                this.setHasCollision(false);
//                this.setTexture("solid");
//                this.setColor(new Color(0x106000));
                this.setTexture("../characters/enemy/goblin");
                this.setBoxScaleHorizontal(0.75);
                this.setBoxScaleVertical(0.75);
                this.AIState = WANDERING;

                // Drops
                this.setDropTable(DropTable.GOBLIN);
                this.xpDrop = 1;
                this.coinDrop = new int[]{1, 3};
                this.moveInterval = ThreadLocalRandom.current().nextInt(500, 2500 + 1);

                // Breadcrumbs
                this.maxPersonalBreadcrumbs = (int) (Math.random() * (5 - 2) + 2);
                this.personalBreadcrumbUpdateInterval = (long) (Math.random() * (1400 - 200) + 200);
                updatePersonalBreadcrumbs();
            }
            default -> {
                this.AIState = STILL;
                this.setTexture("solid");
                this.setColor(defaultBoxColor);
            }
        }

        damage *= magnification;
        maxHP *= magnification;

        if (defaultDamageType) {
            this.effect = new Effect.Damage(damage, damageRate);
        }
        this.setMaxHP(maxHP);
        this.setHP(this.getMaxHP());
    }

    // ENEMY BOXES

    public static List<Enemy> getEnemyBoxes() { return enemyBoxes; }

    public static void clearEnemyBoxes() { enemyBoxes.clear(); }

    public void removeEnemyBox() {
        enemyBoxes.remove(this);
    }

    public EnemyType getEnemyType() { return enemyType; }

    public double getXPDrop() { return xpDrop; }

    public int getCoinDrop() { return ThreadLocalRandom.current().nextInt(coinDrop[0], coinDrop[1] + 1); }

    // BREADCRUMBS

    private final List<int[]> personalBreadcrumbs = new ArrayList<>();
    private long lastPersonalBreadcrumbUpdate = System.currentTimeMillis();
    private int maxPersonalBreadcrumbs;
    private long personalBreadcrumbUpdateInterval;

    public List<int[]> getPersonalBreadcrumbs() {
        return personalBreadcrumbs;
    }

    public void updatePersonalBreadcrumbs() {
        personalBreadcrumbs.add(new int[]{(int) (player.getX() / scale), (int) (player.getY() / scale)});
        lastPersonalBreadcrumbUpdate = System.currentTimeMillis();
        if (personalBreadcrumbs.size() > maxPersonalBreadcrumbs) {
            personalBreadcrumbs.removeFirst();
        }
    }

    public void checkIfBreadcrumbUpdate() {
        if (lastPersonalBreadcrumbUpdate + personalBreadcrumbUpdateInterval < System.currentTimeMillis()) {
            updatePersonalBreadcrumbs();
        }
    }
}
