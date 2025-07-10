package com.ded.misle.world.data.entity.configurations;

import com.ded.misle.items.DropTable;
import com.ded.misle.world.data.entity.EnemyConfigurator;
import com.ded.misle.world.data.entity.GenericConfigurations;
import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.behaviors.ChainBehavior;
import com.ded.misle.world.entities.ai.behaviors.PursueBehavior;
import com.ded.misle.world.entities.ai.behaviors.WaitBehavior;
import com.ded.misle.world.entities.ai.behaviors.WanderBehavior;
import com.ded.misle.world.logic.TurnTimer;

import static com.ded.misle.game.GamePanel.player;

/**
 * Centralized definitions of enemy setup configurations.
 * Each constant provides a lambda that customizes stats, visuals,
 * behavior, and loot for a particular type of enemy.
 * <p>
 * These are used by {@link EnemyType}.
 */
public enum EnemyConfigurations implements GenericConfigurations {
    GOBLIN(goblin -> {
        goblin.setMaxHP(12)
                .fillHP()
                .setDamage(2)
                .setTexture("goblin")
                .setDropTable(DropTable.GOBLIN)
                .setXpDrop(1)
                .setCoinDrop(3)
                .setTurnsToRespawn(50)
                .scheduleOnDamage(
                    () -> goblin.setDisplayHP(true))
                .setMaxSight(8);

        var wait = new WaitBehavior();
        var pursue = new PursueBehavior();
        var wander = new WanderBehavior(3);

        ChainBehavior chain = switch (player.getDifficulty()) {
            case EASY -> new ChainBehavior(
                wait,
                pursue
            );
            case MEDIUM -> new ChainBehavior(
                wait,
                pursue
            );
            case HARD -> new ChainBehavior(
                wait,
                pursue,
                pursue,
                wait,
                wait,
                pursue
            );
            case NIGHTMARE -> new ChainBehavior(
                wait,
                pursue,
                pursue,
                wait,
                pursue
            );
        };

        chain.addConditions(
            pursue.getConditions()
        );

        goblin.getController().setTarget(player);

        AIBehavior[] defaultBehaviors = new AIBehavior[]{
                chain, wander
        };

        chain.addOnSwitchOut(
            ctx -> {
                var lookForPlayer = new WanderBehavior();
                BehaviorContext ctx2 = new BehaviorContext(ctx.self(), ctx.target(), null, ctx.world());

                TurnTimer returnToDefault = TurnTimer.schedule(20, e -> {
                    goblin.setBehaviors(defaultBehaviors);
                }).setRoomScoped(true);
                TurnTimer checkIfPlayer = TurnTimer.schedule(1, true, e -> {
                    if (pursue.matches(ctx2)) {
                        returnToDefault.forceExecution();
                    }
                }).setRoomScoped(true).setStopsAt(19);

                returnToDefault.setAfterExecution(e -> {
                    checkIfPlayer.kill();
                });

                goblin.setBehaviors(
                    lookForPlayer
                );

            }
        );

        goblin.setBehaviors(
            chain,
            wander
        );
    })

    ;

    /**
     * The functional configurator for this enemy setup.
     */
    public final EnemyConfigurator c;

    EnemyConfigurations(EnemyConfigurator configurator) {
        this.c = configurator;
    }
}
