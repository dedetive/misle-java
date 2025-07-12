package com.ded.misle.world.entities.config;

import com.ded.misle.world.entities.enemies.Enemy;

public interface EnemyConfigurator extends GenericConfigurator<Enemy> {
    void configure(Enemy enemy);
}