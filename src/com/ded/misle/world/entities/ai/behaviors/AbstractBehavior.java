package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorContext;

import java.util.function.Function;

public abstract class AbstractBehavior implements AIBehavior {

    protected int priority = Integer.MIN_VALUE;
    protected boolean interruptible = true;

    private Function<BehaviorContext, Boolean> condition = ctx -> true;

    @Override
    public boolean matches(BehaviorContext context) {
        return condition.apply(context);
    }

    @Override
    public void setCondition(Function<BehaviorContext, Boolean> condition) {
        this.condition = condition;
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
    public boolean isInterruptible() {
        return interruptible;
    }

    @Override
    public void setInterruptible(boolean interruptible) {
        this.interruptible = interruptible;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "priority=" + priority +
            '}';
    }
}
