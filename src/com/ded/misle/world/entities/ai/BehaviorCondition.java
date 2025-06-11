package com.ded.misle.world.entities.ai;

import java.util.function.Function;

/**
 * Represents a conditional check that determines whether an AI behavior
 * should be considered for execution based on the provided {@link BehaviorContext}.
 *
 * Can be dynamically configured via {@link #setCondition(Function)}.
 */
public interface BehaviorCondition {

    /**
     * Checks if the condition matches the given behavior context.
     *
     * @param context the AI context (including self, target, world)
     * @return true if the condition is satisfied, false otherwise
     */
    boolean matches(BehaviorContext context);

    /**
     * Updates or replaces the internal logic used for condition matching.
     *
     * @param condition a new condition function
     */
    void setCondition(Function<BehaviorContext, Boolean> condition);
}