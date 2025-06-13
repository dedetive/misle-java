package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;
import com.ded.misle.world.logic.Path;
import com.ded.misle.world.logic.PhysicsEngine;

import java.awt.*;

/**
 * An AI behavior that guides an entity to follow a predefined path of points in a forward-and-backward manner.
 * This behavior moves the entity from the first point in its path toward the last, then reverses direction
 * and moves back toward the first, repeating this process in a loop.
 * <p>
 * The path is defined by a set of points provided upon instantiation.
 * This behavior is interruptible and typically holds the lowest priority, allowing
 * other, higher-priority behaviors to interrupt it when needed (for instance, when the entity notices a nearby player).
 * <p>
 * The main role of this behavior is to enable entities to perform routine or guard-like movement, following their path back and forth.
 * This can aid in adding depth and variation to their routines or making their movement less static.
 */
public class PatrolBehavior extends AbstractBehavior {

    /**
     * The predefined path made up of points that the entity should follow back and forth.
     */
    private final Path patrolPath;

    /**
     * The current step or index in the path.
     */
    private int step;

    /**
     * The index of the last step in the path.
     */
    private final int lastStep;

    /**
     * Flag indicating whether the entity is currently moving in reverse direction.
     */
    private boolean returning;

    /**
     * Initializes a new {@code PatrolBehavior} with the specified points.
     * The points define the path the entity will follow back and forth.
     *
     * @param points the points in the path to follow
     */
    public PatrolBehavior(Point... points) {
        this(new Path(points));
    }

    /**
     * Initializes a new {@code PatrolBehavior} by combining multiple paths into a single path.
     * All points from each path are concatenated in the final path.
     *
     * @param patrolPath the paths to combine into a single path
     */
    public PatrolBehavior(Path... patrolPath) {
        this.patrolPath = new Path();
        for (Path p : patrolPath) {
            this.patrolPath.addPoints(p.getPoints());
        }

        this.lastStep = this.patrolPath.getLength() - 1;

        this.setInterruptible(true);
        this.priority = Integer.MIN_VALUE;
    }

    /**
     * Performs the patrol behavior by attempting to move toward the current step in the path.
     * If the step is occupied, it may trigger an effect instead.
     * After attempting to move, it prepares for the subsequent step.
     *
     * @param context the context for this behavior, including the entity itself
     */
    @Override
    public void tryExecute(BehaviorContext context) {
        if (patrolPath.getLength() == 0) return;

        Point target = patrolPath
            .offset(context.self().getOrigin()) // Align path to entity's origin
            .getPoints()[calculateNextStep()];

        if (PhysicsEngine.isSpaceOccupied(target.x, target.y)) {
            triggerEffectOnPlayerContact(context, target);
        } else {
            BoxManipulation.moveToward(context.self(), target, false);
        }

        patrolPath.undo();
        advanceStep();
    }

    /**
     * Advances the step forward or backward along the path, depending on whether it's currently
     * following forward or reverse direction.
     */
    private void advanceStep() {
        step = calculateNextStep();
    }

    /**
     * Computes the index of the subsequent step along the path.
     * If the path reaches its last or first point, the direction reverses.
     *
     * @return the index of the subsequent step
     */
    private int calculateNextStep() {
        if (step >= lastStep) returning = true;
        else if (step <= 0) returning = false;

        return step + (returning ? -1 : 1);
    }

    /**
     * Returns the behavioral type for this instance.
     *
     * @return the behavior's type
     */
    @Override
    public BehaviorType getType() {
        return BehaviorType.PATROL;
    }

    /**
     * Prints a string representation of this PatrolBehavior.
     *
     * @return a string representation with details about its state
     */
    @Override
    public String toString() {
        return super.toString().replace("}", "") + ", " +
            "patrolPath=" + patrolPath +
            ", lastStep=" + lastStep +
            ", step=" + step +
            ", returning=" + returning +
            '}';
    }
}
