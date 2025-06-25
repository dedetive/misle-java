package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;

public class WaitBehavior extends AbstractBehavior {
    public WaitBehavior() {
        this.setInterruptible(true);
        this.priority = Integer.MIN_VALUE;
    }

    @Override
    public void tryExecute(BehaviorContext context) {

    }

    @Override
    public BehaviorType getType() {
        return BehaviorType.WAIT;
    }
}
