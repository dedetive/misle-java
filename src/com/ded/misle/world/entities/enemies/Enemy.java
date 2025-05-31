package com.ded.misle.world.entities.enemies;

import com.ded.misle.world.logic.effects.Damage;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.items.DropTable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ded.misle.renderer.ColorManager.defaultBoxColor;

public class Enemy extends Entity {

    private final EnemyType enemyType;
    private final double magnification;

    private static final List<Enemy> enemyBoxes = new ArrayList<>();

    private double xpDrop = 0;
    private int[] coinDrop = new int[]{0, 0};

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
        int damageRate = 1;
        boolean defaultDamageType = true;

        switch (enemyType) {
            case RED_BLOCK -> {
                // Attributes
                maxHP = 50;
                damage = 5;
                damageRate = 1;

                // Structural
                this.setTexture("solid");
                this.setColor(new Color(0xA02020));
                this.setDropTable(DropTable.POTION_CHEST);

                // Drops
                this.xpDrop = 50;
                this.coinDrop = new int[]{0, 20};
            }
            case GOBLIN -> {
                // Attributes
                maxHP = 20;
                damage = 3;
                damageRate = 2;

                // Structural
                this.setCollision(true);
//                this.setTexture("solid");
//                this.setColor(new Color(0x106000));
                this.setTexture("../characters/enemy/goblin");
                this.setVisualScaleHorizontal(0.75);
                this.setVisualScaleVertical(0.75);

                // Drops
                this.setDropTable(DropTable.GOBLIN);
                this.xpDrop = 1;
                this.coinDrop = new int[]{1, 3};

                // Breadcrumbs
            }
            default -> {
                this.setTexture("solid");
                this.setColor(defaultBoxColor);
            }
        }

        damage *= magnification;
        maxHP *= magnification;

        if (defaultDamageType) {
            this.effect = new Damage(damage, damageRate).setTriggersOnContact(false);
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

}
