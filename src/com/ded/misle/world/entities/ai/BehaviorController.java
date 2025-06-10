package com.ded.misle.world.entities.ai;

import com.ded.misle.world.entities.Entity;

import java.util.Arrays;
import java.util.Comparator;

import static com.ded.misle.game.GamePanel.player;

public class BehaviorController implements Runnable {
    private final Entity entity;
    private Entity target;
    private AIBehavior currentBehavior;
    private AIBehavior[] behaviors;

    public BehaviorController(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void run() {
        BehaviorContext context = new BehaviorContext(
                entity,
                target,
                player.pos.world
            );

        AIBehavior bestBehavior = Arrays.stream(behaviors)
            .filter(b -> b.matches(context))
            .max(Comparator.comparingInt(AIBehavior::getPriority))
            .orElse(null);

        if (bestBehavior != null) {
            bestBehavior.tryExecute(context);
            currentBehavior = bestBehavior;
        }
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public void setBehaviors(AIBehavior... behaviors) {
        this.behaviors = behaviors;
    }

    public AIBehavior[] getBehaviors() {
        return behaviors;
    }

    public AIBehavior getCurrentBehavior() {
        return currentBehavior;
    }
}
