package com.ded.misle.world.entities.enemies;

import java.util.ArrayList;
import java.util.List;

public class EnemyRegistry {
    private static final List<Enemy> enemies = new ArrayList<>();

    public static void register(Enemy enemy) {
        enemies.add(enemy);
    }

    public static void unregister(Enemy enemy) {
        enemies.remove(enemy);
    }

    public static List<Enemy> all() {
        return enemies;
    }

    public static void clear() {
        enemies.clear();
    }
}
