package com.ded.misle.world.entities.enemies;

import com.ded.misle.items.DropTable;
import com.ded.misle.world.data.EnemyConfigurations;
import com.ded.misle.world.entities.ai.behaviors.PatrolBehavior;

import java.awt.*;

/**
 * Enumeration of predefined enemy types.
 * Each constant defines the setup logic for a specific type of enemy,
 * including health, texture, behavior, and drop information.
 */
public enum EnemyType {
    RED_BLOCK(EnemyConfigurations.RED_BLOCK),
    GOBLIN(EnemyConfigurations.GOBLIN)

    ;

    /**
     * Functional interface that defines how to configure an enemy of this type.
     */
    private final EnemyConfigurations configuration;

    /**
     * Constructs a new EnemyType with the given configuration logic.
     *
     * @param configurator a lambda that sets up an {@link Enemy} instance
     */
    EnemyType(EnemyConfigurations configuration) {
        this.configuration = configuration;
    }

    /**
     * Applies this enemy type’s configuration to the given {@link Enemy} instance.
     *
     * @param enemy the enemy to configure
     */
    public void applyTo(Enemy enemy) {
        configuration.c.configure(enemy);
    }
}
