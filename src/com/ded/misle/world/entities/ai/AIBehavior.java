package com.ded.misle.world.entities.ai;

public interface AIBehavior {

    void tryExecute();
    boolean isInterruptible();

    void setPriority(int priority);
    int getPriority();

    BehaviorType getType();
}