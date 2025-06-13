package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;
import com.ded.misle.world.logic.Path;
import com.ded.misle.world.logic.PhysicsEngine;

import java.awt.Point;
import java.util.*;
import java.util.stream.Stream;

import static com.ded.misle.game.GamePanel.player;

public class WanderBehavior extends AbstractBehavior {
    private int maxDistanceFromOrigin;
    private Path customPath;
    private final WanderMode mode;

    private final Random random = new Random();

    public WanderBehavior() {
        this.maxDistanceFromOrigin = Integer.MAX_VALUE;
        this.mode = WanderMode.DISTANCE;
    }

    public WanderBehavior(int maxDistanceFromOrigin) {
        this.maxDistanceFromOrigin = maxDistanceFromOrigin;
        this.mode = WanderMode.DISTANCE;
    }

    public WanderBehavior(Path customPath) {
        this.customPath = customPath;
        this.mode = WanderMode.CUSTOM_PATH;
    }

    @Override
    public void tryExecute(BehaviorContext context) {
        Entity self = context.self();


        List<Point> validPos;
        if (mode == WanderMode.CUSTOM_PATH) {
            customPath.offset(self.getOrigin());
            validPos = computeValidPositionsCustomPath(self);
            customPath.undo();
        }
        else validPos = computeValidPositionsDistance(self);


        if (validPos.isEmpty()) {
            return;
        }

        Point target = validPos.get(random.nextInt(validPos.size()));
        attemptToMove(context, target);
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

    private List<Point> computeValidPositionsDistance(Entity self) {
        Point origin = self.getOrigin();
        Point currentPos = self.getPos();

        Point[] cardinalPoints = new Point[]{
            new Point(currentPos.x, currentPos.y - 1), // Up
            new Point(currentPos.x, currentPos.y + 1), // Down
            new Point(currentPos.x - 1, currentPos.y), // Left
            new Point(currentPos.x + 1, currentPos.y)  // Right
        };

        if (isWithinWanderRegion(origin, currentPos)) {

            return Stream.of(cardinalPoints)
                .filter(cardinalPoint -> isWithinWanderRegion(origin, cardinalPoint))
                .filter(cardinalPoint ->
                    !(PhysicsEngine.isSpaceOccupied(cardinalPoint.x, cardinalPoint.y)) ||
                    player.getPos().equals(cardinalPoint))
                .toList();
        } else {
            return Stream.of(cardinalPoints)
                .filter(p -> !PhysicsEngine.isSpaceOccupied(p.x, p.y) ||
                    player.getPos().equals(p))
                .filter(p -> getDistanceFromOrigin(p, origin) < getDistanceFromOrigin(currentPos, origin))
                .toList();
        }
    }

    private List<Point> computeValidPositionsCustomPath(Entity self) {
        Point currentPos = self.getPos();

        List<Point> neighbors = List.of(
            new Point(currentPos.x, currentPos.y - 1), // Up
            new Point(currentPos.x, currentPos.y + 1), // Down
            new Point(currentPos.x - 1, currentPos.y), // Left
            new Point(currentPos.x + 1, currentPos.y)  // Right
        );

        if (customPath.contains(currentPos)) {
            return neighbors.stream()
                .filter(customPath::contains)
                .filter(p -> !PhysicsEngine.isSpaceOccupied(p.x, p.y) ||
                    player.getPos().equals(p))
                .toList();
        } else {
            Point nearest = Arrays.stream(customPath.getPoints())
                .min(Comparator.comparingInt(p -> getDistanceFromOrigin(currentPos, p)))
                .orElse(null);
            if (nearest == null) return List.of();

            return neighbors.stream()
                .filter(p -> !PhysicsEngine.isSpaceOccupied(p.x, p.y) ||
                    player.getPos().equals(p))
                .filter(p -> getDistanceFromOrigin(p, nearest) < getDistanceFromOrigin(currentPos, nearest))
                .toList();
        }
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

    @Override
    public BehaviorType getType() {
        return BehaviorType.WANDER;
    }
}
