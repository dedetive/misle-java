package com.ded.misle.world.logic;

import com.ded.misle.world.boxes.Box;

import java.awt.*;

/**
 * Handles line-of-sight checks between boxes in the world.
 * <p>
 * This class determines whether there is an unobstructed path
 * (i.e., a clear line of sight) from a source {@link Box} (origin) to a target {@link Box}.
 * A sight check iterates through each {@link Point} on the path and verifies if the space is occupied.
 */
public class Sight {
    /**
     * The box from which visibility is being calculated.
     */
    private final Box origin;

    /**
     * The target box being checked for visibility.
     */
    private Box target;

    /**
     * Flag indicating whether the visibility state needs to be recalculated.
     */
    private boolean mustUpdate = true;

    /**
     * Cached result of the last visibility calculation.
     */
    private boolean hasDirectSight = false;

    /**
     * Constructs a BoxSight with both an origin and a target.
     *
     * @param origin the source {@link Box} from which vision is checked
     * @param target the target {@link Box} to be checked for visibility
     */
    public Sight(Box origin, Box target) {
        this.origin = origin;
        this.target = target;
    }

    /**
     * Constructs a BoxSight with only the origin. Target can be set later.
     *
     * @param origin the source {@link Box} from which vision is checked
     */
    public Sight(Box origin) {
        this.origin = origin;
    }

    /**
     * Checks whether the origin box has a clear line of sight to the specified target.
     * <p>
     * A clear path is determined by verifying that no collidable space exists along the path.
     *
     * @param target the target {@link Box} to check visibility to
     * @return {@code true} if the origin can see the target, {@code false} otherwise
     */
    public boolean canSee(Box target) {
        if (mustUpdate) {
            Path pathToTarget = new Path(origin.getPos(), target.getPos()).removePoint(new Point(origin.getX(), origin.getY()));

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
     * Checks whether the origin box has a clear line of sight to the current target.<p>
     * This method uses this object's target as default, and will throw an exception if it has not been set.
     *
     * @return {@code true} if the origin can see the target, {@code false} otherwise
     * @throws NullPointerException if target has not been set
     */
    public boolean canSee() {
        if (target == null) throw new NullPointerException("The target box for this BoxSight is null");

        return canSee(this.target);
    }

    /**
     * Sets a new target for the vision check and marks the state to be re-evaluated on the next check.
     *
     * @param target the new target {@link Box}
     */
    public void setTarget(Box target) {
        this.target = target;
        mustUpdate = true;
    }
}
