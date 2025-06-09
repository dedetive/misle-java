package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorType;

public class WanderBehavior implements AIBehavior {
    private int priority = Integer.MIN_VALUE;

    public WanderBehavior() {

    }

    @Override
    public void tryExecute() {

    }

    @Override
    public boolean isInterruptible() {
        return true;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public BehaviorType getType() {
        return BehaviorType.WANDER;
    }
}
