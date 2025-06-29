package com.ded.misle.world.logic;

import java.awt.*;

/**
 * Handles line-of-sight checks between two points in the world.
 * <p>
 * This class determines whether there is an unobstructed path
 * (i.e., a clear line of sight) from a source {@link Point} (origin) to a target {@link Point}.
 * A sight check iterates through each {@link Point} on the path and verifies if the space is occupied.
 */
public class Sight {
    /**
     * The point from which visibility is being calculated.
     */
    private final Point origin;

    /**
     * The target point being checked for visibility.
     */
    private Point target;

    /**
     * Flag indicating whether the visibility state needs to be recalculated.
     */
    private boolean mustUpdate = true;

    /**
     * Cached result of the last visibility calculation.
     */
    private boolean hasDirectSight = false;

    /**
     * Constructs a PointSight with both an origin and a target.
     *
     * @param origin the source {@link Point} from which vision is checked
     * @param target the target {@link Point} to be checked for visibility
     */
    public Sight(Point origin, Point target) {
        this.origin = origin;
        this.target = target;
    }

    /**
     * Constructs a PointSight with only the origin. Target can be set later.
     *
     * @param origin the source {@link Point} from which vision is checked
     */
    public Sight(Point origin) {
        this.origin = origin;
    }

    /**
     * Checks whether the origin point has a clear line of sight to the specified target.
     * <p>
     * A clear path is determined by verifying that no collidable space exists along the path.
     *
     * @param target the target {@link Point} to check visibility to
     * @return {@code true} if the origin can see the target, {@code false} otherwise
     */
    public boolean canSee(Point target) {
        if (mustUpdate) {
            Path pathToTarget = new Path(origin, target)
                .removePoint(new Point(origin.x, origin.y))
                .removePoint(new Point(target.x, target.y));

            hasDirectSight = true;
            for (Point point : pathToTarget.getPoints()) {
                if (PhysicsEngine.isSpaceOccupied(point.x, point.y)) {
                    hasDirectSight = false;
                    break;
                }
            }
        }

        mustUpdate = false;
        return hasDirectSight;
    }

    /**
     * Checks whether the origin point has a clear line of sight to the current target.<p>
     * This method uses this object's target as default, and will throw an exception if it has not been set.
     *
     * @return {@code true} if the origin can see the target, {@code false} otherwise
     * @throws NullPointerException if target has not been set
     */
    public boolean canSee() {
        if (target == null) throw new NullPointerException("The target point for this PointSight is null");

        return canSee(this.target);
    }

    /**
     * Sets a new target for the vision check and marks the state to be re-evaluated on the next check.
     *
     * @param target the new target {@link Point}
     */
    public void setTarget(Point target) {
        this.target = target;
        mustUpdate = true;
    }

    /**
     * Gets this object's origin.
     * @return the origin {@link Point}
     */
    public Point getOrigin() {
        return origin;
    }

    /**
     * Returns this object's target.
     * @return the target {@link Point}
     */
    public Point getTarget() {
        return target;
    }
}
