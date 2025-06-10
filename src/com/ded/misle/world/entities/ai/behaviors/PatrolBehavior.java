package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;
import com.ded.misle.world.logic.Path;
import com.ded.misle.world.logic.PhysicsEngine;

import java.awt.*;

public class PatrolBehavior implements AIBehavior {
    private int priority = Integer.MIN_VALUE;

    private final Path patrolPath;
    private int step;
    private final int lastStep;
    private boolean returning = false;

    public PatrolBehavior(Point... points) {
        this(new Path(points));
    }

    public PatrolBehavior(Path... patrolPath) {
        this.patrolPath = new Path();
        for (Path p : patrolPath) {
            this.patrolPath.addPoints(p.getPoints());
        }
        this.lastStep = this.patrolPath.getLength() - 1;
    }

    @Override
    public void tryExecute(BehaviorContext context) {
        if (patrolPath.getLength() == 0) return;

        updateStepCount();

        Point target = patrolPath.getPoints()[step];

        if (PhysicsEngine.isSpaceOccupied(target.x, target.y)) returning = !returning; // Invert if hit something
        BoxManipulation.moveToward(context.self(), patrolPath.getPoints()[step], false);
    }

    @Override
    public boolean isInterruptible() {
        return true;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public BehaviorType getType() {
        return BehaviorType.PATROL;
    }

    private void updateStepCount() {
        if (step >= lastStep) returning = true;
        else if (step <= 0) returning = false;

        step += returning ? -1 : 1;
    }
}
