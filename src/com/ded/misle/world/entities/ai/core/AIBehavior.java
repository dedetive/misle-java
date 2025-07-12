package com.ded.misle.world.entities.ai.core;

/**
 * Represents an AI behavior that can be conditionally executed in a world context.
 * It extends {@link BehaviorCondition}, so each behavior includes a logic condition
 * and execution method.
 *
 * AI behaviors can also be prioritized and flagged as interruptible or not.
 */
public interface AIBehavior extends BehaviorCondition {

    /**
     * Attempts to execute the behavior using the given context.
     * The behavior is expected to check {@link #matches(BehaviorContext)} before execution.
     *
     * @param context the AI context (entity, target, and world)
     */
    void tryExecute(BehaviorContext context);

    /**
     * Called when this behavior becomes the active behavior,
     * typically after being selected from a set of possible behaviors.
     * <p>
     * This method allows behaviors to perform any setup or transition logic
     * when they are activated.
     *
     * @param context the current behavior context
     */
    void switchIn(BehaviorContext context);

    /**
     * Called when this behavior is no longer the active behavior.
     * <p>
     * This method allows behaviors to perform any cleanup, animation resets,
     * or other teardown logic when being deactivated.
     *
     * @param context the current behavior context
     */
    void switchOut(BehaviorContext context);

    /**
     * Adds a new action to be executed when the behavior becomes active.
     * <p>
     * This allows behaviors to register custom logic to run upon activation,
     * in addition to their own default activation behavior.
     * <p>
     * Actions are executed in the order they were added.
     *
     * @param action the action to execute upon activation
     */
    void addOnSwitchIn(java.util.function.Consumer<BehaviorContext> action);

    /**
     * Adds a new action to be executed when the behavior is deactivated.
     * <p>
     * This allows behaviors to register custom logic to run upon deactivation,
     * in addition to their own default deactivation behavior.
     * <p>
     * Actions are executed in the order they were added.
     *
     * @param action the action to execute upon deactivation
     */
    void addOnSwitchOut(java.util.function.Consumer<BehaviorContext> action);

    /**
     * Returns whether the behavior can be interrupted by other higher-priority behaviors.
     *
     * @return true if the behavior is interruptible, false otherwise
     */
    boolean isInterruptible();

    /**
     * Sets whether the behavior is interruptible.
     *
     * @param interruptible true to allow interruption, false otherwise
     */
    void setInterruptible(boolean interruptible);

    /**
     * Sets the priority of the behavior.
     * Higher values indicate a stronger priority when choosing behaviors to execute.
     *
     * @param priority the priority value
     */
    void setPriority(int priority);

    /**
     * Gets the priority level of the behavior.
     *
     * @return the priority value
     */
    int getPriority();

    /**
     * Returns the specific type of this behavior.
     *
     * @return the {@link BehaviorType} associated with this behavior
     */
    BehaviorType getType();
}