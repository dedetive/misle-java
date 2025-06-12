package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;
import com.ded.misle.world.logic.Path;

import java.awt.*;
import java.util.ArrayList;

public class WanderBehavior extends AbstractBehavior {
    private int maxDistanceFromOrigin;
    private Path wanderingPath;
    private WanderMode wanderMode;

    WanderBehavior() {
        this.maxDistanceFromOrigin = Integer.MAX_VALUE;
        this.wanderMode = WanderMode.DISTANCE;
    }

    WanderBehavior(int maxDistanceFromOrigin) {
        this.maxDistanceFromOrigin = maxDistanceFromOrigin;
        this.wanderMode = WanderMode.DISTANCE;
    }

    WanderBehavior(Path wanderingPath) {
        this.wanderingPath = wanderingPath;
        this.wanderMode = WanderMode.PATH;
    }

    @Override
    public void tryExecute(BehaviorContext context) {

    }

    @Override
    public BehaviorType getType() {
        return BehaviorType.WANDER;
    }

    private int getDistanceFromOrigin(Point origin, Point target) {
        return
            (int) Math.ceil(
                new Path(origin, target).getSpan()
            );
    }

    private boolean isWithinWanderRegion(Point origin, Point target) {
        return getDistanceFromOrigin(origin, target) <= maxDistanceFromOrigin;
    }

    public enum WanderMode {
        /**
         * Walks randomly within a confined path.
         */
        PATH,

        /**
         * Walks randomly within a max distance.
         */
        DISTANCE
    }
}
