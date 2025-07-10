package com.ded.misle.world.data.entity.configurations;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.data.entity.GenericType;
import com.ded.misle.world.entities.Entity;

/**
 * Enumeration of predefined entity types.
 * Each constant refers to a predefined entity configuration
 * declared in {@link EntityConfigurations}, allowing centralized
 * management and easier code navigation.
 */
public enum EntityType implements GenericType {
    BUNNY(EntityConfigurations.BUNNY)

    ;

    /**
     * Reference to a predefined configuration for an entity.
     */
    private final EntityConfigurations configuration;

    /**
     * Constructs a new EntityType based on the given configuration enum.
     *
     * @param configuration a constant from {@link EntityConfigurations}
     *                      that holds the setup logic for this entity
     */
    EntityType(EntityConfigurations configuration) {
        this.configuration = configuration;
    }

    /**
     * Applies this entity type’s configuration to the given {@link Entity} instance.
     *
     * @param entity the entity to configure
     */
    public void applyTo(Entity entity) {
        configuration.c.configure(entity);
    }

    @Override
    public void applyTo(Box box) {
        if (box instanceof Entity) applyTo((Entity) box);
    }
}
