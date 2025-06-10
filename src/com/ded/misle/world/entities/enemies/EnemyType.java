package com.ded.misle.world.entities.enemies;

import com.ded.misle.items.DropTable;
import com.ded.misle.world.entities.ai.behaviors.PatrolBehavior;
import com.ded.misle.world.logic.effects.Damage;

import java.awt.*;

/**
 * Enumeration of predefined enemy types.
 * Each constant defines the setup logic for a specific type of enemy,
 * including health, texture, behavior, and drop information.
 */
public enum EnemyType {
    RED_BLOCK(enemy -> {
        double mag = enemy.getMagnification();

        enemy.setMaxHP(50);
        enemy.setHP(50);
        enemy.effect = new Damage(5 * mag, 1).setTriggersOnContact(false);
        enemy.setTexture("solid");
        enemy.setColor(new Color(0xA02020));
        enemy.setDropTable(DropTable.POTION_CHEST);
        enemy.setXpDrop(50);
        enemy.setCoinDropRange(0, 100);
        enemy.setCollision(true);
    }),

    GOBLIN(enemy -> {
        double mag = enemy.getMagnification();

        enemy.setMaxHP(20 * mag);
        enemy.setHP(20 * mag);
        enemy.effect = new Damage(3 * mag, 2).setTriggersOnContact(false);
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
     * Functional interface that defines how to configure an enemy of this type.
     */
    private final EnemyConfigurator configurator;

    /**
     * Constructs a new EnemyType with the given configuration logic.
     *
     * @param configurator a lambda that sets up an {@link Enemy} instance
     */
    EnemyType(EnemyConfigurator configurator) {
        this.configurator = configurator;
    }

    /**
     * Applies this enemy type’s configuration to the given {@link Enemy} instance.
     *
     * @param enemy the enemy to configure
     */
    public void applyTo(Enemy enemy) {
        configurator.configure(enemy);
    }
}
