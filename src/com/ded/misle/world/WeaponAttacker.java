package com.ded.misle.world;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.HPBox;
import com.ded.misle.world.player.Player;

import java.awt.*;

import static com.ded.misle.core.GamePanel.player;

public class WeaponAttacker {
    private double damage;
    private boolean damagesPlayer;
    // TODO: change range to custom grid pattern instead of int, currently range is unused as its behavior will change
    private int range;

    private Point[] rangePositions;

    public WeaponAttacker(double damage, int range) {
        if (range < 0) System.err.println("WeaponAttacker range must be a positive value!");
        this.damage = damage;
        this.range = range;
        this.damagesPlayer = false;
    }

    public WeaponAttacker(double damage) {
        this(damage, 1);
    }

    public WeaponAttacker() {
        this(0, 1);
    }

    public void attack(int originX, int originY) {
        Point origin = new Point(originX, originY);
        // TODO: use grid range to manipulate these values

        for (Box box : player.pos.world.grid[origin.x][origin.y]) {
            boolean isTargetHPBox = box instanceof HPBox;
            boolean isPlayer = box instanceof Player;
            boolean canDamageThisBox = !isPlayer || this.damagesPlayer;

            if (isTargetHPBox && canDamageThisBox) {

                ((HPBox) box).takeDamage(damage, HPBox.DamageFlag.of(HPBox.DamageFlag.NORMAL), box.getKnockbackDirection());

            }
        }
    }

    public WeaponAttacker setDamage(double damage) {
        this.damage = damage;
        return this;
    }

    public WeaponAttacker setRange(int range) {
        this.range = range;
        return this;
    }

    public WeaponAttacker setDamagesPlayer(boolean damagesPlayer) {
        this.damagesPlayer = damagesPlayer;
        return this;
    }
}
