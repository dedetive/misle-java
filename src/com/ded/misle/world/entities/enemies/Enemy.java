package com.ded.misle.world.entities.enemies;

import com.ded.misle.world.data.CoinDropRange;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.ai.AIBehavior;

import java.awt.*;

public class Enemy extends Entity {

    private final EnemyType type;
    private final double magnification;

    private double xpDrop;
    private CoinDropRange coinDrop;

    private AIBehavior[] behaviors;

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

    public void setCoinDropRange(int min, int max) {
        this.coinDrop = new CoinDropRange(min, max);
    }

    public void setCoinDropRange(CoinDropRange coinDropRange) {
        this.coinDrop = coinDropRange;
    }

    public void setCoinDrop(int coinDrop) {
        this.coinDrop = new CoinDropRange(coinDrop);
    }

    public int getCoinDrop() {
        return coinDrop.roll();
    }

    public void setBehaviors(AIBehavior... behaviors) {
        this.behaviors = behaviors;
    }

    public AIBehavior[] getBehaviors() {
        return behaviors;
    }
}
