package com.ded.misle.world.entities.config.api;

import com.ded.misle.world.entities.enemies.Enemy;

public interface EnemyConfigurator extends GenericConfigurator<Enemy> {
    void configure(Enemy enemy);
}