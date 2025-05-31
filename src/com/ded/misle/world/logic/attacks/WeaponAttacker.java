package com.ded.misle.world.logic.attacks;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.data.TilePattern;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.player.Player;
import com.ded.misle.world.entities.player.PlayerStats;

import java.awt.*;

import static com.ded.misle.game.GamePanel.player;

public class WeaponAttacker {
    private double damage;
    private boolean damagesPlayer;
    private Range range;

    public WeaponAttacker(double damage, Range range) {
        this.damage = damage;
        this.range = range;
        this.damagesPlayer = false;
    }

    public WeaponAttacker(double damage) {
        this(damage, Range.getDefaultRange());
    }

    public WeaponAttacker() {
        this(0, Range.getDefaultRange());
    }

    public void attack(Point origin, PlayerStats.Direction direction) {
        switch (direction) {
            case UP -> range.rotate(TilePattern.Rotation.DEG_90);
            case DOWN -> range.rotate(TilePattern.Rotation.DEG_270);
            case LEFT -> range.rotate(TilePattern.Rotation.DEG_180);
            default -> {}
        }

        range.offset(origin);

        for (Point point : range.getPoints()) {
            boolean isInvalid =
                (point.getX() < 0 || point.getY() < 0) ||
                (point.getX() > player.pos.world.width - 1 || point.getY() > player.pos.world.height - 1);
            if (isInvalid) continue;
            for (Box box : player.pos.world.grid[point.x][point.y]) {
                boolean isTargetEntity = box instanceof Entity;
                boolean isPlayer = box instanceof Player;
                boolean canDamageThisBox = !isPlayer || this.damagesPlayer;

                if (isTargetEntity && canDamageThisBox) {

                    ((Entity) box).takeDamage(damage, Entity.DamageFlag.of(Entity.DamageFlag.NORMAL), box.getKnockbackDirection());

                }
            }
        }
    }

    public WeaponAttacker setDamage(double damage) {
        this.damage = damage;
        return this;
    }

    public WeaponAttacker setRange(Range range) {
        this.range = range;
        return this;
    }

    public Range getRange() {
        try {
            return range.clone();
        } catch (CloneNotSupportedException e) {
            return range;
        }
    }

    public WeaponAttacker setDamagesPlayer(boolean damagesPlayer) {
        this.damagesPlayer = damagesPlayer;
        return this;
    }
}
