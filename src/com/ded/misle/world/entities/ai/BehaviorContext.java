package com.ded.misle.world.entities.ai;

import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.logic.World;

/**
 * Provides contextual information for AI behavior evaluation and execution.
 * This record groups together the current entity (self), an optional target,
 * and the world reference where the interaction is taking place.
 *
 * It is passed to AI behaviors and conditions to provide the necessary state.
 *
 * @param self   the entity performing the behavior
 * @param target the optional target entity (can be null)
 * @param world  the current world context
 */
public record BehaviorContext(
    Entity self,
    Entity target,
    World world
) {
    /**
     * Constructs a behavior context with both self and target entities.
     *
     * @param self   the entity performing the behavior
     * @param target the target entity (can be null)
     * @param world  the current world context
     */
    public BehaviorContext {}

    /**
     * Constructs a behavior context with no target.
     *
     * @param self  the entity performing the behavior
     * @param world the current world context
     */
    public BehaviorContext(Entity self, World world) {
        this(self, null, world);
    }
}