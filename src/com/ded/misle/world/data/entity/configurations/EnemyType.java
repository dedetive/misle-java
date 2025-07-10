package com.ded.misle.world.data.entity.configurations;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.data.entity.GenericType;
import com.ded.misle.world.entities.enemies.Enemy;

/**
 * Enumeration of predefined enemy types.
 * Each constant refers to a predefined enemy configuration
 * declared in {@link EnemyConfigurations}, allowing centralized
 * management and easier code navigation.
 */
public enum EnemyType implements GenericType {
	RED_BLOCK(EnemyConfigurations.RED_BLOCK),
	GOBLIN(EnemyConfigurations.GOBLIN)

	;

	/**
	 * Reference to a predefined configuration for an enemy.
	 */
	private final EnemyConfigurations configuration;

	/**
	 * Constructs a new EnemyType based on the given configuration enum.
	 *
	 * @param configuration a constant from {@link EnemyConfigurations}
	 *                      that holds the setup logic for this enemy
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

	@Override
	public void applyTo(Box box) {
		if (box instanceof Enemy) applyTo((Enemy) box);
	}
}
