package com.ded.misle.world.entities.ai;

public interface BehaviorCondition {
    boolean matches(BehaviorContext context);
}
