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

/**
 * An AI behavior that guides an entity to move to a nearby or custom path.
 * This behavior can either wander anywhere freely,
 * wander within a maximum distance from its origin, or
 * follow a predefined path of points.
 * <p>
 * The direction of a step is equally likely, unconditional, and randomized, within valid spaces.
 * <p>
 * If, for whatever reason, the entity is not in a valid space, it actively attempts to return to its valid positions.
 * <p>
 * This behavior is, by default, interruptible and lowest-priority.
 */
public class WanderBehavior extends AbstractBehavior {
    /**
     * The maximum distance this entity can move away from its origin point while wandering.
     */
    private int maxDistanceFromOrigin;

    /**
     * An optional custom path composed of points within which the entity should move.
     */
    private Path customPath;

    /**
     * The wander mode that controls this behavior.
     * {@link WanderMode#CUSTOM_PATH} will follow the custom path; {@link WanderMode#DISTANCE} will move within maxDistance.
     */
    private final WanderMode mode;

    /**
     * The pseudorandom number generator used to select a new destination when wandering.
     */
    private final Random random = new Random();

    /**
     * Initializes a new WanderBehavior with no maximum distance (integer maximum).
     * The entity will move in a wander-like manner without range limitation.
     */
    public WanderBehavior() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Initializes a new WanderBehavior with a maximum distance from its origin.
     * The entity will move within this range.
     *
     * @param maxDistanceFromOrigin maximum range in tile distance
     */
    public WanderBehavior(int maxDistanceFromOrigin) {
        this.maxDistanceFromOrigin = maxDistanceFromOrigin;
        this.mode = WanderMode.DISTANCE;
        this.setInterruptible(true);
    }

    /**
     * Initializes a new WanderBehavior with a custom path.
     * The entity will follow this path instead of wander within a range.
     *
     * @param customPath custom path for the entity to follow
     */
    public WanderBehavior(Path customPath) {
        this.customPath = customPath;
        this.mode = WanderMode.CUSTOM_PATH;
        this.setInterruptible(true);
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

        Point[] neighbors = getCardinalNeighbors(currentPos).toArray(new Point[0]);

        if (isWithinWanderRegion(origin, currentPos)) {

            return Stream.of(neighbors)
                .filter(cardinalPoint -> isWithinWanderRegion(origin, cardinalPoint))
                .filter(cardinalPoint ->
                    !(PhysicsEngine.isSpaceOccupied(cardinalPoint.x, cardinalPoint.y)) ||
                    player.getPos().equals(cardinalPoint))
                .toList();
        } else {
            return Stream.of(neighbors)
                .filter(p -> !PhysicsEngine.isSpaceOccupied(p.x, p.y) ||
                    player.getPos().equals(p))
                .filter(p -> getDistanceFromOrigin(p, origin) < getDistanceFromOrigin(currentPos, origin))
                .toList();
        }
    }

    private List<Point> computeValidPositionsCustomPath(Entity self) {
        Point currentPos = self.getPos();

        List<Point> neighbors = getCardinalNeighbors(currentPos);

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

    private List<Point> getCardinalNeighbors(Point pos) {
        return List.of(
            new Point(pos.x, pos.y - 1), // Up
            new Point(pos.x, pos.y + 1), // Down
            new Point(pos.x - 1, pos.y), // Left
            new Point(pos.x + 1, pos.y)  // Right
        );
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
