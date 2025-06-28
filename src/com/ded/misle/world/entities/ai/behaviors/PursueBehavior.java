package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;
import com.ded.misle.world.logic.Path;
import com.ded.misle.world.logic.Pathfinder;

import java.awt.*;
import java.util.function.Predicate;

import static com.ded.misle.world.logic.PhysicsEngine.isSpaceOccupied;

public class PursueBehavior extends AbstractBehavior {

    private Predicate<Point> collisionCheck = p -> !isSpaceOccupied(p.x, p.y);

    public PursueBehavior() {
        this.interruptible = true;
        this.priority = 0;

        this.setCondition(
            ctx -> ctx.target() != null &&
                new Pathfinder().findPath(ctx.self().getPos(), ctx.target().getPos(), collisionCheck) != null
        );
    }

    @Override
    public void tryExecute(BehaviorContext context) {
        Entity self = context.self();
        Entity target = context.target();

        Path pathToTarget = new Pathfinder().findPath(
            self.getPos(), target.getPos(), collisionCheck);

        if (pathToTarget == null) return;

        Point p = pathToTarget.getStart();

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
