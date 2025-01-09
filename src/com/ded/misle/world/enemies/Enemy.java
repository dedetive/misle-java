package com.ded.misle.world.enemies;

import com.ded.misle.world.boxes.HPBox;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.ded.misle.core.PhysicsEngine.ObjectType.HP_BOX;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;
import static com.ded.misle.world.boxes.BoxHandling.EditBoxKeys.EFFECT;
import static com.ded.misle.world.boxes.BoxHandling.addBoxToCache;
import static com.ded.misle.world.boxes.BoxHandling.editBox;
import static com.ded.misle.world.player.PlayerAttributes.KnockbackDirection.NONE;

public class Enemy extends HPBox {

    private final EnemyType enemyType;
    public enum EnemyType {
        RED_BLOCK,
        GOBLIN
    }

    private static List<Enemy> enemyBoxes = new ArrayList<>();

    public Enemy(double x, double y, EnemyType enemyType) {
        this.setBoxScaleHorizontal(1);
        this.setBoxScaleVertical(1);
        this.setEffect(new String[]{""});
        this.setRotation(0);
        this.setInteractsWithPlayer(true);

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
            }
            case GOBLIN -> {
                maxHP = 20;
                damage = 2;
                damageRate = 400;

                this.setTexture("solid");
                this.setColor(new Color(0x106000));
                this.setBoxScaleHorizontal(0.75);
                this.setBoxScaleVertical(0.75);
            }
            default -> {
                this.setTexture("solid");
                this.setColor(defaultBoxColor);
            }
        }

        if (defaultDamageType) {
            editBox(this, EFFECT, "{damage, " + damage + ", " + damageRate + ", normal, 0}");
        }
        this.setMaxHP(maxHP);
        this.setHP(this.getMaxHP());
    }

    public static List<Enemy> getEnemyBoxes() { return enemyBoxes; }

    public static void clearEnemyBoxes() { enemyBoxes.clear(); }

    public EnemyType getEnemyType() { return enemyType; }
}
