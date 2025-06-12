package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;
import com.ded.misle.world.logic.Path;
import com.ded.misle.world.logic.PhysicsEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class WanderBehavior extends AbstractBehavior {
    private int maxDistanceFromOrigin;
    private Path wanderingPath;
    private final WanderMode wanderMode;

    private final Random random = new Random();

    public WanderBehavior() {
        this.maxDistanceFromOrigin = Integer.MAX_VALUE;
        this.wanderMode = WanderMode.DISTANCE;
    }

    public WanderBehavior(int maxDistanceFromOrigin) {
        this.maxDistanceFromOrigin = maxDistanceFromOrigin;
        this.wanderMode = WanderMode.DISTANCE;
    }

    public WanderBehavior(Path wanderingPath) {
        this.wanderingPath = wanderingPath;
        this.wanderMode = WanderMode.PATH;
    }

    @Override
    public void tryExecute(BehaviorContext context) {
        java.util.List<Point> validPos = new ArrayList<>();
        Entity self = context.self();

        switch (wanderMode) {
            case DISTANCE -> {
                Point origin = self.getOrigin();
                Point currentPos = self.getPos();

                // Is current pos within valid region
                  // If is, move anywhere valid
                  // If not, attempt to return
                if (isWithinWanderRegion(
                    self.getOrigin(), currentPos)) {

                    Point[] cardinalPoints = new Point[]{
                        new Point(currentPos.x, currentPos.y - 1), // Up
                        new Point(currentPos.x, currentPos.y + 1), // Down
                        new Point(currentPos.x - 1, currentPos.y), // Left
                        new Point(currentPos.x + 1, currentPos.y)  // Right
                    };

                    validPos =
                        Arrays.stream(cardinalPoints)
                            .filter(cardinalPoint -> isWithinWanderRegion(origin, cardinalPoint))
                            .toList();
                }

                Point target = validPos.get(
                    random.nextInt(validPos.size())
                );

                if (PhysicsEngine.isSpaceOccupied(target.x, target.y)) {
                    triggerEffectOnPlayerContact(context, target);
                } else {
                    BoxManipulation.moveToward(self, target, false);
                }
            }
        }
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

    private enum WanderMode {
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
