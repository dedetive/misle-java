package com.ded.misle.world.entities.ai;

public interface AIBehavior {

    void tryExecute(BehaviorContext context);
    boolean isInterruptible();

    void setPriority(int priority);
    int getPriority();

    BehaviorType getType();
}