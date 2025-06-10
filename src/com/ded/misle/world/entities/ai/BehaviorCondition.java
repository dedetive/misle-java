package com.ded.misle.world.entities.ai;

import java.util.function.Function;

public interface BehaviorCondition {
    boolean matches(BehaviorContext context);
    void setCondition(Function<BehaviorContext, Boolean> condition);
}
