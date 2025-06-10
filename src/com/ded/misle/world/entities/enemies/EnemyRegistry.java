package com.ded.misle.world.entities.enemies;

import java.util.ArrayList;
import java.util.List;

/**
 * A registry for managing all active {@link Enemy} instances in the game.
 * This class provides static methods for adding, removing, listing, and clearing enemies.
 */
public class EnemyRegistry {
    /**
     * The list of all registered enemies currently in the world.
     */
    private static final List<Enemy> enemies = new ArrayList<>();

    /**
     * Registers a new enemy into the global enemy list.
     *
     * @param enemy the {@link Enemy} instance to register
     */
    public static void register(Enemy enemy) {
        enemies.add(enemy);
    }

    /**
     * Unregisters an enemy from the global enemy list.
     *
     * @param enemy the {@link Enemy} instance to remove
     */
    public static void unregister(Enemy enemy) {
        enemies.remove(enemy);
    }

    /**
     * Returns a list of all currently registered enemies.
     *
     * @return a {@link List} of all {@link Enemy} instances
     */
    public static List<Enemy> all() {
        return enemies;
    }

    /**
     * Clears all registered enemies from the registry.
     */
    public static void clear() {
        enemies.clear();
    }
}