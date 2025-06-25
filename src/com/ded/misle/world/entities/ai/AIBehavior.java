package com.ded.misle.world.entities.ai;

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
    void onSwitchIn(BehaviorContext context);

    void onSwitchOut(BehaviorContext context);

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