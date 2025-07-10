package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;
import com.ded.misle.world.entities.enemies.Enemy;
import com.ded.misle.world.logic.Path;
import com.ded.misle.world.logic.Pathfinder;
import com.ded.misle.world.logic.PhysicsEngine;
import com.ded.misle.world.logic.Sight;

import java.awt.*;
import java.util.function.Predicate;

import static com.ded.misle.world.logic.PhysicsEngine.isSpaceOccupied;

/**
 * An AI behavior that enables an entity to pursue a visible or previously seen target.
 * <p>
 * This behavior causes the entity to move toward its target using A* pathfinding,
 * following the shortest walkable path available. If the entity can no longer see
 * the target, it instead moves toward the last known position where the target was seen.
 * <p>
 * This behavior is interruptible and holds medium-to-low priority (by default, {@code 0}),
 * making it a natural part of layered AI decision-making — such as being used as the
 * "default active engagement" behavior once the target is spotted.
 * <p>
 * Pursuit proceeds as follows:
 * <ul>
 *   <li>If the target is within line of sight (LOS), a path is calculated directly toward it.</li>
 *   <li>If not visible, but a {@code lastSeenTargetPos} is known, the entity will attempt to path there.</li>
 *   <li>If neither is valid (i.e., target not visible and no position stored), the behavior is inactive.</li>
 * </ul>
 * <p>
 * Upon regaining visual contact, the last seen position is updated to reflect the new location.
 * The behavior also triggers a contact effect if the entity reaches the target's position.
 * <p>
 * This behavior works best in coordination with others (e.g., patrol, wait, wander, attack),
 * and its condition can be reused for targeting logic or used inside {@link ChainBehavior}.
 * <p>
 * The collision check is traditionally checking whether the target point is occupied by a Box with collision.
 * Such check may be modified through {@link #setCollisionCheck(Predicate)}, and will be used for both
 * pathfinding and movement.
 * <p>
 * <strong>Note:</strong> For this behavior to work, a target is required.
 *
 * @see com.ded.misle.world.logic.Pathfinder
 * @see com.ded.misle.world.logic.Sight
 * @see com.ded.misle.world.entities.ai.BehaviorContext
 */
public class PursueBehavior extends AbstractBehavior {

    /**
     * A predicate used to determine which tiles are walkable during pathfinding.
     * Defaults to spaces that are not occupied, as checked by {@link PhysicsEngine#isSpaceOccupied(int, int)}.
     * <p>
     * This can be replaced with custom logic using {@link #setCollisionCheck(Predicate)}.
     */
    private Predicate<Point> collisionCheck = p -> !isSpaceOccupied(p.x, p.y);

    /**
     * Creates a new PursueBehavior with default configuration.
     * <p>
     * It is interruptible and has default priority {@code 0}.
     * The behavior becomes valid if a path to the target or its last known position exists.
     * Visibility checks are performed using {@link Sight}, and pathfinding via {@link Pathfinder}.
     */
    @SuppressWarnings("deprecation")
    public PursueBehavior() {
        this.interruptible = true;
        this.priority = 0;

        this.setCondition(
            ctx -> {
                if (ctx.target() == null) return false;

                Sight sight = new Sight(ctx.self().getPos());
                sight.setMaxSight(ctx.self().getMaxSight());
                boolean canSeeTarget = (sight.canSee(ctx.target().getPos()));
                Point targetPos = canSeeTarget
                    ? ctx.target().getPos()
                    : ctx.lastSeenTargetPos();

                if (targetPos == null) return false;

                Path p = new Pathfinder().findPath(ctx.self().getPos(), targetPos, collisionCheck);
                if (p == null) return false;
                return p.getLength() > 0;
            }
        );
    }

    /**
     * Executes a single tick of the pursue behavior logic.
     * <p>
     * The entity will attempt to move one step along the computed path toward the target.
     * If the entity reaches the same position as the target, a contact effect may be triggered.
     * If the path is blocked or invalid, the entity halts.
     *
     * @param context the context of the behavior, including self, target, and last seen position
     */
    @Override
    public void tryExecute(BehaviorContext context) {
        Entity self = context.self();
        Entity target = context.target();

        Sight sight = new Sight(self.getPos());
        sight.setMaxSight(self.getMaxSight());
        boolean canSeeTarget = (sight.canSee(target.getPos()));
        Point targetPos = canSeeTarget
            ? target.getPos()
            : context.lastSeenTargetPos();

        if (canSeeTarget) {
            self.getController().setLastSeenTargetPos(target.getPos());
        }

        Path pathToTarget = new Pathfinder().findPath(
            self.getPos(), targetPos, collisionCheck);

        if (pathToTarget == null || (!canSeeTarget && context.lastSeenTargetPos() == null))
            return;

        Point p = pathToTarget.getStart();
        if (p == null) return;

        if (target.getPos().equals(p)) {
            this.triggerEffectOnContact(context, p);
        } else if (collisionCheck.test(p)) {
            BoxManipulation.moveToward(self, p, true);
        }
    }

    /**
     * Updates the collision-checking logic used during pathfinding and movement.
     * <p>
     * Useful when custom environmental constraints or special movement logic must be considered.
     *
     * @param collisionCheck a predicate that returns {@code true} for walkable positions
     */
    public void setCollisionCheck(Predicate<Point> collisionCheck) {
        this.collisionCheck = collisionCheck;
    }

    /**
     * Returns the behavior type of this instance.
     *
     * @return the behavior type {@link BehaviorType#PURSUE}
     */
    @Override
    public BehaviorType getType() {
        return BehaviorType.PURSUE;
    }
}
