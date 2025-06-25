package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;

public class WaitBehavior extends AbstractBehavior {
    private int remainingTurns;

    public WaitBehavior(int turns) {
        this.setInterruptible(false);
        this.priority = Integer.MIN_VALUE;

        this.remainingTurns = turns;

        this.setCondition(
            context -> remainingTurns > 0
        );
    }

    public WaitBehavior() {
        this(1);
    }

    @Override
    public void tryExecute(BehaviorContext context) {
        this.remainingTurns--;
    }

    @Override
    public BehaviorType getType() {
        return BehaviorType.WAIT;
    }
}
