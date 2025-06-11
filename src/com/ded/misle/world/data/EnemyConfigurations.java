package com.ded.misle.world.data;

import com.ded.misle.items.DropTable;
import com.ded.misle.world.entities.ai.behaviors.PatrolBehavior;
import com.ded.misle.world.entities.enemies.EnemyConfigurator;
import com.ded.misle.world.entities.enemies.EnemyType;

import java.awt.*;

/**
 * Centralized definitions of enemy setup configurations.
 * Each constant provides a lambda that customizes stats, visuals,
 * behavior, and loot for a particular type of enemy.
 *
 * These are used by {@link EnemyType}.
 */
public enum EnemyConfigurations {
    RED_BLOCK(enemy -> {
        enemy.setMaxHP(50);
        enemy.fillHP();
        enemy.setDamage(5, 1);
        enemy.setTexture("solid");
        enemy.setColor(new Color(0xA02020));
        enemy.setDropTable(DropTable.POTION_CHEST);
        enemy.setXpDrop(50);
        enemy.setCoinDropRange(0, 100);
        enemy.setCollision(true);
    }),

    GOBLIN(enemy -> {
        enemy.setMaxHP(20);
        enemy.fillHP();
        enemy.setDamage(3, 2);
        enemy.setTexture("../characters/enemy/goblin");
        enemy.setDropTable(DropTable.GOBLIN);
        enemy.setXpDrop(1);
        enemy.setCoinDrop(3);
        enemy.setCollision(true);

        Point[] pathPoints = new Point[] {
            new Point(-1, 0),
            new Point(0, 0),
            new Point(1, 0),
            new Point(0, 0),
            new Point(0, 1),
            new Point(0, 0),
            new Point(0, -1),
        };

        enemy.setBehaviors(
            new PatrolBehavior(pathPoints));
    });

    /**
     * The functional configurator for this enemy setup.
     */
    public final EnemyConfigurator c;

    EnemyConfigurations(EnemyConfigurator configurator) {
        this.c = configurator;
    }
}
