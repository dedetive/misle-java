package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorController;
import com.ded.misle.world.entities.ai.BehaviorType;

import java.util.Arrays;
import java.util.List;

public class ChainBehavior extends AbstractBehavior {

    private final AIBehavior[] chain;
    private int currentChainIndex = 0;

    public ChainBehavior(AIBehavior... chain) {
        this.chain = chain;
        this.setInterruptible(false);
        this.priority = Integer.MIN_VALUE;

        this.setCondition(ctx -> !isDone());
    }

    private boolean isDone() {
        return currentChainIndex >= chain.length;
    }

    @Override
    public void tryExecute(BehaviorContext context) {
        if (isDone()) return;

        AIBehavior currentBehavior = chain[currentChainIndex];
        BehaviorController controller = new BehaviorController(context.self());

        controller.setBehaviors(currentBehavior);
        controller.run();

        if (currentBehavior.isInterruptible() ||
            !currentBehavior.matches(context)
        ) {
            currentBehavior.onSwitchOut(context);
            currentChainIndex++;
        }
    }

    @Override
    public void onSwitchOut(BehaviorContext context) {
        super.onSwitchOut(context);
        this.currentChainIndex = 0;
    }

    @Override
    public BehaviorType getType() {
        return BehaviorType.CHAIN;
    }
}
