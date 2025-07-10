package com.ded.misle.world.entities.ai.behaviors;

import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.BehaviorController;
import com.ded.misle.world.entities.ai.BehaviorType;

import java.util.Arrays;

/**
 * A composite AI behavior that executes a predefined sequence of behaviors in order.
 * <p>
 * This behavior evaluates and runs one behavior at a time from a defined chain,
 * switching to the next valid one when the current is interrupted or no longer matches.
 * <p>
 * Each behavior in the chain is executed in order. Once the end of the chain is reached,
 * the behavior is considered complete and can reset upon switching out.
 * Behaviors with invalid conditions are skipped, and do not traditionally cancel the chain.
 * <p>
 * Once a chain has started, it cannot be interrupted, unless its condition is no longer met.
 * Once finished, the chain restarts, and it is finally interruptible,
 * although it may start again if favorable. This behavior
 * typically holds minimal priority ({@code Integer.MIN_VALUE}).
 * <p>
 * This allows for defining multiphase logic, such as approaching a player, attacking,
 * and then retreating, all bundled in one sequence.
 * <p>
 * Adding conditions to this behavior is suggested, and so is mimicking the focal behavior conditions.
 * Such can be done through:
 * <pre>
 * {@code
 *  chain.addConditions(
 *      pursue.getConditions()
 *  );
 * }
 * </pre>
 */
public class ChainBehavior extends AbstractBehavior {

    /**
     * The ordered list of AI behaviors to be executed in sequence.
     */
    private final AIBehavior[] chain;

    /**
     * The current index in the chain representing the behavior being executed.
     */
    private int currentChainIndex = 0;

    /**
     * Constructs a ChainBehavior with a predefined sequence of behaviors.
     *
     * @param chain the sequence of behaviors to execute
     */
    public ChainBehavior(AIBehavior... chain) {
        this.chain = chain;
        this.setInterruptible(false);
        this.priority = Integer.MIN_VALUE;

        addOnSwitchIn(ctx -> this.interruptible = false);
        addOnSwitchOut(ctx -> {
            reset();
        });
    }

    /**
     * Checks if all behaviors in the chain have already been processed.
     *
     * @return {@code true} if the chain has finished executing all behaviors, {@code false} otherwise
     */
    private boolean isDone() {
        return currentChainIndex >= chain.length;
    }

    /**
     * Attempts to execute the current behavior in the chain.
     * <p>
     * If the behavior completes, is interruptible, or no longer matches the context,
     * the behavior is switched out and the next valid behavior is evaluated.
     * When the chain completes, {@code onSwitchOut()} is triggered.
     *
     * @param context the behavior context including the entity and target
     */
    @Override
    public void tryExecute(BehaviorContext context) {
        if (isDone()) {
            switchIn(context);
        }

        AIBehavior currentBehavior = chain[currentChainIndex];
        BehaviorController controller = new BehaviorController(context.self());

        controller.setBehaviors(currentBehavior);
        controller.setTarget(context.target());
        controller.setLastSeenTargetPos(context.lastSeenTargetPos());
        controller.run();

        if (currentBehavior.isInterruptible() || !currentBehavior.matches(context)) {
            currentBehavior.switchOut(context);
            advanceToNextValid(context);

            if (isDone()) reset();
        }
    }

    /**
     * Advances to the next behavior in the chain that matches the given context.
     * If no remaining behavior matches, the chain is marked as done.
     *
     * @param context the current behavior context
     */
    private void advanceToNextValid(BehaviorContext context) {
        while (++currentChainIndex < chain.length) {
            if (chain[currentChainIndex].matches(context)) {
                break;
            }
        }
    }

    /**
     * Returns the behavior type for this class.
     *
     * @return the behavior type {@link BehaviorType#CHAIN}
     */
    @Override
    public BehaviorType getType() {
        return BehaviorType.CHAIN;
    }

    /**
     * Returns the behavior back to its initial state.
     */
    private void reset() {
        this.interruptible = true;
        this.currentChainIndex = 0;
    }

    /**
     * Returns a string representation of the current behavior state.
     *
     * @return a string including the current chain index, total behaviors, and current behavior
     */
    @Override
    public String toString() {
        return super.toString().replace("}", "") +
            ", behaviorChain=" + Arrays.toString(chain) +
            ", currentBehavior=" + (isDone() ? "null" : chain[currentChainIndex]) +
            ", currentIndex=" + currentChainIndex +
            ", total=" + chain.length +
            '}';
    }
}