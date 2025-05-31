package com.ded.misle.world.entities.enemies;

@FunctionalInterface
public interface EnemyConfigurator {
    void configure(Enemy enemy);
}