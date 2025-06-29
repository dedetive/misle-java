package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;
import com.ded.misle.world.entities.enemies.Enemy;
import com.ded.misle.world.logic.Path;
import com.ded.misle.world.logic.Pathfinder;
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

    private Predicate<Point> collisionCheck = p -> !isSpaceOccupied(p.x, p.y);

    public PursueBehavior() {
        this.interruptible = true;
        this.priority = 0;

        this.setCondition(
            ctx -> {
                if (ctx.target() == null) return false;

                boolean canSeeTarget = (new Sight(ctx.self().getPos()).canSee(ctx.target().getPos()));
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

    @Override
    public void tryExecute(BehaviorContext context) {
        Entity self = context.self();
        Entity target = context.target();

        boolean canSeeTarget = (new Sight(self.getPos()).canSee(target.getPos()));
        Point targetPos = canSeeTarget
            ? target.getPos()
            : context.lastSeenTargetPos();

        if (canSeeTarget) {
            ((Enemy) self).getController().setLastSeenTargetPos(target.getPos());
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

    public void setCollisionCheck(Predicate<Point> collisionCheck) {
        this.collisionCheck = collisionCheck;
    }

    @Override
    public BehaviorType getType() {
        return BehaviorType.PURSUE;
    }
}
