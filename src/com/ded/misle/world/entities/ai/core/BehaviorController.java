package com.ded.misle.world.entities.ai.core;

import com.ded.misle.world.entities.Entity;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

import static com.ded.misle.game.GamePanel.player;

/**
 * Controls the behavior logic of an entity by selecting and executing the most appropriate {@link AIBehavior}
 * based on the current context (e.g., the entity's state, the player, and the target).
 */
public class BehaviorController implements Runnable {

    /** The entity that owns this behavior controller. */
    private final Entity<?> entity;

    /** The current target the entity may be reacting to. */
    private Entity<?> target;

    /**
     * The position the entity last saw its target.
     */
    private Point lastSeenTargetPos;

    /** The behavior currently being executed. */
    private AIBehavior currentBehavior;

    /** All available behaviors this entity can choose from. */
    private AIBehavior[] behaviors = new AIBehavior[0];

    /**
     * Constructs a new {@code BehaviorController} for the given entity.
     *
     * @param entity the entity whose behavior is being controlled
     */
    public BehaviorController(Entity<?> entity) {
        this.entity = entity;
    }

    /**
     * Called each turn to evaluate and execute the highest-priority matching behavior.
     * If a behavior is already running and marked as non-interruptible, it will continue
     * running as long as it still matches the context.
     */
    @Override
    public void run() {
        if (behaviors.length == 0) return;

        BehaviorContext context = new BehaviorContext(
            entity,
            target,
            lastSeenTargetPos,
            player.pos.world
        );

        AIBehavior bestBehavior = Arrays.stream(behaviors)
            .filter(b -> b.matches(context))
            .max(Comparator.comparingInt(AIBehavior::getPriority))
            .orElse(null);

        bestBehavior = currentBehavior != null &&
            !currentBehavior.isInterruptible() &&
            currentBehavior.matches(context)
            ? currentBehavior
            : bestBehavior;

        boolean isSwitching = bestBehavior != currentBehavior;
        if (isSwitching) {
            if (currentBehavior != null)
                currentBehavior.switchOut(context);
            if (bestBehavior != null) bestBehavior.switchIn(context);
            else this.run();
        }

        if (bestBehavior != null) {

            bestBehavior.tryExecute(context);
            currentBehavior = bestBehavior;
        }
    }

    /**
     * Sets the target that this entity should track or respond to.
     *
     * @param target the target entity
     */
    public void setTarget(Entity<?> target) {
        this.target = target;
    }

    /**
     * Sets the point that this entity last tracked its position.
     * @param lastSeenTargetPos the target point
     */
    public void setLastSeenTargetPos(Point lastSeenTargetPos) {
        this.lastSeenTargetPos = lastSeenTargetPos;
    }

    /**
     * Sets the list of possible behaviors this controller can evaluate and execute.
     *
     * @param behaviors the available behaviors
     */
    public void setBehaviors(AIBehavior... behaviors) {
        this.behaviors = behaviors;
    }

    /**
     * Returns all available behaviors assigned to this controller.
     *
     * @return the array of behaviors
     */
    public AIBehavior[] getBehaviors() {
        return behaviors;
    }

    /**
     * Returns the current behavior being executed, if any.
     *
     * @return the current behavior or {@code null} if none is active
     */
    public AIBehavior getCurrentBehavior() {
        return currentBehavior;
    }
}