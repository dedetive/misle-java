package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorType;

public class WaitBehavior extends AbstractBehavior {
    private int remainingTurns;
    private final int START_TURNS;

    public WaitBehavior(int turns) {
        this.setInterruptible(false);
        this.priority = 0;

        this.remainingTurns = turns;
        this.START_TURNS = turns;

        this.setCondition(
            context -> remainingTurns > 0
        );
    }

    public WaitBehavior() {
        this(1);
    }

    @Override
    public void onSwitchOut(BehaviorContext context) {
        this.remainingTurns = START_TURNS;
    }

    @Override
    public void tryExecute(BehaviorContext context) {
        this.remainingTurns--;
    }

    @Override
    public BehaviorType getType() {
        return BehaviorType.WAIT;
    }

    /**
     * Prints a string representation of this WaitBehavior.
     *
     * @return a string representation with details about its state
     */
    @Override
    public String toString() {
        return super.toString().replace("}", "") + ", " +
            "startTurns=" + START_TURNS +
            ", remainingTurns=" + remainingTurns +
            '}';
    }
}
