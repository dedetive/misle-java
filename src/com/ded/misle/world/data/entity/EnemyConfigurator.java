package com.ded.misle.world.data.entity;

import com.ded.misle.world.entities.enemies.Enemy;

public interface EnemyConfigurator extends GenericConfigurator<Enemy> {
    void configure(Enemy enemy);
}