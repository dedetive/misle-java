package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;

/**
 * An AI behavior that causes an entity to do nothing ("wait") for a fixed number of turns.
 * <p>
 * This behavior is commonly used to delay actions, synchronize movement, or create turn-based pacing.
 * <p>
 * The behavior will continue running for a specific number of turns defined at initialization.
 * Once the number of remaining turns reaches zero, it becomes invalid and will no longer match.
 * <p>
 * This behavior is non-interruptible by default, meaning it will continue running until the wait is over.
 * <p>
 * A new instance must be created for each use case, as it maintains internal state.
 */
public class WaitBehavior extends AbstractBehavior {

    /** The number of turns remaining until this behavior finishes. */
    private int remainingTurns;

    /** The original number of turns to wait, used to reset {@link #remainingTurns} to this value when the behavior is deactivated. */
    private final int START_TURNS;

    /**
     * Constructs a WaitBehavior that waits for the specified number of turns.
     * <p>
     * The behavior is non-interruptible and has priority zero by default.
     *
     * @param turns the number of turns the entity should wait
     */
    public WaitBehavior(int turns) {
        this.setInterruptible(false);
        this.priority = 0;

        this.remainingTurns = turns;
        this.START_TURNS = turns;

        this.setCondition(
            context -> remainingTurns > 0
        );
    }

    /**
     * Constructs a WaitBehavior that waits for a single turn.
     * <p>
     * Equivalent to calling {@code new WaitBehavior(1)}.
     */
    public WaitBehavior() {
        this(1);
    }

    /**
     * Called when the behavior is switched out (no longer active).
     * Resets the internal turn counter to the original starting value,
     * thus causing its condition to be true if left defaulted.
     *
     * @param context the current behavior context (unused)
     */
    @Override
    public void onSwitchOut(BehaviorContext context) {
        this.remainingTurns = START_TURNS;
    }

    /**
     * Executes the wait behavior by decrementing the remaining turn count.
     * <p>
     * This method is called once per turn as long as the behavior is active.
     *
     * @param context the behavior context for the current turn
     */
    @Override
    public void tryExecute(BehaviorContext context) {
        this.remainingTurns--;
    }

    /**
     * Returns the {@link BehaviorType} associated with this behavior.
     *
     * @return {@code BehaviorType.WAIT}
     */
    @Override
    public BehaviorType getType() {
        return BehaviorType.WAIT;
    }

    /**
     * Returns a string representation of the WaitBehavior,
     * including class name, priority, start turns, and remaining turns.
     *
     * @return a detailed string representation of the internal state
     */
    @Override
    public String toString() {
        return super.toString().replace("}", "") + ", " +
            "startTurns=" + START_TURNS +
            ", remainingTurns=" + remainingTurns +
            '}';
    }
}
