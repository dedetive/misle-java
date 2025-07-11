package com.ded.misle.world.data;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.logic.effects.Chest;
import com.ded.misle.world.logic.effects.Spawnpoint;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents predefined configurations for {@link Box} objects in the game world.
 * <p>
 * Each {@code BoxPreset} defines collision, texture, and optional effects to be applied
 * when loaded into a {@link Box}. This provides a simple and unified way to initialize
 * boxes based on common patterns or styles.
 */
public enum BoxPreset {

    //region Preset Entries

    /**
     * A dark grass tile, walkable and without collision.
     */
    GRASS_DARK(box -> {
        box.setCollision(false);
        box.setTexture("grass_dark");
    }),

    /**
     * A light grass tile, walkable and without collision.
     */
    GRASS_LIGHT(box -> {
        box.setCollision(false);
        box.setTexture("grass_light");
    }),

    /**
     * A stone brick wall tile, solid with collision.
     */
    STONE_BRICK_WALL(box -> {
        box.setCollision(true);
        box.setTexture("stone_brick_wall");
    }),

    /**
     * A cracked stone brick wall tile, solid with collision.
     * <p>
     * Commonly used for breakable walls.
     * <p>
     * This preset is expected to be an {@link Entity}.
     * <p>
     * When applied, it configures the box as:
     * <ul>
     *   <li>1 maximum HP</li>
     *   <li>Starts fully healed</li>
     *   <li>HP bar hidden by default</li>
     * </ul>
     * If the box is not an {@link Entity}, a warning will be logged.
     */
    CRACKED_STONE_BRICK_WALL(box -> {
        try {
            Entity entity = (Entity) box;
            entity.setProportionalMaxHP(1);
            entity.setDisplayHP(false);
            entity.setTextureInEntitiesDirectory(false);
        } catch (ClassCastException e) {
            System.err.println("BoxPreset: Cracked stone brick wall was not able to be turned into an entity");
        }
        box.setCollision(true);
        box.setTexture("cracked_stone_brick_wall");
    }),

    /**
     * A wooden floor tile, walkable and without collision.
     */
    WOODEN_FLOOR(box -> {
        box.setCollision(false);
        box.setTexture("wooden_floor");
    }),

    /**
     * A chest tile that can store items, with collision and a chest effect.
     * <p>
     * It is expected for this effect to be modified in a more specific manner for each usage:
     * <pre>
     * {@code
     * Box box = new Box();
     * CHEST.load(box);
     * box.effect = new Chest(openRate, dropTable);
     * }
     * </pre>
     */
    CHEST(box -> {
        box.effect = new Chest(0, null);
        box.setCollision(true);
        box.setTexture("chest");
    }),

    /**
     * A spawnpoint tile used for player spawning, no collision.
     * <p>
     * It is expected for this effect to be modified in a more specific manner for each usage:
     * <pre>
     * {@code
     * Box box = new Box();
     * SPAWNPOINT.load(box);
     * box.effect = new Spawnpoint(id);
     * }
     * </pre>
     */
    SPAWNPOINT(box -> {
        box.effect = new Spawnpoint(-1);
        box.setTexture("spawnpoint");
    }),

    /**
     * A travel tile, used to transition between rooms, invisible but solid.
     * <p>
     * It is expected for this effect to be modified in a more specific manner for each usage:
     * <pre>
     * {@code
     * Box box = new Box();
     * TRAVEL.load(box);
     * box.effect = new Travel(id, coordinates);
     * }
     * </pre>
     */
    TRAVEL(box -> {
        box.setCollision(true);
        box.setTexture("invisible");
    })

    ;

    /**
     * Defines which presets have "sides", typically for connected textures or adjacency-based rendering.
     * <p>
     * This is used by {@link #hasSides()} to determine special rendering behavior.
     */
    private static final Set<BoxPreset> PRESETS_WITH_SIDES = EnumSet.of(
        STONE_BRICK_WALL,
        CRACKED_STONE_BRICK_WALL,
        WOODEN_FLOOR
    );

    private static final List<Set<BoxPreset>> SIDE_GROUPS = List.of(
        EnumSet.of(STONE_BRICK_WALL, CRACKED_STONE_BRICK_WALL),
        EnumSet.of(WOODEN_FLOOR)
    );

    //endregion

    /**
     * The functional logic applied to a {@link Box} when this preset is loaded.
     * <p>
     * This consumer applies collision, texture, and any effect to the given box.
     */
    private final Consumer<Box> loadFunc;

    /**
     * Constructs a {@code BoxPreset} with its associated behavior.
     *
     * @param loadFunc the consumer defining how to configure a box for this preset
     */
    BoxPreset(Consumer<Box> loadFunc) {
        this.loadFunc = loadFunc;
    }

    /**
     * Applies this preset's configuration to the given box.
     * <p>
     * This sets up the box with the preset's collision, texture, and effects.
     *
     * @param box the box to configure
     */
    public void load(Box box) {
        this.loadFunc.accept(box);
    }

    /**
     * Checks whether this preset has "sides" (typically used for connected textures or adjacency).
     *
     * @return {@code true} if the preset uses sides; {@code false} otherwise
     */
    public boolean hasSides() {
        return PRESETS_WITH_SIDES.contains(this) || !getSideGroups(this).isEmpty();
    }

    public static List<Set<BoxPreset>> getSideGroups(BoxPreset preset) {
        List<Set<BoxPreset>> groups = new java.util.ArrayList<>();
        for (Set<BoxPreset> group : SIDE_GROUPS) {
            if (group.contains(preset)) {
                groups.add(group);
            }
        }
        return groups;
    }
}