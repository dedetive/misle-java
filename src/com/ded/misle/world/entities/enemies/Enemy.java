package com.ded.misle.world.entities.enemies;

import com.ded.misle.world.entities.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Enemy extends Entity {

    private final EnemyType type;
    private final double magnification;

    private static final List<Enemy> enemyBoxes = new ArrayList<>();

    private double xpDrop = 0;
    private int[] coinDrop = new int[]{0, 0};

    // INITIALIZATION

    public Enemy(int x, int y, EnemyType type, double magnification) {
        super(x, y);
        this.magnification = magnification;

        this.type = type;
        this.load();

        enemyBoxes.add(this);
    }

    public void setXpDrop(int xp) {
        this.xpDrop = xp;
    }

    public void setCoinDrop(int min, int max) {
        this.coinDrop = new int[]{min, max};
    }

    public void setCoinDrop(int coinDrop) {
        this.coinDrop = new int[]{coinDrop, coinDrop};
    }

    public double getMagnification() {
        return this.magnification;
    }

    // ENEMY LOADER

    private void load() {
        type.applyTo(this);
    }

    // ENEMY BOXES

    public static List<Enemy> getEnemyBoxes() { return enemyBoxes; }

    public static void clearEnemyBoxes() { enemyBoxes.clear(); }

    public void removeEnemyBox() {
        enemyBoxes.remove(this);
    }

    public EnemyType getEnemyType() { return type; }

    public double getXPDrop() { return xpDrop; }

    public int getCoinDrop() { return ThreadLocalRandom.current().nextInt(coinDrop[0], coinDrop[1] + 1); }
}
