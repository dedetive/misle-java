package com.ded.misle.world.boxes;

import com.ded.misle.world.logic.effects.Chest;
import com.ded.misle.world.logic.effects.Spawnpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public enum BoxPreset {
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
    });

    private static final List<BoxPreset> presetsWithSides = List.of(
        STONE_BRICK_WALL,
        WOODEN_FLOOR
    );

    private static final List<BoxPreset> presetsWithExtra = new ArrayList<>() {{
        // Currently empty
        // Add with:
        // add(PRESET_NAME);
    }};

    public boolean hasExtra() {
        return presetsWithExtra.contains(this);
    }

    public boolean hasSides() {
        if (this.hasExtra()) {
            String baseName = this.name();
            if (baseName.contains("_DECO")) {
                baseName = baseName.substring(0, baseName.indexOf("_DECO"));
            }
            try {
                return presetsWithSides.contains(BoxPreset.valueOf(baseName));
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return presetsWithSides.contains(this);
    }

    private final Consumer<Box> loadFunc;

    BoxPreset(Consumer<Box> loadFunc) {
        this.loadFunc = loadFunc;
    }

    public void load(Box box) {
        this.loadFunc.accept(box);
    }
}
