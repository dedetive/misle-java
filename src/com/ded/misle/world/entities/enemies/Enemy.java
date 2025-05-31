package com.ded.misle.world.entities.enemies;

import com.ded.misle.world.entities.Entity;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Enemy extends Entity {

    private final EnemyType type;
    private final double magnification;

    private double xpDrop;
    private int[] coinDrop;

    public Enemy(Point pos, EnemyType type, double magnification) {
        super(pos.x, pos.y);
        this.magnification = magnification;

        this.type = type;
        this.load();

        EnemyRegistry.register(this);
    }

    private void load() {
        type.applyTo(this);
    }

    public void kill() {
        EnemyRegistry.unregister(this);
    }

    // GETTERS & SETTERS

    public double getMagnification() {
        return this.magnification;
    }

    public EnemyType getEnemyType() {
        return type;
    }

    public double getXPDrop() {
        return xpDrop;
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

    public int getCoinDrop() {
        return ThreadLocalRandom.current().nextInt(coinDrop[0], coinDrop[1] + 1);
    }
}
