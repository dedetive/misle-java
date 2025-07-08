package com.ded.misle.world.data;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.logic.effects.Chest;
import com.ded.misle.world.logic.effects.Spawnpoint;

import java.util.ArrayList;
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
    GRASS_DARK(box -> {
        box.setCollision(false);
        box.setTexture("grass_dark");
    }),
    GRASS_LIGHT(box -> {
        box.setCollision(false);
        box.setTexture("grass_light");
    }),
    STONE_BRICK_WALL(box -> {
        box.setCollision(true);
        box.setTexture("stone_brick_wall");
    }),
    WOODEN_FLOOR(box -> {
        box.setCollision(false);
        box.setTexture("wooden_floor");
    }),
    CHEST(box -> {
        box.effect = new Chest(0, null);
        box.setCollision(true);
        box.setTexture("chest");
    }),
    SPAWNPOINT(box -> {
        box.effect = new Spawnpoint(-1);
        box.setTexture("spawnpoint");
    }),
    TRAVEL(box -> {
        box.setCollision(true);
        box.setTexture("invisible");
    })

    ;
    //endregion

    private final Consumer<Box> loadFunc;

    BoxPreset(Consumer<Box> loadFunc) {
        this.loadFunc = loadFunc;
    }

    /**
     * Applies this preset's configuration to the given box.
     *
     * @param box the box to configure
     */
    public void load(Box box) {
        this.loadFunc.accept(box);
    }

    private static final Set<BoxPreset> PRESETS_WITH_SIDES = EnumSet.of(
        STONE_BRICK_WALL,
        WOODEN_FLOOR
    );

    private static final Set<BoxPreset> PRESETS_WITH_EXTRA = EnumSet.noneOf(BoxPreset.class);

    /**
     * Checks whether this preset has additional extra properties.
     *
     * @return {@code true} if the preset has extra data; {@code false} otherwise
     */
    public boolean hasExtra() {
        return PRESETS_WITH_EXTRA.contains(this);
    }

    /**
     * Checks whether this preset has "sides" (typically used for connected textures or adjacency).
     *
     * @return {@code true} if the preset uses sides; {@code false} otherwise
     */
    public boolean hasSides() {
        String baseName = this.name();
        if (this.hasExtra() && baseName.contains("_DECO")) {
            baseName = baseName.substring(0, baseName.indexOf("_DECO"));
        }
        try {
            return PRESETS_WITH_SIDES.contains(BoxPreset.valueOf(baseName));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
