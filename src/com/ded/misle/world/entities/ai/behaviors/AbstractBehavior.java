package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorContext;

import java.awt.*;
import java.util.function.Function;

import static com.ded.misle.game.GamePanel.player;

/**
 * A base implementation of {@link AIBehavior} that provides default behavior logic,
 * including priority, interruptibility, and condition matching.
 * <p>
 * Subclasses can extend this class to define custom behavior logic by implementing
 * the {@link #tryExecute(BehaviorContext)} method.
 */
public abstract class AbstractBehavior implements AIBehavior {

    /** The priority of this behavior. Higher values take precedence over lower ones. */
    protected int priority = Integer.MIN_VALUE;

    /** Whether this behavior can be interrupted by another one. */
    protected boolean interruptible = true;

    /**
     * A condition function that determines whether this behavior matches the current context.
     * By default, this condition always returns {@code true}.
     */
    private Function<BehaviorContext, Boolean> condition = ctx -> true;

    protected void triggerEffectOnPlayerContact(BehaviorContext context, Point target) {
        if (player.getPos().equals(target)) {
            context.self().effect.run(context.self(), player);
        }
    }

    public void onSwitch(BehaviorContext context) {}

    /**
     * Checks whether this behavior should be selected based on the given context.
     * <p>
     * By default, the condition always returns {@code true}, meaning the behavior
     * is considered valid unless overridden.
     *
     * @param context the context to evaluate
     * @return {@code true} if this behavior matches the given context; {@code false} otherwise
     */
    @Override
    public boolean matches(BehaviorContext context) {
        return condition.apply(context);
    }

    /**
     * Sets a custom condition function used to evaluate whether this behavior applies.
     * <p>
     * Replaces the default condition, which always returns {@code true}.
     *
     * @param condition a function that receives the context and returns {@code true} if it matches
     */
    @Override
    public void setCondition(Function<BehaviorContext, Boolean> condition) {
        this.condition = condition;
    }

    /**
     * Sets the priority of this behavior.
     *
     * @param priority an integer priority; higher values take precedence
     */
    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Gets the priority of this behavior.
     *
     * @return the behavior's priority
     */
    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * Returns whether this behavior can be interrupted by another behavior.
     *  <p>
     *  If this behavior is uninterruptible and is the current behavior, it may only be stopped
     *  if its condition is not met, regardless of its priority.
     *  The only case in which it stops is if its condition no longer matches.
     *
     * @return {@code true} if interruptible; {@code false} otherwise
     */
    @Override
    public boolean isInterruptible() {
        return interruptible;
    }

    /**
     * Sets whether this behavior can be interrupted by a higher-priority behavior.
     * <p>
     * If this behavior is uninterruptible and is the current behavior, it may only be stopped
     * if its condition is not met, regardless of its priority.
     * The only case in which it stops is if its condition no longer matches.
     *
     * @param interruptible {@code true} to allow interruptions; {@code false} to make it exclusive
     */
    @Override
    public void setInterruptible(boolean interruptible) {
        this.interruptible = interruptible;
    }

    /**
     * Returns a string representation of this behavior, showing its class and priority.
     *
     * @return a string describing the behavior
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "priority=" + priority +
            '}';
    }
}