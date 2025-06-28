package com.ded.misle.world.entities.ai;

import java.util.function.Function;

/**
 * Represents a conditional check that determines whether an AI behavior
 * should be considered for execution based on the provided {@link BehaviorContext}.
 * <p>
 * Can be dynamically configured using {@link #setCondition(Function)} or extended via {@link #addCondition(Function)}.
 */
public interface BehaviorCondition {

    /**
     * Checks if the condition matches the given behavior context.
     *
     * @param context the AI context (including self, target, world)
     * @return {@code true} if all conditions are satisfied; {@code false} otherwise
     */
    boolean matches(BehaviorContext context);

    /**
     * Adds a new condition that must be satisfied for the behavior to match.
     * <p>
     * Multiple conditions are combined using logical AND.
     *
     * @param condition a new condition to add
     */
    void addCondition(Function<BehaviorContext, Boolean> condition);

    /**
     * Updates or replaces the internal logic used for condition matching.
     *
     * @deprecated This method replaces all previously added conditions.
     * Use {@link #addCondition(Function)} unless you are intentionally discarding all prior conditions.
     * @param condition a new condition function
     */
    void setCondition(Function<BehaviorContext, Boolean> condition);
}