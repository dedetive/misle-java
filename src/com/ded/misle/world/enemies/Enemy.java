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
    public enum EnemyType {
        RED_BLOCK,
        GOBLIN
    }

    private static List<Enemy> enemyBoxes = new ArrayList<>();

    public static List<Enemy> getEnemyBoxes() {
        return enemyBoxes;
    }

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

        this.loadEnemy(enemyType);


        enemyBoxes.add(this);
        addBoxToCache(this);
    }

    public void loadEnemy(EnemyType enemyType) {
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
                editBox(this, EFFECT, "{damage, 5, 300, normal, 0}");
                this.setDropTableName("mountain");
            }
            case GOBLIN -> {
                maxHP = 20;
                damage = 7;
                damageRate = 400;

                this.setTexture("solid");
                this.setColor(new Color(0x60A020));
                this.setBoxScaleHorizontal(0.75);
                this.setBoxScaleVertical(0.75);
                this.setEffect("{damage, 3, 400, normal, 0}");
                this.setMaxHP(20);
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
}
