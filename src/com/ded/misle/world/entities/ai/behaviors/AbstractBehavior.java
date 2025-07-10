package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.data.Direction;
import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorContext;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;

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
     * A list of condition functions that determine whether this behavior matches the current context.
     * By default, this condition always returns {@code true}.
     * <p>
     * All conditions must be met for the behavior to be run.
     */
    private final java.util.List<Function<BehaviorContext, Boolean>> conditions = new java.util.ArrayList<>();

    /**
     * Triggers the effect of the entity on the target entity if the target position matches the targeted position.
     * <p>
     * This utility method is useful for behaviors that cause interactions when the AI reaches or collides with the entity.
     *
     * @param context the behavior context containing entity and world information
     * @param targetPoint the target position to check for entity contact
     */
    protected void triggerEffectOnContact(BehaviorContext context, Point targetPoint) {
        if (context.target() == null) return;

        if (context.target().getPos().equals(targetPoint)) {
            context.self().effect.run(context.self(), context.target());
            context.self().updateLastDirection(Direction.interpretDirection(targetPoint));
        }
    }

    /**
     * List of actions to execute when the behavior becomes active.
     * <p>
     * Actions are executed in the order they were added, allowing modular
     * customization of activation behavior alongside default logic.
     */
    protected final java.util.List<java.util.function.Consumer<BehaviorContext>> onSwitchInActions = new java.util.ArrayList<>();

    /**
     * List of actions to execute when the behavior is deactivated.
     * <p>
     * Actions are executed in the order they were added, allowing modular
     * customization of deactivation behavior alongside default logic.
     */
    protected final java.util.List<java.util.function.Consumer<BehaviorContext>> onSwitchOutActions = new java.util.ArrayList<>();

    /**
     * Executes all registered activation actions for this behavior.
     * <p>
     * This method is final and cannot be overridden directly; instead,
     * additional actions should be registered via {@link #addOnSwitchIn}.
     *
     * @param context the current behavior context
     */
    @Override
    public final void onSwitchIn(BehaviorContext context) {
        for (var action : onSwitchInActions) {
            action.accept(context);
        }
    }

    /**
     * Executes all registered deactivation actions for this behavior.
     * <p>
     * This method is final and cannot be overridden directly; instead,
     * additional actions should be registered via {@link #addOnSwitchOut}.
     *
     * @param context the current behavior context
     */
    @Override
    public final void onSwitchOut(BehaviorContext context) {
        for (var action : onSwitchOutActions) {
            action.accept(context);
        }
    }

    /**
     * Registers a new action to be performed when this behavior becomes active.
     * <p>
     * Actions are executed in the order they are added.
     *
     * @param action the activation action to add
     */
    @Override
    public final void addOnSwitchIn(java.util.function.Consumer<BehaviorContext> action) {
        onSwitchInActions.add(action);
    }

    /**
     * Registers a new action to be performed when this behavior is deactivated.
     * <p>
     * Actions are executed in the order they are added.
     *
     * @param action the deactivation action to add
     */
    @Override
    public final void addOnSwitchOut(java.util.function.Consumer<BehaviorContext> action) {
        onSwitchOutActions.add(action);
    }

    /**
     * Checks whether this behavior should be selected based on the given context.
     * <p>
     * All registered conditions must return {@code true} for the behavior to match.
     * By default, the condition always returns {@code true}, meaning the behavior
     * is considered valid unless overridden.
     * This might be done either during behavior implementation or construction.
     *
     * @param context the context to evaluate
     * @return {@code true} if all conditions pass; {@code false} otherwise
     */
    @Override
    public final boolean matches(BehaviorContext context) {
        for (Function<BehaviorContext, Boolean> condition : conditions) {
            if (!condition.apply(context)) return false;
        }
        return true;
    }

    @Override
    public final java.util.List<Function<BehaviorContext, Boolean>> getConditions() {
        return conditions;
    }

    /**
     * Adds a new condition that must be met for this behavior to be run.
     * Conditions are combined with logical AND.
     * <p>
     * By default, the condition always returns {@code true}, meaning the behavior
     * is considered valid unless overridden.
     * This might be done either during behavior implementation or construction.
     *
     * @param condition a function that returns true if the condition passes
     */
    @Override
    public final void addCondition(Function<BehaviorContext, Boolean> condition) {
        this.conditions.add(condition);
    }

    @SafeVarargs
    @Override
    public final void addConditions(Function<BehaviorContext, Boolean>... conditions) {
        this.conditions.addAll(Arrays.asList(conditions));
    }

    @Override
    public final void addConditions(java.util.List<Function<BehaviorContext, Boolean>> conditions) {
        this.conditions.addAll(conditions);
    }

    /**
     * Replaces all existing conditions with a new single condition.
     * <p><strong>Warning:</strong> This is a destructive operation that clears all previously added conditions.
     * <br>Use this only if you're certain that replacing all existing conditions is intended.
     * <br>In most cases, prefer using {@link #addCondition(Function)} instead.
     *
     * @deprecated This method replaces all previously added conditions.
     * Use {@link #addCondition(Function)} unless you are intentionally discarding all prior conditions.
     * @param condition a function that receives the context and returns {@code true} if it matches
     */
    @Override
    @Deprecated
    public final void setCondition(Function<BehaviorContext, Boolean> condition) {
        this.conditions.clear();
        this.conditions.add(condition);
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