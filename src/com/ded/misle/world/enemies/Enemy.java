package com.ded.misle.world.enemies;

import com.ded.misle.world.boxes.HPBox;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.PhysicsEngine.ObjectType.HP_BOX;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;
import static com.ded.misle.world.boxes.BoxHandling.EditBoxKeys.EFFECT;
import static com.ded.misle.world.boxes.BoxHandling.addBoxToCache;
import static com.ded.misle.world.boxes.BoxHandling.editBox;
import static com.ded.misle.world.player.PlayerAttributes.KnockbackDirection.NONE;

public class Enemy extends HPBox {

    private final EnemyType enemyType;
    private final double magnification;

    private static List<Enemy> enemyBoxes = new ArrayList<>();

    private double xpDrop = 0;
    private int[] coinDrop = new int[]{0, 0};

    // INITIALIZATION

    public Enemy(double x, double y, EnemyType enemyType, double magnification) {
        this.setBoxScaleHorizontal(1);
        this.setBoxScaleVertical(1);
        this.setEffect(new String[]{""});
        this.setRotation(0);
        this.setInteractsWithPlayer(true);
        this.magnification = magnification;

        this.setObjectType(HP_BOX);
        this.setHasCollision(true);
        this.setKnockbackDirection(NONE);
        this.setX(x);
        this.setY(y);

        this.enemyType = enemyType;
        this.loadEnemy();

        enemyBoxes.add(this);
        addBoxToCache(this);
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
                maxHP = 50;
                damage = 5;
                damageRate = 300;

                this.setTexture("solid");
                this.setColor(new Color(0xA02020));
                this.setDropTableName("mountain");
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

                // Drops
                this.setDropTableName("goblin");
                this.xpDrop = 1;
                this.coinDrop = new int[]{1, 3};

                // Breadcrumbs
                this.maxPersonalBreadcrumbs = (int) (Math.random() * (5 - 2) + 2);
                this.personalBreadcrumbUpdateInterval = (long) (Math.random() * (1400 - 200) + 200);
                updatePersonalBreadcrumbs();
            }
            default -> {
                this.setTexture("solid");
                this.setColor(defaultBoxColor);
            }
        }

        damage *= magnification;
        maxHP *= magnification;

        if (defaultDamageType) {
            editBox(this, EFFECT, "{damage, " + damage + ", " + damageRate + ", normal, 0}");
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
