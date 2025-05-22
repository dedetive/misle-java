package com.ded.misle.world.logic.attacks;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.data.TilePattern;
import com.ded.misle.world.entities.HPBox;
import com.ded.misle.world.entities.player.Player;
import com.ded.misle.world.entities.player.PlayerStats;

import java.awt.*;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.data.TilePattern.MirrorDirection.HORIZONTAL;

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
            case LEFT -> range.mirror(HORIZONTAL);
            default -> {}
        }

        range.offset(origin);

        for (Point point : range.getPoints()) {
            boolean isInvalid = point.getX() < 0 || point.getY() < 0;
            if (isInvalid) continue;
            for (Box box : player.pos.world.grid[point.x][point.y]) {
                boolean isTargetHPBox = box instanceof HPBox;
                boolean isPlayer = box instanceof Player;
                boolean canDamageThisBox = !isPlayer || this.damagesPlayer;

                if (isTargetHPBox && canDamageThisBox) {

                    ((HPBox) box).takeDamage(damage, HPBox.DamageFlag.of(HPBox.DamageFlag.NORMAL), box.getKnockbackDirection());

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

    public WeaponAttacker setDamagesPlayer(boolean damagesPlayer) {
        this.damagesPlayer = damagesPlayer;
        return this;
    }
}
