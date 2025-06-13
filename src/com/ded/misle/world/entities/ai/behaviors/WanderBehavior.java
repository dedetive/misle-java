package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;
import com.ded.misle.world.logic.Path;
import com.ded.misle.world.logic.PhysicsEngine;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static com.ded.misle.game.GamePanel.player;

public class WanderBehavior extends AbstractBehavior {
    private int maxDistanceFromOrigin;
    private Path customPath;
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

    public WanderBehavior(Path customPath) {
        this.customPath = customPath;
        this.wanderMode = WanderMode.CUSTOM_PATH;
    }

    @Override
    public void tryExecute(BehaviorContext context) {
        Entity self = context.self();

        List<Point> validPos = computeValidPositions(self);

        if (validPos.isEmpty()) {
            return;
        }

        Point target = validPos.get(random.nextInt(validPos.size()));

        attemptToMove(context, target);
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

    private List<Point> computeValidPositions(Entity self) {
        List<Point> validPos = new ArrayList<>();
        Point origin = self.getOrigin();
        Point currentPos = self.getPos();

        if (isWithinWanderRegion(origin, currentPos)) {
            Point[] cardinalPoints = new Point[]{
                new Point(currentPos.x, currentPos.y - 1), // Up
                new Point(currentPos.x, currentPos.y + 1), // Down
                new Point(currentPos.x - 1, currentPos.y), // Left
                new Point(currentPos.x + 1, currentPos.y)  // Right
            };

            validPos = Stream.of(cardinalPoints)
                .filter(cardinalPoint -> isWithinWanderRegion(origin, cardinalPoint))
                .filter(cardinalPoint ->
                    !(PhysicsEngine.isSpaceOccupied(cardinalPoint.x, cardinalPoint.y)) ||
                    player.getPos().equals(cardinalPoint))
                .toList();
        }

        return validPos;
    }

    private void attemptToMove(BehaviorContext context, Point target) {
        Entity self = context.self();

        if (PhysicsEngine.isSpaceOccupied(target.x, target.y)) {
            triggerEffectOnPlayerContact(context, target);
        } else {
            BoxManipulation.moveToward(self, target, false);
        }
    }

    private enum WanderMode {
        /**
         * Walks randomly within a confined path.
         */
        CUSTOM_PATH,

        /**
         * Walks randomly within a max distance.
         */
        DISTANCE
    }
}
