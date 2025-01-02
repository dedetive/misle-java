package com.ded.misle.boxes;

import static com.ded.misle.boxes.BoxHandling.deleteBox;

public class HPBox extends Box {
    private double HP;
    private double maxHP;

    public void setHP(double HP) {
        this.HP = HP;
        checkIfDead();
    }

    public void damageBox(double damage) {
        this.HP -= Math.max(damage, 0);
        checkIfDead();
    }

    public void setMaxHP(double maxHP) {
        this.maxHP = maxHP;
    }

    public double getHP() {
        return HP;
    }

    public double getMaxHP() {
        return maxHP;
    }

    public boolean checkIfDead() {
        if (this.HP == 0) {
            deleteBox(this);
            return true;
        }
        return false;
    }
}
