package com.ded.misle.world.entities.ai;

import com.ded.misle.world.entities.Entity;

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
        if (currentBehavior != null) {
            currentBehavior.tryExecute(
                new BehaviorContext(
                    entity,
                    target,
                    player.pos.world
                )
            );
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
}
