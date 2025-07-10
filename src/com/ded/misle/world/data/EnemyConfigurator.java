package com.ded.misle.world.data;

import com.ded.misle.world.entities.enemies.Enemy;

public interface EnemyConfigurator extends GenericConfigurator<Enemy> {
    void configure(Enemy enemy);
}